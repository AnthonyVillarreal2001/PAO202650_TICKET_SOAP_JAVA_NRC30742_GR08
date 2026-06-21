package ec.edu.monster.view;

import com.formdev.flatlaf.FlatClientProperties;
import ec.edu.monster.controller.TicketSession;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {
    private JPanel sidebar;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public DashboardView() {
        setTitle("TicketPremium | Dashboard Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        add(buildSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Add dummy panels to avoid empty cards
        addCard("Bienvenida", new JLabel("Seleccione una opción del menú", SwingConstants.CENTER));
        
        pack();
    }

    private JPanel buildSidebar() {
        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(new Color(10, 61, 66));

        // Brand
        JLabel brand = new JLabel("TicketPremium", SwingConstants.CENTER);
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 22f));
        brand.setForeground(Color.WHITE);
        brand.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(brand, BorderLayout.NORTH);

        // User info
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        JLabel userLbl = new JLabel("Usuario: " + TicketSession.usuario, SwingConstants.LEFT);
        userLbl.setForeground(Color.LIGHT_GRAY);
        userPanel.add(userLbl, BorderLayout.CENTER);
        
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.putClientProperty(FlatClientProperties.STYLE, "arc:10; background:#e74c3c; foreground:white; borderWidth:0;");
        btnLogout.addActionListener(e -> {
            dispose();
            new ec.edu.monster.controller.TicketLoginController(new TicketLoginView()).show();
        });
        userPanel.add(btnLogout, BorderLayout.SOUTH);
        
        sidebar.add(userPanel, BorderLayout.SOUTH);

        // Menu Buttons
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setOpaque(false);
        
        // Dynamically add buttons depending on the logic (or hardcode for now and hide)
        addMenuButton(menu, "Países", "Paises");
        addMenuButton(menu, "Estadios", "Estadios");
        addMenuButton(menu, "Partidos", "Partidos");
        addMenuButton(menu, "Clientes", "Clientes");
        menu.add(Box.createVerticalStrut(20));
        addMenuButton(menu, "Comprar / Vender Boletos", "Ventas");
        addMenuButton(menu, "Facturas", "Facturas");
        addMenuButton(menu, "Amortizaciones", "Amortizaciones");
        addMenuButton(menu, "Reporte MASUP", "Reporte");

        JScrollPane scroll = new JScrollPane(menu);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        sidebar.add(scroll, BorderLayout.CENTER);

        return sidebar;
    }

    private void addMenuButton(JPanel parent, String title, String cardName) {
        JButton btn = new JButton(title);
        btn.setMaximumSize(new Dimension(250, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc:0; background:#0a3d42; foreground:white; borderWidth:0; hoverBackground:#0c4d54;");
        btn.addActionListener(e -> showCard(cardName));
        parent.add(btn);
        parent.add(Box.createVerticalStrut(5));
    }

    public void addCard(String name, JComponent panel) {
        contentPanel.add(panel, name);
    }

    public void showCard(String name) {
        cardLayout.show(contentPanel, name);
    }
}
