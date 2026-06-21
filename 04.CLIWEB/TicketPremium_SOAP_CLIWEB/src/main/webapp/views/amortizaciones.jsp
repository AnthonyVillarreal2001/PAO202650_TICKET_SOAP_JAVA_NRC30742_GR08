<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Tabla de Amortización - TicketPremium</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #121212; color: #fff; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .navbar { background-color: #1e1e1e !important; box-shadow: 0 4px 6px rgba(0,0,0,0.3); }
        .card-glass {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            padding: 30px;
            margin-top: 30px;
        }
        .table { color: #fff; }
        .table-dark th { background-color: #333; color: #0dcaf0; border-color: #444; }
        .table-dark td { border-color: #444; }
        .btn-info { background-color: #0dcaf0; border: none; color: #000; font-weight: bold; }
        .btn-info:hover { background-color: #0bacce; color: #fff; }
        .text-info { color: #0dcaf0 !important; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand text-info fw-bold" href="${pageContext.request.contextPath}/index.jsp">TicketPremium</a>
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">Inicio</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/comprar">Comprar</a></li>
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/amortizaciones">Amortizaciones</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/facturas">Facturas</a></li>
                    <li class="nav-item"><a class="nav-link text-danger" href="${pageContext.request.contextPath}/login">Salir</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container mt-5">
        <div class="card-glass">
            <h2 class="text-center text-info mb-4">Tabla de Amortización (Crédito)</h2>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="amortizaciones" method="get" class="row g-3 justify-content-center mb-5">
                <div class="col-md-6">
                    <label class="form-label text-info">Seleccione Cliente</label>
                    <select name="cliente" class="form-select" required>
                        <option value="">-- Seleccione Cliente --</option>
                        <c:forEach var="c" items="${clientes}">
                            <option value="${c.idCliente}" ${c.idCliente == selectedCliente ? 'selected' : ''}>
                                ${c.nombre} ${c.apellido} (${c.idCliente})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-info w-100">Consultar</button>
                </div>
            </form>

            <c:if test="${not empty selectedCliente}">
                <c:choose>
                    <c:when test="${not empty amortizaciones}">
                        <c:set var="plazo" value="0"/>
                        <c:set var="valorPrestamo" value="0"/>
                        <c:set var="cuotaMensual" value="0"/>
                        <c:set var="nombre" value=""/>
                        
                        <c:forEach var="a" items="${amortizaciones}" varStatus="status">
                            <c:set var="plazo" value="${status.count}"/>
                            <c:set var="cuotaMensual" value="${a.montoCuota}"/>
                            <c:if test="${status.first}">
                                <c:set var="valorPrestamo" value="${a.capital + a.saldo}"/>
                                <c:set var="nombre" value="${a.nombreCliente}"/>
                            </c:if>
                        </c:forEach>
                        
                        <h4 class="text-warning mt-2 mb-4">Cliente: ${nombre}</h4>

                        <div class="table-responsive">
                            <table class="table table-bordered" style="width: auto; background-color: #222;">
                                <tr><th style="background-color: #333;">Valor Préstamo</th><td>$<c:out value="${valorPrestamo}"/></td></tr>
                                <tr><th style="background-color: #333;">Cuotas</th><td>${plazo}</td></tr>
                                <tr><th style="background-color: #333;">Tasa Interés Anual</th><td>16.50%</td></tr>
                                <tr><th style="background-color: #333;">Cuota</th><td class="text-danger fw-bold">($${cuotaMensual})</td></tr>
                            </table>

                            <table class="table table-dark table-hover table-striped mt-4 text-center">
                                <thead>
                                    <tr>
                                        <th># Cuota</th>
                                        <th>Valor Cuota</th>
                                        <th>Interés Pagado</th>
                                        <th>Capital Pagado</th>
                                        <th>Saldo</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>0</td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td>$<c:out value="${valorPrestamo}"/></td>
                                    </tr>
                                    <c:forEach var="a" items="${amortizaciones}">
                                        <tr>
                                            <td>${a.numeroCuota}</td>
                                            <td>$${a.montoCuota}</td>
                                            <td>$${a.interes}</td>
                                            <td>$${a.capital}</td>
                                            <td>$${a.saldo}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning text-center">No se encontraron cuotas de amortización para este cliente. (Quizás sus compras fueron en efectivo).</div>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
    </div>
</body>
</html>
