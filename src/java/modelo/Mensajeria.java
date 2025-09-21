
package modelo;

public class Mensajeria {
    
    private int idmensajeria;
    private String nombre_mensajeria, direccion_mensajeria, cobertura;
    private long tel_mensajeria;

    public int getIdmensajeria() {
        return idmensajeria;
    }

    public void setIdmensajeria(int idmensajeria) {
        this.idmensajeria = idmensajeria;
    }

    public long getTel_mensajeria() {
        return tel_mensajeria;
    }

    public void setTel_mensajeria(long tel_mensajeria) {
        this.tel_mensajeria = tel_mensajeria;
    }

    public String getNombre_mensajeria() {
        return nombre_mensajeria;
    }

    public void setNombre_mensajeria(String nombre_mensajeria) {
        this.nombre_mensajeria = nombre_mensajeria;
    }

    public String getDireccion_mensajeria() {
        return direccion_mensajeria;
    }

    public void setDireccion_mensajeria(String direccion_mensajeria) {
        this.direccion_mensajeria = direccion_mensajeria;
    }

    public String getCobertura() {
        return cobertura;
    }

    public void setCobertura(String cobertura) {
        this.cobertura = cobertura;
    }
    
    
}
