package ua.binotel.www.binotelmobile.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import ua.binotel.www.binotelmobile.MainActivityNew;

public class NetworkChangeReceiver extends BroadcastReceiver {



    public static String conn = "connect";
    public static String stat;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        System.out.println("NetworkChangeReceiver system");
        int status = NetworkUtil.getConnectivityStatusString(context);
        stat = String.valueOf(status);
        if (status > 0) {
            MainActivityNew.postFile();
        }
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (status > 0) {
                MainActivityNew.postFile();
            }
        }

    }
}