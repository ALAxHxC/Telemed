package ponny.org.telemed.vistas.pacientes;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.Preferencias;
import ponny.org.telemed.datos.basededatos.OximetriaBDController;
import ponny.org.telemed.negocio.Oximetria;
import ponny.org.telemed.vistas.Mensajes;

import static android.R.attr.data;
import static ponny.org.telemed.utilidades.Utilidades.sdf;

public class Registros extends AppCompatActivity {
    private OximetriaBDController oximetriaBDController;
    private TabHost tabshost;
    private TabHost.TabSpec pulso, spo2;
    private LineChart lineChartPulso, lineChartOximetro;
    private List<Oximetria> listaGeneral;
    private ArrayList<String> valorex;
    private Preferencias preferencias;
    private Mensajes mensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registros);
        cargarToolbar();
        oximetriaBDController = new OximetriaBDController(this);
        listaGeneral = oximetriaBDController.cargarRegistros();
        preferencias = new Preferencias(this);
        mensajes = new Mensajes(this);
        cargarChart();
        CargarHost();
        try {
            cargarLineDataOximetria();
            cargarLineDataPulso();
        } catch (NullPointerException ex) {
            mensajes.Toast(getString(R.string.no_hay_registros));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_registros, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            mensajes.crearDataPickerFiltroRegistros(this, getString(R.string.seleccione));
            return true;
        }
        if (id == R.id.action_save) {
            this.guardarGraficas();
            return true;

        }
        if (id == R.id.action_all) {
            cargartodos();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void recargarRegistros() {
        try {
            lineChartPulso.invalidate();
            lineChartOximetro.invalidate();
            cargarLineDataOximetria();
            cargarLineDataPulso();
        } catch (Exception ex) {
            mensajes.Toast(getString(R.string.no_hay_registros));
        }
    }

    private void cargartodos() {
        setListaGeneral(oximetriaBDController.cargarRegistros());
        lineChartPulso.invalidate();
        lineChartOximetro.invalidate();
        cargarLineDataOximetria();
        cargarLineDataPulso();

    }

    private void cargarToolbar() {
        setTitle(null);
        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbarRegistros);
        setSupportActionBar(topToolBar);
        topToolBar.setLogo(R.drawable.logo);
        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));

    }

    private void CargarHost() {
        tabshost = (TabHost) findViewById(R.id.tabhostRegistros);
        tabshost.setup();
        pulso = tabshost.newTabSpec(getString(R.string.pulso));
        pulso.setContent(R.id.tabPulso);
        pulso.setIndicator(getString(R.string.pulso));
        spo2 = tabshost.newTabSpec(getString(R.string.oximetria));
        spo2.setContent(R.id.tabOximetro);
        spo2.setIndicator(getString(R.string.oximetria));
        tabshost.addTab(pulso);
        tabshost.addTab(spo2);

    }

    private void cargarChart() {
        lineChartPulso = (LineChart) findViewById(R.id.chartPulso);
        lineChartOximetro = (LineChart) findViewById(R.id.chartOximetro);
    }

    private List<Entry> cargarEntryPulso() {
        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < listaGeneral.size(); i++) {
            Entry entry = new Entry(i, listaGeneral.get(i).getPulse());
            entries.add(entry);
        }
        return entries;

    }

    private List<Entry> cargarEntryOximetria() {
        List<Entry> entries = new ArrayList<>();
        valorex = new ArrayList<>();
        Log.println(Log.ASSERT, "BLE", "Cargara " + listaGeneral.size());
        for (int i = 0; i < listaGeneral.size(); i++) {
            Entry entry = new Entry((i), listaGeneral.get(i).getSpo2());
            entries.add(entry);
            valorex.add(String.valueOf(i));
        }
        return entries;
    }

    private void cargarLineDataOximetria() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataOximetria());
        dataSets.add(oximetriaUmbralData());
        LineData lineData = new LineData(dataSets);
        ejeX(lineChartOximetro);
        lineChartOximetro.setData(lineData);

        lineChartOximetro.invalidate(); // refresh
    }

    private void cargarLineDataPulso() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataPulso());
        dataSets.add(pusloUmbralAltoData());
        dataSets.add(pulsoUmbralBajoData());
        LineData lineData = new LineData(dataSets);
        ejeX(lineChartPulso);
        lineChartPulso.setData(lineData);

        lineChartPulso.invalidate(); // refresh

    }

    private LineDataSet dataPulso() {
        LineDataSet pusloData = new LineDataSet(cargarEntryPulso(), getString(R.string.pulso));
        pusloData.setColor(Color.TRANSPARENT);
        pusloData.setFillColor(Color.GREEN);
        pusloData.setColor(Color.RED);
        pusloData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return pusloData;
    }

    private LineDataSet dataOximetria() {
        LineDataSet oximetriasData = new LineDataSet(cargarEntryOximetria(), getString(R.string.oximetria));
        oximetriasData.setColor(Color.TRANSPARENT);
        oximetriasData.setFillColor(Color.GREEN);
        oximetriasData.setColor(Color.RED);
        oximetriasData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return oximetriasData;

    }

    private LineDataSet oximetriaUmbralData() {

        LineDataSet oximetriaUmbralData = new LineDataSet(valorUrgenciaOximetria(), getString(R.string.umbral));
        oximetriaUmbralData.setColor(Color.BLACK);
        oximetriaUmbralData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return oximetriaUmbralData;
    }

    private LineDataSet pulsoUmbralBajoData() {

        LineDataSet pusloUmbralData = new LineDataSet(valorUrgenciapulsobajo(), getString(R.string.pulso_bajo));
        pusloUmbralData.setColor(Color.BLACK);
        pusloUmbralData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return pusloUmbralData;
    }

    private LineDataSet pusloUmbralAltoData() {

        LineDataSet pusloUmbralData = new LineDataSet(valorUrgenciapulsoalto(), getString(R.string.pulso_alto));
        pusloUmbralData.setColor(Color.BLACK);
        pusloUmbralData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return pusloUmbralData;
    }

    private List<Entry> valorUrgenciaOximetria() {
        List<Entry> valorUrgencia = new ArrayList<>();
        valorUrgencia.add(new Entry(0, preferencias.getSPO2()));
        valorUrgencia.add(new Entry(listaGeneral.size() - 1, preferencias.getSPO2()));
        return valorUrgencia;

    }

    private List<Entry> valorUrgenciapulsobajo() {
        List<Entry> valorUrgencia = new ArrayList<>();
        valorUrgencia.add(new Entry(0, preferencias.getPulsoBajo()));
        valorUrgencia.add(new Entry(listaGeneral.size() - 1, preferencias.getPulsoBajo()));
        return valorUrgencia;

    }

    private List<Entry> valorUrgenciapulsoalto() {
        List<Entry> valorUrgencia = new ArrayList<>();
        valorUrgencia.add(new Entry(0, preferencias.getPulsoAlto()));
        valorUrgencia.add(new Entry(listaGeneral.size() - 1, preferencias.getPulsoAlto()));
        return valorUrgencia;

    }

    private void ejeX(LineChart lineChart) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGridColor(Color.BLACK);

    }

    private void guardarGraficas() {
        if(listaGeneral.size()<=1) {
            if (lineChartOximetro.saveToGallery(getString(R.string.oximetria) + "-" + sdf.format(new Date()), 100) &&
                    lineChartPulso.saveToGallery(getString(R.string.pulso) + sdf.format(new Date()), 100)) {
                mensajes.Toast(getString(R.string.imagen_guardada));
            } else {
                mensajes.Toast(getString(R.string.imagen_no_guardada));
            }
        }else
        {

            mensajes.Toast(getString(R.string.minimo_dos_registros));
        }
    }

    public List<Oximetria> getListaGeneral() {
        return listaGeneral;
    }

    public void setListaGeneral(List<Oximetria> listaGeneral) {
        this.listaGeneral = listaGeneral;
    }

    public OximetriaBDController getOximetriaBDController() {
        return oximetriaBDController;
    }

    public void setOximetriaBDController(OximetriaBDController oximetriaBDController) {
        this.oximetriaBDController = oximetriaBDController;
    }
}





