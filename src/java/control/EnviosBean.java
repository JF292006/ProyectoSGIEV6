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
import java.io.File;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import modelo.Envio;
import modelo.Mensajeria;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@ManagedBean
@SessionScoped
public class EnviosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Envio envio = new Envio();
    private List<Envio> listaEnvios = new ArrayList<>();
    private List<Mensajeria> listaMensajerias = new ArrayList<>();
    private EnviosDAO enviosDAO = new EnviosDAO(); // ✅ aquí declaras el DAO


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
    
    public void exportarPDF() {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reporteEnv.jasper");
            File jasper = new File(path);
            EnvioDataSource pds = new EnvioDataSource();

            JasperPrint jprint = JasperFillManager.fillReport(jasper.getPath(), null, pds);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jprint);

            HttpServletResponse resp = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

            resp.setContentType("application/pdf");
            resp.addHeader("Content-Disposition", "attachment; filename=\"Envios.pdf\"");

            try (ServletOutputStream stream = resp.getOutputStream()) {
                JasperExportManager.exportReportToPdfStream(jprint, stream);

                stream.flush();
                stream.close();
            }
            FacesContext.getCurrentInstance().responseComplete();

        } catch (JRException | IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error creando reporte"));
        }
    }
    
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
    public void irEditarEnvio(Envio envioSeleccionado) {
        try {
            this.envio = envioSeleccionado; // carga los datos en el bean
            listarMensajerias(); // recarga la lista para el select
            FacesContext.getCurrentInstance().getExternalContext().redirect("editarEnvio.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Actualizar envío
    public String actualizarEnvio() {
    try {
        if (enviosDAO.editarEnvio(envio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Envío actualizado correctamente", null));
            listarEnvios();
            return "listarEnvios?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo actualizar el envío", null));
            return null;
        }
    } catch (Exception e) {
        e.printStackTrace();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error actualizando el envío", e.getMessage()));
        return null;
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
    
    public void irAgregarEnviooperario() {
        try {
            envio = new Envio();
            listarMensajerias();
            FacesContext.getCurrentInstance().getExternalContext().redirect("agregarEnviooperario.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void irEditarEnviooperario(Envio envioSeleccionado) {
        try {
            this.envio = envioSeleccionado; // carga los datos en el bean
            listarMensajerias(); // recarga la lista para el select
            FacesContext.getCurrentInstance().getExternalContext().redirect("editarEnviooperario.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String guardarEnviooperario() {
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
        return "/envios/listarEnviosoperario?faces-redirect=true";

    } catch (Exception e) {
        e.printStackTrace(); // ✅ mostrará el error real en la consola GlassFish
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar el envío", e.getMessage()));
        return null;
    }
}
    
    public String actualizarEnviooperario() {
        try {
            if (enviosDAO.editarEnvio(envio)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Envío actualizado correctamente", null));
                listarEnviosoperario(); // 👈 ahora sí usa el listado de operario
                return "listarEnviosoperario?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo actualizar el envío", null));
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error actualizando el envío", e.getMessage()));
            return null;
        }
    }
    
    public void eliminarEnviooperario(int id) {
        try (Connection con = ConDB.conectar()) {
            String sql = "DELETE FROM envio WHERE idenvio=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Envío eliminado correctamente", null));

            listarEnviosoperario();
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error eliminando envío", e.getMessage()));
        }
    }
    
    public void listarEnviosoperario() {
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

}
