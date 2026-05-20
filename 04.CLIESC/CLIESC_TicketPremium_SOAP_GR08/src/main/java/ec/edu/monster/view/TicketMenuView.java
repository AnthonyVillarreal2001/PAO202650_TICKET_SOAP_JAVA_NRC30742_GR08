package ec.edu.monster.view;

import ec.edu.monster.model.PartidoFutbol;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class TicketMenuView extends JFrame {
  private final JLabel lblUsuario = new JLabel("USUARIO");
  private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Código", "Partido", "Fecha", "Lugar"}, 0) {
    @Override public boolean isCellEditable(int row, int column) {
      return false;
    }
  };
  private final JTable table = new JTable(model);
  private Consumer<Integer> onVerLocalidades;
  private Consumer<Integer> onReporte;
  private Runnable onSalir;
  private List<PartidoFutbol> partidos = new ArrayList<>();

  public TicketMenuView() {
    setTitle("TicketPremium | Partidos disponibles");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(1180, 720));
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    add(buildHeader(), BorderLayout.NORTH);
    add(buildMain(), BorderLayout.CENTER);
    pack();
  }

  public void setUsuario(String usuario) {
    lblUsuario.setText(usuario == null || usuario.isBlank() ? "USUARIO" : usuario.trim());
  }

  public void setPartidos(List<PartidoFutbol> partidos) {
    this.partidos = partidos == null ? new ArrayList<>() : new ArrayList<>(partidos);
    renderPartidos();
  }

  public void onVerLocalidades(Consumer<Integer> handler) {
    this.onVerLocalidades = handler;
  }

  public void onReporte(Consumer<Integer> handler) {
    this.onReporte = handler;
  }

  public void onSalir(Runnable handler) {
    this.onSalir = handler;
  }

  public void showError(String msg) {
    javax.swing.JOptionPane.showMessageDialog(this, msg, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
  }

  private JComponent buildHeader() {
    JPanel bar = new JPanel(new BorderLayout()) {
      @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(10, 61, 66, 235));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
      }
    };
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 0, 0, 50)));
    bar.add(buildBrand(), BorderLayout.WEST);

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
    right.setOpaque(false);
    JLabel userChip = new JLabel();
    userChip.setOpaque(true);
    userChip.setBackground(new Color(255, 255, 255, 35));
    userChip.setForeground(Color.WHITE);
    userChip.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    userChip.setText(lblUsuario.getText());
    lblUsuario.addPropertyChangeListener("text", evt -> userChip.setText(String.valueOf(evt.getNewValue())));
    right.add(userChip);

    JButton salir = new JButton("Salir");
    salir.addActionListener(e -> {
      if (onSalir != null) {
        onSalir.run();
      }
    });
    right.add(salir);
    bar.add(right, BorderLayout.EAST);
    return bar;
  }

  private JComponent buildBrand() {
    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
    left.setOpaque(false);
    JLabel title = new JLabel("TicketPremium");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
    title.setForeground(new Color(255, 220, 220));
    JLabel subtitle = new JLabel("Compra de boletos, facturación y control de ventas");
    subtitle.setForeground(new Color(230, 230, 230));
    JPanel text = new JPanel(new GridLayout(2, 1));
    text.setOpaque(false);
    text.add(title);
    text.add(subtitle);
    left.add(text);
    return left;
  }

  private JComponent buildMain() {
    JPanel wrap = new JPanel(new GridBagLayout());
    wrap.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
    wrap.setOpaque(false);

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), BorderFactory.createEmptyBorder(18, 18, 18, 18)));
    panel.setBackground(new Color(255, 255, 255, 240));

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    JLabel title = new JLabel("Partidos disponibles");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
    JLabel subtitle = new JLabel("Selecciona un partido para ver sus localidades o generar un reporte.");
    subtitle.setForeground(new Color(90, 90, 90));
    header.add(title, BorderLayout.NORTH);
    header.add(subtitle, BorderLayout.SOUTH);

    table.setRowHeight(36);
    table.setShowGrid(false);
    table.setIntercellSpacing(new java.awt.Dimension(0, 8));
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(BorderFactory.createEmptyBorder());

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
    actions.setOpaque(false);

    JButton localidades = new JButton("Ver localidades");
    localidades.addActionListener(e -> triggerSelected(onVerLocalidades));

    JButton reporte = new JButton("Reporte");
    reporte.addActionListener(e -> triggerSelected(onReporte));

    actions.add(localidades);
    actions.add(reporte);

    JPanel footer = new JPanel(new BorderLayout());
    footer.setOpaque(false);
    footer.add(actions, BorderLayout.EAST);

    panel.add(header, BorderLayout.NORTH);
    panel.add(scroll, BorderLayout.CENTER);
    panel.add(footer, BorderLayout.SOUTH);
    wrap.add(panel);
    return wrap;
  }

  private void renderPartidos() {
    model.setRowCount(0);

    if (partidos.isEmpty()) {
      model.addRow(new Object[]{"", "No hay partidos disponibles en este momento.", "", ""});
    } else {
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm", new Locale("es", "EC"));
      for (int i = 0; i < partidos.size(); i++) {
        PartidoFutbol partido = partidos.get(i);
        model.addRow(new Object[]{
          safe(partido.getCodigo()),
          formatPartido(partido),
          partido.getFecha() != null ? sdf.format(partido.getFecha()) : "",
          safe(partido.getLugar())
        });
      }
    }

    SwingUtilities.invokeLater(() -> {
      table.revalidate();
      table.repaint();
    });
  }

  private void triggerSelected(Consumer<Integer> handler) {
    if (handler == null) {
      return;
    }
    int row = table.getSelectedRow();
    if (row < 0) {
      showError("Selecciona un partido de la lista.");
      return;
    }
    handler.accept(row);
  }

  private String formatPartido(PartidoFutbol partido) {
    return safe(partido.getEquipoLocal()) + " vs " + safe(partido.getEquipoVisitante());
  }

  private String safe(String value) {
    return value == null ? "" : value;
  }
}