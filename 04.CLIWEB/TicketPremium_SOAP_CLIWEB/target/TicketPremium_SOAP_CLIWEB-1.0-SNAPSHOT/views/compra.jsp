<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Vender Boletos - MASUP</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #1e1e2f; color: #fff; }
        .navbar { background: rgba(0,0,0,0.5); backdrop-filter: blur(10px); }
        .card-glass { background: rgba(255,255,255,0.05); backdrop-filter: blur(15px); border-radius: 15px; padding: 20px; }
        .form-control, .form-select { background: rgba(255,255,255,0.1); color: #fff; border: 1px solid rgba(255,255,255,0.2); }
        .form-control:focus, .form-select:focus { background: rgba(255,255,255,0.2); color: #fff; }
        .form-control[readonly] { background: rgba(255,255,255,0.05); }
        option { color: #000; }

        /* MASUP Styles */
        .stadium-container {
            margin: 20px auto; width: 100%; max-width: 600px; height: 350px;
            background: #2a2a40; border-radius: 300px / 175px; position: relative;
            box-shadow: 0 0 30px rgba(0,0,0,0.5) inset, 0 10px 20px rgba(0,0,0,0.3);
            border: 8px solid #444; display: none; align-items: center; justify-content: center;
        }
        .pitch {
            width: 300px; height: 180px; background: #27ae60; border: 2px solid #fff;
            border-radius: 10px; position: relative;
        }
        .pitch::after { content: ''; position: absolute; left: 50%; top: 0; bottom: 0; width: 2px; background: #fff; }
        .seats-area { position: absolute; display: grid; gap: 4px; }
        .seat { width: 16px; height: 16px; border-radius: 3px; cursor: pointer; transition: transform 0.2s; }
        .seat:hover { transform: scale(1.3); z-index: 10; }
        
        .seat.libre { background: #2ecc71; }
        .seat.comprada { background: #e74c3c; cursor: not-allowed; }
        .seat.seleccionada { background: #3498db; transform: scale(1.1); box-shadow: 0 0 5px #3498db; }
        
        .north { top: 20px; left: 50%; transform: translateX(-50%); grid-template-columns: repeat(20, 1fr); }
        .south { bottom: 20px; left: 50%; transform: translateX(-50%); grid-template-columns: repeat(20, 1fr); }
        .west { left: 25px; top: 50%; transform: translateY(-50%); grid-template-columns: repeat(5, 1fr); }
        .east { right: 25px; top: 50%; transform: translateY(-50%); grid-template-columns: repeat(5, 1fr); }

        .legend span { display: inline-block; width: 15px; height: 15px; margin-right: 5px; border-radius: 3px; vertical-align: middle; }
    </style>
    <script>
        let carrito = {}; 
        let localidadesPrecios = {};
        
        <c:forEach var="l" items="${localidades}">
            localidadesPrecios['${l.codigoLocalidad}'] = parseFloat('${l.precio}');
        </c:forEach>

        function getCodLoc(area) {
            var pId = document.querySelector('input[name="partido"]').value;
            if(area === 'north') return pId + '-GEN';
            if(area === 'south') return pId + '-GVI';
            if(area === 'west') return pId + '-TRI';
            if(area === 'east') return pId + '-PAL';
            return pId + '-GEN';
        }

        var dispData = {};
        <c:forEach var="l" items="${localidades}">
            dispData['${l.codigoLocalidad}'] = parseInt('${l.disponibilidad}');
        </c:forEach>

        var maxCap = {
            'GEN': 1500,
            'GVI': 600,
            'TRI': 500,
            'PAL': 100
        };

        function getDisp(tipo) {
            let pId = document.querySelector('input[name="partido"]').value;
            if(!pId) return 0;
            let val = dispData[pId + '-' + tipo];
            return val !== undefined ? val : 0;
        }

        var ocupadosData = [];
        <c:forEach var="o" items="${ocupados}">
            ocupadosData.push({
                "loc": "${o.codigoLocalidad}",
                "cant": parseInt("${o.cantidad}"),
                "comprador": "${o.comprador}",
                "fecha": "${o.fechaCompra}"
            });
        </c:forEach>

        function renderMap() {
            const container = document.getElementById('stadium-container');
            container.style.display = 'flex';
            
            const areas = [
                { id: 'north-seats', area: 'north', count: 40, prefix: 'Norte/General', tipo: 'GEN' },
                { id: 'south-seats', area: 'south', count: 40, prefix: 'Sur/Visitante', tipo: 'GVI' },
                { id: 'west-seats', area: 'west', count: 25, prefix: 'Oeste/Tribuna', tipo: 'TRI' },
                { id: 'east-seats', area: 'east', count: 25, prefix: 'Este/Palco', tipo: 'PAL' }
            ];

            let pId = document.querySelector('input[name="partido"]').value;
            
            areas.forEach(a => {
                const targetArea = document.getElementById(a.id);
                targetArea.innerHTML = '';
                let codLoc = getCodLoc(a.area);
                if (!localidadesPrecios[codLoc]) return; 
                let precio = localidadesPrecios[codLoc];
                
                let buyersList = [];
                for(let j=0; j<ocupadosData.length; j++){
                    if(ocupadosData[j].loc === codLoc){
                        for(let k=0; k<ocupadosData[j].cant; k++){
                            buyersList.push({
                                comprador: ocupadosData[j].comprador,
                                fecha: ocupadosData[j].fecha
                            });
                        }
                    }
                }
                
                let compradosArr = JSON.parse(localStorage.getItem('comprados_' + pId) || '[]');
                
                let states = [];
                for(let i=1; i<=a.count; i++) {
                    states.push({status: 'libre', id: a.area + '_' + i});
                }
                
                // 1. Asignar los asientos que el usuario clickeó localmente (para mantener la consistencia visual)
                for(let i=0; i<a.count; i++) {
                    if (compradosArr.includes(states[i].id)) {
                        if (buyersList.length > 0) {
                            let b = buyersList.shift();
                            states[i] = {status: 'comprada', comprador: b.comprador, fecha: b.fecha, id: states[i].id};
                        } else {
                            states[i] = {status: 'comprada', comprador: 'Usted', fecha: 'Reciente', id: states[i].id};
                        }
                    }
                }
                
                // 2. Llenar los asientos restantes que vienen de la BD
                while(buyersList.length > 0) {
                    let b = buyersList.shift();
                    for(let i=0; i<a.count; i++) {
                        if (states[i].status === 'libre') {
                            states[i] = {status: 'comprada', comprador: b.comprador, fecha: b.fecha, id: states[i].id};
                            break;
                        }
                    }
                }
                
                for(let i=1; i<=a.count; i++) {
                    let sData = states[i-1];
                    let seatId = sData.id;
                    
                    let status = sData.status;
                    
                    let seat = document.createElement('div');
                    seat.className = 'seat ' + status;
                    
                    seat.setAttribute('data-bs-toggle', 'tooltip');
                    seat.setAttribute('data-bs-html', 'true');
                    seat.setAttribute('data-bs-placement', 'top');
                    
                    if (status === 'comprada') {
                        let comp = sData.comprador || "Sistema";
                        let fec = sData.fecha || "No disponible";
                        seat.title = `<strong>` + a.prefix + ` - Asiento ` + i + `</strong><br/>Comprado por: <span class='text-warning'>` + comp + `</span><br/><small>` + fec + `</small>`;
                    } else {
                        seat.title = `<strong>` + a.prefix + ` - Asiento ` + i + `</strong><br/>Estado: <span class='text-success'>DISPONIBLE</span><br/>Precio: $` + precio;
                    }
                    
                    seat.onclick = function() {
                        if (seat.classList.contains('comprada')) return;
                        
                        if (!carrito[codLoc]) carrito[codLoc] = { precio: localidadesPrecios[codLoc], cantidad: 0, asientos: [] };

                        if (seat.classList.contains('seleccionada')) {
                            seat.classList.remove('seleccionada');
                            seat.classList.add('libre');
                            carrito[codLoc].cantidad--;
                            carrito[codLoc].asientos = carrito[codLoc].asientos.filter(s => s !== seatId);
                            if (carrito[codLoc].cantidad === 0) delete carrito[codLoc];
                        } else {
                            seat.classList.remove('libre');
                            seat.classList.add('seleccionada');
                            carrito[codLoc].cantidad++;
                            carrito[codLoc].asientos.push(seatId);
                        }
                        actualizarCarrito();
                    };
                    targetArea.appendChild(seat);
                }
            });
            
            // Init Bootstrap Tooltips
            setTimeout(() => {
                var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
                tooltipTriggerList.map(function (tooltipTriggerEl) {
                    return new bootstrap.Tooltip(tooltipTriggerEl);
                });
            }, 500);
        }

        function actualizarCarrito() {
            let total = 0;
            let totalAsientos = 0;
            let htmlResumen = "";
            let asientosTotales = [];

            for (let loc in carrito) {
                let item = carrito[loc];
                let sub = item.cantidad * item.precio;
                total += sub;
                totalAsientos += item.cantidad;
                htmlResumen += "<li>" + loc + ": " + item.cantidad + " asient. ($" + sub + ")</li>";
                asientosTotales = asientosTotales.concat(item.asientos);
            }

            document.getElementById('cantidad').value = totalAsientos;
            document.getElementById('carrito_json').value = JSON.stringify(carrito);
            document.getElementById('asientos_json').value = JSON.stringify(asientosTotales);
            
            let form = document.querySelector('form[action="checkout"]');
            document.querySelectorAll('.dinamico').forEach(e => e.remove());
            for (let loc in carrito) {
                let item = carrito[loc];
                let iLoc = document.createElement('input'); iLoc.type = 'hidden'; iLoc.name = 'cod_loc'; iLoc.value = loc; iLoc.className = 'dinamico';
                let iCant = document.createElement('input'); iCant.type = 'hidden'; iCant.name = 'cant_loc'; iCant.value = item.cantidad; iCant.className = 'dinamico';
                let iPrec = document.createElement('input'); iPrec.type = 'hidden'; iPrec.name = 'prec_loc'; iPrec.value = item.precio; iPrec.className = 'dinamico';
                form.appendChild(iLoc); form.appendChild(iCant); form.appendChild(iPrec);
            }

            let ul = document.getElementById('resumen_carrito');
            if(ul) ul.innerHTML = htmlResumen;

            calcTotal(total);
        }

        function calcTotal(subtotal) {
            if (subtotal === undefined) {
               subtotal = 0;
               for (let loc in carrito) subtotal += carrito[loc].cantidad * carrito[loc].precio;
            }

            var iva = subtotal * 0.12;
            var tot = subtotal + iva;
            document.getElementById("totalCalc").innerText = "$" + tot.toFixed(2) + " (Incluye 12% IVA)";
            
            var metodo = document.getElementById("metodoPago").value;
            if(metodo === 'EFECTIVO' && tot > 0) {
                var desc = tot * 0.12;
                tot = tot - desc;
                document.getElementById("totalCalc").innerText += " -> Desc. 12% Efectivo! Final: $" + tot.toFixed(2);
            }
            
            document.getElementById("btnComprar").disabled = (subtotal === 0);
        }

        function filterClientes() {
            var metodo = document.getElementById("metodoPago").value;
            var clienteSelect = document.getElementById("cliente");
            var options = clienteSelect.options;
            var resetSelection = false;
            for (var i = 0; i < options.length; i++) {
                var opt = options[i];
                var isApto = opt.getAttribute("data-apto-credito") === "true";
                if (metodo === "CREDITO") {
                    if (isApto) { opt.style.display = ""; } 
                    else { opt.style.display = "none"; if (opt.selected) resetSelection = true; }
                } else { opt.style.display = ""; }
            }
            if (resetSelection) {
                for (var i = 0; i < options.length; i++) {
                    if (options[i].style.display !== "none") { clienteSelect.selectedIndex = i; break; }
                }
            }
        }
        
        function togglePlazo() {
            var metodo = document.getElementById("metodoPago").value;
            var plazoContainer = document.getElementById("plazoContainer");
            if (metodo === "CREDITO") {
                plazoContainer.style.display = "block";
            } else {
                plazoContainer.style.display = "none";
            }
        }

        function abrirModalConfirmacion() {
            let subtotal = 0;
            for (let loc in carrito) subtotal += carrito[loc].cantidad * carrito[loc].precio;
            var iva = subtotal * 0.12;
            var tot = subtotal + iva;

            var metodo = document.getElementById("metodoPago").value;
            var body = document.getElementById("modalBodyResumen");

            if (metodo === "EFECTIVO") {
                var desc = tot * 0.12;
                var final = tot - desc;
                body.innerHTML = `
                    <h4 class="text-info text-center">Resumen de Factura</h4>
                    <table class="table table-bordered">
                        <tr><th>Subtotal</th><td>$` + subtotal.toFixed(2) + `</td></tr>
                        <tr><th>IVA (12%)</th><td>$` + iva.toFixed(2) + `</td></tr>
                        <tr><th>Total</th><td>$` + tot.toFixed(2) + `</td></tr>
                        <tr><th>Descuento (12%)</th><td class="text-success">-$` + desc.toFixed(2) + `</td></tr>
                        <tr><th>Total a Pagar</th><td class="text-info fw-bold">$` + final.toFixed(2) + `</td></tr>
                    </table>
                `;
            } else {
                // Crédito Simulación
                let plazo = parseInt(document.getElementById("plazoMeses").value);
                let tasaAnual = 0.165;
                let tasaPeriodo = tasaAnual / 12.0;
                let factor = (1 - Math.pow(1 + tasaPeriodo, -plazo)) / tasaPeriodo;
                let cuotaMensual = tot / factor;

                let html = `
                    <h4 class="text-info text-center">Simulación de Amortización</h4>
                    <table class="table table-bordered text-center">
                        <tr><th>Valor Préstamo</th><td>$` + tot.toFixed(2) + `</td></tr>
                        <tr><th>Cuotas</th><td>` + plazo + `</td></tr>
                        <tr><th>Tasa Interés Anual</th><td>16.50%</td></tr>
                        <tr><th>Cuota Mensual</th><td class="text-danger fw-bold">($` + cuotaMensual.toFixed(2) + `)</td></tr>
                    </table>
                    <table class="table table-striped text-center mt-3">
                        <thead class="table-dark">
                            <tr><th># Cuota</th><th>Valor Cuota</th><th>Interés Pagado</th><th>Capital Pagado</th><th>Saldo</th></tr>
                        </thead>
                        <tbody>
                            <tr><td>0</td><td></td><td></td><td></td><td>` + tot.toFixed(2) + `</td></tr>
                `;

                let saldo = tot;
                for (let i = 1; i <= plazo; i++) {
                    let interes = saldo * tasaPeriodo;
                    let capital = cuotaMensual - interes;
                    saldo = saldo - capital;
                    if(saldo < 0) saldo = 0;
                    
                    html += `
                            <tr>
                                <td>` + i + `</td>
                                <td>` + cuotaMensual.toFixed(2) + `</td>
                                <td>` + interes.toFixed(2) + `</td>
                                <td>` + capital.toFixed(2) + `</td>
                                <td>` + saldo.toFixed(2) + `</td>
                            </tr>
                    `;
                }
                html += `</tbody></table>`;
                body.innerHTML = html;
            }

            var myModal = new bootstrap.Modal(document.getElementById('modalConfirmacion'));
            myModal.show();
        }

        function confirmarCompra() {
            guardarCompras();
            document.querySelector('form[action="checkout"]').submit();
        }

        function guardarCompras() {
            let pId = document.querySelector('input[name="partido"]').value;
            let compradosStr = localStorage.getItem('comprados_' + pId) || '[]';
            let compradosArr = JSON.parse(compradosStr);
            let nuevos = JSON.parse(document.getElementById('asientos_json').value || '[]');
            // Append and make unique
            let unidos = [...new Set([...compradosArr, ...nuevos])];
            localStorage.setItem('comprados_' + pId, JSON.stringify(unidos));
            return true;
        }

        window.onload = function() {
            filterClientes();
            var pId = document.querySelector('input[name="partido"]');
            if (pId && pId.value) renderMap();
        };
    </script>
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
        <h2 class="mb-4 text-center">Venta de Boletos - MASUP Checkout</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <div class="row">
            <!-- Columna Izquierda: Selección -->
            <div class="col-md-5">
                <div class="card-glass mb-4">
                    <h5 class="text-info">Paso 1: Seleccione Partido</h5>
                    <form action="comprar" method="get">
                        <select name="partido" class="form-select" onchange="this.form.submit()">
                            <option value="">-- Seleccione --</option>
                            <c:forEach var="p" items="${partidos}">
                                <option value="${p.codigo}" ${selectedPartido == p.codigo ? 'selected' : ''}>
                                    ${p.codigo} - ${p.equipoLocal} vs ${p.equipoVisitante}
                                </option>
                            </c:forEach>
                        </select>
                    </form>
                </div>

                <c:if test="${not empty localidades}">
                <div class="card-glass">
                    <h5 class="text-info">Paso 2: Formulario de Pago</h5>
                    <form action="checkout" method="post" onsubmit="return guardarCompras()">
                        <input type="hidden" name="partido" value="${selectedPartido}">
                        
                        <div class="mb-3">
                            <label>Resumen de Asientos (MASUP)</label>
                            <ul id="resumen_carrito" class="text-info mt-2" style="font-weight: bold; font-size: 1.1em; list-style-type: none; padding-left: 0;"></ul>
                            <input type="hidden" name="carrito_json" id="carrito_json" value="{}">
                            <input type="hidden" name="asientos_json" id="asientos_json" value="[]">
                        </div>
                        
                        <div class="mb-3">
                            <label>Total Asientos Seleccionados</label>
                            <input type="number" name="cantidad" id="cantidad" class="form-control" value="0" readonly>
                        </div>

                        <div class="mb-3">
                            <label>Cliente (Valida Crédito SOAP)</label>
                            <select name="cliente" id="cliente" class="form-select" required>
                                <c:forEach var="c" items="${clientes}">
                                    <option value="${c.idCliente}" data-apto-credito="${aptosCredito[c.idCliente]}">${c.nombres}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label>Método de Pago</label>
                            <select name="metodoPago" id="metodoPago" class="form-select" onchange="calcTotal(); filterClientes(); togglePlazo();">
                                <option value="CREDITO">Crédito Directo (Core Bancario)</option>
                                <option value="EFECTIVO">Efectivo (12% Descuento)</option>
                            </select>
                        </div>

                        <div class="mb-3" id="plazoContainer">
                            <label>Plazo (Meses)</label>
                            <select name="plazoMeses" id="plazoMeses" class="form-select">
                                <option value="3">3 Meses</option>
                                <option value="6" selected>6 Meses</option>
                                <option value="9">9 Meses</option>
                                <option value="12">12 Meses</option>
                            </select>
                        </div>

                        <h5 class="mt-4 text-warning text-center">Total: <span id="totalCalc">$0.00</span></h5>
                        <button type="button" id="btnComprar" class="btn btn-primary w-100 mt-3" disabled onclick="abrirModalConfirmacion()">Procesar Compra</button>
                    </form>
                </div>
                </c:if>
            </div>

            <!-- Modal de Confirmación -->
            <div class="modal fade" id="modalConfirmacion" tabindex="-1" aria-hidden="true" style="color: black;">
              <div class="modal-dialog modal-lg">
                <div class="modal-content">
                  <div class="modal-header">
                    <h5 class="modal-title">Resumen de la Transacción</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body" id="modalBodyResumen">
                    <!-- Contenido dinámico (Simulación de Amortización o Resumen Efectivo) -->
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-success" onclick="confirmarCompra()">Confirmar Compra</button>
                  </div>
                </div>
              </div>
            </div>


            <!-- Columna Derecha: MASUP Estadio -->
            <c:if test="${not empty localidades}">
            <div class="col-md-7">
                <div class="card-glass h-100">
                    <h5 class="text-info text-center">Paso 3: Seleccione sus Asientos</h5>
                    <div class="text-center legend mt-2 mb-3">
                        <span class="bg-success"></span> Libre &nbsp;
                        <span class="bg-danger"></span> Ocupada &nbsp;
                        <span class="bg-primary"></span> Seleccionada
                    </div>
                    
                    <div class="stadium-container" id="stadium-container">
                        <div class="pitch"></div>
                        <div class="seats-area north" id="north-seats"></div>
                        <div class="seats-area south" id="south-seats"></div>
                        <div class="seats-area west" id="west-seats"></div>
                        <div class="seats-area east" id="east-seats"></div>
                    </div>
                    <p class="text-center text-white-50 mt-3"><small>*Haz clic directamente en los asientos para seleccionarlos. Puedes elegir de diferentes localidades (Norte, Sur, Este, Oeste) simultáneamente.</small></p>
                </div>
            </div>
            </c:if>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
