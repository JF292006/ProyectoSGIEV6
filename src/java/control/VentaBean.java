package control;

import dao.VentaDAO;
import dao.ProductoDAO;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import modelo.Producto;
import modelo.Usuarios;
import modelo.Venta;
import modelo.Venta_has_producto;

@ManagedBean
@ViewScoped
public class VentaBean implements Serializable {

    private Venta ventaActual;
    private List<Venta> listaVentas;
    private List<Venta_has_producto> carrito;  // Carrito de productos
    private Producto productoSeleccionado;
    private int cantidadSeleccionada;
    private int usuarioSeleccionado;

    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;

    @PostConstruct
    public void init() {
        ventaActual = new Venta();
        ventaActual.setFecha_factura(new Date());
        listaVentas = new ArrayList<>();
        carrito = new ArrayList<>();
        ventaDAO = new VentaDAO();
        productoDAO = new ProductoDAO();
        listarVentas();
    }

    // ------------------ CRUD VENTAS ------------------

    public void listarVentas() {
        listaVentas = ventaDAO.listar();
    }

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

            // Asociar cliente
            ventaActual.setUsuarios_id_usuario(usuarioSeleccionado);

            // Calcular totales
            ventaActual.setSubtotal(getSubtotal());
            ventaActual.setValor_total(getTotal());

            ventaDAO.agregar(ventaActual, carrito);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Venta registrada exitosamente", null));

            // Reset
            ventaActual = new Venta();
            ventaActual.setFecha_factura(new Date());
            carrito = new ArrayList<>();
            listarVentas();

        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error registrando venta", e.getMessage()));
        }
    }

    public void eliminarVenta(int idVenta) {
        try {
            ventaDAO.eliminar(idVenta);
            listarVentas();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Venta eliminada", ""));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error eliminando venta", e.getMessage()));
        }
    }

    public void verDetalle(Venta v) {
        try {
            this.ventaActual = ventaDAO.buscar(v.getIdfactura());
            this.carrito = ventaDAO.listarDetalle(v.getIdfactura());
            FacesContext.getCurrentInstance().getExternalContext().redirect("detalleVenta.xhtml");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error cargando detalle", e.getMessage()));
        }
    }

    // ------------------ CARRITO ------------------

    public void agregarProducto(Producto p) {
        if (cantidadSeleccionada <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "La cantidad debe ser mayor a 0", ""));
            return;
        }

        // Verificar stock
        if (p.getStock() < cantidadSeleccionada) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Stock insuficiente. Disponible: " + p.getStock(), ""));
            return;
        }

        Venta_has_producto item = null;
        for (Venta_has_producto vhp : carrito) {
            if (vhp.getProducto().getIdproducto() == p.getIdproducto()) {
                item = vhp;
                break;
            }
        }

        if (item == null) {
            item = new Venta_has_producto();
            item.setProducto(p);
            item.setCantidad(cantidadSeleccionada);
            item.setValor_unitario(p.getPrecio_producto());
            carrito.add(item);
        } else {
            int nuevaCantidad = item.getCantidad() + cantidadSeleccionada;

            if (nuevaCantidad > p.getStock()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "No se puede agregar más del stock disponible (" + p.getStock() + ")", ""));
                return;
            }
            item.setCantidad(nuevaCantidad);
        }

        cantidadSeleccionada = 0;
    }

    public void eliminarDelCarrito(Venta_has_producto item) {
        carrito.remove(item);
    }

    // ------------------ CALCULOS ------------------

    public double getSubtotal() {
        double subtotal = 0.0;
        if (carrito != null) {
            for (Venta_has_producto item : carrito) {
                subtotal += item.getCantidad() * item.getValor_unitario();
            }
        }
        return subtotal;
    }

    public double getTotal() {
        double total = getSubtotal();
        if (ventaActual != null) {
            total -= ventaActual.getDescuento();
            total -= ventaActual.getAbono();
        }
        return total;
    }

    // ------------------ GETTERS Y SETTERS ------------------

    public Venta getVentaActual() {
        return ventaActual;
    }

    public void setVentaActual(Venta ventaActual) {
        this.ventaActual = ventaActual;
    }

    public List<Venta> getListaVentas() {
        return listaVentas;
    }

    public void setListaVentas(List<Venta> listaVentas) {
        this.listaVentas = listaVentas;
    }

    public List<Venta_has_producto> getCarrito() {
        return carrito;
    }

    public void setCarrito(List<Venta_has_producto> carrito) {
        this.carrito = carrito;
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(Producto productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public int getCantidadSeleccionada() {
        return cantidadSeleccionada;
    }

    public void setCantidadSeleccionada(int cantidadSeleccionada) {
        this.cantidadSeleccionada = cantidadSeleccionada;
    }

    public int getUsuarioSeleccionado() {
        return usuarioSeleccionado;
    }

    public void setUsuarioSeleccionado(int usuarioSeleccionado) {
        this.usuarioSeleccionado = usuarioSeleccionado;
    }
}
