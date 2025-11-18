
-- 1. PROVINCIA

CREATE TABLE provincia (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL
);


-- 2. MUNICIPIO

CREATE TABLE municipio (
    id           SERIAL PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    provincia_id INTEGER      NOT NULL REFERENCES provincia(id),
    UNIQUE (nombre, provincia_id)
);


-- 3. LOCALIDAD

CREATE TABLE localidad (
    id           SERIAL PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    municipio_id INTEGER      NOT NULL REFERENCES municipio(id),
    UNIQUE (nombre, municipio_id)
);


-- 4. EMPRESA

CREATE TABLE empresa (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(150) NOT NULL
);


-- 5. ESTACION_SERVICIO

CREATE TABLE estacion_servicio (
    id            SERIAL PRIMARY KEY,
    direccion     VARCHAR(255) NOT NULL,
    codigo_postal VARCHAR(10),
    longitud      NUMERIC(9,6),   
    latitud       NUMERIC(9,6),  
    margen        VARCHAR(10),    
    tipo_venta    VARCHAR(50),    
    rem           VARCHAR(50),    
    horario       VARCHAR(255),
    tipo_servicio VARCHAR(20) NOT NULL,  
    localidad_id  INTEGER NOT NULL REFERENCES localidad(id),
    empresa_id    INTEGER NOT NULL REFERENCES empresa(id)
);


-- 6. COMBUSTIBLE

CREATE TABLE combustible (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,  
    tipo        VARCHAR(50),            
    es_maritimo BOOLEAN      NOT NULL DEFAULT FALSE,
    UNIQUE (nombre)
);


-- 7. PRECIO_COMBUSTIBLE

CREATE TABLE precio_combustible (
    estacion_id    INTEGER   NOT NULL REFERENCES estacion_servicio(id),
    combustible_id INTEGER   NOT NULL REFERENCES combustible(id),
    toma_datos     TIMESTAMP NOT NULL,  
    precio         NUMERIC(6,3) NOT NULL,

    PRIMARY KEY (estacion_id, combustible_id, toma_datos)
);
