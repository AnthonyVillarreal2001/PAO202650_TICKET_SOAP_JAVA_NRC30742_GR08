package ec.edu.monster.modelo;

public class Amortizacion {
    private int idAmortizacion;
    private String idClienteCredito;
    private String nombreCliente;
    private int numeroCuota;
    private String fechaVencimiento;
    private double montoCuota;
    private double interes;
    private double capital;
    private double saldo;
    private String estadoCuota;

    public Amortizacion() {}

    public Amortizacion(int idAmortizacion, String idClienteCredito, String nombreCliente, int numeroCuota, String fechaVencimiento, double montoCuota, double interes, double capital, double saldo, String estadoCuota) {
        this.idAmortizacion = idAmortizacion;
        this.idClienteCredito = idClienteCredito;
        this.nombreCliente = nombreCliente;
        this.numeroCuota = numeroCuota;
        this.fechaVencimiento = fechaVencimiento;
        this.montoCuota = montoCuota;
        this.interes = interes;
        this.capital = capital;
        this.saldo = saldo;
        this.estadoCuota = estadoCuota;
    }

    public int getIdAmortizacion() { return idAmortizacion; }
    public void setIdAmortizacion(int idAmortizacion) { this.idAmortizacion = idAmortizacion; }

    public String getIdClienteCredito() { return idClienteCredito; }
    public void setIdClienteCredito(String idClienteCredito) { this.idClienteCredito = idClienteCredito; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public int getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(int numeroCuota) { this.numeroCuota = numeroCuota; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public double getMontoCuota() { return montoCuota; }
    public void setMontoCuota(double montoCuota) { this.montoCuota = montoCuota; }

    public double getInteres() { return interes; }
    public void setInteres(double interes) { this.interes = interes; }

    public double getCapital() { return capital; }
    public void setCapital(double capital) { this.capital = capital; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public String getEstadoCuota() { return estadoCuota; }
    public void setEstadoCuota(String estadoCuota) { this.estadoCuota = estadoCuota; }
}
