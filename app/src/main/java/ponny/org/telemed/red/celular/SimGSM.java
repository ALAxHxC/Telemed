package ponny.org.telemed.red.celular;

import android.content.Context;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Daniel on 16/01/2017.
 */

public class SimGSM {

    private TelephonyManager telmanager;
    public static boolean simcard;
    public SimGSM(TelephonyManager telmanager) {

        try {
           this.telmanager= telmanager;
            simcard=simReady();
   }catch (NullPointerException ex)
        {

            simcard=false;
        }
    }

    /**
     * Si la simcard esta lista
     *
     * @return
     */
    private boolean simReady() {
        return telmanager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }


}
