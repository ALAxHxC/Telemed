package ponny.org.telemed.red.celular;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import ponny.org.telemed.R;
import ponny.org.telemed.vistas.Mensajes;

import static android.R.attr.phoneNumber;

/**
 * Created by Daniel on 17/01/2017.
 */

public class Llamada {
    private String telefono;
    private Context context;

    public Llamada(String telefono, Context context) {
        this.telefono = telefono;
        this.context = context;
    }

    public void llamarContacto() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", telefono, null)));
        }catch (Exception ex)
        {
         new Mensajes(context).simpleAltertDialog(context.getString(R.string.llame),context.getString(R.string.llame_mensaje));
        }
    }
}
