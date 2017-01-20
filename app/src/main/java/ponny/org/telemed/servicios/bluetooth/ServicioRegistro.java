package ponny.org.telemed.servicios.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.Preferencias;
import ponny.org.telemed.negocio.Oximetria;
import ponny.org.telemed.red.bluetooth.ControladorBLE;
import ponny.org.telemed.red.celular.Llamada;
import ponny.org.telemed.red.celular.SimGSM;
import ponny.org.telemed.red.celular.Sms;

/**
 * Created by Daniel on 13/01/2017.
 */
public class ServicioRegistro {
    private ControladorBLE controladorBLE;
    private Context context;

    private int contador;
    private Oximetria oximetria;
    private boolean enviado;
private Preferencias preferencias;
    public ServicioRegistro(ControladorBLE controladorBLE, Context context) {
        this.controladorBLE = controladorBLE;
        this.context = context;
        preferencias=new Preferencias(context);
        enviado = false;
    }

    public IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void displayGattServicesService(List<BluetoothGattService> gattServices) {
        Log.println(Log.ASSERT, "BLE", "sERVICIOS");
        if (gattServices == null)
            return;
        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            if (uuid.equals(context.getString(R.string.servicio_oximetero))) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuid1 = gattCharacteristic.getUuid().toString();
                    if (uuid1.equals(context.getString(R.string.caracteristica_oximetro))) {
                        controladorBLE.getmBluetoothLeService().setCharacteristicNotification(
                                gattCharacteristic, true);
                    }
                }
            }
        }
    }

    public void tratarDatos(byte[] bytes, Oximetria oximetria, TextView spo2, TextView pi, TextView pulse) {
        if (bytes[0] == -128) {
        } else if (bytes[0] == -127) {

            oximetria.setPi((byte) bytes[3] & 0xFF);
            oximetria.setPulse((byte) bytes[1] & 0xFF);
            oximetria.setSpo2((byte) bytes[2] & 0xFF);
            this.oximetria = oximetria;
            spo2.setText(oximetria.getSpo2() + "");
            pi.setText(oximetria.getPi() + "");
            pulse.setText(oximetria.getPulse() + "");
            int pulso = (byte) bytes[1] & 0xFF;
            int oxigeno = (byte) bytes[2] & 0xFF;
            double Pi = (byte) bytes[3] & 0xFF;
            if (oximetria.datosValidos()) {
                contador++;

                if (contador > 4) {
                    //   Log.println(Log.ASSERT, "BLE", "Enviar mensaje");
                    if (!enviado) {
                        hacerLLamada();
                        if (SimGSM.simcard) {


                            //enviarMensaje();
                            Log.println(Log.ASSERT, "BLE", mensaje());
                            // enviarMensaje();

                        }else
                        {    Log.println(Log.ASSERT, "BLE", "No hay simcard disponible");
                        }
                    }

                    }
                }
            }
        }

    public void enviarMensaje() {
        enviado = true;
        //   Sms sms = new Sms(context, "3118577411", mensaje());
        Sms sms = new Sms(context, preferencias.getNumero1(), mensaje());
        sms.enviarMensaje();
    }

    public void hacerLLamada() {
        enviado = true;
        new Llamada(preferencias.getNumero1(), context).llamarContacto();
    }

    public String mensaje() {
        return "Hola , tengo un Spo2: " + oximetria.getSpo2() + "\n" + " y Pulso :" + oximetria.getPulse() + "\n Neesecito Atencion Medica";

    }
}
