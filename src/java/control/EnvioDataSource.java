package control;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Envio;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class EnvioDataSource implements JRDataSource {

    private List<Envio> lstEnv;
    private int indice;

    public EnvioDataSource() {
        lstEnv = new ArrayList<>();
        indice = -1;
        try {
            String sql = "SELECT envio.*, mensajeria.nombre_mensajeria, usuarios.p_nombre FROM envio, mensajeria ,usuarios WHERE mensajeria.idmensajeria = envio.fk_mensajeria AND usuarios.idusuario = envio.usuarios_id_usuario";
            PreparedStatement ps = ConDB.conectar().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Envio env = new Envio();
                env.setEstado_envio(rs.getString("estado_envio"));
                env.setFecha_envio(rs.getDate("fecha_envio"));
                env.setDireccion_envio(rs.getString("direccion_envio"));
                env.setObservaciones(rs.getString("observaciones"));
                env.setNom_mens(rs.getString("nombre_mensajeria"));
                env.setNom_usr(rs.getString("p_nombre"));

                lstEnv.add(env);
            }
        } catch (SQLException e) {
        }
    }

    @Override
    public boolean next() throws JRException {
        indice++;
        return indice < lstEnv.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;

        String nomcampo = jrf.getName();
        switch (nomcampo) {
            case "estado_envio":
                valor = lstEnv.get(indice).getEstado_envio();
                break;
            case "fecha_envio":
                valor = lstEnv.get(indice).getFecha_envio();
                break;
            case "direccion_envio":
                valor = lstEnv.get(indice).getDireccion_envio();
                break;
            case "observaciones":
                valor = lstEnv.get(indice).getObservaciones();
                break;
            case "nombre_mensajeria":
                valor = lstEnv.get(indice).getNom_mens();
                break;
            case "p_nombre":
                valor = lstEnv.get(indice).getNom_usr();
                break;
        }

        return valor;
    }

}
