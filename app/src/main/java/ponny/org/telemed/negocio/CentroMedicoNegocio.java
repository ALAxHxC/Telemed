package ponny.org.telemed.negocio;

import android.util.Log;

import java.util.List;

/**
 * Created by Daniel on 31/01/2017.
 */

public class CentroMedicoNegocio {
    private int id;
    private List<FireBaseEntitys.CentroMedico> centroMedicos;

    public CentroMedicoNegocio(List<FireBaseEntitys.CentroMedico> centroMedicos) {
        this.centroMedicos = centroMedicos;
    }

    public int buscarCentroMedicoMedico(String centroMedico, String clave) {
        for (FireBaseEntitys.CentroMedico cn : centroMedicos) {

            if (cn.getNombre().equalsIgnoreCase(centroMedico) && cn.getClaveMedico().equalsIgnoreCase(clave)) {
                return cn.getId();
            }

        }
        return 0;
    }

    public int buscarCentroMedicoPaciente(String centroMedico, String clave) {
        for (FireBaseEntitys.CentroMedico cn : centroMedicos) {

            if (cn.getNombre().equalsIgnoreCase(centroMedico) && cn.getClavePaciente().equalsIgnoreCase(clave)) {
                Log.println(Log.ASSERT, "FBSQL", cn.getNombre() + cn.getId());
                return cn.getId();
            }

        }
        return 0;
    }
}
