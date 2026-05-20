package ec.edu.monster.controller;

import ec.edu.monster.modelo.PartidoFutbol;
import ec.edu.monster.modelo.ResumenVentaLocalidad;
import ec.edu.monster.modelo.VentaRegistro;
import ec.edu.monster.service.TicketPremiumWebClient;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ReporteController", urlPatterns = {"/reporte"})
public class ReporteController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String codigoPartido = request.getParameter("codigoPartido");
        List<PartidoFutbol> partidos = TicketPremiumWebClient.listarPartidosDisponibles();
        PartidoFutbol partidoSeleccionado = null;
        for (PartidoFutbol partido : partidos) {
            if (partido.getCodigo() != null && partido.getCodigo().equals(codigoPartido)) {
                partidoSeleccionado = partido;
                break;
            }
        }

        List<ResumenVentaLocalidad> resumenList = new ArrayList<>();
        try {
            resumenList = TicketPremiumWebClient.listarResumenVentas(codigoPartido != null ? codigoPartido : "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        request.setAttribute("partido", partidoSeleccionado);
        request.setAttribute("resumen", resumenList);
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reporte.jsp");
        rd.forward(request, response);
    }
}
