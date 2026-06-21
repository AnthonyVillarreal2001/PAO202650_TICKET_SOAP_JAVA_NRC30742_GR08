package ec.edu.monster.modelo;

public class AsientoOcupado {
    private String loc;
    private int cant;
    private String comprador;
    private String fecha;

    public AsientoOcupado() {}

    public String getLoc() { return loc; }
    public void setLoc(String loc) { this.loc = loc; }

    public int getCant() { return cant; }
    public void setCant(int cant) { this.cant = cant; }

    public String getComprador() { return comprador; }
    public void setComprador(String comprador) { this.comprador = comprador; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
