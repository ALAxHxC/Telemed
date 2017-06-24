package ponny.org.telemed.utilidades;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ponny.org.telemed.negocio.EntidadesFireBase;
import ponny.org.telemed.negocio.Oximetria;

/**
 * Created by Daniel on 23/01/2017.
 */

public class Utilidades {
  public static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
  public static final SimpleDateFormat sdfCompleta = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static List<Oximetria> convertirFBtoLocal(List<EntidadesFireBase.OximetriaFB> listaOximetrias)
  {
    int i=1;
    List<Oximetria> listaLocal=new ArrayList<>();
    for(EntidadesFireBase.OximetriaFB oximetriaFB:listaOximetrias)
    {
      Calendar calendar=Calendar.getInstance();
      calendar.setTimeInMillis(oximetriaFB.getTime());
      listaLocal.add(new Oximetria(
              i,oximetriaFB.getSpo2(),oximetriaFB.getPulse(),
              oximetriaFB.getPi(),oximetriaFB.getIsUrgencia(),calendar)
      );
      i++;
    }
    return listaLocal;
  }
}
