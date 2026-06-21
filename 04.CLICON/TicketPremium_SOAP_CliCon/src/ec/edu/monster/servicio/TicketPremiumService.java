package ec.edu.monster.servicio;

import ec.edu.monster.modelo.CompraResultado;
import ec.edu.monster.modelo.LocalidadPartido;
import ec.edu.monster.modelo.PartidoFutbol;
import ec.edu.monster.modelo.ResumenVentaLocalidad;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class TicketPremiumService {

    private static final String ENDPOINT = "http://localhost:8080/WS_EurekaBank_Server/WSFederacion";
    private static final String ENDPOINT_CRUD = "http://localhost:8080/WS_EurekaBank_Server/WSCRUD";
    private static final String ENDPOINT_CREDITO = "http://localhost:8080/WS_EurekaBank_Server/WSCredito";
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";

    public List<PartidoFutbol> listarPartidosDisponibles() {
        String response = invokeSoap(ENDPOINT, "listarPartidosDisponibles", "<ws:listarPartidosDisponibles/>");
        List<PartidoFutbol> partidos = new ArrayList<>();
        try {
            Document document = parseXml(response);
            NodeList nodes = document.getElementsByTagNameNS("*", "partido");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    partidos.add(parsePartido((Element) node));
                }
            }
        } catch (Exception ex) {
            return partidos;
        }
        return partidos;
    }

    public List<LocalidadPartido> listarLocalidadesDisponibles(String codigoPartido) {
        String body = "<ws:listarLocalidadesDisponibles><codigoPartido>" + escapeXml(codigoPartido) + "</codigoPartido></ws:listarLocalidadesDisponibles>";
        String response = invokeSoap(ENDPOINT, "listarLocalidadesDisponibles", body);
        List<LocalidadPartido> localidades = new ArrayList<>();
        try {
            Document document = parseXml(response);
            NodeList nodes = document.getElementsByTagNameNS("*", "localidad");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    localidades.add(parseLocalidad((Element) node));
                }
            }
        } catch (Exception ex) {
            return localidades;
        }
        return localidades;
    }

    public CompraResultado comprarBoleto(String codigoPartido, String codigoLocalidad, int cantidad, String cliente) {
        StringBuilder body = new StringBuilder();
        body.append("<ws:comprarBoleto>");
        body.append("<codigoPartido>").append(escapeXml(codigoPartido)).append("</codigoPartido>");
        body.append("<codigoLocalidad>").append(escapeXml(codigoLocalidad)).append("</codigoLocalidad>");
        body.append("<cantidad>").append(cantidad).append("</cantidad>");
        body.append("<cliente>").append(escapeXml(cliente)).append("</cliente>");
        body.append("</ws:comprarBoleto>");

        String response = invokeSoap(ENDPOINT, "comprarBoleto", body.toString());
        CompraResultado resultado = new CompraResultado();
        try {
            Document document = parseXml(response);
            NodeList estados = document.getElementsByTagNameNS("*", "estado");
            NodeList mensajes = document.getElementsByTagNameNS("*", "mensaje");
            NodeList facturas = document.getElementsByTagNameNS("*", "facturaId");
            NodeList subtotales = document.getElementsByTagNameNS("*", "subtotal");
            NodeList ivas = document.getElementsByTagNameNS("*", "iva");
            NodeList totales = document.getElementsByTagNameNS("*", "total");

            resultado.setEstado(estados.getLength() > 0 ? Integer.parseInt(estados.item(0).getTextContent()) : -1);
            resultado.setMensaje(mensajes.getLength() > 0 ? mensajes.item(0).getTextContent() : "Sin respuesta");
            resultado.setFacturaId(facturas.getLength() > 0 ? Long.parseLong(facturas.item(0).getTextContent()) : 0L);
            resultado.setSubtotal(subtotales.getLength() > 0 ? Double.parseDouble(subtotales.item(0).getTextContent()) : 0d);
            resultado.setIva(ivas.getLength() > 0 ? Double.parseDouble(ivas.item(0).getTextContent()) : 0d);
            resultado.setTotal(totales.getLength() > 0 ? Double.parseDouble(totales.item(0).getTextContent()) : 0d);
        } catch (Exception ex) {
            resultado.setEstado(-1);
            resultado.setMensaje("No se pudo interpretar la respuesta SOAP.");
        }
        return resultado;
    }

    public List<ResumenVentaLocalidad> listarResumenVentas(String codigoPartido) {
        String body = "";
        if (codigoPartido != null && !codigoPartido.isEmpty()) {
            body = "<ws:listarResumenVentas><codigoPartido>" + escapeXml(codigoPartido) + "</codigoPartido></ws:listarResumenVentas>";
        } else {
            body = "<ws:listarResumenVentas/>";
        }
        String response = invokeSoap(ENDPOINT, "listarResumenVentas", body);
        List<ResumenVentaLocalidad> resumenes = new ArrayList<>();
        try {
            Document document = parseXml(response);
            NodeList nodes = document.getElementsByTagNameNS("*", "resumen");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    resumenes.add(parseResumen((Element) node));
                }
            }
        } catch (Exception ex) {
            return resumenes;
        }
        return resumenes;
    }

    public List<ec.edu.monster.modelo.AsientoOcupado> obtenerAsientosOcupados(String codigoPartido) {
        String body = "<ws:obtenerAsientosOcupados><codigoPartido>" + escapeXml(codigoPartido) + "</codigoPartido></ws:obtenerAsientosOcupados>";
        String response = invokeSoap(ENDPOINT, "obtenerAsientosOcupados", body);
        List<ec.edu.monster.modelo.AsientoOcupado> ocupados = new ArrayList<>();
        try {
            Document document = parseXml(response);
            NodeList nodes = document.getElementsByTagNameNS("*", "return");
            if (nodes.getLength() == 0) nodes = document.getElementsByTagNameNS("*", "asientoOcupado");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    ec.edu.monster.modelo.AsientoOcupado o = new ec.edu.monster.modelo.AsientoOcupado();
                    o.setLoc(textOf((Element) node, "codigoLocalidad"));
                    String cantStr = textOf((Element) node, "cantidad");
                    o.setCant(cantStr.isEmpty() ? 0 : Integer.parseInt(cantStr));
                    ocupados.add(o);
                }
            }
        } catch (Exception ex) { }
        return ocupados;
    }

    public List<ec.edu.monster.modelo.Cliente> listarClientes() {
        String body = "<ws:listarClientes/>";
        String response = invokeSoap(ENDPOINT_CRUD, "listarClientes", body);
        List<ec.edu.monster.modelo.Cliente> clientes = new ArrayList<>();
        try {
            Document document = parseXml(response);
            NodeList nodes = document.getElementsByTagNameNS("*", "return");
            if (nodes.getLength() == 0) nodes = document.getElementsByTagNameNS("*", "cliente");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    ec.edu.monster.modelo.Cliente c = new ec.edu.monster.modelo.Cliente();
                    c.setIdCliente(textOf((Element) node, "idCliente"));
                    String nom = textOf((Element) node, "nombres");
                    c.setNombres(nom);
                    c.setApellidos("");
                    boolean apto = nom.toLowerCase().contains("apto") || nom.toLowerCase().contains("apta");
                    c.setAptoCredito(apto);
                    clientes.add(c);
                }
            }
        } catch (Exception ex) { }
        return clientes;
    }

    public List<ec.edu.monster.modelo.Factura> listarFacturas(String idCliente) {
        String body = "<ws:listarFacturas><idCliente>" + escapeXml(idCliente) + "</idCliente></ws:listarFacturas>";
        String response = invokeSoap(ENDPOINT, "listarFacturas", body);
        List<ec.edu.monster.modelo.Factura> facturas = new ArrayList<>();
        try {
            Document document = parseXml(response);
            NodeList nodes = document.getElementsByTagNameNS("*", "return");
            if (nodes.getLength() == 0) nodes = document.getElementsByTagNameNS("*", "factura");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    ec.edu.monster.modelo.Factura f = new ec.edu.monster.modelo.Factura();
                    String idFStr = textOf((Element) node, "idFactura");
                    f.setIdFactura(idFStr.isEmpty() ? 0 : Long.parseLong(idFStr));
                    f.setIdCliente(textOf((Element) node, "idCliente"));
                    f.setFechaFactura(textOf((Element) node, "fechaFactura"));
                    String tot = textOf((Element) node, "totalFactura");
                    f.setTotalFactura(tot.isEmpty() ? 0 : Double.parseDouble(tot));
                    facturas.add(f);
                }
            }
        } catch (Exception ex) { }
        return facturas;
    }

    public List<ec.edu.monster.modelo.DetalleFactura> obtenerDetallesFactura(long idFactura) {
        String body = "<ws:obtenerDetallesFactura><idFactura>" + idFactura + "</idFactura></ws:obtenerDetallesFactura>";
        String response = invokeSoap(ENDPOINT, "obtenerDetallesFactura", body);
        List<ec.edu.monster.modelo.DetalleFactura> detalles = new ArrayList<>();
        try {
            Document document = parseXml(response);
            NodeList nodes = document.getElementsByTagNameNS("*", "return");
            if (nodes.getLength() == 0) nodes = document.getElementsByTagNameNS("*", "detalle");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    ec.edu.monster.modelo.DetalleFactura d = new ec.edu.monster.modelo.DetalleFactura();
                    String idStr = textOf((Element) node, "idDetalle");
                    d.setIdDetalle(idStr.isEmpty() ? 0 : Long.parseLong(idStr));
                    d.setCodigoPartido(textOf((Element) node, "codigoPartido"));
                    String idFStr = textOf((Element) node, "idFactura");
                    d.setIdFactura(idFStr.isEmpty() ? 0 : Long.parseLong(idFStr));
                    d.setCodigoLocalidad(textOf((Element) node, "codigoLocalidad"));
                    String cStr = textOf((Element) node, "cantidad");
                    d.setCantidad(cStr.isEmpty() ? 0 : Integer.parseInt(cStr));
                    String puStr = textOf((Element) node, "precioUnitario");
                    d.setPrecioUnitario(puStr.isEmpty() ? 0 : Double.parseDouble(puStr));
                    String totStr = textOf((Element) node, "totalDetalle");
                    d.setTotalDetalle(totStr.isEmpty() ? 0 : Double.parseDouble(totStr));
                    detalles.add(d);
                }
            }
        } catch (Exception ex) { }
        return detalles;
    }

    public List<ec.edu.monster.modelo.Amortizacion> obtenerAmortizaciones(String idCliente) {
        String body = "<ws:listarAmortizaciones><idCliente>" + escapeXml(idCliente) + "</idCliente></ws:listarAmortizaciones>";
        String response = invokeSoap(ENDPOINT_CREDITO, "listarAmortizaciones", body);
        List<ec.edu.monster.modelo.Amortizacion> amortizaciones = new ArrayList<>();
        try {
            Document document = parseXml(response);
            NodeList nodes = document.getElementsByTagNameNS("*", "return");
            if (nodes.getLength() == 0) nodes = document.getElementsByTagNameNS("*", "amortizacion");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    ec.edu.monster.modelo.Amortizacion a = new ec.edu.monster.modelo.Amortizacion();
                    String mStr = textOf((Element) node, "numeroCuota");
                    a.setMes(mStr.isEmpty() ? 0 : Integer.parseInt(mStr));
                    String cuStr = textOf((Element) node, "montoCuota");
                    a.setCuota(cuStr.isEmpty() ? 0 : Double.parseDouble(cuStr));
                    String iStr = textOf((Element) node, "interes");
                    a.setInteres(iStr.isEmpty() ? 0 : Double.parseDouble(iStr));
                    String amStr = textOf((Element) node, "capital");
                    a.setAmortizacion(amStr.isEmpty() ? 0 : Double.parseDouble(amStr));
                    String sStr = textOf((Element) node, "saldo");
                    a.setSaldo(sStr.isEmpty() ? 0 : Double.parseDouble(sStr));
                    amortizaciones.add(a);
                }
            }
        } catch (Exception ex) { }
        return amortizaciones;
    }

    public boolean guardarAmortizaciones(long idFactura, String idCliente, double valorLocalidades, int plazoMeses) {
        String body = "<ws:guardarAmortizaciones>"
                + "<idFactura>" + idFactura + "</idFactura>"
                + "<idCliente>" + escapeXml(idCliente) + "</idCliente>"
                + "<valorLocalidades>" + valorLocalidades + "</valorLocalidades>"
                + "<plazoMeses>" + plazoMeses + "</plazoMeses>"
                + "</ws:guardarAmortizaciones>";
        String response = invokeSoap(ENDPOINT_CREDITO, "guardarAmortizaciones", body);
        try {
            Document document = parseXml(response);
            NodeList nodes = document.getElementsByTagNameNS("*", "return");
            if (nodes.getLength() > 0) {
                String val = nodes.item(0).getTextContent();
                return val.equalsIgnoreCase("true");
            }
        } catch (Exception ex) { }
        return false;
    }

    private static String invokeSoap(String endpoint, String operation, String bodyFragment) {
        HttpURLConnection connection = null;
        try {
            String envelope = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"" + NAMESPACE + "\">"
                    + "<soapenv:Header/>"
                    + "<soapenv:Body>" + bodyFragment + "</soapenv:Body>"
                    + "</soapenv:Envelope>";

            URL url = URI.create(endpoint).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction", "\"" + operation + "\"");

            try (OutputStream out = connection.getOutputStream()) {
                out.write(envelope.getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();
            InputStream input = status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream();
            if (input == null) {
                return "";
            }

            try (InputStream in = input; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[2048];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                return out.toString(StandardCharsets.UTF_8);
            }
        } catch (Exception ex) {
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private static PartidoFutbol parsePartido(Element element) {
        PartidoFutbol partido = new PartidoFutbol();
        partido.setCodigo(textOf(element, "codigo"));
        partido.setEquipoLocal(textOf(element, "equipoLocal"));
        String visitante = textOf(element, "equipoVisitante");
        if (visitante.isEmpty()) {
            visitante = textOf(element, "equipoVistita");
        }
        partido.setEquipoVisitante(visitante);
        partido.setLugar(textOf(element, "lugar"));
        String fechaTexto = textOf(element, "fecha");
        try {
            if (!fechaTexto.isBlank()) {
                XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(fechaTexto);
                partido.setFecha(calendar.toGregorianCalendar().getTime());
            }
        } catch (Exception ex) {
            partido.setFecha(new Date());
        }
        return partido;
    }

    private static LocalidadPartido parseLocalidad(Element element) {
        LocalidadPartido localidad = new LocalidadPartido();
        localidad.setCodigoLocalidad(textOf(element, "codigoLocalidad"));
        localidad.setCodigoPartido(textOf(element, "codigoPartido"));
        String disponibilidad = textOf(element, "disponibilidad");
        String precio = textOf(element, "precio");
        localidad.setDisponibilidad(disponibilidad.isBlank() ? 0 : Integer.parseInt(disponibilidad));
        localidad.setPrecio(precio.isBlank() ? 0d : Double.parseDouble(precio));
        return localidad;
    }

    private static ResumenVentaLocalidad parseResumen(Element element) {
        ResumenVentaLocalidad resumen = new ResumenVentaLocalidad();
        resumen.setCodigoLocalidad(textOf(element, "codigoLocalidad"));
        String vendidos = textOf(element, "vendidos");
        resumen.setVendidos(vendidos.isBlank() ? 0 : Integer.parseInt(vendidos));
        String totalRecaudado = textOf(element, "totalRecaudado");
        resumen.setTotalRecaudado(totalRecaudado.isBlank() ? 0d : Double.parseDouble(totalRecaudado));
        return resumen;
    }

    private static String textOf(Element element, String name) {
        NodeList nodes = element.getElementsByTagNameNS("*", name);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return "";
    }

    private static String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
