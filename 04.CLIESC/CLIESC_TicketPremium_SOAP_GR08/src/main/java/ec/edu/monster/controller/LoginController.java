package ec.edu.monster.controller;

import ec.edu.monster.service.TicketPremiumApi;
import ec.edu.monster.service.SoapTicketPremiumApi;
import ec.edu.monster.view.HomeView;
import ec.edu.monster.view.LoginView;

public class LoginController {
  private final LoginView view;
  private final TicketPremiumApi api;

  public LoginController(LoginView view) {
    this.view = view;
    this.api  = new SoapTicketPremiumApi();
    bind();
  }

  private void bind() {
    this.view.onLogin((u,p) -> {
      try {
        boolean ok = api.validarIngreso(u, p);
        if (!ok) { view.showError("Credenciales inválidas."); return; }
        Session.usuario = u;
        view.dispose();
        new HomeController(new HomeView(), api).show();
      } catch (Exception ex) {
        view.showError("Error conectando al servicio: " + ex.getMessage());
      }
    });
  }

  /** Mostrar la pantalla de login */
  public void show() {
    view.setVisible(true);
  }
}
