package ua.binotel.www.binotelmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import java.util.List;

import ua.binotel.www.binotelmobile.DB.Contact;
import ua.binotel.www.binotelmobile.DB.DatabaseHandler;

public class TestActivity extends Activity {

    TextView serverTxt;

    public String test;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        serverTxt = (TextView)findViewById(R.id.server_txt);

        //showCacheToast();

        /*handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                serverTxt.addTextChangedListener( text );
            }
        };*/

        DatabaseHandler db = new DatabaseHandler(this);

        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");


        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Contact> contacts = db.getAllContacts();




        for (Contact cn : contacts) {
            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
            test = this + " " + log;


        }

        serverTxt.setText( contacts.toString() );

    }

    void showCacheToast() {
        /*boolean caching = cacher.getCaching();
        Toast.makeText(getApplicationContext(), "Caching: " + caching, Toast.LENGTH_SHORT).show();*/
    }




}
