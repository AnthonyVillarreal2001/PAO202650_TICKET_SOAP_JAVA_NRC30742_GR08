<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>MASUPs - Estadio Interactivo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #1e1e2f; color: #fff; text-align: center; }
        .navbar { background: rgba(0,0,0,0.5); backdrop-filter: blur(10px); }
        .card-glass { background: rgba(255, 255, 255, 0.05); backdrop-filter: blur(10px); border-radius: 15px; padding: 20px; }
        .stadium-container {
            margin: 40px auto;
            width: 800px;
            height: 500px;
            background: #2a2a40;
            border-radius: 400px / 250px;
            position: relative;
            box-shadow: 0 0 30px rgba(0,0,0,0.5) inset, 0 10px 20px rgba(0,0,0,0.3);
            border: 10px solid #444;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .pitch {
            width: 400px;
            height: 250px;
            background: #27ae60;
            border: 2px solid #fff;
            border-radius: 10px;
            position: relative;
        }
        .pitch::after { content: ''; position: absolute; left: 50%; top: 0; bottom: 0; width: 2px; background: #fff; }
        .pitch::before { content: ''; position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%); width: 60px; height: 60px; border: 2px solid #fff; border-radius: 50%; }
        .seats-area { position: absolute; display: grid; gap: 5px; }
        .seat { width: 20px; height: 20px; border-radius: 4px; cursor: pointer; transition: transform 0.2s; }
        .seat:hover { transform: scale(1.3); z-index: 10; }
        .seat.libre { background: #2ecc71; }
        .seat.comprada { background: #e74c3c; }
        
        .north { top: 30px; left: 50%; transform: translateX(-50%); grid-template-columns: repeat(20, 1fr); }
        .south { bottom: 30px; left: 50%; transform: translateX(-50%); grid-template-columns: repeat(20, 1fr); }
        .west { left: 40px; top: 50%; transform: translateY(-50%); grid-template-columns: repeat(5, 1fr); }
        .east { right: 40px; top: 50%; transform: translateY(-50%); grid-template-columns: repeat(5, 1fr); }
        .legend span { display: inline-block; width: 15px; height: 15px; margin-right: 5px; border-radius: 3px; }
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
                    <li class="nav-item"><a class="nav-link" href="reporte">Reporte Ventas</a></li>
                    <li class="nav-item"><a class="nav-link active" href="masup">MASUP Estadio</a></li>
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
        <h2>MASUPs - Vista Real de Ocupación</h2>
        
        <div class="row justify-content-center mt-4">
            <div class="col-md-6">
                <div class="card-glass">
                    <form action="masup" method="get">
                        <label class="form-label text-info fw-bold">Seleccione Partido para Ver Ocupación:</label>
                        <select name="partido" class="form-select" onchange="this.form.submit()">
                            <option value="">-- Seleccione un Partido --</option>
                            <c:forEach var="p" items="${partidos}">
                                <option value="${p.codigo}" ${selectedPartido == p.codigo ? 'selected' : ''}>
                                    ${p.codigo} - ${p.equipoLocal} vs ${p.equipoVisitante}
                                </option>
                            </c:forEach>
                        </select>
                    </form>
                </div>
            </div>
        </div>

        <c:if test="${not empty selectedPartido}">
            <div class="legend mt-3 mb-4">
                <span class="libre bg-success"></span> Libre &nbsp;&nbsp;
                <span class="comprada bg-danger"></span> Ocupada
            </div>

            <div class="stadium-container" id="stadium-container">
                <div class="pitch"></div>
                <div class="seats-area north" id="north-seats"></div>
                <div class="seats-area south" id="south-seats"></div>
                <div class="seats-area west" id="west-seats"></div>
                <div class="seats-area east" id="east-seats"></div>
            </div>
        </c:if>
    </div>

    <!-- Data from Server -->
    <script>
        var dispData = {};
        <c:forEach var="l" items="${localidades}">
            dispData['${l.codigoLocalidad}'] = parseInt('${l.disponibilidad}');
        </c:forEach>

        // Capacidades Totales Fijas por Script de DB
        // General: 1000-1500 (Asumiremos un MAX por partido)
        // Para simplificar la vista, la cantidad de puntitos (seats) será fija,
        // pero la PROPORCIÓN de ocupados/libres dependerá del % de DISPONIBILIDAD.
        
        // P001 Max: GEN=1000, GVI=500, TRI=300, PAL=50
        // Para el visualizador mapeamos: North=GEN, South=GVI, West=TRI, East=PAL
        var maxCap = {
            'GEN': 1500, // Tomamos el maximo histórico de la DB (P004=1500)
            'GVI': 600,
            'TRI': 500,
            'PAL': 100
        };

        var ocupadosData = [];
        <c:forEach var="o" items="${ocupados}">
            ocupadosData.push({
                loc: '${o.codigoLocalidad}',
                cant: parseInt('${o.cantidad}'),
                comprador: '${o.comprador}',
                fecha: '${o.fechaCompra}'
            });
        </c:forEach>
        
        var preciosData = {};
        <c:forEach var="l" items="${localidades}">
            preciosData['${l.codigoLocalidad}'] = '${l.precio}';
        </c:forEach>

        function getDisp(tipo) {
            let pId = "${selectedPartido}";
            if(!pId) return 0;
            let val = dispData[pId + '-' + tipo];
            return val !== undefined ? val : 0;
        }

        function generateRealSeats(areaId, count, tipo, prefix) {
            const container = document.getElementById(areaId);
            if(!container) return;
            container.innerHTML = '';
            
            let pId = "${selectedPartido}";
            let locCode = pId + '-' + tipo;
            let precio = preciosData[locCode] || '0.00';
            
            // Build list of all "buyers" for this block
            let buyersList = [];
            for(let j=0; j<ocupadosData.length; j++){
                if(ocupadosData[j].loc === locCode){
                    for(let k=0; k<ocupadosData[j].cant; k++){
                        buyersList.push({
                            comprador: ocupadosData[j].comprador,
                            fecha: ocupadosData[j].fecha
                        });
                    }
                }
            }

            // Create array of states
            let compradosArr = JSON.parse(localStorage.getItem('comprados_' + pId) || '[]');
            
            let areaMap = {'GEN': 'north', 'GVI': 'south', 'TRI': 'west', 'PAL': 'east'};
            let areaStr = areaMap[tipo] || 'north';
            
            let states = [];
            for(let i=1; i<=count; i++) {
                states.push({status: 'libre', id: areaStr + '_' + i});
            }
            
            // 1. Asignar los asientos que se clickearon localmente
            for(let i=0; i<count; i++) {
                if (compradosArr.includes(states[i].id)) {
                    if (buyersList.length > 0) {
                        let b = buyersList.shift();
                        states[i] = {status: 'comprada', comprador: b.comprador, fecha: b.fecha, id: states[i].id};
                    } else {
                        states[i] = {status: 'comprada', comprador: 'Anónimo', fecha: 'Reciente', id: states[i].id};
                    }
                }
            }
            
            // 2. Llenar los restantes de la BD
            while(buyersList.length > 0) {
                let b = buyersList.shift();
                for(let i=0; i<count; i++) {
                    if (states[i].status === 'libre') {
                        states[i] = {status: 'comprada', comprador: b.comprador, fecha: b.fecha, id: states[i].id};
                        break;
                    }
                }
            }
            // states.sort(() => Math.random() - 0.5); // Removed to keep consistent view across pages

            for(let i=1; i<=count; i++) {
                let sData = states[i-1];
                let seat = document.createElement('div');
                seat.className = 'seat ' + sData.status;
                
                seat.setAttribute('data-bs-toggle', 'tooltip');
                seat.setAttribute('data-bs-html', 'true');
                seat.setAttribute('data-bs-placement', 'top');
                
                if (sData.status === 'comprada') {
                    seat.title = `<strong>` + prefix + ` - Asiento ` + i + `</strong><br/>Comprado por: <span class='text-warning'>` + sData.comprador + `</span><br/><small>` + sData.fecha + `</small>`;
                } else {
                    seat.title = `<strong>` + prefix + ` - Asiento ` + i + `</strong><br/>Estado: <span class='text-success'>DISPONIBLE</span><br/>Precio: $` + precio;
                }
                
                container.appendChild(seat);
            }
        }

        if ("${selectedPartido}") {
            // Visual Seats count (dots in UI)
            generateRealSeats('north-seats', 40, 'GEN', 'Norte/General');
            generateRealSeats('south-seats', 40, 'GVI', 'Sur/Visitante');
            generateRealSeats('west-seats', 25, 'TRI', 'Oeste/Tribuna');
            generateRealSeats('east-seats', 25, 'PAL', 'Este/Palco');
            
            // Init Bootstrap Tooltips
            setTimeout(() => {
                var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
                tooltipTriggerList.map(function (tooltipTriggerEl) {
                    return new bootstrap.Tooltip(tooltipTriggerEl);
                });
            }, 500);
        }
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
