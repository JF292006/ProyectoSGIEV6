package dao;

import control.ConDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import modelo.Envio;

public class EnviosDAO {

    public void agregarEnvio(Envio envio) throws SQLException {
        String sql = "INSERT INTO envio (estado_envio, fecha_envio, fecha_entrega, "
                   + "direccion_salida, direccion_envio, observaciones, novedades, fk_mensajeria, usuarios_id_usuario) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, envio.getEstado_envio());

            if (envio.getFecha_envio() != null) {
                ps.setDate(2, new java.sql.Date(envio.getFecha_envio().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }

            if (envio.getFecha_entrega() != null) {
                ps.setDate(3, new java.sql.Date(envio.getFecha_entrega().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }

            ps.setString(4, envio.getDireccion_salida());
            ps.setString(5, envio.getDireccion_envio());
            ps.setString(6, envio.getObservaciones());
            ps.setString(7, envio.getNovedades());
            ps.setInt(8, envio.getFk_mensajeria());
            ps.setInt(9, envio.getUsuarios_id_usuario());

            ps.executeUpdate();
        }
    }
}
