package ponny.org.telemed.red.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import ponny.org.telemed.R;
import ponny.org.telemed.vistas.Mensajes;
import ponny.org.telemed.vistas.OximeterActivity;
import ponny.org.telemed.servicios.bluetooth.BluetoothLeService;

/**
 * Created by Daniel on 11/01/2017.
 */
public class ControladorBLE {
    public static final int REQUEST_ENABLE_BT = 1;
    private Context mContext;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BluetoothLeService mBluetoothLeService;
    private boolean conexion;
    private Mensajes mensajes;

    public ControladorBLE(Context mContext, BluetoothAdapter.LeScanCallback mLeScanCallback, Mensajes mensajes) {
        this.mContext = mContext;
        this.mLeScanCallback = mLeScanCallback;
        this.mensajes = mensajes;
        conexion = false;
    }

    public ControladorBLE(Context mContext, Mensajes mensajes) {
        this.mContext = mContext;
        this.mLeScanCallback = null;
        this.mensajes = mensajes;
        conexion = false;

    }

    public void validarConexionBlueetooth() {
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) || (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE) == null) {
            mensajes.finshApp(mContext.getString(R.string.error_fatal), mContext.getString(R.string.No_bluetooth));
        }

    }

    public Intent verificacionBluetooth() {
        if (!((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            return enableBtIntent;
        }
        return null;
    }

    public BluetoothAdapter getAdapter() {
        return ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    /**
     * Escan de BLE
     *
     * @return
     */
    public ScanCallback scanCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return (new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.println(Log.ASSERT, "BLE", result.getDevice().getName());
                        if (result.getDevice().getName().equalsIgnoreCase(mContext.getString(R.string.name_device))) {
                            conectarDevice(result.getDevice().getAddress(), result.getDevice().getName());
                        }

                    } else {
                        Log.println(Log.ASSERT, "BLE", "Nada");
                    }
                }
            });
        } else {
            return null;
        }
    }

    /**
     * Conecta el dispositivo
     *
     * @param address
     * @param name
     */
    public void conectarDevice(String address, String name) {
        Log.println(Log.ASSERT, "Conectara", "ble");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getAdapter().getBluetoothLeScanner().stopScan(scanCallback());
        } else {
            getAdapter().stopLeScan(mLeScanCallback);
        }
        if (!conexion) {
            Intent intent = new Intent(mContext, OximeterActivity.class);
            intent.putExtra(mContext.getString(R.string.address), address);
            intent.putExtra(mContext.getString(R.string.name_device), name);
            mContext.startActivity(intent);
        }
        if (!conexion) {
            conexion = true;
        }
    }

    public BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    public void setmBluetoothLeService(BluetoothLeService mBluetoothLeService) {
        this.mBluetoothLeService = mBluetoothLeService;
    }

    /**
     * Desconecta el dispositivo
     */
    public void desconnectar() {
        if (this.getAdapter() == null || this.getAdapter() == null) {
            Log.w("BLE", "BluetoothAdapter not initialized");
            return;
        }
        Log.println(Log.ASSERT, "BLE", "Desconexion");

        this.getmBluetoothLeService().disconnect();
        this.getmBluetoothLeService().close();
        this.setmBluetoothLeService(null);

    }

    public final Runnable hiloScan = new Runnable() {
        @Override
        public void run() {
            getAdapter().stopLeScan(mLeScanCallback);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getAdapter().getBluetoothLeScanner().startScan(scanCallback());
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
        }

        ;
    };
}
