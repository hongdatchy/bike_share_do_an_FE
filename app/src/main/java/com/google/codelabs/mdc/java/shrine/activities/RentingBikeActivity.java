package com.google.codelabs.mdc.java.shrine.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.codelabs.mdc.java.shrine.api.ApiService;
import com.google.codelabs.mdc.java.shrine.entities.BikeInfo;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.codelabs.mdc.java.shrine.socket.SocketClient;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyProgressDialog;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;
import com.google.gson.Gson;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentingBikeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    MyStorage myStorage;
    SocketClient socketClient;
    Marker marker;
    BikeInfo bikeInfo;
    ImageButton imageButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renting_bike);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map_renting_bike);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        imageButton = findViewById(R.id.stop_button);


        socketClient = new SocketClient(this);
        myStorage = new MyStorage(this);

        if(myStorage.get(Constant.statusLockWhenRenting).equals("")){
            myStorage.save(Constant.statusLockWhenRenting, "open");
        }
        if(myStorage.get(Constant.statusLockWhenRenting).equals("open")) {
                imageButton.setImageResource(R.drawable.stop_icon);
        } else{
            imageButton.setImageResource(R.drawable.play_icon);;
        }

        Gson gson = Common.getMyGson();
        bikeInfo = gson.fromJson(myStorage.get(Constant.BIKE_INFO), BikeInfo.class);
        onCLickButton();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap mMap) {
        this.googleMap = mMap;
        LatLng latLng = new LatLng(bikeInfo.getLatitude(), bikeInfo.getLongitude());
        moveCamera(latLng, 12);
        settingUiGoogleMap();
        socketClient.subscriberStompUpdateLatLongBikeOrCheckEndRenting(bikeInfo.getId()
                , googleMap, marker, imageButton);
        runTimer();
    }

    private void onCLickButton(){
        imageButton.setOnClickListener(view -> {
            imageButton.setClickable(false);
            if(myStorage.get(Constant.statusLockWhenRenting).equals("open")){
                socketClient.callApiEndRentBike(bikeInfo.getId(), myStorage.get(Constant.TOKEN_KEY));
            } else {
                callApiContinueRentBike(bikeInfo.getId(), myStorage.get(Constant.TOKEN_KEY));
            }
        });
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

        final TextView timeView = findViewById(R.id.time_view);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes and seconds.
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);
                // Set the text view text.
                timeView.setText(time);
                // increment the seconds variable.
                seconds++;
                // Post the code again with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void callApiContinueRentBike(int bikeId, String token) {
        MyProgressDialog myProgressDialog = new MyProgressDialog(this);
        myProgressDialog.show();

        ApiService.apiService.continueRentBike(bikeId, token).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                if (response.isSuccessful()) {
                    MyResponse myResponse = response.body();
                    assert myResponse != null;
                    if(!myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                        Toast.makeText(RentingBikeActivity.this,"Tiếp tục thuê xe thất bại",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RentingBikeActivity.this,"Phiên làm việc đã hết hạn",Toast.LENGTH_SHORT).show();
                    socketClient.unSubscribe();
                    Common.switchActivity(RentingBikeActivity.this, MainActivity.class);
                }

                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Toast.makeText(RentingBikeActivity.this,"Call api fail",Toast.LENGTH_SHORT).show();
                myProgressDialog.dismiss();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
