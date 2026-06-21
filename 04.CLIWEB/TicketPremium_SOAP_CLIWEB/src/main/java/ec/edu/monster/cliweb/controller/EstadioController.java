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

@WebServlet(name = "EstadioController", urlPatterns = {"/estadios"})
public class EstadioController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                + "<soapenv:Header/><soapenv:Body><ws:listarEstadios/></soapenv:Body></soapenv:Envelope>";
        try {
            String res = SoapHelper.callSoap("WSCRUD", "", env);
            List<Map<String, String>> lista = XmlParser.parseList(res, "estadio");
            request.setAttribute("estadios", lista);
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }
        request.getRequestDispatcher("/views/estadios.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String idEstadio = request.getParameter("idEstadio");
        
        try {
            if ("eliminar".equals(accion)) {
                String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/><soapenv:Body><ws:eliminarEstadio><idEstadio>" + idEstadio + "</idEstadio></ws:eliminarEstadio></soapenv:Body></soapenv:Envelope>";
                SoapHelper.callSoap("WSCRUD", "", env);
            } else if ("guardar".equals(accion)) {
                String nombre = request.getParameter("nombreEstadio");
                String ciudad = request.getParameter("ciudad");
                String capacidad = request.getParameter("capacidad");
                String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/><soapenv:Body><ws:guardarEstadio>"
                        + "<idEstadio>" + idEstadio + "</idEstadio>"
                        + "<nombreEstadio>" + nombre + "</nombreEstadio>"
                        + "<ciudad>" + ciudad + "</ciudad>"
                        + "<capacidad>" + capacidad + "</capacidad>"
                        + "</ws:guardarEstadio></soapenv:Body></soapenv:Envelope>";
                SoapHelper.callSoap("WSCRUD", "", env);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        response.sendRedirect("estadios");
    }
}
