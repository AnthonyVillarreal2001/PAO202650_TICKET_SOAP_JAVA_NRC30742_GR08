package ec.edu.monster.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        if (usuario != null && !usuario.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario.trim());
            response.sendRedirect(request.getContextPath() + "/menu");
        } else {
            request.setAttribute("error", "Ingresa un usuario y contraseña válidos para continuar.");
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
        }
    }
}
