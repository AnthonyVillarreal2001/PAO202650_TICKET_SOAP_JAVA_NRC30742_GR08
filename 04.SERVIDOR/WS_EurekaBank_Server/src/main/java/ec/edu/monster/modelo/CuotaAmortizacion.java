package ec.edu.monster.modelo;

public class CuotaAmortizacion {
    private int numeroCuota;
    private double valorCuota;
    private double interesPagado;
    private double capitalPagado;
    private double saldo;

    public CuotaAmortizacion() {
    }

    public CuotaAmortizacion(int numeroCuota, double valorCuota, double interesPagado, double capitalPagado, double saldo) {
        this.numeroCuota = numeroCuota;
        this.valorCuota = valorCuota;
        this.interesPagado = interesPagado;
        this.capitalPagado = capitalPagado;
        this.saldo = saldo;
    }

    public int getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(int numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public double getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(double valorCuota) {
        this.valorCuota = valorCuota;
    }

    public double getInteresPagado() {
        return interesPagado;
    }

    public void setInteresPagado(double interesPagado) {
        this.interesPagado = interesPagado;
    }

    public double getCapitalPagado() {
        return capitalPagado;
    }

    public void setCapitalPagado(double capitalPagado) {
        this.capitalPagado = capitalPagado;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
