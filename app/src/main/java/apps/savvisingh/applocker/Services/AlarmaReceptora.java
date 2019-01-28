package apps.savvisingh.applocker.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.savvisingh.applocker.Actualizaciones;


public class AlarmaReceptora extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /**Chequea las applicaciones para saber si es prudente bloquearlas*/
        context.startService(new Intent(context, ServCheqApp.class));
        context.startService(new Intent(context, Actualizaciones.class));
    }
}
