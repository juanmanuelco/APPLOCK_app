package apps.savvisingh.applocker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

import apps.savvisingh.applocker.Adapter.AdaptadorApps;
import apps.savvisingh.applocker.NEGOCIO.Datos;
import apps.savvisingh.applocker.NEGOCIO.Servidor;
import apps.savvisingh.applocker.NEGOCIO.singletonDatos;

public class AppsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private AdaptadorApps adaptador;
    ProgressDialog progressDialog;
    Context contexto;
    public ArrayList<String[]> lista_hijos = new ArrayList<>();
    String serverURL= Servidor.servicio("/usuario/apps_usuario");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.RV_apps);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando...");
        StringRequest stringRequest=new StringRequest(Request.Method.POST, serverURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        JsonParser parser = new JsonParser();
                        JsonElement elementObject = parser.parse(result);
                        JsonArray applicaciones=elementObject.getAsJsonArray();
                        for (int i=0; i < applicaciones.size(); i++){
                            String nombre=applicaciones.get(i).getAsJsonObject().get("nombre").toString();
                            String paquete= applicaciones.get(i).getAsJsonObject().get("paquete").toString();
                            String estado = applicaciones.get(i).getAsJsonObject().get("estado").toString();
                            nombre=nombre.substring(1,nombre.length()-1);
                            paquete=paquete.substring(1, paquete.length()-1);
                            lista_hijos.add(new String[]{ nombre, paquete , estado});
                        }
                        adaptador.notifyDataSetChanged();
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
                params.put("usuario",Datos.usuario);
                return params;
            }
        };
        singletonDatos.getInstancia(getApplicationContext()).addToRequest(stringRequest);

        adaptador= new AdaptadorApps(lista_hijos, getApplicationContext());
        mRecyclerView.setAdapter(adaptador);
    }


}
