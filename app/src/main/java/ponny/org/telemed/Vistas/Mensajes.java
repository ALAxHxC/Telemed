package ponny.org.telemed.vistas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.Preferencias;
import ponny.org.telemed.datos.firebase.FireBaseManager;


/**
 * Created by daniel on 29/07/2016.
 * <p>
 * Contiene dialogos , alertas y demas mensajes que no estan incluidos sobre una vista
 */
public class Mensajes {
    private Context context;
    private Preferencias preferencias;
    private ProgressDialog progressDialog;
    private FireBaseManager fireBaseManager;

    /**
     * @param context Actividad
     */
    public Mensajes(Context context) {
        this.context = context;
        preferencias = new Preferencias(context);
        fireBaseManager = new FireBaseManager(context,this);

    }

    public void generarDialogoPacienteInicial() {
        if (Preferencias.debeCrear) {
            generarDialogoDatosPaciente(context.getString(R.string.titulo_registre_susdatos));
        } else {
      /*      Log.println(Log.ASSERT, "BD", "Fecha:" + preferencias.getNacimientPaciente());
            Log.println(Log.ASSERT, "BD", preferencias.getNombrePaciente());
            Log.println(Log.ASSERT, "BD", preferencias.getDescripccionPaciente());
            Log.println(Log.ASSERT, "BD", preferencias.getApellidosPaciente());*/
        }
    }

