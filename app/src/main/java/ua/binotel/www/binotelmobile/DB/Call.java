package ua.binotel.www.binotelmobile.DB;


import java.sql.Timestamp;

public class Call {

    //private variables
    int _id;
    String _name;
    String _phone_number;
    int _start;
    int _end;

    // Empty constructor
    public Call(){

    }
    // constructor
    public Call(int id, String name, String _phone_number, int _start, int _end){
        this._id = id;
        this._name = name;
        this._phone_number = _phone_number;
        this._start = _start;
        this._end = _end;
    }

    // constructor
    public Call(String name, String _phone_number, int _start, int _end){
        String[] separated = name.split("/");
        String fileName = separated[separated.length-1];
        String[] fileNameShort = fileName.split("\\.");
        String[] phoneNumber = fileNameShort[0].split("p");
        this._name = fileNameShort[0].toString();
        this._phone_number = phoneNumber[1].toString();
        this._start = _start;
        this._end = _end;
    }

    // getting start call
    public int getStart() {
        return this._start;
    }

    // setting end call
    public void setStart(int start) {
        this._start = start;
    }

    // getting end call
    public int getEnd() {
        return this._end;
    }

    // setting end call
    public void setEnd(int end) {
        this._end = end;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting phone number
    public String getPhoneNumber(){
        return this._phone_number;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number){
        this._phone_number = phone_number;
    }
}
