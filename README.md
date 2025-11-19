# Actividad 1 – Bases de Datos Avanzadas

Este repositorio contiene el proyecto completo de la Actividad 1 de la asignatura Bases de Datos Avanzadas.
El objetivo del proyecto es:
- Descargar y leer los ficheros originales de precios de carburantes (maritimos y terrestres).
- Depurar y transformar los datos con una aplicación Java + Maven.
- Generar un CSV limpio (carburantes_limpio.csv).
- Cargar automáticamente los datos en una base de datos PostgreSQL (modelo diseñado para la práctica).

Ejecutar una batería de consultas SQL sobre la información cargada.


# Requisitos previos

Necesitarás tener:
- Java 17 o superior
- Maven 3.x
- PostgreSQL 16/17
- pgAdmin (opcional pero recomendable)
- IntelliJ IDEA (recomendado)
- Los ficheros Excel originales (si no se descargan  desde https://geoportalgasolineras.es/geoportal-instalaciones/DescargarFicheros )

# Configuración inicial de la base de datos
- Crear la base de datos
- En pgAdmin ejecutar:
    CREATE DATABASE laboratorio_bbddaa;
      (Si usas otro nombre, recuerda modificarlo después en la clase ConexionPostgres.java.)

- Ejecutar el script:
    bbdd/01_creacion_tablas.sql
   - Este script:
        - Crea las 7 entidades
        - Define las claves primarias
        - Aplica claves foráneas
        - Define la clave compuesta de precio_combustible
  - Puedes comprobar que se han creado todas las tablas:
     02_truncate_y_carga.sql

# Configurar la conexión a PostgreSQL en Java

- Editar la clase:
    src/main/java/com/laboratorioBases/gasolineras/ingesta/ConexionPostgres.java

- Debes ajustar:

  - private static final String DB_NAME  ="laboratorio_bbddaa";-- tu nombre del proyecto de la BBDD
  - private static final String USER     ="postgres";-- la que tengas tu
  - private static final String PASSWORD ="TU_CONTRASEÑA";-- la que tengas tu
  - private static final String URL      ="jdbc:postgresql://localhost:5432/";-- la que tengas tu


# Preparar los ficheros de entrada (opcional)

Si los ficheros Excel no están, colócalos en:

  src/main/resources/

El proyecto los leerá automáticamente.

# Ejecución completa del proyecto (paso a paso)

- Importar el proyecto
- Clonar y abrir en IntelliJ:
- git clone https://github.com/CarlosTorresCid/CarlosTorresCid_Laboratorio_BBDDAA.git
- IntelliJ detectará Maven y descargará dependencias.


# Ejecutar el programa

- Desde IntelliJ:

    - Ejecutar CargadorCsvABaseDatos.java

- El programa:

  - Lee los ficheros Excel
  - Depura y normaliza los datos
  - Genera carburantes_limpio.csv
  - Inserta toda la información creada en carburantes_limpio.csv en PostgreSQL


# Comprobación final de carga

- En pgAdmin ejecutar:

SELECT COUNT(*) FROM provincia;<br>
SELECT COUNT(*) FROM municipio;<br>
SELECT COUNT(*) FROM localidad;<br>
SELECT COUNT(*) FROM empresa;<br>
SELECT COUNT(*) FROM estacion_servicio;<br>
SELECT COUNT(*) FROM combustible;<br>
SELECT COUNT(*) FROM precio_combustible;<br>

# Consultas de la actividad (SQL)

- El archivo: bbdd/03_consultas.sql incluye las 5 consultas pedidas:

- Empresa con más estaciones terrestres

- Empresa con más estaciones marítimas

- Estación más barata de Gasolina 95 E5 en Madrid

- Estación más barata de Gasóleo A a menos de 10 km del centro de Albacete

Provincia con la estación marítima más cara de Gasolina 95 E5

#  Resumen rápido de despliegue

1. Clonar repo
2. Crear BD en PostgreSQL
3. Ejecutar 01_creacion_tablas.sql
4. Configurar credenciales en ConexionPostgres.java
5. Poner Excel en resources (si falta)
6. Ejecutar AplicacionIngesta.java
7. Verificar conteos en PostgreSQL
8. Ejecutar 03_consultas.sql











