package ec.edu.monster.modelo;

public class PurchaseResponse {
    private int estado; // 1 ok, -1 error
    private String mensaje;
    private long facturaId;
    private double total;

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public long getFacturaId() { return facturaId; }
    public void setFacturaId(long facturaId) { this.facturaId = facturaId; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
