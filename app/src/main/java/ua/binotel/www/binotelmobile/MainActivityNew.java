package ua.binotel.www.binotelmobile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.MultipartFormDataBody;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ua.binotel.www.binotelmobile.DB.DatabaseHandler;
import ua.binotel.www.binotelmobile.dualsim.TelephonyInfo;
import ua.binotel.www.binotelmobile.network.NetworkChangeReceiver;
import ua.binotel.www.binotelmobile.network.NetworkUtil;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static ua.binotel.www.binotelmobile.Constants.REQUEST_RECORD_AUDIO_PERMISSION;

public class MainActivityNew extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    public ListView listView;
    // public ScrollView mScrollView;
    public ScrollView mScrollView2;
    public TextView mTextView;
    private static final int CATEGORY_DETAIL = 1;
    private static final int NO_MEMORY_CARD = 2;
    private static final int TERMS = 3;

    public RadioButton radEnable;
    public RadioButton radDisable;

    private static Resources res;
    private Context context;
    private Context client;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    public NetworkChangeReceiver conn;

    public Handler mHandler;

    DatabaseHandler myDb = new DatabaseHandler(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);


        setContentView(R.layout.activity_main_new);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();

        listView = (ListView) findViewById(R.id.mylist);
        // mScrollView = (ScrollView) findViewById(R.id.ScrollView01);
        mScrollView2 = (ScrollView) findViewById(R.id.ScrollView02);
        mTextView = (TextView) findViewById(R.id.txtNoRecords);
        SharedPreferences settings = this.getSharedPreferences(
                Constants.LISTEN_ENABLED, 0);
        boolean silentMode = settings.getBoolean("silentMode", true);

        if (silentMode)
            showDialog(CATEGORY_DETAIL);

        context = this.getBaseContext();

        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();

        if (NetworkUtil.getConnectivityStatus(MainActivityNew.this) > 0 ){ // Connect
            System.out.println("Connect");
//            postFile();
        } else {
            System.out.println("No connection");
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                showMessage("Calls", message.toString());
            }
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            /*String getSimOperatorName = tMgr.getLine1Number();
            String getDeviceId = tMgr.getDeviceId();


            Toast.makeText(context, "getDeviceId: " + getDeviceId,Toast.LENGTH_SHORT).show();*/
        }




//        final TelephonyManagement.TelephonyInfo telephonyInfo;

        String deviceId = tm.getDeviceId();

        String deviceSoftwareVersion = tm.getDeviceSoftwareVersion();
        String networkOperator = tm.getNetworkOperator();
        String networkOperatorName = tm.getNetworkOperatorName();
        String serialNumber = tm.getSimSerialNumber();
        String phoneNumber = tm.getLine1Number();
        String simCountry = tm.getSimCountryIso();

        Log.w("tag", "getDeviceId: " + deviceId);
        Log.w("tag", "getDeviceSoftwareVersion: " + deviceSoftwareVersion);
        Log.w("tag", "getNetworkOperator: " + networkOperator);
        Log.w("tag", "getNetworkOperatorName: " + networkOperatorName);
        Log.w("tag", "getSimSerialNumber: " + serialNumber);
        Log.w("tag", "phoneNumber: " + phoneNumber);
        Log.w("tag", "getSimCountryIso: " + simCountry);

//        telephonyInfo = TelephonyManagement.getInstance().updateTelephonyInfo(context).getTelephonyInfo(context);
        StringBuffer bufferSim = new StringBuffer();
        /*String phoneNumberSim1 = TelephonyUtil.getSendNumber(context, telephonyInfo.getOperatorSIM1());
        String phoneNumberSim2 = TelephonyUtil.getSendNumber(context, telephonyInfo.getOperatorBySlotId(DualsimBase.TYPE_SIM_ASSISTANT));

        bufferSim.append("phoneNumberSim1: " + phoneNumberSim1 + "\n");
        bufferSim.append("phoneNumberSim2: " + phoneNumberSim2 + "\n");
        Log.w(Constants.TAG, bufferSim.toString());
        Toast.makeText(context, "getDeviceId: " + bufferSim.toString(),Toast.LENGTH_SHORT).show();*/



