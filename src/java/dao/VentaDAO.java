package dao;

import control.ConDB;
import modelo.Venta;
import modelo.Venta_has_producto;
import modelo.Producto;
import modelo.Usuarios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    // CREAR VENTA
    public void agregar(Venta venta, List<Venta_has_producto> carrito) throws SQLException {
        Connection con = null;
        PreparedStatement psVenta = null;
        ResultSet rs = null;

        try {
            con = ConDB.conectar();
            con.setAutoCommit(false);

            String sqlVenta = "INSERT INTO venta (fecha_factura, descuento, abono, subtotal, valor_total, observaciones, usuarios_id_usuario) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setDate(1, new java.sql.Date(venta.getFecha_factura().getTime()));
            psVenta.setInt(2, venta.getDescuento());
            psVenta.setDouble(3, venta.getAbono());
            psVenta.setDouble(4, venta.getSubtotal());
            psVenta.setDouble(5, venta.getValor_total());
            psVenta.setString(6, venta.getObservaciones());
            psVenta.setInt(7, venta.getUsuarios_id_usuario());
            psVenta.executeUpdate();

            rs = psVenta.getGeneratedKeys();
            int idVenta = 0;
            if (rs.next()) idVenta = rs.getInt(1);

            for (Venta_has_producto item : carrito) {
                String sqlDet = "INSERT INTO venta_has_producto (venta_idfactura, productos_idproducto, cantidad, valor_unitario) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psDet = con.prepareStatement(sqlDet)) {
                    psDet.setInt(1, idVenta);
                    psDet.setInt(2, item.getProducto().getIdproducto());
                    psDet.setInt(3, item.getCantidad());
                    psDet.setDouble(4, item.getValor_unitario());
                    psDet.executeUpdate();
                }

                String sqlStock = "UPDATE producto SET stock = stock - ? WHERE idproducto = ?";
                try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                    psStock.setInt(1, item.getCantidad());
                    psStock.setInt(2, item.getProducto().getIdproducto());
                    psStock.executeUpdate();
                }
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (psVenta != null) psVenta.close();
            if (con != null) con.close();
        }
    }

    // LISTAR VENTAS
    public List<Venta> listar() {
        List<Venta> lista = new ArrayList<>();
        try (Connection con = ConDB.conectar()) {
            String sql = "SELECT v.*, u.p_nombre, u.p_apellido FROM venta v JOIN usuarios u ON v.usuarios_id_usuario = u.id_usuario";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Venta v = new Venta();
                v.setIdfactura(rs.getInt("idfactura"));
                v.setFecha_factura(rs.getDate("fecha_factura"));
                v.setDescuento(rs.getInt("descuento"));
                v.setAbono(rs.getDouble("abono"));
                v.setSubtotal(rs.getDouble("subtotal"));
                v.setValor_total(rs.getDouble("valor_total"));
                v.setObservaciones(rs.getString("observaciones"));
                v.setUsuarios_id_usuario(rs.getInt("usuarios_id_usuario"));

                Usuarios u = new Usuarios();
                u.setP_nombre(rs.getString("p_nombre"));
                u.setP_apellido(rs.getString("p_apellido"));
                v.setUsuario(u);

                lista.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // LISTAR DETALLE
    public List<Venta_has_producto> listarDetalle(int idVenta) {
    List<Venta_has_producto> lista = new ArrayList<>();
    try (Connection con = ConDB.conectar()) {
        String sql = "SELECT vhp.*, p.nombre_producto " +
                     "FROM venta_has_producto vhp " +
                     "JOIN producto p ON vhp.productos_idproducto = p.idproducto " +
                     "WHERE vhp.venta_idfactura = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idVenta);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Venta_has_producto vhp = new Venta_has_producto();
            Venta v = new Venta();
            v.setIdfactura(rs.getInt("venta_idfactura"));
            vhp.setVenta(v);

            Producto p = new Producto();
            p.setIdproducto(rs.getInt("productos_idproducto"));
            p.setNombre_producto(rs.getString("nombre_producto"));
            vhp.setProducto(p);

            vhp.setCantidad(rs.getInt("cantidad"));
            vhp.setValor_unitario(rs.getDouble("valor_unitario"));

            lista.add(vhp);
        }
        System.out.println("DEBUG listarDetalle → Venta: " + idVenta + " Detalles encontrados: " + lista.size());
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

    // ELIMINAR VENTA
    public void eliminar(int idVenta) throws SQLException {
        Connection con = null;
        try {
            con = ConDB.conectar();
            con.setAutoCommit(false);

            String sqlDet = "SELECT productos_idproducto, cantidad FROM venta_has_producto WHERE venta_idfactura = ?";
            List<Venta_has_producto> detalles = new ArrayList<>();
            try (PreparedStatement psDet = con.prepareStatement(sqlDet)) {
                psDet.setInt(1, idVenta);
                ResultSet rs = psDet.executeQuery();
                while (rs.next()) {
                    Venta_has_producto vhp = new Venta_has_producto();
                    Producto p = new Producto();
                    p.setIdproducto(rs.getInt("productos_idproducto"));
                    vhp.setProducto(p);
                    vhp.setCantidad(rs.getInt("cantidad"));
                    detalles.add(vhp);
                }
            }

            for (Venta_has_producto item : detalles) {
                String sqlStock = "UPDATE producto SET stock = stock + ? WHERE idproducto = ?";
                try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                    psStock.setInt(1, item.getCantidad());
                    psStock.setInt(2, item.getProducto().getIdproducto());
                    psStock.executeUpdate();
                }
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM venta_has_producto WHERE venta_idfactura = ?")) {
                ps.setInt(1, idVenta);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM venta WHERE idfactura = ?")) {
                ps.setInt(1, idVenta);
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }
}
