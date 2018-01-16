package ua.binotel.www.binotelmobile.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ua.binotel.www.binotelmobile.MainActivity;
import ua.binotel.www.binotelmobile.dualsim.TelephonyManagement;

public class NetworkChangeReceiver extends BroadcastReceiver {



    public static String conn = "connect";
    public static String stat;
//    public static TelephonyManagement.TelephonyInfo telephonyInfo;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i("tag", "Test: ");
        int status = NetworkUtil.getConnectivityStatusString(context);
        stat = String.valueOf(status);

        if (status > 0) {
//            MainActivity.postFile();
        }
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (status > 0) {
//                MainActivity.postFile();
            }
        }

//        telephonyInfo = TelephonyManagement.getInstance().updateTelephonyInfo(context).getTelephonyInfo(context);
    }
}