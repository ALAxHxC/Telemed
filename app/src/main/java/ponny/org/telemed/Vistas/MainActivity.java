package ponny.org.telemed.vistas;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ponny.org.telemed.R;
import ponny.org.telemed.red.bluetooth.ControladorBLE;
import ponny.org.telemed.servicios.bluetooth.BluetoothLeService;



public class MainActivity extends AppCompatActivity {

    private ControladorBLE controladorBLE;

    private Handler mHandlerScan;
    private static final long SCAN_PERIOD = 100000;
    Intent gattServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandlerScan = new Handler();
        controladorBLE = new ControladorBLE(this,mLeScanCallback);
        controladorBLE.validarConexionBlueetooth();
        btnBuscar();
        activarBluetooth();
        gattServiceIntent = new Intent(this, BluetoothLeService.class);
    }


    private void activarBluetooth() {
        Intent bleEnable = controladorBLE.verificacionBluetooth();
        if (bleEnable != null)
            startActivityForResult(bleEnable, ControladorBLE.REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == ControladorBLE.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            activarBluetooth();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activarBluetooth();


    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.println(Log.ASSERT, "BLE", device.getName());
                            if (device.getName().equalsIgnoreCase(getString(R.string.name_device))) {
                                controladorBLE.conectarDevice(device.getAddress(), device.getName());
                            }
                        }
                    });
                }
            };

    private void scanearDispositivo(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandlerScan.postDelayed(new Runnable() {
                @Override
                public void run() {

                    controladorBLE.getAdapter().stopLeScan(mLeScanCallback);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        controladorBLE.getAdapter().getBluetoothLeScanner().startScan(controladorBLE.scanCallback());
                      /*  controladorBLE.getAdapter().getBluetoothLeScanner().startScan(new ScanCallback() {
                            @Override
                            public void onScanResult(int callbackType, ScanResult result) {
                                super.onScanResult(callbackType, result);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Log.println(Log.ASSERT, "BLE", result.getDevice().getName());
                                    if (result.getDevice().getName().equalsIgnoreCase(getString(R.string.name_device))) {conectar();}
                                } else {
                                    Log.println(Log.ASSERT, "BLE", "Nada");
                                }
                            }
                        });*/
                    }
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);


            controladorBLE.getAdapter().startLeScan(mLeScanCallback);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                controladorBLE.getAdapter().getBluetoothLeScanner().stopScan(controladorBLE.scanCallback());
                /*controladorBLE.getAdapter().getBluetoothLeScanner().stopScan(new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            if (result.getDevice().getName().equalsIgnoreCase(getString(R.string.name_device))) {
                                conectar();
                            }
                            Log.println(Log.ASSERT,"BLE",result.getDevice().getName());
                        }else
                        {Log.println(Log.ASSERT,"BLE","Nada");}
                    }
                });*/
            }
        } else {

            controladorBLE.getAdapter().stopLeScan(mLeScanCallback);

        }

        invalidateOptionsMenu();
    }

    public void btnBuscar() {
        Button btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanearDispositivo(true);
            }
        });

    }


}
