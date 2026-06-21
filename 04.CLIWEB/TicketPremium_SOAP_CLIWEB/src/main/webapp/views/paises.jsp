<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Admin - Países</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #1e1e2f; color: #fff; }
        .navbar { background: rgba(0,0,0,0.5); backdrop-filter: blur(10px); }
        .card-glass { background: rgba(255,255,255,0.05); backdrop-filter: blur(15px); border-radius: 15px; padding: 20px; }
        .table { color: #fff; }
        .table-dark { background-color: transparent; }
    </style>
    <script>
        function editar(id, nombre) {
            document.getElementById('idPais').value = id;
            document.getElementById('idPais').readOnly = true;
            document.getElementById('nombrePais').value = nombre;
        }
        function limpiar() {
            document.getElementById('formPais').reset();
            document.getElementById('idPais').readOnly = false;
        }
    </script>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark mb-4">
        <div class="container">
            <a class="navbar-brand fw-bold" href="index.jsp" style="color:#00d2ff;">TicketPremium</a>
            <div class="collapse navbar-collapse d-flex">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item"><a class="nav-link active" href="admin">Admin CRUD</a></li>
                </ul>
            </div>
        </div>
    </nav>
    <div class="container">
        <h2 class="text-info mb-4">Gestión de Países</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="card-glass">
            <form id="formPais" action="paises" method="post" class="row g-3">
                <input type="hidden" name="accion" value="guardar">
                <div class="col-md-4">
                    <label>ID País</label>
                    <input type="text" name="idPais" id="idPais" class="form-control" required>
                </div>
                <div class="col-md-4">
                    <label>Nombre</label>
                    <input type="text" name="nombrePais" id="nombrePais" class="form-control" required>
                </div>
                <div class="col-md-4 text-end mt-4 pt-2">
                    <button type="button" class="btn btn-secondary" onclick="limpiar()">Limpiar</button>
                    <button type="submit" class="btn btn-info">Guardar</button>
                </div>
            </form>
        </div>

        <div class="card-glass">
            <table class="table table-dark table-hover">
                <thead>
                    <tr><th>ID</th><th>Nombre</th><th>Acciones</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${paises}">
                        <tr>
                            <td>${p.idPais}</td>
                            <td>${p.nombrePais}</td>
                            <td>
                                <button class="btn btn-sm btn-primary" onclick="editar('${p.idPais}', '${p.nombrePais}')">Editar</button>
                                <form action="paises" method="post" style="display:inline;">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="idPais" value="${p.idPais}">
                                    <button class="btn btn-sm btn-danger" onclick="return confirm('¿Eliminar?')">Eliminar</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
