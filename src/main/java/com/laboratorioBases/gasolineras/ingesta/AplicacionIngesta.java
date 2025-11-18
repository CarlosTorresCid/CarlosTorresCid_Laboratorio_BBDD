package com.laboratorioBases.gasolineras.ingesta;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada de la aplicación de ingesta:
 *  - Lee los dos Excels desde disco
 *  - Los procesa
 *  - Genera un CSV unificado carburantes_limpio.csv
 */
public class AplicacionIngesta {

    // Rutas a los ficheros Excel descargados manualmente
    private static final String FICHERO_TERRESTRES  = "src/main/resources/preciosEESS_es.xls";
    private static final String FICHERO_MARITIMAS   = "src/main/resources/embarcacionesPrecios_es.xls";

    private static final String CSV_SALIDA = "carburantes_limpio.csv";

    public static void main(String[] args) {
        try {
            ProcesadorExcel procesadorTerrestres = new ProcesadorTerrestres();
            ProcesadorExcel procesadorMaritimas = new ProcesadorMaritimas();
            ExportadorCsv exportadorCsv = new ExportadorCsv();

            List<RegistroCsv> registros = new ArrayList<>();

            // 1. Abrir los ficheros Excel desde disco
            try (InputStream inTerrestres = new FileInputStream(FICHERO_TERRESTRES);
                 InputStream inMaritimas  = new FileInputStream(FICHERO_MARITIMAS)) {

                // 2. Procesar cada Excel
                registros.addAll(procesadorTerrestres.procesar(inTerrestres));
                registros.addAll(procesadorMaritimas.procesar(inMaritimas));
            }

            // 3. Exportar el CSV final
            exportadorCsv.exportar(CSV_SALIDA, registros);

            System.out.println("Ingesta de ficheros Excel finalizada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
