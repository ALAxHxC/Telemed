package ponny.org.telemed.servicios.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.Preferencias;
import ponny.org.telemed.datos.basededatos.OximetriaBDController;
import ponny.org.telemed.datos.firebase.FireBaseManager;
import ponny.org.telemed.negocio.Oximetria;
import ponny.org.telemed.red.bluetooth.ControladorBLE;
import ponny.org.telemed.red.celular.Llamada;
import ponny.org.telemed.red.celular.SimGSM;
import ponny.org.telemed.red.celular.Sms;
import ponny.org.telemed.vistas.pacientes.MainActivity;
import ponny.org.telemed.vistas.Mensajes;

/**
 * Created by Daniel on 13/01/2017.
 */
public class ServicioRegistro {
    private ControladorBLE controladorBLE;
    private Context context;
    private int contador;
    private Oximetria oximetria;
    private boolean enviado;
    private OximetriaBDController controllerOximetria;
    private Preferencias preferencias;
    private Mensajes mensajes;
    private FireBaseManager fireBaseManager;

    public ServicioRegistro(ControladorBLE controladorBLE, Context context, Mensajes mensajes) {
        this.controladorBLE = controladorBLE;
        this.context = context;
        preferencias = new Preferencias(context);
        controllerOximetria = new OximetriaBDController(context);
        oximetria = new Oximetria();
        this.mensajes = mensajes;
        this.fireBaseManager = new FireBaseManager(context, preferencias);
        enviado = false;
    }

    /**
     * Filtros de la conexion bluetooth
     *
     * @return
     */
    public IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /**
     * Muestra lso servicios y perfiles del dispositivo
     *
     * @param gattServices
     */
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

    /**
     * Recibe los datos del dispopsitivo y les da tratamiento
     *
     * @param bytes
     * @param spo2
     * @param pi
     * @param pulse
     */
    public void tratarDatos(byte[] bytes, TextView spo2, TextView pi, TextView pulse) {
        if (bytes[0] == -128) {
        } else if (bytes[0] == -127) {
            oximetria.setPi((byte) bytes[3] & 0xFF);
            oximetria.setPulse((byte) bytes[1] & 0xFF);
            oximetria.setSpo2((byte) bytes[2] & 0xFF);
            oximetria.setIsUrgencia(this.isUrgente(oximetria));
            spo2.setText(oximetria.getSpo2() + "");
            pi.setText(oximetria.getPi() + "");
            pulse.setText(oximetria.getPulse() + "");
            int pulso = (byte) bytes[1] & 0xFF;
            int oxigeno = (byte) bytes[2] & 0xFF;
            double Pi = (byte) bytes[3] & 0xFF;
            if (oximetria.datosValidos()) {
                contador++;
                if (contador > context.getResources().getInteger(R.integer.valores_validos)) {
                    oximetria.setCalendar(Calendar.getInstance());
                    //   Log.println(Log.ASSERT, "BLE", "Enviar mensaje");
                    if (!enviado) {
                        if (SimGSM.simcard) {
                            //       hacerLLamada();
                            //enviarMensaje();
                            Log.println(Log.ASSERT, "BLE", mensaje());
                            // enviarMensaje();
                        } else {
                       //     Log.println(Log.ASSERT, "BLE", "No hay simcard disponible");
                        }
                        Log.println(Log.ASSERT,"BLE","Es urgente:"+oximetria.isUrgencia());
                        if(oximetria.isUrgencia()==1)
                        {
                            Log.println(Log.ASSERT,"BLE","Es urgente:"+oximetria.isUrgencia());
                        }
                        oximetria.setDetalle(new Date());
                        fireBaseManager.subirRegistroMedico(preferencias.getIdentificacionPaciente(), oximetria);
                        long resp = controllerOximetria.insertarOximetria(oximetria);
                        if (resp >= 1) {
                            mensajes.Toast(context.getString(R.string.registro_exitoso));
                        } else {
                            mensajes.Toast(context.getString(R.string.registro_fail));
                        }
                        volverAinicio();
                    }

                }
            }
        }
    }

    /**
     * Determina si la toma de datos es urgente
     * @param oximetria el dato tomado
     * @return 1 si urgente
     */
    private int isUrgente(Oximetria oximetria){
        Log.println(Log.ASSERT,"BLE","Determina urgencia" );
        if(preferencias.getSPO2()<=oximetria.getSpo2()){
           return 1;
        }
        if(oximetria.getPulse()>=preferencias.getPulsoAlto()){
            return 1;
        }
        if(oximetria.getPulse()<=preferencias.getPulsoBajo()){
            return 1;
        }
        return 0;
    }
    /**
     * Envia mensaje SMS
     */
    public void enviarMensaje() {
        enviado = true;
        //   Sms sms = new Sms(context, "3118577411", mensaje());
        Sms sms = new Sms(context, preferencias.getNumero1(), mensaje());
        sms.enviarMensaje();
    }

    /**
     * Realiza la llamada telefonica
     */
    public void hacerLLamada() {
        enviado = true;
        new Llamada(preferencias.getNumero1(), context).llamarContacto();
    }

    /**
     * Mensaje que serva enviado SMS
     *
     * @return
     */
    public String mensaje() {
        return "Hola , tengo un Spo2: " + oximetria.getSpo2() + "\n" + " y Pulso :" + oximetria.getPulse() + "\n Neesecito Atencion Medica";

    }

    public void volverAinicio() {
        //desconecta el dispositivo
        controladorBLE.desconnectar();
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

    }

}