//        getFileName("s/d/f/3/0/d20180104174432p5433.3gp");
//        myDb.addCall(new Call("Павло Полуботок", "0965532211", 1515069143, 1515069143));
//        myDb.addCall(new Call("Alexander", "0965532255", 1515069143, 1515070251));

//        myDb.deleteAll();

//        showCallsFromDB();



    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onResume() {

        if (updateExternalStorageState() == Constants.MEDIA_MOUNTED) {
            final List<Model> listDir = FileHelper.listFiles(this);

            if (listDir.isEmpty()) {
                mScrollView2.setVisibility(TextView.VISIBLE);
                listView.setVisibility(ScrollView.GONE);
            } else {
                mScrollView2.setVisibility(TextView.GONE);
                listView.setVisibility(ScrollView.VISIBLE);
            }

            final MyCallsAdapter adapter = new MyCallsAdapter(this, listDir);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    adapter.showPromotionPieceDialog(listDir.get(position)
                            .getCallName(), position);
                }
            });

            adapter.sort(new Comparator<Model>() {
                public int compare(Model arg0, Model arg1) {
                    Long date1 = Long.valueOf(arg0.getCallName().substring(1,
                            15));
                    Long date2 = Long.valueOf(arg1.getCallName().substring(1,
                            15));
                    return (date1 > date2 ? -1 : (date1 == date2 ? 0 : 1));
                }
            });

            listView.setAdapter(adapter);
        } else if (updateExternalStorageState() == Constants.MEDIA_MOUNTED_READ_ONLY) {
            mScrollView2.setVisibility(TextView.VISIBLE);
            listView.setVisibility(ScrollView.GONE);
            showDialog(NO_MEMORY_CARD);
        } else {
            mScrollView2.setVisibility(TextView.VISIBLE);
            listView.setVisibility(ScrollView.GONE);
            showDialog(NO_MEMORY_CARD);
        }

        super.onResume();
        registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public static String getDataFromRawFiles(int id) throws IOException {
        InputStream in_s = res.openRawResource(id);

        byte[] b = new byte[in_s.available()];
        in_s.read(b);
        String value = new String(b);

        return value;
    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(),"Settings",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(),"Trash",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        finish();

                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_email.setText("raj.amalw@learn2crack.com");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public static int updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return Constants.MEDIA_MOUNTED;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return Constants.MEDIA_MOUNTED_READ_ONLY;
        } else {
            return Constants.NO_MEDIA;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharedPreferences settings = this.getSharedPreferences(
                Constants.LISTEN_ENABLED, 0);
        boolean silentMode = settings.getBoolean("silentMode", true);

        MenuItem menuDisableRecord = menu.findItem(R.id.menu_Disable_record);
        MenuItem menuEnableRecord = menu.findItem(R.id.menu_Enable_record);

        // silent is disabled, disableRecord item must be disabled
        menuEnableRecord.setEnabled(silentMode);
        menuDisableRecord.setEnabled(!silentMode);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast toast;
        final Activity currentActivity = this;
        String mess;
        switch (item.getItemId()) {
            case R.id.menu_sim_info:
                StringBuffer simsInfo = new StringBuffer();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
                    SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
                    List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                    int i = 1;
                    for (SubscriptionInfo subscriptionInfo : subsInfoList) {
                        String info = subscriptionInfo.toString();
                        simsInfo.append("SIM " + i + ": " + info + "\n");
                        i ++;
                    }
                    String imeiSIM1 = telephonyInfo.getImsiSIM1();
                    String imeiSIM2 = telephonyInfo.getImsiSIM2();
                    simsInfo.append("imeiSIM1 " + i + ": " + imeiSIM1 + "\n");
                    simsInfo.append("imeiSIM2 " + i + ": " + imeiSIM2 + "\n");
                    showMessage("Info about your SIMs", simsInfo.toString() );
                    break;
                } else {
                    showMessage("you have only one SIM", "");
                    break;
                }
            case R.id.menu_db:
                Cursor res = myDb.getAllData();

                if(res.getCount() == 0) {
                    showMessage("Error", "Nothing found");
                    break;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("Id: " + res.getString(0) + "\n");
                    buffer.append("name: " + res.getString(1) + "\n");
                    buffer.append("Phone number: " + res.getString(2) + "\n");
                    buffer.append("Start: " + res.getInt(3) + "\n");
                    buffer.append("End: " + res.getInt(4) + "\n");
                }
                showMessage("Calls", buffer.toString());
                break;
            case R.id.menu_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainActivityNew.this);
                builder.setTitle(R.string.about_title)
                        .setMessage(R.string.about_content)
                        .setPositiveButton(R.string.about_close_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                break;
            case R.id.menu_Disable_record:
                setSharedPreferences(true);
                toast = Toast.makeText(this,
                        this.getString(R.string.menu_record_is_now_disabled),
                        Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.menu_Enable_record:
                setSharedPreferences(false);
                // activateNotification();
                toast = Toast.makeText(this,
                        this.getString(R.string.menu_record_is_now_enabled),
                        Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.menu_see_terms:
                Intent i = new Intent(this.getBaseContext(), TermsActivity.class);
                startActivity(i);
                break;
            case R.id.menu_privacy_policy:
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://www.privacychoice.org/policy/mobile?policy=306ef01761f300e3c30ccfc534babf6b"));
                startActivity(browserIntent);
                break;
            case R.id.menu_delete_all:
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(
                        MainActivityNew.this);
                builderDelete
                        .setTitle(R.string.dialog_delete_all_title)
                        .setMessage(R.string.dialog_delete_all_content)
                        .setPositiveButton(R.string.dialog_delete_all_yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        myDb.deleteAll();
                                        FileHelper
                                                .deleteAllRecords(currentActivity);
                                        onResume();
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(R.string.dialog_delete_all_no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSharedPreferences(boolean silentMode) {
        SharedPreferences settings = this.getSharedPreferences(
                Constants.LISTEN_ENABLED, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("silentMode", silentMode);
        editor.commit();

        Intent myIntent = new Intent(context, RecordService.class);
        myIntent.putExtra("commandType",
                silentMode ? Constants.RECORDING_DISABLED
                        : Constants.RECORDING_ENABLED);
        myIntent.putExtra("silentMode", silentMode);
        context.startService(myIntent);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CATEGORY_DETAIL:
                LayoutInflater li = LayoutInflater.from(this);
                View categoryDetailView = li.inflate(
                        R.layout.startup_dialog_layout, null);

                AlertDialog.Builder categoryDetailBuilder = new AlertDialog.Builder(
                        this);
                categoryDetailBuilder.setTitle(this
                        .getString(R.string.dialog_welcome_screen));
                categoryDetailBuilder.setView(categoryDetailView);
                AlertDialog categoryDetail = categoryDetailBuilder.create();

                categoryDetail.setButton2("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (radEnable.isChecked())
                                    setSharedPreferences(false);
                                if (radDisable.isChecked())
                                    setSharedPreferences(true);
                            }
                        });

                return categoryDetail;
            case NO_MEMORY_CARD:
                li = LayoutInflater.from(this);

                categoryDetailBuilder = new AlertDialog.Builder(this);
                categoryDetailBuilder.setMessage(R.string.dialog_no_memory);
                categoryDetailBuilder.setCancelable(false);
                categoryDetailBuilder.setPositiveButton(
                        this.getString(R.string.dialog_close),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                categoryDetail = categoryDetailBuilder.create();

                return categoryDetail;
            case TERMS:
                li = LayoutInflater.from(this);

                categoryDetailBuilder = new AlertDialog.Builder(this);
                categoryDetailBuilder.setMessage(this
                        .getString(R.string.dialog_privacy_terms));
                categoryDetailBuilder.setCancelable(false);
                categoryDetailBuilder.setPositiveButton(
                        this.getString(R.string.dialog_terms),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(context, TermsActivity.class);
                                startActivity(i);
                            }
                        });
                categoryDetailBuilder.setNegativeButton(
                        this.getString(R.string.dialog_privacy),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent browserIntent = new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://www.privacychoice.org/policy/mobile?policy=306ef01761f300e3c30ccfc534babf6b"));
                                startActivity(browserIntent);
                            }
                        });
                categoryDetailBuilder.setNeutralButton(
                        this.getString(R.string.dialog_close),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                categoryDetail = categoryDetailBuilder.create();

                return categoryDetail;
            default:
                break;
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        switch (id) {
            case CATEGORY_DETAIL:
                AlertDialog categoryDetail = (AlertDialog) dialog;
                radEnable = (RadioButton) categoryDetail
                        .findViewById(R.id.radio_Enable_record);
                radDisable = (RadioButton) categoryDetail
                        .findViewById(R.id.radio_Disable_record);
                radEnable.setChecked(true);
                break;
            default:
                break;
        }
        super.onPrepareDialog(id, dialog);
    }

    public void postFile() {
        final ArrayList<String> filesListLocal = new ArrayList<String>();
        final ArrayList<String> uniquevalues = new ArrayList<String>();
        AsyncHttpGet get = new AsyncHttpGet("http://pr-web.com.ua/filesList.php");

        String path = FileHelper.getFilePath() + "/"
                + Constants.FILE_DIRECTORY;
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            filesListLocal.add(files[i].getName());
        }
        final StringBuffer buffer = new StringBuffer();

        AsyncHttpClient.getDefaultInstance().executeString(get, new AsyncHttpClient.StringCallback() {
            // Callback is invoked with any exceptions/errors, and the result, if available.
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse response, String result) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                System.out.println("I got a string: " + result);
                String [] strings = new String [] {result};
                List<String> filesListServer = new ArrayList<String>(Arrays.asList(strings));


                for (String item : filesListLocal) {

                    if (filesListServer.contains(item.replace(",",""))) {
//                        duplicatevalues.add(item);
                    } else {
                        uniquevalues.add(item);
                    }
                }
                for (String item: uniquevalues) {
                    AsyncHttpPost post = new AsyncHttpPost("http://pr-web.com.ua/server.php");
                    MultipartFormDataBody body = new MultipartFormDataBody();
                    body.addFilePart("my-file", new File(FileHelper.getFilePath() + "/"
                            + Constants.FILE_DIRECTORY + "/" + item));
                    post.setBody(body);
                    final String md5Str = getMD5(FileHelper.getFilePath() + "/"
                            + Constants.FILE_DIRECTORY + "/" + item);
                    body.addStringPart("md5", md5Str);
                    AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback(){
                        @Override
                        public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                            if (ex != null) {
                                ex.printStackTrace();
                                return;
                            }
                            String json = result;

                            try {

                                final JSONObject obj = new JSONObject(json);
                                String fileName = obj.get("name").toString();
                                String status = obj.get("status").toString();
                                buffer.append("file name: " + fileName + "\n");
                                buffer.append("status: " + status + "\n");
                            } catch (Throwable t) {
                                Log.w("My App", "Could not parse malformed JSON: \"" + json + "\"");
                            }


                        }
                    });
                }
                MainActivityNew.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        showMessage("info", buffer.toString());
                    }
                });

            }

        });

    }

