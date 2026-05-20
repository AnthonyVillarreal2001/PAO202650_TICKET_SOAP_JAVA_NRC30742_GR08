/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.prueba;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.servicio.TicketPremiumService;
import java.util.List;

/**
 *
 * @author JOIS
 */
public class PruebaMovimiento2 {
    
    public static void main(String[] args) {
        try {
            //dato de la prueba
            String cuenta = "00100001";
            double importe = 200;
            String codEmp = "0001";
            
            //proceso
            TicketPremiumService service =new TicketPremiumService();
            service.registrarDeposito(cuenta, importe, codEmp);
            System.out.println("Proceso ok");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
