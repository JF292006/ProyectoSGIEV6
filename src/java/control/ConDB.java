/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import com.mysql.cj.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class ConDB {
    static public Connection conectar(){
        
        Connection conn = null;
        
        try {
            Driver drv = new Driver();
            DriverManager.registerDriver(drv);
            
            String url = "jdbc:mysql://localhost:3306/proyecto?user=root&useSSL=false";
            conn = DriverManager.getConnection(url);           
            
        } catch (SQLException e) {
            System.out.println("Error en Conexión");
        }
        
        return conn;
    }
}
