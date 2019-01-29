package apps.savvisingh.applocker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import apps.savvisingh.applocker.Data.AppInfo;
import apps.savvisingh.applocker.NEGOCIO.Servidor;
import apps.savvisingh.applocker.NEGOCIO.singletonDatos;
import apps.savvisingh.applocker.Utils.CONSTANTES;

import static apps.savvisingh.applocker.NEGOCIO.Datos.usuario;
import static apps.savvisingh.applocker.NEGOCIO.Funcionalidad.liberarApps;

public class Actualizaciones extends Service {

    public Context context=this;

    String serverURL= Servidor.servicio("/usuario/todas_bloqueadas");
    SharedPreferences sharedPreferences;
    String usuario;
    SharedPreferences.Editor editor;
    ArrayList<String> listadoApps;

    public Actualizaciones() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(CONSTANTES.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        usuario=sharedPreferences.getString("sesion", "nulo");
        listadoApps= new ArrayList<>();
        Timer timer;
        timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                bloquearApps();
            }
        };
        // Empezamos dentro de 10ms y luego lanzamos la tarea cada 1000ms
        timer.schedule(task, 10, 100000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void bloquearApps(){
        listadoApps= new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, serverURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        try{
                            JsonParser parser = new JsonParser();
                            JsonElement elementObject = parser.parse(result);
                            JsonArray apps_bloqueadas=elementObject.getAsJsonArray();
                            for (int i=0; i < apps_bloqueadas.size(); i++){
                                String nombre=apps_bloqueadas.get(i).getAsJsonObject().get("nombre").toString();
                                String paquete= apps_bloqueadas.get(i).getAsJsonObject().get("paquete").toString();
                                String version= apps_bloqueadas.get(i).getAsJsonObject().get("version").toString();
                                String cod_version = apps_bloqueadas.get(i).getAsJsonObject().get("cod_version").toString();
                                nombre=nombre.substring(1,nombre.length()-1);
                                paquete=paquete.substring(1, paquete.length()-1);
                                version=version.substring(1, version.length()-1);
                                cod_version=cod_version.substring(1, cod_version.length()-1);
                                listadoApps.add(paquete);
                            }
                            liberarApps(context, listadoApps);
                        }catch (Exception e){
                            liberarApps(context, listadoApps);
                            stopService(new Intent(context, Actualizaciones.class));//4445f35g
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("usuario",usuario);
                return params;
            }
        };
        singletonDatos.getInstancia(getApplicationContext()).addToRequest(stringRequest);
    }
}
