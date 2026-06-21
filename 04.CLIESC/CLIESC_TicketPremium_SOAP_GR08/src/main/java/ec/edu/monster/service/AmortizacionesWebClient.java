package ec.edu.monster.service;

import ec.edu.monster.model.CuotaAmortizacion;

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

public class AmortizacionesWebClient {
    private static final String DEFAULT_ENDPOINT = "http://localhost:8080/WS_EurekaBank_Server/WSCredito";
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";

    private static String getEndpoint() {
        try (InputStream in = AmortizacionesWebClient.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties properties = new Properties();
            if (in != null) {
                properties.load(in);
                String base = properties.getProperty("soap.endpoint");
                if (base != null && base.endsWith("WSFederacion")) {
                    return base.replace("WSFederacion", "WSCredito");
                }
            }
        } catch (Exception ex) { }
        return DEFAULT_ENDPOINT;
    }

    public static List<CuotaAmortizacion> traerAmortizaciones(long idFactura) {
        String body = "<ws:traerAmortizaciones><idFactura>" + idFactura + "</idFactura></ws:traerAmortizaciones>";
        String res = invokeSoap("traerAmortizaciones", body);
        List<CuotaAmortizacion> list = new ArrayList<>();
        try {
            Document doc = parseXml(res);
            NodeList nodes = doc.getElementsByTagNameNS("*", "return");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                CuotaAmortizacion c = new CuotaAmortizacion();
                c.setIdFactura(idFactura);
                String num = textOf(e, "numeroCuota");
                c.setNumeroCuota(num.isEmpty() ? 0 : Integer.parseInt(num));
                
                c.setSaldoInicial(parseDouble(textOf(e, "saldoInicial")));
                c.setCapital(parseDouble(textOf(e, "capital")));
                c.setInteres(parseDouble(textOf(e, "interes")));
                c.setCuota(parseDouble(textOf(e, "cuota")));
                c.setSaldoFinal(parseDouble(textOf(e, "saldoFinal")));
                
                String est = textOf(e, "estado");
                c.setEstado(est.isEmpty() ? 0 : Integer.parseInt(est));
                list.add(c);
            }
        } catch (Exception ignored) { }
        return list;
    }

    public static List<CuotaAmortizacion> listarAmortizaciones(String idCliente) {
        String body = "<ws:listarAmortizaciones><idCliente>" + idCliente + "</idCliente></ws:listarAmortizaciones>";
        String res = invokeSoap("listarAmortizaciones", body);
        List<CuotaAmortizacion> list = new ArrayList<>();
        try {
            Document doc = parseXml(res);
            NodeList nodes = doc.getElementsByTagNameNS("*", "amortizacion");
            if (nodes.getLength() == 0) nodes = doc.getElementsByTagNameNS("*", "return");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                CuotaAmortizacion c = new CuotaAmortizacion();
                String num = textOf(e, "numeroCuota");
                c.setNumeroCuota(num.isEmpty() ? 0 : Integer.parseInt(num));
                
                c.setSaldoInicial(parseDouble(textOf(e, "saldoInicial")));
                c.setCapital(parseDouble(textOf(e, "capital")));
                c.setInteres(parseDouble(textOf(e, "interes")));
                c.setCuota(parseDouble(textOf(e, "cuota")));
                c.setSaldoFinal(parseDouble(textOf(e, "saldoFinal")));
                
                String est = textOf(e, "estado");
                c.setEstado(est.isEmpty() ? 0 : Integer.parseInt(est));
                list.add(c);
            }
        } catch (Exception ignored) { }
        return list;
    }

    private static double parseDouble(String val) {
        return val.isEmpty() ? 0 : Double.parseDouble(val);
    }

    public static void guardarAmortizaciones(String idCliente, double total, int meses) throws Exception {
        String body = "<ws:guardarAmortizaciones>"
                + "<idCliente>" + idCliente + "</idCliente>"
                + "<valorLocalidades>" + total + "</valorLocalidades>"
                + "<plazoMeses>" + meses + "</plazoMeses>"
                + "</ws:guardarAmortizaciones>";
        invokeSoap("guardarAmortizaciones", body);
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
}
