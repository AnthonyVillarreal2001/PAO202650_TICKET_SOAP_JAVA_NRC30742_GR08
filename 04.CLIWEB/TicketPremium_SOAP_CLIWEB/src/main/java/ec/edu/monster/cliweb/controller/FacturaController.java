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

@WebServlet(name = "FacturaController", urlPatterns = {"/facturas"})
public class FacturaController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtener lista de clientes para el select
        String envCli = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                + "<soapenv:Header/><soapenv:Body><ws:listarClientes/></soapenv:Body></soapenv:Envelope>";
        try {
            String resCli = SoapHelper.callSoap("WSCRUD", "", envCli);
            List<Map<String, String>> listaClientes = XmlParser.parseList(resCli, "cliente");
            request.setAttribute("clientes", listaClientes);
        } catch (Exception e) {
            request.setAttribute("error", "Error cargando clientes: " + e.getMessage());
        }

        String idCliente = request.getParameter("cliente");
        if (idCliente != null && !idCliente.isEmpty()) {
            String envFact = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                    + "<soapenv:Header/><soapenv:Body><ws:listarFacturas><idCliente>" + idCliente + "</idCliente></ws:listarFacturas></soapenv:Body></soapenv:Envelope>";
            try {
                String resFact = SoapHelper.callSoap("WSFederacion", "", envFact);
                List<Map<String, String>> facturas = XmlParser.parseList(resFact, "factura");
                request.setAttribute("facturas", facturas);
                request.setAttribute("selectedCliente", idCliente);
            } catch (Exception e) {
                request.setAttribute("error", "Error cargando facturas: " + e.getMessage());
            }
        }

        request.getRequestDispatcher("/views/facturas.jsp").forward(request, response);
    }
}
