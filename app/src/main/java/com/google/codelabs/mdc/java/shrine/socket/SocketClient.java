package com.google.codelabs.mdc.java.shrine.socket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.activities.EndRentBikeActivity;
import com.google.codelabs.mdc.java.shrine.activities.MainActivity;
import com.google.codelabs.mdc.java.shrine.activities.RentingBikeActivity;
import com.google.codelabs.mdc.java.shrine.api.ApiService;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyProgressDialog;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class SocketClient {
    Context context;
    MyStorage myStorage;
    private CompositeDisposable compositeDisposable;
    private final StompClient mStompClient;
    private static final String TAG = "SocketClient";

    public SocketClient(Context context){
        System.out.println("ddddkdndkdbdbdkgdhd");
        this.context = context;
        this.myStorage = new MyStorage(context);
        this.mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
                "ws://" + Constant.DOMAIN_IP + ":" + Constant.OPEN_PORT + "/example-endpoint/websocket");
        mStompClient.connect();
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public void subscriberStomp(String bikeId) {
        resetSubscriptions();
        Disposable disposable = mStompClient.topic("/topic/notifyRenting/" + bikeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {

                    myStorage.save(Constant.OPEN_LOCK_SUCCESS_KEY, topicMessage.getPayload());
                    unSubscribe();
                    Common.switchActivity(context, RentingBikeActivity.class);

                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        compositeDisposable.add(disposable);
    }

    GoogleMap googleMap;
    Marker marker;
    public void subscriberStompUpdateLatLongBikeOrCheckEndRenting(int bikeId
            , GoogleMap gMap, Marker mKer, ImageButton imageButton) {
        this.googleMap = gMap;
        this.marker = mKer;
        resetSubscriptions();
        Disposable disposable = mStompClient.topic("/topic/updateLatLongOrCheckEndRenting/" + bikeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    if(topicMessage.getPayload().equals("Bạn có muốn kết thúc chuyến đi không")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(topicMessage.getPayload());
                        builder.setCancelable(true);

                        builder.setPositiveButton(
                                "Có",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        int bikeId = Integer.parseInt(myStorage.get(Constant.BIKE_ID_KEY));
                                        callApiEndRentBike(bikeId, myStorage.get(Constant.TOKEN_KEY));
                                    }
                                });

                        builder.setNegativeButton(
                                "Khoá xe tạm thời",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        imageButton.setImageResource(R.drawable.play_icon);
                                    }
                                });

                        AlertDialog alert11 = builder.create();
                        alert11.show();
                    }else {
                        String[] s = topicMessage.getPayload().split(",");
                        double mLat = Double.parseDouble(s[0]);
                        double mLong = Double.parseDouble(s[1]);
                        MarkerOptions options = new MarkerOptions();
                        options.position(new LatLng(mLat, mLong));
                        int height = 100;
                        int width = 100;
                        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.location_icon_dot);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                        options.icon(smallMarkerIcon);
                        if(marker != null){
                            marker.remove();
                        }
                        marker = googleMap.addMarker(options);
                        marker.showInfoWindow();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLong), 15));
                    }

                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        compositeDisposable.add(disposable);
    }


    public void unSubscribe(){
        if(mStompClient.isConnected()){
            mStompClient.disconnect();
        }
    }

    private void callApiEndRentBike(int bikeId, String token) {
        MyProgressDialog myProgressDialog = new MyProgressDialog(context);
        myProgressDialog.show();

        ApiService.apiService.endRentBike(bikeId, token).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                if (response.isSuccessful()) {
                    MyResponse myResponse = response.body();
                    assert myResponse != null;
                    if(myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                        Gson gson = Common.getMyGson();
                        String json = gson.toJson(myResponse.getData());
                        myStorage.save(Constant.CONTRACT_BIKE_RESPONSE_KEY, json);
                        unSubscribe();
                        Common.switchActivity(context, EndRentBikeActivity.class);
                    }else{
                        Toast.makeText(context,"Kết thúc thuê xe thất bại",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,"Phiên làm việc đã hết hạn",Toast.LENGTH_SHORT).show();
                    unSubscribe();
                    Common.switchActivity(context, MainActivity.class);
                }

                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Toast.makeText(context,"Call api fail",Toast.LENGTH_SHORT).show();
                myProgressDialog.dismiss();
            }
        });
    }

}
