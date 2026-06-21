package ec.edu.monster.cliweb.controller;

import ec.edu.monster.cliweb.soap.SoapHelper;
import ec.edu.monster.cliweb.soap.XmlParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PartidoController", urlPatterns = {"/partidos"})
public class PartidoController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                + "<soapenv:Header/><soapenv:Body><ws:listarTodosPartidos/></soapenv:Body></soapenv:Envelope>";
        
        String envEstadios = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                + "<soapenv:Header/><soapenv:Body><ws:listarEstadios/></soapenv:Body></soapenv:Envelope>";
                
        try {
            String res = SoapHelper.callSoap("WSCRUD", "", env);
            List<Map<String, String>> lista = XmlParser.parseList(res, "partido");
            request.setAttribute("partidos", lista);
            
            String resE = SoapHelper.callSoap("WSCRUD", "", envEstadios);
            List<Map<String, String>> estadios = XmlParser.parseList(resE, "estadio");
            request.setAttribute("estadios", estadios);
            
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }
        request.getRequestDispatcher("/views/partidos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String codigo = request.getParameter("codigo");
        
        try {
            if ("eliminar".equals(accion)) {
                String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/><soapenv:Body><ws:eliminarPartido><codigo>" + codigo + "</codigo></ws:eliminarPartido></soapenv:Body></soapenv:Envelope>";
                SoapHelper.callSoap("WSCRUD", "", env);
            } else if ("guardar".equals(accion)) {
                String equipoLocal = request.getParameter("equipoLocal");
                String equipoVisitante = request.getParameter("equipoVisitante");
                String fechaStr = request.getParameter("fecha");
                String lugar = request.getParameter("lugar");
                
                // Formatear fecha para SOAP (yyyy-MM-dd'T'HH:mm:ss)
                SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date date = sdfIn.parse(fechaStr);
                SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String soapFecha = sdfOut.format(date);

                String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/><soapenv:Body><ws:guardarPartido>"
                        + "<codigo>" + codigo + "</codigo>"
                        + "<equipoLocal>" + equipoLocal + "</equipoLocal>"
                        + "<equipoVisitante>" + equipoVisitante + "</equipoVisitante>"
                        + "<fecha>" + soapFecha + "</fecha>"
                        + "<lugar>" + lugar + "</lugar>"
                        + "</ws:guardarPartido></soapenv:Body></soapenv:Envelope>";
                SoapHelper.callSoap("WSCRUD", "", env);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        response.sendRedirect("partidos");
    }
}
