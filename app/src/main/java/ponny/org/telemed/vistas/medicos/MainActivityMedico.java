package ponny.org.telemed.vistas.medicos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ponny.org.telemed.R;
import ponny.org.telemed.vistas.Mensajes;

public class MainActivityMedico extends AppCompatActivity {
    private Mensajes mensajes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_medico);
        mensajes = new Mensajes(this);
        mensajes.generarDialogoMedicoInicial();
    }

}
