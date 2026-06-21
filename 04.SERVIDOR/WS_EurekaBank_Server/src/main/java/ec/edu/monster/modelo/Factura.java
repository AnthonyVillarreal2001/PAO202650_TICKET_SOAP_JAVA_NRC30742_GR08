package ec.edu.monster.modelo;

public class Factura {
    private long idFactura;
    private String codigoPartido;
    private String idCliente;
    private String nombreCliente;
    private String vendedor;
    private String fechaEmision;
    private double subtotal;
    private double iva;
    private double total;

    public Factura() {}

    public Factura(long idFactura, String codigoPartido, String idCliente, String nombreCliente, String vendedor, String fechaEmision, double subtotal, double iva, double total) {
        this.idFactura = idFactura;
        this.codigoPartido = codigoPartido;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.vendedor = vendedor;
        this.fechaEmision = fechaEmision;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
    }

    public long getIdFactura() { return idFactura; }
    public void setIdFactura(long idFactura) { this.idFactura = idFactura; }

    public String getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(String codigoPartido) { this.codigoPartido = codigoPartido; }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getVendedor() { return vendedor; }
    public void setVendedor(String vendedor) { this.vendedor = vendedor; }

    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
