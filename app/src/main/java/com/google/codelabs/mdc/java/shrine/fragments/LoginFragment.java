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

/**
 * Fragment representing the login screen for app.
 */
public class LoginFragment extends Fragment {

    private TextInputEditText passwordEditText;
    private TextInputLayout passwordTextInput;
    private TextInputEditText usernameEditText;
    private MyProgressDialog myProgressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        passwordTextInput = view.findViewById(R.id.password_text_input);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        usernameEditText = view.findViewById(R.id.username_edit_text);
        MaterialButton loginButton = view.findViewById(R.id.login_button);
        MaterialButton registerButton = view.findViewById(R.id.register_button);
        myProgressDialog = new MyProgressDialog(getActivity());

        loginButton.setOnClickListener(view1 -> {
            String email = String.valueOf(usernameEditText.getText());
            String pass = String.valueOf(passwordEditText.getText());
//            call api login
            callApiLogin(new LoginForm(email, pass));
        });
        registerButton.setOnClickListener(view1 -> {
            ((MainActivity)requireActivity()).switchRegisterFragment();
        });
        return view;
    }

    private void callApiLogin(LoginForm loginForm) {
        myProgressDialog.show();
        ApiService.apiService.login(loginForm).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

                MyResponse myResponse = response.body();
                passwordTextInput.setError(null);
                assert myResponse != null;

                if(myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                    Gson gson = Common.getMyGson();
                    String json = gson.toJson(myResponse.getData());
                    LoginResponse loginResponse = gson.fromJson(json, LoginResponse.class);

                    MyStorage myStorage = new MyStorage(requireActivity());
                    myStorage.save(Constant.TOKEN_KEY, loginResponse.getToken());
                    myStorage.save(Constant.USER_KEY, gson.toJson(loginResponse.getUserResponse()));
                    // đi đến trang welcome sau khi login thành công
                    Common.switchActivity((AppCompatActivity) getActivity(), MainBikeShare.class);
                }else{
                    passwordTextInput.setError((CharSequence) myResponse.getData());
                }
                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(),"Error internet or server is not running",Toast.LENGTH_SHORT).show();
                myProgressDialog.dismiss();
            }
        });
    }

}
