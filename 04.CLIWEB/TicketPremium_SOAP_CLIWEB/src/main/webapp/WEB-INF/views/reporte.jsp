<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="ec.edu.monster.modelo.PartidoFutbol"%>
<%@page import="ec.edu.monster.modelo.ResumenVentaLocalidad"%>
<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    PartidoFutbol partido = (PartidoFutbol) request.getAttribute("partido");
    List<ResumenVentaLocalidad> resumen = (List<ResumenVentaLocalidad>) request.getAttribute("resumen");
%>
<!DOCTYPE html>
<html>
    <head>
        <title>TicketPremium - Reporte</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/bank-theme.css">
    </head>
    <body>
        <div class="header">
            <h1>TicketPremium</h1>
            <a href="<%= request.getContextPath()%>/menu" class="btn-back">Volver al menú</a>
        </div>

        <div class="main">
            <div class="panel">
                <div class="panel-head">
                    <h2>Resumen de ventas</h2>
                    <p><%= partido != null ? partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante() : "Todos los partidos" %></p>
                </div>

                <div class="table-responsive">
                    <table class="movimientos">
                        <thead>
                            <tr>
                                <th>Localidad</th>
                                <th>Vendidos</th>
                                <th>Total recaudado</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (resumen == null || resumen.isEmpty()) {
                        %>
                            <tr>
                                <td colspan="3">No hay ventas registradas para mostrar.</td>
                            </tr>
                        <%
                            } else {
                                for (ResumenVentaLocalidad item : resumen) {
                        %>
                            <tr>
                                <td><strong><%= item.getCodigoLocalidad()%></strong></td>
                                <td><%= item.getVendidos()%></td>
                                <td>$<%= String.format("%.2f", item.getTotalRecaudado())%></td>
                            </tr>
                        <%
                                }
                            }
                        %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="footer">TicketPremium | Resumen de ventas del partido</div>
    </body>
</html>
