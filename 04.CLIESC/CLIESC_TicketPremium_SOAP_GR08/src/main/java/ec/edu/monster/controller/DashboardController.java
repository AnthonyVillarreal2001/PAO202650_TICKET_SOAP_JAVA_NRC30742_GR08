package ec.edu.monster.controller;

import ec.edu.monster.view.DashboardView;
import javax.swing.JLabel;

public class DashboardController {
    private final DashboardView view;

    public DashboardController(DashboardView view) {
        this.view = view;
        initPanels();
    }

    private void initPanels() {
        view.addCard("Paises", new ec.edu.monster.view.PaisesPanel());
        view.addCard("Estadios", new ec.edu.monster.view.EstadiosPanel());
        view.addCard("Partidos", new ec.edu.monster.view.PartidosPanel());
        view.addCard("Clientes", new ec.edu.monster.view.ClientesPanel());
        view.addCard("Ventas", new ec.edu.monster.view.VentaBoletosPanel());
        view.addCard("Facturas", new ec.edu.monster.view.FacturasPanel());
        view.addCard("Amortizaciones", new ec.edu.monster.view.AmortizacionesPanel());
        view.addCard("Reporte", new ec.edu.monster.view.ReporteMasupPanel());
    }

    public void show() {
        view.setVisible(true);
    }
}
