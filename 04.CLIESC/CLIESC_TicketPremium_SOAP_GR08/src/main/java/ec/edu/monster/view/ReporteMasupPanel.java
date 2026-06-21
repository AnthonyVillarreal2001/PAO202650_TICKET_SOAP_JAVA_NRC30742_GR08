package ec.edu.monster.view;

import ec.edu.monster.model.ResumenVentaLocalidad;
import ec.edu.monster.model.PartidoFutbol;
import ec.edu.monster.service.TicketWebClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ReporteMasupPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> cbPartido;
    private JPanel masupContainer;

    public ReporteMasupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Reporte MASUP de Localidades");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        headerPanel.add(title, BorderLayout.WEST);
        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(new JLabel("Filtrar por Partido:"));
        cbPartido = new JComboBox<>();
        top.add(cbPartido);
        JButton btnLoad = new JButton("Ver Reporte y MASUP");
        btnLoad.addActionListener(e -> loadData());
        top.add(btnLoad);
        headerPanel.add(top, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Localidad", "Vendidos", "Recaudación Total ($)"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        JPanel content = new JPanel(new GridLayout(2, 1, 10, 10));
        content.add(new JScrollPane(table));
        
        masupContainer = new JPanel(new BorderLayout());
        masupContainer.setBorder(BorderFactory.createTitledBorder("Visualización MASUP del Estadio"));
        content.add(masupContainer);
        
        add(content, BorderLayout.CENTER);

        loadPartidos();
    }

    private void loadPartidos() {
        cbPartido.addItem("TODOS - Todos los Partidos");
        new SwingWorker<List<PartidoFutbol>, Void>() {
            @Override protected List<PartidoFutbol> doInBackground() { return TicketWebClient.listarPartidosDisponibles(); }
            @Override protected void done() {
                try {
                    for (PartidoFutbol p : get()) cbPartido.addItem(p.getCodigo() + " - " + p.getEquipoLocal() + " vs " + p.getEquipoVisitante());
                } catch (Exception ex) { }
            }
        }.execute();
    }

    private void loadData() {
        if (cbPartido.getSelectedItem() == null) return;
        String codigo = cbPartido.getSelectedItem().toString().split(" - ")[0];
        model.setRowCount(0);
        masupContainer.removeAll();
        masupContainer.revalidate();
        masupContainer.repaint();
        
        new SwingWorker<List<ResumenVentaLocalidad>, Void>() {
            @Override protected List<ResumenVentaLocalidad> doInBackground() {
                if (codigo.equals("TODOS")) {
                    List<PartidoFutbol> parts = TicketWebClient.listarPartidosDisponibles();
                    Map<String, ResumenVentaLocalidad> global = new java.util.HashMap<>();
                    for (PartidoFutbol p : parts) {
                        List<ResumenVentaLocalidad> res = TicketWebClient.listarResumenVentas(p.getCodigo());
                        for (ResumenVentaLocalidad r : res) {
                            String baseLoc = r.getCodigoLocalidad().substring(r.getCodigoLocalidad().lastIndexOf('-') + 1);
                            global.putIfAbsent(baseLoc, new ResumenVentaLocalidad(baseLoc, 0, 0));
                            ResumenVentaLocalidad ex = global.get(baseLoc);
                            ex.setVendidos(ex.getVendidos() + r.getVendidos());
                            ex.setTotalRecaudado(ex.getTotalRecaudado() + r.getTotalRecaudado());
                        }
                    }
                    return new java.util.ArrayList<>(global.values());
                } else {
                    return TicketWebClient.listarResumenVentas(codigo);
                }
            }
            @Override protected void done() {
                try {
                    int totalVendidos = 0;
                    double totalRecaudado = 0;
                    for (ResumenVentaLocalidad r : get()) {
                        model.addRow(new Object[]{r.getCodigoLocalidad(), r.getVendidos(), String.format(java.util.Locale.US, "%.2f", r.getTotalRecaudado())});
                        totalVendidos += r.getVendidos();
                        totalRecaudado += r.getTotalRecaudado();
                    }
                    model.addRow(new Object[]{"<html><b>TOTAL GENERAL</b></html>", "<html><b>" + totalVendidos + "</b></html>", "<html><b>$" + String.format(java.util.Locale.US, "%.2f", totalRecaudado) + "</b></html>"});
                    
                    if (!codigo.equals("TODOS")) {
                        loadMap(codigo);
                    } else {
                        masupContainer.add(new JLabel("Seleccione un partido específico para ver el mapa MASUP", SwingConstants.CENTER));
                        masupContainer.revalidate();
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }
    
    private void loadMap(String codigoPartido) {
        new SwingWorker<List<ec.edu.monster.model.LocalidadPartido>, Void>() {
            @Override protected List<ec.edu.monster.model.LocalidadPartido> doInBackground() {
                return TicketWebClient.listarLocalidadesDisponibles(codigoPartido);
            }
            @Override protected void done() {
                try {
                    List<ec.edu.monster.model.LocalidadPartido> locs = get();
                    StadiumMapPanel map = new StadiumMapPanel(codigoPartido, locs, true); // true for read-only
                    masupContainer.add(map, BorderLayout.CENTER);
                    masupContainer.revalidate();
                    masupContainer.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
