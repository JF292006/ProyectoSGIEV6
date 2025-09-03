
package control;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import modelo.Usuarios;

@ManagedBean
@ViewScoped
public class UsuariosBean implements Serializable {
    private static final long serialVersionUID = 1L;
    Usuarios usuarios = new Usuarios();
    private List<Usuarios> listaUsuarios = new ArrayList<>();
    
    @PostConstruct
    public void init() {
        listarUsuarios();
    }

    public Usuarios getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Usuarios usuarios) {
        this.usuarios = usuarios;
    }
    
    public List<Usuarios> getListaUsuarios() {
        listarUsuarios(); 
        return listaUsuarios;
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
    
    public void listarUsuarios() {
        listaUsuarios = new ArrayList<>();
        try (Connection con = ConDB.conectar()) {
            String sql = "SELECT id_usuario, tipo_usu, p_nombre, p_apellido, telefono, salario FROM usuarios";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Usuarios u = new Usuarios();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setTipo_usu(rs.getString("tipo_usu"));
                u.setP_nombre(rs.getString("p_nombre"));
                u.setP_apellido(rs.getString("p_apellido"));
                u.setTelefono(rs.getLong("telefono"));
                u.setSalario(rs.getLong("salario"));

                listaUsuarios.add(u);
            }
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error listando usuarios", e.getMessage()));
        }
    }
    
    public void volverAdmin() {
    try {
        FacesContext.getCurrentInstance().getExternalContext().redirect("admin.xhtml");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    

}
