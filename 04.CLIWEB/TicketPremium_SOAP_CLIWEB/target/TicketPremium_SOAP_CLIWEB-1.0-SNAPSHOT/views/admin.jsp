<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Administración - TicketPremium</title>
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
            text-align: center;
            transition: transform 0.3s;
        }
        .card-glass:hover { transform: translateY(-5px); background: rgba(255,255,255,0.1); }
        .card-icon { font-size: 3rem; margin-bottom: 15px; }
        .text-gradient { background: linear-gradient(90deg, #00d2ff 0%, #3a7bd5 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark mb-4">
        <div class="container">
            <a class="navbar-brand fw-bold text-gradient" href="index.jsp">TicketPremium</a>
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item"><a class="nav-link active" href="admin">Admin CRUD</a></li>
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

    <div class="container mt-5">
        <h2 class="text-center text-info mb-5">Panel de Administración (CRUD)</h2>
        <div class="row g-4 justify-content-center">
            
            <div class="col-md-3">
                <a href="paises" class="text-decoration-none text-white">
                    <div class="card-glass">
                        <div class="card-icon">🌍</div>
                        <h4>Países</h4>
                        <p class="text-white-50 text-sm">Gestionar países</p>
                    </div>
                </a>
            </div>

            <div class="col-md-3">
                <a href="estadios" class="text-decoration-none text-white">
                    <div class="card-glass">
                        <div class="card-icon">🏟️</div>
                        <h4>Estadios</h4>
                        <p class="text-white-50 text-sm">Gestionar estadios</p>
                    </div>
                </a>
            </div>

            <div class="col-md-3">
                <a href="clientes" class="text-decoration-none text-white">
                    <div class="card-glass">
                        <div class="card-icon">👥</div>
                        <h4>Clientes</h4>
                        <p class="text-white-50 text-sm">Gestionar clientes</p>
                    </div>
                </a>
            </div>

            <div class="col-md-3">
                <a href="partidos" class="text-decoration-none text-white">
                    <div class="card-glass">
                        <div class="card-icon">⚽</div>
                        <h4>Partidos</h4>
                        <p class="text-white-50 text-sm">Gestionar partidos</p>
                    </div>
                </a>
            </div>

        </div>
    </div>
</body>
</html>
