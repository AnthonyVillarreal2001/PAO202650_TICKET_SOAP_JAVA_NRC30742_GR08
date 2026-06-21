SET FOREIGN_KEY_CHECKS=0;
DELETE FROM DETALLE_FACTURA;
DELETE FROM FACTURA;
DELETE FROM AMORTIZACION;
DELETE FROM TRANSACCION;
DELETE FROM CLIENTE_CREDITO;
DELETE FROM CLIENTE;
DELETE FROM USUARIOS;
DELETE FROM LOCALIDAD_PARTIDO;
DELETE FROM PARTIDO_FUTBOL;
SET FOREIGN_KEY_CHECKS=1;

-- =====================================================================
-- INSERCIÓN DE DATOS: PARTIDO_FUTBOL (Mínimo 5 registros solicitados)
-- =====================================================================
INSERT INTO PARTIDO_FUTBOL (CODIGO, EQUIPO_LOCAL, EQUIPO_VISITANTE, FECHA, LUGAR) VALUES 
('P001', 'México', 'Sudáfrica', '2026-06-11 13:00:00', 'Estadio Ciudad de México'),
('P002', 'Corea del Sur', 'UEFA D', '2026-06-11 20:00:00', 'Estadio Guadalajara'),
('P003', 'Canadá', 'UEFA A', '2026-06-12 13:00:00', 'Toronto Stadium'),
('P004', 'Estados Unidos', 'Paraguay', '2026-06-12 19:00:00', 'Los Angeles Stadium'),
('P005', 'Catar', 'Suiza', '2026-06-13 13:00:00', 'San Francisco Area Stadium'),
('P006', 'Brasil', 'Marruecos', '2026-06-13 16:00:00', 'Nueva Jersey Stadium'),
('P007', 'Haití', 'Escocia', '2026-06-13 19:00:00', 'Boston Stadium'),
('P008', 'Australia', 'UEFA C', '2026-06-13 22:00:00', 'BC Place Vancouver'),
('P009', 'Alemania', 'Curazao', '2026-06-14 11:00:00', 'Houston Stadium'),
('P010', 'Países Bajos', 'Japón', '2026-06-14 14:00:00', 'Dallas Stadium'),
('P011', 'Costa de Marfil', 'Ecuador', '2026-06-14 17:00:00', 'Philadelphia Stadium'),
('P012', 'UEFA B', 'Túnez', '2026-06-14 20:00:00', 'Estadio Monterrey'),
('P013', 'España', 'Cabo Verde', '2026-06-15 10:00:00', 'Atlanta Stadium'),
('P014', 'Bélgica', 'Egipto', '2026-06-15 13:00:00', 'Seattle Stadium'),
('P015', 'Arabia Saudita', 'Uruguay', '2026-06-15 16:00:00', 'Miami Stadium'),
('P016', 'Irán', 'Nueva Zelanda', '2026-06-15 19:00:00', 'Los Angeles Stadium'),
('P017', 'Francia', 'Senegal', '2026-06-16 13:00:00', 'New Jersey Stadium'),
('P018', 'Repechaje 2', 'Noruega', '2026-06-16 16:00:00', 'Boston Stadium'),
('P019', 'Argentina', 'Argelia', '2026-06-16 17:00:00', 'Kansas City Stadium'),
('P020', 'Austria', 'Jordania', '2026-06-16 20:00:00', 'San Francisco Area Stadium'),
('P021', 'Portugal', 'Repechaje 1', '2026-06-17 11:00:00', 'Houston Stadium'),
('P022', 'Inglaterra', 'Croacia', '2026-06-17 14:00:00', 'Dallas Stadium'),
('P023', 'Ghana', 'Panamá', '2026-06-17 17:00:00', 'Toronto Stadium'),
('P024', 'Uzbekistán', 'Colombia', '2026-06-17 20:00:00', 'Estadio Ciudad de México'),
('P025', 'UEFA D', 'Sudáfrica', '2026-06-18 10:00:00', 'Atlanta Stadium');
-- =====================================================================
-- INSERCIÓN DE DATOS: LOCALIDAD_PARTIDO (Mínimo 20 registros solicitados)
-- Nota: CODIGO_LOCALIDAD se genera como 'PARTIDO-TIPO' para asegurar 
-- que sea una clave primaria única por cada partido.
-- =====================================================================

INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P001-GEN', 'P001', 1000, 150.00),
('P001-GVI', 'P001', 500,  150.00),
('P001-TRI', 'P001', 300,  350.00),
('P001-PAL', 'P001', 50,   800.00);

-- P002 (Corea del Sur vs UEFA D) - Estadio Guadalajara (Demanda Estándar)
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P002-GEN', 'P002', 800,  100.00),
('P002-GVI', 'P002', 400,  100.00),
('P002-TRI', 'P002', 200,  200.00),
('P002-PAL', 'P002', 40,   450.00);

-- P003 (Canadá vs UEFA A) - Toronto Stadium (Anfitrión/Demanda Media-Alta)
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P003-GEN', 'P003', 1200, 120.00),
('P003-GVI', 'P003', 600,  120.00),
('P003-TRI', 'P003', 400,  250.00),
('P003-PAL', 'P003', 100,  550.00);

-- P004 (Estados Unidos vs Paraguay) - Los Angeles Stadium (Anfitrión/Alta Demanda)
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P004-GEN', 'P004', 1500, 180.00),
('P004-GVI', 'P004', 500,  180.00),
('P004-TRI', 'P004', 500,  400.00),
('P004-PAL', 'P004', 80,   950.00);

