package com.laboratorioBases.gasolineras.ingesta;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Carga el fichero carburantes_limpio.csv directamente
 * en el modelo normalizado de la base de datos:
 *   - provincia
 *   - municipio
 *   - localidad
 *   - empresa
 *   - estacion_servicio
 *   - combustible
 *   - precio_combustible
 *
 * Esquema según Tablas_Act1_BBDDA.txt:
 *
 *  provincia(id, nombre)
 *  municipio(id, nombre, provincia_id)
 *  localidad(id, nombre, municipio_id)
 *  empresa(id, nombre)
 *  estacion_servicio(
 *      id, direccion, codigo_postal, longitud, latitud,
 *      margen, tipo_venta, rem, horario, tipo_servicio,
 *      localidad_id, empresa_id
 *  )
 *  combustible(
 *      id, nombre, tipo, es_maritimo
 *  )
 *  precio_combustible(
 *      estacion_id, combustible_id, toma_datos, precio
 *  )
 */
public class CargadorCsvABaseDatos {

    private static final String RUTA_CSV = "carburantes_limpio.csv";

    public static void main(String[] args) {
        try {
            new CargadorCsvABaseDatos().cargarCsvEnModeloNormalizado();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal: lee el CSV y va insertando/actualizando
     * las tablas normalizadas.
     */
    public void cargarCsvEnModeloNormalizado() throws IOException, SQLException {
        Path csvPath = Path.of(RUTA_CSV);

        try (Connection conn = ConexionPostgres.getConnection()) {
            conn.setAutoCommit(false);

            try (BufferedReader br = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {

                String linea;
                int totalFilas = 0;

                // 1. Saltar cabecera
                linea = br.readLine();

                // 2. Procesar cada fila
                while ((linea = br.readLine()) != null) {
                    String[] cols = linea.split(";", -1);
                    if (cols.length < 19) {
                        // línea incorrecta, la ignoramos
                        continue;
                    }

                    // --- 2.1. Parsear columnas básicas ---
                    String tipoServicio     = vacioANull(cols[0]);
                    String provinciaNombre  = vacioANull(cols[1]);
                    String municipioNombre  = vacioANull(cols[2]);
                    String localidadNombre  = vacioANull(cols[3]);
                    String codigoPostal     = vacioANull(cols[4]);
                    String direccion        = vacioANull(cols[5]);
                    String margen           = vacioANull(cols[6]);
                    Double longitud         = parseDouble(cols[7]);
                    Double latitud          = parseDouble(cols[8]);
                    Timestamp tomaDatos     = parseTimestamp(cols[9]);

                    String rotulo           = vacioANull(cols[10]); // lo usamos como nombre de empresa
                    String tipoVenta        = vacioANull(cols[11]);
                    String rem              = vacioANull(cols[12]);
                    String horario          = vacioANull(cols[13]);

                    Double precio95E5       = parseDouble(cols[14]);
                    // cols[15] = precio_gasolina_95_e10 (de momento lo ignoramos)
                    Double precioGasoleoA   = parseDouble(cols[16]);
                    // cols[17] = precio_gasoleo_b (ignoramos)
                    // cols[18] = precio_gasoleo_mar (ignoramos)

                    // --- 2.2. Insertar / recuperar provincia, municipio, localidad ---
                    if (provinciaNombre == null || municipioNombre == null || localidadNombre == null) {
                        System.err.println("Fila sin provincia/municipio/localidad, se ignora: " + linea);
                        continue;
                    }

                    Integer provinciaId = getOrInsertProvincia(conn, provinciaNombre);
                    Integer municipioId = getOrInsertMunicipio(conn, municipioNombre, provinciaId);
                    Integer localidadId = getOrInsertLocalidad(conn, localidadNombre, municipioId);

                    // --- 2.3. Empresa: usamos el rótulo como nombre de empresa ---
                    // Si no hay rótulo, inventamos un nombre genérico para no perder la fila
                    String nombreEmpresa;
                    if (rotulo != null) {
                        nombreEmpresa = rotulo;
                    } else {
                        nombreEmpresa = "Empresa desconocida - " + localidadNombre;
                        // Lo mostramos en salida normal (blanco), no en rojo
                        System.out.println("Fila sin rótulo (empresa), se asigna nombre genérico: " + nombreEmpresa);
                    }
                    Integer empresaId = getOrInsertEmpresa(conn, nombreEmpresa);



                    // --- 2.4. Estación de servicio ---
                    int estacionId = getOrInsertEstacion(
                            conn,
                            tipoServicio,
                            codigoPostal,
                            direccion,
                            margen,
                            longitud,
                            latitud,
                            tipoVenta,
                            rem,
                            horario,
                            localidadId,
                            empresaId
                    );

                    // Si no hay fecha, no podemos registrar precios
                    if (tomaDatos == null) {
                        System.err.println("Fila sin toma_datos válida, se salta precio: " + linea);
                    }

                    // --- 2.5. Combustibles fijos + precios ---

                    // Gasolina 95 E5
                    if (precio95E5 != null && tomaDatos != null) {
                        int comb95Id = getOrInsertCombustible(
                                conn,
                                "Gasolina 95 E5",
                                "Gasolina",
                                false
                        );
                        insertarOActualizarPrecio(
                                conn, estacionId, comb95Id, tomaDatos, precio95E5
                        );
                    }

                    // Gasóleo A
                    if (precioGasoleoA != null && tomaDatos != null) {
                        int combGasAId = getOrInsertCombustible(
                                conn,
                                "Gasóleo A",
                                "Gasóleo",
                                false
                        );
                        insertarOActualizarPrecio(
                                conn, estacionId, combGasAId, tomaDatos, precioGasoleoA
                        );
                    }

                    totalFilas++;
                    if (totalFilas % 1000 == 0) {
                        System.out.println("Procesadas " + totalFilas + " filas...");
                    }
                }

                conn.commit();
                System.out.println("Carga completada. Filas procesadas: " + totalFilas);

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // =========================================================
    //  HELPERS DE INSERCIÓN / RECUPERACIÓN (SELECT + INSERT)
    // =========================================================

    private Integer getOrInsertProvincia(Connection conn, String nombre) throws SQLException {
        String select = "SELECT id FROM provincia WHERE nombre = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        String insert = "INSERT INTO provincia (nombre) VALUES (?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Integer getOrInsertMunicipio(Connection conn, String nombre, int provinciaId) throws SQLException {
        String select = "SELECT id FROM municipio WHERE nombre = ? AND provincia_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, nombre);
            ps.setInt(2, provinciaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        String insert = "INSERT INTO municipio (nombre, provincia_id) VALUES (?, ?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, nombre);
            ps.setInt(2, provinciaId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Integer getOrInsertLocalidad(Connection conn, String nombre, int municipioId) throws SQLException {
        String select = "SELECT id FROM localidad WHERE nombre = ? AND municipio_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, nombre);
            ps.setInt(2, municipioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        String insert = "INSERT INTO localidad (nombre, municipio_id) VALUES (?, ?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, nombre);
            ps.setInt(2, municipioId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Integer getOrInsertEmpresa(Connection conn, String nombre) throws SQLException {
        // Tabla empresa: (id, nombre)
        String select = "SELECT id FROM empresa WHERE nombre = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        String insert = "INSERT INTO empresa (nombre) VALUES (?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int getOrInsertEstacion(
            Connection conn,
            String tipoServicio,
            String codigoPostal,
            String direccion,
            String margen,
            Double longitud,
            Double latitud,
            String tipoVenta,
            String rem,
            String horario,
            Integer localidadId,
            Integer empresaId
    ) throws SQLException {

        // Criterio de "misma estación": misma dirección + mismo código postal.
        String select = "SELECT id FROM estacion_servicio " +
                "WHERE direccion = ? AND codigo_postal = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, direccion);
            ps.setString(2, codigoPostal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        String insert = "INSERT INTO estacion_servicio (" +
                "direccion, codigo_postal, longitud, latitud, margen, " +
                "tipo_venta, rem, horario, tipo_servicio, " +
                "localidad_id, empresa_id" +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?) RETURNING id";

        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            int idx = 1;
            ps.setString(idx++, direccion);
            ps.setString(idx++, codigoPostal);

            if (longitud != null) {
                ps.setDouble(idx++, longitud);
            } else {
                ps.setNull(idx++, Types.NUMERIC);
            }

            if (latitud != null) {
                ps.setDouble(idx++, latitud);
            } else {
                ps.setNull(idx++, Types.NUMERIC);
            }

            ps.setString(idx++, margen);
            ps.setString(idx++, tipoVenta);
            ps.setString(idx++, rem);
            ps.setString(idx++, horario);
            ps.setString(idx++, tipoServicio);

            ps.setInt(idx++, localidadId);
            ps.setInt(idx, empresaId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int getOrInsertCombustible(Connection conn,
                                       String nombre,
                                       String tipo,
                                       boolean esMaritimo) throws SQLException {
        // Tabla combustible: (id, nombre, tipo, es_maritimo), UNIQUE(nombre)
        String select = "SELECT id FROM combustible WHERE nombre = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        String insert = "INSERT INTO combustible (nombre, tipo, es_maritimo) " +
                "VALUES (?,?,?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, nombre);
            ps.setString(2, tipo);
            ps.setBoolean(3, esMaritimo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void insertarOActualizarPrecio(
            Connection conn,
            int estacionId,
            int combustibleId,
            Timestamp tomaDatos,
            Double precio
    ) throws SQLException {

        String sql = "INSERT INTO precio_combustible (" +
                "estacion_id, combustible_id, toma_datos, precio" +
                ") VALUES (?,?,?,?) " +
                "ON CONFLICT (estacion_id, combustible_id, toma_datos) " +
                "DO UPDATE SET precio = EXCLUDED.precio";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, estacionId);
            ps.setInt(2, combustibleId);
            ps.setTimestamp(3, tomaDatos);
            ps.setBigDecimal(4, BigDecimal.valueOf(precio));
            ps.executeUpdate();
        }
    }

    // ======================
    //  HELPERS DE PARSEO
    // ======================

    private String vacioANull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private Double parseDouble(String s) {
        s = vacioANull(s);
        if (s == null) return null;
        try {
            return Double.parseDouble(s.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Intenta parsear la fecha de toma_datos en varios formatos:
     *  - "dd/MM/yyyy HH:mm:ss"  (Geoportal – formato completo)
     *  - "dd/MM/yyyy HH:mm"     (Geoportal – sin segundos)
     *  - "yyyy-MM-dd"           (marítimas – LocalDate.toString())
     */
    private Timestamp parseTimestamp(String s) {
        s = vacioANull(s);
        if (s == null) return null;

        // 1) "dd/MM/yyyy HH:mm:ss"
        try {
            DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.parse(s, f1);
            return Timestamp.valueOf(ldt);
        } catch (Exception ignore) {
        }

        // 2) "dd/MM/yyyy HH:mm"
        try {
            DateTimeFormatter f2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime ldt = LocalDateTime.parse(s, f2);
            return Timestamp.valueOf(ldt);
        } catch (Exception ignore) {
        }

        // 3) "yyyy-MM-dd"
        try {
            LocalDate fecha = LocalDate.parse(s); // ISO
            return Timestamp.valueOf(fecha.atStartOfDay());
        } catch (Exception ignore) {
        }

        System.out.println("No se pudo parsear fecha/toma_datos: [" + s + "], se deja NULL");
        return null;
    }
}
