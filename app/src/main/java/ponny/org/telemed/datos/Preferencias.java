package ponny.org.telemed.datos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import ponny.org.telemed.R;

/**
 * Created by Daniel on 17/01/2017.
 */

public class Preferencias {
    private Context context;
    private SharedPreferences preferences;

    public Preferencias(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(context.getString(R.string.preference_archivo), Context.MODE_PRIVATE);
        cargarPreferencias();
    }

    public void cargarPreferencias() {
        if (!preferences.contains(context.getString(R.string.emergencia_numero1))) {
            Log.println(Log.ASSERT, "TEL", "CREA PREFERENCIAS");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(context.getString(R.string.emergencia_numero1), context.getString(R.string.emergenciabase));
            editor.putInt(context.getString(R.string.spo2), context.getResources().getInteger(R.integer.spo2_base));
            editor.putInt(context.getString(R.string.pulso_bajo), context.getResources().getInteger(R.integer.puslo_bajo));
            editor.putInt(context.getString(R.string.pulso_alto), context.getResources().getInteger(R.integer.pulso_alto));
       //     editor.putString(context.getString(R.string.emergencia_numero2), "112");
            editor.commit();
        }

    }

    public void setNumero1(String numero1) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.emergencia_numero1), numero1);
        editor.commit();

    }
    public String getNumero1() {
        return preferences.getString(context.getString(R.string.emergencia_numero1),  context.getString(R.string.emergenciabase));
    }
    public void setSPO2(int spo2)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.spo2), spo2);
        editor.commit();
    }
    public int getSPO2() {
        return preferences.getInt(context.getString(R.string.spo2), context.getResources().getInteger(R.integer.spo2_base));
    }
    public void setPulsoBajo(int pulsoBajo)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.pulso_bajo), pulsoBajo);
        editor.commit();
    }
    public int getPulsoBajo() {
        return preferences.getInt(context.getString(R.string.pulso_bajo), context.getResources().getInteger(R.integer.puslo_bajo));
    }
    public void setPulsoAlto(int pulsoAlto)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.pulso_alto), pulsoAlto);
        editor.commit();
    }
    public int getPulsoAlto() {
        return preferences.getInt(context.getString(R.string.pulso_bajo), context.getResources().getInteger(R.integer.pulso_alto));
    }


}
