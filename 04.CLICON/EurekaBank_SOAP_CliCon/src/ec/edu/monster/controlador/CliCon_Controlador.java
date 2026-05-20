package ec.edu.monster.controlador;

import ec.edu.monster.servicio.TicketPremiumService;
import ec.edu.monster.modelo.CompraResultado;
import ec.edu.monster.modelo.LocalidadPartido;
import ec.edu.monster.modelo.PartidoFutbol;
import ec.edu.monster.modelo.ResumenVentaLocalidad;
import ec.edu.monster.modelo.VentaRegistro;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class CliCon_Controlador {

    private final TicketPremiumService service = new TicketPremiumService();

    public List<PartidoFutbol> listarPartidosDisponibles(){
        return service.listarPartidosDisponibles();
    }

    public List<LocalidadPartido> listarLocalidadesDisponibles(String codigoPartido){
        return service.listarLocalidadesDisponibles(codigoPartido);
    }

    public CompraResultado comprarBoleto(String codigoPartido, String codigoLocalidad, int cantidad, String cliente){
        return service.comprarBoleto(codigoPartido, codigoLocalidad, cantidad, cliente);
    }

    public List<ResumenVentaLocalidad> resumirVentas(String codigoPartido){
        return service.listarResumenVentas(codigoPartido);
    }
}
