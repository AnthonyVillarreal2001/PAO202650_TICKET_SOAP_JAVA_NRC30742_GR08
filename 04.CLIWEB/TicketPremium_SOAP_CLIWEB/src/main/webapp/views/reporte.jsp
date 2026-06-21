<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reporte de Ventas - TicketPremium</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body { background-color: #121212; color: #fff; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .navbar { background-color: #1e1e1e !important; box-shadow: 0 4px 6px rgba(0,0,0,0.3); }
        .card-glass {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: linear-gradient(135deg, #1f4037 0%, #99f2c8 100%);
            border-radius: 10px;
            padding: 20px;
            text-align: center;
            color: #fff;
            box-shadow: 0 4px 10px rgba(0,0,0,0.2);
        }
        .stat-card h3 { font-size: 2.5rem; margin: 0; }
        .stat-card p { margin: 0; font-size: 1.1rem; opacity: 0.9; }
        .table { color: #fff; }
        .table-dark th { background-color: #333; color: #0dcaf0; border-color: #444; }
        .table-dark td { border-color: #444; }
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
                    <li class="nav-item"><a class="nav-link" href="amortizaciones">Amortizaciones</a></li>
                    <li class="nav-item"><a class="nav-link" href="facturas">Facturas</a></li>
                    <li class="nav-item"><a class="nav-link active" href="reporte">Reporte Ventas</a></li>
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
        <h2 class="text-center text-info mb-4">Dashboard: Reporte de Ventas</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- Filtros -->
        <div class="card-glass mb-4">
            <form action="reporte" method="get" class="row g-3 justify-content-center">
                <div class="col-md-4">
                    <label class="form-label text-info">Fecha (Opcional)</label>
                    <input type="date" name="fecha" class="form-control" value="${filtroFecha}">
                </div>
                <div class="col-md-4">
                    <label class="form-label text-info">Vendedor (Opcional)</label>
                    <input type="text" name="vendedor" class="form-control" placeholder="Ej: admin, vendedor1" value="${filtroVendedor}">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-info w-100">Aplicar Filtros</button>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <a href="reporte" class="btn btn-secondary w-100">Limpiar</a>
                </div>
            </form>
        </div>

        <!-- KPIs -->
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="stat-card" style="background: linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%);">
                    <h3>$${String.format("%.2f", totalRecaudado)}</h3>
                    <p>Total Recaudado (con Filtros)</p>
                </div>
            </div>
            <div class="col-md-6">
                <div class="stat-card" style="background: linear-gradient(135deg, #141e30 0%, #243b55 100%);">
                    <h3>${totalTransacciones}</h3>
                    <p>Transacciones (Facturas) Realizadas</p>
                </div>
            </div>
        </div>

        <!-- Gráficos -->
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card-glass text-center">
                    <h5 class="text-info">Ventas por Día (Gráfico)</h5>
                    <canvas id="ventasChart" style="max-height: 300px; width: 100%;"></canvas>
                </div>
            </div>
        </div>

        <!-- Tabla -->
        <div class="card-glass">
            <h5 class="text-info mb-3">Detalle de Facturas</h5>
            <div class="table-responsive">
                <table class="table table-dark table-hover table-striped">
                    <thead>
                        <tr>
                            <th>No. Factura</th>
                            <th>Fecha</th>
                            <th>Cliente</th>
                            <th>Vendedor</th>
                            <th>Partido</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="f" items="${facturas}">
                            <tr>
                                <td>#${f.idFactura}</td>
                                <td>${f.fechaEmision}</td>
                                <td>${f.nombreCliente}</td>
                                <td>${empty f.vendedor ? 'SISTEMA' : f.vendedor}</td>
                                <td>${f.codigoPartido}</td>
                                <td class="text-info fw-bold">$${f.total}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty facturas}">
                            <tr><td colspan="6" class="text-center text-warning">No se encontraron ventas para los filtros aplicados.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Generación del Chart.js -->
    <script>
        // Extraer datos de la tabla dinámicamente para el gráfico
        // Agrupar ventas por fecha (ignorando la hora)
        let dataMap = {};
        <c:forEach var="f" items="${facturas}">
            var d = "${f.fechaEmision}".split(' ')[0]; // Solo fecha YYYY-MM-DD
            if(!dataMap[d]) dataMap[d] = 0;
            dataMap[d] += parseFloat("${f.total}");
        </c:forEach>

        let labels = Object.keys(dataMap).sort();
        let values = labels.map(l => dataMap[l]);

        const ctx = document.getElementById('ventasChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels.length > 0 ? labels : ['Sin Datos'],
                datasets: [{
                    label: 'Recaudación Diaria ($)',
                    data: values.length > 0 ? values : [0],
                    backgroundColor: 'rgba(13, 202, 240, 0.7)',
                    borderColor: 'rgba(13, 202, 240, 1)',
                    borderWidth: 1,
                    borderRadius: 5
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: { beginAtZero: true, ticks: { color: '#fff' } },
                    x: { ticks: { color: '#fff' } }
                },
                plugins: {
                    legend: { labels: { color: '#fff' } }
                }
            }
        });
    </script>
</body>
</html>
