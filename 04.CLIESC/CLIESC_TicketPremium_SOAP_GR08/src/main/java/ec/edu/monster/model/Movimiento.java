package ec.edu.monster.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "movimiento")
@XmlAccessorType(XmlAccessType.FIELD)
public class Movimiento {
  private String cuenta;
  private int nromov;
  private Date fecha;
  private String tipo;
  private String accion;
  private double importe;

  public String getCuenta() { return cuenta; }
  public void setCuenta(String cuenta) { this.cuenta = cuenta; }
  public int getNromov() { return nromov; }
  public void setNromov(int nromov) { this.nromov = nromov; }
  public Date getFecha() { return fecha; }
  public void setFecha(Date fecha) { this.fecha = fecha; }
  public String getTipo() { return tipo; }
  public void setTipo(String tipo) { this.tipo = tipo; }
  public String getAccion() { return accion; }
  public void setAccion(String accion) { this.accion = accion; }
  public double getImporte() { return importe; }
  public void setImporte(double importe) { this.importe = importe; }
}
