package ec.edu.monster.controller;

import ec.edu.monster.modelo.PartidoFutbol;
import ec.edu.monster.service.TicketPremiumWebClient;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MenuController", urlPatterns = {"/menu"})
public class MenuController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        List<PartidoFutbol> partidos = TicketPremiumWebClient.listarPartidosDisponibles();
        request.setAttribute("partidos", partidos);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/menu.jsp");
        rd.forward(request, response);
    }
}