-- P005 (Catar vs Suiza) - San Francisco Area Stadium (Demanda Estándar)
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES 
('P005-GEN', 'P005', 900,  110.00),
('P005-GVI', 'P005', 300,  110.00),
('P005-TRI', 'P005', 250,  220.00),
('P005-PAL', 'P005', 30,   480.00);
-- =====================================================================
-- INSERCIÓN OPCIONAL: FACTURA y DETALLE_FACTURA (Para pruebas)
INSERT INTO FACTURA (ID_FACTURA, CODIGO, ID_CLIENTE, FECHA_EMISION, SUBTOTAL, IVA, TOTAL) VALUES 
(1, 'P001', 'C001', '2026-06-10 18:00:00', 700.00, 84.00, 784.00);

INSERT INTO DETALLE_FACTURA (ID_DETALLE, CODIGO, ID_FACTURA, CODIGO_LOCALIDAD, CANTIDAD, PRECIO_UNITARIO, TOTAL_DETALLE) VALUES 
(1, 'P001', 1, 'P001-TRI', 2, 350.00, 700.00);

-- =====================================================================
-- INSERCIÓN DE DATOS: USUARIOS DEL SISTEMA
-- =====================================================================
INSERT INTO USUARIOS (USERNAME, PASSWORD, ROL) VALUES 
('MONSTER', 'JlkdJiriw9IyD/kq2Qhk144GcCJTIEHoMEaI24gufGk=', 'ADMIN'),
('ADMIN2', 'W0AXFIllklEJfneQ/C8YkuIYOnJUb+HfKD0Hhl25FJw=', 'ADMIN'),
('VENDEDOR1', 'A599T2hS57MIVfdvuawEL3z0N290Vx6bjZ6khZRuYYQ=', 'VENDEDOR'),
('VENDEDOR2', 'pf4OE8/sBV7w5wzcGJ4dAqmm6v2kkpU3wX9lPsl8U2c=', 'VENDEDOR'),
('AUDITOR1', 'H9VHn7iwB4DJd65XARwve2FDQwnig0i14SsEhZnR4JE=', 'AUDITOR');

-- =====================================================================
-- INSERCIÓN DE DATOS: CLIENTES DE PRUEBA (CRÉDITO Y EFECTIVO)
-- =====================================================================
INSERT INTO CLIENTE (ID_CLIENTE, NOMBRES, CORREO, TELEFONO, EDAD, GENERO) VALUES 
('C001', 'Juan Perez (Apto)', 'juan@test.com', '0991', 30, 'M'),
('C002', 'Maria Gomez (Apta - 22a)', 'maria@test.com', '0992', 22, 'F'),
('C003', 'Carlos Joven (Rech - Edad)', 'carlos@test.com', '0993', 20, 'M'),
('C004', 'Luis Moroso (Rech - Mora)', 'luis@test.com', '0994', 40, 'M'),
('C005', 'Ana SinFondos (Rech - Liquidez)', 'ana@test.com', '0995', 35, 'F'),
('C006', 'MONSTER (Apto)', 'monster@espe.edu.ec', '0996', 50, 'M'),
('C007', 'Cliente Rico (Apto)', 'rico@test.com', '0997', 45, 'M'),
('C008', 'Estudiante VIP (Apto)', 'vip@test.com', '0998', 25, 'F');

INSERT INTO CLIENTE_CREDITO (ID_CLIENTE_CREDITO, HISTORIAL_FINANCIERO, MONTO_MAXIMO) VALUES 
('C001', 'BUENO', 5000.0),
('C002', 'BUENO', 5000.0),
('C003', 'BUENO', 5000.0),
('C004', 'BUENO', 5000.0),
('C005', 'BUENO', 5000.0),
('C006', 'EXCELENTE', 10000.0),
('C007', 'EXCELENTE', 20000.0),
('C008', 'BUENO', 8000.0);

INSERT INTO TRANSACCION (ID_CLIENTE_CREDITO, TIPO_TRANSACCION, FECHA_TRANSACCION, MONTO) VALUES 
('C001', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 5 DAY), 2000),
('C001', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 45 DAY), 2000),
('C001', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 80 DAY), 2000),
('C001', 'RETIRO', DATE_SUB(SYSDATE(), INTERVAL 10 DAY), 500),

('C002', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 10 DAY), 1500),
('C002', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 40 DAY), 1500),

('C003', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 15 DAY), 3000),

('C004', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 5 DAY), 5000),

('C005', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 500),
('C005', 'RETIRO', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 4000),

('C006', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 10 DAY), 5000),
('C006', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 30 DAY), 5000),
('C006', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 60 DAY), 5000),

('C007', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 15 DAY), 10000),
('C007', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 45 DAY), 15000),

('C008', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 5 DAY), 3000),
('C008', 'DEPOSITO', DATE_SUB(SYSDATE(), INTERVAL 35 DAY), 3000);

INSERT INTO AMORTIZACION (ID_CLIENTE_CREDITO, NUMERO_CUOTA, FECHA_VENCIMIENTO, MONTO_CUOTA, INTERES, CAPITAL, SALDO, ESTADO_CUOTA) VALUES 
('C004', 1, DATE_ADD(SYSDATE(), INTERVAL 1 MONTH), 150.0, 10.0, 140.0, 850.0, 'PENDIENTE');