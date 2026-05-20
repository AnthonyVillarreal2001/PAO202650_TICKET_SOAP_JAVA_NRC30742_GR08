/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/WebService.java to edit this template
 */
package ec.edu.monster.ws;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.OperacionCuentaResponse;
import ec.edu.monster.servicio.TicketPremiumService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JOIS
 */
@WebService(serviceName = "WSTicketPremium")
public class WSTicketPremium {

    //LOGIN
    @WebMethod(operationName = "validarIngreso")
    public String validarIngreso(@WebParam(name = "usuario") String usuario, @WebParam(name = "password") String password) {
        TicketPremiumService usuarioServicio = new TicketPremiumService();
        boolean exitoso = usuarioServicio.validarIngreso(usuario, password);
        if (exitoso) {
            return "Exitoso";
        }
        return "Denegado";
    }

    /**
     * Web service operation
     *
     * @param cuenta
     * @return Retorna la lista de movimientos de una cuenta
     */
    @WebMethod(operationName = "traerMovimientos")
    @WebResult(name = "movimiento")
    public List<Movimiento> traerMovimientos(@WebParam(name = "cuenta") String cuenta) {

        List<Movimiento> lista;

        //proceso
        try {
            //recuperar moviemintos
            TicketPremiumService service = new TicketPremiumService();
            lista = service.leerMovimientos(cuenta);
        } catch (Exception e) {
            //en caso de error, retorne una lista vacia
            lista = new ArrayList<>();
        }

        //retorno
        return lista;
    }

    /**
     * Web service operation
     *
     * @param cuenta
     * @param importe
     * @return Estado, 1 o -1
     */
    @WebMethod(operationName = "regDeposito")
    @WebResult(name = "resultado")
    public OperacionCuentaResponse regDeposito(
            @WebParam(name = "cuenta") String cuenta,
            @WebParam(name = "importe") double importe) {

        String codEmp = "0001";
        OperacionCuentaResponse resp = new OperacionCuentaResponse();

        try {
            TicketPremiumService service = new TicketPremiumService();
            double saldo = service.registrarDeposito(cuenta, importe, codEmp);
            resp.setEstado(1);
            resp.setSaldo(saldo);
        } catch (Exception e) {
            resp.setEstado(-1);
            resp.setSaldo(-1); // o 0, como tú quieras
        }
        return resp;
    }

    @WebMethod(operationName = "regRetiro")
    @WebResult(name = "resultado")
    public OperacionCuentaResponse regRetiro(
            @WebParam(name = "cuenta") String cuenta,
            @WebParam(name = "importe") double importe) {

        String codEmp = "0004";
        OperacionCuentaResponse resp = new OperacionCuentaResponse();

        try {
            TicketPremiumService service = new TicketPremiumService();
            double saldo = service.registrarRetiro(cuenta, importe, codEmp);
            resp.setEstado(1);
            resp.setSaldo(saldo);
            System.out.println("Retiro exitoso: cuenta=" + cuenta + ", importe=" + importe);
        } catch (Exception e) {
            resp.setEstado(-1);
            resp.setSaldo(-1);
            System.err.println("Error al realizar el retiro: " + e.getMessage());
        }
        return resp;
    }

    @WebMethod(operationName = "regTransferencia")
    @WebResult(name = "resultado")
    public OperacionCuentaResponse regTransferencia(
            @WebParam(name = "cuentaOrigen") String cuentaOrigen,
            @WebParam(name = "cuentaDestino") String cuentaDestino,
            @WebParam(name = "importe") double importe) {

        String codEmp = "0004";
        OperacionCuentaResponse resp = new OperacionCuentaResponse();

        try {
            TicketPremiumService service = new TicketPremiumService();
            double saldoOrigen = service.registrarTransferencia(cuentaOrigen, cuentaDestino, importe, codEmp);
            resp.setEstado(1);
            resp.setSaldo(saldoOrigen);  // saldo de la cuenta origen
        } catch (Exception e) {
            resp.setEstado(-1);
            resp.setSaldo(-1);
        }
        return resp;
    }

}
