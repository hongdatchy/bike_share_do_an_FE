package com.google.codelabs.mdc.java.shrine.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.entities.ContractBikeResponse;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;


public class EndRentBikeActivity extends AppCompatActivity {
    TextView startTimeTextView;
    TextView endTimeTextView;
    TextView totalTimeTextView;
    TextView distanceTextView;
    MyStorage myStorage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_rent_bike);
        startTimeTextView = findViewById(R.id.start_time_textview);
        endTimeTextView = findViewById(R.id.end_time_textview);
        totalTimeTextView = findViewById(R.id.total_time_textview);
        distanceTextView = findViewById(R.id.distance_textview);
        myStorage = new MyStorage(this);

        ContractBikeResponse contractBikeResponse = Common.getMyGson()
                .fromJson(myStorage.get(Constant.CONTRACT_BIKE_RESPONSE_KEY), ContractBikeResponse.class);

        startTimeTextView.setText(Common.formatDate(contractBikeResponse.getStartTime()));
        endTimeTextView.setText(Common.formatDate(contractBikeResponse.getEndTime()));
        totalTimeTextView.setText("chưa làm");
        double distance = (double)Math.round(contractBikeResponse.getDistance() * 100) / 100;
        String distanceStr = distance + " km";
        distanceTextView.setText(distanceStr);
    }


}
