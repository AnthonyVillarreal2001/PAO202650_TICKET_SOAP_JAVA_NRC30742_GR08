package ec.edu.monster.view;

import ec.edu.monster.model.LocalidadPartido;
import ec.edu.monster.model.PartidoFutbol;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class LocalidadesDialog extends JDialog {
  public LocalidadesDialog(java.awt.Window owner, PartidoFutbol partido, List<LocalidadPartido> localidades, String cliente, BiConsumer<LocalidadPartido, Integer> onComprar) {
    super(owner, "TicketPremium - Localidades", ModalityType.APPLICATION_MODAL);
    setMinimumSize(new Dimension(980, 640));
    setLocationRelativeTo(owner);
    setLayout(new BorderLayout());

    JPanel root = new JPanel(new BorderLayout(0, 16));
    root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

    JPanel header = new JPanel(new BorderLayout());
    JLabel title = new JLabel(partido != null ? partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante() : "Localidades disponibles");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
    JLabel subtitle = new JLabel(partido != null && partido.getFecha() != null ? new java.text.SimpleDateFormat("dd-MMMM-yyyy HH:mm", new java.util.Locale("es", "EC")).format(partido.getFecha()) : "");
    subtitle.setForeground(new Color(90, 90, 90));
    header.add(title, BorderLayout.NORTH);
    header.add(subtitle, BorderLayout.SOUTH);

    JPanel list = new JPanel();
    list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

    List<LocalidadPartido> safeLocalidades = localidades == null ? new ArrayList<>() : localidades;
    if (safeLocalidades.isEmpty()) {
      JLabel empty = new JLabel("No hay localidades disponibles para este partido.");
      empty.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));
      list.add(empty);
    } else {
      for (LocalidadPartido localidad : safeLocalidades) {
        list.add(buildCard(localidad, onComprar));
        list.add(javax.swing.Box.createVerticalStrut(12));
      }
    }

    JScrollPane scroll = new JScrollPane(list);
    scroll.setBorder(BorderFactory.createEmptyBorder());

    JButton volver = new JButton("Volver");
    volver.addActionListener(e -> dispose());
    JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    footer.add(volver);

    root.add(header, BorderLayout.NORTH);
    root.add(scroll, BorderLayout.CENTER);
    root.add(footer, BorderLayout.SOUTH);
    setContentPane(root);
  }

  private JPanel buildCard(LocalidadPartido localidad, BiConsumer<LocalidadPartido, Integer> onComprar) {
    JPanel card = new JPanel(new GridBagLayout()) {
      @Override protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.setColor(new Color(220, 220, 220));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2.dispose();
      }
    };
    card.setOpaque(false);
    card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

    GridBagConstraints gc = new GridBagConstraints();
    gc.gridx = 0;
    gc.gridy = 0;
    gc.anchor = GridBagConstraints.WEST;
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.weightx = 1;

    JLabel codigo = new JLabel(safe(localidad.getCodigoLocalidad()));
    codigo.setFont(codigo.getFont().deriveFont(Font.BOLD, 18f));
    card.add(codigo, gc);

    gc.gridy++;
    card.add(new JLabel("Disponibilidad: " + localidad.getDisponibilidad()), gc);

    gc.gridy++;
    card.add(new JLabel("Precio: $" + String.format(java.util.Locale.US, "%.2f", localidad.getPrecio())), gc);

    gc.gridy++;
    JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
    form.setOpaque(false);
    JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, Math.max(1, localidad.getDisponibilidad()), 1));
    cantidad.setPreferredSize(new Dimension(90, cantidad.getPreferredSize().height));
    JButton comprar = new JButton("Comprar");
    comprar.addActionListener(e -> {
      if (onComprar != null) {
        onComprar.accept(localidad, (Integer) cantidad.getValue());
      }
    });
    form.add(new JLabel("Cantidad:"));
    form.add(cantidad);
    form.add(comprar);
    card.add(form, gc);

    return card;
  }

  private String safe(String value) {
    return value == null ? "" : value;
  }
}