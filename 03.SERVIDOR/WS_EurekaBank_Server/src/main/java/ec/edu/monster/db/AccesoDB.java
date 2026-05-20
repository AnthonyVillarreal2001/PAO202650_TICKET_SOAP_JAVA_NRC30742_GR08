/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author JOIS
 */
public class AccesoDB {

    public AccesoDB() {
    }

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        String dbType = "mysql";
        String host = "localhost";
        String port = "3306";
        String database = "bd_examen_ticket";
        String user = "root";
        String pass = "";
        String useSSL = "false";
        String driver = "com.mysql.cj.jdbc.Driver";
        String instance = "";

        try {
            InputStream in = AccesoDB.class.getClassLoader().getResourceAsStream("db.properties");
            if (in != null) {
                props.load(in);
                dbType = props.getProperty("db.type", dbType).toLowerCase();
                host = props.getProperty("db.host", host);
                port = props.getProperty("db.port", port);
                database = props.getProperty("db.name", database);
                user = props.getProperty("db.user", user);
                pass = props.getProperty("db.pass", pass);
                useSSL = props.getProperty("db.useSSL", useSSL);
                driver = props.getProperty("db.driver", driver);
                instance = props.getProperty("db.instance", instance);
            }

            Class.forName(driver);

            if ("sqlserver".equals(dbType)) {
                String url;
                if (instance != null && !instance.isEmpty()) {
                    url = String.format("jdbc:sqlserver://%s;instanceName=%s;databaseName=%s;encrypt=false;trustServerCertificate=true", host, instance, database);
                } else if (port != null && !port.isEmpty()) {
                    url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=false;trustServerCertificate=true", host, port, database);
                } else {
                    url = String.format("jdbc:sqlserver://%s;databaseName=%s;encrypt=false;trustServerCertificate=true", host, database);
                }
                if (user == null || user.trim().isEmpty()) {
                    return DriverManager.getConnection(url);
                }
                return DriverManager.getConnection(url, user, pass);
            }

            String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=UTC", host, port, database, useSSL);
            return DriverManager.getConnection(url, user, pass);

        } catch (SQLException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw new SQLException("ERROR, no se encuentra el driver: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new SQLException("ERROR, no se pudo leer db.properties: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("ERROR, no se tiene acceso al servidor: " + e.getMessage(), e);
        }
    }
    /*public static Connection getConnection() throws SQLException {
        Connection cn = null;
        Properties props = new Properties();
        // Valores por defecto
        String dbType = "mysql"; // mysql | sqlserver
        String host = "34.173.124.32";
        String port = "3306";
        String database = "TicketPremium";
        String user = "MONSTER";
        String pass = "Monster_9";
        String useSSL = "false";
        String driver = "com.mysql.cj.jdbc.Driver";
        String instance = ""; // for SQL Server LocalDB instance name, e.g. MSSQLLocalDB

        // Intentar cargar propiedades desde classpath: /db.properties
        try (InputStream in = AccesoDB.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                try {
                    props.load(in);
                    dbType = props.getProperty("db.type", dbType).toLowerCase();
                    host = props.getProperty("db.host", host);
                    port = props.getProperty("db.port", port);
                    database = props.getProperty("db.name", database);
                    user = props.getProperty("db.user", user);
                    pass = props.getProperty("db.pass", pass);
                    useSSL = props.getProperty("db.useSSL", useSSL);
                    driver = props.getProperty("db.driver", driver);
                    instance = props.getProperty("db.instance", instance);
                } catch (IOException ex) {
                    // si no se puede leer, usamos valores por defecto
                }
            }
        } catch (IOException ex) {
            // ignore, seguiremos con valores por defecto
        }

        try {
            // Preparar driver y URL según el tipo de base de datos
            String url;
            if ("sqlserver".equals(dbType)) {
                // Driver por defecto para SQL Server
                if (driver == null || driver.isEmpty() || driver.contains("mysql")) {
                    driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                }
                Class.forName(driver);
                // Si se especifica instancia (LocalDB), usar instanceName
                if (instance != null && !instance.isEmpty()) {
                    // Ejemplo: jdbc:sqlserver://localhost;instanceName=LOCALDB;databaseName=TicketPremium
                    url = String.format("jdbc:sqlserver://%s;instanceName=%s;databaseName=%s;encrypt=false;trustServerCertificate=true", host, instance, database);
                } else if (port != null && !port.isEmpty()) {
                    url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=false;trustServerCertificate=true", host, port, database);
                } else {
                    url = String.format("jdbc:sqlserver://%s;databaseName=%s;encrypt=false;trustServerCertificate=true", host, database);
                }
                // Si user está vacío o es null, usar autenticación integrada (Windows Auth)
                if (user == null || user.trim().isEmpty()) {
                    // Añadir integratedSecurity=true para Windows Authentication
                    url += ";integratedSecurity=true";
                    cn = DriverManager.getConnection(url);
                } else {
                    cn = DriverManager.getConnection(url, user, pass);
                }
            } else {
                // Por defecto: MySQL
                if (driver == null || driver.isEmpty()) {
                    driver = "com.mysql.cj.jdbc.Driver";
                }
                Class.forName(driver);
                // Construir URL MySQL
                // Forzar conexión SSL pero sin validar el certificado (dev)
                // Construir URL MySQL SIN SSL (para Cloud SQL si permite conexiones sin SSL)
                url = String.format(
                        "jdbc:mysql://%s:%s/%s?useSSL=false&sslMode=DISABLED&serverTimezone=UTC",
                        host, port, database
                );
                cn = DriverManager.getConnection(url, user, pass);

            }
        } catch (SQLException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error, no se encuentra el driver: " + e.getMessage());
        } catch (Exception e) {
            throw new SQLException("Error, no se tiene acceso al servidor: " + e.getMessage());
        }
        return cn;
    }*/

}
