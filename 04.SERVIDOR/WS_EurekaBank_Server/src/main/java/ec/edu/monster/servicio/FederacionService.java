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
import ec.edu.monster.modelo.Factura;

public class FederacionService {

    public List<PartidoFutbol> listarPartidosDisponibles() {
        List<PartidoFutbol> lista = new ArrayList<>();
        String sql = "SELECT CODIGO, EQUIPO_LOCAL, EQUIPO_VISITANTE, FECHA, LUGAR FROM PARTIDO_FUTBOL WHERE FECHA >= '2026-06-01' ORDER BY FECHA";
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

    public PurchaseResponse comprarBoleto(String codigoPartido, String codigoLocalidad, int cantidad, String cliente, String vendedor) {
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

            sql = "INSERT INTO FACTURA(ID_FACTURA, CODIGO, ID_CLIENTE, VENDEDOR, FECHA_EMISION, SUBTOTAL, IVA, TOTAL) VALUES(?, ?, ?, ?, SYSDATE(), ?, ?, ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setLong(1, idFactura);
            pstm.setString(2, codigoPartido);
            pstm.setString(3, cliente);
            pstm.setString(4, vendedor);
            pstm.setDouble(5, subtotal);
            pstm.setDouble(6, iva);
            pstm.setDouble(7, total);
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
            e.printStackTrace();
            PurchaseResponse r = new PurchaseResponse();
            r.setEstado(-1);
            r.setMensaje("Error DB: " + e.getMessage());
            return r;
        }
    }

    public PurchaseResponse comprarBoletosMultiples(String codigoPartido, String codigosLocalidades, String cantidades, String cliente, String vendedor) {
        PurchaseResponse resp = new PurchaseResponse();
        String sql = "";
        try (Connection cn = AccesoDB.getConnection()) {
            cn.setAutoCommit(false);
            
            String[] locs = codigosLocalidades.split(",");
            String[] cants = cantidades.split(",");
            
            if (locs.length != cants.length) {
                resp.setEstado(-1);
                resp.setMensaje("Las listas de localidades y cantidades no coinciden.");
                return resp;
            }

            double subtotalGeneral = 0;
            double[] precios = new double[locs.length];

            for (int i = 0; i < locs.length; i++) {
                String loc = locs[i];
                int cant = Integer.parseInt(cants[i]);
                
                // 1. Obtener precio y disponibilidad
                double precio = 0;
                int disp = 0;
                sql = "SELECT PRECIO, DISPONIBILIDAD FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO = ? AND CODIGO_LOCALIDAD = ? FOR UPDATE";
                try (PreparedStatement pstm = cn.prepareStatement(sql)) {
                    pstm.setString(1, codigoPartido);
                    pstm.setString(2, loc);
                    try (ResultSet rs = pstm.executeQuery()) {
                        if (rs.next()) {
                            precio = rs.getDouble("PRECIO");
                            disp = rs.getInt("DISPONIBILIDAD");
                        } else {
                            cn.rollback();
                            resp.setEstado(-1);
                            resp.setMensaje("Localidad " + loc + " no encontrada.");
                            return resp;
                        }
                    }
                }
                
                if (disp < cant) {
                    cn.rollback();
                    resp.setEstado(-1);
                    resp.setMensaje("Sin disponibilidad suficiente para " + loc + ".");
                    return resp;
                }
                
                // 2. Actualizar disponibilidad
                sql = "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD = ? WHERE CODIGO_PARTIDO = ? AND CODIGO_LOCALIDAD = ?";
                try (PreparedStatement pstm = cn.prepareStatement(sql)) {
                    pstm.setInt(1, disp - cant);
                    pstm.setString(2, codigoPartido);
                    pstm.setString(3, loc);
                    pstm.executeUpdate();
                }
                
                precios[i] = precio;
                subtotalGeneral += (precio * cant);
            }

            // 3. Crear Factura única
            long idFactura = 1;
            sql = "SELECT IFNULL(MAX(ID_FACTURA),0) + 1 AS NEWID FROM FACTURA";
            try (PreparedStatement pstm = cn.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) idFactura = rs.getLong("NEWID");
            }

            double iva = subtotalGeneral * 0.12;
            double total = subtotalGeneral + iva;

            sql = "INSERT INTO FACTURA(ID_FACTURA, CODIGO, ID_CLIENTE, VENDEDOR, FECHA_EMISION, SUBTOTAL, IVA, TOTAL) VALUES(?, ?, ?, ?, SYSDATE(), ?, ?, ?)";
            try (PreparedStatement pstm = cn.prepareStatement(sql)) {
                pstm.setLong(1, idFactura);
                pstm.setString(2, codigoPartido);
                pstm.setString(3, cliente);
                pstm.setString(4, vendedor);
                pstm.setDouble(5, subtotalGeneral);
                pstm.setDouble(6, iva);
                pstm.setDouble(7, total);
                pstm.executeUpdate();
            }

            // 4. Crear múltiples Detalles de Factura
            long idDetalle = 1;
            sql = "SELECT IFNULL(MAX(ID_DETALLE),0) + 1 AS NEWID FROM DETALLE_FACTURA";
            try (PreparedStatement pstm = cn.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) idDetalle = rs.getLong("NEWID");
            }

