
package control;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Proveedor;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ProveedorDataSource implements JRDataSource{
    
    private List<Proveedor> lstProv;
    private int indice;
    
     public ProveedorDataSource() {
        lstProv = new ArrayList<>();
        indice = -1;
        try {
            String sql = "SELECT * FROM proveedor";
            PreparedStatement ps = ConDB.conectar().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Proveedor prov = new Proveedor();
                prov.setNombre_proveedor(rs.getString("nombre_proveedor"));
                prov.setCorreo_proveedor(rs.getString("correo_proveedor"));
                prov.setTelefono(rs.getLong("telefono"));
                prov.setDireccion(rs.getString("direccion"));

                lstProv.add(prov);
            }
        } catch (SQLException e) {
        }
    }
    @Override
    public boolean next() throws JRException {
       indice++;
        return indice < lstProv.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;

        String nomcampo = jrf.getName();
        switch (nomcampo) {
            case "nombre_proveedor":
                valor = lstProv.get(indice).getNombre_proveedor();
                break;
            case "correo_proveedor":
                valor = lstProv.get(indice).getCorreo_proveedor();
                break;
            case "telefono":
                valor = lstProv.get(indice).getTelefono();
                break;
            case "direccion":
                valor = lstProv.get(indice).getDireccion();
                break;
        }

        return valor;
    }
    
}
