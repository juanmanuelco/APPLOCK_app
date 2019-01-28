package apps.savvisingh.applocker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.takwolf.android.lock9.Lock9View;

import java.util.HashMap;
import java.util.Map;

import apps.savvisingh.applocker.NEGOCIO.Servidor;
import apps.savvisingh.applocker.NEGOCIO.recorrido;
import apps.savvisingh.applocker.NEGOCIO.singletonDatos;


public class RegistroActivity extends AppCompatActivity {
    Lock9View lock9View;
    TextView textView;
    boolean primerIntento = true;
    boolean segundoIntento = false;
    String patronIngresado;
    Context context;
    Button BTN_confirmar;
    EditText ET_mail_padre;
    ProgressDialog progressDialog;
    String serverURL= Servidor.servicio("/usuario/registro"); //4255h8ni

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = getApplicationContext();
        setContentView(R.layout.activity_password_set);
        lock9View = (Lock9View) findViewById(R.id.lock_9_view);
        textView = (TextView) findViewById(R.id.textView);
        BTN_confirmar = (Button)findViewById(R.id.BTN_confirmar);
        BTN_confirmar.setEnabled(false);
        ET_mail_padre= (EditText)findViewById(R.id.ET_mail_padre);


        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String contrasena) {
                if (primerIntento) {
                    patronIngresado = contrasena;
                    primerIntento = false;
                    segundoIntento = true;
                    textView.setText(R.string.REPASS);
                    return;
                }
                if (segundoIntento) {
                    if (!patronIngresado.matches(contrasena)) {
                        Toast.makeText(getApplicationContext(), R.string.NOMATCH, Toast.LENGTH_SHORT).show();
                        primerIntento = true;
                        segundoIntento = false;
                        textView.setText(R.string.DIBUJAR);
                        return;
                    }
                    EditText[] campos=new EditText[]{ET_mail_padre};
                    recorrido recor=new recorrido(campos);
                    if(!recor.Recorrer(campos)){
                        textView.setText(R.string.OB_MAIL);
                        return;
                    }
                    textView.setText(R.string.PAT_ACEPT);
                    BTN_confirmar.setEnabled(true);
                    BTN_confirmar.setBackgroundColor(context.getResources().getColor(R.color.Activo));
                }
            }
        });
    }
    public void Cancelar(View v){
        finish();
    }
    public void Confirmar(View v){
        if(isEmailValid(ET_mail_padre.getText())){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Comprobando...");
            progressDialog.show();

            StringRequest stringRequest=new StringRequest(Request.Method.POST, serverURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            if (progressDialog != null)
                                progressDialog.dismiss();
                            if((result.toString()).equals("E0")){
                                mostrarMensaje("Error", "No se pudo completar la transacción");
                                return;
                            }
                            if((result.toString()).equals("E1")){
                                mostrarMensaje("Error", "Error de red");
                                return;
                            }
                            if((result.toString()).equals("E3")){
                                mostrarMensaje("Error", "Ya existe esta cuenta");
                                return;
                            }
                            mostrarMensaje("Listo", getString(R.string.POR_CONF));
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mostrarMensaje("Advertencia", getString(R.string.MAIL_SEND));
                    error.printStackTrace();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map <String,String> params =new HashMap<String, String >();
                    params.put("mail",ET_mail_padre.getText().toString());
                    params.put("pass", patronIngresado);
                    return params;
                }
            };
            singletonDatos.getInstancia(this).addToRequest(stringRequest);
        }else{
            mostrarMensaje("Error", "Este no es un email");
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

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}
