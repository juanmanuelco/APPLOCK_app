package apps.savvisingh.applocker;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import apps.savvisingh.applocker.Services.AlarmaReceptora;
import apps.savvisingh.applocker.Services.ServCheqApp;

public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 1000;
    Context context;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public static int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 12345;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_splash);
        checkPermissions();
    }


    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                OverlayPermissionDialogFragment dialogFragment = new OverlayPermissionDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "Overlay Permission");
                return;
            }
            if(!hasUsageStatsPermission()){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                UsageAcessDialogFragment dialogFragment = new UsageAcessDialogFragment();
                ft.add(dialogFragment, null);
                ft.commitAllowingStateLoss();
                return;
            }
            startService();
            }
        }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    public void startService(){
        startService(new Intent(SplashActivity.this, ServCheqApp.class));

        try {
            Intent alarmIntent = new Intent(context, AlarmaReceptora.class);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
            int interval = (86400 * 1000) / 4;
            if (manager != null) manager.cancel(pendingIntent);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        checkPermissions();
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }


    public static class OverlayPermissionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.DESC_SUPERPO)
                    .setTitle(R.string.SUPERPO)
                    .setPositiveButton(R.string.PERMIT, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getActivity().getPackageName()));
                            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                        }
                    });
            return builder.create();
        }
    }

    public static class UsageAcessDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.DESC_DATA_USE)
                    .setTitle(R.string.DATA_USE)
                    .setPositiveButton(R.string.CONFIRMAR, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(
                                    new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                                    MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                        }
                    });
            return builder.create();
        }
    }
}
