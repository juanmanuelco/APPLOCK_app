package apps.savvisingh.applocker.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.savvisingh.applocker.Data.AppInfo;
import apps.savvisingh.applocker.Prefrence.SharedPreference;
import apps.savvisingh.applocker.R;


public class AdptadorListaAplicaciones extends RecyclerView.Adapter<AdptadorListaAplicaciones.ViewHolder> {
    List<AppInfo> AppsInstaladas = new ArrayList();
    private Context context;
    SharedPreference sharedPreference;
    String tipoAppRequeridos;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView NombreApp;
        public CardView cardView;
        public ImageView icono;
        public Switch switchView;

        /**Instancia los elementos del cardview*/

        public ViewHolder(View v) {
            super(v);
            NombreApp = (TextView) v.findViewById(R.id.applicationName);
            cardView = (CardView) v.findViewById(R.id.card_view);
            icono = (ImageView) v.findViewById(R.id.icon);
            switchView = (Switch) v.findViewById(R.id.switchView);
        }
    }

    /**Obtiene la lista de aplicacines por medio de un contructor*/
    public AdptadorListaAplicaciones(List<AppInfo> appInfoList, Context context, String tipoAppRequeridos) {
        AppsInstaladas = appInfoList;
        this.context = context;
        this.tipoAppRequeridos = tipoAppRequeridos;
        sharedPreference = new SharedPreference();
    }


    @Override
    public AdptadorListaAplicaciones.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**Asigna el layout a poner dentro de el RecyclerView*/
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        /**Llena el layout con la información de las aplicaciones*/
        final AppInfo appInfo = AppsInstaladas.get(position);
        holder.NombreApp.setText(appInfo.getNombre());
        holder.icono.setBackgroundDrawable(appInfo.getIcono());
        holder.switchView.setOnCheckedChangeListener(null);
        holder.cardView.setOnClickListener(null);

        /**Si la aplicación es cnsiderada como bloqueada se activa el switch*/
        if (checkItemBloqueado(appInfo.getNombrePaquete())) holder.switchView.setChecked(true);
        else holder.switchView.setChecked(false);

        holder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /**Lo que pasa cuando se activa o se desctiva el switch*/
                if (isChecked) sharedPreference.AnadirBloqueo(context, appInfo.getNombrePaquete());
                else sharedPreference.QuitarBloqueo(context, appInfo.getNombrePaquete());
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
        /**Cuenta las plicaciones instaladas*/
        return AppsInstaladas.size();
    }
    
    public boolean checkItemBloqueado(String checkApp) {

        /**Chequea si el swtch debe o ono estar bloqueado*/
        boolean check = false;
        List<String> locked = sharedPreference.getBloqueados(context);
        if (locked != null) {
            for (String lock : locked) {
                if (lock.equals(checkApp)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

}