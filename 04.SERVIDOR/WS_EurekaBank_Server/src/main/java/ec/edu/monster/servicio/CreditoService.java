package ec.edu.monster.servicio;

import ec.edu.monster.db.AccesoDB;
import ec.edu.monster.modelo.EvaluacionCredito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ec.edu.monster.modelo.Amortizacion;
import ec.edu.monster.modelo.CuotaAmortizacion;

public class CreditoService {

    public EvaluacionCredito evaluarSujetoCredito(String idCliente) {
        EvaluacionCredito resultado = new EvaluacionCredito();
        
        try (Connection cn = AccesoDB.getConnection()) {
            
            // 1. Validar Cliente y obtener Edad/Género
            String sqlCliente = "SELECT EDAD, GENERO FROM CLIENTE WHERE ID_CLIENTE = ?";
            int edad = 0;
            String genero = "";
            boolean clienteExiste = false;
            
            try (PreparedStatement pstm = cn.prepareStatement(sqlCliente)) {
                pstm.setString(1, idCliente);
                try (ResultSet rs = pstm.executeQuery()) {
                    if (rs.next()) {
                        clienteExiste = true;
                        edad = rs.getInt("EDAD");
                        genero = rs.getString("GENERO");
                    }
                }
            }
            
            if (!clienteExiste) {
                resultado.setEstado(0);
                resultado.setMensaje("Cliente no encontrado o inactivo.");
                return resultado;
            }
            
            // 2. Mayor de 25 años (si es masculino)
            if ("M".equalsIgnoreCase(genero) && edad <= 25) {
                resultado.setEstado(0);
                resultado.setMensaje("Cliente masculino debe ser mayor de 25 años.");
                return resultado;
            }
            
            // 3. Al menos 1 depósito en el último mes
            String sqlDepMes = "SELECT COUNT(*) AS CANT FROM TRANSACCION WHERE ID_CLIENTE_CREDITO = ? AND TIPO_TRANSACCION = 'DEPOSITO' AND FECHA_TRANSACCION >= DATE_SUB(SYSDATE(), INTERVAL 1 MONTH)";
            try (PreparedStatement pstm = cn.prepareStatement(sqlDepMes)) {
                pstm.setString(1, idCliente);
                try (ResultSet rs = pstm.executeQuery()) {
                    if (rs.next() && rs.getInt("CANT") < 1) {
                        resultado.setEstado(0);
                        resultado.setMensaje("El cliente no tiene depósitos en el último mes.");
                        return resultado;
                    }
                }
            }
            
            // 4. No tiene créditos activos (cuotas pendientes)
            String sqlCreditos = "SELECT COUNT(*) AS CANT FROM AMORTIZACION WHERE ID_CLIENTE_CREDITO = ? AND ESTADO_CUOTA = 'PENDIENTE'";
            try (PreparedStatement pstm = cn.prepareStatement(sqlCreditos)) {
                pstm.setString(1, idCliente);
                try (ResultSet rs = pstm.executeQuery()) {
                    if (rs.next() && rs.getInt("CANT") > 0) {
                        resultado.setEstado(0);
                        resultado.setMensaje("El cliente tiene créditos activos (cuotas pendientes).");
                        return resultado;
                    }
                }
            }
            
            // 5. Calcular promedios últimos 3 meses
            double sumDepositos = 0;
            double sumRetiros = 0;
            
            String sqlSumas = "SELECT TIPO_TRANSACCION, SUM(MONTO) AS TOTAL FROM TRANSACCION WHERE ID_CLIENTE_CREDITO = ? AND FECHA_TRANSACCION >= DATE_SUB(SYSDATE(), INTERVAL 3 MONTH) GROUP BY TIPO_TRANSACCION";
            try (PreparedStatement pstm = cn.prepareStatement(sqlSumas)) {
                pstm.setString(1, idCliente);
                try (ResultSet rs = pstm.executeQuery()) {
                    while (rs.next()) {
                        String tipo = rs.getString("TIPO_TRANSACCION");
                        if ("DEPOSITO".equalsIgnoreCase(tipo)) {
                            sumDepositos = rs.getDouble("TOTAL");
                        } else if ("RETIRO".equalsIgnoreCase(tipo)) {
                            sumRetiros = rs.getDouble("TOTAL");
                        }
                    }
                }
            }
            
            double promDepositos = sumDepositos / 3.0;
            double promRetiros = sumRetiros / 3.0;
            
            // Fórmula: ((Promedio Depósitos - Promedio Retiros) * 30%) * 6
            double capacidadPago = (promDepositos - promRetiros) * 0.30;
            if (capacidadPago <= 0) {
                resultado.setEstado(0);
                resultado.setMensaje("Capacidad de pago insuficiente para otorgar crédito.");
                return resultado;
            }
            
            double montoMaximo = capacidadPago * 6;
            
            resultado.setEstado(1);
            resultado.setMensaje("Cliente APRUEBA para crédito.");
            resultado.setMontoMaximo(Math.round(montoMaximo * 100.0) / 100.0);
            
        } catch (SQLException e) {
            resultado.setEstado(-1);
            resultado.setMensaje("Error en base de datos: " + e.getMessage());
        }
        
        return resultado;
    }
    
