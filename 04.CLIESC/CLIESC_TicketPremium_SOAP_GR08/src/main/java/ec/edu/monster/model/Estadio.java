package ec.edu.monster.model;

public class Estadio {
    private String idEstadio;
    private String nombreEstadio;
    private String ciudad;
    private int capacidad;

    public Estadio() {}
    public Estadio(String idEstadio, String nombreEstadio, String ciudad, int capacidad) {
        this.idEstadio = idEstadio;
        this.nombreEstadio = nombreEstadio;
        this.ciudad = ciudad;
        this.capacidad = capacidad;
    }

    public String getIdEstadio() { return idEstadio; }
    public void setIdEstadio(String idEstadio) { this.idEstadio = idEstadio; }
    public String getNombreEstadio() { return nombreEstadio; }
    public void setNombreEstadio(String nombreEstadio) { this.nombreEstadio = nombreEstadio; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    
    @Override
    public String toString() { return nombreEstadio; }
}
