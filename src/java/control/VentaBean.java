package control;

import dao.VentaDAO;
import dao.ProductoDAO;
import modelo.Producto;
import modelo.Venta;
import modelo.Venta_has_producto;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class VentaBean implements Serializable {
    
    

    private Venta ventaActual;
    private Venta ventaSeleccionada;
    private List<Venta> listaVentas;
    private List<Venta_has_producto> carrito;
    private List<Venta_has_producto> detallesVenta;

    private int usuarioSeleccionado;
    private Map<Integer, Number> cantidadesPorProducto;

    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;

    @ManagedProperty("#{productoBean}")
    private ProductoBean productoBean;

    @PostConstruct
    public void init() {
        ventaActual = new Venta();
        ventaActual.setFecha_factura(new Date());
        listaVentas = new ArrayList<>();
        carrito = new ArrayList<>();
        detallesVenta = new ArrayList<>();
        cantidadesPorProducto = new HashMap<>();
        ventaDAO = new VentaDAO();
        productoDAO = new ProductoDAO();
        listarVentas();
    }

    public void listarVentas() {
        listaVentas = ventaDAO.listar();
    }

    /** Guarda la venta y descuenta stock **/
    public void guardarVenta() {
        try {
            if (usuarioSeleccionado == 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe seleccionar un cliente", ""));
                return;
            }
            if (carrito == null || carrito.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe agregar productos a la venta", ""));
                return;
            }

            ventaActual.setUsuarios_id_usuario(usuarioSeleccionado);
            ventaActual.setSubtotal(getSubtotal());
            ventaActual.setValor_total(getTotal());

            ventaDAO.agregar(ventaActual, carrito);

            if (productoBean != null) productoBean.cargarProductos();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Venta registrada exitosamente", null));

            limpiarFormulario();
            listarVentas();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    private void limpiarFormulario() {
        ventaActual = new Venta();
        ventaActual.setFecha_factura(new Date());
        carrito = new ArrayList<>();
        cantidadesPorProducto = new HashMap<>();
        usuarioSeleccionado = 0;
    }

    /** Agrega un producto al carrito **/
    public void agregarProducto(Producto p) {
        int cantidad = getCantidadProducto(p.getIdproducto());
        if (cantidad <= 0) cantidad = 1;
        if (p.getStock() < cantidad) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Stock insuficiente. Disponible: " + p.getStock(), ""));
            return;
        }

        Venta_has_producto existente = carrito.stream()
                .filter(item -> item.getProducto().getIdproducto() == p.getIdproducto())
                .findFirst().orElse(null);

        if (existente != null) {
            int nuevaCantidad = existente.getCantidad() + cantidad;
            if (nuevaCantidad > p.getStock()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se puede superar el stock disponible: " + p.getStock(), ""));
                return;
            }
            existente.setCantidad(nuevaCantidad);
        } else {
            Venta_has_producto nuevo = new Venta_has_producto();
            nuevo.setProducto(p);
            nuevo.setCantidad(cantidad);
            nuevo.setValor_unitario(p.getPrecio_producto());
            carrito.add(nuevo);
        }
        cantidadesPorProducto.put(p.getIdproducto(), 1);
    }

    public void eliminarDelCarrito(Venta_has_producto item) {
        carrito.remove(item);
    }

    public double getSubtotal() {
        return carrito.stream().mapToDouble(i -> i.getCantidad() * i.getValor_unitario()).sum();
    }

    public double getTotal() {
        double total = getSubtotal();
        if (ventaActual.getDescuento() > 0) total -= getSubtotal() * ventaActual.getDescuento() / 100;
        if (ventaActual.getAbono() > 0) total -= ventaActual.getAbono();
        return Math.max(total, 0);
    }

    public int getCantidadProducto(int idProducto) {
        Number n = cantidadesPorProducto.get(idProducto);
        return (n == null) ? 1 : Math.max(n.intValue(), 1);
    }

    /** Preparar y ver detalle **/
public String verDetalle(Venta venta) {
    System.out.println("VER DETALLE → ID factura: " + venta.getIdfactura());
    this.ventaSeleccionada = venta;
    this.detallesVenta = ventaDAO.listarDetalle(venta.getIdfactura());
    System.out.println("VER DETALLE → Detalles obtenidos: " + detallesVenta.size());
    for (Venta_has_producto d : detallesVenta) {
        System.out.println("Producto: " + d.getProducto().getNombre_producto() +
                           ", Cantidad: " + d.getCantidad());
    }
    return "/ventas/detalleVenta.xhtml?faces-redirect=true";
}




    /** Eliminar venta y devolver stock **/
    public void eliminarVenta(int idVenta) {
        try {
            ventaDAO.eliminar(idVenta);
            listarVentas();
            if (productoBean != null) productoBean.cargarProductos();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Venta eliminada y stock devuelto", null));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error eliminando: " + e.getMessage(), null));
        }
    }

    // ==== GETTERS / SETTERS ====
    public Venta getVentaActual() { return ventaActual; }
    public void setVentaActual(Venta ventaActual) { this.ventaActual = ventaActual; }
    public List<Venta> getListaVentas() { return listaVentas; }
    public void setListaVentas(List<Venta> listaVentas) { this.listaVentas = listaVentas; }
    public List<Venta_has_producto> getCarrito() { return carrito; }
    public void setCarrito(List<Venta_has_producto> carrito) { this.carrito = carrito; }
    public int getUsuarioSeleccionado() { return usuarioSeleccionado; }
    public void setUsuarioSeleccionado(int usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }
    public Map<Integer, Number> getCantidadesPorProducto() { return cantidadesPorProducto; }
    public void setCantidadesPorProducto(Map<Integer, Number> cantidadesPorProducto) { this.cantidadesPorProducto = cantidadesPorProducto; }
    public ProductoBean getProductoBean() { return productoBean; }
    public void setProductoBean(ProductoBean productoBean) { this.productoBean = productoBean; }
    public Venta getVentaSeleccionada() { return ventaSeleccionada; }
    public void setVentaSeleccionada(Venta ventaSeleccionada) { this.ventaSeleccionada = ventaSeleccionada; }
    public List<Venta_has_producto> getDetallesVenta() { return detallesVenta; }
    public void setDetallesVenta(List<Venta_has_producto> detallesVenta) { this.detallesVenta = detallesVenta; }
}
