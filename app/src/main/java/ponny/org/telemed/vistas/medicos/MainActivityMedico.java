package ponny.org.telemed.vistas.medicos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.Preferencias;
import ponny.org.telemed.datos.firebase.FireBaseManager;
import ponny.org.telemed.negocio.EntidadesFireBase;
import ponny.org.telemed.vistas.Mensajes;
import ponny.org.telemed.vistas.manager.ActividadesManager;
import ponny.org.telemed.vistas.medicos.listas.ListaMedico;

public class MainActivityMedico extends AppCompatActivity {
    private Mensajes mensajes;
    private ListView listaPacientes;
    private Toolbar toolbarMedico;
    private FireBaseManager fireBaseManager;
    private Preferencias preferencias;
    private List<EntidadesFireBase.Paciente> pacienteList;
    private ListaMedico adapter;
    private ActividadesManager actividadesManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_medico);
      // Preferencias preferencias
        pacienteList=new ArrayList<>();
        cargarVistas();
       mensajes = new Mensajes(this);
        preferencias=new Preferencias(this);
        adapter=new ListaMedico(null,this);
        actividadesManager=new ActividadesManager(this);
        try{
       fireBaseManager=new FireBaseManager(this,mensajes);
        fireBaseManager.cargarPacientes(this);}
        catch (Exception ex){
            Log.println(Log.ASSERT,"error",ex.toString());
            ex.printStackTrace();
        }

    }

    private void cargarVistas()
    {
        listaPacientes=(ListView)findViewById(R.id.listViewPacientes);
        toolbarMedico=(Toolbar)findViewById(R.id.toolbarMedico);
        listaPacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                fireBaseManager.traerRegistrosMedicos(((EntidadesFireBase.Paciente)adapter.getItem(i)));
            }
        });
    }

    public ListView getListaPacientes() {
        return listaPacientes;
    }

    public List<EntidadesFireBase.Paciente> getPacienteList() {
        return pacienteList;
    }

    public void setPacienteList(List<EntidadesFireBase.Paciente> pacienteList) {
        this.pacienteList = pacienteList;
    }

    public ListaMedico getAdapter() {
        return adapter;
    }

    public void setAdapter(ListaMedico adapter) {
        this.adapter = adapter;
        listaPacientes.setAdapter(adapter);

    }
}
