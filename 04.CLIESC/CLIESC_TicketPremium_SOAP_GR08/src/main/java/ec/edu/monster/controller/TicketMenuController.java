package ec.edu.monster.controller;

import ec.edu.monster.model.CompraResultado;
import ec.edu.monster.model.LocalidadPartido;
import ec.edu.monster.model.PartidoFutbol;
import ec.edu.monster.model.ResumenVentaLocalidad;
import ec.edu.monster.model.VentaRegistro;
import ec.edu.monster.service.TicketWebClient;
import ec.edu.monster.view.CompraDialog;
import ec.edu.monster.view.LocalidadesDialog;
import ec.edu.monster.view.ReporteDialog;
import ec.edu.monster.view.TicketMenuView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TicketMenuController {
  private final TicketMenuView view;
  private final String usuario;
  private List<PartidoFutbol> partidos = new ArrayList<>();

  public TicketMenuController(TicketMenuView view, String usuario) {
    this.view = view;
    this.usuario = usuario;
    bind();
    loadPartidos();
  }

  private void bind() {
    view.onSalir(() -> {
      TicketSession.reset();
      view.dispose();
      new TicketLoginController(new ec.edu.monster.view.TicketLoginView()).show();
    });

    view.onVerLocalidades(index -> {
      if (index < 0 || index >= partidos.size()) {
        view.showError("Selecciona un partido válido.");
        return;
      }
      openLocalidades(partidos.get(index));
    });

    view.onReporte(index -> {
      if (index < 0 || index >= partidos.size()) {
        view.showError("Selecciona un partido válido.");
        return;
      }
      openReporte(partidos.get(index));
    });
  }

  private void loadPartidos() {
    try {
      partidos = TicketWebClient.listarPartidosDisponibles();
      view.setUsuario(usuario);
      view.setPartidos(partidos);
    } catch (Exception ex) {
      view.showError("No se pudieron cargar los partidos: " + ex.getMessage());
      view.setPartidos(new ArrayList<>());
    }
  }

  private void openLocalidades(PartidoFutbol partido) {
    try {
      List<LocalidadPartido> localidades = TicketWebClient.listarLocalidadesDisponibles(partido.getCodigo());
      final LocalidadesDialog[] dialogRef = new LocalidadesDialog[1];
      dialogRef[0] = new LocalidadesDialog(view, partido, localidades, usuario, (localidad, cantidad) -> {
        try {
          CompraResultado resultado = TicketWebClient.comprarBoleto(partido.getCodigo(), localidad.getCodigoLocalidad(), cantidad, usuario);
          if (resultado.getEstado() == 1) {
            double subtotal = localidad.getPrecio() * cantidad;
            double iva = subtotal * 0.12;
            double total = subtotal + iva;

            CompraDialog resultDialog = new CompraDialog(view, true, resultado.getFacturaId(), usuario, formatPartido(partido), localidad.getCodigoLocalidad(), cantidad, subtotal, iva, total, () -> {
              dialogRef[0].dispose();
              view.toFront();
            }, () -> openReporte(partido));
            resultDialog.setVisible(true);
          } else {
            CompraDialog resultDialog = new CompraDialog(view, false, 0L, usuario, formatPartido(partido), localidad.getCodigoLocalidad(), cantidad, 0d, 0d, 0d, () -> dialogRef[0].dispose(), () -> openReporte(partido));
            resultDialog.setErrorMessage(resultado.getMensaje());
            resultDialog.setVisible(true);
          }
        } catch (Exception ex) {
          view.showError("Error en la compra: " + ex.getMessage());
        }
      });
      dialogRef[0].setVisible(true);
    } catch (Exception ex) {
      view.showError("No se pudieron cargar las localidades: " + ex.getMessage());
    }
  }

  private void openReporte(PartidoFutbol partido) {
    try {
      String codigoPartido = (partido != null && partido.getCodigo() != null) ? partido.getCodigo() : "";
      List<ResumenVentaLocalidad> resumen = TicketWebClient.listarResumenVentas(codigoPartido);
      new ReporteDialog(view, partido, resumen).setVisible(true);
    } catch (Exception ex) {
      view.showError("No se pudo obtener el reporte: " + ex.getMessage());
    }
  }

  private String formatPartido(PartidoFutbol partido) {
    return partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante();
  }

  public void show() {
    view.setVisible(true);
  }
}