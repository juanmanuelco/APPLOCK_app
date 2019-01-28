package apps.savvisingh.applocker.Services;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.takwolf.android.lock9.Lock9View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import apps.savvisingh.applocker.Actualizaciones;
import apps.savvisingh.applocker.InicioActivity;
import apps.savvisingh.applocker.NEGOCIO.Servidor;
import apps.savvisingh.applocker.NEGOCIO.singletonDatos;
import apps.savvisingh.applocker.Prefrence.SharedPreference;
import apps.savvisingh.applocker.R;



public class ServCheqApp extends Service {

    public static final String TAG = "ServCheqApp";
    private Context context = null;
    private Timer timer;
    ImageView imageView;
    private WindowManager windowManager;
    private Dialog dialog;
    public static String appActual = "";
    public static String appPrevia = "";
    SharedPreference sharedPreference;
    List<String> nombrePaquete;
    String serverURL= Servidor.servicio("/usuario/desboqueo");

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sharedPreference = new SharedPreference();
        context.startService(new Intent(context, Actualizaciones.class));

        /**Obtiene los bloqueados*/
        if (sharedPreference != null) nombrePaquete = sharedPreference.getBloqueados(context);

        /**Activa un control temporal */
        timer = new Timer("ServCheqApp");
        timer.schedule(recargarTarea, 1000L, 1000L);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageView = new ImageView(this);
        imageView.setVisibility(View.GONE);

        /**Crea el popup donde se ingresará el patrón*/
        final WindowManager.LayoutParams parametros_pantalla = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        /**Establece los  valores del popup*/
        parametros_pantalla.gravity = Gravity.TOP | Gravity.CENTER;
        parametros_pantalla.x = ((getApplicationContext().getResources().getDisplayMetrics().widthPixels) / 2);
        parametros_pantalla.y = ((getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 2);
        windowManager.addView(imageView, parametros_pantalla);

    }

    /**Permite ejecutar la inspección de aplicaciones */
    private TimerTask recargarTarea = new TimerTask() {
        @Override
        public void run() {
            if (sharedPreference != null) nombrePaquete = sharedPreference.getBloqueados(context);
            if (estaLaAppEnPrimerPlano()) {
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            if (!appActual.matches(appPrevia)) {
                                /**Muestra el popup*/
                                mostrarPantDesbloq();
                                appPrevia = appActual;
                            }
                        }
                    });
                }
                return;
            }
            if (imageView != null) {
                imageView.post(new Runnable() {
                    public void run() {
                        /**Si se acepta el patrón oculta el popup*/
                        ocultarPopupBloq();
                    }
                });
                return;
            }
        }
    };

    void mostrarPantDesbloq() {
        showDialog();
    }

    void ocultarPopupBloq() {
        appPrevia = "";
        /**Oculta el popup de bloqueo de apps*/
        try { if (dialog != null && dialog.isShowing())  dialog.dismiss();
        } catch (Exception e) { e.printStackTrace(); }
    }

    void showDialog() {
        /**Obtiene el contexto de la aplicación*/
        if (context == null) context = getApplicationContext();

        /**Establece los elementos del popup de bloqueo*/
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.popup_unlock, null, false);
        Lock9View lock9View = (Lock9View) promptsView.findViewById(R.id.lock_9_view);
        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {

                /**Revisa que la contraseña sea correcta*/
                revisarContrasena(password);
            }
        });

        /**Aumenta las propiedades del popup*/
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(promptsView);
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
                return true;
            }
        });

        dialog.show();

    }

    public void revisarContrasena(final String passwd){
        /**Hace una petición al servidor para comprobar la validez de la contraseña*/
        StringRequest stringRequest=new StringRequest(Request.Method.POST, serverURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if((result.toString()).equals("E1")){

                            /**Si el servidor no puede hacerse cargo intenta una sesión local*/
                            if (passwd.matches(sharedPreference.getPassword(context))) dialog.dismiss();
                            else Toast.makeText(getApplicationContext(), "Patrón equivocado", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if((result.toString()).equals("E2")){

                            /**Si el usuario no existe se elimina la sesión y se direcciona a configurar*/
                            sharedPreference.setSession(context);
                            Intent intent= new Intent(context, InicioActivity.class);
                            startActivity(intent);
                            return;
                        }
                        if(passwd.matches(result.substring(1, result.length()))) dialog.dismiss();
                        else Toast.makeText(getApplicationContext(), "Patrón equivocado", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                /**Si no está conectado a la red intenta hacer un inisio de sesión local*/
                if (passwd.matches(sharedPreference.getPassword(context))) dialog.dismiss();
                else Toast.makeText(getApplicationContext(), "Patrón equivocado", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<String, String >();
                /**Se envía la sesión al servidor para validar su existencia*/
                params.put("usuario", sharedPreference.getSesion(context));
                return params;
            }
        };
        singletonDatos.getInstancia(this).addToRequest(stringRequest);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {}
        return START_STICKY;
    }

    public boolean estaLaAppEnPrimerPlano() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tarea = manager.getRunningTasks(5);
        if (Build.VERSION.SDK_INT <= 20) {
            if (tarea.size() > 0) {
                ComponentName componentInfo = tarea.get(0).topActivity;
                for (int i = 0; nombrePaquete != null && i < nombrePaquete.size(); i++) {
                    String nompaq= "sistema: "+ componentInfo.getPackageName()+" servidor: "+ nombrePaquete.get(i);
                    if (componentInfo.getPackageName().equals(nombrePaquete.get(i))) {
                        appActual = nombrePaquete.get(i);
                        return true;
                    }
                }
            }
        } else {
            String mpackageName = manager.getRunningAppProcesses().get(0).processName;
            UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTask.isEmpty()) mpackageName = "";
                else mpackageName = runningTask.get(runningTask.lastKey()).getPackageName();
            }

            for (int i = 0; nombrePaquete != null && i < nombrePaquete.size(); i++) {
                String nompaq= "sistema: "+ mpackageName+" servidor: "+ nombrePaquete.get(i);
                if (mpackageName.equals(nombrePaquete.get(i))) {
                    appActual = nombrePaquete.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        if (imageView != null) windowManager.removeView(imageView);
        try { if (dialog != null && dialog.isShowing()) dialog.dismiss(); }
        catch (Exception e) {e.printStackTrace();  }
    }
}
