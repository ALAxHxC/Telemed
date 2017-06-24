package ponny.org.telemed.vistas.listas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.negocio.Oximetria;
import ponny.org.telemed.utilidades.Utilidades;

/**
 * Created by Daniel on 22/06/2017.
 */

public class ListaOximetrias extends BaseAdapter {
    private Context context;
    private List<Oximetria> oximetriaList;

    public ListaOximetrias(Context context, List<Oximetria> oximetriaList) {
        this.context = context;
        this.oximetriaList = oximetriaList;
    }

    @Override
    public int getCount() {
        return oximetriaList.size();
    }

    @Override
    public Object getItem(int i) {
        return oximetriaList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_oximetria, null);
        try {
            Oximetria oximetria = oximetriaList.get(i);
            TextView fecha = (TextView) view.findViewById(R.id.textViewIdItem);
            Log.println(Log.ASSERT,"Tag",oximetria.getDetalle()+"");
            fecha.setText(Utilidades.sdfCompleta.format(oximetria.getDetalle()));
            ImageView emergencia = (ImageView) view.findViewById(R.id.imageViewIsEmergencia);
            if (oximetria.isUrgencia() == 1) {
                emergencia.setImageDrawable(context.getResources().getDrawable(R.drawable.red));
            }
            TextView textViewSPO2Item = (TextView) view.findViewById(R.id.textViewSPO2Item);
            textViewSPO2Item.setText("" + oximetria.getSpo2());
            TextView textViewPulsoItem = (TextView) view.findViewById(R.id.textViewPulsoItem);
            textViewPulsoItem.setText("" + oximetria.getPulse());
            TextView textViewPI=(TextView)view.findViewById(R.id.textViewPIItem);
            textViewPI.setText(""+oximetria.getPi());

        } catch (Exception ex) {
            Log.println(Log.ASSERT, "Error", ex.toString());
            ex.printStackTrace();
        }
        return view;
    }
}
