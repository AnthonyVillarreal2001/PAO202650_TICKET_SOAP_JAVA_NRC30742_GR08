package ec.edu.monster.servicio;

import ec.edu.monster.db.AccesoDB;
import ec.edu.monster.modelo.Pais;
import ec.edu.monster.modelo.Estadio;
import ec.edu.monster.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CRUDService {

    // ================== PAISES ==================
    public List<Pais> listarPaises() {
        List<Pais> lista = new ArrayList<>();
        String sql = "SELECT ID_PAIS, NOMBRE_PAIS FROM PAIS";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                lista.add(new Pais(rs.getString("ID_PAIS"), rs.getString("NOMBRE_PAIS")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardarPais(Pais p) {
        String sql = "INSERT INTO PAIS (ID_PAIS, NOMBRE_PAIS) VALUES (?, ?) ON DUPLICATE KEY UPDATE NOMBRE_PAIS = ?";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, p.getIdPais());
            pstm.setString(2, p.getNombrePais());
            pstm.setString(3, p.getNombrePais());
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarPais(String idPais) {
        String sql = "DELETE FROM PAIS WHERE ID_PAIS = ?";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, idPais);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== ESTADIOS ==================
    public List<Estadio> listarEstadios() {
        List<Estadio> lista = new ArrayList<>();
        String sql = "SELECT ID_ESTADIO, NOMBRE_ESTADIO, CIUDAD, CAPACIDAD FROM ESTADIO";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                lista.add(new Estadio(rs.getString("ID_ESTADIO"), rs.getString("NOMBRE_ESTADIO"), rs.getString("CIUDAD"), rs.getInt("CAPACIDAD")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardarEstadio(Estadio e) {
        String sql = "INSERT INTO ESTADIO (ID_ESTADIO, NOMBRE_ESTADIO, CIUDAD, CAPACIDAD) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE NOMBRE_ESTADIO=?, CIUDAD=?, CAPACIDAD=?";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, e.getIdEstadio());
            pstm.setString(2, e.getNombreEstadio());
            pstm.setString(3, e.getCiudad());
            pstm.setInt(4, e.getCapacidad());
            pstm.setString(5, e.getNombreEstadio());
            pstm.setString(6, e.getCiudad());
            pstm.setInt(7, e.getCapacidad());
            return pstm.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean eliminarEstadio(String idEstadio) {
        String sql = "DELETE FROM ESTADIO WHERE ID_ESTADIO = ?";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, idEstadio);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== CLIENTES ==================
    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT ID_CLIENTE, NOMBRES, CORREO, TELEFONO, EDAD, GENERO FROM CLIENTE";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(rs.getString("ID_CLIENTE"), rs.getString("NOMBRES"), rs.getString("CORREO"), rs.getString("TELEFONO"), rs.getInt("EDAD"), rs.getString("GENERO")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardarCliente(Cliente c) {
        String sql = "INSERT INTO CLIENTE (ID_CLIENTE, NOMBRES, CORREO, TELEFONO, EDAD, GENERO) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE NOMBRES=?, CORREO=?, TELEFONO=?, EDAD=?, GENERO=?";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, c.getIdCliente());
            pstm.setString(2, c.getNombres());
            pstm.setString(3, c.getCorreo());
            pstm.setString(4, c.getTelefono());
            pstm.setInt(5, c.getEdad());
            pstm.setString(6, c.getGenero());
            pstm.setString(7, c.getNombres());
            pstm.setString(8, c.getCorreo());
            pstm.setString(9, c.getTelefono());
            pstm.setInt(10, c.getEdad());
            pstm.setString(11, c.getGenero());
            return pstm.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean eliminarCliente(String idCliente) {
        String sql = "DELETE FROM CLIENTE WHERE ID_CLIENTE = ?";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, idCliente);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // ================== PARTIDOS ==================
    public List<ec.edu.monster.modelo.PartidoFutbol> listarTodosPartidos() {
        List<ec.edu.monster.modelo.PartidoFutbol> lista = new ArrayList<>();
        String sql = "SELECT CODIGO, EQUIPO_LOCAL, EQUIPO_VISITANTE, FECHA, LUGAR FROM PARTIDO_FUTBOL ORDER BY FECHA ASC";
        try (Connection cn = AccesoDB.getConnection();
             PreparedStatement pstm = cn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                lista.add(new ec.edu.monster.modelo.PartidoFutbol(
                    rs.getString("CODIGO"),
                    rs.getString("EQUIPO_LOCAL"),
                    rs.getString("EQUIPO_VISITANTE"),
                    rs.getTimestamp("FECHA"),
                    rs.getString("LUGAR")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardarPartido(ec.edu.monster.modelo.PartidoFutbol p) {
        String sql = "INSERT INTO PARTIDO_FUTBOL (CODIGO, EQUIPO_LOCAL, EQUIPO_VISITANTE, FECHA, LUGAR) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE EQUIPO_LOCAL=?, EQUIPO_VISITANTE=?, FECHA=?, LUGAR=?";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, p.getCodigo());
            pstm.setString(2, p.getEquipoLocal());
            pstm.setString(3, p.getEquipoVisitante());
            pstm.setTimestamp(4, new java.sql.Timestamp(p.getFecha().getTime()));
            pstm.setString(5, p.getLugar());
            
            pstm.setString(6, p.getEquipoLocal());
            pstm.setString(7, p.getEquipoVisitante());
            pstm.setTimestamp(8, new java.sql.Timestamp(p.getFecha().getTime()));
            pstm.setString(9, p.getLugar());
            
            return pstm.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean eliminarPartido(String codigo) {
        String sql = "DELETE FROM PARTIDO_FUTBOL WHERE CODIGO = ?";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, codigo);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
