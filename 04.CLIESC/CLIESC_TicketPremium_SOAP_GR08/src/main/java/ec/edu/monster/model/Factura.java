package ec.edu.monster.model;

import java.util.Date;

public class Factura {
    private long idFactura;
    private String idCliente;
    private Date fecha;
    private double subtotal;
    private double iva;
    private double total;
    private int estado; // 1: pagado, 0: pendiente

    public Factura() {}

    public long getIdFactura() { return idFactura; }
    public void setIdFactura(long idFactura) { this.idFactura = idFactura; }
    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}
