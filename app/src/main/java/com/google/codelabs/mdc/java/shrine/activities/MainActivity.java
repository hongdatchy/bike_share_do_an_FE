package com.google.codelabs.mdc.java.shrine.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.api.ApiService;
import com.google.codelabs.mdc.java.shrine.bikeshare.MainBikeShare;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.codelabs.mdc.java.shrine.fragments.ActiveAccountFragment;
import com.google.codelabs.mdc.java.shrine.fragments.LoginFragment;
import com.google.codelabs.mdc.java.shrine.fragments.RegisterFragment;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {
            callApiCheckLoginByToken();
        }


    }

    public void switchRegisterFragment(){
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new RegisterFragment())
            .commit();
    }

    public void switchLoginFragment(){
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new LoginFragment())
            .commit();
    }

    public void switchActiveFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ActiveAccountFragment())
                .commit();
    }

    private void callApiCheckLoginByToken(){
        MyStorage myStorage = new MyStorage(this);
        String token = myStorage.get(Constant.TOKEN_KEY);
        if(!token.equals("")){
            ApiService.apiService.checkLoginByToken(token).enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

                    MyResponse myResponse = response.body();
                    assert myResponse != null;
                    if(myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                        Common.switchActivity(MainActivity.this, MainBikeShare.class);
                    }else{
                        MainActivity.this.switchLoginFragment();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                    Toast.makeText(MainActivity.this,"Call api fail",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            MainActivity.this.switchLoginFragment();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
