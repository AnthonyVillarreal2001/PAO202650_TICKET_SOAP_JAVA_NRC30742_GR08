package ec.edu.monster.controller;

import ec.edu.monster.view.TicketLoginView;
import ec.edu.monster.view.TicketMenuView;

public class TicketLoginController {
  private final TicketLoginView view;

  public TicketLoginController(TicketLoginView view) {
    this.view = view;
    bind();
  }

  private void bind() {
    view.onLogin((usuario, password) -> {
      if (usuario == null || usuario.trim().isEmpty() || password == null || password.trim().isEmpty()) {
        view.showError("Ingresa un usuario y contraseña válidos para continuar.");
        return;
      }

      TicketSession.usuario = usuario.trim();
      view.dispose();
      new TicketMenuController(new TicketMenuView(), TicketSession.usuario).show();
    });
  }

  public void show() {
    view.setVisible(true);
  }
}