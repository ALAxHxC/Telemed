package ponny.org.telemed.vistas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import ponny.org.telemed.datos.Preferencias;


/**
 * Created by daniel on 29/07/2016.
 */
public class Mensajes {
    private Context context;
    private Preferencias preferencias;

    public Mensajes(Context context) {
        this.context = context;
        preferencias = new Preferencias(context);
    }

    public void Toast(String body) {
        Toast.makeText(context, body, Toast.LENGTH_LONG).show();

    }

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

    public void simpleAltertDialog(String titulo, String cuerpo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo);
        builder.setMessage(cuerpo);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();

    }

    public void registrarNumero(String titulo,String mensaje)

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
                        registrarNumero1( input.getText().toString());
                    }
                }
        );
        builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        builder.show();
    }

    private boolean validarTexto(String entrada) {
        return !(entrada.isEmpty() || entrada.length() < 10);
    }

    private void registrarNumero1(String entrada) {
        if (validarTexto(entrada)) {
            preferencias.setNumero1(entrada);
        }
    }


}
