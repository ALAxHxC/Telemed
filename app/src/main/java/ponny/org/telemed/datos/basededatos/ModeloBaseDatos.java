package ponny.org.telemed.datos.basededatos;

import android.provider.BaseColumns;

/**
 * Created by Daniel on 22/01/2017.
 */

public class ModeloBaseDatos implements BaseColumns {
    public static int version_basededatos = 1;
    public static String dataBaseName = "telemed.db";
    public static String tablaOximetria = "Oximetria";
    public static String col_id = "oxi_id";
    public static String col_pi = "oxi_pi";
    public static String col_pulse = "oxi_pulse";
    public static String col_spo2 = "oxi_spo2";
    public static String col_urgencia = "oxi_urgencia";
    public static String col_year = "oxi_year";
    public static String col_month = "oxi_mont";
    public static String col_day = "oxi_day";
    public static String col_stamp = "oxi_time";
    public static String table_oximetria_create =
            "CREATE TABLE " + tablaOximetria + " (  "
                    + col_id + "  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    col_pi + " REAL NOT NULL, " +
                    col_pulse + " INT NOT NULL, " +
                    col_spo2 + " INT NOT NULL, " +
                    col_urgencia + " INT NOT NULL, " +
                    col_year + " INT NOT NULL, " +
                    col_month + " INT NOT NULL, " +
                    col_day + " INT NOT NULL, " +
                    col_stamp + " TEXT NOT NULL " +
                    " );";
    public static String table_oximetria_delete = "DROP TABLE IF EXISTS " + tablaOximetria;
    public static String[] rows_oximetria = new String[]{col_id, col_pi, col_pulse, col_spo2, col_urgencia, col_year, col_month, col_day, col_stamp};
    public static String consulta_general = "SELECT " + col_id + "," + col_pi + "," + col_pulse + "," + col_spo2 + "," + col_urgencia + "," + col_year + "," + col_month + "," + col_day + "," + col_stamp + " FROM  " + tablaOximetria;
    public static String consutal_mes = "  WHERE " + col_year + "=? AND " + col_month + "=?";
}
