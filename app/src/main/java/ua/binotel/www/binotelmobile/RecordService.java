package ua.binotel.www.binotelmobile;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.MultipartFormDataBody;

import java.io.IOException;

import ua.binotel.www.binotelmobile.DB.Call;
import ua.binotel.www.binotelmobile.DB.DatabaseHandler;

import static ua.binotel.www.binotelmobile.Constants.TAG;

public class RecordService extends Service {

    private MediaRecorder recorder = null;
    private String phoneNumber = null;
    private String myPhone = null;

    private String fileName;
    private boolean onCall = false;
    private boolean recording = false;
    private boolean silentMode = false;
    private boolean onForeground = false;
    DatabaseHandler myDb = new DatabaseHandler(this);
    public int startInt;
    public int endInt;
    NetworkInfo wifiCheck;

    private boolean isStart = false;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        MainActivityNew.postFile();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "RecordService onStartCommand");

        myPhone = intent.getStringExtra("phoneNumber");
        if (intent != null) {
            int commandType = intent.getIntExtra("commandType", 0);
            if (commandType != 0) {
                if (commandType == Constants.RECORDING_ENABLED) {
                    Log.d(TAG, "RecordService RECORDING_ENABLED");
                    silentMode = intent.getBooleanExtra("silentMode", true);
                    if (!silentMode && phoneNumber != null && onCall
                            && !recording)
                        commandType = Constants.STATE_START_RECORDING;

                } else if (commandType == Constants.RECORDING_DISABLED) {
                    Log.d(TAG, "RecordService RECORDING_DISABLED");
                    silentMode = intent.getBooleanExtra("silentMode", true);
                    if (onCall && phoneNumber != null && recording)
                        commandType = Constants.STATE_STOP_RECORDING;
                }

                if (commandType == Constants.STATE_INCOMING_NUMBER) {
                    Log.d(TAG, "RecordService STATE_INCOMING_NUMBER");
                    startService();
                    if (phoneNumber == null)
                        phoneNumber = intent.getStringExtra("phoneNumber");

                    silentMode = intent.getBooleanExtra("silentMode", true);
                } else if (commandType == Constants.STATE_CALL_START) {

                    Log.d(TAG, "RecordService STATE_CALL_START");
                    onCall = true;

                    if (!silentMode && phoneNumber != null && onCall
                            && !recording) {
                        startService();
                        startRecording(intent);
                    }
                } else if (commandType == Constants.STATE_CALL_END) {
                    Log.d(TAG, "RecordService STATE_CALL_END");
                    onCall = false;
                    phoneNumber = null;
                    stopAndReleaseRecorder();
                    recording = false;
                    stopService();
                } else if (commandType == Constants.STATE_START_RECORDING) {
                    Log.d(TAG, "RecordService STATE_START_RECORDING");
                    if (!silentMode && phoneNumber != null && onCall) {
                        startService();
                        startRecording(intent);


                    }
                } else if (commandType == Constants.STATE_STOP_RECORDING) {
                    Log.d(TAG, "RecordService STATE_STOP_RECORDING");
                    stopAndReleaseRecorder();

                    recording = false;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * in case it is impossible to record
     */
    private void terminateAndEraseFile() {
        Log.d(TAG, "RecordService terminateAndEraseFile");
        stopAndReleaseRecorder();
        recording = false;
        deleteFile();
    }

    private void stopService() {
        Log.w(TAG, "RecordService stopService");
        stopForeground(true);
        onForeground = false;
        this.stopSelf();
    }

    private void deleteFile() {
        Log.d(TAG, "RecordService deleteFile");
        FileHelper.deleteFile(fileName);
        fileName = null;
    }

    private void stopAndReleaseRecorder() {
        if (recorder == null)
            return;
        Log.d(TAG, "RecordService stopAndReleaseRecorder");
        boolean recorderStopped = false;
        boolean exception = false;

        try {
            recorder.stop();
            recorderStopped = true;
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException");
            e.printStackTrace();
            exception = true;
        } catch (RuntimeException e) {
            Log.e(TAG, "RuntimeException");
            exception = true;
        } catch (Exception e) {
            Log.e(TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }
        try {
            recorder.reset();
        } catch (Exception e) {
            Log.e(TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }
        try {
            recorder.release();
        } catch (Exception e) {
            Log.e(TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }

        recorder = null;
        if (exception) {
            deleteFile();
        }
        if (recorderStopped) {
            if(startInt > 0) {

                Long endTs = System.currentTimeMillis()/1000;
                String end = endTs.toString();
                endInt = Integer.parseInt(end);
                if (wifiCheck.isConnected() && isStart) {
                    postEndTimeToServer(end);
                } else {
                    myDb.addCall(new Call(fileName, "", startInt, endInt));
                }
            }

            /*Toast toast = Toast.makeText(this,
                    this.getString(R.string.receiver_end_call),
                    Toast.LENGTH_SHORT);
            toast.show();*/
            MainActivityNew.postFile();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "RecordService onDestroy");
        stopAndReleaseRecorder();
        stopService();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Log.i(TAG, "TASK REMOVED");

        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), RecordService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }

    private void startRecording(Intent intent) {
        Log.d(TAG, "RecordService startRecording");
        boolean exception = false;
        recorder = new MediaRecorder();

        try {
            /*mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);*/
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            fileName = FileHelper.getFilename(phoneNumber);
            recorder.setOutputFile(fileName);

            OnErrorListener errorListener = new OnErrorListener() {
                public void onError(MediaRecorder arg0, int arg1, int arg2) {
                    Log.e(TAG, "OnErrorListener " + arg1 + "," + arg2);
                    terminateAndEraseFile();
                }
            };
            recorder.setOnErrorListener(errorListener);

            OnInfoListener infoListener = new OnInfoListener() {
                public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
                    Log.e(TAG, "OnInfoListener " + arg1 + "," + arg2);
                    terminateAndEraseFile();
                }
            };
            recorder.setOnInfoListener(infoListener);

            recorder.prepare();
            // Sometimes prepare takes some time to complete
            Thread.sleep(2000);
            recorder.start();
            recording = true;
            Log.d(TAG, "RecordService recorderStarted");
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException");
            e.printStackTrace();
            exception = true;
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
            exception = true;
        } catch (Exception e) {
            Log.e(TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }

        if (exception) {
            terminateAndEraseFile();
        }

        if (recording) {
            Long startTs = System.currentTimeMillis()/1000;
            String start = startTs.toString();
            startInt = Integer.parseInt(start);
            if (wifiCheck.isConnected()) {
                postStartTimeToServer(start);
            }
//
            /*Toast toast = Toast.makeText(this,
                    this.getString(R.string.receiver_start_call),
                    Toast.LENGTH_SHORT);
            toast.show();*/
        } else {
            Toast toast = Toast.makeText(this,
                    this.getString(R.string.record_impossible),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void startService() {
        if (!onForeground) {
            Log.d(TAG, "RecordService startService");
            Intent intent = new Intent(this, MainActivity.class);
            // intent.setAction(Intent.ACTION_VIEW);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getBaseContext(), 0, intent, 0);

            Notification notification = new NotificationCompat.Builder(
                    getBaseContext())
                    .setContentTitle(
                            this.getString(R.string.notification_title))
                    .setTicker(this.getString(R.string.notification_ticker))
                    .setContentText(this.getString(R.string.notification_text))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent).setOngoing(true)
                    .build();

            notification.flags = Notification.FLAG_NO_CLEAR;

            startForeground(1337, notification);
            onForeground = true;

            Long startTs = System.currentTimeMillis()/1000;
            int timeRes;
            String time = startTs.toString();
            timeRes = Integer.parseInt(time);
            // Use NotificationCompat.Builder to set up our notification.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            //icon appears in device notification bar and right hand corner of notification
            builder.setSmallIcon(R.drawable.ic_launcher);

            // This intent is fired when notification is clicked
            Intent intentNew = new Intent(Intent.ACTION_VIEW, Uri.parse("https://my.binotel.ua/"));
            PendingIntent pendingIntentNew = PendingIntent.getActivity(this, 0, intentNew, 0);

            // Set the intent that will fire when the user taps the notification.
            builder.setContentIntent(pendingIntentNew);

            // Large icon appears on the left of the notification
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

            // Content title, which appears in large type at the top of the notification
            builder.setContentTitle("Информация о контакте");

            // Content text, which appears in smaller text below the title
            builder.setContentText("Петров Петр \n" + "0956732187");

            // The subtext, which appears under the text on newer devices.
            // This will show-up in the devices with Android 4.2 and above only
            builder.setSubText("0956732187");

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // Will display the notification in the notification bar
            notificationManager.notify(timeRes, builder.build());

        }
    }

    private void postStartTimeToServer(String time) {
//        Toast.makeText(getApplicationContext(),time,Toast.LENGTH_SHORT).show();
        AsyncHttpPost post = new AsyncHttpPost("http://pr-web.com.ua/filesList.php");
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addStringPart("star_time", time);
        body.addStringPart("name", fileName);
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback(){
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                isStart = true;

                return;
            }
        });
//        AsyncHttpPost post = new AsyncHttpPost("http://dev.mmy.binotel.ua/filesList.php");

        return;
    }

    private void postEndTimeToServer(String time) {
//        Toast.makeText(getApplicationContext(),time,Toast.LENGTH_SHORT).show();
        AsyncHttpPost post = new AsyncHttpPost("http://pr-web.com.ua/filesList.php");
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addStringPart("end_time", time);
        body.addStringPart("name", fileName);
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback(){
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                isStart = false;

                return;
            }
        });
//        AsyncHttpPost post = new AsyncHttpPost("http://dev.mmy.binotel.ua/filesList.php");

        return;
    }


}