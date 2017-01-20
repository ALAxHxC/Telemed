package ponny.org.telemed.red.celular;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import ponny.org.telemed.R;

/**
 * Created by Daniel on 16/01/2017.
 */

public class Sms {
    private Context context;
    private String numero;
    private SmsManager smsAdmin;
    private String mensaje;
    private Intent deliveryIntent, sentIntent;
    private PendingIntent deliverPI, sentPI;
    public static boolean booleanEnvioSms;
    public Sms(Context context, String numero, String mensaje) {
        this.context = context;
        this.numero = numero;
        smsAdmin = SmsManager.getDefault();
        this.mensaje = mensaje;
        sentIntent = new Intent(context.getString(R.string.sent));
        deliveryIntent = new Intent(context.getString(R.string.delivered));
        booleanEnvioSms=false;
        registrarRecividores();
        instanciarIntents();
    }

    public void enviarMensaje() {
        Log.println(Log.ASSERT, "SMS", "Enviara Mensaje");
        if (SimGSM.simcard) {
            try {
                smsAdmin.sendTextMessage(numero, null, mensaje, sentPI, deliverPI);

            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
    }

    private void instanciarIntents() {
        try {
            deliverPI = PendingIntent.getBroadcast(
                    context, 0, deliveryIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            sentPI = PendingIntent.getBroadcast(
                    context, 0, sentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void registrarRecividores() {
        context.registerReceiver(broadcastReceiverSms, new IntentFilter(context.getString(R.string.sent)));
        context.registerReceiver(broadcastDeliverySms, new IntentFilter(context.getString(R.string.delivered)));
    }

    BroadcastReceiver broadcastReceiverSms = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = "";

            switch (getResultCode()) {

                case Activity.RESULT_OK:
                    result = "Envio Mensaje Correctamente";
                    booleanEnvioSms=true;
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    result = "Falllo El Envio";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    result = "Radio off";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    result = "No PDU defined";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    result = "No hay servicio";
                    break;
            }
            Log.println(Log.ASSERT, "sms", result);
        }


    };
    BroadcastReceiver broadcastDeliverySms = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.println(Log.ASSERT, "SMS", "Envio");
                    break;
                case Activity.RESULT_CANCELED:
                    Log.println(Log.ASSERT, "SMS", "No Envio");
                    break;
            }

        }
    };

}
