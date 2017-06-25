package ponny.org.telemed.vistas.medicos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.negocio.EntidadesFireBase;
import ponny.org.telemed.negocio.Oximetria;
import ponny.org.telemed.utilidades.Utilidades;
import ponny.org.telemed.vistas.Mensajes;
import ponny.org.telemed.vistas.VistaRegistros;
import ponny.org.telemed.vistas.listas.ListaOximetrias;
import ponny.org.telemed.vistas.manager.ActividadesManager;

import static ponny.org.telemed.utilidades.Utilidades.sdf;

public class VistaPaciente extends AppCompatActivity implements VistaRegistros {
    private List<EntidadesFireBase.OximetriaFB> listaOximetrias;
    private List<Oximetria> oximetriasListTotal;
    private List<Oximetria> oximetriasTratadas;
    private ActividadesManager actividadesManager;
    private TabHost tabshost;
    private TabHost.TabSpec pulso, spo2, registros;
    private LineChart lineChartPulso, lineChartOximetro;
    private EntidadesFireBase.Paciente paciente;
    private ArrayList<String> valorex;
    private ListView listViewOximetrias;
    private TextView idpacientetxt, pulsoaltotxt, puslobajotxt, spo2txt, nombrestxt;
    private Mensajes mensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_paciente);
        actividadesManager = new ActividadesManager(this);
        mensajes = new Mensajes(this);
        cargarExtras();
        cargarVistas();
        cargarToolbar();
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
            mensajes.crearDataPickerFiltroRegistrosMedico(this, getString(R.string.seleccione));
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

    public void cargarToolbar() {
        setTitle(null);
        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbarActividadPacientes);
        setSupportActionBar(topToolBar);
        topToolBar.setLogo(R.drawable.logo);
        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));

    }

    private void cargarExtras() {
        listaOximetrias = actividadesManager.getOximtriasFireBase(getIntent().getExtras());
        oximetriasListTotal = new ArrayList<>();
        paciente = actividadesManager.getPaciente(getIntent().getExtras());
        oximetriasTratadas = new ArrayList<>();
        oximetriasTratadas = Utilidades.convertirFBtoLocal(listaOximetrias);
        organizarRegistros();
        oximetriasListTotal.addAll(oximetriasTratadas);
        Log.println(Log.ASSERT, "FB", listaOximetrias.toString());
    }

    private void cargarVistas() {
        lineChartPulso = (LineChart) findViewById(R.id.chartPulsoPaciente);
        lineChartOximetro = (LineChart) findViewById(R.id.chartOximetroPaciente);
        listViewOximetrias = (ListView) findViewById(R.id.listViewOximetriasItem);
        ListaOximetrias adapter = new ListaOximetrias(this, oximetriasTratadas);
        listViewOximetrias.setAdapter(adapter);
        cargarHost();
        cargarLineDataOximetria();
        cargarLineDataPulso();
        cargarVistasPaciente();
    }

    private void cargarVistasPaciente() {
        idpacientetxt = (TextView) findViewById(R.id.textViewCedulaView);
        idpacientetxt.setText(paciente.getIdentificacion());

        nombrestxt = (TextView) findViewById(R.id.textViewPacienteNombres);
        nombrestxt.setText(paciente.getNombres() + " " + paciente.getApellidos());

        spo2txt = (TextView) findViewById(R.id.textViewPusloAltoView);
        spo2txt.setText("" + paciente.getSpo2());

        puslobajotxt = (TextView) findViewById(R.id.textViewPulsoBajoView);
        puslobajotxt.setText("" + paciente.getPuslobajo());

        pulsoaltotxt = (TextView) findViewById(R.id.textViewSPO2View);
        pulsoaltotxt.setText("" + paciente.getPusloalto());
    }

    private void cargarHost() {
        tabshost = (TabHost) findViewById(R.id.tabhostRegistros);
        tabshost.setup();

        pulso = tabshost.newTabSpec(getString(R.string.pulso));
        pulso.setContent(R.id.tabPulsoPaciente);
        pulso.setIndicator(getString(R.string.pulso));

        spo2 = tabshost.newTabSpec(getString(R.string.oximetria));
        spo2.setContent(R.id.tabOximetroPaciente);
        spo2.setIndicator(getString(R.string.oximetria));

        registros = tabshost.newTabSpec(getString(R.string.oximetria));
        registros.setContent(R.id.tabRegistrosPaciente);
        registros.setIndicator(getString(R.string.Registros));

        tabshost.addTab(registros);
        tabshost.addTab(pulso);
        tabshost.addTab(spo2);

    }

    public void cargarLineDataOximetria() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataOximetria());
        dataSets.add(oximetriaUmbralData());
        LineData lineData = new LineData(dataSets);
        ejeX(lineChartOximetro);
        lineChartOximetro.setData(lineData);

        lineChartOximetro.invalidate(); // refresh
    }

    public void cargarLineDataPulso() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataPulso());
        dataSets.add(pusloUmbralAltoData());
        dataSets.add(pulsoUmbralBajoData());
        LineData lineData = new LineData(dataSets);
        ejeX(lineChartPulso);
        lineChartPulso.setData(lineData);

        lineChartPulso.invalidate(); // refresh

    }

    public LineDataSet dataPulso() {
        LineDataSet pusloData = new LineDataSet(cargarEntryPulso(), getString(R.string.pulso));
        pusloData.setColor(Color.TRANSPARENT);
        pusloData.setFillColor(Color.GREEN);
        pusloData.setColor(Color.RED);
        pusloData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return pusloData;
    }

    public LineDataSet dataOximetria() {
        LineDataSet oximetriasData = new LineDataSet(cargarEntryOximetria(), getString(R.string.oximetria));
        oximetriasData.setColor(Color.TRANSPARENT);
        oximetriasData.setFillColor(Color.GREEN);
        oximetriasData.setColor(Color.RED);
        oximetriasData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return oximetriasData;

    }

    public LineDataSet oximetriaUmbralData() {

        LineDataSet oximetriaUmbralData = new LineDataSet(valorUrgenciaOximetria(), getString(R.string.umbral));
        oximetriaUmbralData.setColor(Color.BLACK);
        oximetriaUmbralData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return oximetriaUmbralData;
    }

    public LineDataSet pulsoUmbralBajoData() {

        LineDataSet pusloUmbralData = new LineDataSet(valorUrgenciapulsobajo(), getString(R.string.pulso_bajo));
        pusloUmbralData.setColor(Color.BLACK);
        pusloUmbralData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return pusloUmbralData;
    }

    public LineDataSet pusloUmbralAltoData() {

        LineDataSet pusloUmbralData = new LineDataSet(valorUrgenciapulsoalto(), getString(R.string.pulso_alto));
        pusloUmbralData.setColor(Color.BLACK);
        pusloUmbralData.setAxisDependency(YAxis.AxisDependency.LEFT);
        return pusloUmbralData;
    }

    public List<Entry> valorUrgenciaOximetria() {
        List<Entry> valorUrgencia = new ArrayList<>();
        valorUrgencia.add(new Entry(0, paciente.getSpo2()));
        valorUrgencia.add(new Entry(oximetriasTratadas.size() - 1, paciente.getSpo2()));
        return valorUrgencia;

    }

    public List<Entry> valorUrgenciapulsobajo() {
        List<Entry> valorUrgencia = new ArrayList<>();
        valorUrgencia.add(new Entry(0, paciente.getPuslobajo()));
        valorUrgencia.add(new Entry(oximetriasTratadas.size() - 1, paciente.getPuslobajo()));
        return valorUrgencia;

    }

    public List<Entry> valorUrgenciapulsoalto() {
        List<Entry> valorUrgencia = new ArrayList<>();
        valorUrgencia.add(new Entry(0, paciente.getPusloalto()));
        valorUrgencia.add(new Entry(oximetriasTratadas.size() - 1, paciente.getPusloalto()));
        return valorUrgencia;

    }

    public void ejeX(LineChart lineChart) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGridColor(Color.BLACK);

    }

    @Override
    public void guardarGraficas() {

    }

    public List<Entry> cargarEntryPulso() {
        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < oximetriasTratadas.size(); i++) {
            Entry entry = new Entry(i, oximetriasTratadas.get(i).getPulse());
            entries.add(entry);
        }
        return entries;

    }

    public List<Entry> cargarEntryOximetria() {
        List<Entry> entries = new ArrayList<>();
        valorex = new ArrayList<>();
        Log.println(Log.ASSERT, "BLE", "Cargara " + oximetriasTratadas.size());
        for (int i = 0; i < oximetriasTratadas.size(); i++) {
            Entry entry = new Entry((i), oximetriasTratadas.get(i).getSpo2());
            entries.add(entry);
            valorex.add(String.valueOf(i));
        }
        return entries;
    }

    public void recargarRegistros() {
        try {
            lineChartPulso.invalidate();
            lineChartOximetro.invalidate();
            ListaOximetrias adapter = new ListaOximetrias(this, oximetriasTratadas);
            listViewOximetrias.setAdapter(adapter);
            cargarLineDataOximetria();
            cargarLineDataPulso();
        } catch (Exception ex) {
            mensajes.Toast(getString(R.string.no_hay_registros));
        }
    }

    public List<Oximetria> getOximetriasTratadas() {
        return oximetriasTratadas;
    }

    public void setOximetriasTratadas(List<Oximetria> oximetriasTratadas) {
        this.oximetriasTratadas = oximetriasTratadas;
    }

    public boolean buscarSegunFecha(int year, int month) {
        List<Oximetria> listaTemporal = new ArrayList<>();
        for (Oximetria oximetria : oximetriasTratadas) {
            if (oximetria.getCalendar().get(Calendar.MONTH) == month
                    && oximetria.getCalendar().get(Calendar.YEAR) == year) {
                listaTemporal.add(oximetria);
            }
        }
        if (listaTemporal.size() > 0) {
            return true;
        }
        mensajes.Toast(getString(R.string.no_hay_registros));
        return false;
    }

    public void cargartodos() {
        mensajes.Toast(getString(R.string.recargando_registros));
        oximetriasTratadas.clear();
        oximetriasTratadas.addAll(this.oximetriasListTotal);
        recargarRegistros();
    }
    public void organizarRegistros(){
        Collections.sort(oximetriasTratadas, new Comparator<Oximetria>() {

            @Override
            public int compare(Oximetria oximetria, Oximetria t1) {
                return (int)(t1.getCalendar().getTimeInMillis()-oximetria.getCalendar().getTimeInMillis());
            }

        });

    }

}
