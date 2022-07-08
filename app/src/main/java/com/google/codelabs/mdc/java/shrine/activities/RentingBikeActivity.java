package com.google.codelabs.mdc.java.shrine.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.entities.BikeInfo;
import com.google.codelabs.mdc.java.shrine.socket.SocketClient;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;
import com.google.gson.Gson;

import java.util.Locale;

public class RentingBikeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    MyStorage myStorage;
    SocketClient socketClient;
    Marker marker;
    BikeInfo bikeInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renting_bike);

        socketClient = new SocketClient(this);
        myStorage = new MyStorage(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map_renting_bike);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Gson gson = Common.getMyGson();
        bikeInfo = gson.fromJson(myStorage.get(Constant.BIKE_INFO), BikeInfo.class);
        socketClient.subscriberStompCheckEndRenting(bikeInfo.getId());


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;


        LatLng latLng = new LatLng(bikeInfo.getLatitude(), bikeInfo.getLongitude());
        moveCamera(latLng, 12);
        settingUiGoogleMap();
        socketClient.subscriberStompUpdateLatLongBike(bikeInfo.getId(), googleMap, marker);
        runTimer();
    }

    private void moveCamera(LatLng latLng, int zoom){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void settingUiGoogleMap() {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (!checkPermission()) {
            askPermission();
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                Constant.REQUEST_LOCATION
        );
    }

    private int seconds = 0;
    private void runTimer() {

        // Get the text view.
        final TextView timeView = (TextView)findViewById(R.id.time_view);

        // Creates a new Handler
        final Handler handler = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);

                // Set the text view text.
                timeView.setText(time);

                // increment the seconds variable.

                seconds++;
                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }

}
