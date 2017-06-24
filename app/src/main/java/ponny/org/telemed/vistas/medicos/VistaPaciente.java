package ponny.org.telemed.vistas.medicos;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.negocio.EntidadesFireBase;
import ponny.org.telemed.negocio.Oximetria;
import ponny.org.telemed.utilidades.Utilidades;
import ponny.org.telemed.vistas.listas.ListaOximetrias;
import ponny.org.telemed.vistas.manager.ActividadesManager;

public class VistaPaciente extends AppCompatActivity {
    private List<EntidadesFireBase.OximetriaFB> listaOximetrias;
    private List<Oximetria> oximetriasTratadas;
    private ActividadesManager actividadesManager;
    private TabHost tabshost;
    private TabHost.TabSpec pulso, spo2, registros;
    private LineChart lineChartPulso, lineChartOximetro;
    private EntidadesFireBase.Paciente paciente;
    private ArrayList<String> valorex;
    private ListView listViewOximetrias;
    private TextView idpacientetxt, pulsoaltotxt, puslobajotxt, spo2txt, nombrestxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_paciente);
        actividadesManager = new ActividadesManager(this);
        cargarExtras();
        cargarVistas();
    }

    private void cargarExtras() {
        listaOximetrias = actividadesManager.getOximtriasFireBase(getIntent().getExtras());
        paciente = actividadesManager.getPaciente(getIntent().getExtras());
        oximetriasTratadas=new ArrayList<>();
        oximetriasTratadas =Utilidades.convertirFBtoLocal(listaOximetrias);
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
        valorUrgencia.add(new Entry(0, paciente.getSpo2()));
        valorUrgencia.add(new Entry(oximetriasTratadas.size() - 1, paciente.getSpo2()));
        return valorUrgencia;

    }

    private List<Entry> valorUrgenciapulsobajo() {
        List<Entry> valorUrgencia = new ArrayList<>();
        valorUrgencia.add(new Entry(0, paciente.getPuslobajo()));
        valorUrgencia.add(new Entry(oximetriasTratadas.size() - 1, paciente.getPuslobajo()));
        return valorUrgencia;

    }

    private List<Entry> valorUrgenciapulsoalto() {
        List<Entry> valorUrgencia = new ArrayList<>();
        valorUrgencia.add(new Entry(0, paciente.getPusloalto()));
        valorUrgencia.add(new Entry(oximetriasTratadas.size() - 1, paciente.getPusloalto()));
        return valorUrgencia;

    }

    private void ejeX(LineChart lineChart) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGridColor(Color.BLACK);

    }

    private List<Entry> cargarEntryPulso() {
        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < oximetriasTratadas.size(); i++) {
            Entry entry = new Entry(i, oximetriasTratadas.get(i).getPulse());
            entries.add(entry);
        }
        return entries;

    }

    private List<Entry> cargarEntryOximetria() {
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
}
