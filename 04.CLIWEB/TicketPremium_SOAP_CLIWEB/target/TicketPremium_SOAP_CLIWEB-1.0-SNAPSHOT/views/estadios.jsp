<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Estadios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #121212; color: #fff; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .navbar { background-color: #1e1e1e !important; box-shadow: 0 4px 6px rgba(0,0,0,0.3); }
        .card-glass { background: rgba(255, 255, 255, 0.05); backdrop-filter: blur(10px); border-radius: 15px; padding: 20px; margin-bottom: 20px; }
        .table-dark th { background-color: #333; color: #0dcaf0; }
    </style>
    <script>
        function editar(id, nombre, ciudad, capacidad) {
            document.getElementById('idEstadio').value = id;
            document.getElementById('idEstadio').readOnly = true;
            document.getElementById('nombreEstadio').value = nombre;
            document.getElementById('ciudad').value = ciudad;
            document.getElementById('capacidad').value = capacidad;
        }
        function limpiar() {
            document.getElementById('formEstadio').reset();
            document.getElementById('idEstadio').readOnly = false;
        }
    </script>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark mb-4">
        <div class="container">
            <a class="navbar-brand fw-bold" style="color:#00d2ff;" href="index.jsp">TicketPremium</a>
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item"><a class="nav-link active" href="admin">Admin CRUD</a></li>
                </ul>
            </div>
        </div>
    </nav>
    <div class="container">
        <h2 class="text-info mb-4">Gestión de Estadios</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="card-glass">
            <form id="formEstadio" action="estadios" method="post" class="row g-3">
                <input type="hidden" name="accion" value="guardar">
                <div class="col-md-3">
                    <label>ID Estadio</label>
                    <input type="text" name="idEstadio" id="idEstadio" class="form-control" required>
                </div>
                <div class="col-md-3">
                    <label>Nombre</label>
                    <input type="text" name="nombreEstadio" id="nombreEstadio" class="form-control" required>
                </div>
                <div class="col-md-3">
                    <label>Ciudad</label>
                    <input type="text" name="ciudad" id="ciudad" class="form-control" required>
                </div>
                <div class="col-md-3">
                    <label>Capacidad</label>
                    <input type="number" name="capacidad" id="capacidad" class="form-control" required>
                </div>
                <div class="col-12 text-end mt-3">
                    <button type="button" class="btn btn-secondary" onclick="limpiar()">Limpiar</button>
                    <button type="submit" class="btn btn-info">Guardar</button>
                </div>
            </form>
        </div>

        <div class="card-glass">
            <table class="table table-dark table-hover">
                <thead>
                    <tr><th>ID</th><th>Nombre</th><th>Ciudad</th><th>Capacidad</th><th>Acciones</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="e" items="${estadios}">
                        <tr>
                            <td>${e.idEstadio}</td>
                            <td>${e.nombreEstadio}</td>
                            <td>${e.ciudad}</td>
                            <td>${e.capacidad}</td>
                            <td>
                                <button class="btn btn-sm btn-primary" onclick="editar('${e.idEstadio}', '${e.nombreEstadio}', '${e.ciudad}', '${e.capacidad}')">Editar</button>
                                <form action="estadios" method="post" style="display:inline;">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="idEstadio" value="${e.idEstadio}">
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
