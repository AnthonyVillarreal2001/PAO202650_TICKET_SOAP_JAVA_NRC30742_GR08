/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/5/2026 17:50:35                           */
/*==============================================================*/

CREATE DATABASE IF NOT EXISTS bd_examen_ticket;
USE bd_examen_ticket;

SET FOREIGN_KEY_CHECKS=0;

drop table if exists DETALLE_FACTURA;
drop table if exists FACTURA;
drop table if exists LOCALIDAD_PARTIDO;
drop table if exists PARTIDO_FUTBOL;
drop table if exists USUARIOS;
drop table if exists CLIENTE;
drop table if exists ESTADIO;
drop table if exists PAIS;
drop table if exists AMORTIZACION;
drop table if exists TRANSACCION;
drop table if exists CLIENTE_CREDITO;
drop table if exists MOVIMIENTO;
drop table if exists TIPOMOVIMIENTO;
drop table if exists CUENTA;

-- Volver a habilitar la revisión de claves foráneas
SET FOREIGN_KEY_CHECKS=1;

/*==============================================================*/
/* Table: DETALLE_FACTURA                                       */
/*==============================================================*/
create table DETALLE_FACTURA
(
   ID_DETALLE           numeric(8,0) not null,
   CODIGO               varchar(100),
   ID_FACTURA           numeric(8,0),
   CODIGO_LOCALIDAD     varchar(100),
   CANTIDAD             numeric(8,0) not null,
   PRECIO_UNITARIO      float(8,2) not null,
   TOTAL_DETALLE        float(8,2) not null,
   primary key (ID_DETALLE)
);

/*==============================================================*/
/* Table: FACTURA                                               */
/*==============================================================*/
create table FACTURA
(
   ID_FACTURA           numeric(8,0) not null,
   CODIGO               varchar(100),
   ID_CLIENTE           varchar(100),
   VENDEDOR             varchar(50),
   FECHA_EMISION        datetime not null,
   SUBTOTAL             float(8,2) not null,
   IVA                  float(8,2) not null,
   TOTAL                float(8,2) not null,
   primary key (ID_FACTURA)
);

/*==============================================================*/
/* Table: LOCALIDAD_PARTIDO                                     */
/*==============================================================*/
create table LOCALIDAD_PARTIDO
(
   CODIGO_LOCALIDAD     varchar(100) not null,
   CODIGO_PARTIDO       varchar(100) not null,
   DISPONIBILIDAD       numeric(8,0) not null,
   PRECIO               float(8,2) not null,
   primary key (CODIGO_LOCALIDAD)
);

/*==============================================================*/
/* Table: PARTIDO_FUTBOL                                        */
/*==============================================================*/
create table PARTIDO_FUTBOL
(
   CODIGO               varchar(100) not null,
   EQUIPO_LOCAL         varchar(100) not null,
   EQUIPO_VISITANTE     varchar(100) not null,
   FECHA                datetime not null,
   LUGAR                varchar(100) not null,
   primary key (CODIGO)
);

/*==============================================================*/
/* Table: USUARIOS                                              */
/*==============================================================*/
create table USUARIOS
(
   ID_USUARIO           int not null auto_increment,
   USERNAME             varchar(50) not null unique,
   PASSWORD             varchar(256) not null,
   ROL                  varchar(50),
   primary key (ID_USUARIO)
);

/*==============================================================*/
/* Table: CLIENTE                                               */
/*==============================================================*/
create table CLIENTE
(
   ID_CLIENTE           varchar(100) not null,
   NOMBRES              varchar(100) not null,
   CORREO               varchar(100) not null,
   TELEFONO             varchar(20),
   EDAD                 int,
   GENERO               varchar(1),
   primary key (ID_CLIENTE)
);

/*==============================================================*/
/* Table: PAIS                                                  */
/*==============================================================*/
create table PAIS
(
   ID_PAIS              varchar(100) not null,
   NOMBRE_PAIS          varchar(100) not null,
   primary key (ID_PAIS)
);

/*==============================================================*/
/* Table: ESTADIO                                               */
/*==============================================================*/
create table ESTADIO
(
   ID_ESTADIO           varchar(100) not null,
   NOMBRE_ESTADIO       varchar(100) not null,
   CIUDAD               varchar(100) not null,
   CAPACIDAD            numeric(8,0) not null,
   primary key (ID_ESTADIO)
);

/*==============================================================*/
/* Table: CLIENTE_CREDITO                                       */
/*==============================================================*/
create table CLIENTE_CREDITO
(
   ID_CLIENTE_CREDITO   varchar(100) not null,
   HISTORIAL_FINANCIERO varchar(255) not null,
   MONTO_MAXIMO         float(8,2) not null,
   primary key (ID_CLIENTE_CREDITO)
);

