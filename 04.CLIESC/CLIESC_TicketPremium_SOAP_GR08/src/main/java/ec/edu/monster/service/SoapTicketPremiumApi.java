package ec.edu.monster.service;

import ec.edu.monster.config.AppConfig;
import ec.edu.monster.model.Movimiento;
import ec.edu.monster.model.OperacionCuentaResponse;
import ec.edu.monster.util.Debug;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.Service;
import javax.xml.namespace.QName;
import java.net.URL;
import java.util.List;

public class SoapTicketPremiumApi implements TicketPremiumApi {

  @WebService(targetNamespace = AppConfig.SERVICE_NS, name = "WSTicketPremium")
  public interface WSTicketPremiumPort {
    @WebMethod(operationName = "validarIngreso")
    @WebResult(name = "return")
    String validarIngreso(@WebParam(name = "usuario") String usuario,
                          @WebParam(name = "password") String password);

    @WebMethod(operationName = "traerMovimientos")
    @WebResult(name = "movimiento")
    List<Movimiento> traerMovimientos(@WebParam(name = "cuenta") String cuenta);

    @WebMethod(operationName = "regDeposito")
    @WebResult(name = "resultado")
    OperacionCuentaResponse regDeposito(@WebParam(name = "cuenta") String cuenta,
                                        @WebParam(name = "importe") double importe);

    @WebMethod(operationName = "regRetiro")
    @WebResult(name = "resultado")
    OperacionCuentaResponse regRetiro(@WebParam(name = "cuenta") String cuenta,
                                      @WebParam(name = "importe") double importe);

    @WebMethod(operationName = "regTransferencia")
    @WebResult(name = "resultado")
    OperacionCuentaResponse regTransferencia(@WebParam(name = "cuentaOrigen") String cuentaOrigen,
                                             @WebParam(name = "cuentaDestino") String cuentaDestino,
                                             @WebParam(name = "importe") double importe);
  }

  private WSTicketPremiumPort port() throws Exception {
    Debug.log("WSDL_URL=" + AppConfig.WSDL_URL);
    Debug.log("SERVICE_NS=" + AppConfig.SERVICE_NS + "  SERVICE_NAME=" + AppConfig.SERVICE_NAME);
    URL wsdl = new URL(AppConfig.WSDL_URL);
    QName qname = new QName(AppConfig.SERVICE_NS, AppConfig.SERVICE_NAME);
    Service svc = Service.create(wsdl, qname);
    WSTicketPremiumPort p = svc.getPort(WSTicketPremiumPort.class);
    Debug.log("Port creado: " + (p != null));
    return p;
  }

  @Override
  public boolean validarIngreso(String usuario, String password) throws Exception {
    Debug.log("LLAMADA validarIngreso(" + usuario + ", ****)");
    String r = port().validarIngreso(usuario, password);
    Debug.log("RESP validarIngreso=" + r);
    return "Exitoso".equalsIgnoreCase(r);
  }

  @Override
  public List<Movimiento> traerMovimientos(String cuenta) throws Exception {
    Debug.log("LLAMADA traerMovimientos(" + cuenta + ")");
    List<Movimiento> r = port().traerMovimientos(cuenta);
    Debug.log("RESP traerMovimientos size=" + (r == null ? "null" : r.size()));
    return r;
  }

  @Override
  public OperacionCuentaResponse regDeposito(String cuenta, double importe) throws Exception {
    Debug.log("LLAMADA regDeposito(cta=" + cuenta + ", imp=" + importe + ")");
    OperacionCuentaResponse r = port().regDeposito(cuenta, importe);
    Debug.log("RESP regDeposito: " + resp(r));
    return r;
  }

  @Override
  public OperacionCuentaResponse regRetiro(String cuenta, double importe) throws Exception {
    Debug.log("LLAMADA regRetiro(cta=" + cuenta + ", imp=" + importe + ")");
    OperacionCuentaResponse r = port().regRetiro(cuenta, importe);
    Debug.log("RESP regRetiro: " + resp(r));
    return r;
  }

  @Override
  public OperacionCuentaResponse regTransferencia(String o, String d, double importe) throws Exception {
    Debug.log("LLAMADA regTransferencia(origen=" + o + ", destino=" + d + ", imp=" + importe + ")");
    OperacionCuentaResponse r = port().regTransferencia(o, d, importe);
    Debug.log("RESP regTransferencia: " + resp(r));
    return r;
  }

  private String resp(OperacionCuentaResponse r){
    if (r == null) return "null";
    return "estado=" + r.getEstado() + ", saldo=" + r.getSaldo();
  }
}