    public void mostrarDialogo() {
        progressDialog = new ProgressDialog(context, R.style.AppTheme);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getString(R.string.espere));
        progressDialog.show();

    }

    public void cancelarDialogos() {
        progressDialog.dismiss();
    }

    /**
     * Muestra un Toast
     *
     * @param body mensaje del toast
     */
    public void Toast(String body) {
        Toast.makeText(context, body, Toast.LENGTH_LONG).show();

    }

    /**
     * Muestra una alerta y finaliza la aplicacion
     *
     * @param title titulo de la alerta
     * @param body  mensaje de la alerta
     */
    public void finshApp(String title, String body) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(body);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
        builder.create();
        builder.show();

    }

    /**
     * Muestra una alerta simple
     *
     * @param titulo titlo de la alerta
     * @param cuerpo mensaje de la alerta
     */
    public void simpleAltertDialog(String titulo, String cuerpo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo);
        builder.setMessage(cuerpo);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();

    }

    /**
     * Registra numero de contacto
     *
     * @param titulo  titulo del mensaje
     * @param mensaje
     */
    public void registrarNumero(String titulo, String mensaje)

    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        final EditText input = new EditText(context);
        input.setText(preferencias.getNumero1());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);
        builder.setView(input);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        registrarNumero1(input.getText().toString());
                        fireBaseManager.cargarPaciente(preferencias);
                    }
                }
        );
        builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        fireBaseManager.cargarPaciente(preferencias);
                    }
                }
        );
        builder.show();
    }

    /**
     * Valida numero registrado (Celular)
     *
     * @param entrada numero
     * @return valido
     */
    private boolean validarTextoNumero(String entrada) {
        return !(entrada.isEmpty() || entrada.length() < 10);
    }

    private void registrarNumero1(String entrada) {
        if (validarTextoNumero(entrada)) {
            preferencias.setNumero1(entrada);
        }
    }

    /**
     * Muestra una alerta para el ingreso de parametros bajo los que se genera alertas
     *
     * @param titulo      titulo de la almerta
     * @param llamaNumero indica si se debe llamar la alerta de numero ( solo para el primero inicio de la aplicacion)
     */
    public void generarDialogoParametros(String titulo, boolean llamaNumero) {
        Dialog dialogo = new Dialog(context);
        dialogo.setContentView(R.layout.dialogo_parametros);
        dialogo.setTitle(titulo);
        TextView spo2 = (TextView) dialogo.findViewById(R.id.editTextSPO2);
        TextView pulsobajo = (TextView) dialogo.findViewById(R.id.editTextPulsoAlto);
        TextView pulsoalto = (TextView) dialogo.findViewById(R.id.editTextPulsoBajo);
        Button guardar = (Button) dialogo.findViewById(R.id.btnAceptarParametros);
        Button cancelar = (Button) dialogo.findViewById(R.id.btnCancelarParametros);
        cancelarDialogo(cancelar, dialogo);
        guardarParametros(guardar, dialogo, spo2, pulsoalto, pulsobajo, llamaNumero);
        spo2.setText(preferencias.getSPO2() + "");
        pulsoalto.setText(preferencias.getPulsoAlto() + "");
        pulsobajo.setText(preferencias.getPulsoBajo() + "");

        dialogo.show();
        dialogo.setCancelable(false);
    }

    /**
     * Guarda los parametros de la alerta
     *
     * @param button       boton de reggistro
     * @param dialog       dialogo
     * @param spo2         nivel de oxigeno en la sangre
     * @param pulsoalto    puslo alto del paciente
     * @param pulsobajo    pulso bajo del paciente
     * @param llamanumeros indica si se debe llamar la alerta de numero ( solo para el primero inicio de la aplicacion)
     */
    private void guardarParametros(Button button, final Dialog dialog, final TextView spo2, final TextView pulsoalto, final TextView pulsobajo, final boolean llamanumeros) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferencias.setSPO2(Integer.parseInt(spo2.getText().toString()));
                preferencias.setPulsoAlto(Integer.parseInt(pulsoalto.getText().toString()));
                preferencias.setPulsoBajo(Integer.parseInt(pulsobajo.getText().toString()));
                fireBaseManager.cargarPaciente(preferencias);
                dialog.dismiss();
                if (llamanumeros) {
                    registrarNumero(context.getString(R.string.titulo_numero), context.getString(R.string.cuerpo_numero));

                }
            }
        });

    }

    /**
     * Crea un dialogo del pacieiente
     *
     * @param titulo titulo del dialogo
     * @return dialogo
     */
    public Dialog cargarDialogoDatosPaciente(String titulo) {
        Dialog dialogo = new Dialog(context);
        dialogo.setContentView(R.layout.dialogo_paciente);
        dialogo.setTitle(titulo);
        dialogo.setCancelable(false);
        return dialogo;
    }

    /**
     * Configura y muestra el dialogo del paciente
     *
     * @param titulo titulo del dialgoo
     */
    public void generarDialogoDatosPaciente(String titulo) {
        final Dialog dialogo = cargarDialogoDatosPaciente(titulo);
        final EditText identificacion = (EditText) dialogo.findViewById(R.id.editTextIdentificacion);
        final EditText nombres = (EditText) dialogo.findViewById(R.id.editTextNombres);
        final EditText apellidos = (EditText) dialogo.findViewById(R.id.editTextApellidos);

        final EditText descripccion = (EditText) dialogo.findViewById(R.id.editTextDescripccion);
        FloatingActionButton btnGuardarDatos = (FloatingActionButton) dialogo.findViewById(R.id.floatingActionButtonNextPaciente);
        btnGuardarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (validarTextosPaciente(identificacion, nombres, apellidos, descripccion)) {
                        preferencias.seIdentificacionPaciente(identificacion.getText().toString());
                        preferencias.setNamePaciente(nombres.getEditableText().toString());
                        preferencias.setApellidosPaciente(apellidos.getEditableText().toString());
                        preferencias.setDescripccionPaciente(descripccion.getEditableText().toString());
                        dialogo.dismiss();
                        crearDataPickerDialogo();
                    } else {
                        Toast(context.getString(R.string.recurde_llenar_datos));

                    }
                } catch (NullPointerException ex) {
                    Toast(context.getString(R.string.recurde_llenar_datos));
                    ex.printStackTrace();
                }
            }
        });
        dialogo.show();


    }

    /**
     * Crea un dialogo generico en base del recurso presente
     *
     * @param titulo titulo del dialogo
     * @return dialogo
     */
    private Dialog crearPickerGenerico(String titulo) {
        Dialog dialogo = new Dialog(context);
        dialogo.setTitle(titulo);
        dialogo.setContentView(R.layout.dialogo_nacimiento);
        dialogo.setCancelable(false);
        return dialogo;

    }

    /**
     * Dialogo fecha de nacimkiento paciente
     */
    private void crearDataPickerDialogo() {
        final Dialog dialogo = crearPickerGenerico(context.getText(R.string.titulo_tu_nacimiento).toString());
        final DatePicker calendario = (DatePicker) dialogo.findViewById(R.id.datePickerNacimiento);
        FloatingActionButton btnGuardar = (FloatingActionButton) dialogo.findViewById(R.id.floatingActionButtonGuardarCalendario);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferencias.setNacimientPaciente(calendario.getYear() + "-" + calendario.getMonth() + "-" + calendario.getDayOfMonth());
                Log.println(Log.ASSERT, "BD", preferencias.getNacimientPaciente());
                dialogo.dismiss();
                generarDialogoParametros(context.getString(R.string.titulo_parametros), true);
            }
        });
        dialogo.show();
    }

    /**
     * Dialogo para filtrar registros del paciente
     *
     * @param registros actividad
     * @param titulo    titulo del dialgo
     */
    public void crearDataPickerFiltroRegistros(final Registros registros, String titulo) {
        final Dialog dialogo = crearPickerGenerico(titulo);
        final DatePicker calendario = (DatePicker) dialogo.findViewById(R.id.datePickerNacimiento);
        FloatingActionButton btnGuardar = (FloatingActionButton) dialogo.findViewById(R.id.floatingActionButtonGuardarCalendario);
        eliminarDayField(calendario);
        btnGuardar.setImageResource(R.drawable.buscar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.println(Log.ASSERT, "CAL", calendario.getMonth() + " " + calendario.getYear());
                //  preferencias.setNacimientPaciente(calendario.getYear() + "-" + calendario.getMonth() + "-" + calendario.getDayOfMonth());
                //  Log.println(Log.ASSERT, "BD", preferencias.getNacimientPaciente());
                registros.setListaGeneral(registros.getOximetriaBDController().cargarRegistros(calendario.getYear(), calendario.getMonth()));
                registros.recargarRegistros();
                dialogo.dismiss();
                //  generarDialogoParametros(context.getString(R.string.titulo_parametros), true);
            }
        });
        dialogo.show();
    }

    /**
     * Elimina el campo de dia de un datapicker
     *
     * @param datePicker
     */
    private void eliminarDayField(DatePicker datePicker) {
        try {
            java.lang.reflect.Field[] f = datePicker.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : f) {
                if (field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner")) {
                    field.setAccessible(true);
                    Object dmPicker = new Object();
                    dmPicker = field.get(datePicker);
                    ((View) dmPicker).setVisibility(View.GONE);
                }
            }
        } catch (SecurityException e) {
            Log.println(Log.ASSERT, "CAL", e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.println(Log.ASSERT, "CAL", e.getMessage());
        } catch (IllegalAccessException e) {
            Log.println(Log.ASSERT, "CAL", e.getMessage());
        }

    }

    /**
     * Valida los datos que ingresa el paciente en formulario de inicio
     *
     * @param args vector textos
     * @return valides
     */
    private boolean validarTextosPaciente(EditText... args) {
        Log.println(Log.ASSERT, "BD", "Cantidad de argumentos" + args.length);
        if (args.length > 0)
            for (EditText texto : args) {
                if (texto.getText().toString().isEmpty() || texto.getText().toString().length() <= 4) {
                    return false;
                }
            }
        return true;

    }

    /**
     * Cancela un dialogo
     *
     * @param button
     * @param dialog
     */
    private void cancelarDialogo(Button button, final Dialog dialog) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    public Preferencias getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(Preferencias preferencias) {
        this.preferencias = preferencias;
    }
}
