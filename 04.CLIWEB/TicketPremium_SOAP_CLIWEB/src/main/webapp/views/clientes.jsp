<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Clientes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #121212; color: #fff; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .navbar { background-color: #1e1e1e !important; box-shadow: 0 4px 6px rgba(0,0,0,0.3); }
        .card-glass { background: rgba(255, 255, 255, 0.05); backdrop-filter: blur(10px); border-radius: 15px; padding: 20px; margin-bottom: 20px; }
        .table-dark th { background-color: #333; color: #0dcaf0; }
    </style>
    <script>
        function editar(id, nombres, correo, telefono, edad, genero) {
            document.getElementById('idCliente').value = id;
            document.getElementById('idCliente').readOnly = true;
            document.getElementById('nombres').value = nombres;
            document.getElementById('correo').value = correo;
            document.getElementById('telefono').value = telefono;
            document.getElementById('edad').value = edad;
            document.getElementById('genero').value = genero;
        }
        function limpiar() {
            document.getElementById('formCliente').reset();
            document.getElementById('idCliente').readOnly = false;
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
        <h2 class="text-info mb-4">Gestión de Clientes</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="card-glass">
            <form id="formCliente" action="clientes" method="post" class="row g-3">
                <input type="hidden" name="accion" value="guardar">
                <div class="col-md-2">
                    <label>ID / Cédula</label>
                    <input type="text" name="idCliente" id="idCliente" class="form-control" required>
                </div>
                <div class="col-md-3">
                    <label>Nombres</label>
                    <input type="text" name="nombres" id="nombres" class="form-control" required>
                </div>
                <div class="col-md-3">
                    <label>Correo</label>
                    <input type="email" name="correo" id="correo" class="form-control" required>
                </div>
                <div class="col-md-2">
                    <label>Teléfono</label>
                    <input type="text" name="telefono" id="telefono" class="form-control" required>
                </div>
                <div class="col-md-1">
                    <label>Edad</label>
                    <input type="number" name="edad" id="edad" class="form-control" required>
                </div>
                <div class="col-md-1">
                    <label>Género</label>
                    <select name="genero" id="genero" class="form-select" required>
                        <option value="M">M</option>
                        <option value="F">F</option>
                        <option value="O">O</option>
                    </select>
                </div>
                <div class="col-12 text-end mt-3">
                    <button type="button" class="btn btn-secondary" onclick="limpiar()">Limpiar</button>
                    <button type="submit" class="btn btn-info">Guardar</button>
                </div>
            </form>
        </div>

        <div class="card-glass">
            <div class="table-responsive">
                <table class="table table-dark table-hover">
                    <thead>
                        <tr><th>Cédula</th><th>Nombres</th><th>Correo</th><th>Teléfono</th><th>Edad</th><th>Género</th><th>Acciones</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${clientes}">
                            <tr>
                                <td>${c.idCliente}</td>
                                <td>${c.nombres}</td>
                                <td>${c.correo}</td>
                                <td>${c.telefono}</td>
                                <td>${c.edad}</td>
                                <td>${c.genero}</td>
                                <td>
                                    <button class="btn btn-sm btn-primary" onclick="editar('${c.idCliente}', '${c.nombres}', '${c.correo}', '${c.telefono}', '${c.edad}', '${c.genero}')">Editar</button>
                                    <form action="clientes" method="post" style="display:inline;">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="idCliente" value="${c.idCliente}">
                                        <button class="btn btn-sm btn-danger" onclick="return confirm('¿Eliminar?')">Eliminar</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
