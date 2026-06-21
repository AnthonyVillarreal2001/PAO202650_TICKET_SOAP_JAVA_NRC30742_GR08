package ec.edu.monster.cliweb.controller;

import ec.edu.monster.cliweb.soap.SoapHelper;
import ec.edu.monster.cliweb.soap.XmlParser;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "VerFacturaController", urlPatterns = {"/ver_factura"})
public class VerFacturaController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idFacturaStr = request.getParameter("id");
        if (idFacturaStr == null || idFacturaStr.isEmpty()) {
            response.sendRedirect("facturas");
            return;
        }

        try {
            // Get Factura
            String envFactura = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                    + "<soapenv:Header/><soapenv:Body><ws:obtenerFactura><idFactura>" + idFacturaStr + "</idFactura></ws:obtenerFactura></soapenv:Body></soapenv:Envelope>";
            String resFactura = SoapHelper.callSoap("WSFederacion", "", envFactura);
            List<Map<String, String>> facList = XmlParser.parseList(resFactura, "return");
            Map<String, String> factura = facList.isEmpty() ? null : facList.get(0);
            
            // Get Detalles
            String envDetalles = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                    + "<soapenv:Header/><soapenv:Body><ws:obtenerDetallesFactura><idFactura>" + idFacturaStr + "</idFactura></ws:obtenerDetallesFactura></soapenv:Body></soapenv:Envelope>";
            String resDetalles = SoapHelper.callSoap("WSFederacion", "", envDetalles);
            List<Map<String, String>> detalles = XmlParser.parseList(resDetalles, "detalle");
            
            request.setAttribute("factura", factura);
            request.setAttribute("detalles", detalles);
            
            request.getRequestDispatcher("/views/ver_factura.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Error consultando factura: " + e.getMessage());
            request.getRequestDispatcher("/views/facturas.jsp").forward(request, response);
        }
    }
}
