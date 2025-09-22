package control;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Producto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ProductoDataSource implements JRDataSource {

    private List<Producto> lstProd;
    private int indice;

    public ProductoDataSource() {
        lstProd = new ArrayList<>();
        indice = -1;
        try {
            String sql = "SELECT producto.*, proveedor.nombre_proveedor FROM producto, proveedor WHERE proveedor.idproveedor = producto.proveedor_idproveedor";
            PreparedStatement ps = ConDB.conectar().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Producto prod = new Producto();
                prod.setNombre_producto(rs.getString("nombre_producto"));
                prod.setPrecio_producto(rs.getDouble("precio_producto"));
                prod.setDescripcion_producto(rs.getString("descripcion_producto"));
                prod.setNombre_tipo(rs.getString("nombre_tipo"));
                prod.setRegistrosanitario(rs.getString("registrosanitario"));
                prod.setNom_prov(rs.getString("nombre_proveedor"));

                lstProd.add(prod);
            }
        } catch (SQLException e) {
        }
    }

    @Override
    public boolean next() throws JRException {
        indice++;
        return indice < lstProd.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;

        String nomcampo = jrf.getName();
        switch (nomcampo) {
            case "nombre_producto":
                valor = lstProd.get(indice).getNombre_producto();
                break;
            case "precio_producto":
                valor = lstProd.get(indice).getPrecio_producto();
                break;
            case "descripcion_producto":
                valor = lstProd.get(indice).getDescripcion_producto();
                break;
            case "nombre_tipo":
                valor = lstProd.get(indice).getNombre_tipo();
                break;
            case "registrosanitario":
                valor = lstProd.get(indice).getRegistrosanitario();
                break;
            case "nombre_proveedor":
                valor = lstProd.get(indice).getNom_prov();
                break;

        }

        return valor;
    }

}
