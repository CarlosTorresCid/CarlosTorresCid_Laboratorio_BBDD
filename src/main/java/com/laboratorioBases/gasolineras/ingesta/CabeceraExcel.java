package com.laboratorioBases.gasolineras.ingesta;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapa de nombres de cabecera de Excel -> índice de columna.
 * Nos permite obtener valores por nombre de columna.
 */
public class CabeceraExcel {

    private final Map<String, Integer> indicesPorNombre = new HashMap<>();

    public CabeceraExcel(Row filaCabecera) {
        System.out.println("=== CABECERAS DETECTADAS ===");

        for (Cell cell : filaCabecera) {
            if (cell == null) continue;
            cell.setCellType(CellType.STRING);
            String nombreOriginal = cell.getStringCellValue();
            String clave = TextoUtil.normalizarNombreCabecera(nombreOriginal);

            System.out.println("Original: [" + nombreOriginal + "]  --> Normalizado: [" + clave + "]");

            indicesPorNombre.put(clave, cell.getColumnIndex());
        }
    }

    /**
     * Devuelve el valor de una celda de la fila indicada, buscando
     * la columna por nombre lógico.
     */
    public String getValor(Row fila, String nombreLogico) {
        String clave = TextoUtil.normalizarNombreCabecera(nombreLogico);
        Integer idx = indicesPorNombre.get(clave);
        if (idx == null) {
            return "";
        }
        Cell cell = fila.getCell(idx);
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
