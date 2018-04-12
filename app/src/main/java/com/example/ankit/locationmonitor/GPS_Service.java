package com.example.ankit.locationmonitor;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * Created by ankit on 10-02-2018.
 */

public class GPS_Service extends Service {
    DatabaseHelper myDb;
    public Button view_all;
    private LocationListener listener;
    private LocationManager locationManager;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        myDb=new DatabaseHelper(this);
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i=new Intent("location_Update");
                i.putExtra("coordinates",location.getLatitude()+"  "+location.getLongitude());
                sendBroadcast(i);
                String date=MainActivity.getDate();
                fetchingDetails(location,date);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(i);

            }


        };
        locationManager=(LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60000,100,listener);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(locationManager!=null){
            locationManager.removeUpdates(listener);
        }
    }

    public void fetchingDetails(Location location,String dat){
        boolean isInserted=myDb.insertData(location.getLatitude(),location.getLongitude(),dat);

        if(isInserted==true){
            Toast.makeText(GPS_Service.this,"Data inserted",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(GPS_Service.this, "Data not inserted ", Toast.LENGTH_SHORT).show();
        }
    }



}
