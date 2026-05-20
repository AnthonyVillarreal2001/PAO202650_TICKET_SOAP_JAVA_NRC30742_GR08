<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    Object error = request.getAttribute("error");
    Object resultadoObj = request.getAttribute("resultado");
    Double subtotal = (Double) request.getAttribute("subtotal");
    Double iva = (Double) request.getAttribute("iva");
    Double total = (Double) request.getAttribute("total");
    String partidoDescripcion = (String) request.getAttribute("partidoDescripcion");
    String localidadDescripcion = (String) request.getAttribute("localidadDescripcion");
    Integer cantidad = (Integer) request.getAttribute("cantidad");
    Long facturaId = (Long) request.getAttribute("facturaId");
    boolean exito = error == null && resultadoObj != null;
%>
<!DOCTYPE html>
<html>
    <head>
        <title>TicketPremium - Compra</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/bank-theme.css">
    </head>
    <body>
        <div class="header">
            <h1>TicketPremium</h1>
            <a href="<%= request.getContextPath()%>/menu" class="btn-back">Volver al menú</a>
        </div>

        <div class="modal-overlay">
            <div class="modal">
                <div class="result-icon">
                    <% if (exito) { %>
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="12" cy="12" r="10" fill="#27ae60"/>
                        <path d="M8 12.5l2.5 2.5 5.5-5.5" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <% } else { %>
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="12" cy="12" r="10" fill="#c0392b"/>
                        <path d="M8 8l8 8M16 8l-8 8" stroke="white" stroke-width="2.5" stroke-linecap="round"/>
                    </svg>
                    <% } %>
                </div>

                <h2><%= exito ? "Compra realizada" : "Error en la compra" %></h2>

                <% if (exito) { %>
                <div class="purchase-summary">
                    <p><strong>Factura:</strong> <%= facturaId %></p>
                    <p><strong>Cliente:</strong> <%= usuario %></p>
                    <p><strong>Partido:</strong> <%= partidoDescripcion %></p>
                    <p><strong>Localidad:</strong> <%= localidadDescripcion %></p>
                    <p><strong>Cantidad:</strong> <%= cantidad %></p>
                    <p><strong>Subtotal:</strong> $<%= String.format("%.2f", subtotal != null ? subtotal : 0d) %></p>
                    <p><strong>IVA (12%):</strong> $<%= String.format("%.2f", iva != null ? iva : 0d) %></p>
                    <p><strong>Total:</strong> $<%= String.format("%.2f", total != null ? total : 0d) %></p>
                </div>
                <% } else { %>
                <div class="modal-message">
                    <%= error != null ? error : "No se pudo completar la operación" %>
                </div>
                <% } %>

                <div style="margin-top:18px; display:flex; gap:12px; justify-content:center; flex-wrap:wrap;">
                    <a class="btn" href="<%= request.getContextPath()%>/menu">Ir al menú</a>
                    <a class="btn btn-ghost" href="<%= request.getContextPath()%>/reporte">Ver reporte</a>
                </div>
            </div>
        </div>

        <div class="footer">TicketPremium | Factura final con IVA incluido</div>
    </body>
</html>
