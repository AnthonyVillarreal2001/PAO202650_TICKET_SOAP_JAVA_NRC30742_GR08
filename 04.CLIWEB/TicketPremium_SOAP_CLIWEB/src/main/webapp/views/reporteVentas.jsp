<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reporte Ventas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #1e1e2f; color: #fff; }
        .navbar { background: rgba(0,0,0,0.5); backdrop-filter: blur(10px); }
        .card-glass { background: rgba(255,255,255,0.05); backdrop-filter: blur(15px); border-radius: 15px; padding: 20px; }
        .table { color: #fff; }
        .table-dark { background-color: transparent; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark mb-4">
        <div class="container">
            <a class="navbar-brand fw-bold" href="index.jsp" style="color:#00d2ff;">TicketPremium</a>
            <div class="collapse navbar-collapse d-flex">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item"><a class="nav-link" href="paises">Países</a></li>
                    <li class="nav-item"><a class="nav-link" href="comprar">Comprar Boletos</a></li>
                    <li class="nav-item"><a class="nav-link" href="reporte">Reporte Ventas</a></li>
                    <li class="nav-item"><a class="nav-link" href="masup">MASUP Estadio</a></li>
                </ul>
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <span class="nav-link text-white-50">👤 Bienvenido, <b>${sessionScope.usuarioLogueado}</b></span>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-outline-danger btn-sm mt-1 ms-3" href="logout">Cerrar Sesión</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <div class="container">
        <h2 class="mb-4">Resumen de Ventas por Partido (SOAP)</h2>

        <div class="card-glass mb-4">
            <form action="reporte" method="get">
                <label>Seleccione un Partido:</label>
                <select name="partido" class="form-select bg-dark text-white border-secondary w-50 d-inline-block mx-2" onchange="this.form.submit()">
                    <option value="">-- Todos o Seleccione --</option>
                    <c:forEach var="p" items="${partidos}">
                        <option value="${p.codigo}" ${selectedPartido == p.codigo ? 'selected' : ''}>
                            ${p.codigo} - ${p.equipoLocal} vs ${p.equipoVisitante}
                        </option>
                    </c:forEach>
                </select>
            </form>
        </div>

        <c:if test="${not empty reporte}">
            <div class="card-glass">
                <table class="table table-dark table-hover text-center">
                    <thead>
                        <tr class="table-active">
                            <th>Localidad</th>
                            <th>Boletos Vendidos</th>
                            <th>Total Recaudado (USD)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:set var="totalBol" value="0" />
                        <c:set var="totalDin" value="0" />
                        <c:forEach var="r" items="${reporte}">
                            <tr>
                                <td><span class="badge bg-primary fs-6">${r.codigoLocalidad}</span></td>
                                <td>${r.vendidos}</td>
                                <td class="text-success fw-bold">$${r.totalRecaudado}</td>
                            </tr>
                            <c:set var="totalBol" value="${totalBol + r.vendidos}" />
                            <c:set var="totalDin" value="${totalDin + r.totalRecaudado}" />
                        </c:forEach>
                        <tr class="fw-bold fs-5 border-top">
                            <td>TOTAL GENERAL</td>
                            <td>${totalBol}</td>
                            <td class="text-warning">$${totalDin}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</body>
</html>
