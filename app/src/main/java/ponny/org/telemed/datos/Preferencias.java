package ponny.org.telemed.datos;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import ponny.org.telemed.R;


/**
 * Created by Daniel on 17/01/2017.
 * Guarda en las preferencias del sistema
 */

public class Preferencias {
    private Context context;
    private SharedPreferences preferences;
    // Si debe crear las preferencias en el sistema
    public static boolean debeCrear = true;

    /**
     * Actividad
     *
     * @param context
     */
    public Preferencias(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(context.getString(R.string.preference_archivo), Context.MODE_PRIVATE);
        cargarPreferencias();
    }

    /**
     * Carga las preferencias en el sistema
     */
    public void cargarPreferencias() {
        if ((preferences.getString(context.getString(R.string.identificacionPac), null) == null)) {
            Log.println(Log.ASSERT, "TEL", "CREA PREFERENCIAS");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(context.getString(R.string.emergencia_numero1), context.getString(R.string.emergenciabase));
            editor.putInt(context.getString(R.string.spo2), context.getResources().getInteger(R.integer.spo2_base));
            editor.putInt(context.getString(R.string.pulso_bajo), context.getResources().getInteger(R.integer.puslo_bajo));
            editor.putInt(context.getString(R.string.pulso_alto), context.getResources().getInteger(R.integer.pulso_alto));
            cargarPreferenciasPaciente(editor);
            editor.commit();
            debeCrear=true;
        } else {
            debeCrear = false;
        }

    }

    /**
     * Cargas las preferencias del paciente
     *
     * @param editor
     */
    public void cargarPreferenciasPaciente(SharedPreferences.Editor editor) {
        if (!preferences.contains(context.getString(R.string.identificacionPac))) {

            editor.putString(context.getString(R.string.identificacionPac), null);
            editor.putString(context.getString(R.string.nombresPac), null);
            editor.putString(context.getString(R.string.apellidosPac), null);
            editor.putString(context.getString(R.string.nacimientoPac), null);
 //           editor.putString(context.getString(R.string.nacimientoPac), null);

        }
    }

    /**
     * Guarda el numero del paciente
     *
     * @param numero1
     */
    public void setNumero1(String numero1) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.emergencia_numero1), numero1);
        editor.commit();

    }

    /**
     * Devuelve el numero del paciente
     *
     * @return retorna el numero del paceinte
     */
    public String getNumero1() {
        return preferences.getString(context.getString(R.string.emergencia_numero1), context.getString(R.string.emergenciabase));
    }

    /**
     * Guarda el spo2 del paciente
     * @param spo2
     */
    public void setSPO2(int spo2) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.spo2), spo2);
        editor.commit();
    }

    /**
     * Devuelve el niuvel de oximetria
     * @return
     */
    public int getSPO2() {
        return preferences.getInt(context.getString(R.string.spo2), context.getResources().getInteger(R.integer.spo2_base));
    }

    /**
     * Guarda Pulso bajo en el sistema
     * @param pulsoBajo
     */
    public void setPulsoBajo(int pulsoBajo) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.pulso_bajo), pulsoBajo);
        editor.commit();
    }

    /**
     * Deuvelve el puslo bajo del paciente
     * @return
     */
    public int getPulsoBajo() {
        return preferences.getInt(context.getString(R.string.pulso_bajo), context.getResources().getInteger(R.integer.puslo_bajo));
    }

    /**
     * Ingresa Pulso Alto del paciente
     * @param pulsoAlto
     */
    public void setPulsoAlto(int pulsoAlto) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.pulso_alto), pulsoAlto);
        editor.commit();
    }

    /**
     * Devuelve puslo alto del paciente
     * @return
     */
    public int getPulsoAlto() {
        return preferences.getInt(context.getString(R.string.pulso_alto), context.getResources().getInteger(R.integer.pulso_alto));
    }

    public void setNacimientPaciente(String date) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.nacimientoPac), date);
        editor.commit();

    }

    public String getNacimientPaciente() {
        return preferences.getString(context.getString(R.string.nacimientoPac), null);

    }

    public void seIdentificacionPaciente(String identificacion) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.identificacionPac), identificacion);
        editor.commit();
    }
    public String getIdentificacionPaciente()
    {
        return preferences.getString(context.getString(R.string.identificacionPac), null);
    }

    public String getNombrePaciente() {
        return preferences.getString(context.getString(R.string.nombresPac), null);

    }

    public void setNamePaciente(String nombres) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.nombresPac), nombres);
        editor.commit();
    }

    public String getApellidosPaciente() {
        return preferences.getString(context.getString(R.string.apellidosPac), null);

    }

    public void setApellidosPaciente(String apellidosPaciente) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.apellidosPac), apellidosPaciente);
        editor.commit();
    }

    public String getDescripccionPaciente() {
        return preferences.getString(context.getString(R.string.descripccionPac), null);
    }

    public void setDescripccionPaciente(String descripccionPaciente) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.descripccionPac), descripccionPaciente);
        editor.commit();

    }


}
