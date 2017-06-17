package ponny.org.telemed.vistas.medicos.listas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.negocio.EntidadesFireBase;

/**
 * Created by Daniel on 16/06/2017.
 */

public class ListaMedico extends BaseAdapter {
    private List<EntidadesFireBase.Paciente> listaPacientes;
    private Context context;

    public ListaMedico(List<EntidadesFireBase.Paciente> listaPacientes, Context context) {
        this.listaPacientes = listaPacientes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listaPacientes.size();
    }

    @Override
    public Object getItem(int i) {
        return listaPacientes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_paciente, null);
        try {
            EntidadesFireBase.Paciente paciente = listaPacientes.get(i);
            Log.println(Log.ASSERT,"Paciente",paciente.toStringMedico());
            TextView nombres = (TextView) view.findViewById(R.id.item_textViewNombres);
            nombres.setText(paciente.getNombres() + "  " + paciente.getApellidos());
            TextView identificacion = (TextView) view.findViewById(R.id.item_textViewId);
            identificacion.setText(paciente.getIdentificacion()+"");
            TextView pulso_bajo = (TextView) view.findViewById(R.id.item_textViewPulsoBajo);
            pulso_bajo.setText(paciente.getPuslobajo()+"");
            TextView pulso_alto = (TextView) view.findViewById(R.id.item_textViewPulsoAlto);
            pulso_alto.setText(paciente.getPusloalto()+"");
            TextView spo2 = (TextView) view.findViewById(R.id.item_textViewSPO2);
            spo2.setText(paciente.getSpo2()+"");
        } catch (Exception ex) {
            Log.println(Log.ASSERT, "Error", ex.toString());
            ex.printStackTrace();
        }
        return view;


    }
}
