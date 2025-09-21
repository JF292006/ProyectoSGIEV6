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
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import modelo.Mensajeria;

@ManagedBean
@SessionScoped
public class MensajeriaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Mensajeria mensajeria = new Mensajeria();
    private List<Mensajeria> listaMensajeria = new ArrayList<>();

    @PostConstruct
    public void init() {
        listarMensajeria();
    }

    public Mensajeria getMensajeria() {
        return mensajeria;
    }

    public void setMensajeria(Mensajeria mensajeria) {
        this.mensajeria = mensajeria;
    }

    public List<Mensajeria> getListaMensajeria() {
        listarMensajeria();
        return listaMensajeria;
    }

    // ========= MÉTODOS CRUD =========

    public void listarMensajeria() {
        listaMensajeria = new ArrayList<>();
        try (Connection con = ConDB.conectar()) {
            String sql = "SELECT idmensajeria, nombre_mensajeria, tel_mensajeria, direccion_mensajeria, cobertura FROM mensajeria";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Mensajeria m = new Mensajeria();
                m.setIdmensajeria(rs.getInt("idmensajeria"));
                m.setNombre_mensajeria(rs.getString("nombre_mensajeria"));
                m.setTel_mensajeria(rs.getLong("tel_mensajeria"));
                m.setDireccion_mensajeria(rs.getString("direccion_mensajeria"));
                m.setCobertura(rs.getString("cobertura"));

                listaMensajeria.add(m);
            }
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error listando mensajerías", e.getMessage()));
        }
    }

    public void irAgregarMensajeria() {
        try {
            mensajeria = new Mensajeria();
            FacesContext.getCurrentInstance().getExternalContext().redirect("agregarMensajeria.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void volverListado() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("listarMensajeria.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarMensajeria() {
        try (Connection con = ConDB.conectar()) {
            String sql = "INSERT INTO mensajeria (nombre_mensajeria, tel_mensajeria, direccion_mensajeria, cobertura) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, mensajeria.getNombre_mensajeria());
            ps.setLong(2, mensajeria.getTel_mensajeria());
            ps.setString(3, mensajeria.getDireccion_mensajeria());
            ps.setString(4, mensajeria.getCobertura());

            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Mensajería registrada correctamente", null));

            listarMensajeria();
            FacesContext.getCurrentInstance().getExternalContext().redirect("listarMensajeria.xhtml");

        } catch (SQLException | IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error registrando mensajería", e.getMessage()));
        }
    }

    public void editarMensajeria(Mensajeria m) {
        try (Connection con = ConDB.conectar()) {
            String sql = "SELECT idmensajeria, nombre_mensajeria, tel_mensajeria, direccion_mensajeria, cobertura FROM mensajeria WHERE idmensajeria=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, m.getIdmensajeria());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Mensajeria mCompleta = new Mensajeria();
                mCompleta.setIdmensajeria(rs.getInt("idmensajeria"));
                mCompleta.setNombre_mensajeria(rs.getString("nombre_mensajeria"));
                mCompleta.setTel_mensajeria(rs.getLong("tel_mensajeria"));
                mCompleta.setDireccion_mensajeria(rs.getString("direccion_mensajeria"));
                mCompleta.setCobertura(rs.getString("cobertura"));

                this.mensajeria = mCompleta;
            }

            FacesContext.getCurrentInstance().getExternalContext().redirect("editarMensajeria.xhtml");

        } catch (SQLException | IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar mensajería", e.getMessage()));
        }
    }

    public void actualizarMensajeria() {
        try (Connection con = ConDB.conectar()) {
            String sql = "UPDATE mensajeria SET nombre_mensajeria=?, tel_mensajeria=?, direccion_mensajeria=?, cobertura=? WHERE idmensajeria=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, mensajeria.getNombre_mensajeria());
            ps.setLong(2, mensajeria.getTel_mensajeria());
            ps.setString(3, mensajeria.getDireccion_mensajeria());
            ps.setString(4, mensajeria.getCobertura());
            ps.setInt(5, mensajeria.getIdmensajeria());

            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Mensajería actualizada correctamente", null));

            listarMensajeria();
            FacesContext.getCurrentInstance().getExternalContext().redirect("listarMensajeria.xhtml");

        } catch (SQLException | IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error actualizando mensajería", e.getMessage()));
        }
    }

    public void eliminarMensajeria(int id) {
        try (Connection con = ConDB.conectar()) {
            String sql = "DELETE FROM mensajeria WHERE idmensajeria=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Mensajería eliminada correctamente", null));

            listarMensajeria();

        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error eliminando mensajería", e.getMessage()));
        }
    }
    
    public void volverMyE() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("mensajesyenvios.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
