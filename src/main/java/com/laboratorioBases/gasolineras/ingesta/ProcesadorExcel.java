package com.laboratorioBases.gasolineras.ingesta;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Contrato que deben cumplir los procesadores de ficheros Excel.
 */
public interface ProcesadorExcel {

    /**
     * Procesa el Excel de entrada y devuelve una lista de registros unificados.
     */
    List<RegistroCsv> procesar(InputStream input) throws IOException;
}
