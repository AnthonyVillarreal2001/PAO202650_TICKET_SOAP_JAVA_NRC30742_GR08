package ec.edu.monster.ws;

import ec.edu.monster.modelo.EvaluacionCredito;
import ec.edu.monster.servicio.CreditoService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService(serviceName = "WSCredito")
public class WSCredito {

    @WebMethod(operationName = "evaluarSujetoCredito")
    @WebResult(name = "evaluacion")
    public EvaluacionCredito evaluarSujetoCredito(@WebParam(name = "idCliente") String idCliente) {
        try {
            System.out.println("[WSCredito] evaluarSujetoCredito invocado para cliente: " + idCliente);
            CreditoService s = new CreditoService();
            return s.evaluarSujetoCredito(idCliente);
        } catch (Exception e) {
            System.out.println("[WSCredito] ERROR: " + e.getMessage());
            e.printStackTrace();
            return new EvaluacionCredito(-1, "Error interno del servidor: " + e.getMessage(), 0);
        }
    }

    @WebMethod(operationName = "generarTablaAmortizacion")
    @WebResult(name = "cuota")
    public java.util.List<ec.edu.monster.modelo.CuotaAmortizacion> generarTablaAmortizacion(
            @WebParam(name = "valorLocalidades") double valorLocalidades, 
            @WebParam(name = "plazoMeses") int plazoMeses) {
        try {
            System.out.println("[WSCredito] generarTablaAmortizacion invocado para valor: " + valorLocalidades + ", plazo: " + plazoMeses);
            CreditoService s = new CreditoService();
            return s.generarTablaAmortizacion(valorLocalidades, plazoMeses);
        } catch (Exception e) {
            System.out.println("[WSCredito] ERROR: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    @WebMethod(operationName = "listarAmortizaciones")
    @WebResult(name = "amortizacion")
    public java.util.List<ec.edu.monster.modelo.Amortizacion> listarAmortizaciones(@WebParam(name = "idCliente") String idCliente) {
        try {
            CreditoService s = new CreditoService();
            return s.listarAmortizaciones(idCliente);
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    @WebMethod(operationName = "guardarAmortizaciones")
    @WebResult(name = "exito")
    public boolean guardarAmortizaciones(
            @WebParam(name = "idCliente") String idCliente,
            @WebParam(name = "valorLocalidades") double valorLocalidades,
            @WebParam(name = "plazoMeses") int plazoMeses) {
        try {
            CreditoService s = new CreditoService();
            return s.guardarAmortizaciones(idCliente, valorLocalidades, plazoMeses);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
