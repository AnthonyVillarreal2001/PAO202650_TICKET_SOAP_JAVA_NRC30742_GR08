<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Factura #${factura.idFactura} - TicketPremium</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f5f6fa; color: #333; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .invoice-container { background: #fff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); margin-top: 40px; margin-bottom: 40px; }
        .invoice-header { border-bottom: 2px solid #0dcaf0; padding-bottom: 20px; margin-bottom: 30px; }
        .invoice-logo { font-size: 2rem; font-weight: bold; color: #0dcaf0; }
        .invoice-details { margin-bottom: 30px; }
        .table-invoice th { background-color: #0dcaf0; color: #fff; }
        .totals-row { font-weight: bold; }
        .footer-note { font-size: 0.9rem; color: #777; text-align: center; margin-top: 40px; border-top: 1px solid #ddd; padding-top: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="d-flex justify-content-between mt-3">
            <a href="facturas?cliente=${factura.idCliente}" class="btn btn-secondary">&larr; Volver a Mis Facturas</a>
            <button onclick="window.print()" class="btn btn-primary">Imprimir Factura</button>
        </div>

        <div class="row justify-content-center">
            <div class="col-lg-10">
                <div class="invoice-container">
                    <div class="row invoice-header">
                        <div class="col-sm-6">
                            <div class="invoice-logo">TICKET PREMIUM</div>
                            <div>Dirección: Av. General Rumiñahui s/n, Sangolquí</div>
                            <div>Teléfono: 1800-TICKET (842538)</div>
                            <div>Email: soporte@ticketpremium.ec</div>
                        </div>
                        <div class="col-sm-6 text-end">
                            <h2 class="text-uppercase" style="color: #555;">Factura Electrónica</h2>
                            <h4>No. 001-001-${String.format("%09d", factura.idFactura)}</h4>
                            <div><strong>RUC Emisor:</strong> 1792141544001</div>
                            <div><strong>Fecha de Emisión:</strong> ${factura.fechaEmision}</div>
                            <div><strong>Clave de Acceso:</strong> <small class="text-muted">1234567890123456789012345678901234567890123456789</small></div>
                        </div>
                    </div>

                    <div class="row invoice-details">
                        <div class="col-sm-8">
                            <h5 class="mb-3">Datos del Cliente:</h5>
                            <div><strong>Razón Social / Nombres:</strong> ${factura.nombreCliente}</div>
                            <div><strong>RUC / CI:</strong> ${factura.idCliente}</div>
                        </div>
                        <div class="col-sm-4 text-end">
                            <div><strong>Partido Cod:</strong> ${factura.codigoPartido}</div>
                        </div>
                    </div>

                    <table class="table table-bordered table-invoice mt-4">
                        <thead>
                            <tr>
                                <th>Cod. Localidad</th>
                                <th>Descripción</th>
                                <th class="text-center">Cant.</th>
                                <th class="text-end">Precio Unit.</th>
                                <th class="text-end">Descuento</th>
                                <th class="text-end">Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${detalles}">
                                <tr>
                                    <td>${d.codigoLocalidad}</td>
                                    <td>Asiento(s) en ${d.codigoLocalidad}</td>
                                    <td class="text-center">${d.cantidad}</td>
                                    <td class="text-end">$${d.precioUnitario}</td>
                                    <td class="text-end">$0.00</td>
                                    <td class="text-end">$${d.totalDetalle}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div class="row mt-4">
                        <div class="col-sm-7">
                            <div class="p-3 bg-light border rounded">
                                <strong>Información Adicional:</strong><br>
                                FORMA DE PAGO: EFECTIVO / SIN UTILIZACION DEL SISTEMA FINANCIERO<br>
                                Este documento es una representación impresa de un CFDI válido.
                            </div>
                        </div>
                        <div class="col-sm-5">
                            <table class="table table-sm table-borderless text-end">
                                <tr>
                                    <td><strong>Subtotal 12%:</strong></td>
                                    <td>$${factura.subtotal}</td>
                                </tr>
                                <tr>
                                    <td><strong>Subtotal 0%:</strong></td>
                                    <td>$0.00</td>
                                </tr>
                                <tr>
                                    <td><strong>Descuento (12% Efectivo):</strong></td>
                                    <td class="text-success">-$${String.format("%.2f", factura.subtotal * 0.12)}</td>
                                </tr>
                                <tr>
                                    <td><strong>Subtotal Neto:</strong></td>
                                    <td>$${String.format("%.2f", factura.subtotal - (factura.subtotal * 0.12))}</td>
                                </tr>
                                <tr>
                                    <td><strong>IVA 12%:</strong></td>
                                    <td>$${factura.iva}</td>
                                </tr>
                                <tr class="totals-row" style="font-size: 1.2rem;">
                                    <td><strong>Total a Pagar:</strong></td>
                                    <td class="text-primary">$${factura.total}</td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div class="footer-note">
                        Gracias por su compra. TicketPremium le desea un excelente partido.
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
