package ponny.org.telemed.red.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Daniel on 16/01/2017.
 */

public class AccesoInternet {


        public static boolean comprobacion(Context context) {

            AccesoInternet acceso=new AccesoInternet();
            return acceso.existeConexion(context);
        }

    /**
     * Devuelve estado de conexion
     * @param context
     * @return
     */
        private boolean existeConexion(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return hayRedDisponible();
            }
            Log.println(Log.ASSERT,"LOG","Sin internet");
            return false;
        }

    /**
     * Realiza un ping para verificar si hay conexion a inernet
     * @return devuelve red
     */
        private Boolean hayRedDisponible() {
            try {
                Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1   www.google.com");
                int returnVal = p1.waitFor();
                boolean reachable = (returnVal == 0);
                if (reachable) {
                    Log.println(Log.ASSERT,"INTERNET","Acceso a internet");
                    return reachable;
                } else {
                    Log.println(Log.ASSERT,"INTERNET","No internet access");
                    //     System.out.println("No Internet access");
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
            return false;
        }
    }


