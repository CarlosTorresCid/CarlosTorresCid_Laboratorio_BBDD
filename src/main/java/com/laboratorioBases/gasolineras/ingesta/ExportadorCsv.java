package com.laboratorioBases.gasolineras.ingesta;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Escribe la lista de registros unificados en un fichero CSV.
 */
public class ExportadorCsv {

    public void exportar(String rutaSalida, List<RegistroCsv> registros) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(rutaSalida, false),
                StandardCharsets.UTF_8
        ))) {
            // Cabecera
            writer.println(RegistroCsv.CABECERA_CSV);

            // Filas
            for (RegistroCsv r : registros) {
                writer.println(r.toCsvLine());
            }

            System.out.println("CSV generado: " + rutaSalida +
                    " (" + registros.size() + " filas)");
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar CSV", e);
        }
    }
}
