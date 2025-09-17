package dao;

import control.ConDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Proveedor;

public class ProveedorDAO {

    PreparedStatement ps;
    ResultSet rs;

    public List<Proveedor> listar() {
        List<Proveedor> listaProvs = new ArrayList<>();

        try {
            String sql = "SELECT * FROM proveedor";
            ps = ConDB.conectar().prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Proveedor prov = new Proveedor();
                prov.setIdproveedor(rs.getInt("idproveedor"));
                prov.setNombre_proveedor(rs.getString("nombre_proveedor"));
                prov.setCorreo_proveedor(rs.getString("correo_proveedor"));
                prov.setTelefono(rs.getLong("telefono"));
                prov.setDireccion(rs.getString("direccion"));

                listaProvs.add(prov);
            }
        } catch (SQLException e) {
        }

        return listaProvs;
    }

    public void agregar(Proveedor prov) {
        try {
            String sql = "INSERT INTO proveedor (nombre_proveedor, correo_proveedor, telefono, direccion) VALUES(?, ?, ?, ?)";
            ps = ConDB.conectar().prepareStatement(sql);
            ps.setString(1, prov.getNombre_proveedor());
            ps.setString(2, prov.getCorreo_proveedor());
            ps.setLong(3, prov.getTelefono());
            ps.setString(4, prov.getDireccion());

            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public void actualizar(Proveedor prov) {
        try {
            String sql = "UPDATE proveedor SET nombre_proveedor = ?, correo_proveedor = ?, telefono = ?, direccion = ? WHERE idproveedor = ?";
            ps = ConDB.conectar().prepareStatement(sql);
            ps.setString(1, prov.getNombre_proveedor());
            ps.setString(2, prov.getCorreo_proveedor());
            ps.setLong(3, prov.getTelefono());
            ps.setString(4, prov.getDireccion());
            ps.setInt(5, prov.getIdproveedor());

            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public void eliminar(Proveedor prov) {
        try {
            String sql = "DELETE FROM proveedor WHERE idproveedor = ?";
            ps = ConDB.conectar().prepareStatement(sql);
            ps.setInt(1, prov.getIdproveedor());

            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public Proveedor buscar(Proveedor p) {
        Proveedor prov = null;

        try {
            String sql = "SELECT * FROM proveedor WHERE idproveedor = ?";
            ps = ConDB.conectar().prepareStatement(sql);
            ps.setInt(1, p.getIdproveedor());

            rs = ps.executeQuery();

            if (rs.next()) {
                prov = new Proveedor();
                prov.setIdproveedor(rs.getInt("idproveedor"));
                prov.setNombre_proveedor(rs.getString("nombre_proveedor"));
                prov.setCorreo_proveedor(rs.getString("correo_proveedor"));
                prov.setTelefono(rs.getLong("telefono"));
                prov.setDireccion(rs.getString("direccion"));
            }
        } catch (SQLException e) {
        }

        return prov;
    }

    public Proveedor buscar(int idproveedor) {
        Proveedor prov = null;

        try {
            String sql = "SELECT * FROM proveedor WHERE idproveedor = ?";
            ps = ConDB.conectar().prepareStatement(sql);
            ps.setInt(1, idproveedor);

            rs = ps.executeQuery();

            if (rs.next()) {
                prov = new Proveedor();
                prov.setIdproveedor(rs.getInt("idproveedor"));
                prov.setNombre_proveedor(rs.getString("nombre_proveedor"));
                prov.setCorreo_proveedor(rs.getString("correo_proveedor"));
                prov.setTelefono(rs.getLong("telefono"));
                prov.setDireccion(rs.getString("direccion"));
            }
        } catch (SQLException e) {
        }

        return prov;
    }
}
