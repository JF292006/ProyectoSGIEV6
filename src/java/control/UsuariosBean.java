
package control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import modelo.Usuarios;

@ManagedBean
public class UsuariosBean {
    Usuarios usuarios = new Usuarios();

    public Usuarios getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Usuarios usuarios) {
        this.usuarios = usuarios;
    }
    
    public void autenticar(){
        try {
            Connection con = ConDB.conectar();
            
            String sql = "SELECT * FROM usuarios WHERE correo = ? AND clave = ?";            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuarios.getCorreo());  
            String pw = Utilidades.encriptar(usuarios.getClave());
            ps.setString(2, pw);
                        
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", rs.getString("p_nombre"));
                
                switch(rs.getString("tipo_usu")){
                    case "operario":
                        FacesContext.getCurrentInstance().getExternalContext().redirect("operario.xhtml");
                        break;
                    case "administrador":
                        FacesContext.getCurrentInstance().getExternalContext().redirect("admin.xhtml");
                        break; 
                }
            }else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Correo y/o Contraseña no válidos", "Aviso"));
            }            
        } catch (SQLException | IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error accediendo a la Base de Datos", "Error"));
        }
    }
    
    public void verifSesion(){
        String nom = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user");
        
        if(nom == null){
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("noacceso.xhtml");
            } catch (IOException ex) {                
            }
        }
    }
    
    public void cerrarSesion(){
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
         try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            } catch (IOException ex) {                
            }
    }
}
