package ec.edu.monster.modelo;

public class EvaluacionCredito {
    private int estado; // 1 = Aprobado, 0 = Rechazado, -1 = Error
    private String mensaje;
    private double montoMaximo;

    public EvaluacionCredito() {
    }

    public EvaluacionCredito(int estado, String mensaje, double montoMaximo) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.montoMaximo = montoMaximo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public double getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(double montoMaximo) {
        this.montoMaximo = montoMaximo;
    }
}
