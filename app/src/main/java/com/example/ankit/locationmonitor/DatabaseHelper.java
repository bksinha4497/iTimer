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


    public boolean insertData(double lat,double longi,String time,int places){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        double scale = Math.pow(10, places);
        lat=Math.round(lat*scale)/scale;
        longi=Math.round(longi*scale)/scale;
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
        public String address, time;
        public MyObj(double l, double lo, String add, String t){
            lat=l;
            longt=lo;
            address=add;
            time=t;
        }
    }

    public ArrayList<MyObj> normalizeData(Context cont){

        ArrayList<MyObj> arrayList=new ArrayList<>();

        double avgLat=0,avgLong=0;
        int m=1;
        String FirstTime="";
        SQLiteDatabase db=this.getWritableDatabase();
        DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss ");
        DateFormat df2 = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss ");
        Cursor res1=db.rawQuery("select LAT,LONG,TIME from "+TABLE_NAME,null);
        Cursor res3=db.rawQuery("select LAT,LONG,TIME from "+TABLE_NAME,null);


        if(res1.getCount()==0){
            showMessage("error","nothing found", cont);
            return null;
        }
        res1.moveToPosition(0);

        StringBuffer buffer=new StringBuffer();
        avgLat=res1.getDouble(0);
        avgLong=res1.getDouble(1);
        FirstTime=res1.getString(2);
        buffer.append("LAT: "+avgLat+"\n");
        buffer.append("LONG: "+avgLong+"\n");
        buffer.append("FIRST_TIME: "+FirstTime+"\n");
        String s1=LocationFinder(avgLat,avgLong, cont);
        buffer.append("Address: "+s1+"\n");
        arrayList.add(new MyObj(avgLat, avgLong, s1, FirstTime));

        res3.moveToPosition(0);

        while(res3.moveToNext()){
            m++;
            Cursor res2=db.rawQuery("select LAT,LONG,TIME from "+TABLE_NAME,null);
            int n=1,flag=1;


            while(n<m){
                n++;
                res2.moveToNext();        //pointer will point to first row
                Location startPoint=new Location("locationA");
                startPoint.setLatitude(res2.getDouble(0));
                startPoint.setLongitude(res2.getDouble(1));

                Location endPoint=new Location("locationA");
                endPoint.setLatitude(res3.getDouble(0));
                endPoint.setLongitude(res3.getDouble(1));

                double distance=startPoint.distanceTo(endPoint);
                //Log.i("TAG","the distance in metres is "+distance);

                /*long diff;
                long elapsedDays;
                try {
                    Date date1 = df1.parse(res2.getString(2));
                    Date date2 = df2.parse(res3.getString(2));
                    long days=1000*60*60*24;
                    diff = date2.getTime() - date1.getTime();
                    elapsedDays = diff / days;
                    if(distance<80){
                        flag=0;
                        if(elapsedDays>1){
                            Log.i("TAG","the difference is  "+elapsedDays);

                            avgLat=res3.getDouble(0);
                            avgLong=res3.getDouble(1);
                            FirstTime=res3.getString(2);
                            buffer.append("LAT: "+avgLat+"\n");
                            buffer.append("LONG: "+avgLong+"\n");
                            buffer.append("FIRST_TIME: "+FirstTime+"\n");
                            String s=LocationFinder(avgLat,avgLong, cont);
                            buffer.append("Address: "+s+"\n");
                            arrayList.add(new MyObj(avgLat, avgLong, s, FirstTime));
                        }

                        break;
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }*/




                if(distance<80){
                    flag=0;
                    break;
                }

            }
            if(flag==1){
                avgLat=res3.getDouble(0);
                avgLong=res3.getDouble(1);
                FirstTime=res3.getString(2);
                buffer.append("LAT: "+avgLat+"\n");
                buffer.append("LONG: "+avgLong+"\n");
                buffer.append("FIRST_TIME: "+FirstTime+"\n");
                String s=LocationFinder(avgLat,avgLong, cont);
                buffer.append("Address: "+s+"\n");
                arrayList.add(new MyObj(avgLat, avgLong, s, FirstTime));
            }

        }
        showMessage("Data",buffer.toString(), cont);


        return arrayList;
    }

    public String LocationFinder(double avgLat, double avgLong, Context cont) {

        String strAdd = "";
        Geocoder gc = new Geocoder(cont, Locale.getDefault());
        try {
            List<Address> addresses= gc.getFromLocation(avgLat,avgLong,1);
            if (addresses!=null){
                Address returnedAddress=addresses.get(0);

                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    //strReturnedAddress.append(returnedAddress.getLocality()).append("\n");
                    //strReturnedAddress.append(returnedAddress.getPostalCode()).append("\n");
                    //strReturnedAddress.append(returnedAddress.getCountryName());

                }

                strAdd = strReturnedAddress.toString();
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
