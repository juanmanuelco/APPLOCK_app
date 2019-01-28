package apps.savvisingh.applocker.NEGOCIO;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apps.savvisingh.applocker.Data.AppInfo;
import apps.savvisingh.applocker.Prefrence.SharedPreference;

import static apps.savvisingh.applocker.Adapter.ObtenerListaAppsAsync.obtenerListaInstalados;

/**
 * Created by juanm on 27/01/2019.
 */

public class Funcionalidad {
    public static String  subirApps= Servidor.servicio("/usuario/subir_apps");
    static SharedPreference sharedPreference;
    public static void obtencionApps(final Context c, final String usuario){
        final AsyncTask tarea= new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                List<AppInfo> listado = obtenerListaInstalados(c);
                String listaEnviar="";
                /**Obtiene las aplicaciones y las guarda enuna lista de objetos*/
                for (AppInfo item: listado) {
                    listaEnviar+=item.getNombre()+",";
                    listaEnviar+=  item.getNombrePaquete()+",";
                    listaEnviar+= item.getNombreVersion()+",";
                    listaEnviar+=item.getVersionCode()+";";
                }
                return listaEnviar;
            }

            @Override
            protected void onPostExecute(Object o) {
                envioApps(o.toString(), c, usuario);
            }
        };
        tarea.execute();
    }


    public static void liberarApps(final Context context, final ArrayList<String> aBloquear){
        sharedPreference = new SharedPreference();
        final AsyncTask tarea = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                List<AppInfo> listado = obtenerListaInstalados(context);
                ArrayList<String> lista = new ArrayList<>();
                for(AppInfo item:listado){
                    //
                    sharedPreference.QuitarBloqueo(context, item.getNombrePaquete());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                for(String item : aBloquear){
                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show();
                    sharedPreference.AnadirBloqueo(context, item);
                }
            }
        };
        tarea.execute();
    }


    public static void envioApps(final String data, final Context c, final String usuario){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, subirApps,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(c, "No hay conexión a red, las aplicaciones serán obtenidas mas luego", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("apps", data);
                params.put("usuario", usuario);
                return params;
            }
        };
        singletonDatos.getInstancia(c).addToRequest(stringRequest);
    }

}
