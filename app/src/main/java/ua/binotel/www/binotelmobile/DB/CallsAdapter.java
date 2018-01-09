package ua.binotel.www.binotelmobile.DB;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ua.binotel.www.binotelmobile.Constants;
import ua.binotel.www.binotelmobile.FileHelper;
import ua.binotel.www.binotelmobile.Model;
import ua.binotel.www.binotelmobile.R;

public class CallsAdapter extends ArrayAdapter<Model> {

    private final Context context;
    private List<Model> list;
    private MediaPlayer   mPlayer = null;
    private static String mFileName = null;

    public CallsAdapter(Context context, List<Model> list) {

        super(context, R.layout.callrow, list);
        this.list = list;
        this.context = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater
                .inflate(R.layout.rowlayout, parent, false);
        final TextView textView = (TextView) rowView
                .findViewById(R.id.label_list);
        final TextView textView2 = (TextView) rowView
                .findViewById(R.id.label_list_2);
        // final ImageView imgDelete =
        // (ImageView)rowView.findViewById(R.id.img_delete);
        String getCallName = list.get(position).getCallName();
        String myDateStr = getCallName.substring(1, 15);
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyyMMddkkmmss");

        Date dateObj = new Date();
        try {
            dateObj = curFormater.parse(myDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        textView2.setText(DateFormat.getDateInstance().format(dateObj) + " "
                + DateFormat.getTimeInstance().format(dateObj));
        String myPhone = getCallName.substring(16, getCallName.length() - 4);

        if (!myPhone.matches("^[\\d]{1,}$")) {
            myPhone = context.getString(R.string.withheld_number);
        } else if (list.get(position).getUserNameFromContact() != myPhone) {
            myPhone = list.get(position).getUserNameFromContact();
        }

        textView.setText(myPhone);

        return rowView;
    }


}
