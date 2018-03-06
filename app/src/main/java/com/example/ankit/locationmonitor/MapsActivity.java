package com.example.ankit.locationmonitor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.ankit.locationmonitor.R.*;
import static com.example.ankit.locationmonitor.R.id.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    public final static int MY_PERMISSION_FINE_LOCATION = 101;
    public Button mark;
    ZoomControls zoom;
    //MapsActivity myMaps;

    public MapsActivity(){

    }

    public MapsActivity(DatabaseHelper databaseHelper) {
        //this.myMaps = myMaps;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        zoom = (ZoomControls) findViewById(zcZoom);
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());

            }
        });
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());

            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public  void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }

        addMarkers();
    }

    private void addMarkers(){

        //TODO Get all marker positions

        DatabaseHelper db=new DatabaseHelper(this);
        ArrayList<DatabaseHelper.MyObj> myObjs = db.normalizeData(this);

        LatLng locations=null;
        /*for(DatabaseHelper.MyObj obj : myObjs){
            locations = new LatLng(obj.lat, obj.longt);
            mMap.addMarker(new MarkerOptions().position(locations).title(obj.address+" \n "+obj.time));
        }*/



        for(int i=0;i<myObjs.size();i++){
            locations=new LatLng(myObjs.get(i).lat,myObjs.get(i).longt);
            mMap.addMarker(new MarkerOptions().position(locations).title(myObjs.get(i).address).snippet(myObjs.get(i).time));
        }



        if(locations!=null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locations));
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        mMap.setMyLocationEnabled(true);
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"this app requires location permissions to be granted",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }
    }

    public void goToLocationZoom(double lat,double lng,float zoom){
        LatLng l1=new LatLng(lat,lng);
        CameraUpdate update=CameraUpdateFactory.newLatLngZoom(l1,zoom);
        mMap.moveCamera(update);
    }
    Marker marker;

    public void geoLocate(View view) throws IOException {
        EditText et=(EditText)findViewById(R.id.etLocationEntry);
        String location=et.getText().toString();
        Geocoder gc=new Geocoder(this);


        List<Address> list=gc.getFromLocationName(location,1);
        Address address=list.get(0);
        final String locality=address.getLocality();
        Toast.makeText(this,locality, Toast.LENGTH_LONG).show();
        final double lat=address.getLatitude();
        final double lng=address.getLongitude();
        goToLocationZoom(lat,lng,15);

        mark=(Button)findViewById(R.id.btMark);
        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarkers(locality,lat,lng);

            }
        });
    }

    public void setMarkers(String locality,double lat,double lng){
        /*if(marker!=null){
            marker.remove();
        }*/
        MarkerOptions options=new MarkerOptions().title(locality)
                                                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                                 .position(new LatLng(lat,lng)).snippet("new place");
        marker=mMap.addMarker(options);
    }
}
