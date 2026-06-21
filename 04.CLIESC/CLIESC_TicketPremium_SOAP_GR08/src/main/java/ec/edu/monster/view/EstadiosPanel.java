package ec.edu.monster.view;

import ec.edu.monster.model.Estadio;
import ec.edu.monster.service.CrudWebClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EstadiosPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtId, txtNombre, txtCiudad, txtCapacidad;

    public EstadiosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Administración de Estadios");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Nombre", "Ciudad", "Capacidad"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtId.setText(model.getValueAt(row, 0).toString());
                txtId.setEditable(false);
                txtNombre.setText(model.getValueAt(row, 1).toString());
                txtCiudad.setText(model.getValueAt(row, 2).toString());
                txtCapacidad.setText(model.getValueAt(row, 3).toString());
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 6, 10, 10));
        form.add(new JLabel("ID:")); txtId = new JTextField(); form.add(txtId);
        form.add(new JLabel("Nombre:")); txtNombre = new JTextField(); form.add(txtNombre);
        form.add(new JLabel("Ciudad:")); txtCiudad = new JTextField(); form.add(txtCiudad);
        form.add(new JLabel("Capacidad:")); txtCapacidad = new JTextField(); form.add(txtCapacidad);
        
        JButton btnSave = new JButton("Guardar"); btnSave.addActionListener(e -> save()); form.add(btnSave);
        JButton btnDelete = new JButton("Eliminar"); btnDelete.addActionListener(e -> delete()); form.add(btnDelete);
        JButton btnClear = new JButton("Limpiar"); btnClear.addActionListener(e -> clear()); form.add(btnClear);

        add(form, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        new SwingWorker<List<Estadio>, Void>() {
            @Override protected List<Estadio> doInBackground() { return CrudWebClient.listarEstadios(); }
            @Override protected void done() {
                try {
                    for (Estadio e : get()) model.addRow(new Object[]{e.getIdEstadio(), e.getNombreEstadio(), e.getCiudad(), e.getCapacidad()});
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void save() {
        try {
            String id = txtId.getText().trim();
            String nom = txtNombre.getText().trim();
            String ciu = txtCiudad.getText().trim();
            int cap = Integer.parseInt(txtCapacidad.getText().trim());
            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() { return CrudWebClient.guardarEstadio(new Estadio(id, nom, ciu, cap)); }
                @Override protected void done() { loadData(); clear(); }
            }.execute();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error en los datos"); }
    }

    private void delete() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?") == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() { return CrudWebClient.eliminarEstadio(id); }
                @Override protected void done() { loadData(); clear(); }
            }.execute();
        }
    }

    private void clear() {
        txtId.setText(""); txtId.setEditable(true);
        txtNombre.setText(""); txtCiudad.setText(""); txtCapacidad.setText("");
        table.clearSelection();
    }
}
