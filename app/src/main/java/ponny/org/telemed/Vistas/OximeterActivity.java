package ponny.org.telemed.vistas;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import ponny.org.telemed.R;
import ponny.org.telemed.negocio.Oximetria;
import ponny.org.telemed.red.bluetooth.ControladorBLE;
import ponny.org.telemed.red.celular.SimGSM;
import ponny.org.telemed.red.celular.Sms;
import ponny.org.telemed.servicios.bluetooth.BluetoothLeService;
import ponny.org.telemed.servicios.bluetooth.ServicioRegistro;


public class OximeterActivity extends AppCompatActivity {

    private ControladorBLE controladorBLE;
    private String address, name;
    private ServicioRegistro servicio;
    private Oximetria oximetria;
    private TextView txtSPO2, txtPI, txtPulse;
    private Mensajes mensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oximeter);
        mensajes = new Mensajes(this);
        controladorBLE = new ControladorBLE(this, mensajes);
        servicio = new ServicioRegistro(controladorBLE, this, mensajes);
        oximetria = new Oximetria();
        obtenerExtras();
        cargarVistas();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, servicio.makeGattUpdateIntentFilter());

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        //mBluetoothLeService = null;
    }

    @Override
    public void onBackPressed() {
    servicio.volverAinicio();
    }

    private void cargarVistas() {
        txtPI = (TextView) findViewById(R.id.txtPI);
        txtPulse = (TextView) findViewById(R.id.txtPulse);
        txtSPO2 = (TextView) findViewById(R.id.txtSPO2);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            controladorBLE.setmBluetoothLeService(((BluetoothLeService.LocalBinder) service).getService());
            if (!controladorBLE.getmBluetoothLeService().initialize()) {
                finish();
            }
            Log.println(Log.ASSERT, "BLE", "cONECTANDO");

            controladorBLE.getmBluetoothLeService().connect(address);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            controladorBLE.setmBluetoothLeService(null);
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.println(Log.ASSERT,"BLE","rECIBIENDO");
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.println(Log.ASSERT, "BLE", "conectado");
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.println(Log.ASSERT, "BLE", "Desconectado");
             servicio.volverAinicio();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                servicio.displayGattServicesService(controladorBLE.getmBluetoothLeService().getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                try {//Limpia salida
                    String recibido = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                    byte[] bytes = intent.getExtras().getByteArray("data");
                    servicio.tratarDatos(bytes, txtSPO2, txtPI, txtPulse);
                    //Log.println(Log.ASSERT, "BLE", "Datos");
                } catch (NullPointerException ex) {
                    Log.println(Log.ASSERT, "BLE", ex.toString());
                }

            }


        }
    };

    private void obtenerExtras() {
        address = getIntent().getStringExtra(getString(R.string.address));
        name = getIntent().getStringExtra(getString(R.string.name_device));
        Log.println(Log.ASSERT, "BLE", name + " " + address);
    }

}
