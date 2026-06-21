<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>TicketPremium - Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #1e1e2f, #2a2a40);
            color: #fff;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-card {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(15px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 20px;
            padding: 40px;
            width: 100%;
            max-width: 400px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
        }
        .form-control {
            background: rgba(255,255,255,0.1);
            color: #fff;
            border: 1px solid rgba(255,255,255,0.2);
            border-radius: 10px;
        }
        .form-control:focus {
            background: rgba(255,255,255,0.2);
            color: #fff;
            border-color: #00d2ff;
            box-shadow: none;
        }
        .btn-gradient {
            background: linear-gradient(90deg, #00d2ff 0%, #3a7bd5 100%);
            border: none;
            color: white;
            font-weight: bold;
            border-radius: 10px;
            padding: 10px;
            transition: opacity 0.3s ease;
        }
        .btn-gradient:hover {
            opacity: 0.9;
            color: white;
        }
        .text-gradient {
            background: linear-gradient(90deg, #00d2ff 0%, #3a7bd5 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
    </style>
</head>
<body>
    <div class="login-card text-center">
        <h2 class="fw-bold mb-4">Ingresar a <br><span class="text-gradient">TicketPremium</span></h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger" style="background: rgba(220,53,69,0.2); border: 1px solid #dc3545; color: #ff6b6b;">
                ${error}
            </div>
        </c:if>

        <form action="login" method="post">
            <div class="mb-4 text-start">
                <label class="form-label text-white-50">Usuario (Administrador)</label>
                <input type="text" name="usuario" class="form-control form-control-lg" required placeholder="MONSTER">
            </div>
            <div class="mb-4 text-start">
                <label class="form-label text-white-50">Contraseña</label>
                <input type="password" name="password" class="form-control form-control-lg" required placeholder="••••••••">
            </div>
            <button type="submit" class="btn btn-gradient w-100 btn-lg">Iniciar Sesión Segura</button>
        </form>
    </div>
</body>
</html>
