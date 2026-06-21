<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Partidos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #121212; color: #fff; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .navbar { background-color: #1e1e1e !important; box-shadow: 0 4px 6px rgba(0,0,0,0.3); }
        .card-glass { background: rgba(255, 255, 255, 0.05); backdrop-filter: blur(10px); border-radius: 15px; padding: 20px; margin-bottom: 20px; }
        .table-dark th { background-color: #333; color: #0dcaf0; }
    </style>
    <script>
        function editar(codigo, local, visitante, fecha, lugar) {
            document.getElementById('codigo').value = codigo;
            document.getElementById('codigo').readOnly = true;
            document.getElementById('equipoLocal').value = local;
            document.getElementById('equipoVisitante').value = visitante;
            document.getElementById('fecha').value = fecha; // Formato yyyy-MM-ddThh:mm
            document.getElementById('lugar').value = lugar;
        }
        function limpiar() {
            document.getElementById('formPartido').reset();
            document.getElementById('codigo').readOnly = false;
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
        <h2 class="text-info mb-4">Gestión de Partidos</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="card-glass">
            <form id="formPartido" action="partidos" method="post" class="row g-3">
                <input type="hidden" name="accion" value="guardar">
                <div class="col-md-2">
                    <label>Código</label>
                    <input type="text" name="codigo" id="codigo" class="form-control" required placeholder="Ej: P001">
                </div>
                <div class="col-md-3">
                    <label>Equipo Local</label>
                    <input type="text" name="equipoLocal" id="equipoLocal" class="form-control" required>
                </div>
                <div class="col-md-3">
                    <label>Equipo Visitante</label>
                    <input type="text" name="equipoVisitante" id="equipoVisitante" class="form-control" required>
                </div>
                <div class="col-md-2">
                    <label>Fecha y Hora</label>
                    <input type="datetime-local" name="fecha" id="fecha" class="form-control" required>
                </div>
                <div class="col-md-2">
                    <label>Lugar</label>
                    <select name="lugar" id="lugar" class="form-select" required>
                        <option value="">Seleccione...</option>
                        <c:forEach var="e" items="${estadios}">
                            <option value="${e.nombreEstadio}">${e.nombreEstadio} - ${e.ciudad}</option>
                        </c:forEach>
                    </select>
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
                    <tr><th>Código</th><th>Local</th><th>Visitante</th><th>Fecha</th><th>Lugar</th><th>Acciones</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${partidos}">
                        <tr>
                            <td>${p.codigo}</td>
                            <td>${p.equipoLocal}</td>
                            <td>${p.equipoVisitante}</td>
                            <td>${p.fecha}</td>
                            <td>${p.lugar}</td>
                            <td>
                                <!-- Formateamos la fecha para html datetime-local si es posible, o usamos JS (se asume que viene algo como 2026-05-25 19:00:00 o 2026-05-25T19:00:00) -->
                                <button class="btn btn-sm btn-primary" onclick="editar('${p.codigo}', '${p.equipoLocal}', '${p.equipoVisitante}', '${p.fecha}'.replace(' ', 'T').substring(0,16), '${p.lugar}')">Editar</button>
                                <form action="partidos" method="post" style="display:inline;">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="codigo" value="${p.codigo}">
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
