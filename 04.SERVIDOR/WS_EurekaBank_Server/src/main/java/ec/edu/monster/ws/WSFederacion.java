package ec.edu.monster.ws;

import ec.edu.monster.modelo.LocalidadPartido;
import ec.edu.monster.modelo.PartidoFutbol;
import ec.edu.monster.modelo.PurchaseResponse;
import ec.edu.monster.modelo.ResumenVentaLocalidad;
import ec.edu.monster.servicio.FederacionService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import java.util.ArrayList;
import java.util.List;
import ec.edu.monster.modelo.Factura;

@WebService(serviceName = "WSFederacion")
public class WSFederacion {

    @WebMethod(operationName = "listarPartidosDisponibles")
    @WebResult(name = "partido")
    public List<PartidoFutbol> listarPartidosDisponibles() {
        try {
            System.out.println("[WSFederacion] listarPartidosDisponibles invocado");
            FederacionService s = new FederacionService();
            List<PartidoFutbol> resultado = s.listarPartidosDisponibles();
            System.out.println("[WSFederacion] Retornando " + resultado.size() + " partidos");
            return resultado;
        } catch (Exception e) {
            System.out.println("[WSFederacion] ERROR en listarPartidosDisponibles: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @WebMethod(operationName = "listarLocalidadesDisponibles")
    @WebResult(name = "localidad")
    public List<LocalidadPartido> listarLocalidadesDisponibles(@WebParam(name = "codigoPartido") String codigoPartido) {
        try {
            System.out.println("[WSFederacion] listarLocalidadesDisponibles invocado con codigoPartido=" + codigoPartido);
            FederacionService s = new FederacionService();
            List<LocalidadPartido> resultado = s.listarLocalidadesDisponibles(codigoPartido);
            System.out.println("[WSFederacion] Retornando " + resultado.size() + " localidades");
            return resultado;
        } catch (Exception e) {
            System.out.println("[WSFederacion] ERROR en listarLocalidadesDisponibles: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @WebMethod(operationName = "comprarBoleto")
    @WebResult(name = "resultado")
    public PurchaseResponse comprarBoleto(
            @WebParam(name = "codigoPartido") String codigoPartido,
            @WebParam(name = "codigoLocalidad") String codigoLocalidad,
            @WebParam(name = "cantidad") int cantidad,
            @WebParam(name = "cliente") String cliente,
            @WebParam(name = "vendedor") String vendedor) {
        try {
            FederacionService s = new FederacionService();
            return s.comprarBoleto(codigoPartido, codigoLocalidad, cantidad, cliente, vendedor);
        } catch (Exception e) {
            PurchaseResponse r = new PurchaseResponse();
            r.setEstado(-1);
            r.setMensaje(e.getMessage());
            return r;
        }
    }

    @WebMethod(operationName = "comprarBoletosMultiples")
    @WebResult(name = "resultado")
    public PurchaseResponse comprarBoletosMultiples(
            @WebParam(name = "codigoPartido") String codigoPartido,
            @WebParam(name = "codigosLocalidades") String codigosLocalidades,
            @WebParam(name = "cantidades") String cantidades,
            @WebParam(name = "cliente") String cliente,
            @WebParam(name = "vendedor") String vendedor) {
        try {
            FederacionService s = new FederacionService();
            return s.comprarBoletosMultiples(codigoPartido, codigosLocalidades, cantidades, cliente, vendedor);
        } catch (Exception e) {
            PurchaseResponse r = new PurchaseResponse();
            r.setEstado(-1);
            r.setMensaje(e.getMessage());
            return r;
        }
    }

    @WebMethod(operationName = "listarResumenVentas")
    @WebResult(name = "resumen")
    public List<ResumenVentaLocalidad> listarResumenVentas(@WebParam(name = "codigoPartido") String codigoPartido) {
        try {
            FederacionService s = new FederacionService();
            List<ResumenVentaLocalidad> resultado = s.listarResumenVentas(codigoPartido);
            return resultado;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @WebMethod(operationName = "listarFacturas")
    @WebResult(name = "factura")
    public List<Factura> listarFacturas(@WebParam(name = "idCliente") String idCliente) {
        try {
            FederacionService s = new FederacionService();
            return s.listarFacturas(idCliente);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    @WebMethod(operationName = "obtenerFactura")
    @WebResult(name = "factura")
    public Factura obtenerFactura(@WebParam(name = "idFactura") long idFactura) {
        try {
            FederacionService s = new FederacionService();
            return s.obtenerFactura(idFactura);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @WebMethod(operationName = "obtenerDetallesFactura")
    @WebResult(name = "detalle")
    public List<ec.edu.monster.modelo.DetalleFactura> obtenerDetallesFactura(@WebParam(name = "idFactura") long idFactura) {
        try {
            FederacionService s = new FederacionService();
            return s.obtenerDetallesFactura(idFactura);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @WebMethod(operationName = "listarTodasLasFacturas")
    @WebResult(name = "factura")
    public List<Factura> listarTodasLasFacturas(
            @WebParam(name = "fecha") String fecha, 
            @WebParam(name = "vendedor") String vendedor) {
        try {
            FederacionService s = new FederacionService();
            return s.listarTodasLasFacturas(fecha, vendedor);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @WebMethod(operationName = "obtenerAsientosOcupados")
    @WebResult(name = "asientoOcupado")
    public List<ec.edu.monster.modelo.AsientoOcupado> obtenerAsientosOcupados(@WebParam(name = "codigoPartido") String codigoPartido) {
        try {
            return new FederacionService().obtenerAsientosOcupados(codigoPartido);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
