package ua.binotel.www.binotelmobile;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MyPhoneReceiver extends BroadcastReceiver {

    private String phoneNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
//            String getSimOperatorName = tMgr.getSimOperatorName();

//            Toast.makeText(context, "getSimOperatorName: " + getSimOperatorName,Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            List<CellInfo> mPhoneNumber = tm.getAllCellInfo();
//            Toast.makeText(context, "getSimOperatorName: " + mPhoneNumber,Toast.LENGTH_SHORT).show();
        }

        phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

//        Log.d(Constants.TAG, "MyPhoneReciever phoneNumber "+phoneNumber);
        if (MainActivity.updateExternalStorageState() == Constants.MEDIA_MOUNTED) {
            try {
                SharedPreferences settings = context.getSharedPreferences(
                        Constants.LISTEN_ENABLED, 0);
                boolean silent = settings.getBoolean("silentMode", true);
                if (extraState != null) {
                    if (extraState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        Intent myIntent = new Intent(context,
                                RecordService.class);
                        myIntent.putExtra("commandType",
                                Constants.STATE_CALL_START);
                        context.startService(myIntent);
                    } else if (extraState
                            .equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        Intent myIntent = new Intent(context,
                                RecordService.class);
                        myIntent.putExtra("commandType",
                                Constants.STATE_CALL_END);
                        context.startService(myIntent);
                    } else if (extraState
                            .equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        if (phoneNumber == null)
                            phoneNumber = intent
                                    .getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        Intent myIntent = new Intent(context,
                                RecordService.class);
                        Toast.makeText(context, "incoming number: " + phoneNumber,Toast.LENGTH_SHORT).show();
                        myIntent.putExtra("commandType",
                                Constants.STATE_INCOMING_NUMBER);
                        myIntent.putExtra("phoneNumber", phoneNumber);
                        myIntent.putExtra("silentMode", silent);
                        context.startService(myIntent);
                    }
                } else if (phoneNumber != null) {
                    Intent myIntent = new Intent(context, RecordService.class);
                    myIntent.putExtra("commandType",
                            Constants.STATE_INCOMING_NUMBER);
                    myIntent.putExtra("phoneNumber", phoneNumber);
                    myIntent.putExtra("silentMode", silent);
                    context.startService(myIntent);
                }
            } catch (Exception e) {
                Log.e(Constants.TAG, "Exception");
                e.printStackTrace();
            }
        }
    }

}