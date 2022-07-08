package com.google.codelabs.mdc.java.shrine.bikeshare.ui.map;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.activities.MainActivity;
import com.google.codelabs.mdc.java.shrine.activities.RentingBikeActivity;
import com.google.codelabs.mdc.java.shrine.api.ApiService;
import com.google.codelabs.mdc.java.shrine.entities.BikeInfo;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.codelabs.mdc.java.shrine.entities.RentBikeRequest;
import com.google.codelabs.mdc.java.shrine.socket.SocketClient;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyProgressDialog;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBileActivity extends AppCompatActivity {

    TextView frameNumberTextView;
    TextView productYearTextView;
    TextView batteryTextView;
    Button buttonRentBike;
    MyStorage myStorage;
    MyProgressDialog myProgressDialog;
    Spinner spinnerPaymentMethod;
    String paymentMethod;
    String bikeId;
    SocketClient socketClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_bike);
        frameNumberTextView = findViewById(R.id.frameNumberTextView);
        productYearTextView = findViewById(R.id.productYearTextView);
        batteryTextView = findViewById(R.id.batteryTextView);
        buttonRentBike = findViewById(R.id.rent_bike_button);
        spinnerPaymentMethod = findViewById(R.id.paymentMethodSpinner);

        List<String>arrPaymentMethod = Arrays.asList("tài khoản momo", "tài khoản ngân hàng", "thẻ điện thoại");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, arrPaymentMethod);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        myStorage = new MyStorage(this);
        myProgressDialog = new MyProgressDialog(this);
        bikeId = myStorage.get(Constant.BIKE_ID_KEY);

        socketClient = new SocketClient(this);
        socketClient.subscriberStomp(bikeId);

        callApiGetBikeInfoByBikeId(Integer.parseInt(bikeId));
        spinnerPaymentMethodOnclick();
        onClickRentBikeButton();
    }

    private void spinnerPaymentMethodOnclick(){
        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                paymentMethod = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void callApiGetBikeInfoByBikeId(int bikeId) {
        myProgressDialog.show();
        ApiService.apiService.findBikeInfoByBikeId(bikeId).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                MyResponse myResponse = response.body();
                assert myResponse != null;
                if(myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                    Gson gson = Common.getMyGson();
                    String json = gson.toJson(myResponse.getData());
                    myStorage.save(Constant.BIKE_INFO, json);
                    BikeInfo bikeInfo = gson.fromJson(json, BikeInfo.class);
                    String frameNumber = "Số khung: " + bikeInfo.getFrameNumber();
                    String productYear = "Năm sản xuất: " + bikeInfo.getProductYear();
                    String battery = "Pin: " + bikeInfo.getBattery() + " %";
                    frameNumberTextView.setText(frameNumber);
                    productYearTextView.setText(productYear);
                    batteryTextView.setText(battery);
                }
                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Toast.makeText(DetailBileActivity.this,"call api fail",Toast.LENGTH_SHORT).show();
                myProgressDialog.dismiss();
            }

        });
    }

    private void onClickRentBikeButton(){
        buttonRentBike.setOnClickListener(view -> {
            String token = myStorage.get(Constant.TOKEN_KEY);
            RentBikeRequest rentBikeRequest = new RentBikeRequest(Integer.parseInt(bikeId), paymentMethod);
            callApiRentBike(rentBikeRequest, token);
        });
    }

    private void callApiRentBike(RentBikeRequest rentBikeRequest, String token) {
        myProgressDialog.show();

        ApiService.apiService.rentBike(rentBikeRequest, token).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                if (response.isSuccessful()) {
                    MyResponse myResponse = response.body();
                    assert myResponse != null;
                    if(myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                        Gson gson = Common.getMyGson();
                        String json = gson.toJson(myResponse.getData());
                        myStorage.save(Constant.CONTRACT_BIKE_KEY, json);
                        if(Common.checkRentBikeSuccess(DetailBileActivity.this)){
                            Toast.makeText(DetailBileActivity.this, "Thuê xe thành công rồi bbbbbbbbb", Toast.LENGTH_LONG).show();
                            Common.switchActivity(DetailBileActivity.this, RentingBikeActivity.class);
                        }
                    }else{
                        Toast.makeText(DetailBileActivity.this,"Thuê xe thất bại",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailBileActivity.this,"Phiên làm việc đã hết hạn",Toast.LENGTH_SHORT).show();
                    Common.switchActivity(DetailBileActivity.this, MainActivity.class);
                }

                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Toast.makeText(DetailBileActivity.this,"Call api fail",Toast.LENGTH_SHORT).show();
                myProgressDialog.dismiss();
            }
        });
    }

}
