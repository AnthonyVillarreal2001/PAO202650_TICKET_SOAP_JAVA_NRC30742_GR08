package ec.edu.monster.model;

public class AsientoOcupado {
    private String loc;
    private int cant;
    private String comprador;
    private String fecha;

    public AsientoOcupado() {}

    public AsientoOcupado(String loc, int cant, String comprador, String fecha) {
        this.loc = loc;
        this.cant = cant;
        this.comprador = comprador;
        this.fecha = fecha;
    }

    public String getLoc() { return loc; }
    public void setLoc(String loc) { this.loc = loc; }

    public int getCant() { return cant; }
    public void setCant(int cant) { this.cant = cant; }

    public String getComprador() { return comprador; }
    public void setComprador(String comprador) { this.comprador = comprador; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
