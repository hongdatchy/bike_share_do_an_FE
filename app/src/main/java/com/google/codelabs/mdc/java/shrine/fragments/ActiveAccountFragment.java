package com.google.codelabs.mdc.java.shrine.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.activities.MainActivity;
import com.google.codelabs.mdc.java.shrine.api.ApiService;
import com.google.codelabs.mdc.java.shrine.bikeshare.MainBikeShare;
import com.google.codelabs.mdc.java.shrine.entities.LoginForm;
import com.google.codelabs.mdc.java.shrine.entities.LoginResponse;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyProgressDialog;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveAccountFragment extends Fragment {

    private TextInputEditText activeEditText;
    private TextInputLayout activeLayout;
    private MyProgressDialog myProgressDialog;
    private MaterialButton activeButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active_accout, container, false);
        activeEditText = view.findViewById(R.id.active_text_input);
        activeLayout = view.findViewById(R.id.active_layout);
        activeButton = view.findViewById(R.id.active_button);

        myProgressDialog = new MyProgressDialog(getActivity());

        onClickActive();
        return view;
    }

    private void onClickActive(){
        activeButton.setOnClickListener(view -> {
            if(validateCode()){
                callApiActive(activeEditText.getText().toString());
            }
        });

    }

    private void callApiActive(String activeCode) {
        myProgressDialog.show();
        ApiService.apiService.active(activeCode).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

                MyResponse myResponse = response.body();
                activeLayout.setError(null);
                assert myResponse != null;

                if(myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                    ((MainActivity)requireActivity()).switchLoginFragment();
                }else{
                    activeLayout.setError((CharSequence) myResponse.getData());
                }
                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(),"Error callApiActive",Toast.LENGTH_SHORT).show();
                myProgressDialog.dismiss();
            }
        });
    }

    private boolean validateCode(){
        boolean rs = true;
        if("".equals(activeEditText.getText().toString())){
            activeLayout.setError("Chưa nhập mã active code");
            rs = false;
        }else{
            activeLayout.setError(null);
        }
        return rs;
    }

}
