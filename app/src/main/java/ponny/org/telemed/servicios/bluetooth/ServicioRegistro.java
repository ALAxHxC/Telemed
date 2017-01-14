package ponny.org.telemed.servicios.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.negocio.Oximetria;
import ponny.org.telemed.red.bluetooth.ControladorBLE;

/**
 * Created by Daniel on 13/01/2017.
 */
public class ServicioRegistro {
    private ControladorBLE controladorBLE;
    private Context context;

    public ServicioRegistro(ControladorBLE controladorBLE, Context context) {
        this.controladorBLE = controladorBLE;
        this.context = context;
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
            spo2.setText(oximetria.getSpo2()+"");
            pi.setText(oximetria.getPi()+"");
            pulse.setText(oximetria.getPulse()+"");
            int pulso = (byte) bytes[1] & 0xFF;
            int oxigeno = (byte) bytes[2] & 0xFF;
            double Pi = (byte) bytes[3] & 0xFF;

        }
    }
}
