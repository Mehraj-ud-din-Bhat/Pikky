package com.xscoder.pikky.loginSignUp;

/*==================================================
 Pikky

 © XScoder 2018
 All Rights reserved

 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
 YOU WILL BE LEGALLY PROSECUTED

==================================================*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.xscoder.pikky.Home.Home;
import com.xscoder.pikky.R;
import com.xscoder.pikky.setting.TermsOfUse;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BLOCKED_BY;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.USER_IS_REPORTED;
import static com.xscoder.pikky.Configurations.USER_IS_VERIFIED;
import static com.xscoder.pikky.Configurations.USER_MUTED_BY;
import static com.xscoder.pikky.Configurations.USER_NOT_INTERESTING_FOR;
import static com.xscoder.pikky.Configurations.USER_USERNAME_LOWERCASE;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class SignUp extends AppCompatActivity {

    /* Views */
    EditText usernameTxt, passwordTxt, emailTxt, fullnameTxt;
    RadioButton agreeRadioButt;



    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint({"SetTextI18n", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        // Init views
        usernameTxt = findViewById(R.id.usernameTxt2);
        passwordTxt = findViewById(R.id.passwordTxt2);
        emailTxt = findViewById(R.id.emailTxt2);
        fullnameTxt = findViewById(R.id.fullnameTxt2);
        usernameTxt.setTypeface(osRegular);
        passwordTxt.setTypeface(osRegular);
        emailTxt.setTypeface(osRegular);
        fullnameTxt.setTypeface(osRegular);
        agreeRadioButt = findViewById(R.id.supAgreeRadioButt);
        agreeRadioButt.setChecked(false);


        // Get App name
        TextView titleTxt = findViewById(R.id.suTitleTxt);
        titleTxt.setTypeface(osSemibold);
        titleTxt.setText("Sign up to " + getString(R.string.app_name));



        //-----------------------------------------------
        // MARK - SIGN UP BUTTON
        //-----------------------------------------------
        Button signupButt = findViewById(R.id.signUpButt2);
        signupButt.setTypeface(osBold);
        signupButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usernameTxt.getText().toString().matches("") || passwordTxt.getText().toString().matches("") ||
                        emailTxt.getText().toString().matches("") || fullnameTxt.getText().toString().matches("")
                        ) {

                    simpleAlert("You must fill all the fields to Sign Up!", SignUp.this);


                } else {
                    if (agreeRadioButt.isChecked()) {
                        showHUD(SignUp.this);
                        dismissKeyboard();

                        final ParseUser currentUser = new ParseUser();
                        List<String>empty = new ArrayList<>();

                        currentUser.setUsername(usernameTxt.getText().toString());
                        currentUser.setPassword(passwordTxt.getText().toString());
                        currentUser.setEmail(emailTxt.getText().toString());
                        currentUser.put(USER_FULLNAME, fullnameTxt.getText().toString());
                        currentUser.put(USER_USERNAME_LOWERCASE, usernameTxt.getText().toString().toLowerCase());
                        currentUser.put(USER_IS_REPORTED, false);
                        currentUser.put(USER_IS_FOLLOWING, empty);
                        currentUser.put(USER_NOT_INTERESTING_FOR, empty);
                        currentUser.put(USER_MUTED_BY, empty);
                        currentUser.put(USER_IS_VERIFIED, false);
                        currentUser.put(USER_BLOCKED_BY, empty);

                        currentUser.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException error) {
                                if (error == null) {

                                    // Save default avatar
                                    Bitmap bitmap = BitmapFactory.decodeResource(SignUp.this.getResources(), R.drawable. default_avatar);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    ParseFile imageFile = new ParseFile("avatar.jpg", byteArray);
                                    currentUser.put(USER_AVATAR, imageFile);
                                    currentUser.saveInBackground();

                                    hideHUD();

                                    // Enter the to Home screen
                                    startActivity(new Intent(SignUp.this, Home.class));

                                // error
                                } else {
                                    hideHUD();
                                    simpleAlert(error.getMessage(), SignUp.this);
                                }
                            }
                        });


                        // You haven't agreed with terms of Use
                    } else { simpleAlert("You must agree to our Terms of Use and Privacy Policy", SignUp.this); }
                }

            }});



        //-----------------------------------------------
        // MARK - TERMS OF USE BUTTON
        //-----------------------------------------------
        Button touButt = findViewById(R.id.touButt);
        touButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, TermsOfUse.class));
            }});




        //-----------------------------------------------
        // MARK - DISMISS BUTTON
        //-----------------------------------------------
        Button dismissButt = findViewById(R.id.signupDismissButt);
        dismissButt.setTypeface(osSemibold);
        dismissButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }});



    }// ./ onCreate()





    //-----------------------------------------------
    // MARK - DISMISS KEYBOARD
    //-----------------------------------------------
    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(usernameTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(emailTxt.getWindowToken(), 0);
    }



} // @end
