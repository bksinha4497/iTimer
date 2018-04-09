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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.ankit.locationmonitor.R.*;
import static com.example.ankit.locationmonitor.R.id.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    public final static int MY_PERMISSION_FINE_LOCATION = 101;
    public Button mark, clear,Button ;
    ZoomControls zoom;

    public MapsActivity() {

    }

    public MapsActivity(DatabaseHelper databaseHelper) {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_maps);

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (mMap != null) {

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc=new Geocoder(MapsActivity.this);
                    LatLng l1=marker.getPosition();
                    double lat=l1.latitude;
                    double lng=l1.longitude;
                    List<Address> list=null;
                    try {
                        list=gc.getFromLocation(lat,lng,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address add=list.get(0);
                    marker.setTitle(add.getAddressLine(0));
                    marker.showInfoWindow();

                }
            });



            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvSnippet = (TextView) v.findViewById(id.tv_snippet);
                    LatLng l1 = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }


        addMarkers();

    }

    private void addMarkers() {

        //TODO Get all marker positions

        DatabaseHelper db = new DatabaseHelper(this);
        ArrayList<DatabaseHelper.MyObj> myObjs = db.normalizeData(this);

        LatLng locations = null;
        /*for(DatabaseHelper.MyObj obj : myObjs){
            locations = new LatLng(obj.lat, obj.longt);
            mMap.addMarker(new MarkerOptions().position(locations).title(obj.address+" \n "+obj.time));
        }*/


        for (int i = 0; i < myObjs.size(); i++) {
            locations = new LatLng(myObjs.get(i).lat, myObjs.get(i).longt);
            mMap.addMarker(new MarkerOptions().position(locations).title(myObjs.get(i).address).snippet(myObjs.get(i).time));
        }


        if (locations != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locations));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "this app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }
    }



    public void goToLocationZoom(double lat, double lng, float zoom) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
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
        //final String locality=address.getLocality();
        final String locality=address.getAddressLine(0);
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


        clear=(Button)findViewById(id.btClear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(marker!=null){
                    marker.remove();
                }
            }
        });

    }

    Circle circle;
    public void setMarkers(String locality,double lat,double lng){
        if(marker!=null){
            removeEverything();
        }

        MarkerOptions options=new MarkerOptions().title(locality)
                                                  .draggable(true)
                                                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                                  //.icon(BitmapDescriptorFactory.fromResource(mipmap.ic_launcher))
                                                 .position(new LatLng(lat,lng)).snippet("new place");
        marker= mMap.addMarker(options);
        circle=drawCircle(new LatLng(lat,lng));

    }

    private Circle drawCircle(LatLng latLng) {
        CircleOptions options=new CircleOptions()
                              .center(latLng)
                              .radius(1000)
                              .fillColor(0x33FF0000)
                              .strokeColor(Color.BLUE)
                              .strokeWidth(3);

        return mMap.addCircle(options);
    }

    private void removeEverything(){
        marker.remove();
        marker=null;
        circle.remove();
        circle=null;

    }


}
