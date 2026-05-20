package ec.edu.monster.controller;

import ec.edu.monster.modelo.LocalidadPartido;
import ec.edu.monster.modelo.PartidoFutbol;
import ec.edu.monster.service.TicketPremiumWebClient;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LocalidadesController", urlPatterns = {"/localidades"})
public class MovimientosController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String codigoPartido = request.getParameter("codigoPartido");
        String equipoLocal = request.getParameter("equipoLocal");
        String equipoVisitante = request.getParameter("equipoVisitante");
        String fecha = request.getParameter("fecha");
        String lugar = request.getParameter("lugar");

        List<LocalidadPartido> localidades = TicketPremiumWebClient.listarLocalidadesDisponibles(codigoPartido);

        PartidoFutbol partido = new PartidoFutbol();
        partido.setCodigo(codigoPartido);
        partido.setEquipoLocal(equipoLocal);
        partido.setEquipoVisitante(equipoVisitante);
        partido.setLugar(lugar);
        request.setAttribute("partido", partido);
        request.setAttribute("fechaTexto", fecha);
        request.setAttribute("localidades", localidades);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/localidades.jsp");
        rd.forward(request, response);
    }
}