    public List<CuotaAmortizacion> generarTablaAmortizacion(double valorLocalidades, int plazoMeses) {
        List<CuotaAmortizacion> tabla = new ArrayList<>();
        
        // Validar rango de meses permitido (entre 3 y 18)
        if (plazoMeses < 3 || plazoMeses > 18) {
            return tabla;
        }
        
        double tasaAnual = 0.165;
        double tasaPeriodo = tasaAnual / 12.0;
        
        // Cuota = ValorLocalidades / ( (1 - (1 + TasaPeriodo)^-NumeroCuotas) / TasaPeriodo )
        double factor = (1 - Math.pow(1 + tasaPeriodo, -plazoMeses)) / tasaPeriodo;
        double cuota = valorLocalidades / factor;
        
        double saldo = valorLocalidades;
        
        for (int i = 1; i <= plazoMeses; i++) {
            double interes = saldo * tasaPeriodo;
            double capital = cuota - interes;
            saldo = saldo - capital;
            
            // Redondear a 2 decimales
            double cuotaRedondeada = Math.round(cuota * 100.0) / 100.0;
            double interesRedondeado = Math.round(interes * 100.0) / 100.0;
            double capitalRedondeado = Math.round(capital * 100.0) / 100.0;
            double saldoRedondeado = Math.round(saldo * 100.0) / 100.0;
            
            if (saldoRedondeado < 0) saldoRedondeado = 0; // Ajuste por decimales en la cuota final
            
            tabla.add(new CuotaAmortizacion(i, cuotaRedondeada, interesRedondeado, capitalRedondeado, saldoRedondeado));
        }
        
        return tabla;
    }

    public List<Amortizacion> listarAmortizaciones(String idCliente) {
        List<Amortizacion> lista = new ArrayList<>();
        String sql = "SELECT A.ID_AMORTIZACION, A.ID_CLIENTE_CREDITO, C.NOMBRES, A.NUMERO_CUOTA, DATE_FORMAT(A.FECHA_VENCIMIENTO, '%Y-%m-%d') AS FECHA, A.MONTO_CUOTA, A.INTERES, A.CAPITAL, A.SALDO, A.ESTADO_CUOTA FROM AMORTIZACION A JOIN CLIENTE C ON A.ID_CLIENTE_CREDITO = C.ID_CLIENTE WHERE A.ID_CLIENTE_CREDITO = ? ORDER BY A.NUMERO_CUOTA ASC";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, idCliente);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Amortizacion a = new Amortizacion(
                        rs.getInt("ID_AMORTIZACION"),
                        rs.getString("ID_CLIENTE_CREDITO"),
                        rs.getString("NOMBRES"),
                        rs.getInt("NUMERO_CUOTA"),
                        rs.getString("FECHA"),
                        rs.getDouble("MONTO_CUOTA"),
                        rs.getDouble("INTERES"),
                        rs.getDouble("CAPITAL"),
                        rs.getDouble("SALDO"),
                        rs.getString("ESTADO_CUOTA")
                    );
                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardarAmortizaciones(String idCliente, double valorLocalidades, int plazoMeses) {
        List<CuotaAmortizacion> cuotas = generarTablaAmortizacion(valorLocalidades, plazoMeses);
        if(cuotas.isEmpty()) return false;
        
        String sql = "INSERT INTO AMORTIZACION (ID_CLIENTE_CREDITO, NUMERO_CUOTA, FECHA_VENCIMIENTO, MONTO_CUOTA, INTERES, CAPITAL, SALDO, ESTADO_CUOTA) VALUES (?, ?, DATE_ADD(SYSDATE(), INTERVAL ? MONTH), ?, ?, ?, ?, 'PENDIENTE')";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql)) {
             
            for (CuotaAmortizacion cuota : cuotas) {
                pstm.setString(1, idCliente);
                pstm.setInt(2, cuota.getNumeroCuota());
                pstm.setInt(3, cuota.getNumeroCuota());
                pstm.setDouble(4, cuota.getValorCuota());
                pstm.setDouble(5, cuota.getInteresPagado());
                pstm.setDouble(6, cuota.getCapitalPagado());
                pstm.setDouble(7, cuota.getSaldo());
                pstm.addBatch();
            }
            pstm.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
