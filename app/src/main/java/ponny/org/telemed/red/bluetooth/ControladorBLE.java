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
    private Mensajes mensajes;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BluetoothLeService mBluetoothLeService;
    private boolean conexion;

    public ControladorBLE(Context mContext, BluetoothAdapter.LeScanCallback mLeScanCallback) {
        this.mContext = mContext;
        this.mLeScanCallback = mLeScanCallback;
        mensajes = new Mensajes(mContext);
        conexion=false;
    }
    public ControladorBLE(Context mContext)
    {
        this.mContext = mContext;
        this.mLeScanCallback = null;
        mensajes = new Mensajes(mContext);
        conexion=false;

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

    public void conectarDevice(String address, String name) {
        Log.println(Log.ASSERT,"Conectara","ble");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getAdapter().getBluetoothLeScanner().stopScan(scanCallback());
        } else {
            getAdapter().stopLeScan(mLeScanCallback);
        }
        if(!conexion) {
            Intent intent = new Intent(mContext, OximeterActivity.class);
            intent.putExtra(mContext.getString(R.string.address), address);
            intent.putExtra(mContext.getString(R.string.name_device), name);
            mContext.startActivity(intent);
        }
        if(!conexion)
        {conexion=true;}
    }

    public BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    public void setmBluetoothLeService(BluetoothLeService mBluetoothLeService) {
        this.mBluetoothLeService = mBluetoothLeService;
    }


}
