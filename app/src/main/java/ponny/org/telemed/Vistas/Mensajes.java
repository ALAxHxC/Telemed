package ponny.org.telemed.vistas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;


/**
 * Created by daniel on 29/07/2016.
 */
public class Mensajes {
    private Context context;

    public Mensajes(Context context) {
        this.context = context;
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


}
