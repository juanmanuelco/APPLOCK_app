package apps.savvisingh.applocker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.takwolf.android.lock9.Lock9View;

import java.util.HashMap;
import java.util.Map;

import apps.savvisingh.applocker.NEGOCIO.Datos;
import apps.savvisingh.applocker.NEGOCIO.Servidor;
import apps.savvisingh.applocker.NEGOCIO.recorrido;
import apps.savvisingh.applocker.NEGOCIO.singletonDatos;

public class LoginActivity extends AppCompatActivity {
    Lock9View patronPass;
    EditText ET_mail_padre;
    String serverURL= Servidor.servicio("/usuario/login");
    ProgressDialog progressDialog;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        setContentView(R.layout.activity_password);
        ET_mail_padre= (EditText) findViewById(R.id.ET_mail_padre);
        patronPass = (Lock9View) findViewById(R.id.lock_9_view);
        patronPass.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                iniciarSesion(ET_mail_padre, password);
            }
        });
    }
    public void iniciarSesion(final EditText mail, final String pass){
        EditText[] campos=new EditText[]{mail};
        recorrido recor=new recorrido(campos);
        if(recor.Recorrer(campos)){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Comprobando...");
            progressDialog.show();
            StringRequest stringRequest=new StringRequest(Request.Method.POST, serverURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            if (progressDialog != null) progressDialog.dismiss();
                            if((result.toString()).equals("E1")){
                                mostrarMensaje("Error", "Error de red");
                                return;
                            }
                            if((result.toString()).equals("E4")){
                                mostrarMensaje("Error", "Datos incorrectos o cuenta sin confirmar");
                                return;
                            }

                            Datos.nombre=mail.getText().toString();

                            Intent intent = new Intent(LoginActivity.this, HijosActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mostrarMensaje("Error", "El servidor no responde");
                    error.printStackTrace();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map <String,String> params =new HashMap<String, String >();
                    params.put("mail",mail.getText().toString());
                    params.put("pass", pass);
                    return params;
                }
            };
            singletonDatos.getInstancia(this).addToRequest(stringRequest);

        }else {
            mostrarMensaje("ERROR", "Se necesita ingresar un email");
        }
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
    public void Olvido_pass(View v){
        /**abre un cuadro de dialogo para ingresar el codigo de usuario*/
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogo_cambio = layoutInflater.inflate(R.layout.recup_pass, null);
        final AlertDialog alertD = new AlertDialog.Builder(this).create();
        final EditText ET_usuario = (EditText) dialogo_cambio.findViewById(R.id.ET_usuario);
        Button BTN_ACEPTAR =(Button) dialogo_cambio.findViewById(R.id.BTN_GUA_ACEP);
        BTN_ACEPTAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperar(ET_usuario.getText().toString());
            }
        });
        Button BTN_CANCELAR= (Button) dialogo_cambio.findViewById(R.id.BTN_GUA_CANC);
        BTN_CANCELAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertD.dismiss();
            } //7936lv6s
        });
        alertD.setView(dialogo_cambio);
        alertD.show();
    }
    public void Registro(View v){
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }
    public void recuperar(final String mail){
        String cadena_recuperacion = Servidor.servicio("/usuario/recuperacion");
        StringRequest stringRequest=new StringRequest(Request.Method.POST, cadena_recuperacion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if(result.equals("E1")){
                            Toast.makeText(LoginActivity.this, "No existe esa cuenta", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(result.equals("E2")){
                            Toast.makeText(LoginActivity.this, "Fallo al intentar recuperar su cuenta", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(LoginActivity.this, "Revise su mail para recuperar su cuenta", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mostrarMensaje("Error", "El servidor no responde");
                error.printStackTrace();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("mail",mail);
                return params;
            }
        };
        singletonDatos.getInstancia(this).addToRequest(stringRequest);
    }
}
