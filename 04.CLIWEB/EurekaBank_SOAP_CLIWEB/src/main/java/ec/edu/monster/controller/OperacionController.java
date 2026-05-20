package ec.edu.monster.controller;

import ec.edu.monster.modelo.CompraResultado;
import ec.edu.monster.modelo.LocalidadPartido;
import ec.edu.monster.modelo.PartidoFutbol;
import ec.edu.monster.modelo.VentaRegistro;
import ec.edu.monster.service.TicketPremiumWebClient;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "OperacionController", urlPatterns = {"/compra"})
public class OperacionController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String codigoPartido = request.getParameter("codigoPartido");
        String codigoLocalidad = request.getParameter("codigoLocalidad");
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        String cliente = request.getParameter("cliente");
        String partidoDescripcion = request.getParameter("partidoDescripcion");
        String localidadDescripcion = request.getParameter("localidadDescripcion");
        double precioUnitario = Double.parseDouble(request.getParameter("precioUnitario"));

        CompraResultado resultado = TicketPremiumWebClient.comprarBoleto(codigoPartido, codigoLocalidad, cantidad, cliente);

        try {
            if (resultado.getEstado() == 1) {
                double subtotal = precioUnitario * cantidad;
                double iva = subtotal * 0.12;
                double total = subtotal + iva;

                request.setAttribute("resultado", resultado);
                request.setAttribute("partidoDescripcion", partidoDescripcion);
                request.setAttribute("localidadDescripcion", localidadDescripcion);
                request.setAttribute("cantidad", cantidad);
                request.setAttribute("subtotal", subtotal);
                request.setAttribute("iva", iva);
                request.setAttribute("total", total);
                request.setAttribute("cliente", cliente);
                request.setAttribute("facturaId", resultado.getFacturaId());
            } else {
                request.setAttribute("error", resultado.getMensaje() != null ? resultado.getMensaje() : "No se pudo completar la compra.");
            }
        } catch (Exception ex) {
            request.setAttribute("error", "Error en la compra: " + ex.getMessage());
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/compra.jsp");
        rd.forward(request, response);
    }
}
