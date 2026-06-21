package ec.edu.monster.model;

public class ResumenVentaLocalidad {
  private String codigoLocalidad;
  private int vendidos;
  private double totalRecaudado;

  public ResumenVentaLocalidad() {}
  public ResumenVentaLocalidad(String codigoLocalidad, int vendidos, double totalRecaudado) {
    this.codigoLocalidad = codigoLocalidad;
    this.vendidos = vendidos;
    this.totalRecaudado = totalRecaudado;
  }

  public String getCodigoLocalidad() {
    return codigoLocalidad;
  }

  public void setCodigoLocalidad(String codigoLocalidad) {
    this.codigoLocalidad = codigoLocalidad;
  }

  public int getVendidos() {
    return vendidos;
  }

  public void setVendidos(int vendidos) {
    this.vendidos = vendidos;
  }

  public double getTotalRecaudado() {
    return totalRecaudado;
  }

  public void setTotalRecaudado(double totalRecaudado) {
    this.totalRecaudado = totalRecaudado;
  }
}