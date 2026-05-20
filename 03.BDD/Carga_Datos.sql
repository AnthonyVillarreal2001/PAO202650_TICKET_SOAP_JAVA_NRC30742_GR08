-- =====================================================================
-- INSERCIÓN DE DATOS: PARTIDO_FUTBOL (Mínimo 5 registros solicitados)
-- =====================================================================
INSERT INTO PARTIDO_FUTBOL (CODIGO, EQUIPO_LOCAL, EQUIPO_VISTITA, FECHA, LUGAR) VALUES 
('P001', 'Equipo A', 'Equipo B', '2026-05-25 19:00:00', 'Estadio Olímpico'),
('P002', 'Equipo C', 'Equipo D', '2026-05-28 16:00:00', 'Estadio Nacional'),
('P003', 'Equipo E', 'Equipo F', '2026-06-02 18:30:00', 'Arena del Sur'),
('P004', 'Equipo G', 'Equipo H', '2026-06-10 20:00:00', 'Estadio Norte'),
('P005', 'Equipo I', 'Equipo J', '2026-06-15 17:00:00', 'Estadio Central');

-- =====================================================================
-- INSERCIÓN DE DATOS: LOCALIDAD_PARTIDO (Mínimo 20 registros solicitados)
-- Nota: CODIGO_LOCALIDAD se genera como 'PARTIDO-TIPO' para asegurar 
-- que sea una clave primaria única por cada partido.
-- =====================================================================

-- Localidades para P001
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P001-GEN', 'P001', 1000, 10.00),
('P001-GVI', 'P001', 500,  10.00),
('P001-TRI', 'P001', 300,  25.00),
('P001-PAL', 'P001', 50,   50.00);

-- Localidades para P002
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P002-GEN', 'P002', 800,  12.00),
('P002-GVI', 'P002', 400,  12.00),
('P002-TRI', 'P002', 200,  30.00),
('P002-PAL', 'P002', 40,   60.00);

-- Localidades para P003
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P003-GEN', 'P003', 1200, 8.00),
('P003-GVI', 'P003', 600,  8.00),
('P003-TRI', 'P003', 400,  20.00),
('P003-PAL', 'P003', 100,  40.00);

-- Localidades para P004
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P004-GEN', 'P004', 1500, 15.00),
('P004-GVI', 'P004', 500,  15.00),
('P004-TRI', 'P004', 500,  35.00),
('P004-PAL', 'P004', 80,   70.00);

-- Localidades para P005
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P005-GEN', 'P005', 900,  10.00),
('P005-GVI', 'P005', 300,  10.00),
('P005-TRI', 'P005', 250,  25.00),
('P005-PAL', 'P005', 30,   55.00);

-- =====================================================================
-- INSERCIÓN OPCIONAL: FACTURA y DETALLE_FACTURA (Para pruebas)
-- =====================================================================
INSERT INTO FACTURA (ID_FACTURA, CODIGO, FECHA_EMISION, SUBTOTAL, IVA, TOTAL) VALUES 
(1, 'P001', '2026-05-19 18:00:00', 50.00, 6.00, 56.00);

INSERT INTO DETALLE_FACTURA (ID_DETALLE, CODIGO, ID_FACTURA, CODIGO_LOCALIDAD, CANTIDAD, PRECIO_UNITARIO, TOTAL_DETALLE) VALUES 
(1, 'P001', 1, 'P001-TRI', 2, 25.00, 50.00);