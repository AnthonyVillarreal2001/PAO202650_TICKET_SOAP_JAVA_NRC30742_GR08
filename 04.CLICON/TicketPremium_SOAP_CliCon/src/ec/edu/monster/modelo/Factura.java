package ec.edu.monster.modelo;

public class Factura {
    private long idFactura;
    private String idCliente;
    private String fechaFactura;
    private double totalFactura;

    public Factura() {}

    public long getIdFactura() { return idFactura; }
    public void setIdFactura(long idFactura) { this.idFactura = idFactura; }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public String getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(String fechaFactura) { this.fechaFactura = fechaFactura; }

    public double getTotalFactura() { return totalFactura; }
    public void setTotalFactura(double totalFactura) { this.totalFactura = totalFactura; }
}
