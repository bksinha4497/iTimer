package com.example.ankit.locationmonitor;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.example.ankit.locationmonitor.R.*;
import static com.example.ankit.locationmonitor.R.id.*;
import android.app.Dialog;
import android.graphics.Color;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    public final static int MY_PERMISSION_FINE_LOCATION = 101;
    public Button mark, clear;
    public Button btn_date,bt_sat;
    int year_x,month_x,day_x;
    static final int Dialog_id=0;
    ZoomControls zoom;

    public MapsActivity() {

    }

    public MapsActivity(DatabaseHelper databaseHelper) {


    }
    public void showDialogOnDateClick()
    {
        btn_date=(Button)findViewById(id.bt_date);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(Dialog_id);
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id==Dialog_id)
            return new DatePickerDialog(this,dpickerListner ,year_x,month_x,day_x);
            return null;

    }

    private DatePickerDialog.OnDateSetListener dpickerListner=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            setDatedMarker();
        }
    };
    private void setDatedMarker(){
        String date = String.format("%02d", day_x)+"."+String.format("%02d", month_x)+"."+String.format("%04d", year_x);
        Toast.makeText(MapsActivity.this,date,Toast.LENGTH_SHORT).show();
        addMarkers(date);
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
        bt_sat=(Button)findViewById(id.btSatellite);
        bt_sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });


        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);
        //Log.i("TAG","day of month "+day_x);
        showDialogOnDateClick();
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

        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);
        setDatedMarker();

    }

    private void addMarkers(String date) {

        //TODO Get all marker positions
        mMap.clear();

        DatabaseHelper db = new DatabaseHelper(this);
        ArrayList<DatabaseHelper.MyObj> myObjs = db.normalizeData(this, date);

        LatLng locations = null;
        /*for(DatabaseHelper.MyObj obj : myObjs){
            locations = new LatLng(obj.lat, obj.longt);
            mMap.addMarker(new MarkerOptions().position(locations).title(obj.address+" \n "+obj.time));
        }*/


        for (int i = 0; i < myObjs.size(); i++) {
            locations = new LatLng(myObjs.get(i).lat, myObjs.get(i).longt);
            mMap.addMarker(new MarkerOptions().position(locations).title(myObjs.get(i).address).snippet(myObjs.get(i).getTimeWithTotalTime()));
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

        Log.i("TAG", "Reached 1");
        List<Address> list=gc.getFromLocationName(location,1);
        Log.i("TAG", "Reached 2 : "+list.size());
        Address address=list.get(0);
        //final String locality=address.getLocality();
        final String locality=address.getAddressLine(0);
        Toast.makeText(this,locality, Toast.LENGTH_LONG).show();
        final double lat=address.getLatitude();
        final double lng=address.getLongitude();

        Log.i("TAG", "Reached 3 : "+lat+" , "+lng);

        goToLocationZoom(lat,lng,15);

        Log.i("TAG", "Reached 4");
        mark=(Button)findViewById(R.id.btMark);
        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarkers(locality,lat,lng);

            }
        });

        Log.i("TAG", "Reached 5");
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
