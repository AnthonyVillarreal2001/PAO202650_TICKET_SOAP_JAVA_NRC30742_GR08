package ec.edu.monster.view;

import ec.edu.monster.model.AsientoOcupado;
import ec.edu.monster.model.LocalidadPartido;
import ec.edu.monster.service.TicketWebClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.Arrays;

public class StadiumMapPanel extends JPanel {
    public interface SeatSelectionListener {
        void onSelectionChanged(Map<String, Integer> cart, double subtotal, int totalCount);
    }

    private String codigoPartido;
    private List<LocalidadPartido> localidades;
    private List<AsientoOcupado> ocupadosData;
    private Map<String, List<String>> compradosLocal;
    private List<SeatSelectionListener> listeners = new ArrayList<>();
    private Map<String, Integer> selectedCounts = new HashMap<>(); // LocalidadCode -> Count
    private boolean isReadOnly = false;

    public StadiumMapPanel(String codigoPartido, List<LocalidadPartido> localidades) {
        this(codigoPartido, localidades, false);
    }

    public StadiumMapPanel(String codigoPartido, List<LocalidadPartido> localidades, boolean isReadOnly) {
        this.codigoPartido = codigoPartido;
        this.localidades = localidades;
        this.isReadOnly = isReadOnly;
        this.ocupadosData = new ArrayList<>();
        this.compradosLocal = new HashMap<>();
        
        loadLocalStore();

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel loading = new JLabel("Cargando mapa del estadio...", SwingConstants.CENTER);
        add(loading, BorderLayout.CENTER);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    ocupadosData = TicketWebClient.obtenerAsientosOcupados(codigoPartido);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                removeAll();
                add(buildStadium(), BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        }.execute();
    }

    private void loadLocalStore() {
        Preferences prefs = Preferences.userNodeForPackage(StadiumMapPanel.class);
        String saved = prefs.get("comprados_" + codigoPartido, "");
        if (!saved.isEmpty()) {
            String[] arr = saved.split(",");
            for (String s : arr) {
                if (s.isEmpty()) continue;
                String area = s.split("_")[0];
                compradosLocal.putIfAbsent(area, new ArrayList<>());
                compradosLocal.get(area).add(s);
            }
        }
    }