/*==============================================================*/
/* Table: TRANSACCION                                           */
/*==============================================================*/
create table TRANSACCION
(
   ID_TRANSACCION       int not null auto_increment,
   ID_CLIENTE_CREDITO   varchar(100) not null,
   TIPO_TRANSACCION     varchar(50) not null,
   FECHA_TRANSACCION    datetime not null,
   MONTO                float(8,2) not null,
   primary key (ID_TRANSACCION)
);

/*==============================================================*/
/* Table: AMORTIZACION                                          */
/*==============================================================*/
create table AMORTIZACION
(
   ID_AMORTIZACION      int not null auto_increment,
   ID_CLIENTE_CREDITO   varchar(100) not null,
   NUMERO_CUOTA         int not null,
   FECHA_VENCIMIENTO    date not null,
   MONTO_CUOTA          float(8,2) not null,
   INTERES              float(8,2) not null,
   CAPITAL              float(8,2) not null,
   SALDO                float(8,2) not null,
   ESTADO_CUOTA         varchar(50) not null,
   primary key (ID_AMORTIZACION)
);

/*==============================================================*/
/* Tablas Antiguas para Compatibilidad (CUENTA, MOVIMIENTO)     */
/*==============================================================*/
create table CUENTA
(
   chr_cuencodigo       varchar(14) not null,
   dec_cuensaldo        decimal(10,2) not null,
   int_cuencontmov      int not null,
   vch_cuenestado       varchar(15) not null,
   primary key (chr_cuencodigo)
);

create table TIPOMOVIMIENTO
(
   chr_tipocodigo       varchar(3) not null,
   vch_tipodescripcion  varchar(40) not null,
   vch_tipoaccion       varchar(10) not null,
   primary key (chr_tipocodigo)
);

create table MOVIMIENTO
(
   chr_cuencodigo       varchar(14) not null,
   int_movinumero       int not null,
   dtt_movifecha        datetime not null,
   chr_emplcodigo       varchar(4) not null,
   chr_tipocodigo       varchar(3) not null,
   dec_moviimporte      decimal(10,2) not null,
   primary key (chr_cuencodigo, int_movinumero)
);


alter table DETALLE_FACTURA add constraint FK_APLICA_A foreign key (ID_FACTURA)
      references FACTURA (ID_FACTURA) on delete restrict on update restrict;

alter table DETALLE_FACTURA add constraint FK_CONTIENE foreign key (CODIGO_LOCALIDAD)
      references LOCALIDAD_PARTIDO (CODIGO_LOCALIDAD) on delete restrict on update restrict;

alter table DETALLE_FACTURA add constraint FK_REGISTRA foreign key (CODIGO)
      references PARTIDO_FUTBOL (CODIGO) on delete restrict on update restrict;

alter table FACTURA add constraint FK_TIENE foreign key (CODIGO)
      references PARTIDO_FUTBOL (CODIGO) on delete restrict on update restrict;

-- Insertar datos de prueba para PARTIDO_FUTBOL (al menos 5 partidos)
INSERT INTO PARTIDO_FUTBOL(CODIGO, EQUIPO_LOCAL, EQUIPO_VISITANTE, FECHA, LUGAR) VALUES
('P001','EQUIPO A','EQUIPO B', DATE_ADD(NOW(), INTERVAL 2 DAY),'ESTADIO CENTRAL'),
('P002','EQUIPO C','EQUIPO D', DATE_ADD(NOW(), INTERVAL 5 DAY),'ESTADIO NORTE'),
('P003','EQUIPO E','EQUIPO F', DATE_ADD(NOW(), INTERVAL 7 DAY),'ESTADIO SUR'),
('P004','EQUIPO G','EQUIPO H', DATE_ADD(NOW(), INTERVAL 10 DAY),'ESTADIO OESTE'),
('P005','EQUIPO I','EQUIPO J', DATE_ADD(NOW(), INTERVAL 15 DAY),'ESTADIO ESTE');

-- Insertar datos de prueba para LOCALIDAD_PARTIDO (al menos 20 registros)
INSERT INTO LOCALIDAD_PARTIDO(CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO) VALUES
('PALCO_P001','P001',50,30.00),
('TRIBUNA_P001','P001',300,20.00),
('GENERAL_P001','P001',1500,5.00),
('VISITA_P001','P001',200,6.00),
('PALCO_P002','P002',40,28.00),
('TRIBUNA_P002','P002',250,18.00),
('GENERAL_P002','P002',1400,5.50),
('VISITA_P002','P002',180,6.50),
('PALCO_P003','P003',30,32.00),
('TRIBUNA_P003','P003',220,22.00),
('GENERAL_P003','P003',1200,4.50),
('VISITA_P003','P003',160,5.50),
('PALCO_P004','P004',25,35.00),
('TRIBUNA_P004','P004',200,19.00),
('GENERAL_P004','P004',1100,4.00),
('VISITA_P004','P004',150,5.00),
('PALCO_P005','P005',20,40.00),
('TRIBUNA_P005','P005',180,21.00),
('GENERAL_P005','P005',1000,3.50),
('VISITA_P005','P005',130,4.50);


