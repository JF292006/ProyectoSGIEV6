package control;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dao.EnviosDAO;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import modelo.Envio;
import modelo.Mensajeria;

@ManagedBean
@SessionScoped
public class EnviosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Envio envio = new Envio();
    private List<Envio> listaEnvios = new ArrayList<>();
    private List<Mensajeria> listaMensajerias = new ArrayList<>();

    @PostConstruct
    public void init() {
        listarEnvios();
        listarMensajerias();
    }

    // ===== Getters y Setters =====
    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    public List<Envio> getListaEnvios() {
        listarEnvios();
        return listaEnvios;
    }

    public List<Mensajeria> getListaMensajerias() {
        listarMensajerias();
        return listaMensajerias;
    }

    // ===== Métodos =====

    // Listar envíos
    public void listarEnvios() {
        listaEnvios = new ArrayList<>();
        try (Connection con = ConDB.conectar()) {
            String sql = "SELECT idenvio, estado_envio, fecha_envio, fecha_entrega, direccion_envio, direccion_salida, observaciones, novedades, fk_mensajeria, usuarios_id_usuario FROM envio";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Envio e = new Envio();
                e.setIdenvio(rs.getInt("idenvio"));
                e.setEstado_envio(rs.getString("estado_envio"));
                e.setFecha_envio(rs.getDate("fecha_envio"));
                e.setFecha_entrega(rs.getDate("fecha_entrega"));
                e.setDireccion_envio(rs.getString("direccion_envio"));
                e.setDireccion_salida(rs.getString("direccion_salida"));
                e.setObservaciones(rs.getString("observaciones"));
                e.setNovedades(rs.getString("novedades"));
                e.setFk_mensajeria(rs.getInt("fk_mensajeria"));
                e.setUsuarios_id_usuario(rs.getInt("usuarios_id_usuario"));
                listaEnvios.add(e);
            }

        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error listando envíos", e.getMessage()));
        }
    }

    // Listar mensajerías
    public void listarMensajerias() {
        listaMensajerias = new ArrayList<>();
        try (Connection con = ConDB.conectar()) {
            String sql = "SELECT idmensajeria, nombre_mensajeria FROM mensajeria";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Mensajeria m = new Mensajeria();
                m.setIdmensajeria(rs.getInt("idmensajeria"));
                m.setNombre_mensajeria(rs.getString("nombre_mensajeria"));
                listaMensajerias.add(m);
            }

        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error listando mensajerías", e.getMessage()));
        }
    }

    // Obtener ID del usuario logueado
    private int getUsuarioLogueadoId() {
        Object idObj = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userId");
        if (idObj != null) {
            return (int) idObj;
        } else {
            return 0; // Usuario no logueado
        }
    }

    // Ir a agregar envío
    public void irAgregarEnvio() {
        try {
            envio = new Envio();
            listarMensajerias();
            FacesContext.getCurrentInstance().getExternalContext().redirect("agregarEnvio.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Guardar envío
    public String guardarEnvio() {
    System.out.println("DEBUG: guardarEnvio() - entrando...");
    try {
        int userId = getUsuarioLogueadoId();
        System.out.println("DEBUG: userId = " + userId);

        if (userId == 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario no logueado", null));
            return null;
        }
        envio.setUsuarios_id_usuario(userId);

        EnviosDAO dao = new EnviosDAO();
        dao.agregarEnvio(envio); // Aquí puede estar el error

        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().getFlash().setKeepMessages(true);
        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Envío registrado correctamente", null));

        envio = new Envio(); // limpiar
        System.out.println("DEBUG: guardarEnvio() - insert ok");
        return "/envios/listarEnvios?faces-redirect=true";

    } catch (Exception e) {
        e.printStackTrace(); // ✅ mostrará el error real en la consola GlassFish
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar el envío", e.getMessage()));
        return null;
    }
}




    // Editar envío
    public void editarEnvio(Envio e) {
        try (Connection con = ConDB.conectar()) {
            String sql = "SELECT * FROM envio WHERE idenvio=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, e.getIdenvio());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                envio = new Envio();
                envio.setIdenvio(rs.getInt("idenvio"));
                envio.setEstado_envio(rs.getString("estado_envio"));
                envio.setFecha_envio(rs.getDate("fecha_envio"));
                envio.setFecha_entrega(rs.getDate("fecha_entrega"));
                envio.setDireccion_envio(rs.getString("direccion_envio"));
                envio.setDireccion_salida(rs.getString("direccion_salida"));
                envio.setObservaciones(rs.getString("observaciones"));
                envio.setNovedades(rs.getString("novedades"));
                envio.setFk_mensajeria(rs.getInt("fk_mensajeria"));
            }

            FacesContext.getCurrentInstance().getExternalContext().redirect("editarEnvio.xhtml");

        } catch (SQLException | IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar envío", ex.getMessage()));
        }
    }

    // Actualizar envío
    public void actualizarEnvio() {
        try (Connection con = ConDB.conectar()) {
            String sql = "UPDATE envio SET estado_envio=?, fecha_envio=?, fecha_entrega=?, direccion_envio=?, direccion_salida=?, observaciones=?, novedades=?, fk_mensajeria=? WHERE idenvio=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, envio.getEstado_envio());
            ps.setDate(2, envio.getFecha_envio() != null ? new java.sql.Date(envio.getFecha_envio().getTime()) : null);
            ps.setDate(3, envio.getFecha_entrega() != null ? new java.sql.Date(envio.getFecha_entrega().getTime()) : null);
            ps.setString(4, envio.getDireccion_envio());
            ps.setString(5, envio.getDireccion_salida());
            ps.setString(6, envio.getObservaciones() != null ? envio.getObservaciones() : null);
            ps.setString(7, envio.getNovedades() != null ? envio.getNovedades() : null);
            ps.setInt(8, envio.getFk_mensajeria());
            ps.setInt(9, envio.getIdenvio());

            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Envío actualizado correctamente", null));

            listarEnvios();
            FacesContext.getCurrentInstance().getExternalContext().redirect("listarEnvios.xhtml");

        } catch (SQLException | IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error actualizando envío", e.getMessage()));
        }
    }

    // Eliminar envío
    public void eliminarEnvio(int id) {
        try (Connection con = ConDB.conectar()) {
            String sql = "DELETE FROM envio WHERE idenvio=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Envío eliminado correctamente", null));

            listarEnvios();
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error eliminando envío", e.getMessage()));
        }
    }

    // Volver al listado
    public void volverListado() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("listarEnvios.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void volverMyE() {
    try {
        FacesContext.getCurrentInstance().getExternalContext().redirect("/mensajeria/mensajesyenvios.xhtml");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
