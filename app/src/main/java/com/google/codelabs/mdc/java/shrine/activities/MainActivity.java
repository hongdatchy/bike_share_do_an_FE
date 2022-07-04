package com.google.codelabs.mdc.java.shrine.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.fragments.LoginFragment;
import com.google.codelabs.mdc.java.shrine.fragments.RegisterFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
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

}
