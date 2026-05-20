package ec.edu.monster.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CompraDialog extends JDialog {
  private final JLabel lblMessage = new JLabel();

  public CompraDialog(java.awt.Window owner, boolean exito, long facturaId, String cliente, String partidoDescripcion, String localidadDescripcion, int cantidad, double subtotal, double iva, double total, Runnable onGoMenu, Runnable onGoReporte) {
    super(owner, "TicketPremium - Compra", ModalityType.APPLICATION_MODAL);
    setMinimumSize(new Dimension(760, 520));
    setLocationRelativeTo(owner);
    setLayout(new BorderLayout());

    JPanel root = new JPanel(new BorderLayout(0, 18));
    root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

    JPanel icon = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel mark = new JLabel(exito ? "✓" : "✕", SwingConstants.CENTER);
    mark.setOpaque(true);
    mark.setForeground(Color.WHITE);
    mark.setBackground(exito ? new Color(39, 174, 96) : new Color(192, 57, 43));
    mark.setFont(mark.getFont().deriveFont(Font.BOLD, 28f));
    mark.setPreferredSize(new Dimension(64, 64));
    icon.add(mark);

    JLabel title = new JLabel(exito ? "Compra realizada" : "Error en la compra", SwingConstants.CENTER);
    title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

    JPanel summary = new JPanel(new GridLayout(0, 1, 0, 8));
    summary.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)), BorderFactory.createEmptyBorder(16, 20, 16, 20)));
    summary.setBackground(Color.WHITE);

    if (exito) {
      summary.add(new JLabel("Factura: " + facturaId));
      summary.add(new JLabel("Cliente: " + safe(cliente)));
      summary.add(new JLabel("Partido: " + safe(partidoDescripcion)));
      summary.add(new JLabel("Localidad: " + safe(localidadDescripcion)));
      summary.add(new JLabel("Cantidad: " + cantidad));
      summary.add(new JLabel("Subtotal: $" + fmt(subtotal)));
      summary.add(new JLabel("IVA (12%): $" + fmt(iva)));
      summary.add(new JLabel("Total: $" + fmt(total)));
    } else {
      lblMessage.setForeground(new Color(192, 57, 43));
      summary.add(lblMessage);
    }

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    JButton menu = new JButton("Ir al menú");
    menu.addActionListener(e -> {
      dispose();
      if (onGoMenu != null) {
        onGoMenu.run();
      }
    });
    JButton reporte = new JButton("Ver reporte");
    reporte.addActionListener(e -> {
      if (onGoReporte != null) {
        onGoReporte.run();
      }
    });
    buttons.add(menu);
    buttons.add(reporte);

    root.add(icon, BorderLayout.NORTH);
    root.add(title, BorderLayout.CENTER);
    root.add(summary, BorderLayout.SOUTH);

    add(root, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  public void setErrorMessage(String message) {
    lblMessage.setText(message == null || message.isBlank() ? "No se pudo completar la operación" : message);
  }

  private String fmt(double value) {
    return String.format(java.util.Locale.US, "%.2f", value);
  }

  private String safe(String value) {
    return value == null ? "" : value;
  }
}