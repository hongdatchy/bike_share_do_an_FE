package com.google.codelabs.mdc.java.shrine.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.gson.reflect.TypeToken;


import java.util.List;

public class MapDetailRoutesContractActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_routes_contract);
        // get gg map
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ggMapFragment);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        googleMap = gMap;
        String routes = getIntent().getStringExtra("routes");
        List<LatLng> listPoint = Common.getMyGson().fromJson(routes, new TypeToken<List<LatLng>>(){}.getType());
        if(!listPoint.isEmpty()){
            LatLng centerPointInRoutes = listPoint.get(listPoint.size()/2);
            drawRoute(listPoint);
            addMaker(listPoint.get(0), Constant.ORIGIN);
            addMaker(listPoint.get(listPoint.size()-1), Constant.DESTINATION);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(centerPointInRoutes.latitude, centerPointInRoutes.longitude), 15));
        }
    }

    private void drawRoute(List<LatLng> listPoint){
        PolylineOptions opts = new PolylineOptions().addAll(listPoint).color(Color.BLUE).width(10);
        googleMap.addPolyline(opts);

    }

    private void addMaker(LatLng latLng, String type){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        if(type.equals(Constant.ORIGIN)){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }else {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        googleMap.addMarker(options).showInfoWindow();
    }

}
