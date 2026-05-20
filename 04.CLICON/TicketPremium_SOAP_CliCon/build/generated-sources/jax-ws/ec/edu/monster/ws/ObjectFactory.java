
package ec.edu.monster.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.edu.monster.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Movimiento_QNAME = new QName("http://ws.monster.edu.ec/", "movimiento");
    private final static QName _RegDeposito_QNAME = new QName("http://ws.monster.edu.ec/", "regDeposito");
    private final static QName _RegDepositoResponse_QNAME = new QName("http://ws.monster.edu.ec/", "regDepositoResponse");
    private final static QName _RegRetiro_QNAME = new QName("http://ws.monster.edu.ec/", "regRetiro");
    private final static QName _RegRetiroResponse_QNAME = new QName("http://ws.monster.edu.ec/", "regRetiroResponse");
    private final static QName _RegTransferencia_QNAME = new QName("http://ws.monster.edu.ec/", "regTransferencia");
    private final static QName _RegTransferenciaResponse_QNAME = new QName("http://ws.monster.edu.ec/", "regTransferenciaResponse");
    private final static QName _TraerMovimientos_QNAME = new QName("http://ws.monster.edu.ec/", "traerMovimientos");
    private final static QName _TraerMovimientosResponse_QNAME = new QName("http://ws.monster.edu.ec/", "traerMovimientosResponse");
    private final static QName _ValidarIngreso_QNAME = new QName("http://ws.monster.edu.ec/", "validarIngreso");
    private final static QName _ValidarIngresoResponse_QNAME = new QName("http://ws.monster.edu.ec/", "validarIngresoResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.edu.monster.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Movimiento }
     * 
     */
    public Movimiento createMovimiento() {
        return new Movimiento();
    }

    /**
     * Create an instance of {@link RegDeposito }
     * 
     */
    public RegDeposito createRegDeposito() {
        return new RegDeposito();
    }

    /**
     * Create an instance of {@link RegDepositoResponse }
     * 
     */
    public RegDepositoResponse createRegDepositoResponse() {
        return new RegDepositoResponse();
    }

    /**
     * Create an instance of {@link RegRetiro }
     * 
     */
    public RegRetiro createRegRetiro() {
        return new RegRetiro();
    }

    /**
     * Create an instance of {@link RegRetiroResponse }
     * 
     */
    public RegRetiroResponse createRegRetiroResponse() {
        return new RegRetiroResponse();
    }

    /**
     * Create an instance of {@link RegTransferencia }
     * 
     */
    public RegTransferencia createRegTransferencia() {
        return new RegTransferencia();
    }

    /**
     * Create an instance of {@link RegTransferenciaResponse }
     * 
     */
    public RegTransferenciaResponse createRegTransferenciaResponse() {
        return new RegTransferenciaResponse();
    }

    /**
     * Create an instance of {@link TraerMovimientos }
     * 
     */
    public TraerMovimientos createTraerMovimientos() {
        return new TraerMovimientos();
    }

    /**
     * Create an instance of {@link TraerMovimientosResponse }
     * 
     */
    public TraerMovimientosResponse createTraerMovimientosResponse() {
        return new TraerMovimientosResponse();
    }

    /**
     * Create an instance of {@link ValidarIngreso }
     * 
     */
    public ValidarIngreso createValidarIngreso() {
        return new ValidarIngreso();
    }

    /**
     * Create an instance of {@link ValidarIngresoResponse }
     * 
     */
    public ValidarIngresoResponse createValidarIngresoResponse() {
        return new ValidarIngresoResponse();
    }

    /**
     * Create an instance of {@link OperacionCuentaResponse }
     * 
     */
    public OperacionCuentaResponse createOperacionCuentaResponse() {
        return new OperacionCuentaResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Movimiento }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Movimiento }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "movimiento")
    public JAXBElement<Movimiento> createMovimiento(Movimiento value) {
        return new JAXBElement<Movimiento>(_Movimiento_QNAME, Movimiento.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegDeposito }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegDeposito }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regDeposito")
    public JAXBElement<RegDeposito> createRegDeposito(RegDeposito value) {
        return new JAXBElement<RegDeposito>(_RegDeposito_QNAME, RegDeposito.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegDepositoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegDepositoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regDepositoResponse")
    public JAXBElement<RegDepositoResponse> createRegDepositoResponse(RegDepositoResponse value) {
        return new JAXBElement<RegDepositoResponse>(_RegDepositoResponse_QNAME, RegDepositoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegRetiro }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegRetiro }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regRetiro")
    public JAXBElement<RegRetiro> createRegRetiro(RegRetiro value) {
        return new JAXBElement<RegRetiro>(_RegRetiro_QNAME, RegRetiro.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegRetiroResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegRetiroResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regRetiroResponse")
    public JAXBElement<RegRetiroResponse> createRegRetiroResponse(RegRetiroResponse value) {
        return new JAXBElement<RegRetiroResponse>(_RegRetiroResponse_QNAME, RegRetiroResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegTransferencia }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegTransferencia }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regTransferencia")
    public JAXBElement<RegTransferencia> createRegTransferencia(RegTransferencia value) {
        return new JAXBElement<RegTransferencia>(_RegTransferencia_QNAME, RegTransferencia.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegTransferenciaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegTransferenciaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regTransferenciaResponse")
    public JAXBElement<RegTransferenciaResponse> createRegTransferenciaResponse(RegTransferenciaResponse value) {
        return new JAXBElement<RegTransferenciaResponse>(_RegTransferenciaResponse_QNAME, RegTransferenciaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerMovimientos }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerMovimientos }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerMovimientos")
    public JAXBElement<TraerMovimientos> createTraerMovimientos(TraerMovimientos value) {
        return new JAXBElement<TraerMovimientos>(_TraerMovimientos_QNAME, TraerMovimientos.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerMovimientosResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerMovimientosResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerMovimientosResponse")
    public JAXBElement<TraerMovimientosResponse> createTraerMovimientosResponse(TraerMovimientosResponse value) {
        return new JAXBElement<TraerMovimientosResponse>(_TraerMovimientosResponse_QNAME, TraerMovimientosResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidarIngreso }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ValidarIngreso }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "validarIngreso")
    public JAXBElement<ValidarIngreso> createValidarIngreso(ValidarIngreso value) {
        return new JAXBElement<ValidarIngreso>(_ValidarIngreso_QNAME, ValidarIngreso.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidarIngresoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ValidarIngresoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "validarIngresoResponse")
    public JAXBElement<ValidarIngresoResponse> createValidarIngresoResponse(ValidarIngresoResponse value) {
        return new JAXBElement<ValidarIngresoResponse>(_ValidarIngresoResponse_QNAME, ValidarIngresoResponse.class, null, value);
    }

}
