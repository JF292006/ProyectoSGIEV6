
package control;

import dao.ProveedorDAO;
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
import modelo.Proveedor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@ManagedBean
@ApplicationScoped
public class ProveedorBean {
    private final ProveedorDAO provDAO = new ProveedorDAO();
    private Proveedor proveedor = new Proveedor();
    private List<Proveedor> lstProvs = new ArrayList<>();
    
    public void exportarPDF() {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reporteProv.jasper");
            File jasper = new File(path);
            ProveedorDataSource pds = new ProveedorDataSource();

            JasperPrint jprint = JasperFillManager.fillReport(jasper.getPath(), null, pds);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jprint);

            HttpServletResponse resp = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

            resp.setContentType("application/pdf");
            resp.addHeader("Content-Disposition", "attachment; filename=\"Proveedores.pdf\"");

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
    
     @PostConstruct
    public void init() {
        listar();
    }
    
    public void listar(){
        lstProvs = provDAO.listar();
    }
    
    public void agregar(){
        provDAO.agregar(proveedor);
        listar();
    }
    
    public void editar(Proveedor prov){
        proveedor = provDAO.buscar(prov);
        listar();
    }
    
    public void actualizar(){
        provDAO.actualizar(proveedor);
        listar();
    }
    
    public void eliminar(Proveedor prov){
        provDAO.eliminar(prov);
        listar();
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<Proveedor> getLstProvs() {
        return lstProvs;
    }

    public void setLstProvs(List<Proveedor> lstProvs) {
        this.lstProvs = lstProvs;
    }
    
    public void limpiar(){
        proveedor = new Proveedor();
    }
}
