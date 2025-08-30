
package modelo;

import java.util.Date;

public class Envio {
    private String estado_envio, direccion_envio, direccion_salida, observaciones, novedades;
    private int idenvio, fk_mensajeria, usuarios_id_usuario;
    private Date fecha_envio, fecha_entrega;

    public String getEstado_envio() {
        return estado_envio;
    }

    public void setEstado_envio(String estado_envio) {
        this.estado_envio = estado_envio;
    }

    public String getDireccion_envio() {
        return direccion_envio;
    }

    public void setDireccion_envio(String direccion_envio) {
        this.direccion_envio = direccion_envio;
    }

    public String getDireccion_salida() {
        return direccion_salida;
    }

    public void setDireccion_salida(String direccion_salida) {
        this.direccion_salida = direccion_salida;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getNovedades() {
        return novedades;
    }

    public void setNovedades(String novedades) {
        this.novedades = novedades;
    }

    public int getIdenvio() {
        return idenvio;
    }

    public void setIdenvio(int idenvio) {
        this.idenvio = idenvio;
    }

    public int getFk_mensajeria() {
        return fk_mensajeria;
    }

    public void setFk_mensajeria(int fk_mensajeria) {
        this.fk_mensajeria = fk_mensajeria;
    }

    public int getUsuarios_id_usuario() {
        return usuarios_id_usuario;
    }

    public void setUsuarios_id_usuario(int usuarios_id_usuario) {
        this.usuarios_id_usuario = usuarios_id_usuario;
    }

    public Date getFecha_envio() {
        return fecha_envio;
    }

    public void setFecha_envio(Date fecha_envio) {
        this.fecha_envio = fecha_envio;
    }

    public Date getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(Date fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }
    
    
}
