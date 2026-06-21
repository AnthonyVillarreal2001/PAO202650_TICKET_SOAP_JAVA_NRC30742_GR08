package ec.edu.monster.view;

import ec.edu.monster.model.Factura;
import ec.edu.monster.service.TicketWebClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacturasPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<ec.edu.monster.model.Cliente> cbCliente;

    public FacturasPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Historial de Facturas");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID Factura", "ID Cliente", "Subtotal", "IVA", "Total", "Estado", "Acción"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        // Handle "Ver Factura" click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row >= 0 && col == 6) {
                    long id = (Long) model.getValueAt(row, 0);
                    String idCli = (String) model.getValueAt(row, 1);
                    double sub = (Double) model.getValueAt(row, 2);
                    double iva = (Double) model.getValueAt(row, 3);
                    double tot = (Double) model.getValueAt(row, 4);
                    String est = (String) model.getValueAt(row, 5);
                    showFacturaDialog(id, idCli, sub, iva, tot, est);
                }
            }
        });
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        top.add(new JLabel("Filtrar por Cliente:"));
        cbCliente = new JComboBox<>();
        cbCliente.addItem(new ec.edu.monster.model.Cliente("", "Todos los Clientes", "", "", 0, ""));
        top.add(cbCliente);
        
        JButton btnLoad = new JButton("Consultar Facturas");
        btnLoad.addActionListener(e -> loadData());
        top.add(btnLoad);
        add(top, BorderLayout.SOUTH);

        loadCombos();
        loadData();
    }
    
    private void loadCombos() {
        new SwingWorker<List<ec.edu.monster.model.Cliente>, Void>() {
            @Override protected List<ec.edu.monster.model.Cliente> doInBackground() { return ec.edu.monster.service.CrudWebClient.listarClientes(); }
            @Override protected void done() {
                try {
                    for (ec.edu.monster.model.Cliente c : get()) cbCliente.addItem(c);
                } catch (Exception ex) { }
            }
        }.execute();
    }

    private void loadData() {
        model.setRowCount(0);
        ec.edu.monster.model.Cliente sel = (ec.edu.monster.model.Cliente) cbCliente.getSelectedItem();
        boolean byClient = sel != null && !sel.getIdCliente().isEmpty();
        
        new SwingWorker<List<Factura>, Void>() {
            @Override protected List<Factura> doInBackground() { 
                if (byClient) return TicketWebClient.listarFacturas(sel.getIdCliente());
                return TicketWebClient.listarTodasLasFacturas(); 
            }
            @Override protected void done() {
                try {
                    for (Factura f : get()) {
                        String est = f.getEstado() == 1 ? "Pagado" : "Pendiente";
                        model.addRow(new Object[]{f.getIdFactura(), f.getIdCliente(), f.getSubtotal(), f.getIva(), f.getTotal(), est, "Ver Factura (SRI)"});
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }
    
    private void showFacturaDialog(long id, String idCli, double sub, double iva, double tot, String est) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Factura Electrónica", true);
        d.setSize(600, 700);
        d.setLocationRelativeTo(this);
        
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 2), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        
        JLabel header = new JLabel("<html><center><h2 style='color:#0dcaf0;'>TICKET PREMIUM</h2><p>RUC: 1792141544001<br/>Matriz: Quito, Ecuador<br/>Obligado a llevar contabilidad: SI</p></center></html>");
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(header);
        
        p.add(Box.createVerticalStrut(20));
        
        JPanel info = new JPanel(new GridLayout(3, 2, 5, 5));
        info.setBackground(Color.WHITE);
        info.add(new JLabel("FACTURA Nro: 001-001-" + String.format("%09d", id)));
        info.add(new JLabel("FECHA: " + new java.util.Date().toString()));
        info.add(new JLabel("CLIENTE ID/RUC: " + idCli));
        info.add(new JLabel("ESTADO: " + est));
        p.add(info);
        
        p.add(Box.createVerticalStrut(20));
        
        // Fetch details
        List<ec.edu.monster.model.DetalleFactura> detalles = ec.edu.monster.service.TicketWebClient.obtenerDetallesFactura(id);
        Object[][] data = new Object[detalles.size()][4];
        for (int i = 0; i < detalles.size(); i++) {
            ec.edu.monster.model.DetalleFactura detObj = detalles.get(i);
            data[i][0] = "Asiento(s) en " + detObj.getCodigoLocalidad();
            data[i][1] = String.valueOf(detObj.getCantidad());
            data[i][2] = "$" + String.format(java.util.Locale.US, "%.2f", detObj.getPrecioUnitario());
            data[i][3] = "$" + String.format(java.util.Locale.US, "%.2f", detObj.getTotalDetalle());
        }
        if (detalles.isEmpty()) {
            data = new Object[][]{{"Boletos MASUP (Consolidado)", "-", "$"+sub, "$"+sub}};
        }
        
        JTable det = new JTable(data, new Object[]{"Descripción", "Cant", "P.Unit", "P.Total"});
        det.setEnabled(false);
        p.add(new JScrollPane(det));
        
        double descuento = sub * 0.12;
        double subNeto = sub - descuento;
        
        JPanel totals = new JPanel(new GridLayout(6, 2));
        totals.setBackground(Color.WHITE);
        totals.setBorder(BorderFactory.createEmptyBorder(10, 300, 10, 0));
        totals.add(new JLabel("Subtotal 12%:")); totals.add(new JLabel("$" + String.format(java.util.Locale.US, "%.2f", sub)));
        totals.add(new JLabel("Subtotal 0%:")); totals.add(new JLabel("$0.00"));
        totals.add(new JLabel("Desc (12% Efectivo):")); 
        JLabel lDesc = new JLabel("-$" + String.format(java.util.Locale.US, "%.2f", descuento));
        lDesc.setForeground(new Color(25, 135, 84));
        totals.add(lDesc);
        totals.add(new JLabel("Subtotal Neto:")); totals.add(new JLabel("$" + String.format(java.util.Locale.US, "%.2f", subNeto)));
        totals.add(new JLabel("IVA 12%:")); totals.add(new JLabel("$" + String.format(java.util.Locale.US, "%.2f", iva)));
        JLabel lblT = new JLabel("Total a Pagar:"); lblT.setFont(lblT.getFont().deriveFont(Font.BOLD));
        JLabel lblTV = new JLabel("$" + String.format(java.util.Locale.US, "%.2f", tot)); lblTV.setFont(lblTV.getFont().deriveFont(Font.BOLD));
        lblTV.setForeground(Color.BLUE);
        totals.add(lblT); totals.add(lblTV);
        
        p.add(totals);
        
        d.add(p);
        
        JButton btnPrint = new JButton("Cerrar Factura");
        btnPrint.addActionListener(e -> d.dispose());
        d.add(btnPrint, BorderLayout.SOUTH);
        
        d.setVisible(true);
    }
}
