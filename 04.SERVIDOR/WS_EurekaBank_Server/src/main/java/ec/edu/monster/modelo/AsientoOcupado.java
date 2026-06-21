package ec.edu.monster.modelo;

public class AsientoOcupado {
    private String codigoLocalidad;
    private int cantidad;
    private String comprador;
    private String fechaCompra;

    public AsientoOcupado() {}

    public AsientoOcupado(String codigoLocalidad, int cantidad, String comprador, String fechaCompra) {
        this.codigoLocalidad = codigoLocalidad;
        this.cantidad = cantidad;
        this.comprador = comprador;
        this.fechaCompra = fechaCompra;
    }

    public String getCodigoLocalidad() { return codigoLocalidad; }
    public void setCodigoLocalidad(String codigoLocalidad) { this.codigoLocalidad = codigoLocalidad; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getComprador() { return comprador; }
    public void setComprador(String comprador) { this.comprador = comprador; }

    public String getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(String fechaCompra) { this.fechaCompra = fechaCompra; }
}
