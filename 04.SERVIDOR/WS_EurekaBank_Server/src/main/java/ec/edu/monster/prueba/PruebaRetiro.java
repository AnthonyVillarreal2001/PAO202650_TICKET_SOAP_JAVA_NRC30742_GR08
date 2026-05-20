/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.prueba;

import ec.edu.monster.servicio.TicketPremiumService;

/**
 *
 * @author JOIS
 */
public class PruebaRetiro {
    public static void main(String[] args) {
        try {
            //dato de la prueba
            String cuenta = "00100001";
            double importe = 10;
            String codEmp = "0001";
            
            //proceso
            TicketPremiumService service =new TicketPremiumService();
            service.registrarRetiro(cuenta, importe, codEmp);
            System.out.println("Proceso ok");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
