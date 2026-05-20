package ec.edu.monster.test;

import ec.edu.monster.db.AccesoDB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Test class to verify database connectivity
 */
public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("\n=== Database Connection Test ===\n");
        
        try {
            System.out.println("1. Attempting to get connection...");
            Connection conn = AccesoDB.getConnection();
            System.out.println("✓ Connection successful!");
            System.out.println("  Connection: " + conn);
            
            System.out.println("\n2. Testing PARTIDO_FUTBOL table...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM PARTIDO_FUTBOL");
            if (rs.next()) {
                int count = rs.getInt("total");
                System.out.println("✓ PARTIDO_FUTBOL count: " + count);
            }
            rs.close();
            
            System.out.println("\n3. Testing LOCALIDAD_PARTIDO table...");
            rs = stmt.executeQuery("SELECT COUNT(*) as total FROM LOCALIDAD_PARTIDO");
            if (rs.next()) {
                int count = rs.getInt("total");
                System.out.println("✓ LOCALIDAD_PARTIDO count: " + count);
            }
            rs.close();
            
            System.out.println("\n4. Listing first 3 PARTIDO_FUTBOL records...");
            rs = stmt.executeQuery("SELECT CODIGO, EQUIPO_LOCAL, EQUIPO_VISITANTE, FECHA, LUGAR FROM PARTIDO_FUTBOL LIMIT 3");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("CODIGO") + ": " + 
                                 rs.getString("EQUIPO_LOCAL") + " vs " + 
                                 rs.getString("EQUIPO_VISITANTE"));
            }
            rs.close();
            
            stmt.close();
            conn.close();
            System.out.println("\n✓ All tests passed!");
            
        } catch (Exception e) {
            System.out.println("\n✗ ERROR: " + e.getMessage());
            System.out.println("\nStacktrace:");
            e.printStackTrace();
            
            System.out.println("\nPossible causes:");
            System.out.println("1. MySQL server is not running");
            System.out.println("2. Database bd_examen_ticket doesn't exist");
            System.out.println("3. User MONSTER doesn't exist or has wrong password");
            System.out.println("4. Tables don't exist or have wrong structure");
        }
    }
}
