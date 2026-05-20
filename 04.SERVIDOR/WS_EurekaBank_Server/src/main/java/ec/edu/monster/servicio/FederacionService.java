package ec.edu.monster.servicio;

import ec.edu.monster.db.AccesoDB;
import ec.edu.monster.modelo.LocalidadPartido;
import ec.edu.monster.modelo.PartidoFutbol;
import ec.edu.monster.modelo.PurchaseResponse;
import ec.edu.monster.modelo.ResumenVentaLocalidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FederacionService {

    public List<PartidoFutbol> listarPartidosDisponibles() {
        List<PartidoFutbol> lista = new ArrayList<>();
        String sql = "SELECT CODIGO, EQUIPO_LOCAL, EQUIPO_VISITANTE, FECHA, LUGAR FROM PARTIDO_FUTBOL ORDER BY FECHA";
        try (Connection cn = AccesoDB.getConnection();
                PreparedStatement pstm = cn.prepareStatement(sql);
                ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                PartidoFutbol p = new PartidoFutbol();
                p.setCodigo(rs.getString("CODIGO"));
                p.setEquipoLocal(rs.getString("EQUIPO_LOCAL"));
                p.setEquipoVisitante(rs.getString("EQUIPO_VISITANTE"));
                p.setFecha(rs.getTimestamp("FECHA"));
                p.setLugar(rs.getString("LUGAR"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar partidos: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<LocalidadPartido> listarLocalidadesDisponibles(String codigoPartido) {
        List<LocalidadPartido> lista = new ArrayList<>();
        String sql = "SELECT CODIGO_LOCALIDAD, CODIGO_PARTIDO, DISPONIBILIDAD, PRECIO FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO = ? AND DISPONIBILIDAD > 0";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, codigoPartido);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    LocalidadPartido l = new LocalidadPartido();
                    l.setCodigoLocalidad(rs.getString("CODIGO_LOCALIDAD"));
                    l.setCodigoPartido(rs.getString("CODIGO_PARTIDO"));
                    l.setDisponibilidad(rs.getInt("DISPONIBILIDAD"));
                    l.setPrecio(rs.getDouble("PRECIO"));
                    lista.add(l);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar localidades: " + e.getMessage(), e);
        }
        return lista;
    }

    public PurchaseResponse comprarBoleto(String codigoPartido, String codigoLocalidad, int cantidad, String cliente) {
        PurchaseResponse resp = new PurchaseResponse();
        Connection cn = null;
        try {
            cn = AccesoDB.getConnection();
            cn.setAutoCommit(false);

            // verificar disponibilidad
            String sql = "SELECT DISPONIBILIDAD, PRECIO FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO = ? AND CODIGO_LOCALIDAD = ? FOR UPDATE";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, codigoPartido);
            pstm.setString(2, codigoLocalidad);
            ResultSet rs = pstm.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Localidad no encontrada");
            }
            int dispon = rs.getInt("DISPONIBILIDAD");
            double precio = rs.getDouble("PRECIO");
            rs.close();
            pstm.close();

            if (dispon < cantidad) {
                throw new SQLException("No hay suficiente disponibilidad");
            }

            // decrementar disponibilidad
            sql = "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD = DISPONIBILIDAD - ? WHERE CODIGO_PARTIDO = ? AND CODIGO_LOCALIDAD = ?";
            pstm = cn.prepareStatement(sql);
            pstm.setInt(1, cantidad);
            pstm.setString(2, codigoPartido);
            pstm.setString(3, codigoLocalidad);
            int mod = pstm.executeUpdate();
            pstm.close();
            if (mod == 0) {
                throw new SQLException("No se pudo actualizar disponibilidad");
            }

            // crear factura
            // obtener nuevo id
            long idFactura = 1;
            sql = "SELECT IFNULL(MAX(ID_FACTURA),0) + 1 AS NEWID FROM FACTURA";
            pstm = cn.prepareStatement(sql);
            rs = pstm.executeQuery();
            if (rs.next()) idFactura = rs.getLong("NEWID");
            rs.close();
            pstm.close();

            double subtotal = precio * cantidad;
            double iva = subtotal * 0.12; // 12% IVA
            double total = subtotal + iva;

            sql = "INSERT INTO FACTURA(ID_FACTURA, CODIGO, FECHA_EMISION, SUBTOTAL, IVA, TOTAL) VALUES(?, ?, SYSDATE(), ?, ?, ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setLong(1, idFactura);
            pstm.setString(2, codigoPartido);
            pstm.setDouble(3, subtotal);
            pstm.setDouble(4, iva);
            pstm.setDouble(5, total);
            pstm.executeUpdate();
            pstm.close();

            // detalle
            long idDetalle = 1;
            sql = "SELECT IFNULL(MAX(ID_DETALLE),0) + 1 AS NEWID FROM DETALLE_FACTURA";
            pstm = cn.prepareStatement(sql);
            rs = pstm.executeQuery();
            if (rs.next()) idDetalle = rs.getLong("NEWID");
            rs.close();
            pstm.close();

            sql = "INSERT INTO DETALLE_FACTURA(ID_DETALLE, CODIGO, ID_FACTURA, CODIGO_LOCALIDAD, CANTIDAD, PRECIO_UNITARIO, TOTAL_DETALLE) VALUES(?, ?, ?, ?, ?, ?, ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setLong(1, idDetalle);
            pstm.setString(2, codigoPartido);
            pstm.setLong(3, idFactura);
            pstm.setString(4, codigoLocalidad);
            pstm.setInt(5, cantidad);
            pstm.setDouble(6, precio);
            pstm.setDouble(7, subtotal);
            pstm.executeUpdate();
            pstm.close();

            cn.commit();

            resp.setEstado(1);
            resp.setMensaje("Compra registrada");
            resp.setFacturaId(idFactura);
            resp.setTotal(total);
            return resp;

        } catch (SQLException e) {
            try { if (cn != null) cn.rollback(); } catch (Exception ex) {}
            throw new RuntimeException("Error en compra: " + e.getMessage(), e);
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception ex) {}
        }
    }

    public List<ResumenVentaLocalidad> listarResumenVentas(String codigoPartido) {
        List<ResumenVentaLocalidad> lista = new ArrayList<>();
        boolean filtrar = codigoPartido != null && !codigoPartido.isBlank();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT d.CODIGO_LOCALIDAD, SUM(d.CANTIDAD) AS VENDIDOS, SUM(d.TOTAL_DETALLE) AS TOTAL_RECAUDADO ");
        sql.append("FROM DETALLE_FACTURA d INNER JOIN FACTURA f ON f.ID_FACTURA = d.ID_FACTURA ");
        if (filtrar) {
            sql.append("WHERE f.CODIGO = ? ");
        }
        sql.append("GROUP BY d.CODIGO_LOCALIDAD ORDER BY d.CODIGO_LOCALIDAD");

        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql.toString())) {
            if (filtrar) {
                pstm.setString(1, codigoPartido);
            }
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ResumenVentaLocalidad item = new ResumenVentaLocalidad();
                    item.setCodigoLocalidad(rs.getString("CODIGO_LOCALIDAD"));
                    item.setVendidos(rs.getInt("VENDIDOS"));
                    item.setTotalRecaudado(rs.getDouble("TOTAL_RECAUDADO"));
                    lista.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar resumen de ventas: " + e.getMessage(), e);
        }
        return lista;
    }

}
