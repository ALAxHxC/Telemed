package ponny.org.telemed.vistas.manager;

import android.content.Context;
import android.content.Intent;

import ponny.org.telemed.vistas.pacientes.MainActivity;
import ponny.org.telemed.vistas.medicos.MainActivityMedico;

/**
 * Created by Daniel on 14/06/2017.
 */

public class ActividadesManager {
    private Context context;

    public ActividadesManager(Context context) {
        this.context = context;
    }
    public void irMedicoMain(){
        context.startActivity(new Intent(context, MainActivityMedico.class));
    }
    public void irPacienteMain(){
        context.startActivity(new Intent(context,MainActivity.class));
    }
}
