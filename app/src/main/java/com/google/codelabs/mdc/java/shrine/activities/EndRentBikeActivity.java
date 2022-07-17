package com.google.codelabs.mdc.java.shrine.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.bikeshare.MainBikeShare;
import com.google.codelabs.mdc.java.shrine.entities.ContractBikeResponse;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;

import java.util.Date;
import java.util.Locale;


public class EndRentBikeActivity extends AppCompatActivity {
    TextView startTimeTextView;
    TextView endTimeTextView;
    TextView totalTimeTextView;
    TextView distanceTextView;
    MyStorage myStorage;
    Button buttonToMainBikeShare;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_rent_bike);
        startTimeTextView = findViewById(R.id.start_time_textview);
        endTimeTextView = findViewById(R.id.end_time_textview);
        totalTimeTextView = findViewById(R.id.total_time_textview);
        distanceTextView = findViewById(R.id.distance_textview);
        buttonToMainBikeShare = findViewById(R.id.to_main_bike_share_button);


        myStorage = new MyStorage(this);

        ContractBikeResponse contractBikeResponse = Common.getMyGson()
                .fromJson(myStorage.get(Constant.CONTRACT_BIKE_RESPONSE_KEY), ContractBikeResponse.class);

        startTimeTextView.setText(Common.formatDate(contractBikeResponse.getStartTime()));
        endTimeTextView.setText(Common.formatDate(contractBikeResponse.getEndTime()));
        String totalTime = getTotalTime(contractBikeResponse.getStartTime(), contractBikeResponse.getEndTime());
        totalTimeTextView.setText(totalTime);
        Double distance = contractBikeResponse.getDistance();
        distance = distance != null ? (double)Math.round(distance * 100) / 100 : 0;
        String distanceStr = distance + " km";
        distanceTextView.setText(distanceStr);
        // xoá data không cần thiết nữa
        clearMyStorage();
        onClickButton();

    }

    private String getTotalTime(Date startTime, Date endTime){
        int seconds = (int) ((endTime.getTime() - startTime.getTime()) / 1000);
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        // Format the seconds into hours, minutes and seconds.
        return String.format(Locale.getDefault(),
                "%d:%02d:%02d", hours, minutes, secs);
    }

    private void onClickButton(){
        buttonToMainBikeShare.setOnClickListener(view -> {
            Common.switchActivity(this, MainBikeShare.class);
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void clearMyStorage(){
        myStorage.save(Constant.BIKE_ID_KEY, "");
        myStorage.save(Constant.BIKE_INFO, "");
        myStorage.save(Constant.OPEN_LOCK_SUCCESS_KEY, "");
        myStorage.save(Constant.CONTRACT_BIKE_RESPONSE_KEY, "");
        myStorage.save(Constant.statusLockWhenRenting, "");
    }

}
