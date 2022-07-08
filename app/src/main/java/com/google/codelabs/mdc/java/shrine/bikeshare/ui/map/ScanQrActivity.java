package com.google.codelabs.mdc.java.shrine.bikeshare.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;
import com.google.zxing.Result;

public class ScanQrActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    MyStorage myStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr_activity);
        myStorage = new MyStorage(ScanQrActivity.this);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {

                    // hàm này chạy khi detect được mã qr code thành công
                    @Override
                    public void run() {

                        myStorage.save(Constant.BIKE_ID_KEY, result.getText());
                        Toast.makeText(ScanQrActivity.this, "Quét mã QR thành công", Toast.LENGTH_SHORT).show();
                        Common.switchActivity(ScanQrActivity.this, DetailBileActivity.class);
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

}
