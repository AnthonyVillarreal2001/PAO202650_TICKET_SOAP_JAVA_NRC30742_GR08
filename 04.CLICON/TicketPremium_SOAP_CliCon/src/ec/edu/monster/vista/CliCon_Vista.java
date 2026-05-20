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
                System.out.println("4. Comprar boleto");
                System.out.println("5. Ver reporte de ventas");
                System.out.println("6. Cerrar sesión");
                System.out.println("7. Salir");
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
                    case 4 -> comprarBoleto(scanner, controlador, partidoSeleccionado, usuario);
                    case 5 -> mostrarReporte(controlador, partidoSeleccionado);
                    case 6 -> {
                        System.out.println("\nCerrando sesión...");
                        sesionActiva = false;
                    }
                    case 7 -> {
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

    private static void comprarBoleto(Scanner scanner, CliCon_Controlador controlador, PartidoFutbol partidoSeleccionado, String cliente) {
        if (partidoSeleccionado == null) {
            System.out.println("\033[31mPrimero selecciona un partido.\033[0m");
            return;
        }

        List<LocalidadPartido> localidades = controlador.listarLocalidadesDisponibles(partidoSeleccionado.getCodigo());
        if (localidades == null || localidades.isEmpty()) {
            System.out.println("\033[31mNo hay localidades disponibles para este partido.\033[0m");
            return;
        }

        mostrarLocalidades(scanner, controlador, partidoSeleccionado);

        System.out.print("\nIngrese el código de la localidad: ");
        String codigoLocalidad = scanner.nextLine().trim();
        LocalidadPartido localidadSeleccionada = null;
        for (LocalidadPartido localidad : localidades) {
            if (localidad.getCodigoLocalidad() != null && localidad.getCodigoLocalidad().equalsIgnoreCase(codigoLocalidad)) {
                localidadSeleccionada = localidad;
                break;
            }
        }

        if (localidadSeleccionada == null) {
            System.out.println("\033[31mNo se encontró la localidad indicada.\033[0m");
            return;
        }

        System.out.print("Ingrese la cantidad: ");
        int cantidad;
        try {
            cantidad = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("\033[31mCantidad no válida.\033[0m");
            return;
        }

        if (cantidad <= 0 || cantidad > localidadSeleccionada.getDisponibilidad()) {
            System.out.println("\033[31mCantidad fuera de rango.\033[0m");
            return;
        }

        CompraResultado resultado = controlador.comprarBoleto(partidoSeleccionado.getCodigo(), codigoLocalidad, cantidad, cliente);
        if (resultado.getEstado() == 1) {
            double subtotal = localidadSeleccionada.getPrecio() * cantidad;
            double iva = subtotal * 0.12;
            double total = subtotal + iva;
            System.out.println("\n\033[32mCompra realizada\033[0m");
            System.out.println("Factura: " + resultado.getFacturaId());
            System.out.println("Cliente: " + cliente);
            System.out.println("Partido: " + partidoSeleccionado.getEquipoLocal() + " vs " + partidoSeleccionado.getEquipoVisitante());
            System.out.println("Localidad: " + localidadSeleccionada.getCodigoLocalidad());
            System.out.println("Cantidad: " + cantidad);
            System.out.printf("Subtotal: $%.2f%n", subtotal);
            System.out.printf("IVA (12%%): $%.2f%n", iva);
            System.out.printf("Total: $%.2f%n", total);
        } else {
            System.out.println("\033[31m" + (resultado.getMensaje() != null ? resultado.getMensaje() : "No se pudo completar la compra.") + "\033[0m");
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
            String p = password.trim().toUpperCase();

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
