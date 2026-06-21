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

@WebServlet(name = "ClienteController", urlPatterns = {"/clientes"})
public class ClienteController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                + "<soapenv:Header/><soapenv:Body><ws:listarClientes/></soapenv:Body></soapenv:Envelope>";
        try {
            String res = SoapHelper.callSoap("WSCRUD", "", env);
            List<Map<String, String>> lista = XmlParser.parseList(res, "cliente");
            request.setAttribute("clientes", lista);
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }
        request.getRequestDispatcher("/views/clientes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String idCliente = request.getParameter("idCliente");
        
        try {
            if ("eliminar".equals(accion)) {
                String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/><soapenv:Body><ws:eliminarCliente><idCliente>" + idCliente + "</idCliente></ws:eliminarCliente></soapenv:Body></soapenv:Envelope>";
                SoapHelper.callSoap("WSCRUD", "", env);
            } else if ("guardar".equals(accion)) {
                String nombres = request.getParameter("nombres");
                String correo = request.getParameter("correo");
                String telefono = request.getParameter("telefono");
                String edad = request.getParameter("edad");
                String genero = request.getParameter("genero");
                
                String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/><soapenv:Body><ws:guardarCliente>"
                        + "<idCliente>" + idCliente + "</idCliente>"
                        + "<nombres>" + nombres + "</nombres>"
                        + "<correo>" + correo + "</correo>"
                        + "<telefono>" + telefono + "</telefono>"
                        + "<edad>" + edad + "</edad>"
                        + "<genero>" + genero + "</genero>"
                        + "</ws:guardarCliente></soapenv:Body></soapenv:Envelope>";
                SoapHelper.callSoap("WSCRUD", "", env);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        response.sendRedirect("clientes");
    }
}
