package ua.binotel.www.binotelmobile;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MyPhoneReceiver extends BroadcastReceiver {

    private String phoneNumber;
    private static WindowManager windowManager;
    private static ViewGroup windowLayout;

    @Override
    public void onReceive(Context context, Intent intent) {

//        MainActivityNew.checkSimCard();
        /*TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            String getSimOperatorName = tMgr.getLine1Number();
            String getDeviceId = tMgr.getDeviceId();

            Toast.makeText(context, "getDeviceId: " + getDeviceId,Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            List<CellInfo> mPhoneNumber = tm.getAllCellInfo();
            Toast.makeText(context, "getSimOperatorName: " + mPhoneNumber,Toast.LENGTH_SHORT).show();
        }
        String getLine1Number = tm.getLine1Number();
        String getDeviceId = tMgr.getDeviceId();

        Toast.makeText(context, "getDeviceId: " + getDeviceId,Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "getSimOperatorName: " + getLine1Number,Toast.LENGTH_LONG).show();*/
        phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//        showWindow(context, phoneNumber);

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

    private void showWindow(Context context, String phone) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;

        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);

        TextView textViewNumber=(TextView) windowLayout.findViewById(R.id.textViewNumber);
        Button buttonClose=(Button) windowLayout.findViewById(R.id.buttonClose);
        textViewNumber.setText(phone);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow();
            }
        });

        windowManager.addView(windowLayout, params);
    }

    private void closeWindow() {
        if (windowLayout !=null){
            windowManager.removeView(windowLayout);
            windowLayout =null;
        }
    }

}