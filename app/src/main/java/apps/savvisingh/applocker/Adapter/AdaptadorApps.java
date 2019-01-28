package apps.savvisingh.applocker.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import apps.savvisingh.applocker.NEGOCIO.Datos;
import apps.savvisingh.applocker.NEGOCIO.Servidor;
import apps.savvisingh.applocker.NEGOCIO.singletonDatos;
import apps.savvisingh.applocker.R;

/**
 * Created by juanm on 27/01/2019.
 */

public class AdaptadorApps extends RecyclerView.Adapter<AdaptadorApps.ViewHolderDatos>{

    private View.OnClickListener listener;
    ArrayList<String[]> lista_apps;
    Context context;
    View view;
    String serverURL= Servidor.servicio("/usuario/bloquear_app");
    ProgressDialog progressDialog;


    public AdaptadorApps(ArrayList<String[]> hijos, Context c) {

        /**Obtiene la informaciín desde fuera*/
        lista_apps =hijos;
        context=c;
    }


    @Override
    public AdaptadorApps.ViewHolderDatos onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Registrando...");
        return new AdaptadorApps.ViewHolderDatos(view);
    }


    @Override
    public void onBindViewHolder(final AdaptadorApps.ViewHolderDatos holder, int position) {

        /**Llena los elementos en el RecyclerView*/
        final String[] appInfo = lista_apps.get(position);
        holder.NombreApp.setText(appInfo[0]);
        holder.PaqueteApp.setText(appInfo[1]);

        Boolean estado= Boolean.parseBoolean(appInfo[2]);
        if(!estado) holder.switchView.setChecked(true);
        else holder.switchView.setChecked(false);


        holder.switchView.setOnCheckedChangeListener(null);
        holder.cardView.setOnClickListener(null);

        holder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Boolean chequeo=true;
                if(isChecked){
                    chequeo=false;
                }
                cambiarEstado(chequeo, appInfo[1], Datos.usuario);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**Permite dar click en los switchs*/
                holder.switchView.performClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        return lista_apps.size();
    }

    public void cambiarEstado(final Boolean estado, final String paquete , final String usuario){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, serverURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Toast.makeText(context, "Listo", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<String, String >();
                params.put("usuario",usuario);
                params.put("estado",estado+"");
                params.put("paquete",paquete);
                return params;
            }
        };
        singletonDatos.getInstancia(context).addToRequest(stringRequest);
    }


    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        /**Instancia los elementos del RecyclerView*/
        public TextView NombreApp, PaqueteApp;
        public CardView cardView;
        public ImageView icono;
        public Switch switchView;

        public ViewHolderDatos(@NonNull View v) {
            super(v);
            NombreApp = (TextView) v.findViewById(R.id.applicationName);
            PaqueteApp  = (TextView)v.findViewById(R.id.paquete) ;
            cardView = (CardView) v.findViewById(R.id.card_view);
            icono = (ImageView) v.findViewById(R.id.icon);
            switchView = (Switch) v.findViewById(R.id.switchView);
        }
    }
}
