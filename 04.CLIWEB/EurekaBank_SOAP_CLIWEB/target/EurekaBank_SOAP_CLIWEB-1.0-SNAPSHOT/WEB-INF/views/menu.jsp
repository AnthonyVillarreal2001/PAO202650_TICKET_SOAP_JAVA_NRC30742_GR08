<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="ec.edu.monster.modelo.PartidoFutbol"%>
<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    List<PartidoFutbol> partidos = (List<PartidoFutbol>) request.getAttribute("partidos");
%>
<!DOCTYPE html>
<html>
    <head>
        <title>TicketPremium - Partidos disponibles</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/bank-theme.css">
    </head>
    <body>
        <div class="header">
            <div class="header-left">
                <h1>TicketPremium</h1>
            </div>

            <div class="header-right">
                <div class="user-chip">
                    <span><%= usuario%></span>
                </div>
                <a href="<%= request.getContextPath()%>/index.jsp" class="logout-btn">
                    Salir
                </a>
            </div>
        </div>

        <div class="main">
            <div class="panel">
                <div class="panel-head">
                    <h2>Partidos disponibles</h2>
                    <p>Selecciona un partido para ver sus localidades o generar un reporte.</p>
                </div>

                <div class="table-responsive">
                    <table class="movimientos">
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Partido</th>
                                <th>Fecha</th>
                                <th>Lugar</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (partidos == null || partidos.isEmpty()) {
                        %>
                            <tr>
                                <td colspan="5">No hay partidos disponibles en este momento.</td>
                            </tr>
                        <%
                            } else {
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MMMM-yyyy HH:mm");
                                for (PartidoFutbol partido : partidos) {
                                    String fechaTexto = partido.getFecha() != null ? sdf.format(partido.getFecha()) : "";
                        %>
                            <tr>
                                <td><strong><%= partido.getCodigo()%></strong></td>
                                <td><%= partido.getEquipoLocal()%> vs <%= partido.getEquipoVisitante()%></td>
                                <td><%= fechaTexto%></td>
                                <td><%= partido.getLugar()%></td>
                                <td>
                                    <a class="btn btn-inline" href="<%= request.getContextPath()%>/localidades?codigoPartido=<%= partido.getCodigo()%>&equipoLocal=<%= java.net.URLEncoder.encode(partido.getEquipoLocal(), java.nio.charset.StandardCharsets.UTF_8)%>&equipoVisitante=<%= java.net.URLEncoder.encode(partido.getEquipoVisitante(), java.nio.charset.StandardCharsets.UTF_8)%>&fecha=<%= java.net.URLEncoder.encode(fechaTexto, java.nio.charset.StandardCharsets.UTF_8)%>&lugar=<%= java.net.URLEncoder.encode(partido.getLugar(), java.nio.charset.StandardCharsets.UTF_8)%>">Ver localidades</a>
                                    <a class="btn btn-inline btn-ghost" href="<%= request.getContextPath()%>/reporte?codigoPartido=<%= partido.getCodigo()%>">Reporte</a>
                                </td>
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

        <div class="footer">TicketPremium | Compra de boletos, facturación y control de ventas</div>
</body>
</html>