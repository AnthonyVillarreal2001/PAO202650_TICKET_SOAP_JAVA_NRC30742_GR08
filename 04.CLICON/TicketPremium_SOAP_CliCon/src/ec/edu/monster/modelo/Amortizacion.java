package ec.edu.monster.modelo;

public class Amortizacion {
    private int mes;
    private double cuota;
    private double interes;
    private double amortizacion;
    private double saldo;

    public Amortizacion() {}

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public double getCuota() { return cuota; }
    public void setCuota(double cuota) { this.cuota = cuota; }

    public double getInteres() { return interes; }
    public void setInteres(double interes) { this.interes = interes; }

    public double getAmortizacion() { return amortizacion; }
    public void setAmortizacion(double amortizacion) { this.amortizacion = amortizacion; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
