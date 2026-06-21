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

@WebServlet(name = "MasupController", urlPatterns = {"/masup"})
public class MasupController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener Partidos
        String envP = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                + "<soapenv:Header/><soapenv:Body><ws:listarPartidosDisponibles/></soapenv:Body></soapenv:Envelope>";
        try {
            String resP = SoapHelper.callSoap("WSFederacion", "", envP);
            List<Map<String, String>> partidos = XmlParser.parseList(resP, "partido");
            request.setAttribute("partidos", partidos);
        } catch (Exception e) {
            request.setAttribute("error", "Error cargando partidos: " + e.getMessage());
        }

        // 2. Si hay un partido seleccionado, obtener localidades
        String pId = request.getParameter("partido");
        if (pId != null && !pId.isEmpty()) {
            request.setAttribute("selectedPartido", pId);
            String envL = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                    + "<soapenv:Header/><soapenv:Body><ws:listarLocalidadesDisponibles>"
                    + "<codigoPartido>" + pId + "</codigoPartido>"
                    + "</ws:listarLocalidadesDisponibles></soapenv:Body></soapenv:Envelope>";
                    
            String envO = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                    + "<soapenv:Header/><soapenv:Body><ws:obtenerAsientosOcupados>"
                    + "<codigoPartido>" + pId + "</codigoPartido>"
                    + "</ws:obtenerAsientosOcupados></soapenv:Body></soapenv:Envelope>";
            try {
                String resL = SoapHelper.callSoap("WSFederacion", "", envL);
                List<Map<String, String>> localidades = XmlParser.parseList(resL, "localidad");
                request.setAttribute("localidades", localidades);
                
                String resO = SoapHelper.callSoap("WSFederacion", "", envO);
                List<Map<String, String>> ocupados = XmlParser.parseList(resO, "asientoOcupado");
                request.setAttribute("ocupados", ocupados);
            } catch (Exception e) {
                request.setAttribute("error", "Error cargando datos: " + e.getMessage());
            }
        }

        request.getRequestDispatcher("/views/masup.jsp").forward(request, response);
    }
}
