package control;

import dao.ProductoDAO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import modelo.Producto;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@ManagedBean
@ApplicationScoped
public class ProductoBean {

    private final ProductoDAO prodDAO = new ProductoDAO();
    private Producto producto = new Producto();
    private List<Producto> lstProds = new ArrayList<>();

    public void exportarPDF() {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reporte.jasper");
            File jasper = new File(path);
            ProductoDataSource pds = new ProductoDataSource();

            JasperPrint jprint = JasperFillManager.fillReport(jasper.getPath(), null, pds);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jprint);

            HttpServletResponse resp = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            resp.setContentType("application/pdf");
            resp.addHeader("Content-Disposition", "attachment; filename=\"Productos.pdf\"");

            try (ServletOutputStream stream = resp.getOutputStream()) {
                JasperExportManager.exportReportToPdfStream(jprint, stream);
                stream.flush();
            }
            FacesContext.getCurrentInstance().responseComplete();

        } catch (JRException | IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error creando reporte"));
        }
    }

    @PostConstruct
    public void init() {
        listar();
    }

    public void listar() {
        lstProds = prodDAO.listar();
    }

    public void agregar() {
        prodDAO.agregar(producto);
        listar();
    }

    public void editar(Producto prod) {
        producto = prodDAO.buscar(prod.getIdproducto());
        listar();
    }

    public void actualizar() {
        prodDAO.actualizar(producto);
        listar();
    }

    public void eliminar(Producto prod) {
        prodDAO.eliminar(prod);
        listar();
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Producto> getLstProds() {
        return lstProds;
    }

    public void setLstProds(List<Producto> lstProds) {
        this.lstProds = lstProds;
    }

    public void limpiar() {
        producto = new Producto();
    }

    // ✅ NUEVO: Método para recargar productos desde otros beans (p.ej., VentaBean)
    public void cargarProductos() {
        listar(); // reutiliza el método existente para no duplicar código
    }
}
