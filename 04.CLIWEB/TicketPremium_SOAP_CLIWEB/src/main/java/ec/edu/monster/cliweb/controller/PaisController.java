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

@WebServlet(name = "PaisController", urlPatterns = {"/paises"})
public class PaisController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        // Listar
        String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                + "<soapenv:Header/><soapenv:Body><ws:listarPaises/></soapenv:Body></soapenv:Envelope>";
        try {
            String res = SoapHelper.callSoap("WSCRUD", "", envelope);
            List<Map<String, String>> paises = XmlParser.parseList(res, "pais");
            request.setAttribute("paises", paises);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        request.getRequestDispatcher("/views/paises.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String id = request.getParameter("idPais");

        try {
            if ("eliminar".equals(accion)) {
                String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/><soapenv:Body><ws:eliminarPais><idPais>" + id + "</idPais></ws:eliminarPais></soapenv:Body></soapenv:Envelope>";
                SoapHelper.callSoap("WSCRUD", "", envelope);
            } else if ("guardar".equals(accion)) {
                String nombre = request.getParameter("nombrePais");
                String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/><soapenv:Body><ws:guardarPais>"
                        + "<idPais>" + id + "</idPais>"
                        + "<nombrePais>" + nombre + "</nombrePais>"
                        + "</ws:guardarPais></soapenv:Body></soapenv:Envelope>";
                SoapHelper.callSoap("WSCRUD", "", envelope);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect("paises");
    }
}
