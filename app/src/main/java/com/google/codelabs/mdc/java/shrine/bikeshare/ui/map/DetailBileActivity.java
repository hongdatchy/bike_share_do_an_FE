package com.google.codelabs.mdc.java.shrine.bikeshare.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.api.ApiService;
import com.google.codelabs.mdc.java.shrine.entities.BikeInfo;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBileActivity extends AppCompatActivity {

    private static final String TAG = "DetailBileActivity TAG";
    TextView frameNumberTextView;
    TextView productYearTextView;
    TextView batteryTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_bike);
        frameNumberTextView = findViewById(R.id.frameNumberTextView);
        productYearTextView = findViewById(R.id.productYearTextView);
        batteryTextView = findViewById(R.id.batteryTextView);
        MyStorage myStorage = new MyStorage(DetailBileActivity.this);
        String userId = myStorage.get(Constant.BIKE_ID_KEY);


        callApiGetBikeInfoByBikeId(Integer.parseInt(userId));

    }

    private void callApiGetBikeInfoByBikeId(int bikeId) {

        ApiService.apiService.findBikeInfoByBikeId(bikeId).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

                MyResponse myResponse = response.body();
                assert myResponse != null;
                if(myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                    Gson gson = Common.getMyGson();
                    String json = gson.toJson(myResponse.getData());
                    BikeInfo bikeInfo = gson.fromJson(json, BikeInfo.class);
                    String frameNumber = String.valueOf(bikeInfo.getFrameNumber());
                    String productYear = String.valueOf(bikeInfo.getProductYear());
                    String battery = String.valueOf(bikeInfo.getBattery());
                    frameNumberTextView.setText(frameNumber);
                    productYearTextView.setText(productYear);
                    batteryTextView.setText(battery);
                    System.out.println(TAG+json);
                }

            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Toast.makeText(DetailBileActivity.this,"Error callApiGetBikeInfoByBikeId",Toast.LENGTH_SHORT).show();

            }

        });
    }

}
