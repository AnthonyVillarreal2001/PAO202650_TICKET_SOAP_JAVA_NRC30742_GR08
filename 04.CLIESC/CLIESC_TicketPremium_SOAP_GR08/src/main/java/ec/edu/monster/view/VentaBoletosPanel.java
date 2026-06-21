package ec.edu.monster.view;

import ec.edu.monster.model.Cliente;
import ec.edu.monster.model.PartidoFutbol;
import ec.edu.monster.model.LocalidadPartido;
import ec.edu.monster.service.CrudWebClient;
import ec.edu.monster.service.TicketWebClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class VentaBoletosPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;

    public VentaBoletosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Venta de Boletos");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Código", "Local", "Visitante", "Fecha", "Estadio"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBuy = new JButton("Comprar / Ver MASUP");
        btnBuy.addActionListener(e -> startPurchase());
        bottom.add(btnBuy);
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        new SwingWorker<List<PartidoFutbol>, Void>() {
            @Override protected List<PartidoFutbol> doInBackground() { return TicketWebClient.listarPartidosDisponibles(); }
            @Override protected void done() {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for (PartidoFutbol p : get()) {
                        String f = p.getFecha() != null ? sdf.format(p.getFecha()) : "Sin fecha";
                        model.addRow(new Object[]{p.getCodigo(), p.getEquipoLocal(), p.getEquipoVisitante(), f, p.getLugar()});
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void startPurchase() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un partido de la lista.");
            return;
        }
        String codigoPartido = model.getValueAt(row, 0).toString();
        
        // Fetch localities
        new SwingWorker<List<LocalidadPartido>, Void>() {
            @Override protected List<LocalidadPartido> doInBackground() { return TicketWebClient.listarLocalidadesDisponibles(codigoPartido); }
            @Override protected void done() {
                try {
                    List<LocalidadPartido> locs = get();
                    PartidoFutbol p = new PartidoFutbol(codigoPartido, model.getValueAt(row, 1).toString(), model.getValueAt(row, 2).toString(), null, model.getValueAt(row, 4).toString());
                    
                    // Show custom dialog with Client combo, Payment type and MASUP
                    showPurchaseDialog(p, locs);
                    
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void showPurchaseDialog(PartidoFutbol partido, List<LocalidadPartido> locs) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "MASUP - Compra de Boletos", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(this);

        StadiumMapPanel stadium = new StadiumMapPanel(partido.getCodigo(), locs);
        dialog.add(stadium, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setPreferredSize(new Dimension(350, 0));

        JPanel form = new JPanel(new GridLayout(6, 1, 5, 5));
        form.add(new JLabel("Cliente:"));
        JComboBox<Cliente> cbClient = new JComboBox<>();
        form.add(cbClient);

        form.add(new JLabel("Método de Pago:"));
        JComboBox<String> cbPayment = new JComboBox<>(new String[]{"Efectivo", "Crédito"});
        form.add(cbPayment);
        
        JLabel lblPlazo = new JLabel("Plazo (Meses):");
        JSpinner spPlazo = new JSpinner(new SpinnerNumberModel(6, 3, 12, 1));
        lblPlazo.setVisible(false);
        spPlazo.setVisible(false);
        form.add(lblPlazo);
        form.add(spPlazo);
        
        cbPayment.addActionListener(e -> {
            boolean isCred = "Crédito".equals(cbPayment.getSelectedItem());
            lblPlazo.setVisible(isCred);
            spPlazo.setVisible(isCred);
        });

        rightPanel.add(form, BorderLayout.NORTH);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Resumen de Carrito"));
        JLabel lblCartList = new JLabel("<html><ul><li>No hay asientos seleccionados</li></ul></html>");
        cartPanel.add(new JScrollPane(lblCartList), BorderLayout.CENTER);
        
        JLabel lblTotal = new JLabel("Total: $0.00 (0 asientos)");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        cartPanel.add(lblTotal, BorderLayout.SOUTH);
        rightPanel.add(cartPanel, BorderLayout.CENTER);

        final Map<String, Integer> currentCart = new java.util.HashMap<>();
        final double[] finalSubtotal = {0};
        final int[] finalCount = {0};

        Runnable updateTotals = () -> {
            double subtotal = finalSubtotal[0];
            double iva = subtotal * 0.12;
            double tot = subtotal + iva;
            String payment = cbPayment.getSelectedItem().toString();
            if ("Efectivo".equals(payment) && tot > 0) {
                double desc = tot * 0.12;
                double finalTotal = tot - desc;
                lblTotal.setText("<html>Total: <font color='green'>$" + String.format(java.util.Locale.US, "%.2f", finalTotal) + "</font> (" + finalCount[0] + " asientos) <br/><small>Desc 12% Efectivo Aplicado</small></html>");
            } else {
                lblTotal.setText("<html>Total: <font color='green'>$" + String.format(java.util.Locale.US, "%.2f", tot) + "</font> (" + finalCount[0] + " asientos)</html>");
            }
        };

        cbPayment.addActionListener(e -> {
            boolean isCred = "Crédito".equals(cbPayment.getSelectedItem());
            lblPlazo.setVisible(isCred);
            spPlazo.setVisible(isCred);
            updateTotals.run();
        });

        stadium.addSelectionListener((cart, subtotal, totalCount) -> {
            currentCart.clear();
            currentCart.putAll(cart);
            finalSubtotal[0] = subtotal;
            finalCount[0] = totalCount;
            
            StringBuilder sb = new StringBuilder("<html><ul style='margin-left: 10px; padding-left: 0;'>");
            if (cart.isEmpty()) {
                sb.append("<li>No hay asientos seleccionados</li>");
            } else {
                for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                    double p = 0;
                    for (LocalidadPartido lp : locs) { if (lp.getCodigoLocalidad().endsWith(entry.getKey())) p = lp.getPrecio(); }
                    sb.append("<li><b>").append(entry.getKey()).append("</b>: ").append(entry.getValue()).append(" asient. ($").append(String.format(java.util.Locale.US, "%.2f", entry.getValue() * p)).append(")</li>");
                }
            }
            sb.append("</ul></html>");
            lblCartList.setText(sb.toString());
            updateTotals.run();
        });

        JButton btnConfirm = new JButton("Confirmar Compra");
        btnConfirm.addActionListener(e -> {
            if (currentCart.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Seleccione al menos un asiento en el mapa interactivo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Cliente c = (Cliente) cbClient.getSelectedItem();
            if (c == null) return;
            
            String payment = cbPayment.getSelectedItem().toString();
            double subtotal = finalSubtotal[0];
            double iva = subtotal * 0.12;
            double tot = subtotal + iva;
            
            if (payment.equals("Crédito")) {
                int meses = (Integer) spPlazo.getValue();
                double tasaAnual = 0.165;
                double tasaPeriodo = tasaAnual / 12.0;
                double factor = (1 - Math.pow(1 + tasaPeriodo, -meses)) / tasaPeriodo;
                double cuotaMensual = tot / factor;
                
                StringBuilder msg = new StringBuilder();
                msg.append("<html><h4 style='color:#0dcaf0;text-align:center;'>Simulación de Amortización</h4>");
                msg.append("<table border='1' cellpadding='4' cellspacing='0' width='100%' style='border-collapse: collapse; text-align: right;'>");
                msg.append("<tr><td style='text-align:left; font-weight:bold;'>Valor Préstamo</td><td>").append(String.format(java.util.Locale.US, "%.2f", tot)).append("</td></tr>");
                msg.append("<tr><td style='text-align:left; font-weight:bold;'>Cuotas</td><td>").append(meses).append("</td></tr>");
                msg.append("<tr><td style='text-align:left; font-weight:bold;'>Tasa Interés Anual</td><td>16.50%</td></tr>");
                msg.append("<tr><td style='text-align:left; font-weight:bold;'>Cuota Mensual</td><td><font color='red'>($").append(String.format(java.util.Locale.US, "%.2f", cuotaMensual)).append(")</font></td></tr>");
                msg.append("</table><br/>");
                
                msg.append("<table border='1' cellpadding='4' cellspacing='0' width='100%' style='border-collapse: collapse; text-align: right;'>");
                msg.append("<tr bgcolor='#000000'><th style='color:white;'># Cuota</th><th style='color:white;'>Valor Cuota</th><th style='color:white;'>Interés Pagado</th><th style='color:white;'>Capital Pagado</th><th style='color:white;'>Saldo</th></tr>");
                msg.append("<tr><td style='text-align:center;'>0</td><td></td><td></td><td></td><td>").append(String.format(java.util.Locale.US, "%.2f", tot)).append("</td></tr>");
                
                double saldo = tot;
                for (int i = 1; i <= meses; i++) {
                    double interes = saldo * tasaPeriodo;
                    double capital = cuotaMensual - interes;
                    saldo = saldo - capital;
                    if (saldo < 0) saldo = 0;
                    msg.append("<tr><td style='text-align:center;'>").append(i).append("</td>");
                    msg.append("<td>").append(String.format(java.util.Locale.US, "%.2f", cuotaMensual)).append("</td>");
                    msg.append("<td>").append(String.format(java.util.Locale.US, "%.2f", interes)).append("</td>");
                    msg.append("<td>").append(String.format(java.util.Locale.US, "%.2f", capital)).append("</td>");
                    msg.append("<td>").append(String.format(java.util.Locale.US, "%.2f", saldo)).append("</td></tr>");
                }
                msg.append("</table>");
                msg.append("<br/><b>¿Confirmar deuda a nombre de ").append(c.getNombres()).append("?</b></html>");
                
                int conf = JOptionPane.showConfirmDialog(dialog, msg.toString(), "Confirmar Crédito", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (conf != JOptionPane.YES_OPTION) return;
            } else {
                double desc = tot * 0.12;
                double finalTotal = tot - desc;
                StringBuilder msg = new StringBuilder();
                msg.append("<html><h4 style='color:#0dcaf0;text-align:center;'>Resumen de Factura (Efectivo)</h4>");
                msg.append("<table border='1' cellpadding='4' cellspacing='0' width='100%' style='border-collapse: collapse;'>");
                msg.append("<tr><th style='text-align:left;'>Subtotal</th><td style='text-align:right;'>$").append(String.format(java.util.Locale.US, "%.2f", subtotal)).append("</td></tr>");
                msg.append("<tr><th style='text-align:left;'>IVA (12%)</th><td style='text-align:right;'>$").append(String.format(java.util.Locale.US, "%.2f", iva)).append("</td></tr>");
                msg.append("<tr><th style='text-align:left;'>Total</th><td style='text-align:right;'>$").append(String.format(java.util.Locale.US, "%.2f", tot)).append("</td></tr>");
                msg.append("<tr><th style='text-align:left;'>Desc (12%)</th><td style='text-align:right;'><font color='green'>-$").append(String.format(java.util.Locale.US, "%.2f", desc)).append("</font></td></tr>");
                msg.append("<tr><th style='text-align:left;'>Total a Pagar</th><td style='text-align:right;'><b>$").append(String.format(java.util.Locale.US, "%.2f", finalTotal)).append("</b></td></tr>");
                msg.append("</table><br/><b>¿Confirmar compra?</b></html>");
                
                int conf = JOptionPane.showConfirmDialog(dialog, msg.toString(), "Confirmar Compra Efectivo", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (conf != JOptionPane.YES_OPTION) return;
            }

            // Realizar la compra múltiple iterando sobre el cart
            boolean algunExito = false;
            String msgRes = "";
            
            StringBuilder sbLocs = new StringBuilder();
            StringBuilder sbCants = new StringBuilder();
            for (Map.Entry<String, Integer> entry : currentCart.entrySet()) {
                if (sbLocs.length() > 0) {
                    sbLocs.append(",");
                    sbCants.append(",");
                }
                sbLocs.append(partido.getCodigo()).append("-").append(entry.getKey());
                sbCants.append(entry.getValue());
            }
            
            ec.edu.monster.model.CompraResultado res = TicketWebClient.comprarBoletosMultiples(
                partido.getCodigo(), sbLocs.toString(), sbCants.toString(), c.getIdCliente()
            );
            
            if (res.getEstado() == 1 || res.getEstado() == 2) {
                algunExito = true;
                for (Map.Entry<String, Integer> entry : currentCart.entrySet()) {
                    stadium.addLocalPurchase(entry.getKey(), entry.getValue());
                }
                msgRes += "Factura Consolidada ID " + res.getFacturaId() + " generada exitosamente.\n";
            } else {
                msgRes += "Error en la compra: " + res.getMensaje() + "\n";
            }
            
            if (algunExito) {
                if (payment.equals("Crédito")) {
                    int meses = (Integer) spPlazo.getValue();
                    try {
                        ec.edu.monster.service.AmortizacionesWebClient.guardarAmortizaciones(c.getIdCliente(), tot, meses);
                        msgRes += "\nTabla de Amortización generada con éxito.";
                    } catch (Exception ex) {
                        msgRes += "\nError generando amortización: " + ex.getMessage();
                    }
                }
                JOptionPane.showMessageDialog(dialog, "Compra Procesada:\n" + msgRes, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Falló la compra:\n" + msgRes, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        rightPanel.add(btnConfirm, BorderLayout.SOUTH);

        dialog.add(rightPanel, BorderLayout.EAST);

        new SwingWorker<List<Cliente>, Void>() {
            @Override protected List<Cliente> doInBackground() { return CrudWebClient.listarClientes(); }
            @Override protected void done() {
                try {
                    for (Cliente c : get()) cbClient.addItem(c);
                } catch (Exception ex) { }
            }
        }.execute();

        dialog.setVisible(true);
    }
}
