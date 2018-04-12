package com.example.ankit.locationmonitor;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    //GPS_Service gs;
    public Button btn_start,btn_stop,btn_view,btn_delete,btn_normalize,btn_map;
    public TextView textView1;
    public BroadcastReceiver broadcastReceiver;
    public EditText editid  ;


    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver==null) {
            broadcastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    textView1.append("\n"+intent.getExtras().get("coordinates"));
                }
            };

        }
        registerReceiver(broadcastReceiver ,new IntentFilter("location_Update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb=new DatabaseHelper(this);
        //gs=new GPS_Service(this);


        btn_start=(Button)findViewById(R.id.button1);
        btn_stop=(Button)findViewById(R.id.button2);
        btn_view=(Button)findViewById(R.id.button3);
        btn_delete = (Button)findViewById((R.id.button5));
        btn_normalize=(Button)findViewById((R.id.button4));
        btn_map=(Button)findViewById((R.id.button6)) ;

        textView1=(TextView)findViewById(R.id.textView);
        editid=(EditText)findViewById((R.id.editTextId));
        if(!runtime_permissions());
            enable_buttons();


        normalize();
        viewAll();
        deleteAll();
        showMap();
    }

    public void normalize(){
        btn_normalize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.normalizeData(MainActivity.this, null);
            }
        });
    }

    public void deleteAll() {
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.deleteData(editid.getText().toString(),MainActivity.this);
            }
        });
    }

    public void viewAll(){
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.ViewData(MainActivity.this);
            }
        });
    }

    public void showMap(){
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

    }






    private void enable_buttons(){
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),GPS_Service.class);
                stopService(i);
            }
        });
    }

    private boolean runtime_permissions(){
        if(Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }
            else{
                runtime_permissions();
            }
        }
    }

    public static String getDate(){
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss ");
        String dat = df.format(Calendar.getInstance().getTime());
        return dat;
    }

}
