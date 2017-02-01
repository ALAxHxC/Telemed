package ponny.org.telemed.negocio;

import android.icu.util.Calendar;

import java.util.Date;


/**
 * Created by Daniel on 13/01/2017.
 */
public class Oximetria {
    private int id;
    private int spo2, pulse;
    private double pi;
    private int isUrgencia;
    java.util.Calendar calendar;
    private Date detalle;

    public Oximetria() {
    }

    public Oximetria(int spo2, int pulse, double pi) {
        this.spo2 = spo2;
        this.pulse = pulse;
        this.pi = pi;
        calendar = java.util.Calendar.getInstance();
        isUrgencia = 0;
    }

    public int getSpo2() {
        return spo2;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = (pi / 10.0);
    }

    public int isUrgencia() {
        return isUrgencia;
    }

    public void setUrgencia(int urgencia) {
        isUrgencia = urgencia;
    }

    public java.util.Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(java.util.Calendar calendar) {
        this.calendar = calendar;
    }

    public boolean datosValidos() {
        return !(this.spo2 >= 127 || this.pulse >= 255 || this.pi == 0.0);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsUrgencia() {
        return isUrgencia;
    }

    public void setIsUrgencia(int isUrgencia) {
        this.isUrgencia = isUrgencia;
    }

    public Date getDetalle() {
        return detalle;
    }

    public void setDetalle(Date detalle) {
        this.detalle = detalle;
    }
}
