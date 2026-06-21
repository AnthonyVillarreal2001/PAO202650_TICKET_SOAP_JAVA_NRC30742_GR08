package ec.edu.monster.service;

import ec.edu.monster.model.Cliente;
import ec.edu.monster.model.Estadio;
import ec.edu.monster.model.Pais;
import ec.edu.monster.model.PartidoFutbol;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CrudWebClient {
    private static final String DEFAULT_ENDPOINT = "http://localhost:8080/WS_EurekaBank_Server/WSCRUD";
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";

    private static String getEndpoint() {
        try (InputStream in = CrudWebClient.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties properties = new Properties();
            if (in != null) {
                properties.load(in);
                String base = properties.getProperty("soap.endpoint");
                if (base != null && base.endsWith("WSFederacion")) {
                    return base.replace("WSFederacion", "WSCRUD");
                }
            }
        } catch (Exception ex) { }
        return DEFAULT_ENDPOINT;
    }

    public static List<Pais> listarPaises() {
        String res = invokeSoap("listarPaises", "<ws:listarPaises/>");
        List<Pais> list = new ArrayList<>();
        try {
            Document doc = parseXml(res);
            NodeList nodes = doc.getElementsByTagNameNS("*", "pais");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                list.add(new Pais(textOf(e, "idPais"), textOf(e, "nombrePais")));
            }
        } catch (Exception ignored) { }
        return list;
    }

    public static boolean guardarPais(Pais pais) {
        String body = "<ws:guardarPais><idPais>" + escapeXml(pais.getIdPais()) + "</idPais><nombrePais>" + escapeXml(pais.getNombrePais()) + "</nombrePais></ws:guardarPais>";
        String res = invokeSoap("guardarPais", body);
        return res.contains("<return>true</return>");
    }

    public static boolean eliminarPais(String idPais) {
        String body = "<ws:eliminarPais><idPais>" + escapeXml(idPais) + "</idPais></ws:eliminarPais>";
        String res = invokeSoap("eliminarPais", body);
        return res.contains("<return>true</return>");
    }

    public static List<Estadio> listarEstadios() {
        String res = invokeSoap("listarEstadios", "<ws:listarEstadios/>");
        List<Estadio> list = new ArrayList<>();
        try {
            Document doc = parseXml(res);
            NodeList nodes = doc.getElementsByTagNameNS("*", "estadio");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                String capStr = textOf(e, "capacidad");
                int cap = capStr.isEmpty() ? 0 : Integer.parseInt(capStr);
                list.add(new Estadio(textOf(e, "idEstadio"), textOf(e, "nombreEstadio"), textOf(e, "ciudad"), cap));
            }
        } catch (Exception ignored) { }
        return list;
    }

    public static boolean guardarEstadio(Estadio estadio) {
        String body = "<ws:guardarEstadio>" +
                "<idEstadio>" + escapeXml(estadio.getIdEstadio()) + "</idEstadio>" +
                "<nombreEstadio>" + escapeXml(estadio.getNombreEstadio()) + "</nombreEstadio>" +
                "<ciudad>" + escapeXml(estadio.getCiudad()) + "</ciudad>" +
                "<capacidad>" + estadio.getCapacidad() + "</capacidad>" +
                "</ws:guardarEstadio>";
        String res = invokeSoap("guardarEstadio", body);
        return res.contains("<return>true</return>");
    }

    public static boolean eliminarEstadio(String idEstadio) {
        String body = "<ws:eliminarEstadio><idEstadio>" + escapeXml(idEstadio) + "</idEstadio></ws:eliminarEstadio>";
        String res = invokeSoap("eliminarEstadio", body);
        return res.contains("<return>true</return>");
    }

    public static List<Cliente> listarClientes() {
        String res = invokeSoap("listarClientes", "<ws:listarClientes/>");
        List<Cliente> list = new ArrayList<>();
        try {
            Document doc = parseXml(res);
            NodeList nodes = doc.getElementsByTagNameNS("*", "cliente");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                String edadStr = textOf(e, "edad");
                int edad = edadStr.isEmpty() ? 0 : Integer.parseInt(edadStr);
                list.add(new Cliente(
                        textOf(e, "idCliente"), textOf(e, "nombres"), textOf(e, "correo"),
                        textOf(e, "telefono"), edad, textOf(e, "genero")));
            }
        } catch (Exception ignored) { }
        return list;
    }

    public static boolean guardarCliente(Cliente cliente) {
        String body = "<ws:guardarCliente>" +
                "<idCliente>" + escapeXml(cliente.getIdCliente()) + "</idCliente>" +
                "<nombres>" + escapeXml(cliente.getNombres()) + "</nombres>" +
                "<correo>" + escapeXml(cliente.getCorreo()) + "</correo>" +
                "<telefono>" + escapeXml(cliente.getTelefono()) + "</telefono>" +
                "<edad>" + cliente.getEdad() + "</edad>" +
                "<genero>" + escapeXml(cliente.getGenero()) + "</genero>" +
                "</ws:guardarCliente>";
        String res = invokeSoap("guardarCliente", body);
        return res.contains("<return>true</return>");
    }

    public static boolean eliminarCliente(String idCliente) {
        String body = "<ws:eliminarCliente><idCliente>" + escapeXml(idCliente) + "</idCliente></ws:eliminarCliente>";
        String res = invokeSoap("eliminarCliente", body);
        return res.contains("<return>true</return>");
    }

    public static List<PartidoFutbol> listarTodosPartidos() {
        String res = invokeSoap("listarTodosPartidos", "<ws:listarTodosPartidos/>");
        List<PartidoFutbol> list = new ArrayList<>();
        try {
            Document doc = parseXml(res);
            NodeList nodes = doc.getElementsByTagNameNS("*", "partido");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                PartidoFutbol p = new PartidoFutbol();
                p.setCodigo(textOf(e, "codigo"));
                p.setEquipoLocal(textOf(e, "equipoLocal"));
                p.setEquipoVisitante(textOf(e, "equipoVisitante"));
                p.setLugar(textOf(e, "lugar"));
                // Date parsing is complex, for CRUD we mostly just show the raw or parse basic
                list.add(p);
            }
        } catch (Exception ignored) { }
        return list;
    }

    public static boolean eliminarPartido(String codigo) {
        String body = "<ws:eliminarPartido><codigo>" + escapeXml(codigo) + "</codigo></ws:eliminarPartido>";
        String res = invokeSoap("eliminarPartido", body);
        return res.contains("<return>true</return>");
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
            if (input == null) return "";

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
            if (connection != null) connection.disconnect();
        }
    }

    private static Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private static String textOf(Element element, String name) {
        NodeList nodes = element.getElementsByTagNameNS("*", name);
        if (nodes.getLength() > 0) return nodes.item(0).getTextContent().trim();
        return "";
    }

    private static String escapeXml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}
