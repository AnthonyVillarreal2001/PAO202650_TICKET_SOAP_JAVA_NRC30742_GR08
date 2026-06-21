package ec.edu.monster.cliweb.controller;

import ec.edu.monster.cliweb.soap.SoapHelper;
import ec.edu.monster.cliweb.soap.XmlParser;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("password");

        // Request a WSTicketPremium.validarIngreso
        String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                + "<soapenv:Header/><soapenv:Body><ws:validarIngreso>"
                + "<usuario>" + usuario + "</usuario>"
                + "<password>" + clave + "</password>"
                + "</ws:validarIngreso></soapenv:Body></soapenv:Envelope>";

        try {
            String res = SoapHelper.callSoap("WSTicketPremium", "", envelope);
            // El resultado de validarIngreso (boolean) viene envuelto en <return>
            String authResult = XmlParser.parseSingleValue(res, "return");

            if ("Exitoso".equals(authResult)) {
                // Crear Sesión
                HttpSession session = request.getSession();
                session.setAttribute("usuarioLogueado", usuario);
                response.sendRedirect("index.jsp");
            } else {
                request.setAttribute("error", "Credenciales incorrectas o usuario no activo.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error interno comunicándose con el servidor SOAP: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
