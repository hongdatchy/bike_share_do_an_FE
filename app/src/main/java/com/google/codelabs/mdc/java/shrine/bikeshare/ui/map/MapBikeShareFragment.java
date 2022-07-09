package com.google.codelabs.mdc.java.shrine.bikeshare.ui.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.activities.ScanQrActivity;
import com.google.codelabs.mdc.java.shrine.api.ApiService;
import com.google.codelabs.mdc.java.shrine.databinding.FragmentMapBikeShareBinding;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.codelabs.mdc.java.shrine.entities.Station;
import com.google.codelabs.mdc.java.shrine.entities.adapter.AdapterStation;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapBikeShareFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    EditText originEditText;
    Double myLatOrigin = null, myLongOrigin = null;
    Double myLatDestination = null, myLongDestination = null;
    List<Station> stationList = new ArrayList<>();
    Spinner spinner;
    Marker markerOrigin, markerDestination;
    Polyline polyline;
    ImageView scanQrImageView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FragmentMapBikeShareBinding binding = FragmentMapBikeShareBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        originEditText = root.findViewById(R.id.origin_edit_text);
        scanQrImageView = root.findViewById(R.id.scanQrCode);

        // get gg map
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.ggMapFragment);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);

        // set value for spinner
        callApiFindListNearStation(root);

        // set onclick scanQr Image view
        setOnclickScanQr(scanQrImageView);
        return root;
    }

    @Override
    public void onMapReady(@NotNull GoogleMap gMap) {
        googleMap = gMap;
        settingUiGoogleMap();
        makeAutoCompletePlace();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.LAT_OF_HUST, Constant.LONG_OF_HUST), 11));
    }

    private void setOnclickScanQr(ImageView imageView){
        imageView.setOnClickListener(view -> {
            Common.switchActivity((AppCompatActivity) getActivity(), ScanQrActivity.class);
        });
    }

    private void callApiFindListNearStation(View root){

        ApiService.apiService.getAllStation().enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                MyResponse myResponse = response.body();
                assert myResponse != null;
                if(Constant.SUCCESS_MESSAGE_CALL_API.equals(myResponse.getMessage())){
                    Gson gson = Common.getMyGson();
                    String json = gson.toJson(myResponse.getData());
                    stationList = gson.fromJson(json, new TypeToken<List<Station>>(){}.getType());
                    Station stationAsFirstItemInSpinner = new Station();
                    stationAsFirstItemInSpinner.setId(0);
                    stationList.add(0, stationAsFirstItemInSpinner);
                    spinner = root.findViewById(R.id.spinnerStation);
                    ArrayAdapter<Station> adapter = new AdapterStation(requireActivity(),
                            R.layout.my_spinner_item, stationList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinnerOnclick(spinner);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                System.out.println("call api fail");
            }
        });
    }

    private void spinnerOnclick(Spinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Station station = (Station) parent.getItemAtPosition(position); //this is your selected item
                myLatDestination = station.getLatitude();
                myLongDestination = station.getLongitude();
                if(!station.getId().equals(0)){
                    addMaker(new LatLng(myLatDestination, myLongDestination), Constant.DESTINATION, station.getLocation());
                    moveCamera(new LatLng(myLatDestination, myLongDestination), 13);
                    drawRoute();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void settingUiGoogleMap() {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (!checkPermission()) {
            askPermission();
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setPadding(0, 500, 0, 0);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location location = getLastKnownLocation();
                if(location!= null){
                    LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    myLatOrigin = userLocation.latitude;
                    myLongOrigin = userLocation.longitude;
                    String s = "Vị trí hiện tại";
                    originEditText.setText(s);
                    addMaker(new LatLng(myLatOrigin, myLongOrigin), Constant.ORIGIN, "");
                    drawRoute();
                }
                return false;
            }
        });
    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager)
                requireActivity().getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkPermission()) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                Constant.REQUEST_LOCATION
        );
    }

    private void makeAutoCompletePlace() {
        //        auto place google api
        Places.initialize(requireActivity(), getResources().getString(R.string.google_maps_key));
        originEditText.setFocusable(false);
        originEditText.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setCountries(Collections.singletonList("VN"))
                    .build(requireActivity());
            activityResultLauncher.launch(intent);
        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            assert data != null;
                            Place place = Autocomplete.getPlaceFromIntent(data);
                            originEditText.setText(place.getAddress());
                            String sOrigin = String.valueOf(place.getLatLng());
                            sOrigin = sOrigin.replaceAll("lat/lng: ", "");
                            sOrigin = sOrigin.replace("(", "");
                            sOrigin = sOrigin.replace(")", "");
                            String[] split = sOrigin.split(",");
                            myLatOrigin = Double.parseDouble(split[0]);
                            myLongOrigin = Double.parseDouble(split[1]);
                            moveCamera(new LatLng(myLatOrigin, myLongOrigin), 13);
                            addMaker(new LatLng(myLatOrigin, myLongOrigin), Constant.ORIGIN, "");
                            drawRoute();
                        }catch (Exception exception){
                            System.out.println("place api not working or other case");
                        }
                    }
                }
            });

    private void addMaker(LatLng latLng, String type, String title){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(title);
        if(type.equals(Constant.ORIGIN)){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            if(markerOrigin != null){
                markerOrigin.remove();
            }
            markerOrigin = googleMap.addMarker(options);
            markerOrigin.showInfoWindow();
        }else {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            if(markerDestination != null){
                markerDestination.remove();
            }
            markerDestination = googleMap.addMarker(options);
            markerDestination.showInfoWindow();
        }
    }

    private void moveCamera(LatLng latLng, int zoom){
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void drawRoute(){
        if(polyline != null){
            polyline.remove();
        }
        if(myLatOrigin != null && myLongOrigin != null && myLatDestination != null && myLongDestination != null){
            ApiService.apiService.getDirection(myLatOrigin +","+myLongOrigin,
                    myLatDestination + "," + myLongDestination,
                    "driving",
                    getResources().getString(R.string.google_maps_key))
                    .enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                            List<LatLng> points = new ArrayList<>();
                            JSONArray jRoutes;
                            JSONArray jLegs;
                            JSONArray jSteps;
                            String distance ="";
                            try {
                                JsonObject jsonObject = JsonParser.parseString(new Gson().toJson(response.body())).getAsJsonObject();
                                jRoutes = new JSONObject(jsonObject.toString()).getJSONArray("routes");
                                for (int i = 0; i < jRoutes.length(); i++) {
                                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                                    for (int j = 0; j < jLegs.length(); j++) {
                                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                                        distance = ((JSONObject) jLegs.get(j)).getJSONObject("distance").getString("text");
                                        for (int k = 0; k < jSteps.length(); k++) {
                                            String polyline;
                                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                            List<LatLng> list = PolyUtil.decode(polyline);
                                            for (int l = 0; l < list.size(); l++) {
                                                points.add(new LatLng(list.get(l).latitude, list.get(l).longitude));
                                            }
                                        }
                                    }
                                }
                                System.out.println(distance);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (points.size() > 0) {
                                PolylineOptions opts = new PolylineOptions().addAll(points).color(Color.BLUE).width(10);
                                polyline = googleMap.addPolyline(opts);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                            System.out.println("call api fail");
                        }
                    });
        }
    }
}