package ec.edu.monster.view;

import ec.edu.monster.model.PartidoFutbol;
import ec.edu.monster.model.ResumenVentaLocalidad;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ReporteDialog extends JDialog {
  public ReporteDialog(java.awt.Window owner, PartidoFutbol partido, List<ResumenVentaLocalidad> resumen) {
    super(owner, "TicketPremium - Reporte", ModalityType.APPLICATION_MODAL);
    setMinimumSize(new Dimension(820, 560));
    setLocationRelativeTo(owner);
    setLayout(new BorderLayout());

    JPanel root = new JPanel(new BorderLayout(0, 16));
    root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

    JPanel header = new JPanel(new BorderLayout());
    JLabel title = new JLabel("Resumen de ventas");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
    JLabel subtitle = new JLabel(partido != null ? partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante() : "Todos los partidos");
    subtitle.setForeground(new Color(90, 90, 90));
    header.add(title, BorderLayout.NORTH);
    header.add(subtitle, BorderLayout.SOUTH);

    JPanel list = new JPanel();
    list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
    List<ResumenVentaLocalidad> safe = resumen == null ? new ArrayList<>() : resumen;
    if (safe.isEmpty()) {
      list.add(new JLabel("No hay ventas registradas para mostrar."));
    } else {
      for (ResumenVentaLocalidad item : safe) {
        list.add(buildCard(item));
        list.add(javax.swing.Box.createVerticalStrut(10));
      }
    }

    JScrollPane scroll = new JScrollPane(list);
    scroll.setBorder(BorderFactory.createEmptyBorder());

    JButton close = new JButton("Cerrar");
    close.addActionListener(e -> dispose());
    JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    footer.add(close);

    root.add(header, BorderLayout.NORTH);
    root.add(scroll, BorderLayout.CENTER);
    root.add(footer, BorderLayout.SOUTH);
    add(root, BorderLayout.CENTER);
  }

  private JPanel buildCard(ResumenVentaLocalidad item) {
    JPanel card = new JPanel(new GridBagLayout());
    card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), BorderFactory.createEmptyBorder(14, 16, 14, 16)));
    card.setBackground(Color.WHITE);

    GridBagConstraints gc = new GridBagConstraints();
    gc.gridx = 0;
    gc.gridy = 0;
    gc.anchor = GridBagConstraints.WEST;
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.weightx = 1;

    JLabel codigo = new JLabel(item.getCodigoLocalidad());
    codigo.setFont(codigo.getFont().deriveFont(Font.BOLD, 18f));
    card.add(codigo, gc);

    gc.gridy++;
    card.add(new JLabel("Vendidos: " + item.getVendidos()), gc);

    gc.gridy++;
    card.add(new JLabel("Total recaudado: $" + String.format(java.util.Locale.US, "%.2f", item.getTotalRecaudado())), gc);

    return card;
  }
}