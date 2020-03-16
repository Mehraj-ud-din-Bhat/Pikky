package com.xscoder.pikky.loginSignUp;

/*==================================================
 Pikky

 © XScoder 2018
 All Rights reserved


 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
 YOU WILL BE LEGALLY PROSECUTED

==================================================*/


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.xscoder.pikky.R;
import com.xscoder.pikky.loginSignUp.Operations.LogIn;

import java.util.Objects;

import API.API;
import API.API_URLS;
import ModalClasses.LoginResponse;
import ModalClasses.UserProfile;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class Login extends AppCompatActivity {

    /* Views */
    EditText usernameTxt;
    EditText passwordTxt;
    TextInputLayout textInputLayoutUserName,textInputLayoutPassword;


    @SuppressLint({"SetTextI18n", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        // Init views
        usernameTxt = findViewById(R.id.usernameTxt);
        passwordTxt = findViewById(R.id.passwordTxt);
        textInputLayoutUserName=findViewById(R.id.outlinedTextFieldUserName);
        textInputLayoutPassword=findViewById(R.id.outlinedTextFieldPassword);
        usernameTxt.setTypeface(osRegular);
        passwordTxt.setTypeface(osRegular);

        //This method is used to toggle color of inputTextLayout Hint and remove errors when focus is back
        setUpViews();


        // Get Title
        TextView titleTxt = findViewById(R.id.loginTitleTxt);
        titleTxt.setTypeface(osSemibold);
        titleTxt.setText("Log in to " + getString(R.string.app_name));


        // MARK: - LOGIN BUTTON ------------------------------------------------------------------
        Button loginButt = findViewById(R.id.loginButt);
        loginButt.setTypeface(osBold);
        loginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


             /*
              XCODES LOGIN THROUGH EMAIL PASSWORD DISABLED
              ParseUser.logInInBackground(usernameTxt.getText().toString(), passwordTxt.getText().toString(),
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException error) {
                                if (user != null) {
                                    hideHUD();
                                    // Go to Home screen
                                    startActivity(new Intent(Login.this, Home.class));

                                // error
                                } else {
                                    hideHUD();
                                    simpleAlert(error.getMessage(), Login.this);
                }}});*/


                if (usernameTxt.getText().toString().isEmpty() || passwordTxt.getText().toString().isEmpty()) {
                    if (usernameTxt.getText().toString().isEmpty()) {
                     //   simpleAlert("Email or Phone required", Login.this);
                        textInputLayoutUserName.setError("Email or Phone required");
                        return;
                    } else {
                        textInputLayoutPassword.setError("Password required");
                        return;
                    }

                } else {
                    showHUD(Login.this);
                    doLogin(usernameTxt.getText().toString(), passwordTxt.getText().toString());
                }

            }
        });


        // MARK: - SIGN UP BUTTON ------------------------------------------------------------------
        Button signupButt = findViewById(R.id.signUpButt);
        signupButt.setTypeface(osSemibold);
        signupButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });


        // MARK: - FORGOT PASSWORD BUTTON ------------------------------------------------------------------
        Button fpButt = findViewById(R.id.forgotPassButt);
        fpButt.setTypeface(osSemibold);
        fpButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                alert.setTitle(R.string.app_name);
                alert.setMessage("Type the valid email address you've used to register on this app");

                // Add an EditTxt
                final EditText editTxt = new EditText(Login.this);
                editTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                alert.setView(editTxt)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                // Reset password
                                ParseUser.requestPasswordResetInBackground(editTxt.getText().toString(), new RequestPasswordResetCallback() {
                                    public void done(ParseException error) {
                                        if (error == null) {
                                            simpleAlert("We've sent you an email to reset your password!", Login.this);
                                        } else {
                                            simpleAlert(error.getMessage(), Login.this);
                                        }
                                    }
                                });

                            }
                        });
                alert.show();

            }
        });


        // MARK: - DISMISS BUTTON ----------------------------------------------
        Button dismButt = findViewById(R.id.lDismissButt);
        dismButt.setTypeface(osSemibold);
        dismButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }// end onCreate()

// Code By MEHRAJ LOGIN API CALL

    void doLogin(String email,String pass)
    {
        LogIn login=new LogIn(this,email,pass,textInputLayoutUserName,textInputLayoutPassword);
        login.doLogin();
    }

 void   setUpViews()
    {

            usernameTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus){
                       // textInputLayoutUserName.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
                        if(textInputLayoutUserName.isErrorEnabled())
                        {
                            textInputLayoutUserName.setError(null);
                        }

                    } else {
                      //  textInputLayoutUserName.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                    }
                }
            });

        passwordTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                   // textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
                     if(textInputLayoutPassword.isErrorEnabled())
                     {
                       textInputLayoutPassword.setError(null);
                     }

                } else {
                  //  textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                }
            }
        });



    }


    //End Code by Mehraj

} // @end
