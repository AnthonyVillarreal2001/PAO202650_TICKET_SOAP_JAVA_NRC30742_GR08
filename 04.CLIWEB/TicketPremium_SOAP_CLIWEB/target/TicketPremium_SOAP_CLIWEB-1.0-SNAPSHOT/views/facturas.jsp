<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mis Facturas - TicketPremium</title>
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
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/amortizaciones">Amortizaciones</a></li>
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/facturas">Facturas</a></li>
                    <li class="nav-item"><a class="nav-link text-danger" href="${pageContext.request.contextPath}/login">Salir</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container mt-5">
        <div class="card-glass">
            <h2 class="text-center text-info mb-4">Mis Facturas (Pagos Directos)</h2>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="facturas" method="get" class="row g-3 justify-content-center mb-5">
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
                    <c:when test="${not empty facturas}">
                        <div class="table-responsive">
                            <table class="table table-dark table-hover table-striped mt-3">
                                <thead>
                                    <tr>
                                        <th>No. Factura</th>
                                        <th>Cliente</th>
                                        <th>Código Partido</th>
                                        <th>Fecha Emisión</th>
                                        <th>Subtotal</th>
                                        <th>IVA (12%)</th>
                                        <th>Total Final</th>
                                        <th>Acción</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="f" items="${facturas}">
                                        <tr>
                                            <td>#${f.idFactura}</td>
                                            <td>${f.nombreCliente}</td>
                                            <td>${f.codigoPartido}</td>
                                            <td>${f.fechaEmision}</td>
                                            <td>$${f.subtotal}</td>
                                            <td>$${f.iva}</td>
                                            <td class="text-info fw-bold">$${f.total}</td>
                                            <td>
                                                <a href="ver_factura?id=${f.idFactura}" class="btn btn-primary btn-sm">Ver Factura</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning text-center">No se encontraron facturas para este cliente. (Quizás sus compras fueron mediante Crédito o no tiene compras).</div>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
    </div>
</body>
</html>
