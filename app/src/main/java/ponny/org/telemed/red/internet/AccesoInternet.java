package ponny.org.telemed.red.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Daniel on 16/01/2017.
 */

public class AccesoInternet {


        public static boolean Comprobacion(Context context) {
            AccesoInternet acceso=new AccesoInternet();
            return acceso.isOnline(context);
        }

        private boolean isOnline(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return isAvailable();
            }
            return false;
        }

        private Boolean isAvailable() {
            try {
                Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1   homecaredev.dcsdigital.com");
                int returnVal = p1.waitFor();
                boolean reachable = (returnVal == 0);
                if (reachable) {
                    //  System.out.println("Internet access");
                    return reachable;
                } else {
                    //     System.out.println("No Internet access");
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
            return false;
        }
    }


