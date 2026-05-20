package ec.edu.monster.service;

import ec.edu.monster.model.CompraResultado;
import ec.edu.monster.model.LocalidadPartido;
import ec.edu.monster.model.PartidoFutbol;
import ec.edu.monster.model.ResumenVentaLocalidad;
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
import java.util.Properties;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public final class TicketWebClient {
  private static final String DEFAULT_ENDPOINT = "http://localhost:8080/WS_EurekaBank_Server/WSFederacion";
  private static final String NAMESPACE = "http://ws.monster.edu.ec/";

  private TicketWebClient() {
  }

  private static String getEndpoint() {
    try (InputStream in = TicketWebClient.class.getClassLoader().getResourceAsStream("app.properties")) {
      Properties properties = new Properties();
      if (in != null) {
        properties.load(in);
        return properties.getProperty("soap.endpoint", DEFAULT_ENDPOINT);
      }
    } catch (Exception ex) {
      // usar valor por defecto
    }
    return DEFAULT_ENDPOINT;
  }

  public static List<PartidoFutbol> listarPartidosDisponibles() {
    String response = invokeSoap("listarPartidosDisponibles", "<ws:listarPartidosDisponibles/>");
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

  public static List<LocalidadPartido> listarLocalidadesDisponibles(String codigoPartido) {
    String body = "<ws:listarLocalidadesDisponibles><codigoPartido>" + escapeXml(codigoPartido) + "</codigoPartido></ws:listarLocalidadesDisponibles>";
    String response = invokeSoap("listarLocalidadesDisponibles", body);
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

  public static CompraResultado comprarBoleto(String codigoPartido, String codigoLocalidad, int cantidad, String cliente) {
    StringBuilder body = new StringBuilder();
    body.append("<ws:comprarBoleto>");
    body.append("<codigoPartido>").append(escapeXml(codigoPartido)).append("</codigoPartido>");
    body.append("<codigoLocalidad>").append(escapeXml(codigoLocalidad)).append("</codigoLocalidad>");
    body.append("<cantidad>").append(cantidad).append("</cantidad>");
    body.append("<cliente>").append(escapeXml(cliente)).append("</cliente>");
    body.append("</ws:comprarBoleto>");

    String response = invokeSoap("comprarBoleto", body.toString());
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

  public static List<ResumenVentaLocalidad> listarResumenVentas(String codigoPartido) {
    String body = "";
    if (codigoPartido != null && !codigoPartido.isEmpty()) {
      body = "<ws:listarResumenVentas><codigoPartido>" + escapeXml(codigoPartido) + "</codigoPartido></ws:listarResumenVentas>";
    } else {
      body = "<ws:listarResumenVentas/>";
    }
    String response = invokeSoap("listarResumenVentas", body);
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

  private static String invokeSoap(String operation, String bodyFragment) {
    HttpURLConnection connection = null;
    try {
      String envelope = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
          + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"" + NAMESPACE + "\">"
          + "<soapenv:Header/>"
          + "<soapenv:Body>" + bodyFragment + "</soapenv:Body>"
          + "</soapenv:Envelope>";

      URL url = URI.create(getEndpoint()).toURL();
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