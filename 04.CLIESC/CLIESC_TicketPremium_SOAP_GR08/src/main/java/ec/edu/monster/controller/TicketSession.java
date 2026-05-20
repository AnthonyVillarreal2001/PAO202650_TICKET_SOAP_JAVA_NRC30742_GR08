package ec.edu.monster.controller;

import ec.edu.monster.model.VentaRegistro;
import java.util.ArrayList;
import java.util.List;

public final class TicketSession {
  private TicketSession() {
  }

  public static String usuario;
  public static final List<VentaRegistro> ventas = new ArrayList<>();

  public static void reset() {
    usuario = null;
    synchronized (ventas) {
      ventas.clear();
    }
  }
}