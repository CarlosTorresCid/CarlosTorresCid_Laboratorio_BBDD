package com.laboratorioBases.gasolineras.ingesta;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Procesa el Excel de estaciones terrestres.
 */
public class ProcesadorTerrestres implements ProcesadorExcel {

    @Override
    public List<RegistroCsv> procesar(InputStream input) throws IOException {

        List<RegistroCsv> registros = new ArrayList<>();

        try (Workbook wb = new HSSFWorkbook(input)) {

            Sheet sheet = wb.getSheetAt(0);

            if (sheet.getPhysicalNumberOfRows() == 0) {
                System.out.println("Hoja de terrestres vacía.");
                return registros;
            }

            // Buscar la fila que contenga "Provincia" (cabecera real)
            Row filaCabecera = null;
            int filaCabeceraIndex = -1;

            for (Row row : sheet) {
                if (row == null) continue;
                Cell c0 = row.getCell(0);
                if (c0 == null) continue;
                c0.setCellType(CellType.STRING);
                String valor = c0.getStringCellValue();
                if (valor != null && valor.toLowerCase().contains("provincia")) {
                    filaCabecera = row;
                    filaCabeceraIndex = row.getRowNum();
                    break;
                }
            }

            if (filaCabecera == null) {
                System.out.println("No se ha encontrado la fila de cabecera en terrestres.");
                return registros;
            }

            CabeceraExcel mapaCabecera = new CabeceraExcel(filaCabecera);

            // Datos: desde la fila siguiente a la cabecera real
            for (int i = filaCabeceraIndex + 1; i <= sheet.getLastRowNum(); i++) {

                Row fila = sheet.getRow(i);
                if (fila == null) continue;

                RegistroCsv r = new RegistroCsv();
                r.setTipoServicio("TERRESTRE");
                r.setProvincia(mapaCabecera.getValor(fila, "provincia"));
                r.setMunicipio(mapaCabecera.getValor(fila, "municipio"));
                r.setLocalidad(mapaCabecera.getValor(fila, "localidad"));
                r.setCodigoPostal(mapaCabecera.getValor(fila, "codigo postal"));
                r.setDireccion(mapaCabecera.getValor(fila, "direccion"));
                r.setMargen(mapaCabecera.getValor(fila, "margen"));
                r.setLongitud(mapaCabecera.getValor(fila, "longitud"));
                r.setLatitud(mapaCabecera.getValor(fila, "latitud"));
                r.setTomaDatos(mapaCabecera.getValor(fila, "toma de datos"));
                r.setRotulo(mapaCabecera.getValor(fila, "rotulo"));
                r.setTipoVenta(mapaCabecera.getValor(fila, "tipo venta"));

                String rem = mapaCabecera.getValor(fila, "rem.");
                if (rem.isEmpty()) rem = mapaCabecera.getValor(fila, "rem");
                r.setRem(rem);

                r.setHorario(mapaCabecera.getValor(fila, "horario"));

                // Precios
                r.setPrecioGasolina95E5(mapaCabecera.getValor(fila, "precio gasolina 95 e5"));
                r.setPrecioGasolina95E10(mapaCabecera.getValor(fila, "precio gasolina 95 e10"));
                r.setPrecioGasoleoA(mapaCabecera.getValor(fila, "precio gasoleo a"));
                r.setPrecioGasoleoB(mapaCabecera.getValor(fila, "precio gasoleo b"));
                r.setPrecioGasoleoMar(""); // No existe en terrestres

                registros.add(r);
            }
        }

        System.out.println("✔ Terrestres procesadas: " + registros.size());
        return registros;
    }
}
