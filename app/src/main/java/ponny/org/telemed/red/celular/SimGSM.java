package ponny.org.telemed.red.celular;

import android.content.Context;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

/**
 * Created by Daniel on 16/01/2017.
 */

public class SimGSM {
    private Context context;
    private TelephonyManager telmanager;
    public SimGSM(Context context) {
        this.context = context;
        telmanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);


    }

    /**
     * Si la simcard esta lista
     * @return
     */
    public boolean simReady() {
        return telmanager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }



}
