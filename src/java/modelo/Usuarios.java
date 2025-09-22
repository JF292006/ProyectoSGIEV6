
package modelo;

import java.util.Date;

public class Usuarios {
    
    private int id_usuario;
    private long telefono, salario, num_identificacion;
    private String tipo_usu, clave, p_nombre, s_nombre, p_apellido, s_apellido, correo, direccion;
    private Date fecha_nacimiento;

    public Usuarios(int id_usuario, long num_identificacion, long telefono, long salario, String tipo_usu, String clave, String p_nombre, String s_nombre, String p_apellido, String s_apellido, String correo, String direccion, Date fecha_nacimiento) {
        this.id_usuario = id_usuario;
        this.num_identificacion = num_identificacion;
        this.telefono = telefono;
        this.salario = salario;
        this.tipo_usu = tipo_usu;
        this.clave = clave;
        this.p_nombre = p_nombre;
        this.s_nombre = s_nombre;
        this.p_apellido = p_apellido;
        this.s_apellido = s_apellido;
        this.correo = correo;
        this.direccion = direccion;
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public Usuarios() {
  
    }
    
    
    
    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public long getNum_identificacion() {
        return num_identificacion;
    }

    public void setNum_identificacion(long num_identificacion) {
        this.num_identificacion = num_identificacion;
    }

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public long getSalario() {
        return salario;
    }

    public void setSalario(long salario) {
        this.salario = salario;
    }

    public String getTipo_usu() {
        return tipo_usu;
    }

    public void setTipo_usu(String tipo_usu) {
        this.tipo_usu = tipo_usu;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getP_nombre() {
        return p_nombre;
    }

    public void setP_nombre(String p_nombre) {
        this.p_nombre = p_nombre;
    }

    public String getS_nombre() {
        return s_nombre;
    }

    public void setS_nombre(String s_nombre) {
        this.s_nombre = s_nombre;
    }

    public String getP_apellido() {
        return p_apellido;
    }

    public void setP_apellido(String p_apellido) {
        this.p_apellido = p_apellido;
    }

    public String getS_apellido() {
        return s_apellido;
    }

    public void setS_apellido(String s_apellido) {
        this.s_apellido = s_apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }
    
    
            
}
