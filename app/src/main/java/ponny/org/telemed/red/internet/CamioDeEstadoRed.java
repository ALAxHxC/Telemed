package ponny.org.telemed.red.internet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Daniel on 16/01/2017.
 */

public class CamioDeEstadoRed extends BroadcastReceiver {
    /*
    Estado de internet
     */
    public static boolean internet;
    /*
    Es llamanda cada vez que se cambia el estado de red a internet
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        Log.println(Log.ASSERT, "INERNET", "cAMBIA");
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            internet = true;
            Log.println(Log.ASSERT, "INERNET", "true");
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            internet = false;
            Log.println(Log.ASSERT, "INERNET", "false");
        }
    }
}
