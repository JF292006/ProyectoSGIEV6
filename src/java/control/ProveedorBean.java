
package control;

import dao.ProveedorDAO;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import modelo.Proveedor;

@ManagedBean
@ApplicationScoped
public class ProveedorBean {
    private final ProveedorDAO provDAO = new ProveedorDAO();
    private Proveedor proveedor = new Proveedor();
    private List<Proveedor> lstProvs = new ArrayList<>();
    
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
