package apps.savvisingh.applocker.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.savvisingh.applocker.Actualizaciones;


public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        /**Inicia el servicio de chequeo de apps*/
        context.startService(new Intent(context, ServCheqApp.class));
        context.startService(new Intent(context, Actualizaciones.class));

         /**Repite el chequeo cada determinado tiempo*/
        Intent alarmIntent = new Intent(context, AlarmaReceptora.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = (86400 * 1000) / 4;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }
}
