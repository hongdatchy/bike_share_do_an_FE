package com.google.codelabs.mdc.java.shrine.bikeshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.activities.MainActivity;
import com.google.codelabs.mdc.java.shrine.api.ApiService;
import com.google.codelabs.mdc.java.shrine.databinding.ActivityMainBikeShareBinding;
import com.google.codelabs.mdc.java.shrine.entities.LoginForm;
import com.google.codelabs.mdc.java.shrine.entities.LoginResponse;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.codelabs.mdc.java.shrine.entities.UserResponse;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyProgressDialog;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainBikeShare extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    MyProgressDialog myProgressDialog;
    MyStorage myStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBikeShareBinding binding = ActivityMainBikeShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        myProgressDialog = new MyProgressDialog(this);
        myStorage = new MyStorage(this);
        setFullName(navigationView);
        onclickLogout(navigationView);

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setFullName(NavigationView navigationView){

        Gson gson = Common.getMyGson();
        UserResponse userResponse = gson.fromJson(myStorage.get(Constant.USER_KEY), UserResponse.class);
        View headerView = navigationView.getHeaderView(0);
        TextView fullNameTextView = headerView.findViewById(R.id.full_name_text_view);
        String fullName = userResponse.getLastname() + " " + userResponse.getFirstname();
        fullNameTextView.setText(fullName);
    }

    private void onclickLogout(NavigationView navigationView){
        View headerView = navigationView.getHeaderView(0);
        Button logoutButton = headerView.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> {
            callApiLogout();
        });
    }

    private void callApiLogout() {
        String token = myStorage.get(Constant.TOKEN_KEY);

        myProgressDialog.show();

        ApiService.apiService.logout(token).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

                MyResponse myResponse = response.body();

                assert myResponse != null;
                if(myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                    myStorage.save(Constant.TOKEN_KEY, "");
                    Common.switchActivity(MainBikeShare.this, MainActivity.class);
                }
                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainBikeShare.this,"Call api fail",Toast.LENGTH_SHORT).show();
                myProgressDialog.dismiss();
            }
        });
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finishAffinity();
    }
}
