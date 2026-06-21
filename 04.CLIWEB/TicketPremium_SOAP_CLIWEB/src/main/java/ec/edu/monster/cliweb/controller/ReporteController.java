package ec.edu.monster.cliweb.controller;

import ec.edu.monster.cliweb.soap.SoapHelper;
import ec.edu.monster.cliweb.soap.XmlParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ReporteController", urlPatterns = {"/reporte"})
public class ReporteController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String fecha = request.getParameter("fecha");
        String vendedor = request.getParameter("vendedor");

        if (fecha == null) fecha = "";
        if (vendedor == null) vendedor = "";

        // Llamar a WSFederacion.listarTodasLasFacturas
        String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                + "<soapenv:Header/><soapenv:Body><ws:listarTodasLasFacturas>"
                + "<fecha>" + fecha + "</fecha>"
                + "<vendedor>" + vendedor + "</vendedor>"
                + "</ws:listarTodasLasFacturas></soapenv:Body></soapenv:Envelope>";

        try {
            String res = SoapHelper.callSoap("WSFederacion", "", envelope);
            List<Map<String, String>> facturas = XmlParser.parseList(res, "factura");
            
            // Analítica básica
            double totalRecaudado = 0;
            for(Map<String, String> f : facturas) {
                totalRecaudado += Double.parseDouble(f.getOrDefault("total", "0"));
            }

            request.setAttribute("facturas", facturas);
            request.setAttribute("totalRecaudado", totalRecaudado);
            request.setAttribute("totalTransacciones", facturas.size());
            request.setAttribute("filtroFecha", fecha);
            request.setAttribute("filtroVendedor", vendedor);

        } catch (Exception e) {
            request.setAttribute("error", "Error cargando reporte: " + e.getMessage());
            request.setAttribute("facturas", new ArrayList<>());
        }

        request.getRequestDispatcher("/views/reporte.jsp").forward(request, response);
    }
}
