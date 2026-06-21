package ec.edu.monster.ws;

import ec.edu.monster.modelo.Pais;
import ec.edu.monster.modelo.Estadio;
import ec.edu.monster.modelo.Cliente;
import ec.edu.monster.servicio.CRUDService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import java.util.List;

@WebService(serviceName = "WSCRUD")
public class WSCRUD {

    // ================== PAISES ==================
    @WebMethod(operationName = "listarPaises")
    @WebResult(name = "pais")
    public List<Pais> listarPaises() {
        return new CRUDService().listarPaises();
    }

    @WebMethod(operationName = "guardarPais")
    @WebResult(name = "resultado")
    public boolean guardarPais(
            @WebParam(name = "idPais") String idPais,
            @WebParam(name = "nombrePais") String nombrePais) {
        return new CRUDService().guardarPais(new Pais(idPais, nombrePais));
    }

    @WebMethod(operationName = "eliminarPais")
    @WebResult(name = "resultado")
    public boolean eliminarPais(@WebParam(name = "idPais") String idPais) {
        return new CRUDService().eliminarPais(idPais);
    }

    // ================== ESTADIOS ==================
    @WebMethod(operationName = "listarEstadios")
    @WebResult(name = "estadio")
    public List<Estadio> listarEstadios() {
        return new CRUDService().listarEstadios();
    }

    @WebMethod(operationName = "guardarEstadio")
    @WebResult(name = "resultado")
    public boolean guardarEstadio(
            @WebParam(name = "idEstadio") String idEstadio,
            @WebParam(name = "nombreEstadio") String nombreEstadio,
            @WebParam(name = "ciudad") String ciudad,
            @WebParam(name = "capacidad") int capacidad) {
        return new CRUDService().guardarEstadio(new Estadio(idEstadio, nombreEstadio, ciudad, capacidad));
    }

    @WebMethod(operationName = "eliminarEstadio")
    @WebResult(name = "resultado")
    public boolean eliminarEstadio(@WebParam(name = "idEstadio") String idEstadio) {
        return new CRUDService().eliminarEstadio(idEstadio);
    }

    // ================== CLIENTES ==================
    @WebMethod(operationName = "listarClientes")
    @WebResult(name = "cliente")
    public List<Cliente> listarClientes() {
        return new CRUDService().listarClientes();
    }

    @WebMethod(operationName = "guardarCliente")
    @WebResult(name = "resultado")
    public boolean guardarCliente(
            @WebParam(name = "idCliente") String idCliente,
            @WebParam(name = "nombres") String nombres,
            @WebParam(name = "correo") String correo,
            @WebParam(name = "telefono") String telefono,
            @WebParam(name = "edad") int edad,
            @WebParam(name = "genero") String genero) {
        return new CRUDService().guardarCliente(new Cliente(idCliente, nombres, correo, telefono, edad, genero));
    }

    @WebMethod(operationName = "eliminarCliente")
    @WebResult(name = "resultado")
    public boolean eliminarCliente(@WebParam(name = "idCliente") String idCliente) {
        return new CRUDService().eliminarCliente(idCliente);
    }
    // ================== PARTIDOS ==================
    @WebMethod(operationName = "listarTodosPartidos")
    @WebResult(name = "partido")
    public List<ec.edu.monster.modelo.PartidoFutbol> listarTodosPartidos() {
        return new CRUDService().listarTodosPartidos();
    }

    @WebMethod(operationName = "guardarPartido")
    @WebResult(name = "resultado")
    public boolean guardarPartido(
            @WebParam(name = "codigo") String codigo,
            @WebParam(name = "equipoLocal") String equipoLocal,
            @WebParam(name = "equipoVisitante") String equipoVisitante,
            @WebParam(name = "fecha") java.util.Date fecha,
            @WebParam(name = "lugar") String lugar) {
        return new CRUDService().guardarPartido(new ec.edu.monster.modelo.PartidoFutbol(codigo, equipoLocal, equipoVisitante, fecha, lugar));
    }

    @WebMethod(operationName = "eliminarPartido")
    @WebResult(name = "resultado")
    public boolean eliminarPartido(@WebParam(name = "codigo") String codigo) {
        return new CRUDService().eliminarPartido(codigo);
    }
}
