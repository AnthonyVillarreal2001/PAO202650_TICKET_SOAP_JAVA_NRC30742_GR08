package ec.edu.monster.view;

import ec.edu.monster.model.CuotaAmortizacion;
import ec.edu.monster.service.AmortizacionesWebClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AmortizacionesPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<ec.edu.monster.model.Cliente> cbCliente;

    public AmortizacionesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Amortizaciones Activas");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Cuota", "Saldo Inicial", "Capital", "Interés", "Cuota", "Saldo Final", "Estado"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Cliente:"));
        cbCliente = new JComboBox<>();
        top.add(cbCliente);
        JButton btnLoad = new JButton("Consultar Créditos");
        btnLoad.addActionListener(e -> loadData());
        top.add(btnLoad);
        add(top, BorderLayout.SOUTH);

        loadCombos();
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
        ec.edu.monster.model.Cliente sel = (ec.edu.monster.model.Cliente) cbCliente.getSelectedItem();
        if (sel == null || sel.getIdCliente().isEmpty()) return;
        
        String idStr = sel.getIdCliente();
        model.setRowCount(0);
        new SwingWorker<List<CuotaAmortizacion>, Void>() {
            @Override protected List<CuotaAmortizacion> doInBackground() { return AmortizacionesWebClient.listarAmortizaciones(idStr); }
            @Override protected void done() {
                try {
                    for (CuotaAmortizacion c : get()) {
                        String est = c.getEstado() == 1 ? "Pagado" : "Pendiente";
                        model.addRow(new Object[]{
                                c.getNumeroCuota(),
                                String.format(java.util.Locale.US, "%.2f", c.getSaldoInicial()),
                                String.format(java.util.Locale.US, "%.2f", c.getCapital()),
                                String.format(java.util.Locale.US, "%.2f", c.getInteres()),
                                String.format(java.util.Locale.US, "%.2f", c.getCuota()),
                                String.format(java.util.Locale.US, "%.2f", c.getSaldoFinal()),
                                est
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }
}
