package apps.savvisingh.applocker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import org.w3c.dom.Text;

import java.security.SecureRandom;
import java.math.BigInteger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import apps.savvisingh.applocker.Adapter.AdaptadorHijos;
import apps.savvisingh.applocker.NEGOCIO.Datos;
import apps.savvisingh.applocker.NEGOCIO.Servidor;
import apps.savvisingh.applocker.NEGOCIO.singletonDatos;

public class HijosActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private AdaptadorHijos adaptador;
    ProgressDialog progressDialog;
    Context contexto;
    public ArrayList<String[]> lista_hijos = new ArrayList<>();
    String serverURL= Servidor.servicio("/usuario/hijos");
    String todosHijos= Servidor.servicio("/usuario/hijos_todos");
    String padre;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hijos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contexto= this;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando...");
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        padre= Datos.nombre;

        StringRequest stringRequest=new StringRequest(Request.Method.POST, todosHijos,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        JsonParser parser = new JsonParser();
                        JsonElement elementObject = parser.parse(result);
                        JsonArray hijos_obtenidos=elementObject.getAsJsonArray();
                        for (int i=0; i < hijos_obtenidos.size(); i++){
                            String nombre=hijos_obtenidos.get(i).getAsJsonObject().get("codigo").toString();
                            String estado= hijos_obtenidos.get(i).getAsJsonObject().get("estado").toString();
                            nombre=nombre.substring(1,nombre.length()-1);
                            estado=estado.substring(1, estado.length()-1);
                            lista_hijos.add(new String[]{ nombre, estado });
                        }
                        adaptador.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                final AlertDialog.Builder BuilDialogo=new AlertDialog.Builder(contexto)
                        .setTitle("Error")
                        .setMessage("No dispone de conexión a internet")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                final AlertDialog dialogo= BuilDialogo.create();
                dialogo.show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("peticion","vamos");
                params.put("padre", padre);
                return params;
            }
        };
        singletonDatos.getInstancia(getApplicationContext()).addToRequest(stringRequest);

        adaptador= new AdaptadorHijos(lista_hijos, getApplicationContext());

        //Cuando se da click a un elemento del recyclerview
        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder BuilDialogo=new AlertDialog.Builder(contexto)
                        .setTitle("Atencion")
                        .setMessage(R.string.INFO_USER)
                        .setPositiveButton("Administrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextView texto=(TextView) view.findViewById(R.id.TV_codigo);
                                Datos.usuario= (texto.getText()).toString();
                                Intent listados= new Intent(HijosActivity.this, AppsActivity.class);
                                startActivity(listados);
                            }
                        })
                        .setNegativeButton("Eliminar usuario", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextView texto=(TextView) view.findViewById(R.id.TV_codigo);
                                int posicion= mRecyclerView.getChildAdapterPosition(view);
                                String hijo= (texto.getText()).toString();

                                eliminarUsuario(hijo,posicion );
                            }
                        });
                final AlertDialog dialogo= BuilDialogo.create();
                dialogo.show();
            }
        });
        mRecyclerView.setAdapter(adaptador);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lista_hijos.size()>=5) {
                    mostrarMensaje("Advertencia", "Solo se puede administrar hasta 5 dispositivos");
                    return;
                }
                final String data= aleatorios();
                progressDialog.show();
                StringRequest stringRequest=new StringRequest(Request.Method.POST, serverURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {
                                if (progressDialog != null) progressDialog.dismiss();
                                if((result.toString()).equals("Error 1")){
                                    mostrarMensaje("Error", "Error de red");
                                    return;
                                }
                                lista_hijos.add(new String[]{data, "Sin usar"});
                                adaptador.notifyDataSetChanged();
                                final AlertDialog.Builder BuilDialogo1=new AlertDialog.Builder(contexto)
                                        .setTitle("Listo")
                                        .setMessage("Ingrese el código "+ data+ " en el dispositivo a administrar como niño")
                                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                final AlertDialog dialogo1= BuilDialogo1.create();
                                dialogo1.show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        final AlertDialog.Builder BuilDialogo=new AlertDialog.Builder(contexto)
                                .setTitle("Error")
                                .setMessage("No dispone de conexión a internet")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        final AlertDialog dialogo= BuilDialogo.create();
                        dialogo.show();
                    }
                }
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String,String> params =new HashMap<String, String >();
                        params.put("nuevo_usuario",data);
                        params.put("padre", padre);
                        return params;
                    }
                };
                singletonDatos.getInstancia(getApplicationContext()).addToRequest(stringRequest);
            }
        });
    }

    public void eliminarUsuario(final String usuario, final int elem){
        String cadena_eliminacion=Servidor.servicio("/usuario/eiminar_usuario");
        StringRequest stringRequest=new StringRequest(Request.Method.POST, cadena_eliminacion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Toast.makeText(contexto, "Hijo eliminado con éxito", Toast.LENGTH_SHORT).show();
                        lista_hijos.remove(elem);
                        adaptador.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(contexto, "no hay conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("hijo", usuario);
                return params;
            }
        };
        singletonDatos.getInstancia(getApplicationContext()).addToRequest(stringRequest);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public static String aleatorios() {
        String mili =  System.currentTimeMillis()+"";
        String mili_text=mili.substring(mili.length()-5,mili.length()-1);
        SecureRandom random = new SecureRandom();
        String text = new BigInteger(130, random).toString(32);
        return mili_text+text.substring(1,5);
    }
    public void mostrarMensaje(String titulo,String mensaje){
        if (progressDialog != null)
            progressDialog.dismiss();
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }


}
