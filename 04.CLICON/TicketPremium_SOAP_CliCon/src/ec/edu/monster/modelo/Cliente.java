package ec.edu.monster.modelo;

public class Cliente {
    private String idCliente;
    private String apellidos;
    private String nombres;
    private boolean aptoCredito;

    public Cliente() {}

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public boolean isAptoCredito() { return aptoCredito; }
    public void setAptoCredito(boolean aptoCredito) { this.aptoCredito = aptoCredito; }
}
