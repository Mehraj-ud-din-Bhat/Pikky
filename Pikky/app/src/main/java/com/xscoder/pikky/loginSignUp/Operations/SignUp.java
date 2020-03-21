package com.xscoder.pikky.loginSignUp.Operations;

import android.content.Context;
import android.widget.Toast;

import com.xscoder.pikky.loginSignUp.ModalClasses.LoginResponse;
import com.xscoder.pikky.loginSignUp.ModalClasses.SignUpResponse;
import com.xscoder.pikky.loginSignUp.ModalClasses.UserProfile;

import API.API;
import API.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class SignUp {
    private Context myContext;

    private String userFname;
    private String userLname;
    private String userEmail;
    private String userPhone;
    private String userPassword;
    private String userProfileBase64;

    public SignUp(Context myContext) {
        this.myContext = myContext;
    }

    public void doSignUp(String userFname, String userLname, String userEmail, String userPhone, String userPassword, String userProfileBase64) {
        this.userFname = userFname;
        this.userLname = userLname;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userPassword = userPassword;
        this.userProfileBase64 = userProfileBase64;


        RetrofitClient retrofit = new RetrofitClient();
        API myApi = retrofit.getMyAPI();
        Call<SignUpResponse> call = myApi.registerUser(new UserProfile(this.userFname,
                this.userLname,
                this.userEmail,
                this.userPhone,
                this.userPassword,
                this.userProfileBase64));
        // makeToast(this.userProfileBase64+""); test purpose


        call.enqueue(new Callback<SignUpResponse>() {
            SignUpResponse myResponse;

            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {

                if (!response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    hideHUD();
                    makeToast(response.message().toString());
                    makeToast(response.raw().toString());
                    // Log.i(TAG,response.raw().toString());
                    return;
                }
                myResponse = response.body();

                validateSignupResponse(myResponse);
                hideHUD();
               /*
                  used for debugging
                 makeToast(myResponse.toString());
                  makeToast(myResponse.getMessage());*/


            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                // textViewResult.setText(t.getMessage());
                hideHUD();
                //  makeToast(t.getMessage());
            }
        });
    }


    public void makeToast(String text) {
        try {
            Toast.makeText(myContext, text, Toast.LENGTH_LONG).show();

        } catch (Exception e) {


        }


    }

    void validateSignupResponse(SignUpResponse res) {
        if (res.isResgistered()) {
            simpleAlert("Registration Succesful: " + res.getMessage(), myContext);

        } else {

            simpleAlert("Opps!" + res.getMessage(), myContext);
        }

    }
}
