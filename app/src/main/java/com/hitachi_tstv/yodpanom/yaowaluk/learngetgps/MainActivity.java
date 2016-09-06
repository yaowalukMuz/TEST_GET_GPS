package com.hitachi_tstv.yodpanom.yaowaluk.learngetgps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    //Explicit
    private TextView latTextView, lngTextView;
    private EditText plateEditText;

    private LocationManager locationManager;
    private Criteria criteria;
    private boolean GPSABoolean, networkABoolean;
    private int timeAnInt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindWidget();

        setupLocation();

    }

    private void autoUpdate() {

        timeAnInt += 1;


        //Change Policy
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        //Get Current Time
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String getTimeDate = dateFormat.format(date);

        Log.d("Test", "Time ==> " + timeAnInt + " = " + getTimeDate);

        myLoop();


    }   // autoUpdate

    private void myLoop() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoUpdate();
            }
        }, 1000);   // Mil Second
    }




    @Override
    protected void onResume() {
        super.onResume();
        //locationManager.removeUpdates(locationListener);
        locationManager.removeUpdates(locationListener);
        String strLat = "Unknow";
        String strLng = "Unknow";

        Location networkLocation = requestLocation(LocationManager.NETWORK_PROVIDER, "No Internet");
        if (networkLocation != null) {
            strLat = String.format("%.7f", networkLocation.getLatitude());
            strLng = String.format("%.7f", networkLocation.getLongitude());
        }

        Location gpsLocation = requestLocation(LocationManager.GPS_PROVIDER, "No GPS card");
        if (gpsLocation != null) {
            strLat = String.format("%.7f", gpsLocation.getLatitude());
            strLng = String.format("%.7f", gpsLocation.getLongitude());
        }

        latTextView.setText(strLat);
        lngTextView.setText(strLng);
    }

    @Override
    protected void onStop() {
        super.onStop();

        locationManager.removeUpdates(locationListener);

    }


    public Location requestLocation(String strProvider, String strError) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {


            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        } else {
            Log.d("GPS", strError);
        }


        return location;
    }



    public final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latTextView.setText(String.format("%.7f", location.getLatitude()));
            lngTextView.setText(String.format("%.7f", location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };






    private void bindWidget(){
        latTextView = (TextView) findViewById(R.id.textView5);
        lngTextView = (TextView) findViewById(R.id.textView6);
        plateEditText = (EditText) findViewById(R.id.editText);
    }

    private void setupLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
    }
}
