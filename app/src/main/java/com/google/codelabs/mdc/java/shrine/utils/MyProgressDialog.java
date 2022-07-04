package com.google.codelabs.mdc.java.shrine.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.codelabs.mdc.java.shrine.R;

public class MyProgressDialog {
    private ProgressDialog progressDialog;

    public MyProgressDialog(Context context){
        progressDialog = new ProgressDialog(context);
    }

    public void show(){
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void dismiss(){
        progressDialog.dismiss();
    }
}
