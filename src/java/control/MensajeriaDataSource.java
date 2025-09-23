package control;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Mensajeria;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class MensajeriaDataSource implements JRDataSource {
    private List<Mensajeria> lstMens;
    private int indice;
    public MensajeriaDataSource() {
        lstMens = new ArrayList<>();
        indice = -1;
        try {
            String sql = "SELECT * FROM mensajeria";
            PreparedStatement ps = ConDB.conectar().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Mensajeria mens = new Mensajeria();
                mens.setNombre_mensajeria(rs.getString("nombre_mensajeria"));
                mens.setTel_mensajeria(rs.getLong("tel_mensajeria"));
                mens.setDireccion_mensajeria(rs.getString("direccion_mensajeria"));
                mens.setCobertura(rs.getString("cobertura"));

                lstMens.add(mens);
            }
        } catch (SQLException e) {
        }
    }

    @Override
    public boolean next() throws JRException {
        indice++;
        return indice < lstMens.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;

        String nomcampo = jrf.getName();
        switch (nomcampo) {
            case "nombre_mensajeria":
                valor = lstMens.get(indice).getNombre_mensajeria();
                break;
            case "tel_mensajeria":
                valor = lstMens.get(indice).getTel_mensajeria();
                break;
            case "direccion_mensajeria":
                valor = lstMens.get(indice).getDireccion_mensajeria();
                break;
            case "cobertura":
                valor = lstMens.get(indice).getCobertura();
                break;
        }

        return valor;
    }

}
