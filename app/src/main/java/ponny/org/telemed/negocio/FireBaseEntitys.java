package ponny.org.telemed.negocio;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

/**
 * Created by Daniel on 30/01/2017.
 */

public class FireBaseEntitys {


    public static class CentroMedico {
        int id;
        String claveMedico;
        String clavePaciente;
        String nombre;

        public CentroMedico() {
        }

        public CentroMedico(int id, String claveMedico, String clavePaciente, String nombre) {
            this.id = id;
            this.claveMedico = claveMedico;
            this.clavePaciente = clavePaciente;
            this.nombre = nombre;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getClaveMedico() {
            return claveMedico;
        }

        public void setClaveMedico(String claveMedico) {
            this.claveMedico = claveMedico;
        }

        public String getClavePaciente() {
            return clavePaciente;
        }

        public void setClavePaciente(String clavePaciente) {
            this.clavePaciente = clavePaciente;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

    }

    public static class Paciente {
        String identificacion;
        String nombres;
        String apellidos;
        int spo2;
        int puslobajo;
        int pusloalto;
        String numeropaciente;
        int centromedico;
        String idFcm;

        public Paciente() {

        }

        public Paciente(String identificacion, String nombres, String apellidos, int spo2, int puslobajo, int pusloalto, String numeropaciente, int centromedico, String idFcm) {
            this.identificacion = identificacion;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.spo2 = spo2;
            this.puslobajo = puslobajo;
            this.pusloalto = pusloalto;
            this.numeropaciente = numeropaciente;
            this.centromedico = centromedico;
            this.idFcm=idFcm;
        }

        public String getIdentificacion() {
            return identificacion;
        }

        public void setIdentificacion(String identificacion) {
            this.identificacion = identificacion;
        }

        public String getNombres() {
            return nombres;
        }

        public void setNombres(String nombres) {
            this.nombres = nombres;
        }

        public String getApellidos() {
            return apellidos;
        }

        public void setApellidos(String apellidos) {
            this.apellidos = apellidos;
        }

        public int getSpo2() {
            return spo2;
        }

        public void setSpo2(int spo2) {
            this.spo2 = spo2;
        }

        public int getPuslobajo() {
            return puslobajo;
        }

        public void setPuslobajo(int puslobajo) {
            this.puslobajo = puslobajo;
        }

        public int getPusloalto() {
            return pusloalto;
        }

        public void setPusloalto(int pusloalto) {
            this.pusloalto = pusloalto;
        }

        public String getNumeropaciente() {
            return numeropaciente;
        }

        public void setNumeropaciente(String numeropaciente) {
            this.numeropaciente = numeropaciente;
        }

        public int getCentromedico() {
            return centromedico;
        }

        public void setCentromedico(int centromedico) {
            this.centromedico = centromedico;
        }

        public String getIdFcm() {
            return idFcm;
        }

        public void setIdFcm(String idFcm) {
            this.idFcm = idFcm;
        }
    }
    public static class Medico{
        String identificacion;
        String nombres;
        String apellidos;
        String fcm;

        public Medico(String identificacion, String nombres, String apellidos,String fcm) {
            this.identificacion = identificacion;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.fcm = fcm;
        }

        public Medico() {
        }

        public String getIdentificacion() {
            return identificacion;
        }

        public void setIdentificacion(String identificacion) {
            this.identificacion = identificacion;
        }

        public String getNombres() {
            return nombres;
        }

        public void setNombres(String nombres) {
            this.nombres = nombres;
        }

        public String getApellidos() {
            return apellidos;
        }

        public void setApellidos(String apellidos) {
            this.apellidos = apellidos;
        }

        public String getFcm() {
            return fcm;
        }

        public void setFcm(String fcm) {
            this.fcm = fcm;
        }
    }
}
