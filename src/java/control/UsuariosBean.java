
package control;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import javax.faces.context.FacesContext;
import modelo.Usuarios;

@ManagedBean
@SessionScoped
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
    
    public void autenticar() {
    try (Connection con = ConDB.conectar()) {

        String sql = "SELECT * FROM usuarios WHERE correo = ? AND clave = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, usuarios.getCorreo());
        String pw = Utilidades.encriptar(usuarios.getClave());
        ps.setString(2, pw);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            // Crear objeto Usuarios con todos los datos que necesites
            Usuarios u = new Usuarios();
            u.setId_usuario(rs.getInt("id_usuario"));
            u.setTipo_usu(rs.getString("tipo_usu"));
            u.setP_nombre(rs.getString("p_nombre"));
            u.setS_nombre(rs.getString("s_nombre"));
            u.setP_apellido(rs.getString("p_apellido"));
            u.setS_apellido(rs.getString("s_apellido"));
            u.setCorreo(rs.getString("correo"));
            u.setTelefono(rs.getLong("telefono"));
            u.setSalario(rs.getLong("salario"));
            u.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
            u.setDireccion(rs.getString("direccion"));
            // NOTA: NO guardes la clave en sesión por seguridad

            // Guardar el objeto completo en sesión
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", u);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userId", u.getId_usuario());

            // Redireccionar según tipo
            switch (rs.getString("tipo_usu")) {
                case "operario":
                    FacesContext.getCurrentInstance().getExternalContext().redirect("operario.xhtml");
                    break;
                case "administrador":
                    FacesContext.getCurrentInstance().getExternalContext().redirect("admin.xhtml");
                    break;
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Correo y/o Contraseña no válidos", "Aviso"));
        }

    } catch (SQLException | IOException e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Error accediendo a la Base de Datos", "Error"));
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
    
    public String cerrarSesion() {
    // Invalida toda la sesión
    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    // Retorna la página de login/index con redirect
    return "/index.xhtml?faces-redirect=true";
}


    public void listarUsuarios() {
        listaUsuarios = new ArrayList<>();
        try (Connection con = ConDB.conectar()) {
            // Incluimos num_identificacion y correo en la consulta
            String sql = "SELECT id_usuario, num_identificacion, tipo_usu, p_nombre, p_apellido, correo, telefono, salario FROM usuarios";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Usuarios u = new Usuarios();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNum_identificacion(rs.getLong("num_identificacion")); // <-- nuevo
                u.setTipo_usu(rs.getString("tipo_usu"));
                u.setP_nombre(rs.getString("p_nombre"));
                u.setP_apellido(rs.getString("p_apellido"));
                u.setCorreo(rs.getString("correo")); // <-- nuevo
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
    
    public void irAgregarUsuario() {
        try {
            usuarios = new Usuarios();
            FacesContext.getCurrentInstance().getExternalContext().redirect("agregarusuario.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
     public void guardarUsuario() {
        try (Connection con = ConDB.conectar()) {
            String sql = "INSERT INTO usuarios (num_identificacion, tipo_usu, clave, p_nombre, s_nombre, p_apellido, s_apellido, correo, telefono, salario, fecha_nacimiento, direccion) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, usuarios.getNum_identificacion());
            ps.setString(2, usuarios.getTipo_usu());

            
            String pw = Utilidades.encriptar(usuarios.getClave());
            ps.setString(3, pw);

            ps.setString(4, usuarios.getP_nombre());
            ps.setString(5, usuarios.getS_nombre());
            ps.setString(6, usuarios.getP_apellido());
            ps.setString(7, usuarios.getS_apellido());
            ps.setString(8, usuarios.getCorreo());
            ps.setLong(9, usuarios.getTelefono());
            ps.setLong(10, usuarios.getSalario());

            
            if (usuarios.getFecha_nacimiento() != null) {
                ps.setDate(11, new java.sql.Date(usuarios.getFecha_nacimiento().getTime()));
            } else {
                ps.setDate(11, null);
            }

            ps.setString(12, usuarios.getDireccion());

            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario registrado correctamente", null));

           
            listarUsuarios();
            FacesContext.getCurrentInstance().getExternalContext().redirect("listarusuarios.xhtml");

        } catch (SQLException | IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error registrando usuario", e.getMessage()));
        }
    }
     
     public void volverListado() {
    try {
        FacesContext.getCurrentInstance().getExternalContext().redirect("listarusuarios.xhtml");
    } catch (IOException e) {
        e.printStackTrace();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo volver al listado", e.getMessage()));
    }
}
     
     public void editarUsuario(Usuarios u) {
    try (Connection con = ConDB.conectar()) {
        String sql = "SELECT id_usuario, num_identificacion, tipo_usu, p_nombre, s_nombre, p_apellido, s_apellido, correo, telefono, salario, fecha_nacimiento, direccion " +
                     "FROM usuarios WHERE id_usuario = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, u.getId_usuario());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Usuarios usuarioCompleto = new Usuarios();
            usuarioCompleto.setId_usuario(rs.getInt("id_usuario"));
            usuarioCompleto.setNum_identificacion(rs.getInt("num_identificacion"));
            usuarioCompleto.setTipo_usu(rs.getString("tipo_usu"));
            usuarioCompleto.setP_nombre(rs.getString("p_nombre"));
            usuarioCompleto.setS_nombre(rs.getString("s_nombre"));
            usuarioCompleto.setP_apellido(rs.getString("p_apellido"));
            usuarioCompleto.setS_apellido(rs.getString("s_apellido"));
            usuarioCompleto.setCorreo(rs.getString("correo"));
            usuarioCompleto.setTelefono(rs.getLong("telefono"));
            usuarioCompleto.setSalario(rs.getLong("salario"));
            usuarioCompleto.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
            usuarioCompleto.setDireccion(rs.getString("direccion"));
            // NO cargamos la clave por seguridad

            this.usuarios = usuarioCompleto; // asignamos el usuario completo al bean
        }

        FacesContext.getCurrentInstance().getExternalContext().redirect("editarusuario.xhtml");

    } catch (SQLException | IOException e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar usuario", e.getMessage()));
    }
}


    public void actualizarUsuario() {
    try (Connection con = ConDB.conectar()) {

        String sql;
        PreparedStatement ps;

        if (usuarios.getClave() != null && !usuarios.getClave().isEmpty()) {
            // Si escribió una nueva clave → se actualiza todo incluyendo clave
            sql = "UPDATE usuarios SET num_identificacion=?, tipo_usu=?, clave=?, p_nombre=?, s_nombre=?, p_apellido=?, s_apellido=?, correo=?, telefono=?, salario=?, fecha_nacimiento=?, direccion=? WHERE id_usuario=?";
            ps = con.prepareStatement(sql);

            ps.setLong(1, usuarios.getNum_identificacion());
            ps.setString(2, usuarios.getTipo_usu());
            String pw = Utilidades.encriptar(usuarios.getClave());
            ps.setString(3, pw);
            ps.setString(4, usuarios.getP_nombre());
            ps.setString(5, usuarios.getS_nombre());
            ps.setString(6, usuarios.getP_apellido());
            ps.setString(7, usuarios.getS_apellido());
            ps.setString(8, usuarios.getCorreo());
            ps.setLong(9, usuarios.getTelefono());
            ps.setLong(10, usuarios.getSalario());

            if (usuarios.getFecha_nacimiento() != null) {
                ps.setDate(11, new java.sql.Date(usuarios.getFecha_nacimiento().getTime()));
            } else {
                ps.setDate(11, null);
            }

            ps.setString(12, usuarios.getDireccion());
            ps.setInt(13, usuarios.getId_usuario());

        } else {
            // Si NO escribió clave → no se toca el campo clave
            sql = "UPDATE usuarios SET num_identificacion=?, tipo_usu=?, p_nombre=?, s_nombre=?, p_apellido=?, s_apellido=?, correo=?, telefono=?, salario=?, fecha_nacimiento=?, direccion=? WHERE id_usuario=?";
            ps = con.prepareStatement(sql);

            ps.setLong(1, usuarios.getNum_identificacion());
            ps.setString(2, usuarios.getTipo_usu());
            ps.setString(3, usuarios.getP_nombre());
            ps.setString(4, usuarios.getS_nombre());
            ps.setString(5, usuarios.getP_apellido());
            ps.setString(6, usuarios.getS_apellido());
            ps.setString(7, usuarios.getCorreo());
            ps.setLong(8, usuarios.getTelefono());
            ps.setLong(9, usuarios.getSalario());

            if (usuarios.getFecha_nacimiento() != null) {
                ps.setDate(10, new java.sql.Date(usuarios.getFecha_nacimiento().getTime()));
            } else {
                ps.setDate(10, null);
            }

            ps.setString(11, usuarios.getDireccion());
            ps.setInt(12, usuarios.getId_usuario());
        }

        ps.executeUpdate();

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario actualizado correctamente", null));

        listarUsuarios();
        FacesContext.getCurrentInstance().getExternalContext().redirect("listarusuarios.xhtml");

    } catch (SQLException | IOException e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error actualizando usuario", e.getMessage()));
    }
}

    public void eliminarUsuario(int id) {
        try (Connection con = ConDB.conectar()) {
            String sql = "DELETE FROM usuarios WHERE id_usuario=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario eliminado correctamente", null));

            listarUsuarios();
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error eliminando usuario", e.getMessage()));
        }
    }
    
    public void cargaMasivaUsuarios(FileUploadEvent event) {
        UploadedFile file = event.getFile();

        try (Workbook workbook = new HSSFWorkbook(file.getInputStream()); Connection con = ConDB.conectar()) {

            Sheet sheet = workbook.getSheetAt(0);
            String sql = "INSERT INTO usuarios (num_identificacion, tipo_usu, clave, p_nombre, s_nombre, p_apellido, s_apellido, correo, telefono, salario, fecha_nacimiento, direccion) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            DataFormatter formatter = new DataFormatter();

            int filasInsertadas = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Saltar header
                }
                try {
                    // Leer celdas
                    String idStr = formatter.formatCellValue(row.getCell(0)).trim();
                    String tipoUsu = formatter.formatCellValue(row.getCell(1)).trim();
                    String clave = formatter.formatCellValue(row.getCell(2)).trim();
                    String pNombre = formatter.formatCellValue(row.getCell(3)).trim();
                    String sNombre = formatter.formatCellValue(row.getCell(4)).trim();
                    String pApellido = formatter.formatCellValue(row.getCell(5)).trim();
                    String sApellido = formatter.formatCellValue(row.getCell(6)).trim();
                    String correo = formatter.formatCellValue(row.getCell(7)).trim();
                    String telStr = formatter.formatCellValue(row.getCell(8)).trim();
                    String salarioStr = formatter.formatCellValue(row.getCell(9)).trim();
                    Cell fechaCell = row.getCell(10);
                    String direccion = formatter.formatCellValue(row.getCell(11)).trim();

                    if (idStr.isEmpty() && tipoUsu.isEmpty() && clave.isEmpty() && pNombre.isEmpty()
                            && pApellido.isEmpty() && correo.isEmpty() && telStr.isEmpty()) {
                        continue; // Fila vacía
                    }

                    long numIdentificacion = Long.parseLong(idStr.replaceAll("\\D", ""));
                    long telefono = Long.parseLong(telStr.replaceAll("\\D", ""));
                    long salario = salarioStr.isEmpty() ? 0 : Long.parseLong(salarioStr);
                    String claveEncriptada = clave.isEmpty() ? "" : Utilidades.encriptar(clave);

                    // Leer fecha
                    java.sql.Date fechaNacimiento = null;
                    if (fechaCell != null && fechaCell.getCellType() != Cell.CELL_TYPE_BLANK) {
                        if (DateUtil.isCellDateFormatted(fechaCell)) {
                            java.util.Date utilDate = fechaCell.getDateCellValue();
                            fechaNacimiento = new java.sql.Date(utilDate.getTime());
                        } else {
                            String fechaStr = formatter.formatCellValue(fechaCell).trim();
                            java.util.Date utilDate = null;
                            // Intentar varios formatos
                            String[] formatos = {"yyyy-MM-dd", "dd/MM/yyyy", "MM/dd/yy"};
                            for (String f : formatos) {
                                try {
                                    utilDate = new SimpleDateFormat(f).parse(fechaStr);
                                    break; // salió bien
                                } catch (ParseException e) {
                                    // Ignorar y probar siguiente formato
                                }
                            }
                            if (utilDate == null) {
                                throw new Exception("Fecha inválida: " + fechaStr);
                            }
                            fechaNacimiento = new java.sql.Date(utilDate.getTime());
                        }
                    }

                    // Preparar y ejecutar statement
                    ps.setLong(1, numIdentificacion);
                    ps.setString(2, tipoUsu);
                    ps.setString(3, claveEncriptada);
                    ps.setString(4, pNombre);
                    ps.setString(5, sNombre);
                    ps.setString(6, pApellido);
                    ps.setString(7, sApellido);
                    ps.setString(8, correo);
                    ps.setLong(9, telefono);
                    ps.setLong(10, salario);
                    ps.setDate(11, fechaNacimiento);
                    ps.setString(12, direccion);

                    ps.executeUpdate();
                    filasInsertadas++;

                } catch (Exception filaEx) {
                    System.err.println("Error en fila " + (row.getRowNum() + 1) + ": " + filaEx.getMessage());
                }
            }

            System.out.println("Carga masiva finalizada. Filas insertadas: " + filasInsertadas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    
    
    

}
