package com.xscoder.pikky.loginSignUp.Operations;

import android.content.Context;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import API.RetrofitClient;
import API.API_URLS;
import ModalClasses.LoginResponse;
import ModalClasses.UserProfile;
import retrofit2.Call;
import retrofit2.Callback;
import API.API;
import retrofit2.Response;

import retrofit2.converter.gson.GsonConverterFactory;

import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class LogIn {

    private Context myContext;
    private String userEmail;
    private String userPassword;
   private TextInputLayout textInputLayoutUserName,textInputLayoutPassword;

    public LogIn() {
    }

    public LogIn(Context myContext, String userEmail, String userPassword) {
        this.myContext = myContext;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }


    public LogIn(Context myContext, String userEmail, String userPassword, TextInputLayout textInputLayoutUserName, TextInputLayout textInputLayoutPassword) {
        this.myContext = myContext;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.textInputLayoutUserName = textInputLayoutUserName;
        this.textInputLayoutPassword = textInputLayoutPassword;
    }

    public void doLogin() {
        //Login by UserName and Password throgh API METHOD
     RetrofitClient retrofit=new RetrofitClient();
       API myApi=retrofit.getMyAPI();
        Call<LoginResponse> call = myApi.userLogin(this.userEmail, this.userPassword);
        // progressDialog.setMessage("Please wait..");
        //progressDialog.show();


        call.enqueue(new Callback<LoginResponse>() {
            LoginResponse myResponse;

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (!response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    hideHUD();
                    makeToast(response.message().toString());
                    makeToast(response.raw().toString());
                    // Log.i(TAG,response.raw().toString());
                    return;
                }
                myResponse = response.body();
                validateLoginResponse(myResponse);
                hideHUD();
               /*
                  used for debugging
                 makeToast(myResponse.toString());
                  makeToast(myResponse.getMessage());*/


            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // textViewResult.setText(t.getMessage());
                hideHUD();
                makeToast(t.getMessage());
            }
        });


    }

    public void makeToast(String text) {
        try {
            Toast.makeText(myContext, text, Toast.LENGTH_LONG).show();

        } catch (Exception e) {


        }


    }

    void validateLoginResponse(LoginResponse response)
    {
        if(response.isUserExists() && response.isPasswordMatch())
        {
            UserProfile userProfile=new UserProfile();
            userProfile=response.getUserProfile();
            simpleAlert("Registered USER: "+response.getMessage(),myContext);

        }
        else {
            if(response.isUserExists()==true && response.isPasswordMatch()==false)
            {
                textInputLayoutPassword.setError("Password Incorrect");

            }
            else
            simpleAlert(response.getMessage(),myContext);
        }


    }
}
