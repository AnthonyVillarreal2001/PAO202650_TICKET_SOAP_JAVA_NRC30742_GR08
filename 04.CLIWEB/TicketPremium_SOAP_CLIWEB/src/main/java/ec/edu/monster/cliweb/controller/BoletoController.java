package ec.edu.monster.cliweb.controller;

import ec.edu.monster.cliweb.soap.SoapHelper;
import ec.edu.monster.cliweb.soap.XmlParser;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BoletoController", urlPatterns = {"/comprar", "/checkout"})
public class BoletoController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        
        if ("/comprar".equals(action) || "/checkout".equals(action)) {
            // Cargar partidos
            String envPartidos = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                    + "<soapenv:Header/><soapenv:Body><ws:listarPartidosDisponibles/></soapenv:Body></soapenv:Envelope>";
            try {
                String resPartidos = SoapHelper.callSoap("WSFederacion", "", envPartidos);
                List<Map<String, String>> partidos = XmlParser.parseList(resPartidos, "partido");
                request.setAttribute("partidos", partidos);
                
                String pId = request.getParameter("partido");
                if (pId != null && !pId.isEmpty()) {
                    String envLoc = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                            + "<soapenv:Header/><soapenv:Body><ws:listarLocalidadesDisponibles><codigoPartido>" + pId + "</codigoPartido></ws:listarLocalidadesDisponibles></soapenv:Body></soapenv:Envelope>";
                    String resLoc = SoapHelper.callSoap("WSFederacion", "", envLoc);
                    List<Map<String, String>> localidades = XmlParser.parseList(resLoc, "localidad");
                    request.setAttribute("localidades", localidades);
                    
                    String envO = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                            + "<soapenv:Header/><soapenv:Body><ws:obtenerAsientosOcupados>"
                            + "<codigoPartido>" + pId + "</codigoPartido>"
                            + "</ws:obtenerAsientosOcupados></soapenv:Body></soapenv:Envelope>";
                    String resO = SoapHelper.callSoap("WSFederacion", "", envO);
                    List<Map<String, String>> ocupados = XmlParser.parseList(resO, "asientoOcupado");
                    request.setAttribute("ocupados", ocupados);
                    
                    request.setAttribute("selectedPartido", pId);
                }
                
                // Load clients for dropdown
                String envCli = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                    + "<soapenv:Header/><soapenv:Body><ws:listarClientes/></soapenv:Body></soapenv:Envelope>";
                String resCli = SoapHelper.callSoap("WSCRUD", "", envCli);
                List<Map<String, String>> listaClientes = XmlParser.parseList(resCli, "cliente");
                request.setAttribute("clientes", listaClientes);
                
                // Check credit eligibility for dynamic UI filter
                java.util.Map<String, Boolean> aptosCredito = new java.util.HashMap<>();
                for (Map<String, String> cli : listaClientes) {
                    String cid = cli.get("idCliente");
                    String envCred = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                            + "<soapenv:Header/><soapenv:Body><ws:evaluarSujetoCredito><idCliente>" + cid + "</idCliente></ws:evaluarSujetoCredito></soapenv:Body></soapenv:Envelope>";
                    try {
                        String resCred = SoapHelper.callSoap("WSCredito", "", envCred);
                        String estadoStr = XmlParser.parseSingleValue(resCred, "estado");
                        aptosCredito.put(cid, "1".equals(estadoStr));
                    } catch (Exception e) {
                        aptosCredito.put(cid, false);
                    }
                }
                request.setAttribute("aptosCredito", aptosCredito);
                
            } catch (Exception e) {
                request.setAttribute("error", e.getMessage());
            }
            request.getRequestDispatcher("/views/compra.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        if ("/checkout".equals(action)) {
            String pId = request.getParameter("partido");
            String cliente = request.getParameter("cliente");
            String metodoPago = request.getParameter("metodoPago");
            
            String[] codLocs = request.getParameterValues("cod_loc");
            String[] cantLocs = request.getParameterValues("cant_loc");
            String[] precLocs = request.getParameterValues("prec_loc");
            
            if (codLocs == null || codLocs.length == 0) {
                request.setAttribute("error", "No se han seleccionado localidades.");
                doGet(request, response);
                return;
            }

            double subtotalGeneral = 0;
            for (int i=0; i<codLocs.length; i++) {
                int c = Integer.parseInt(cantLocs[i]);
                double p = Double.parseDouble(precLocs[i]);
                subtotalGeneral += (c * p);
            }
            
            double iva = subtotalGeneral * 0.12;
            double total = subtotalGeneral + iva;
            
            if ("EFECTIVO".equals(metodoPago)) {
                double descuento = total * 0.12;
                total = total - descuento;
                request.setAttribute("descuentoAplicado", descuento);
            } else if ("CREDITO".equals(metodoPago)) {
                String envCredito = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/><soapenv:Body><ws:evaluarSujetoCredito><idCliente>" + cliente + "</idCliente></ws:evaluarSujetoCredito></soapenv:Body></soapenv:Envelope>";
                try {
                    String resCredito = SoapHelper.callSoap("WSCredito", "", envCredito);
                    String estadoStr = XmlParser.parseSingleValue(resCredito, "estado");
                    String msg = XmlParser.parseSingleValue(resCredito, "mensaje");
                    String montoStr = XmlParser.parseSingleValue(resCredito, "montoMaximo");
                    
                    int estado = estadoStr != null ? Integer.parseInt(estadoStr) : -1;
                    if (estado != 1) {
                        request.setAttribute("error", "Crédito Rechazado: " + msg);
                        doGet(request, response);
                        return;
                    }
                    double montoMaximo = Double.parseDouble(montoStr);
                    if (montoMaximo < total) {
                        request.setAttribute("error", "Crédito Aprobado pero monto máximo (" + montoMaximo + ") no cubre el total (" + total + ").");
                        doGet(request, response);
                        return;
                    }
                } catch (Exception e) {
                    request.setAttribute("error", "Error consultando Core Bancario: " + e.getMessage());
                    doGet(request, response);
                    return;
                }
            }
            
            // Procesar Multiples Compras SOAP Consolidado
            boolean todoOk = true;
            StringBuilder msgs = new StringBuilder();
            String vendedor = (String) request.getSession().getAttribute("usuarioLogueado");
            if (vendedor == null) vendedor = "SISTEMA";
            
            String locsCsv = String.join(",", codLocs);
            String cantsCsv = String.join(",", cantLocs);

            String envCompra = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                    + "<soapenv:Header/><soapenv:Body><ws:comprarBoletosMultiples>"
                    + "<codigoPartido>" + pId + "</codigoPartido>"
                    + "<codigosLocalidades>" + locsCsv + "</codigosLocalidades>"
                    + "<cantidades>" + cantsCsv + "</cantidades>"
                    + "<cliente>" + cliente + "</cliente>"
                    + "<vendedor>" + vendedor + "</vendedor>"
                    + "</ws:comprarBoletosMultiples></soapenv:Body></soapenv:Envelope>";
            try {
                String resCompra = SoapHelper.callSoap("WSFederacion", "", envCompra);
                String estadoStr = XmlParser.parseSingleValue(resCompra, "estado");
                String msg = XmlParser.parseSingleValue(resCompra, "mensaje");
                if (!"1".equals(estadoStr) && !"2".equals(estadoStr)) {
                    todoOk = false;
                    msgs.append("Error en la compra: ").append(msg).append(". ");
                }
            } catch (Exception e) {
                todoOk = false;
                msgs.append("Error SOAP: ").append(e.getMessage()).append(". ");
            }
            
            if (todoOk) {
                if ("CREDITO".equals(metodoPago)) {
                    String plazoStr = request.getParameter("plazoMeses");
                    int plazo = (plazoStr != null) ? Integer.parseInt(plazoStr) : 6;
                    String envAmort = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                            + "<soapenv:Header/><soapenv:Body><ws:guardarAmortizaciones>"
                            + "<idCliente>" + cliente + "</idCliente>"
                            + "<valorLocalidades>" + total + "</valorLocalidades>"
                            + "<plazoMeses>" + plazo + "</plazoMeses>"
                            + "</ws:guardarAmortizaciones></soapenv:Body></soapenv:Envelope>";
                    try {
                        SoapHelper.callSoap("WSCredito", "", envAmort);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    response.sendRedirect("amortizaciones?cliente=" + cliente);
                    return;
                } else {
                    // Redirect to facturas
                    response.sendRedirect("facturas?cliente=" + cliente);
                    return;
                }
            } else {
                request.setAttribute("error", "Hubo problemas en algunas localidades: " + msgs.toString());
                doGet(request, response);
            }
        }
    }
}
