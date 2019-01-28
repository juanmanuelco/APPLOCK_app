package apps.savvisingh.applocker.Prefrence;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apps.savvisingh.applocker.Utils.CONSTANTES;


public class SharedPreference {

    public static final String LOCKED_APP = "locked_app";
    public SharedPreference() {
        super();
    }

    public void guardarBloqueadas(Context context, List<String> lockedApp) {

        /**Almacena el listaado de aplicaciones bloqueadas en un sharedpreferences*/
        SharedPreferences herramientas;
        SharedPreferences.Editor editor;
        herramientas = context.getSharedPreferences(CONSTANTES.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = herramientas.edit();
        Gson gson = new Gson();

        /**transforma el contenido obtenido en formato JSON*/
        String jsonLockedApp = gson.toJson(lockedApp);
        editor.putString(LOCKED_APP, jsonLockedApp);
        editor.commit();
    }

    public void AnadirBloqueo(Context context, String app) {
        /**Añade aplicaciones al listado de bloqueadas*/
        List<String> AppBloqueada = getBloqueados(context);
        if (AppBloqueada == null) AppBloqueada = new ArrayList<String>();
        AppBloqueada.add(app);
        guardarBloqueadas(context, AppBloqueada);
    }

    public void QuitarBloqueo(Context context, String app) {
        /**Quita las aplicaciones de a lista de bloqueadas*/
        ArrayList<String> locked = getBloqueados(context);
        if (locked != null) {
            locked.clear();

            guardarBloqueadas(context, locked);
        }
    }

    public ArrayList<String> getBloqueados(Context context) {
        SharedPreferences herramientas;
        List<String> bloqueadas;
        herramientas = context.getSharedPreferences(CONSTANTES.MyPREFERENCES, Context.MODE_PRIVATE);
        if (herramientas.contains(LOCKED_APP)) {
            /**Obtiene todas las aplicaciones bloqueadas*/
            String jsonLocked = herramientas.getString(LOCKED_APP, null);
            Log.i("ItemBloq", jsonLocked);
            Gson gson = new Gson();
            String[] itemsBloqueados = gson.fromJson(jsonLocked, String[].class);

            /**Añade las apicaciones al listado*/
            bloqueadas = Arrays.asList(itemsBloqueados);

            bloqueadas = new ArrayList<String>(bloqueadas);
        } else
            return null;
        return (ArrayList<String>) bloqueadas;
    }

    public String getPassword(Context context) {
        SharedPreferences pRef;
        /**Busca obtener la contrasea de desboqueo fuera de conexión a internet*/
        pRef = context.getSharedPreferences(CONSTANTES.MyPREFERENCES, Context.MODE_PRIVATE);
        if (pRef.contains(CONSTANTES.PASSWORD)) return pRef.getString(CONSTANTES.PASSWORD, "");
        return "";
    }
    public String getSesion(Context context) {
        SharedPreferences sesionref;
        /**Busca la sesión de usuario para el equipo administrado*/
        sesionref = context.getSharedPreferences(CONSTANTES.MyPREFERENCES, Context.MODE_PRIVATE);
        return sesionref.getString("sesion", "nulo");
    }

    public void setSession(Context context){
        SharedPreferences sesionref;
        /**Vacia la sesión del dispositivo administrado*/
        sesionref = context.getSharedPreferences(CONSTANTES.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sesionref.edit();
        editor.putString("sesion", "nulo");
        editor.commit();
    }
}
