/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ec.edu.monster.vista;

import ec.edu.monster.controlador.CliCon_Controlador;
import ec.edu.monster.modelo.CompraResultado;
import ec.edu.monster.modelo.LocalidadPartido;
import ec.edu.monster.modelo.PartidoFutbol;
import ec.edu.monster.modelo.ResumenVentaLocalidad;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author JOIS
 */
public class CliCon_Vista {

    private static final String USUARIO = "MONSTER";
    private static final String PASS = "MONSTER9";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        CliCon_Controlador controlador = new CliCon_Controlador();
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           T I C K E T P R E M I U M                        ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");

        while (true) {
            System.out.println("\n--------------------");
            System.out.println("     BIENVENIDO     ");
            System.out.println("--------------------");

            System.out.print("\nIngrese su usuario: ");
            String usuario = scanner.nextLine().trim();
            System.out.print("Ingrese su contraseña: ");
            String password = scanner.nextLine().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                System.out.println("\033[31mIngresa un usuario y contraseña válidos para continuar.\033[0m");
                continue;
            }

            // Authenticate against server WSTicketPremium.validarIngreso
            boolean autenticado = validarIngresoSoap(usuario, password);
            if (!autenticado) {
                System.out.println("\033[31mCredenciales incorrectas. Intenta nuevamente.\033[0m");
                continue;
            }

            System.out.println("\033[32mAcceso exitoso\033[0m");

            List<PartidoFutbol> partidos = controlador.listarPartidosDisponibles();
            PartidoFutbol partidoSeleccionado = null;

