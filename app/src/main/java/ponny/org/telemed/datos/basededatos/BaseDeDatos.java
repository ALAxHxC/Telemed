package ponny.org.telemed.datos.basededatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ponny.org.telemed.datos.basededatos.ModeloBaseDatos.dataBaseName;
import static ponny.org.telemed.datos.basededatos.ModeloBaseDatos.version_basededatos;

/**
 * Created by Daniel on 22/01/2017.
 */

public class BaseDeDatos extends SQLiteOpenHelper {

    public BaseDeDatos(Context context,  SQLiteDatabase.CursorFactory factory) {
        super(context,dataBaseName, factory, version_basededatos);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ModeloBaseDatos.table_oximetria_create);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(ModeloBaseDatos.table_oximetria_delete);
        sqLiteDatabase.execSQL(ModeloBaseDatos.table_oximetria_create);
    }
}
