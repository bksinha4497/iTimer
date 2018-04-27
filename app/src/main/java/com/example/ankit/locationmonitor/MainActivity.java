package com.example.ankit.locationmonitor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.app.NotificationCompat;
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

public class MainActivity extends Activity {
    DatabaseHelper myDb;
    public Button btn_start,btn_stop,btn_view,btn_delete,btn_map,btn_graph;
    public TextView textView1;
    public BroadcastReceiver broadcastReceiver;
    public EditText editid  ;
    LocationManager locationManager;



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



        btn_start=(Button)findViewById(R.id.button1);
        btn_stop=(Button)findViewById(R.id.button2);
        btn_view=(Button)findViewById(R.id.button3);
        btn_delete = (Button)findViewById((R.id.button5));
        btn_map=(Button)findViewById((R.id.button6)) ;
        btn_graph=(Button) findViewById(R.id.button7);

        textView1=(TextView)findViewById(R.id.textView);
        editid=(EditText)findViewById((R.id.editTextId));
        if(!runtime_permissions());
            enable_buttons();



        viewAll();
        deleteAll();
        showMap();
        graph();

    }

    public void graph()
    {
        btn_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent g = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(g);
            }});

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
                //new
                locationManager=(LocationManager)MainActivity.this.getSystemService(LOCATION_SERVICE);
                boolean isGpsON;
                isGpsON =locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(isGpsON) startService(i);
                else  {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                   // Toast.makeText(MainActivity.this,"GPS is OFF",Toast.LENGTH_SHORT).show();

                }

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
