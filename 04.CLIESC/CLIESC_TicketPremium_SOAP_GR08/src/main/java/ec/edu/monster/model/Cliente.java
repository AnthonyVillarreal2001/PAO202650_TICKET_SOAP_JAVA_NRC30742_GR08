package ec.edu.monster.model;

public class Cliente {
    private String idCliente;
    private String nombres;
    private String correo;
    private String telefono;
    private int edad;
    private String genero;

    public Cliente() {}

    public Cliente(String idCliente, String nombres, String correo, String telefono, int edad, String genero) {
        this.idCliente = idCliente;
        this.nombres = nombres;
        this.correo = correo;
        this.telefono = telefono;
        this.edad = edad;
        this.genero = genero;
    }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    @Override
    public String toString() { return nombres; }
}
