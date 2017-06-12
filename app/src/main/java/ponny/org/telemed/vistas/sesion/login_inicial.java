package ponny.org.telemed.vistas.sesion;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.Preferencias;
import ponny.org.telemed.datos.firebase.FireBaseManager;
import ponny.org.telemed.negocio.CentroMedicoNegocio;
import ponny.org.telemed.vistas.MainActivity;
import ponny.org.telemed.vistas.Mensajes;
import ponny.org.telemed.vistas.medicos.MainActivityMedico;

public class Login_inicial extends AppCompatActivity {
    private FireBaseManager fireBaseManager;
    private TextView txtPassword;
    private Spinner spHospitales;
    private FloatingActionButton btnMedico, btnPaciente;
    private CentroMedicoNegocio centroMedicoNegocio;
    private Mensajes mensajes;
    private Preferencias preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_inicial);
        fireBaseManager = new FireBaseManager(this);
        fireBaseManager.cargarCentrosMedicos(this);
        mensajes = new Mensajes(this);
        preferencias = new Preferencias(this);
        if (!Preferencias.debeCrear) {
            iniciarPaciente();
        }
        cargarVistas();
        cargarMedico();
        cargarPaciente();
        //  Log.println(Log.ASSERT, "FB SQL", fireBaseManager.getListaFirebase().size() + "");

    }

    private void cargarVistas() {
        txtPassword = (TextView) findViewById(R.id.passwordHospital);
        spHospitales = (Spinner) findViewById(R.id.spinnerHospital);
        btnMedico = (FloatingActionButton) findViewById(R.id.fbMedico);
        btnPaciente = (FloatingActionButton) findViewById(R.id.fbPaciente);


    }

    public void cargarSpinnerHospitales() {
        String[] hospitales = new String[fireBaseManager.getListaFirebase().size()];
        for (int i = 0; i < fireBaseManager.getListaFirebase().size(); i++) {

            hospitales[i] = fireBaseManager.getListaFirebase().get(i).getNombre();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hospitales);
        spHospitales.setAdapter(adapter);

    }

    private void cargarMedico() {
        btnMedico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int respuesta = centroMedicoNegocio.buscarCentroMedicoMedico(spHospitales.getSelectedItem().toString(), txtPassword.getText().toString());
                if (respuesta == 0) {
                    mensajes.Toast(getString(R.string.no_encontro_hospital));
                } else {

                    iniciarMedico();

                }
            }
        });

    }

    private void cargarPaciente() {
        btnPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int respuesta = centroMedicoNegocio.buscarCentroMedicoPaciente(spHospitales.getSelectedItem().toString(), txtPassword.getText().toString());
                if (respuesta == 0) {
                    mensajes.Toast(getString(R.string.no_encontro_hospital));
                } else {
                    preferencias.setIdCentroMedico(respuesta);
                    iniciarPaciente();

                }
            }
        });

    }

    private void iniciarMedico() {
        Intent intent = new Intent(this, MainActivityMedico.class);
        startActivity(intent);
    }

    private void iniciarPaciente() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public CentroMedicoNegocio getCentroMedicoNegocio() {
        return centroMedicoNegocio;
    }

    public void setCentroMedicoNegocio(CentroMedicoNegocio centroMedicoNegocio) {
        this.centroMedicoNegocio = centroMedicoNegocio;
    }
}
