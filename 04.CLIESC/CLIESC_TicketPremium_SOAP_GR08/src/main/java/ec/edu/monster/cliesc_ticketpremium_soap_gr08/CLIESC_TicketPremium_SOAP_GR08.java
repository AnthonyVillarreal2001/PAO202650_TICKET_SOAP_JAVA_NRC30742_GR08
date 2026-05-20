package ec.edu.monster.cliesc_TicketPremium_soap_GR08;

import com.formdev.flatlaf.FlatLightLaf;
import ec.edu.monster.controller.TicketLoginController;
import ec.edu.monster.view.TicketLoginView;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class CLIESC_TicketPremium_SOAP_GR08 {

  public static void main(String[] args) {
    FlatLightLaf.setup();
    UIManager.put("Component.arc", 20);
    UIManager.put("Button.arc", 22);
    UIManager.put("TextComponent.arc", 20);
    UIManager.put("Component.focusWidth", 2);
    UIManager.put("ScrollBar.showButtons", false);
    UIManager.put("TitlePane.unifiedBackground", true);

    SwingUtilities.invokeLater(() -> new TicketLoginController(new TicketLoginView()).show());
  }
}
