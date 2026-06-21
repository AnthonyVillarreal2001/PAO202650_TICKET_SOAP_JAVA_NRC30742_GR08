<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>TicketPremium - Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #1e1e2f, #2a2a40);
            color: #fff;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
        }
        .navbar {
            background: rgba(0, 0, 0, 0.5) !important;
            backdrop-filter: blur(10px);
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        .card-glass {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(15px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            transition: transform 0.3s ease;
            height: 100%;
        }
        .card-glass:hover {
            transform: translateY(-5px);
            background: rgba(255, 255, 255, 0.1);
        }
        .text-gradient {
            background: linear-gradient(90deg, #00d2ff 0%, #3a7bd5 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark mb-4">
        <div class="container">
            <a class="navbar-brand fw-bold text-gradient" href="index.jsp">TicketPremium</a>
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item"><a class="nav-link" href="admin">Admin CRUD</a></li>
                    <li class="nav-item"><a class="nav-link" href="comprar">Comprar Boletos</a></li>
                    <li class="nav-item"><a class="nav-link" href="amortizaciones">Amortizaciones</a></li>
                    <li class="nav-item"><a class="nav-link" href="facturas">Facturas</a></li>
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

    <div class="container mt-5 text-center">
        <h1 class="display-4 fw-bold mb-4">Bienvenido a <span class="text-gradient">TicketPremium</span></h1>
        <p class="lead text-white-50 mb-5">El ecosistema definitivo para la venta oficial de boletos del Mundial 2026.</p>

        <div class="row g-4 justify-content-center">
            <div class="col-md-3">
                <a href="admin" class="text-decoration-none">
                    <div class="card card-glass text-center p-4">
                        <h4 class="text-white">Admin CRUD</h4>
                        <p class="text-white-50 m-0">Gestiona Países, Estadios, etc.</p>
                    </div>
                </a>
            </div>
            <div class="col-md-3">
                <a href="comprar" class="text-decoration-none">
                    <div class="card card-glass text-center p-4">
                        <h4 class="text-white">Vender Boletos</h4>
                        <p class="text-white-50 m-0">Procesa compras con SOAP.</p>
                    </div>
                </a>
            </div>
            <div class="col-md-3">
                <a href="reporte" class="text-decoration-none">
                    <div class="card card-glass text-center p-4">
                        <h4 class="text-white">Reporte Ventas</h4>
                        <p class="text-white-50 m-0">Ver resumen financiero.</p>
                    </div>
                </a>
            </div>
            <div class="col-md-3">
                <a href="masup" class="text-decoration-none">
                    <div class="card card-glass text-center p-4">
                        <h4 class="text-white">Estadio MASUP</h4>
                        <p class="text-white-50 m-0">Mapa interactivo de asientos.</p>
                    </div>
                </a>
            </div>
            <div class="col-md-3">
                <a href="amortizaciones" class="text-decoration-none">
                    <div class="card card-glass text-center p-4">
                        <h4 class="text-white">Amortizaciones</h4>
                        <p class="text-white-50 m-0">Cuotas de crédito.</p>
                    </div>
                </a>
            </div>
            <div class="col-md-3">
                <a href="facturas" class="text-decoration-none">
                    <div class="card card-glass text-center p-4">
                        <h4 class="text-white">Facturas</h4>
                        <p class="text-white-50 m-0">Compras en efectivo.</p>
                    </div>
                </a>
            </div>
        </div>
    </div>
</body>
</html>