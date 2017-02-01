package ponny.org.telemed.datos.basededatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ponny.org.telemed.negocio.Oximetria;

import static ponny.org.telemed.datos.basededatos.ModeloBaseDatos.consulta_general;
import static ponny.org.telemed.datos.basededatos.ModeloBaseDatos.consutal_mes;
import static ponny.org.telemed.utilidades.Utilidades.sdf;

/**
 * Created by Daniel on 22/01/2017.
 */

public class OximetriaBDController {
    private BaseDeDatos baseDeDatos;

    public OximetriaBDController(Context context) {

        this.baseDeDatos = new BaseDeDatos(context, null);
    }

    public long insertarOximetria(Oximetria oximetria) {
        ContentValues registro = new ContentValues();

        SQLiteDatabase bd = baseDeDatos.getWritableDatabase();
        long respuesta = -1;
        try {
            registro.put(ModeloBaseDatos.col_pi, oximetria.getPi());
            registro.put(ModeloBaseDatos.col_pulse, oximetria.getPulse());
            registro.put(ModeloBaseDatos.col_spo2, oximetria.getSpo2());
            registro.put(ModeloBaseDatos.col_urgencia, oximetria.isUrgencia());
            registro.put(ModeloBaseDatos.col_day, oximetria.getCalendar().get(Calendar.DAY_OF_MONTH));
            registro.put(ModeloBaseDatos.col_month, oximetria.getCalendar().get(Calendar.MONTH));
            registro.put(ModeloBaseDatos.col_year, oximetria.getCalendar().get(Calendar.YEAR));
            registro.put(ModeloBaseDatos.col_stamp, sdf.format(oximetria.getDetalle()));
            respuesta = bd.insert(ModeloBaseDatos.tablaOximetria, null, registro);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.println(Log.ASSERT, "BD", ex.getMessage());

        }
        bd.close();
        return respuesta;
    }

    public List<Oximetria> cargarRegistros() {
        List<Oximetria> listaOxtrimetria = new ArrayList<Oximetria>();
        SQLiteDatabase bd = baseDeDatos.getWritableDatabase();
        try {

            Cursor lista = bd.rawQuery(consulta_general, null);
            if (lista.moveToFirst()) {
                Log.println(Log.ASSERT, "SQL", lista.getString(8));
                listarOximetrias(listaOxtrimetria, lista);
            } else {
                listaOxtrimetria = null;
            }
        } catch (Exception ex) {
            listaOxtrimetria = null;
        }
        bd.close();
        return listaOxtrimetria;
    }

    public List<Oximetria> cargarRegistros(int year, int day) {
        List<Oximetria> listaOxtrimetria = new ArrayList<Oximetria>();
        SQLiteDatabase bd = baseDeDatos.getWritableDatabase();
        try {
            String[] args = new  String[]{String.valueOf(year), String.valueOf(day)};
            Cursor lista = bd.rawQuery(consulta_general + consutal_mes, args);
            if (lista.moveToFirst()) {
                Log.println(Log.ASSERT, "SQL", lista.getString(8));
                listarOximetrias(listaOxtrimetria, lista);
            } else {
                listaOxtrimetria = null;
            }
        } catch (Exception ex) {
            listaOxtrimetria = null;
        }
        bd.close();
        return listaOxtrimetria;
    }

    private void listarOximetrias(List<Oximetria> lista, Cursor cursor) {
        do {
            lista.add(castearOximetria(cursor));
        } while (cursor.moveToNext());
    }

    private Oximetria castearOximetria(Cursor cursor) {
        Oximetria oximetria = new Oximetria();
        oximetria.setId(cursor.getInt(0));
        oximetria.setPi(cursor.getDouble(1));
        oximetria.setPulse(cursor.getInt(2));
        oximetria.setSpo2(cursor.getInt(3));
        oximetria.setUrgencia(cursor.getInt(4));
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(Calendar.YEAR, cursor.getInt(5));
        calendar.set(Calendar.MONTH, cursor.getInt(6));
        calendar.set(Calendar.DAY_OF_MONTH, cursor.getInt(7));
        oximetria.setCalendar(calendar);
        try {
            oximetria.setDetalle(sdf.parse(cursor.getString(8)));
        } catch (ParseException e) {
            Log.println(Log.ASSERT, "SQL", "Imposible hacer casteo");
            e.printStackTrace();
        }
        return oximetria;

    }

}
