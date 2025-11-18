package com.laboratorioBases.gasolineras.ingesta;

import java.text.Normalizer;

/**
 * Utilidades de texto:
 * - normalizar nombres de cabecera de Excel
 * - limpiar campos antes de escribirlos en CSV
 */
public class TextoUtil {

    /**
     * Normaliza un nombre de cabecera:
     * - minúsculas
     * - quita tildes
     * - colapsa espacios
     * - quita comillas
     */
    public static String normalizarNombreCabecera(String s) {
        if (s == null) return "";
        s = s.toLowerCase().trim();
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        s = s.replaceAll("\\s+", " ");
        s = s.replace("\"", "");
        return s;
    }

    /**
     * Limpia un valor para escribirlo en CSV:
     * - cambia ';' por ','
     * - quita saltos de línea
     * - trim
     */
    public static String limpiarCampoCsv(String s) {
        if (s == null) return "";
        return s.replace(";", ",")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }
}
