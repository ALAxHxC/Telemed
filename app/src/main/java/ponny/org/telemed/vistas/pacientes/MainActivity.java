package ponny.org.telemed.vistas.pacientes;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.basededatos.OximetriaBDController;
import ponny.org.telemed.red.bluetooth.ControladorBLE;
import ponny.org.telemed.red.celular.SimGSM;
import ponny.org.telemed.red.internet.AccesoInternet;
import ponny.org.telemed.vistas.Mensajes;

import static ponny.org.telemed.red.internet.CamioDeEstadoRed.internet;


public class MainActivity extends AppCompatActivity {

    private ControladorBLE controladorBLE;
    private FloatingActionButton btnEscan;
    private Handler mHandlerScan;
    private static final long SCAN_PERIOD = 100000;
    private Mensajes mensajes;
    private OximetriaBDController oximetriaBDController;
    // private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(null);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setLogo(R.drawable.logo);
        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));

        mHandlerScan = new Handler();
        mensajes = new Mensajes(this);
        controladorBLE = new ControladorBLE(this, mLeScanCallback, mensajes);
        controladorBLE.validarConexionBlueetooth();
        oximetriaBDController = new OximetriaBDController(this);

        try {
            Log.println(Log.ASSERT, "BD", "Cantidad Registros" + oximetriaBDController.cargarRegistros().size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        iniciarRed();
        btnBuscar();

        activarBluetooth();


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
            cargarRegistrosAcvitiy();
            return true;
        }
        if (id == R.id.action_refresh) {
            mensajes.registrarNumero(getString(R.string.titulo_numero), getString(R.string.cuerpo_numero));

        }
        if (id == R.id.action_new) {
            mensajes.generarDialogoParametros(getString(R.string.titulo_parametros), false);

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
        //activarBluetooth();
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
// q                                Log.println(Log.ASSERT, "BLE", device.getName());

                                if (device.getName().equalsIgnoreCase(getString(R.string.name_device))) {
                                    controladorBLE.conectarDevice(device.getAddress(), device.getName());
                                }
                            } catch (NullPointerException ex) {
                            //    ex.printStackTrace();
                            }
                        }
                    });
                }
            };

    private void scanearDispositivo(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandlerScan.postDelayed(controladorBLE.hiloScan, SCAN_PERIOD);


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

        btnEscan = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        btnEscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //      fireBaseManager.cargarPaciente(MainActivity.this, mensajes.getPreferencias());
                scanearDispositivo(true);
            }
        });


    }

    private void iniciarRed() {
        internet = AccesoInternet.comprobacion(this);
        SimGSM simGSM = new SimGSM((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        Log.println(Log.ASSERT, "TEL", "SIM:" + SimGSM.simcard);
        //    Sms sms=new Sms(this,"3118577411","mensajeprueba");
        //   sms.enviarMensaje();
    }

    public void cargarRegistrosAcvitiy() {


        Intent intent = new Intent(this, Registros.class);
        startActivity(intent);

    }


}
