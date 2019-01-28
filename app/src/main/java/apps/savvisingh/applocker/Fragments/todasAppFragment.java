package apps.savvisingh.applocker.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import apps.savvisingh.applocker.Adapter.AdptadorListaAplicaciones;
import apps.savvisingh.applocker.Adapter.ObtenerListaAppsAsync;
import apps.savvisingh.applocker.Data.AppInfo;
import apps.savvisingh.applocker.R;


public class todasAppFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adaptador;
    private RecyclerView.LayoutManager mLayoutManager;
    static String requiredAppsType;

    public static todasAppFragment newInstance(String requiredApps) {
        requiredAppsType = requiredApps;
        todasAppFragment f = new todasAppFragment();
        return (f);
    }


    public todasAppFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ProgressDialog progressDialog;
    List<AppInfo> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**Asigna el layout para mostrar en el recyclerView*/
        View v = inflater.inflate(R.layout.fragment_all_apps, container, false);

        /**Muestra un cuadro de progreso mientras obtiene las apicaciones*/
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando aplicacines");
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        /**Crea un array para guardar */
        list = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        /**Instancia un adaptador de datos para el RecyclerView*/
        adaptador = new AdptadorListaAplicaciones(list , getActivity(), requiredAppsType);
        mRecyclerView.setAdapter(adaptador);

        /**Obtiene las aplicaciones en una tarea aincrona*/
        ObtenerListaAppsAsync task = new ObtenerListaAppsAsync(this);
        task.execute(requiredAppsType);
        return v;
    }


    public void showProgressBar() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressDialog.show();

    }

    public void hideProgressBar(){
        progressDialog.dismiss();
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void updateData(List<AppInfo> list){
        this.list.clear();
        this.list.addAll(list);
        adaptador.notifyDataSetChanged();
    }
}
