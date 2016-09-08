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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


    //Explicit SQLite
    private LogGPS objLogGPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindWidget();

        setupLocation();

        connectedSQLite();

    }// onCreate

    private void connectedSQLite(){
        objLogGPS = new LogGPS(this);
    }

    public void clickSaveData(View view) {

        String strStamp = plateEditText.getText().toString().trim();

        if (strStamp.equals("")) {
            Toast.makeText(this, "กรุณากรอกชื่อ สถานที่ด้วย คะ",
                    Toast.LENGTH_SHORT).show();
        } else {

            String strLat = latTextView.getText().toString();
            String strLng = lngTextView.getText().toString();

            updateValueToSQLite( strLat, strLng,strStamp);

        }

    }   // clickSaveData


    public void updateValueToSQLite( String strLat, String strLng,String strStamp) {
        objLogGPS.addTransactionGPS(strLat, strLng, strStamp);
       Toast.makeText(this, "Save  Success",Toast.LENGTH_SHORT).show();

    }

//    private void updateValueToSQLite(String strName, String strLat, String strLng) {
//
//        OkHttpClient okHttpClient = new OkHttpClient();
//        RequestBody requestBody = new FormEncodingBuilder()
//                .add("isAdd", "true")
//                .add("Name", strName)
//                .add("Lat", strLat)
//                .add("Lng", strLng)
//                .build();
//        Request.Builder builder = new Request.Builder();
//        Request request = builder.url("http://swiftcodingthai.com/watch/php_add_plate.php").post(requestBody).build();
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                finish();
//            }
//        });
//
//
//    }   // updateValue


    private void autoUpdate() {

        timeAnInt += 1;


        //Change Policy
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        //Get Current Time
       // DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
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
        }, 10);   // Mil Second
    }

    @Override
    protected void onResume() {
        super.onResume();


        // locationManager.removeUpdates(locationListener);

        String strLat = "Unknown";
        String strLng = "Unknown";

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

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String getTimeDate = dateFormat.format(date);

        latTextView.setText(strLat);
        lngTextView.setText(strLng);
        plateEditText.setText(getTimeDate.toString());

    }   // onResume

    @Override
    protected void onStart() {
        super.onStart();

        GPSABoolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!GPSABoolean) {
            networkABoolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!networkABoolean) {
                Log.d("GPS", "Cannot Find Location");
            }

        }


    }

    @Override
    protected void onStop() {
        super.onStop();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);

    }

    public Location requestLocation(String strProvider, String strError) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
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


    private void setupLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);


    }   // setupLocation

    private void bindWidget() {
        latTextView = (TextView) findViewById(R.id.textView4);
        lngTextView = (TextView) findViewById(R.id.textView6);
        plateEditText = (EditText) findViewById(R.id.editText);
    }

}
