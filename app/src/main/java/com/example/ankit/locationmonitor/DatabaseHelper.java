package com.example.ankit.locationmonitor;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Date;

/**
 * Created by ankit on 10-02-2018.
 */

public class DatabaseHelper extends  SQLiteOpenHelper{
    public static final String DATABASE_NAME="gpsdata.db";
    public static final String TABLE_NAME="gps_table";
    public static final String COL_1="ID";
    public static final String COL_2="LAT";
    public static final String COL_3="LONG";
    public static final String COL_4="TIME";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db=this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" create table "+TABLE_NAME+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT,LAT DOUBLE,LONG DOUBLE,TIME TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
    }


    //SQLiteDatabase db1=this.getWritableDatabase();
    //Cursor res7=db1.rawQuery("select LAT,LONG,TIME from "+TABLE_NAME,null);
    public boolean insertData(double lat,double longi,String time){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
//        double scale = Math.pow(10, places);
//        lat=Math.round(lat*scale)/scale;
//        longi=Math.round(longi*scale)/scale;

        contentValues.put(COL_2,lat);
        contentValues.put(COL_3,longi);
        contentValues.put(COL_4,time);
        long result=db.insert(TABLE_NAME,null,contentValues);

        if(result==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public void ViewData(Context cont){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME,null);


        if(res.getCount()==0){
            showMessage("error","nothing found", cont);

            return;
        }
        StringBuffer buffer=new StringBuffer();
        while(res.moveToNext()){
            buffer.append("ID: "+res.getDouble(0)+"\n");
            buffer.append("LAT: "+res.getDouble(1)+"\n");
            buffer.append("LONG: "+res.getDouble(2)+"\n");
            buffer.append("TIME: "+res.getString(3)+"\n\n");
        }
        showMessage("Data",buffer.toString(), cont);
    }


    class MyObj{
        public double lat, longt;
        public String address, time, time2,loc1;
        public MyObj(double l, double lo, String add, String t, String loc){
            lat=l;
            longt=lo;
            address=add;
            time=t;
            loc1=loc;
        }
        public void setLeaveTime(String time2){
            this.time2=time2;
        }
        public String getTimeWithTotalTime(){
            if(time2!=null)
                return time +" to "+time2;
            else
                return time;
        }
    }

    public ArrayList<MyObj> normalizeData(Context cont, String date){

        ArrayList<MyObj> arrayList=new ArrayList<>();


        SQLiteDatabase db=this.getWritableDatabase();

        Cursor res=null;
        if(date==null)
            res=db.rawQuery("select * from "+TABLE_NAME,null);
        else
            res=db.rawQuery("select * from "+TABLE_NAME+" WHERE TIME LIKE '%"+date+"%'",null);

        if(res.getCount()==0){
            showMessage("Error","Select Valid Date", cont);            return arrayList;
        }
        MyObj temp=null;
        while(res.moveToNext()){
            double avgLat=res.getDouble(1);
            double avgLong=res.getDouble(2);
            String time=res.getString(3);
            String s=LocationFinder(avgLat,avgLong, cont);
            String loc=LocationFinder1(avgLat,avgLong, cont);
            if(temp!=null)
                temp.setLeaveTime(time);
            temp = new MyObj(avgLat, avgLong, s, time,loc);
            arrayList.add(temp);
        }

        return arrayList;
    }

    public String LocationFinder(double avgLat, double avgLong, Context cont) {

        String strAdd = "";//Loc="";
        Geocoder gc = new Geocoder(cont, Locale.getDefault());
        try {
            List<Address> addresses= gc.getFromLocation(avgLat,avgLong,1);
            if (addresses!=null){
                Address returnedAddress=addresses.get(0);

                StringBuilder strReturnedAddress = new StringBuilder("");
                //StringBuilder strLOC = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");

                    //strLOC.append(returnedAddress.getLocality()).append("\n");
                    //strReturnedAddress.append(returnedAddress.getPostalCode()).append("\n");
                    //strReturnedAddress.append(returnedAddress.getCountryName());

                }

                strAdd = strReturnedAddress.toString();
               // Loc=strLOC.toString();
            }

            else{
                Log.i("TAG", "No Address returned!");
            }

            //Log.i("TAG", "Reached 3 : "+strAdd.toString());

            return strAdd.toString();

        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return strAdd.toString();
    }

    //new added
    public String LocationFinder1(double avgLat, double avgLong, Context cont) {

        String strAdd = "";//Loc="";
        Geocoder gc = new Geocoder(cont, Locale.getDefault());
        try {
            List<Address> addresses= gc.getFromLocation(avgLat,avgLong,1);
            if (addresses!=null){
                Address returnedAddress=addresses.get(0);

                StringBuilder strReturnedAddress = new StringBuilder("");
                StringBuilder strLOC = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getSubLocality()).append("\n");

                   // strLOC.append(returnedAddress.getLocality()).append("\n");
                    //strReturnedAddress.append(returnedAddress.getPostalCode()).append("\n");
                    //strReturnedAddress.append(returnedAddress.getCountryName());

                }

                strAdd = strReturnedAddress.toString();
                //Loc=strLOC.toString();
            }

            else{
                Log.i("TAG", "No Address returned!");
            }

            //Log.i("TAG", "Reached 3 : "+strAdd.toString());

            return strAdd.toString();

        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return strAdd.toString();
    }



    public void showMessage(String title,String message, Context cont){
        AlertDialog.Builder builder=new AlertDialog.Builder(cont);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void deleteData(String id,Context cont){

        SQLiteDatabase db=this.getWritableDatabase();
        Integer deletedRows=db.delete(TABLE_NAME,"ID=?",new String[] {id});
        if(deletedRows>0){
            Toast.makeText(cont,"data deleted",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(cont,"data not deleted",Toast.LENGTH_SHORT).show();
        }
    }

}
