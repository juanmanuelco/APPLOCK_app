package apps.savvisingh.applocker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import apps.savvisingh.applocker.NEGOCIO.Funcionalidad;
import apps.savvisingh.applocker.NEGOCIO.Servidor;
import apps.savvisingh.applocker.NEGOCIO.singletonDatos;
import apps.savvisingh.applocker.Services.ServCheqApp;
import apps.savvisingh.applocker.Utils.CONSTANTES;

public class InicioActivity extends AppCompatActivity {
    String serverURL= Servidor.servicio("/usuario/integrar");
    String serverIntegracion= Servidor.servicio("/usuario/hijo_uso");
    ProgressDialog progressDialog;
    Context context;
    SharedPreferences sharedPref;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        context=this;
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        sharedPreferences = getSharedPreferences(CONSTANTES.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /***Si se elige como adulto se procede a abrir el login */
    public void soyAdulto(View v){
        startService(new Intent(context, Actualizaciones.class));
        Intent intent =  new Intent(this, SplashActivity.class);
        startActivity(intent);
    }

    /**Si se elige como niño intenta registrar usuaario*/
    public void soyNino(View v){
        startService(new Intent(context, Actualizaciones.class));
        String usuario_sesion= sharedPref.getString("sesion", "nulo");

        /**Si el usuario ya se encunetra administrado no permite su cambio*/
        startService(new Intent(InicioActivity.this, ServCheqApp.class));
        if(usuario_sesion.equals("nulo")) permitirRegistro();
        else comprobarRegistro(usuario_sesion);

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
    public void permitirRegistro(){

        /**abre un cuadro de dialogo para ingresar el codigo de usuario*/
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogo_cambio = layoutInflater.inflate(R.layout.ingresar_usuario, null);
        final AlertDialog alertD = new AlertDialog.Builder(context).create();
        final EditText ET_nombreUsuario = (EditText) dialogo_cambio.findViewById(R.id.ET_usuario);
        Button BTN_ACEPTAR =(Button) dialogo_cambio.findViewById(R.id.BTN_GUA_ACEP);
        BTN_ACEPTAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario(ET_nombreUsuario.getText().toString(), alertD);
            }
        });
        Button BTN_CANCELAR= (Button) dialogo_cambio.findViewById(R.id.BTN_GUA_CANC);
        BTN_CANCELAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertD.dismiss();
            }
        });
        alertD.setView(dialogo_cambio);
        alertD.show();
    }
    public void registrarUsuario(final String usuario_txt, final AlertDialog alertD){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Comprobando...");
        progressDialog.show();

        /**En caso de que no este administrado se puede guardar en la base de datos su registro*/
        StringRequest stringRequest=new StringRequest(Request.Method.POST, serverURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (progressDialog != null) progressDialog.dismiss();
                        if((result.toString()).equals("E1")){
                            mostrarMensaje("Error", "Este código no existe");
                            return;
                        }
                        if((result.toString()).equals("E2")){
                            mostrarMensaje("Error", "Este código ya está siendo utilizado por otro dispositivo");
                            return;
                        }

                        /**Guarda el codigo del dispositivo*/
                        GuardarPreferencia("sesion", usuario_txt);
                        GuardarPreferencia("pass", result.substring(1, result.length()));

                        editor.putString(CONSTANTES.PASSWORD, result.substring(1, result.length()));
                        editor.commit();

                        editor.putString("sesion", usuario_txt);
                        editor.commit();




                        AlertDialog.Builder alertDialogBuilder;
                        alertDialogBuilder=new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Listo");
                        alertDialogBuilder.setMessage("Equipo registrado");
                        alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertD.dismiss();
                            }
                        });
                        AlertDialog alertDialog=alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mostrarMensaje("Error", "No hay conexion con el servidor");
                error.printStackTrace();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("usuario", usuario_txt);
                return params;
            }
        };
        singletonDatos.getInstancia(this).addToRequest(stringRequest);

        Funcionalidad.obtencionApps(context, usuario_txt);
    }



    public void comprobarRegistro(final String usuario){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Comprobando...");
        progressDialog.show();

        /**Comprueba que el codigo de usuario siga existiendo*/
        StringRequest stringRequest=new StringRequest(Request.Method.POST, serverIntegracion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (progressDialog != null) progressDialog.dismiss();
                        if(result.equals("existe")) mostrarMensaje("Información", "Este dispositivo ya se encuentra administrado con el pseudonimo de "+ usuario);
                        else {
                            GuardarPreferencia("sesion", "nulo");
                            editor.putString("sesion", "nulo");
                            editor.commit();
                            permitirRegistro();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mostrarMensaje("Error", "No hay conexion con el servidor");
                error.printStackTrace();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("usuario", usuario);
                return params;
            }
        };
        singletonDatos.getInstancia(this).addToRequest(stringRequest);
    }

    public void GuardarPreferencia(String nick ,String valor){
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(nick, valor);
        editor.commit();
    }

}