//    private void openDB() {
//      DatabaseHandler myDb = new DatabaseHandler(this);
//      myDb.open();
//    }

    private void showCallsFromDB() {
        final List<Model> listDir = FileHelper.listFiles(this);
        final MyCallsAdapter adapter = new MyCallsAdapter(this, listDir);
        adapter.sort(new Comparator<Model>() {
            public int compare(Model arg0, Model arg1) {
                Long date1 = Long.valueOf(arg0.getCallName().substring(1,
                        15));
                Long date2 = Long.valueOf(arg1.getCallName().substring(1,
                        15));
                return (date1 > date2 ? -1 : (date1 == date2 ? 0 : 1));
            }
        });

        listView.setAdapter(adapter);
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    public void getFileName(String path) {
        AsyncHttpPost post = new AsyncHttpPost("http://pr-web.com.ua/filesList.php");
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addStringPart("time", "12121212121");
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback(){
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                final String res = result.toString();
                MainActivityNew.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Handle UI here
                        Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                    }
                });

                return;
            }
        });
//        AsyncHttpPost post = new AsyncHttpPost("http://dev.mmy.binotel.ua/filesList.php");

        return;
    }

    public static String getMD5EncryptedString(String encTarget){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }

    public static String getMD5(String path) {
        String md5Checksum = "";
        try {
            String md5Origin	= "";//original file's md5 checksum
            String filePath   = path; //fill with the real file path name

            FileInputStream fis   = new FileInputStream(filePath);
            md5Checksum	= Util.md5(
                    fis);

            if (md5Checksum.equals(md5Origin)) {
                //file is valid

            }
        } catch (Exception e) {

        }
        return md5Checksum;
    }

}
