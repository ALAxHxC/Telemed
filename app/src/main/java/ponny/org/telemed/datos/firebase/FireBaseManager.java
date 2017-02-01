package ponny.org.telemed.datos.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.Preferencias;
import ponny.org.telemed.negocio.CentroMedicoNegocio;
import ponny.org.telemed.negocio.FireBaseEntitys;
import ponny.org.telemed.vistas.MainActivity;
import ponny.org.telemed.vistas.Mensajes;
import ponny.org.telemed.vistas.sesion.Login_inicial;

/**
 * Created by Daniel on 30/01/2017.
 */

public class FireBaseManager {
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    Context context;
    private Mensajes mensajes;
    private List<FireBaseEntitys.CentroMedico> listaFirebase;

    public FireBaseManager(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listaFirebase = new ArrayList<>();
        mensajes = new Mensajes(context);
        //      subirCentrosMedicos();
        // subirPaciente();
    }

    public FireBaseManager(Context context, Mensajes mensajes) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listaFirebase = new ArrayList<>();
        this.mensajes = mensajes;
        //      subirCentrosMedicos();
        // subirPaciente();
    }

    //En el caso que se quiera subir informacion
    private void subirCentrosMedicos() {
        mDatabase.child(context.getString(R.string.Centro_Medico)).push();
        List<FireBaseEntitys.CentroMedico> lista = new ArrayList();
        lista.add(new FireBaseEntitys.CentroMedico(1, "sha1234", "sha1234", "Famisanar"));
        lista.add(new FireBaseEntitys.CentroMedico(2, "sha1234", "sha1234", "Cafe Salud"));
        lista.add(new FireBaseEntitys.CentroMedico(3, "sha1234", "sha1234", "Salud Total"));
        lista.add(new FireBaseEntitys.CentroMedico(4, "sha1234", "sha1234", "Compensar"));
        lista.add(new FireBaseEntitys.CentroMedico(5, "sha1234", "sha1234", "Tu familia"));
        lista.add(new FireBaseEntitys.CentroMedico(6, "sha1234", "sha1234", "Colsubsidio"));
        mDatabase.child(context.getString(R.string.Centro_Medico)).setValue(lista);
    }

    private void subirPaciente() {
        mDatabase.child(context.getString(R.string.paciente)).push().setValue("paciente");
    }

    public void cargarPaciente(Preferencias preferencias) {
        Log.println(Log.ASSERT, "CREANDO PACIENTE", "CREANDO PACIENTE");
        mDatabase.child(context.getString(R.string.paciente)).child(preferencias.getIdentificacionPaciente()).setValue(
                new FireBaseEntitys.Paciente(preferencias.getIdentificacionPaciente(),
                        preferencias.getNombrePaciente(),
                        preferencias.getApellidosPaciente(),
                        preferencias.getSPO2(),
                        preferencias.getPulsoBajo(),
                        preferencias.getPulsoAlto(),
                        preferencias.getNumero1())
        );
    }

    public void cargarCentrosMedicos(final Login_inicial login_inicial) {
        mensajes.mostrarDialogo();
        mDatabase.child(context.getString(R.string.Centro_Medico)).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterator<DataSnapshot> iterable = dataSnapshot.getChildren().iterator();
                        while (iterable.hasNext()) {
                            //  Log.println(Log.ASSERT, "FBSQL", iterable.next().getValue().toString());
                            listaFirebase.add(iterable.next().getValue(FireBaseEntitys.CentroMedico.class));

                        }
                        Log.println(Log.ASSERT, "FB SQL", getListaFirebase().size() + "");
                        mensajes.cancelarDialogos();
                        login_inicial.setCentroMedicoNegocio(new CentroMedicoNegocio(listaFirebase));
                        login_inicial.cargarSpinnerHospitales();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.println(Log.ASSERT, "FB SQL", databaseError.toException().getMessage());
                        databaseError.toException().printStackTrace();

                    }
                });

    }

    public List<FireBaseEntitys.CentroMedico> getListaFirebase() {
        return listaFirebase;
    }
}
