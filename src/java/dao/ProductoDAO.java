package dao;

import control.ConDB;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import modelo.Producto;

public class ProductoDAO {

    PreparedStatement ps;
    ResultSet rs;

    public List<Producto> listar() {
        List<Producto> listaProds = new ArrayList<>();

        try {
            String sql = "SELECT * FROM producto";
            ps = ConDB.conectar().prepareStatement(sql);

            rs = ps.executeQuery();

            while (rs.next()) {
                Producto prod = new Producto();
                prod.setIdproducto(rs.getInt("idproducto"));
                prod.setNombre_producto(rs.getString("nombre_producto"));
                prod.setPrecio_producto(rs.getDouble("precio_producto"));
                prod.setDescripcion_producto(rs.getString("descripcion_producto"));
                prod.setNombre_tipo(rs.getString("nombre_tipo"));
                prod.setRegistrosanitario(rs.getString("registrosanitario"));
                prod.setProveedor_idproveedor(rs.getInt("proveedor_idproveedor"));

                ProveedorDAO provDAO = new ProveedorDAO();
                prod.setProv(provDAO.buscar(rs.getInt("proveedor_idproveedor")));
                listaProds.add(prod);
            }
        } catch (SQLException e) {
        }

        return listaProds;

    }

    public void agregar(Producto prod) {
        try {
            String sql = "INSERT INTO producto(nombre_producto, precio_producto, descripcion_producto, nombre_tipo, registrosanitario, proveedor_idproveedor) VALUES (?, ?, ?, ?, ?, ?)";
            ps = ConDB.conectar().prepareStatement(sql);
            ps.setString(1, prod.getNombre_producto());
            ps.setDouble(2, prod.getPrecio_producto());
            ps.setString(3, prod.getDescripcion_producto());
            ps.setString(4, prod.getNombre_tipo());
            ps.setString(5, prod.getRegistrosanitario());
            ps.setInt(6, prod.getProveedor_idproveedor());

            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Producto creado exitosamente"));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error creando productos"));
        }
    }

    public void actualizar(Producto prod) {
        try {
            String sql = "UPDATE producto SET nombre_producto = ?, precio_producto = ?, descripcion_producto = ?, nombre_tipo = ?, registrosanitario = ?, proveedor_idproveedor = ? WHERE idproducto = ?";

            ps = ConDB.conectar().prepareStatement(sql);
            ps = ConDB.conectar().prepareStatement(sql);
            ps.setString(1, prod.getNombre_producto());
            ps.setDouble(2, prod.getPrecio_producto());
            ps.setString(3, prod.getDescripcion_producto());
            ps.setString(4, prod.getNombre_tipo());
            ps.setString(5, prod.getRegistrosanitario());
            ps.setInt(6, prod.getProveedor_idproveedor());
            ps.setInt(7, prod.getIdproducto());

            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Producto actualizado exitosamente"));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso", "Error actualizando el producto"));
        }
    }

    public void eliminar(Producto prod) {
        try {
            String sql = "DELETE FROM producto WHERE idproducto = ?";
            ps = ConDB.conectar().prepareStatement(sql);
            ps.setInt(1, prod.getIdproducto());
            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Producto eliminado exitosamente"));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso", "Error eliminando el producto"));
        }
    }

    public Producto buscar(int p) {
        Producto prod = null;
        try {
            String sql = "SELECT * FROM producto WHERE idproducto = ?";
            ps = ConDB.conectar().prepareStatement(sql);
            ps.setInt(1, p);

            rs = ps.executeQuery();

            if (rs.next()) {
                prod = new Producto();
                prod.setIdproducto(rs.getInt("idproducto"));
                prod.setNombre_producto(rs.getString("nombre_producto"));
                prod.setPrecio_producto(rs.getDouble("precio_producto"));
                prod.setDescripcion_producto(rs.getString("descripcion_producto"));
                prod.setNombre_tipo(rs.getString("nombre_tipo"));
                prod.setRegistrosanitario(rs.getString("registrosanitario"));

                ProveedorDAO provDAO = new ProveedorDAO();
                prod.setProv(provDAO.buscar(rs.getInt("proveedor_idproveedor")));

            }
        } catch (SQLException e) {
        }

        return prod;
    }
}
