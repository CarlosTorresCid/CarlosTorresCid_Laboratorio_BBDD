-- Vaciar tablas respetando las claves foráneas
TRUNCATE TABLE precio_combustible RESTART IDENTITY CASCADE;
TRUNCATE TABLE estacion_servicio RESTART IDENTITY CASCADE;
TRUNCATE TABLE combustible RESTART IDENTITY CASCADE;
TRUNCATE TABLE localidad RESTART IDENTITY CASCADE;
TRUNCATE TABLE municipio RESTART IDENTITY CASCADE;
TRUNCATE TABLE provincia RESTART IDENTITY CASCADE;
TRUNCATE TABLE empresa RESTART IDENTITY CASCADE;


--Comprobamos que la BBDD esté vacía 

SELECT COUNT(*) FROM provincia;
SELECT COUNT(*) FROM municipio;
SELECT COUNT(*) FROM estacion_servicio;
SELECT COUNT(*) FROM precio_combustible;

--Una vez ejecutado el programa Java y se hayan metido los datos volvemos a consultar lo mismo y veremos que se ha llenado con datos
SELECT COUNT(*) FROM provincia;
SELECT COUNT(*) FROM municipio;
SELECT COUNT(*) FROM estacion_servicio;
SELECT COUNT(*) FROM precio_combustible;