    private JPanel buildStadium() {
        JPanel stadium = new JPanel(new GridBagLayout());
        stadium.setOpaque(false);
        stadium.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));
        stadium.setBackground(Color.WHITE);

        // Areas: GEN (Norte), GVI (Sur), TRI (Oeste), PAL (Este)
        // Let's use proportional grid
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(10, 10, 10, 10);

        // Norte (GEN)
        gc.gridx = 1; gc.gridy = 0;
        stadium.add(buildZone("GEN", "Norte/General", "north", 40), gc);

        // Oeste (TRI)
        gc.gridx = 0; gc.gridy = 1;
        stadium.add(buildZone("TRI", "Oeste/Tribuna", "west", 25), gc);

        // Cancha
        gc.gridx = 1; gc.gridy = 1;
        JPanel cancha = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 204, 113));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(10, 10, getWidth()-20, getHeight()-20, 20, 20);
                g2.drawLine(getWidth()/2, 10, getWidth()/2, getHeight()-10);
                g2.drawOval(getWidth()/2 - 20, getHeight()/2 - 20, 40, 40);
                g2.dispose();
            }
        };
        cancha.setPreferredSize(new Dimension(150, 100));
        stadium.add(cancha, gc);

        // Este (PAL)
        gc.gridx = 2; gc.gridy = 1;
        stadium.add(buildZone("PAL", "Este/Palco", "east", 25), gc);

        // Sur (GVI)
        gc.gridx = 1; gc.gridy = 2;
        stadium.add(buildZone("GVI", "Sur/Visitante", "south", 40), gc);

        return stadium;
    }    private void notifyListeners() {
        double subtotal = 0;
        int count = 0;
        Map<String, Integer> cart = new HashMap<>();
        
        for (Map.Entry<String, Integer> entry : selectedCounts.entrySet()) {
            if (entry.getValue() > 0) {
                cart.put(entry.getKey(), entry.getValue());
                count += entry.getValue();
                for (LocalidadPartido lp : localidades) {
                    if (lp.getCodigoLocalidad().endsWith(entry.getKey())) {
                        subtotal += (entry.getValue() * lp.getPrecio());
                        break;
                    }
                }
            }
        }
        for (SeatSelectionListener l : listeners) {
            l.onSelectionChanged(cart, subtotal, count);
        }
    }

    private JPanel buildZone(String tipoLocalidad, String nombreZona, String areaPrefix, int dotCount) {
        JPanel zone = new JPanel(new BorderLayout());
        zone.setOpaque(false);

        JLabel lbl = new JLabel(nombreZona, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 10f));
        zone.add(lbl, BorderLayout.NORTH);

        LocalidadPartido locInfo = null;
        for (LocalidadPartido lp : localidades) {
            if (lp.getCodigoLocalidad().endsWith(tipoLocalidad)) {
                locInfo = lp;
                break;
            }
        }

        JPanel dotsPanel = new JPanel();
        if (dotCount == 40) {
            dotsPanel.setLayout(new GridLayout(2, 20, 2, 2));
        } else {
            dotsPanel.setLayout(new GridLayout(5, 5, 2, 2));
        }
        dotsPanel.setOpaque(false);

        if (locInfo == null) {
            zone.add(dotsPanel, BorderLayout.CENTER);
            return zone;
        }

        // Prepare buyers list
        List<AsientoOcupado> myBuyers = new ArrayList<>();
        for (AsientoOcupado ao : ocupadosData) {
            if (ao.getLoc().endsWith(tipoLocalidad)) {
                for (int k = 0; k < ao.getCant(); k++) {
                    myBuyers.add(ao);
                }
            }
        }

        List<String> myComprados = compradosLocal.getOrDefault(areaPrefix, new ArrayList<>());

        List<SeatData> states = new ArrayList<>();
        for (int i = 1; i <= dotCount; i++) {
            states.add(new SeatData("libre", areaPrefix + "_" + i));
        }

        // 1. Assign local clicks
        for (int i = 0; i < dotCount; i++) {
            if (myComprados.contains(states.get(i).id)) {
                if (!myBuyers.isEmpty()) {
                    AsientoOcupado b = myBuyers.remove(0);
                    states.get(i).status = "comprada";
                    states.get(i).comprador = b.getComprador();
                    states.get(i).fecha = b.getFecha();
                } else {
                    states.get(i).status = "comprada";
                    states.get(i).comprador = "Usted";
                    states.get(i).fecha = "Reciente";
                }
            }
        }

        // 2. Assign remaining
        while (!myBuyers.isEmpty()) {
            AsientoOcupado b = myBuyers.remove(0);
            for (int i = 0; i < dotCount; i++) {
                if (states.get(i).status.equals("libre")) {
                    states.get(i).status = "comprada";
                    states.get(i).comprador = b.getComprador();
                    states.get(i).fecha = b.getFecha();
                    break;
                }
            }
        }

        for (int i = 0; i < dotCount; i++) {
            SeatData s = states.get(i);
            SeatDot dot = new SeatDot(s, tipoLocalidad);
            String priceLabel = "$" + String.format(java.util.Locale.US, "%.2f", locInfo.getPrecio());
            if (s.status.equals("comprada")) {
                dot.setToolTipText("<html><b>" + nombreZona + " - Asiento " + (i+1) + "</b><br/>Comprado por: <font color='orange'>" + s.comprador + "</font><br/><small>" + s.fecha + "</small></html>");
            } else if (s.status.equals("seleccionada")) {
                dot.setToolTipText("<html><b>" + nombreZona + " - Asiento " + (i+1) + "</b><br/>Estado: <font color='yellow'>SELECCIONADO</font></html>");
            } else {
                dot.setToolTipText("<html><b>" + nombreZona + " - Asiento " + (i+1) + "</b><br/>Precio: <font color='green'>" + priceLabel + "</font></html>");
            }
            dotsPanel.add(dot);
        }

        zone.add(dotsPanel, BorderLayout.CENTER);
        return zone;
    }

    public void addSelectionListener(SeatSelectionListener l) { listeners.add(l); }

    public void addLocalPurchase(String tipoLocalidad, int cantidad) {
        String areaPrefix = "north";
        if (tipoLocalidad.equals("GVI")) areaPrefix = "south";
        else if (tipoLocalidad.equals("TRI")) areaPrefix = "west";
        else if (tipoLocalidad.equals("PAL")) areaPrefix = "east";

        Preferences prefs = Preferences.userNodeForPackage(StadiumMapPanel.class);
        String saved = prefs.get("comprados_" + codigoPartido, "");
        List<String> allLocal = new ArrayList<>();
        if (!saved.isEmpty()) {
            allLocal.addAll(Arrays.asList(saved.split(",")));
        }

        // Add the currently visually selected seats of this type to local storage
        for (int i = 1; i <= (areaPrefix.equals("north")||areaPrefix.equals("south")?40:25); i++) {
            String seatId = areaPrefix + "_" + i;
            if (visuallySelected.contains(seatId) && !allLocal.contains(seatId)) {
                allLocal.add(seatId);
            }
        }
        
        prefs.put("comprados_" + codigoPartido, String.join(",", allLocal));
    }

    private List<String> visuallySelected = new ArrayList<>();

    private class SeatData {
        String status;
        String id;
        String comprador;
        String fecha;

        SeatData(String status, String id) {
            this.status = status;
            this.id = id;
        }
    }

    private class SeatDot extends JComponent {
        private SeatData data;
        private boolean hovered = false;
        private String locType;

        SeatDot(SeatData data, String locType) {
            this.data = data;
            this.locType = locType;
            setPreferredSize(new Dimension(8, 8));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isReadOnly || data.status.equals("comprada")) return;
                    if (data.status.equals("libre")) {
                        data.status = "seleccionada";
                        visuallySelected.add(data.id);
                        selectedCounts.put(locType, selectedCounts.getOrDefault(locType, 0) + 1);
                    } else if (data.status.equals("seleccionada")) {
                        data.status = "libre";
                        visuallySelected.remove(data.id);
                        int c = selectedCounts.getOrDefault(locType, 0);
                        if (c > 0) selectedCounts.put(locType, c - 1);
                    }
                    notifyListeners();
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data.status.equals("comprada")) {
                g2.setColor(new Color(231, 76, 60)); // Red
            } else if (data.status.equals("seleccionada")) {
                g2.setColor(new Color(241, 196, 15)); // Yellow
            } else {
                g2.setColor(new Color(46, 204, 113)); // Green
            }

            g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
            
            if (hovered) {
                g2.setColor(Color.BLACK);
                g2.drawOval(0, 0, getWidth()-1, getHeight()-1);
            }
            g2.dispose();
        }
    }
}
