package ponny.org.telemed.vistas.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;
import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.negocio.EntidadesFireBase;
import ponny.org.telemed.vistas.medicos.VistaPaciente;
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
    public void irPacienteId(EntidadesFireBase.Paciente id, List<EntidadesFireBase.OximetriaFB> oximetriaFBList){
        Bundle bundle=new Bundle();
        bundle.putSerializable(context.getString(R.string.identificacionPac),id);
        bundle.putSerializable(context.getString(R.string.registros_oximetrias), (Serializable) oximetriaFBList);
        Intent intent=new Intent(context, VistaPaciente.class);
        intent.replaceExtras(bundle);
        context.startActivity(intent);

    }
    public List<EntidadesFireBase.OximetriaFB> getOximtriasFireBase(Bundle bundle){
        return (List<EntidadesFireBase.OximetriaFB>)bundle.getSerializable(context.getString(R.string.registros_oximetrias));
    }
    public EntidadesFireBase.Paciente getPaciente(Bundle bundle){
        return (EntidadesFireBase.Paciente)bundle.getSerializable(context.getString(R.string.identificacionPac));
    }
}
