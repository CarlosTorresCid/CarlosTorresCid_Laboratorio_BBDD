package com.laboratorioBases.gasolineras.ingesta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionPostgres {


    private static final String URL =
            "jdbc:postgresql://localhost:5432/CarlosTorres_Actividad1_BbddAvanzadas_";
    private static final String USER = "postgres";      // tu usuario
    private static final String PASSWORD = "1234"; // tu contraseña

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
