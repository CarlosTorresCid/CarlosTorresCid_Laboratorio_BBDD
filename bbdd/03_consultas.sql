
------1. Empresa con más estaciones terrestres---------

SELECT e.id,
       e.nombre,
       t.num_estaciones
FROM empresa e
JOIN (
    SELECT empresa_id,
           COUNT(*) AS num_estaciones
    FROM estacion_servicio
    WHERE tipo_servicio = 'TERRESTRE'
    GROUP BY empresa_id
) AS t
  ON e.id = t.empresa_id
WHERE t.num_estaciones = (
    SELECT MAX(sub.num_estaciones)
    FROM (
        SELECT empresa_id,
               COUNT(*) AS num_estaciones
        FROM estacion_servicio
        WHERE tipo_servicio = 'TERRESTRE'
        GROUP BY empresa_id
    ) AS sub
);

-----------2. Empresa con más estaciones marítimas------------

SELECT e.id,
       e.nombre,
       t.num_estaciones
FROM empresa e
JOIN (
    SELECT empresa_id,
           COUNT(*) AS num_estaciones
    FROM estacion_servicio
    WHERE tipo_servicio = 'MARITIMA'
    GROUP BY empresa_id
) AS t
  ON e.id = t.empresa_id
WHERE t.num_estaciones = (
    SELECT MAX(sub.num_estaciones)
    FROM (
        SELECT empresa_id,
               COUNT(*) AS num_estaciones
        FROM estacion_servicio
        WHERE tipo_servicio = 'MARITIMA'
        GROUP BY empresa_id
    ) AS sub
);

-- 3. Estación más barata de Gasolina 95 E5 en la provincia de Madrid

SELECT 
    es.id,
    es.direccion,
    m.nombre AS municipio,
    p.nombre AS provincia,
    e.nombre AS empresa,       -- ← NUEVA COLUMNA
    pc.precio,
    pc.toma_datos
FROM estacion_servicio es
JOIN empresa e          ON es.empresa_id = e.id     -- ← JOIN a empresa
JOIN localidad l        ON es.localidad_id = l.id
JOIN municipio m        ON l.municipio_id = m.id
JOIN provincia p        ON m.provincia_id = p.id
JOIN precio_combustible pc ON pc.estacion_id = es.id
JOIN combustible c      ON c.id = pc.combustible_id
WHERE c.nombre = 'Gasolina 95 E5'
  AND p.nombre = 'MADRID'
  -- Solo el último precio de cada estación+combustible
  AND pc.toma_datos = (
      SELECT MAX(pc2.toma_datos)
      FROM precio_combustible pc2
      WHERE pc2.estacion_id    = pc.estacion_id
        AND pc2.combustible_id = pc.combustible_id
  )
  -- Y ese precio sea el mínimo entre todos esos últimos precios
  AND pc.precio = (
      SELECT MIN(pc3.precio)
      FROM estacion_servicio es3
      JOIN empresa e3         ON es3.empresa_id = e3.id
      JOIN localidad l3       ON es3.localidad_id = l3.id
      JOIN municipio m3       ON l3.municipio_id = m3.id
      JOIN provincia p3       ON m3.provincia_id = p3.id
      JOIN precio_combustible pc3 ON pc3.estacion_id = es3.id
      JOIN combustible c3     ON c3.id = pc3.combustible_id
      WHERE c3.nombre = 'Gasolina 95 E5'
        AND p3.nombre = 'MADRID'
        AND pc3.toma_datos = (
            SELECT MAX(pc4.toma_datos)
            FROM precio_combustible pc4
            WHERE pc4.estacion_id    = pc3.estacion_id
              AND pc4.combustible_id = pc3.combustible_id
        )
  );

----------4. Estación más barata de Gasóleo A a menos de 10 km del centro de Albacete-----------------

WITH estaciones_con_dist AS (
    SELECT 
        es.id,
        es.direccion,
        m.nombre AS municipio,
        p.nombre AS provincia,
        pc.precio,
        pc.toma_datos,
        -- Distancia real en kilómetros al centro de Albacete
        6371 * 2 * ASIN(
            SQRT(
                POWER(SIN(RADIANS(es.latitud  - 38.994349) / 2), 2) +
                COS(RADIANS(38.994349)) * COS(RADIANS(es.latitud)) *
                POWER(SIN(RADIANS(es.longitud - (-1.858542)) / 2), 2)
            )
        ) AS distancia_km
    FROM estacion_servicio es
    JOIN localidad  l   ON es.localidad_id = l.id
    JOIN municipio  m   ON l.municipio_id = m.id
    JOIN provincia  p   ON m.provincia_id = p.id
    JOIN precio_combustible pc ON pc.estacion_id = es.id
    JOIN combustible c   ON c.id = pc.combustible_id
    WHERE 
        c.nombre = 'Gasóleo A'
        -- Solo el último precio por estación+combustible
        AND pc.toma_datos = (
            SELECT MAX(pc2.toma_datos)
            FROM precio_combustible pc2
            WHERE pc2.estacion_id    = pc.estacion_id
              AND pc2.combustible_id = pc.combustible_id
        )
)

SELECT *
FROM estaciones_con_dist e1
WHERE 
    e1.distancia_km < 10
    AND e1.precio = (
        SELECT MIN(e2.precio)
        FROM estaciones_con_dist e2
        WHERE e2.distancia_km < 10
    );

 
 --------5. Provincia con la estación marítima más cara de Gasolina 95 E5 --------------

SELECT 
    p.nombre AS provincia,
    es.id    AS estacion_id,
    es.direccion,
    pc.precio,
    pc.toma_datos
FROM estacion_servicio es
JOIN localidad  l   ON es.localidad_id = l.id
JOIN municipio  m   ON l.municipio_id = m.id
JOIN provincia  p   ON m.provincia_id = p.id
JOIN precio_combustible pc ON pc.estacion_id = es.id
JOIN combustible c   ON c.id = pc.combustible_id
WHERE es.tipo_servicio = 'MARITIMA'         
  AND c.nombre       = 'Gasolina 95 E5'
  -- Solo el último precio por estación+combustible
  AND pc.toma_datos = (
      SELECT MAX(pc2.toma_datos)
      FROM precio_combustible pc2
      WHERE pc2.estacion_id    = pc.estacion_id
        AND pc2.combustible_id = pc.combustible_id
  )
  -- Y que ese precio sea el máximo entre todos esos últimos precios marítimos
  AND pc.precio = (
      SELECT MAX(pc3.precio)
      FROM estacion_servicio es3
      JOIN localidad  l3   ON es3.localidad_id = l3.id
      JOIN municipio  m3   ON l3.municipio_id = m3.id
      JOIN provincia  p3   ON m3.provincia_id = p3.id
      JOIN precio_combustible pc3 ON pc3.estacion_id = es3.id
      JOIN combustible c3   ON c3.id = pc3.combustible_id
      WHERE es3.tipo_servicio = 'MARITIMA'
        AND c3.nombre         = 'Gasolina 95 E5'
        AND pc3.toma_datos = (
            SELECT MAX(pc4.toma_datos)
            FROM precio_combustible pc4
            WHERE pc4.estacion_id    = pc3.estacion_id
              AND pc4.combustible_id = pc3.combustible_id
        )
  );
