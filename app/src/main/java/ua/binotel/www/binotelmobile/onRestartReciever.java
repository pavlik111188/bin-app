package ua.binotel.www.binotelmobile;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class onRestartReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        // TODO Auto-generated method stub
        // Register Services
//        registerServices(context);
//        MyWidget_2x2.registerServices(context);

        // reInitialize appWidgets
        /*AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);

        MyWidget_1x1 widget1x1=new CallWidgi();
        widget1x1.onUpdate
                (context,
                        AppWidgetManager.getInstance(context),
                        widget1x1.getIDs(context, appWidgetManager));

        MyWidget_2x2 widget2x2=new CallWidgi2();
        widget2x1.onUpdate(context,
                AppWidgetManager.getInstance(context),
                widget2x2.getIDs(context, appWidgetManager));*/
    }
}