<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>TicketPremium</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/bank-theme.css">
    </head>
    <body>
        <div class="header">
            <div class="header-left">
                <h1>TicketPremium</h1>
            </div>
        </div>

        <div class="login">
            <div class="login-icon">
                <span style="font-size:64px;line-height:1;">🎟️</span>
            </div>
            <h2>Ingreso al sistema</h2>
            <p style="margin-bottom:18px;color:#566;">Acceso local para comprar boletos y consultar partidos.</p>
            <form action="login" method="post">
                <div class="form-group">
                    <input type="text" name="usuario" placeholder="Usuario" required />
                </div>
                <div class="form-group">
                    <input type="password" name="password" placeholder="Contraseña" required />
                </div>
                <button type="submit" class="btn">Ingresar</button>
            </form>
            <%
                String error = (String) request.getAttribute("error");
                if (error != null) {
            %>
            <div class="error">
                <span><%= error%></span>
            </div>
            <% }%>
        </div>
        <div class="footer">TicketPremium | Integración SOAP con la Federación de Fútbol</div>
</body>
</html>