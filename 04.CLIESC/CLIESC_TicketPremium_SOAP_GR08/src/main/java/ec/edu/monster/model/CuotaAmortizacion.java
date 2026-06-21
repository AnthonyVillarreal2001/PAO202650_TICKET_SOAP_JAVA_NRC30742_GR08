package ec.edu.monster.model;

import java.util.Date;

public class CuotaAmortizacion {
    private long idFactura;
    private int numeroCuota;
    private Date fechaVencimiento;
    private double saldoInicial;
    private double capital;
    private double interes;
    private double cuota;
    private double saldoFinal;
    private int estado;

    public CuotaAmortizacion() {}

    public long getIdFactura() { return idFactura; }
    public void setIdFactura(long idFactura) { this.idFactura = idFactura; }
    public int getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(int numeroCuota) { this.numeroCuota = numeroCuota; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public double getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(double saldoInicial) { this.saldoInicial = saldoInicial; }
    public double getCapital() { return capital; }
    public void setCapital(double capital) { this.capital = capital; }
    public double getInteres() { return interes; }
    public void setInteres(double interes) { this.interes = interes; }
    public double getCuota() { return cuota; }
    public void setCuota(double cuota) { this.cuota = cuota; }
    public double getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(double saldoFinal) { this.saldoFinal = saldoFinal; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}