            boolean sesionActiva = true;
            while (sesionActiva) {
                System.out.println("\n==========================");
                System.out.println("       MENÚ PRINCIPAL     ");
                System.out.println("==========================");
                System.out.println("1. Ver partidos disponibles");
                System.out.println("2. Seleccionar partido");
                System.out.println("3. Ver localidades del partido seleccionado");
                System.out.println("4. Comprar boleto (MASHUP)");
                System.out.println("5. Ver reporte de ventas");
                System.out.println("6. Mis Facturas");
                System.out.println("7. Mis Amortizaciones");
                System.out.println("8. Cerrar sesión");
                System.out.println("9. Salir");
                System.out.print("\nSeleccione una opción: ");

                int opcion;
                try {
                    opcion = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException ex) {
                    System.out.println("\033[31mOpción no válida. Intenta nuevamente.\033[0m");
                    continue;
                }

                switch (opcion) {
                    case 1 -> mostrarPartidos(partidos);
                    case 2 -> partidoSeleccionado = seleccionarPartido(scanner, partidos);
                    case 3 -> mostrarLocalidades(scanner, controlador, partidoSeleccionado);
                    case 4 -> comprarBoletoMashup(scanner, controlador, partidoSeleccionado, usuario);
                    case 5 -> mostrarReporte(controlador, partidoSeleccionado);
                    case 6 -> mostrarFacturas(scanner, controlador);
                    case 7 -> mostrarAmortizaciones(scanner, controlador);
                    case 8 -> {
                        System.out.println("\nCerrando sesión...");
                        sesionActiva = false;
                    }
                    case 9 -> {
                        System.out.println("\nSaliendo del sistema. ¡Gracias!");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("\033[31mOpción no válida. Intenta nuevamente.\033[0m");
                }
            }
        }
    }

    private static void mostrarPartidos(List<PartidoFutbol> partidos) {
        System.out.println("\n============================================================");
        System.out.printf("%-8s %-28s %-20s %-18s%n", "Código", "Partido", "Fecha", "Lugar");
        System.out.println("============================================================");
        if (partidos == null || partidos.isEmpty()) {
            System.out.println("No hay partidos disponibles en este momento.");
            return;
        }
        for (int i = 0; i < partidos.size(); i++) {
            PartidoFutbol partido = partidos.get(i);
            System.out.printf("%-8s %-28s %-20s %-18s%n",
                    partido.getCodigo(),
                    partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante(),
                    partido.getFecha(),
                    partido.getLugar());
        }
    }

    private static PartidoFutbol seleccionarPartido(Scanner scanner, List<PartidoFutbol> partidos) {
        if (partidos == null || partidos.isEmpty()) {
            System.out.println("\033[31mNo hay partidos para seleccionar.\033[0m");
            return null;
        }
        mostrarPartidos(partidos);
        System.out.print("\nIngrese el código del partido: ");
        String codigo = scanner.nextLine().trim();
        for (PartidoFutbol partido : partidos) {
            if (partido.getCodigo() != null && partido.getCodigo().equalsIgnoreCase(codigo)) {
                System.out.println("\033[32mPartido seleccionado: " + partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante() + "\033[0m");
                return partido;
            }
        }
        System.out.println("\033[31mNo se encontró el partido indicado.\033[0m");
        return null;
    }

    private static void mostrarLocalidades(Scanner scanner, CliCon_Controlador controlador, PartidoFutbol partidoSeleccionado) {
        if (partidoSeleccionado == null) {
            System.out.println("\033[31mPrimero selecciona un partido.\033[0m");
            return;
        }
        List<LocalidadPartido> localidades = controlador.listarLocalidadesDisponibles(partidoSeleccionado.getCodigo());
        System.out.println("\n============================================================");
        System.out.println(partidoSeleccionado.getEquipoLocal() + " vs " + partidoSeleccionado.getEquipoVisitante());
        System.out.println("============================================================");
        if (localidades == null || localidades.isEmpty()) {
            System.out.println("No hay localidades disponibles para este partido.");
            return;
        }
        System.out.printf("%-18s %-16s %-14s%n", "Localidad", "Disponibilidad", "Precio");
        for (LocalidadPartido localidad : localidades) {
            System.out.printf("%-18s %-16d $%-13.2f%n",
                    localidad.getCodigoLocalidad(),
                    localidad.getDisponibilidad(),
                    localidad.getPrecio());
        }
    }

    private static void comprarBoletoMashup(Scanner scanner, CliCon_Controlador controlador, PartidoFutbol partidoSeleccionado, String vendedor) {
        if (partidoSeleccionado == null) {
            System.out.println("\033[31mPrimero selecciona un partido.\033[0m");
            return;
        }

        List<LocalidadPartido> localidades = controlador.listarLocalidadesDisponibles(partidoSeleccionado.getCodigo());
        if (localidades == null || localidades.isEmpty()) {
            System.out.println("\033[31mNo hay localidades disponibles para este partido.\033[0m");
            return;
        }

        java.util.Map<String, java.util.List<Integer>> carrito = new java.util.HashMap<>();
        for (LocalidadPartido l : localidades) carrito.put(l.getCodigoLocalidad(), new java.util.ArrayList<>());

        boolean comprando = true;

        while (comprando) {
            List<ec.edu.monster.modelo.AsientoOcupado> ocupados = controlador.obtenerAsientosOcupados(partidoSeleccionado.getCodigo());
            
            System.out.println("\n============================================================");
            System.out.println("                 M A S H U P   A S C I I");
            System.out.println("============================================================");
            
            String locNorte = "NORTE"; String locSur = "SUR"; String locEste = "ESTE"; String locOeste = "OESTE";
            for (LocalidadPartido l : localidades) {
                if (l.getCodigoLocalidad().toUpperCase().contains("GEN")) locNorte = l.getCodigoLocalidad();
                if (l.getCodigoLocalidad().toUpperCase().contains("GVI")) locSur = l.getCodigoLocalidad();
                if (l.getCodigoLocalidad().toUpperCase().contains("PAL")) locEste = l.getCodigoLocalidad();
                if (l.getCodigoLocalidad().toUpperCase().contains("TRI")) locOeste = l.getCodigoLocalidad();
            }

            int nOcup = 0, sOcup = 0, eOcup = 0, oOcup = 0;
            for (ec.edu.monster.modelo.AsientoOcupado a : ocupados) {
                if (a.getLoc().equalsIgnoreCase(locNorte)) nOcup += a.getCant();
                if (a.getLoc().equalsIgnoreCase(locSur)) sOcup += a.getCant();
                if (a.getLoc().equalsIgnoreCase(locEste)) eOcup += a.getCant();
                if (a.getLoc().equalsIgnoreCase(locOeste)) oOcup += a.getCant();
            }

            System.out.println("                  [ NORTE / " + locNorte + " ]");
            System.out.println("                  Ocupados: " + nOcup + " | En Carrito: " + carrito.get(locNorte).size());
            System.out.println("       +----------------------------------+");
            System.out.println(" [ O ] |                                  | [ E ]");
            System.out.println(" [ E ] |                                  | [ S ]");
            System.out.println(" [ S ] |              CANCHA              | [ T ]");
            System.out.println(" [ T ] |                                  | [ E ]");
            System.out.println(" [ E ] |                                  | ");
            System.out.println("       +----------------------------------+");
            System.out.println("                  [ SUR / " + locSur + " ]");
            System.out.println("                  Ocupados: " + sOcup + " | En Carrito: " + carrito.get(locSur).size());
            System.out.println();
            System.out.println("OESTE / " + locOeste + " -> Ocupados: " + oOcup + " | En Carrito: " + carrito.get(locOeste).size());
            System.out.println("ESTE / " + locEste + " -> Ocupados: " + eOcup + " | En Carrito: " + carrito.get(locEste).size());

            System.out.println("\nOpciones:");
            System.out.println("1. Ver Asientos de una Localidad");
            System.out.println("2. Ver Resumen y Pagar");
            System.out.println("3. Salir del Mashup");
            System.out.print("Opción (o digite el código ej. " + locNorte + "): ");
            String input = scanner.nextLine().trim();

            if (input.equals("3")) {
                comprando = false;
            } else if (input.equals("2")) {
                int totalCar = 0; for(java.util.List<Integer> list : carrito.values()) totalCar += list.size();
                if (totalCar == 0) {
                    System.out.println("\033[31mEl carrito está vacío.\033[0m");
                } else {
                    procesarPago(scanner, controlador, partidoSeleccionado, vendedor, carrito, localidades);
                    comprando = false;
                }
            } else {
                String codLoc = input;
                boolean matched = false;
                for (String key : carrito.keySet()) {
                    if (key.equalsIgnoreCase(input)) {
                        codLoc = key;
                        matched = true;
                        break;
                    }
                }
                
                if (input.equals("1") || matched) {
                    if (input.equals("1")) {
                        System.out.print("Ingrese el código exacto de la localidad (ej. " + locNorte + "): ");
                        codLoc = scanner.nextLine().trim();
                    }
                    
                    LocalidadPartido seleccionada = null;
                    for (LocalidadPartido l : localidades) {
                        if (l.getCodigoLocalidad().equalsIgnoreCase(codLoc)) seleccionada = l;
                    }
                
                if (seleccionada == null) {
                    System.out.println("\033[31mCódigo inválido.\033[0m");
                    continue;
                }

                int ocupLoc = 0;
                for (ec.edu.monster.modelo.AsientoOcupado a : ocupados) {
                    if (a.getLoc().equalsIgnoreCase(codLoc)) ocupLoc += a.getCant();
                }

                int totalSeats = ocupLoc + seleccionada.getDisponibilidad();
                boolean subMenu = true;
                while (subMenu) {
                    System.out.println("\n--- MAPA DE ASIENTOS: " + codLoc + " ---");
                    int col = 0;
                    for (int i = 1; i <= totalSeats; i++) {
                        if (i <= ocupLoc) {
                            System.out.print("\033[31m[ X ]\033[0m ");
                        } else {
                            if (carrito.get(codLoc).contains(i)) {
                                System.out.print("\033[33m[" + String.format("%3d", i) + "]\033[0m ");
                            } else {
                                System.out.print("\033[32m[" + String.format("%3d", i) + "]\033[0m ");
                            }
                        }
                        col++;
                        if (col == 20) { System.out.println(); col = 0; }
                    }
                    if (col != 0) System.out.println();
                    System.out.println("\n\033[31m[ X ]\033[0m Ocupado | \033[32m[ N ]\033[0m Libre | \033[33m[ N ]\033[0m En Carrito");
                    
                    System.out.print("\nIngrese Número de Asiento para Seleccionar/Deseleccionar (o '0' para volver): ");
                    try {
                        int seatNum = Integer.parseInt(scanner.nextLine().trim());
                        if (seatNum == 0) {
                            subMenu = false;
                        } else if (seatNum < 1 || seatNum > totalSeats) {
                            System.out.println("\033[31mAsiento fuera de rango.\033[0m");
                        } else if (seatNum <= ocupLoc) {
                            System.out.println("\033[31mEste asiento ya está vendido.\033[0m");
                        } else {
                            if (carrito.get(codLoc).contains(seatNum)) {
                                carrito.get(codLoc).remove(Integer.valueOf(seatNum));
                                System.out.println("\033[33mAsiento removido del carrito.\033[0m");
                            } else {
                                carrito.get(codLoc).add(seatNum);
                                System.out.println("\033[32mAsiento añadido al carrito.\033[0m");
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\033[31mNúmero inválido.\033[0m");
                    }
                }
            } else {
                System.out.println("\033[31mOpción inválida.\033[0m");
            }
            }
        }
    }

    private static void procesarPago(Scanner scanner, CliCon_Controlador controlador, PartidoFutbol partido, String vendedor, java.util.Map<String, java.util.List<Integer>> carrito, List<LocalidadPartido> localidades) {
        double subtotal = 0;
        int totalTickets = 0;
        System.out.println("\n--- Resumen de Compra ---");
        for (java.util.Map.Entry<String, java.util.List<Integer>> entry : carrito.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            double precio = 0;
            for (LocalidadPartido l : localidades) {
                if (l.getCodigoLocalidad().equalsIgnoreCase(entry.getKey())) precio = l.getPrecio();
            }
            double sub = precio * entry.getValue().size();
            subtotal += sub;
            totalTickets += entry.getValue().size();
            System.out.println(entry.getKey() + " x " + entry.getValue().size() + " = $" + sub + " (Asientos: " + entry.getValue() + ")");
        }
        double iva = subtotal * 0.12;
        double totalCalc = subtotal + iva;
        System.out.printf("Total (inc. IVA 12%%): $%.2f%n", totalCalc);

        System.out.println("\nMétodo de Pago:");
        System.out.println("1. EFECTIVO");
        System.out.println("2. CREDITO");
        System.out.print("Seleccione: ");
        String metodo = scanner.nextLine().trim();

        if (metodo.equals("1")) {
            double descuento = totalCalc * 0.12;
            totalCalc -= descuento;
            System.out.printf("\033[32mDescuento 12%% Efectivo aplicado. Total final: $%.2f\033[0m%n", totalCalc);
        }

        List<ec.edu.monster.modelo.Cliente> clientes = controlador.listarClientes();
        System.out.println("\nSeleccione un cliente:");
        for (int i = 0; i < clientes.size(); i++) {
            ec.edu.monster.modelo.Cliente c = clientes.get(i);
            if (metodo.equals("2") && !c.isAptoCredito()) continue;
            System.out.println((i + 1) + ". " + c.getNombres() + " " + c.getApellidos() + " [" + c.getIdCliente() + "]");
        }
        System.out.print("Índice del cliente: ");
        int cIndex = -1;
        try { cIndex = Integer.parseInt(scanner.nextLine().trim()) - 1; } catch(Exception e){}
        if (cIndex < 0 || cIndex >= clientes.size() || (metodo.equals("2") && !clientes.get(cIndex).isAptoCredito())) {
            System.out.println("\033[31mCliente inválido o no apto para crédito.\033[0m");
            return;
        }
        ec.edu.monster.modelo.Cliente clienteSeleccionado = clientes.get(cIndex);

        int plazoMeses = 0;
        if (metodo.equals("2")) {
            System.out.print("Ingrese plazo en meses (3, 6, 9, 12): ");
            try { plazoMeses = Integer.parseInt(scanner.nextLine().trim()); } catch(Exception e){}
            if (plazoMeses <= 0) {
                System.out.println("\033[31mPlazo inválido.\033[0m"); return;
            }
        }

        System.out.println("\nProcesando compra...");
        
        // Single ticket emulation for multiple items (since CLIWEB handles it by sending parallel tickets or a multiple endpoint)
        // Here we just loop or send the first (if we don't have multiple endpoint configured). Wait, WSFederacion has comprarBoleto!
        // We will call comprarBoleto for each item in the cart or use a loop. For simplicity, just call buying one by one.
        // Wait, TicketPremiumService ONLY has comprarBoleto (single). 
        // I will use a loop, and accumulate facturas.
        
        for (java.util.Map.Entry<String, java.util.List<Integer>> entry : carrito.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            CompraResultado res = controlador.comprarBoleto(partido.getCodigo(), entry.getKey(), entry.getValue().size(), clienteSeleccionado.getIdCliente());
            if (res.getEstado() == 1) {
                System.out.println("\033[32mCompra exitosa de " + entry.getValue().size() + " boletos en " + entry.getKey() + "\033[0m");
                System.out.println("Factura ID generada: " + res.getFacturaId());
                if (metodo.equals("2")) {
                    double valor = 0;
                    for(LocalidadPartido l : localidades) if (l.getCodigoLocalidad().equals(entry.getKey())) valor = l.getPrecio() * entry.getValue().size();
                    boolean amortOk = controlador.guardarAmortizaciones(res.getFacturaId(), clienteSeleccionado.getIdCliente(), valor, plazoMeses);
                    if (amortOk) System.out.println("\033[32mTabla de amortización generada exitosamente.\033[0m");
                    else System.out.println("\033[31mError generando amortización.\033[0m");
                }
            } else {
                System.out.println("\033[31mError en compra: " + res.getMensaje() + "\033[0m");
            }
        }
    }

    private static void mostrarFacturas(Scanner scanner, CliCon_Controlador controlador) {
        List<ec.edu.monster.modelo.Cliente> clientes = controlador.listarClientes();
        System.out.println("\nSeleccione un cliente para ver facturas:");
        for (int i = 0; i < clientes.size(); i++) {
            System.out.println((i + 1) + ". " + clientes.get(i).getNombres() + " " + clientes.get(i).getApellidos() + " [" + clientes.get(i).getIdCliente() + "]");
        }
        System.out.print("Índice del cliente: ");
        int cIndex = -1;
        try { cIndex = Integer.parseInt(scanner.nextLine().trim()) - 1; } catch(Exception e){}
        if (cIndex < 0 || cIndex >= clientes.size()) {
            System.out.println("\033[31mCliente inválido.\033[0m"); return;
        }
        String idCliente = clientes.get(cIndex).getIdCliente();

        List<ec.edu.monster.modelo.Factura> facturas = controlador.listarFacturas(idCliente);
        if (facturas.isEmpty()) {
            System.out.println("No hay facturas para este cliente.");
            return;
        }

        System.out.println("\n--- Facturas de " + clientes.get(cIndex).getNombres() + " ---");
        for (ec.edu.monster.modelo.Factura f : facturas) {
            System.out.println("Factura ID: " + f.getIdFactura() + " | Fecha: " + f.getFechaFactura() + " | Total: $" + f.getTotalFactura());
        }

        System.out.print("\nIngrese ID de factura para ver detalles (o Enter para salir): ");
        String fIdStr = scanner.nextLine().trim();
        if (!fIdStr.isEmpty()) {
            try {
                long fId = Long.parseLong(fIdStr);
                List<ec.edu.monster.modelo.DetalleFactura> detalles = controlador.obtenerDetallesFactura(fId);
                System.out.println("\nDetalles de Factura " + fId + ":");
                for (ec.edu.monster.modelo.DetalleFactura d : detalles) {
                    System.out.println("- Localidad: " + d.getCodigoLocalidad() + " | Cant: " + d.getCantidad() + " | Precio U.: $" + d.getPrecioUnitario() + " | Total: $" + d.getTotalDetalle());
                }
            } catch (Exception e) {}
        }
    }

    private static void mostrarAmortizaciones(Scanner scanner, CliCon_Controlador controlador) {
        List<ec.edu.monster.modelo.Cliente> clientes = controlador.listarClientes();
        System.out.println("\nSeleccione un cliente para ver amortizaciones:");
        for (int i = 0; i < clientes.size(); i++) {
            System.out.println((i + 1) + ". " + clientes.get(i).getNombres() + " " + clientes.get(i).getApellidos() + " [" + clientes.get(i).getIdCliente() + "]");
        }
        System.out.print("Índice del cliente: ");
        int cIndex = -1;
        try { cIndex = Integer.parseInt(scanner.nextLine().trim()) - 1; } catch(Exception e){}
        if (cIndex < 0 || cIndex >= clientes.size()) {
            System.out.println("\033[31mCliente inválido.\033[0m"); return;
        }
        String idCliente = clientes.get(cIndex).getIdCliente();

        List<ec.edu.monster.modelo.Amortizacion> amort = controlador.obtenerAmortizaciones(idCliente);
        if (amort.isEmpty()) {
            System.out.println("No hay amortizaciones registradas para este cliente.");
            return;
        }

        System.out.println("\n======================================================================");
        System.out.println("                  TABLA DE AMORTIZACIÓN               ");
        System.out.println("======================================================================");
        System.out.printf("%-6s %-12s %-12s %-15s %-12s%n", "Mes", "Cuota", "Interés", "Amortización", "Saldo");
        System.out.println("----------------------------------------------------------------------");
        for (ec.edu.monster.modelo.Amortizacion a : amort) {
            System.out.printf("%-6d $%-11.2f $%-11.2f $%-14.2f $%-11.2f%n",
                    a.getMes(), a.getCuota(), a.getInteres(), a.getAmortizacion(), a.getSaldo());
        }
    }

    private static void mostrarReporte(CliCon_Controlador controlador, PartidoFutbol partidoSeleccionado) {
        List<ResumenVentaLocalidad> resumen = controlador.resumirVentas(partidoSeleccionado != null ? partidoSeleccionado.getCodigo() : null);
        System.out.println("\n============================================================");
        System.out.println("Resumen de ventas");
        System.out.println("============================================================");
        if (partidoSeleccionado != null) {
            System.out.println(partidoSeleccionado.getEquipoLocal() + " vs " + partidoSeleccionado.getEquipoVisitante());
        } else {
            System.out.println("Todos los partidos");
        }
        if (resumen == null || resumen.isEmpty()) {
            System.out.println("No hay ventas registradas para mostrar.");
            return;
        }
        System.out.printf("%-18s %-10s %-15s%n", "Localidad", "Vendidos", "Total recaudado");
        for (ResumenVentaLocalidad item : resumen) {
            System.out.printf("%-18s %-10d $%-14.2f%n",
                    item.getCodigoLocalidad(),
                    item.getVendidos(),
                    item.getTotalRecaudado());
        }
    }

    private static boolean validarIngresoSoap(String usuario, String password) {
        try {
            String u = usuario.trim().toUpperCase();
            String p = password.trim();

            String endpoint = "http://localhost:8080/WS_EurekaBank_Server/WSTicketPremium";

            String envelope = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                    + "<soapenv:Header/>"
                    + "<soapenv:Body>"
                    + "<ws:validarIngreso>"
                    + "<usuario>" + escapeXml(u) + "</usuario>"
                    + "<password>" + escapeXml(p) + "</password>"
                    + "</ws:validarIngreso>"
                    + "</soapenv:Body>"
                    + "</soapenv:Envelope>";

            boolean call = callSoapEndpoint(endpoint, envelope);
            if (call) return true;

            // Fallback for common typo MOSTER -> MONSTER
            if (u.equals("MOSTER")) {
                String envelope2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">"
                        + "<soapenv:Header/>"
                        + "<soapenv:Body>"
                        + "<ws:validarIngreso>"
                        + "<usuario>MONSTER</usuario>"
                        + "<password>" + escapeXml(p) + "</password>"
                        + "</ws:validarIngreso>"
                        + "</soapenv:Body>"
                        + "</soapenv:Envelope>";
                return callSoapEndpoint(endpoint, envelope2);
            }

            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    private static boolean callSoapEndpoint(String endpoint, String envelope) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction", "");

            try (OutputStream out = connection.getOutputStream()) {
                out.write(envelope.getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();
            InputStream input = status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream();
            if (input == null) return false;

            try (InputStream in = input; ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[2048];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    bout.write(buffer, 0, read);
                }
                String resp = new String(bout.toByteArray(), StandardCharsets.UTF_8);
                return resp.contains("Exitoso") || resp.contains("<return>Exitoso</return>");
            }
        } catch (Exception ex) {
            return false;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static String escapeXml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

}
