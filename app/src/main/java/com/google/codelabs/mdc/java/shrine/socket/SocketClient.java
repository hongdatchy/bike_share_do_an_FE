package com.google.codelabs.mdc.java.shrine.socket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class SocketClient {
    Context context;
    MyStorage myStorage;
    private CompositeDisposable compositeDisposable;
    private final StompClient mStompClient;
    private static final String TAG = "SocketClient";

    public SocketClient(Context context){
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
                    if(Common.checkRentBikeSuccess(context)){
//                        Toast.makeText(context, "Thuê xe thành công rồi ạaaaaaaaaaaaaaaaaaaa", Toast.LENGTH_LONG).show();
                    }
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        compositeDisposable.add(disposable);
    }

    GoogleMap googleMap;
    Marker marker;
    public void subscriberStompUpdateLatLongBike(int bikeId, GoogleMap gMap, Marker mKer) {
        this.googleMap = gMap;
        this.marker = mKer;
        resetSubscriptions();
        Disposable disposable = mStompClient.topic("/topic/updateLatLong/" + bikeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
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
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        compositeDisposable.add(disposable);
    }

    public void subscriberStompCheckEndRenting(int bikeId) {
        resetSubscriptions();
        Disposable disposable = mStompClient.topic("/topic/checkEndRenting/" + bikeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Toast.makeText(context, "Banj cos muoons keets thucs chuyeens xe khoong", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Write your message here.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
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

}
