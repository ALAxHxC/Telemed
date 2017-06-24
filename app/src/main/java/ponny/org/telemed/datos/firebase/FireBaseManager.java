package ponny.org.telemed.datos.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ponny.org.telemed.R;
import ponny.org.telemed.datos.Preferencias;
import ponny.org.telemed.negocio.CentroMedicoNegocio;
import ponny.org.telemed.negocio.EntidadesFireBase;
import ponny.org.telemed.negocio.Oximetria;
import ponny.org.telemed.vistas.Mensajes;
import ponny.org.telemed.vistas.manager.ActividadesManager;
import ponny.org.telemed.vistas.medicos.MainActivityMedico;
import ponny.org.telemed.vistas.medicos.listas.ListaMedico;
import ponny.org.telemed.vistas.sesion.Login_inicial;

/**
 * Created by Daniel on 30/01/2017.
 */

public class FireBaseManager {
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    Context context;
    private Mensajes mensajes;
    private List<EntidadesFireBase.CentroMedico> listaFirebase;
    private Preferencias preferencias;
    private ActividadesManager actividadesManager;

    public FireBaseManager(Context context, Preferencias preferencias) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listaFirebase = new ArrayList<>();
        this.preferencias = preferencias;
        mensajes = new Mensajes(context, preferencias, this);
        actividadesManager=new ActividadesManager(context);
        //      subirCentrosMedicos();
        // subirPaciente();
    }

    public FireBaseManager(Context context, Mensajes mensajes) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listaFirebase = new ArrayList<>();
        this.mensajes = mensajes;
        this.preferencias = new Preferencias(context);
        actividadesManager=new ActividadesManager(context);
        //      subirCentrosMedicos();
        // subirPaciente();
    }

    //En el caso que se quiera subir informacion
    private void subirCentrosMedicos() {
        mDatabase.child(context.getString(R.string.Centro_Medico)).push();
        List<EntidadesFireBase.CentroMedico> lista = new ArrayList();
        lista.add(new EntidadesFireBase.CentroMedico(1, "sha1234", "sha1234", "Famisanar"));
        lista.add(new EntidadesFireBase.CentroMedico(2, "sha1234", "sha1234", "Cafe Salud"));
        lista.add(new EntidadesFireBase.CentroMedico(3, "sha1234", "sha1234", "Salud Total"));
        lista.add(new EntidadesFireBase.CentroMedico(4, "sha1234", "sha1234", "Compensar"));
        lista.add(new EntidadesFireBase.CentroMedico(5, "sha1234", "sha1234", "Tu familia"));
        lista.add(new EntidadesFireBase.CentroMedico(6, "sha1234", "sha1234", "Colsubsidio"));
        mDatabase.child(context.getString(R.string.Centro_Medico)).setValue(lista);
    }

    public void subirRegistroMedico(String id, Oximetria oximetria) {
        mDatabase.child(context.getString(R.string.oximetria)).
                child(id).
                child(oximetria.getCalendar().getTimeInMillis() + "")
                .setValue(new EntidadesFireBase.OximetriaFB(oximetria.getSpo2(),
                        oximetria.getPulse(),
                        oximetria.getPi(),
                        oximetria.getIsUrgencia(),
                        oximetria.getCalendar().getTimeInMillis()
                ));
    }

    public void traerRegistrosMedicos(final EntidadesFireBase.Paciente id) {
        mDatabase.child(context.getString(R.string.oximetria)).child(id.getIdentificacion()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    mensajes.mostrarDialogo();

                    List<EntidadesFireBase.OximetriaFB> listaOximetrias = new ArrayList<EntidadesFireBase.OximetriaFB>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        listaOximetrias.add(data.getValue(EntidadesFireBase.OximetriaFB.class));
                    }
                    if (listaOximetrias.size() <= 0) {
                        mensajes.cancelarDialogos();
                        mensajes.Toast(context.getString(R.string.no_hay_registros_paciente));
                        return;
                    }
                    mensajes.cancelarDialogos();
                    actividadesManager.irPacienteId(id,listaOximetrias);
                } else {
                    mensajes.Toast(context.getString(R.string.no_hay_registros_paciente));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mensajes.Toast(context.getString(R.string.no_hay_registros_paciente));
            }
        });
    }

    public void subirMedico() {

        mDatabase.child(context.getString(R.string.Centro_Medico)).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 0;
                        Iterator<DataSnapshot> iterable = dataSnapshot.getChildren().iterator();
                        while (iterable.hasNext()) {
                            // Log.println(Log.ASSERT, "FBSQL", iterable.next().getValue().toString());
                            EntidadesFireBase.CentroMedico centroMedico = (iterable.next().getValue(EntidadesFireBase.CentroMedico.class));
                            Log.println(Log.ASSERT, "FBSQL", centroMedico.getNombre());
                            if (centroMedico.getId() == preferencias.getIdCentroMedico()) {
                                mDatabase.child(context.getString(R.string.Centro_Medico))
                                        .child(i + "").child(context.getString(R.string.medico))
                                        .child(preferencias.getIdentificacionPaciente())
                                        .setValue(new EntidadesFireBase.Medico(
                                                preferencias.getIdentificacionPaciente(),
                                                preferencias.getNombrePaciente(),
                                                preferencias.getApellidosPaciente(),
                                                FirebaseInstanceId.getInstance().getToken(),
                                                preferencias.getIdCentroMedico()));
                                preferencias.setListaCentroMedicoId(i);
                                preferencias.setDebeCrear(false);
                                mensajes.mostrarDialogo();
                                mensajes.getActividadesManager().irMedicoMain();
                                return;

                            }
                            i++;
                            Log.println(Log.ASSERT, "FIREBASE", "REGISTRO MEDICO");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        preferencias.setDebeCrear(true);
                        mensajes.finshApp(context.getString(R.string.error), context.getString(R.string.sin_conexion));
                    }
                });
    }

    private void subirPacientes() {
        mDatabase.child(context.getString(R.string.paciente)).push().setValue("paciente");
    }

    public void subirPaciente() {
        mDatabase.child(context.getString(R.string.Centro_Medico)).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            int i = 0;
                            Iterator<DataSnapshot> iterable = dataSnapshot.getChildren().iterator();
                            while (iterable.hasNext()) {
                                EntidadesFireBase.CentroMedico centroMedico = (iterable.next().getValue(EntidadesFireBase.CentroMedico.class));
                                Log.println(Log.ASSERT, "FBSQL", centroMedico.getNombre());
                                if (centroMedico.getId() == preferencias.getIdCentroMedico()) {

                                    mDatabase.child(context.getString(R.string.Centro_Medico))
                                            .child(i + "").child(context.getString(R.string.paciente)).
                                            child(preferencias.getIdentificacionPaciente()).
                                            setValue(
                                                    new EntidadesFireBase.Paciente(preferencias.getIdentificacionPaciente(),
                                                            preferencias.getNombrePaciente(),
                                                            preferencias.getApellidosPaciente(),
                                                            preferencias.getSPO2(),
                                                            preferencias.getPulsoBajo(),
                                                            preferencias.getPulsoAlto(),
                                                            preferencias.getNumero1(),
                                                            preferencias.getIdCentroMedico(),
                                                            FirebaseInstanceId.getInstance().getToken(),
                                                            preferencias.getDescripccionPaciente()
                                                    ));

                                    preferencias.setDebeCrear(false);
                                    preferencias.setListaCentroMedicoId(i);
                                    mensajes.mostrarDialogo();
                                    mensajes.getActividadesManager().irPacienteMain();


                                    Log.println(Log.ASSERT, "CREANDO PACIENTE", i + "CREANDO PACIENTE");
                                    return;
                                }
                                i++;
                            }
                        } catch (Exception ex) {
                            Log.println(Log.ASSERT, "FIREBASE", ex.toString());
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        preferencias.setDebeCrear(true);
                        mensajes.finshApp(context.getString(R.string.error), context.getString(R.string.sin_conexion));
                    }
                });
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
                            listaFirebase.add(iterable.next().getValue(EntidadesFireBase.CentroMedico.class));
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

    public void cargarPacientes(final MainActivityMedico mainActivityMedico) {
        mDatabase.child(context.getString(R.string.Centro_Medico)).child(preferencias.getListaCentroMedicoId() + "")
                .child(context.getString(R.string.paciente)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.println(Log.ASSERT, "Los datos son", dataSnapshot.toString());
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    EntidadesFireBase.Paciente paciente = data.getValue(EntidadesFireBase.Paciente.class);
                    mainActivityMedico.getPacienteList().add(paciente);
                }
                Log.println(Log.ASSERT, "FB", mainActivityMedico.getPacienteList().size() + "");
                ListaMedico listaMedico = new ListaMedico(mainActivityMedico.getPacienteList(), mainActivityMedico);
                mainActivityMedico.setAdapter(listaMedico);

                Log.println(Log.ASSERT, "FB", mainActivityMedico.getListaPacientes().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.println(Log.ASSERT, "Error", databaseError.toString());
            }
        });

    }

    public List<EntidadesFireBase.CentroMedico> getListaFirebase() {
        return listaFirebase;
    }
}
