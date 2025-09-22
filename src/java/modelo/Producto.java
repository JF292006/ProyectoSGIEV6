
package modelo;

public class Producto {
    
    private int idproducto, proveedor_idproveedor;
    private double precio_producto;
    private String nombre_producto, descripcion_producto, nombre_tipo, registrosanitario, nom_prov;
    Proveedor prov = new Proveedor();

    public String getNom_prov() {
        return nom_prov;
    }

    public void setNom_prov(String nom_prov) {
        this.nom_prov = nom_prov;
    }
    
    public Proveedor getProv() {
        return prov;
    }
    public void setProv(Proveedor prov) {
        this.prov = prov;
    }
    public int getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(int idproducto) {
        this.idproducto = idproducto;
    }

    public int getProveedor_idproveedor() {
        return proveedor_idproveedor;
    }

    public void setProveedor_idproveedor(int proveedor_idproveedor) {
        this.proveedor_idproveedor = proveedor_idproveedor;
    }

    public double getPrecio_producto() {
        return precio_producto;
    }

    public void setPrecio_producto(double precio_producto) {
        this.precio_producto = precio_producto;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public String getDescripcion_producto() {
        return descripcion_producto;
    }

    public void setDescripcion_producto(String descripcion_producto) {
        this.descripcion_producto = descripcion_producto;
    }

    public String getNombre_tipo() {
        return nombre_tipo;
    }

    public void setNombre_tipo(String nombre_tipo) {
        this.nombre_tipo = nombre_tipo;
    }

    public String getRegistrosanitario() {
        return registrosanitario;
    }

    public void setRegistrosanitario(String registrosanitario) {
        this.registrosanitario = registrosanitario;
    }
    
    
}
