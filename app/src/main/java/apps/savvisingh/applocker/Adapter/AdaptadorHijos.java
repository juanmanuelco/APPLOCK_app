package apps.savvisingh.applocker.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import apps.savvisingh.applocker.R;

/**
 * Created by juanm on 25/01/2019.
 */

public class AdaptadorHijos extends RecyclerView.Adapter<AdaptadorHijos.ViewHolderDatos> implements View.OnClickListener{

    private View.OnClickListener listener;
    ArrayList<String[]> lista_hijos;
    Context context;
    View view;


    public AdaptadorHijos(ArrayList<String[]> hijos, Context c) {

        /**Obtiene la informaciín desde fuera*/
        lista_hijos=hijos;
        context=c;
    }


    @Override
    public AdaptadorHijos.ViewHolderDatos onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_hijos, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        view.setOnClickListener(this);
        return new ViewHolderDatos(view);
    }


    @Override
    public void onBindViewHolder(AdaptadorHijos.ViewHolderDatos holder, int position) {

        /**Llena los elementos en el RecyclerView*/
        String codigo= lista_hijos.get(position)[0];
        holder.TV_codigo.setText(codigo);
        holder.TV_estado.setText(lista_hijos.get(position)[1]);
    }

    @Override
    public int getItemCount() {
        return lista_hijos.size();
    }

    public void setOnClickListener(View.OnClickListener listen){
        this.listener=listen;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null) listener.onClick(view);
    }
    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        /**Instancia los elementos del RecyclerView*/
        TextView TV_codigo, TV_estado;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            TV_codigo =(TextView) itemView.findViewById(R.id.TV_codigo);
            TV_estado= (TextView)itemView.findViewById(R.id.TV_estado);
        }
    }
}
