<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="ec.edu.monster.modelo.LocalidadPartido"%>
<%@page import="ec.edu.monster.modelo.PartidoFutbol"%>
<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    PartidoFutbol partido = (PartidoFutbol) request.getAttribute("partido");
    String fechaTexto = (String) request.getAttribute("fechaTexto");
    List<LocalidadPartido> localidades = (List<LocalidadPartido>) request.getAttribute("localidades");
%>
<!DOCTYPE html>
<html>
    <head>
        <title>TicketPremium - Localidades</title>
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
                    <h2><%= partido != null ? partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante() : "Localidades disponibles" %></h2>
                    <p><%= fechaTexto != null ? fechaTexto : "" %></p>
                </div>

                <div class="table-responsive">
                    <table class="movimientos">
                        <thead>
                            <tr>
                                <th>Localidad</th>
                                <th>Disponibilidad</th>
                                <th>Precio</th>
                                <th>Comprar</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (localidades == null || localidades.isEmpty()) {
                        %>
                            <tr>
                                <td colspan="4">No hay localidades disponibles para este partido.</td>
                            </tr>
                        <%
                            } else {
                                for (LocalidadPartido localidad : localidades) {
                        %>
                            <tr>
                                <td><strong><%= localidad.getCodigoLocalidad()%></strong></td>
                                <td><%= localidad.getDisponibilidad()%></td>
                                <td>$<%= String.format("%.2f", localidad.getPrecio())%></td>
                                <td>
                                    <form action="<%= request.getContextPath()%>/compra" method="post" class="purchase-form">
                                        <input type="hidden" name="codigoPartido" value="<%= partido != null ? partido.getCodigo() : ""%>" />
                                        <input type="hidden" name="partidoDescripcion" value="<%= partido != null ? partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante() : ""%>" />
                                        <input type="hidden" name="codigoLocalidad" value="<%= localidad.getCodigoLocalidad()%>" />
                                        <input type="hidden" name="localidadDescripcion" value="<%= localidad.getCodigoLocalidad()%>" />
                                        <input type="hidden" name="precioUnitario" value="<%= localidad.getPrecio()%>" />
                                        <input type="hidden" name="cliente" value="<%= usuario%>" />
                                        <input type="number" min="1" max="<%= localidad.getDisponibilidad()%>" name="cantidad" value="1" required />
                                        <button type="submit" class="btn">Comprar</button>
                                    </form>
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

        <div class="footer">TicketPremium | Compra de boletos</div>
    </body>
</html>
