package ec.edu.monster.view;

import ec.edu.monster.model.Cliente;
import ec.edu.monster.service.CrudWebClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientesPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtId, txtNombres, txtCorreo, txtTelefono, txtEdad;
    private JComboBox<String> cbGenero;

    public ClientesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Administración de Clientes");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Nombres", "Correo", "Teléfono", "Edad", "Género"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtId.setText(model.getValueAt(row, 0).toString());
                txtId.setEditable(false);
                txtNombres.setText(model.getValueAt(row, 1).toString());
                txtCorreo.setText(model.getValueAt(row, 2).toString());
                txtTelefono.setText(model.getValueAt(row, 3).toString());
                txtEdad.setText(model.getValueAt(row, 4).toString());
                cbGenero.setSelectedItem(model.getValueAt(row, 5).toString());
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 6, 10, 10));
        form.add(new JLabel("Cédula:")); txtId = new JTextField(); form.add(txtId);
        form.add(new JLabel("Nombres:")); txtNombres = new JTextField(); form.add(txtNombres);
        form.add(new JLabel("Correo:")); txtCorreo = new JTextField(); form.add(txtCorreo);
        form.add(new JLabel("Teléfono:")); txtTelefono = new JTextField(); form.add(txtTelefono);
        form.add(new JLabel("Edad:")); txtEdad = new JTextField(); form.add(txtEdad);
        form.add(new JLabel("Género:")); 
        cbGenero = new JComboBox<>(new String[]{"M", "F", "Otro"}); form.add(cbGenero);
        
        JButton btnSave = new JButton("Guardar"); btnSave.addActionListener(e -> save()); form.add(btnSave);
        JButton btnDelete = new JButton("Eliminar"); btnDelete.addActionListener(e -> delete()); form.add(btnDelete);
        JButton btnClear = new JButton("Limpiar"); btnClear.addActionListener(e -> clear()); form.add(btnClear);

        add(form, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        new SwingWorker<List<Cliente>, Void>() {
            @Override protected List<Cliente> doInBackground() { return CrudWebClient.listarClientes(); }
            @Override protected void done() {
                try {
                    for (Cliente c : get()) model.addRow(new Object[]{c.getIdCliente(), c.getNombres(), c.getCorreo(), c.getTelefono(), c.getEdad(), c.getGenero()});
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void save() {
        try {
            String id = txtId.getText().trim();
            String nom = txtNombres.getText().trim();
            String cor = txtCorreo.getText().trim();
            String tel = txtTelefono.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());
            String gen = cbGenero.getSelectedItem().toString();
            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() { return CrudWebClient.guardarCliente(new Cliente(id, nom, cor, tel, edad, gen)); }
                @Override protected void done() { loadData(); clear(); }
            }.execute();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error en los datos"); }
    }

    private void delete() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?") == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() { return CrudWebClient.eliminarCliente(id); }
                @Override protected void done() { loadData(); clear(); }
            }.execute();
        }
    }

    private void clear() {
        txtId.setText(""); txtId.setEditable(true);
        txtNombres.setText(""); txtCorreo.setText(""); txtTelefono.setText(""); txtEdad.setText("");
        cbGenero.setSelectedIndex(0);
        table.clearSelection();
    }
}
