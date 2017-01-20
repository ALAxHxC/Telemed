package ponny.org.telemed.vistas;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.Preferencias;
import ponny.org.telemed.red.bluetooth.ControladorBLE;
import ponny.org.telemed.red.celular.SimGSM;
import ponny.org.telemed.red.internet.AccesoInternet;
import ponny.org.telemed.servicios.bluetooth.BluetoothLeService;

import static ponny.org.telemed.red.internet.CamioDeEstadoRed.internet;


public class MainActivity extends AppCompatActivity {

    private ControladorBLE controladorBLE;

    private Handler mHandlerScan;
    private static final long SCAN_PERIOD = 100000;
    Intent gattServiceIntent;
    private Preferencias preferencias;
    private Mensajes mensajes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(null);

        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setLogo(R.drawable.logo);
        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));
        /// Log.println(Log.ASSERT,"INTERNET", AccesoInternet.comprobacion(this)+"");
        mHandlerScan = new Handler();
        preferencias=new Preferencias(this);
        controladorBLE = new ControladorBLE(this, mLeScanCallback);
        controladorBLE.validarConexionBlueetooth();
        mensajes=new Mensajes(this);
        iniciarRed();
        btnBuscar();
        activarBluetooth();
        gattServiceIntent = new Intent(this, BluetoothLeService.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_refresh){
            mensajes.registrarNumero(getString(R.string.titulo_numero),getString(R.string.cuerpo_numero));
          //  Toast.makeText(MainActivity.this, "Refresh App", Toast.LENGTH_LONG).show();
        }
        if(id == R.id.action_new){
            Toast.makeText(MainActivity.this, "Create Text", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void activarBluetooth() {
        Intent bleEnable = controladorBLE.verificacionBluetooth();
        if (bleEnable != null)
            startActivityForResult(bleEnable, ControladorBLE.REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ControladorBLE.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            activarBluetooth();
            return;
        }

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
    private void iniciarRed()
    {
        internet = AccesoInternet.comprobacion(this);
        SimGSM simGSM = new SimGSM((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        Log.println(Log.ASSERT,"TEL","SIM:"+SimGSM.simcard);
    //    Sms sms=new Sms(this,"3118577411","mensajeprueba");
     //   sms.enviarMensaje();
    }


}
