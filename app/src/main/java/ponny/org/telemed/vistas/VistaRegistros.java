package ponny.org.telemed.vistas;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

/**
 * Created by Daniel on 24/06/2017.
 */

public interface VistaRegistros {
    public void cargarToolbar();
    public void cargarLineDataOximetria();
    public void cargarLineDataPulso();
    public   LineDataSet dataPulso();
    public LineDataSet oximetriaUmbralData();
    public LineDataSet pulsoUmbralBajoData();
    public LineDataSet pusloUmbralAltoData();
    public List<Entry> cargarEntryOximetria();
    public List<Entry> valorUrgenciaOximetria();
    public List<Entry> valorUrgenciapulsobajo();
    public List<Entry> valorUrgenciapulsoalto();
    public void ejeX(LineChart lineChart);
    public void guardarGraficas();
    public void recargarRegistros();
    public void cargartodos();

}
