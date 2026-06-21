package ec.edu.monster.view;

import ec.edu.monster.model.Pais;
import ec.edu.monster.service.CrudWebClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PaisesPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtId;
    private JTextField txtNombre;

    public PaisesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Administración de Países");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtId.setText(model.getValueAt(row, 0).toString());
                txtId.setEditable(false);
                txtNombre.setText(model.getValueAt(row, 1).toString());
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        form.add(new JLabel("ID País:"));
        txtId = new JTextField(5);
        form.add(txtId);

        form.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(15);
        form.add(txtNombre);

        JButton btnSave = new JButton("Guardar");
        btnSave.addActionListener(e -> save());
        form.add(btnSave);

        JButton btnDelete = new JButton("Eliminar");
        btnDelete.addActionListener(e -> delete());
        form.add(btnDelete);

        JButton btnClear = new JButton("Limpiar");
        btnClear.addActionListener(e -> clear());
        form.add(btnClear);

        add(form, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        new SwingWorker<List<Pais>, Void>() {
            @Override
            protected List<Pais> doInBackground() {
                return CrudWebClient.listarPaises();
            }
            @Override
            protected void done() {
                try {
                    for (Pais p : get()) model.addRow(new Object[]{p.getIdPais(), p.getNombrePais()});
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void save() {
        String id = txtId.getText().trim();
        String nom = txtNombre.getText().trim();
        if (id.isEmpty() || nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Llene todos los campos");
            return;
        }
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() { return CrudWebClient.guardarPais(new Pais(id, nom)); }
            @Override protected void done() { loadData(); clear(); }
        }.execute();
    }

    private void delete() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) return;
        int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar país?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() { return CrudWebClient.eliminarPais(id); }
                @Override protected void done() { loadData(); clear(); }
            }.execute();
        }
    }

    private void clear() {
        txtId.setText("");
        txtId.setEditable(true);
        txtNombre.setText("");
        table.clearSelection();
    }
}
