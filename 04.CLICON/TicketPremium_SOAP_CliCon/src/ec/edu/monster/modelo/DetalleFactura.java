package ec.edu.monster.modelo;

public class DetalleFactura {
    private long idDetalle;
    private String codigoPartido;
    private long idFactura;
    private String codigoLocalidad;
    private int cantidad;
    private double precioUnitario;
    private double totalDetalle;

    public DetalleFactura() {}

    public long getIdDetalle() { return idDetalle; }
    public void setIdDetalle(long idDetalle) { this.idDetalle = idDetalle; }

    public String getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(String codigoPartido) { this.codigoPartido = codigoPartido; }

    public long getIdFactura() { return idFactura; }
    public void setIdFactura(long idFactura) { this.idFactura = idFactura; }

    public String getCodigoLocalidad() { return codigoLocalidad; }
    public void setCodigoLocalidad(String codigoLocalidad) { this.codigoLocalidad = codigoLocalidad; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getTotalDetalle() { return totalDetalle; }
    public void setTotalDetalle(double totalDetalle) { this.totalDetalle = totalDetalle; }
}
