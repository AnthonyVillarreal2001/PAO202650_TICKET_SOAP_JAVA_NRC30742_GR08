package ec.edu.monster.modelo;

public class Pais {
    private String idPais;
    private String nombrePais;

    public Pais() {}

    public Pais(String idPais, String nombrePais) {
        this.idPais = idPais;
        this.nombrePais = nombrePais;
    }

    public String getIdPais() { return idPais; }
    public void setIdPais(String idPais) { this.idPais = idPais; }
    public String getNombrePais() { return nombrePais; }
    public void setNombrePais(String nombrePais) { this.nombrePais = nombrePais; }
}
