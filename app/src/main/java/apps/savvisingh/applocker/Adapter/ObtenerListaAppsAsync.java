package apps.savvisingh.applocker.Adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import apps.savvisingh.applocker.Data.AppInfo;
import apps.savvisingh.applocker.Fragments.todasAppFragment;
import apps.savvisingh.applocker.Prefrence.SharedPreference;
import apps.savvisingh.applocker.Utils.CONSTANTES;


public class ObtenerListaAppsAsync extends AsyncTask <String , Void, List<AppInfo>> {

    todasAppFragment contenedor;

    /**Constrctor recibe un fragmento*/
    public ObtenerListaAppsAsync(todasAppFragment fragment){
        contenedor = fragment;
    }
    @Override
    protected List<AppInfo> doInBackground(String... strings) {

        /**Ejecuta en segundo plano*/
        String TipoApp = strings[0];

        /**Llena una lista con todas las apicaciones instaladas*/
        List<AppInfo> listado = obtenerListaInstalados(contenedor.getActivity());

        /**Instancia los almacenamientos de los datos */
        SharedPreference sharedPreference = new SharedPreference();
        List<AppInfo> AppBloqueadas = new ArrayList<AppInfo>();
        List<AppInfo> AppSinBloqueo = new ArrayList<AppInfo>();

        boolean flag = true;
            for (int i = 0; i < listado.size(); i++) {
                flag = true;
                if (sharedPreference.getBloqueados(contenedor.getActivity()) != null) {
                    for (int j = 0; j < sharedPreference.getBloqueados(contenedor.getActivity()).size(); j++) {
                        if (listado.get(i).getNombrePaquete().matches(sharedPreference.getBloqueados(contenedor.getActivity()).get(j))) {
                            AppBloqueadas.add(listado.get(i));
                            flag = false;
                        }
                    }
                }
                if (flag) AppSinBloqueo.add(listado.get(i));
            }

            if (TipoApp.matches(CONSTANTES.BLOQUEADO)) {
                /**Llena el listado con las aplicaciones bloqueadas*/
                listado.clear();
                listado.addAll(AppBloqueadas);
            } else if (TipoApp.matches(CONSTANTES.NOBLOQUEADO)) {
                /**Llena el listado con las aplicaciones sin bloquear*/
                listado.clear();
                listado.addAll(AppSinBloqueo);
            }
        return listado;
    }


    public static List<AppInfo> obtenerListaInstalados(Context context) {
        /**Instancia las variables a usar*/
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> AppsInstaladas = new ArrayList();

        /**Obtiene los paquetes instalados en el sistema*/
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        /**Compprueba que el listado no este vacio*/
        if (apps != null && !apps.isEmpty()) {
            for (int i = 0; i < apps.size(); i++) {
                PackageInfo p = apps.get(i);
                try {
                    /**Obtiene los atributos de cada aplicación*/
                    if (null != packageManager.getLaunchIntentForPackage(p.packageName)) {
                        AppInfo app = new AppInfo();
                        app.setNombre(p.applicationInfo.loadLabel(packageManager).toString());
                        app.setNombrePaquete(p.packageName);
                        app.setNombreVersion(p.versionName);
                        app.setCodigoVersion(p.versionCode);
                        app.setIcono(p.applicationInfo.loadIcon(packageManager));

                        /**Llena la lista con las aplicaciones instaladas*/
                        AppsInstaladas.add(app);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return AppsInstaladas;
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        contenedor.showProgressBar();
    }

    @Override
    protected void onPostExecute(List<AppInfo> appInfos) {
        super.onPostExecute(appInfos);
        if(contenedor !=null && contenedor.getActivity()!=null) {
            contenedor.hideProgressBar();
            contenedor.updateData(appInfos);
        }
    }
}