            sql = "INSERT INTO DETALLE_FACTURA(ID_DETALLE, CODIGO, ID_FACTURA, CODIGO_LOCALIDAD, CANTIDAD, PRECIO_UNITARIO, TOTAL_DETALLE) VALUES(?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstm = cn.prepareStatement(sql)) {
                for (int i = 0; i < locs.length; i++) {
                    pstm.setLong(1, idDetalle++);
                    pstm.setString(2, codigoPartido);
                    pstm.setLong(3, idFactura);
                    pstm.setString(4, locs[i]);
                    pstm.setInt(5, Integer.parseInt(cants[i]));
                    pstm.setDouble(6, precios[i]);
                    pstm.setDouble(7, precios[i] * Integer.parseInt(cants[i]));
                    pstm.addBatch();
                }
                pstm.executeBatch();
            }

            cn.commit();

            resp.setEstado(1);
            resp.setMensaje("Compra múltiple consolidada exitosamente");
            resp.setFacturaId(idFactura);
            resp.setTotal(total);
            return resp;
        } catch (SQLException e) {
            e.printStackTrace();
            PurchaseResponse r = new PurchaseResponse();
            r.setEstado(-1);
            r.setMensaje("Error DB: " + e.getMessage());
            return r;
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

    public List<Factura> listarFacturas(String idCliente) {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT F.ID_FACTURA, F.CODIGO, F.ID_CLIENTE, C.NOMBRES, F.VENDEDOR, DATE_FORMAT(F.FECHA_EMISION, '%Y-%m-%d %H:%i:%s') AS FECHA, F.SUBTOTAL, F.IVA, F.TOTAL FROM FACTURA F JOIN CLIENTE C ON F.ID_CLIENTE = C.ID_CLIENTE WHERE F.ID_CLIENTE = ? ORDER BY F.FECHA_EMISION DESC";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, idCliente);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Factura f = new Factura(
                        rs.getLong("ID_FACTURA"),
                        rs.getString("CODIGO"),
                        rs.getString("ID_CLIENTE"),
                        rs.getString("NOMBRES"),
                        rs.getString("VENDEDOR"),
                        rs.getString("FECHA"),
                        rs.getDouble("SUBTOTAL"),
                        rs.getDouble("IVA"),
                        rs.getDouble("TOTAL")
                    );
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Factura obtenerFactura(long idFactura) {
        String sql = "SELECT F.ID_FACTURA, F.CODIGO, F.ID_CLIENTE, C.NOMBRES, F.VENDEDOR, DATE_FORMAT(F.FECHA_EMISION, '%Y-%m-%d %H:%i:%s') AS FECHA, F.SUBTOTAL, F.IVA, F.TOTAL FROM FACTURA F JOIN CLIENTE C ON F.ID_CLIENTE = C.ID_CLIENTE WHERE F.ID_FACTURA = ?";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setLong(1, idFactura);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new Factura(
                        rs.getLong("ID_FACTURA"),
                        rs.getString("CODIGO"),
                        rs.getString("ID_CLIENTE"),
                        rs.getString("NOMBRES"),
                        rs.getString("VENDEDOR"),
                        rs.getString("FECHA"),
                        rs.getDouble("SUBTOTAL"),
                        rs.getDouble("IVA"),
                        rs.getDouble("TOTAL")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ec.edu.monster.modelo.DetalleFactura> obtenerDetallesFactura(long idFactura) {
        List<ec.edu.monster.modelo.DetalleFactura> lista = new ArrayList<>();
        String sql = "SELECT ID_DETALLE, CODIGO, ID_FACTURA, CODIGO_LOCALIDAD, CANTIDAD, PRECIO_UNITARIO, TOTAL_DETALLE FROM DETALLE_FACTURA WHERE ID_FACTURA = ?";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setLong(1, idFactura);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ec.edu.monster.modelo.DetalleFactura d = new ec.edu.monster.modelo.DetalleFactura(
                        rs.getLong("ID_DETALLE"),
                        rs.getString("CODIGO"),
                        rs.getLong("ID_FACTURA"),
                        rs.getString("CODIGO_LOCALIDAD"),
                        rs.getInt("CANTIDAD"),
                        rs.getDouble("PRECIO_UNITARIO"),
                        rs.getDouble("TOTAL_DETALLE")
                    );
                    lista.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public List<Factura> listarTodasLasFacturas(String fecha, String vendedor) {
        List<Factura> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT F.ID_FACTURA, F.CODIGO, F.ID_CLIENTE, C.NOMBRES, F.VENDEDOR, DATE_FORMAT(F.FECHA_EMISION, '%Y-%m-%d %H:%i:%s') AS FECHA, F.SUBTOTAL, F.IVA, F.TOTAL " +
            "FROM FACTURA F " +
            "LEFT JOIN CLIENTE C ON F.ID_CLIENTE = C.ID_CLIENTE " +
            "WHERE 1=1 "
        );
        
        boolean hasFecha = (fecha != null && !fecha.trim().isEmpty());
        boolean hasVendedor = (vendedor != null && !vendedor.trim().isEmpty());
        
        if (hasFecha) {
            sql.append("AND DATE(F.FECHA_EMISION) = ? ");
        }
        if (hasVendedor) {
            sql.append("AND F.VENDEDOR = ? ");
        }
        sql.append("ORDER BY F.FECHA_EMISION DESC");

        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql.toString())) {
            
            int idx = 1;
            if (hasFecha) pstm.setString(idx++, fecha);
            if (hasVendedor) pstm.setString(idx++, vendedor);
            
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Factura f = new Factura(
                        rs.getLong("ID_FACTURA"),
                        rs.getString("CODIGO"),
                        rs.getString("ID_CLIENTE"),
                        rs.getString("NOMBRES"),
                        rs.getString("VENDEDOR"),
                        rs.getString("FECHA"),
                        rs.getDouble("SUBTOTAL"),
                        rs.getDouble("IVA"),
                        rs.getDouble("TOTAL")
                    );
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public List<ec.edu.monster.modelo.AsientoOcupado> obtenerAsientosOcupados(String codigoPartido) {
        List<ec.edu.monster.modelo.AsientoOcupado> lista = new ArrayList<>();
        String sql = "SELECT df.CODIGO_LOCALIDAD, df.CANTIDAD, c.NOMBRES, DATE_FORMAT(f.FECHA_EMISION, '%Y-%m-%d %H:%i:%s') AS FECHA " +
                     "FROM DETALLE_FACTURA df " +
                     "JOIN FACTURA f ON df.ID_FACTURA = f.ID_FACTURA " +
                     "JOIN CLIENTE c ON f.ID_CLIENTE = c.ID_CLIENTE " +
                     "WHERE df.CODIGO_LOCALIDAD LIKE ? " +
                     "ORDER BY f.FECHA_EMISION ASC";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, codigoPartido + "-%");
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ec.edu.monster.modelo.AsientoOcupado(
                        rs.getString("CODIGO_LOCALIDAD"),
                        rs.getInt("CANTIDAD"),
                        rs.getString("NOMBRES"),
                        rs.getString("FECHA")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
