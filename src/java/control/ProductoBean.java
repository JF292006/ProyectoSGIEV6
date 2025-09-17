package control;

import dao.ProductoDAO;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import modelo.Producto;

@ManagedBean
@ApplicationScoped
public class ProductoBean {

    private final ProductoDAO prodDAO = new ProductoDAO();
    private Producto producto = new Producto();
    private List<Producto> lstProds = new ArrayList<>();

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
}
