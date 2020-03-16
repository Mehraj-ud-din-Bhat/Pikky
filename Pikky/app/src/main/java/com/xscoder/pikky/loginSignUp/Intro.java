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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.facebook.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xscoder.pikky.Home.Home;
import com.xscoder.pikky.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BLOCKED_BY;
import static com.xscoder.pikky.Configurations.USER_EMAIL;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.USER_IS_REPORTED;
import static com.xscoder.pikky.Configurations.USER_IS_VERIFIED;
import static com.xscoder.pikky.Configurations.USER_MUTED_BY;
import static com.xscoder.pikky.Configurations.USER_NOT_INTERESTING_FOR;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.USER_USERNAME_LOWERCASE;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.showHUD;


public class Intro extends AppCompatActivity {


    /*--- VARIABLES ---*/
    Context ctx = this;




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Hiding Title bar of this activity screen */
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Hide Status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // Init views
        TextView descrTxt = findViewById(R.id.inDescriptionTxt);
        descrTxt.setTypeface(osSemibold);



        // Get App name
        TextView appNameTxt = findViewById(R.id.inAppNameTxt);
        appNameTxt.setTypeface(osBold);
        appNameTxt.setText(getString(R.string.app_name));



        // MARK: - SIGNUP BUTTON ------------------------------------
        Button signupButt = findViewById(R.id.inSignUpButt);
        signupButt.setTypeface(osBold);
        signupButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(Intro.this, SignUp.class));
         }});




        // MARK: - LOGIN BUTTON ------------------------------------
        Button loginButt = findViewById(R.id.inLoginButt);
        loginButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(Intro.this, Login.class));
          }});




        //-----------------------------------------------
        // MARK - FACEBOOK LOGIN BUTTON
        //-----------------------------------------------
        Button fbButt = findViewById(R.id.facebookButt);
        fbButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> permissions = Arrays.asList("public_profile", "email");
                showHUD(ctx);

                ParseFacebookUtils.logInWithReadPermissionsInBackground(Intro.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            Log.i(TAG, "ERROR ON FB LOGIN: " + e.getMessage());
                        }

                        if (user == null) {
                            Log.i(TAG, "CANCELLED Facebook login.");
                            hideHUD();

                        } else if (user.isNew()) {
                            getUserDetailsFromFB();

                        } else {
                            Log.i(TAG, "RETURNING User logged in through Facebook!");
                            hideHUD();
                            startActivity(new Intent(Intro.this, Home.class));
                        }}});
            }});


        // This code generates a KeyHash that you'll have to copy from your Logcat console and paste it into Key Hashes field in the 'Settings' section of your Facebook Android App
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i(TAG, "HASH KEY TO COPY: " + Base64.encodeToString(md.digest(), Base64.DEFAULT) + "\nPACKAGE NAME: " + getPackageName());
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) { }


    }// end onCreate()




    //-----------------------------------------------
    // MARK - FACEBOOK GRAPH REQUEST
    //-----------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    void getUserDetailsFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try{
                    String name = object.getString("name");
                    String email = object.getString("email");
                    final String facebookID = object.getString("id");
                    String username = "";

                    String[] one = name.toLowerCase().split(" ");
                    for (String word : one) { username += word; }
                    Log.i(TAG, "USERNAME: " + username + "\n");
                    Log.i(TAG, "email: " + email + "\n");
                    Log.i(TAG, "name: " + name + "\n");
                    Log.i(TAG, "facebookID: " + facebookID);

                    // Prepare data
                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put(USER_USERNAME, username);
                    currentUser.put(USER_USERNAME_LOWERCASE, username.toLowerCase()); 
                    if (email != null) { currentUser.put(USER_EMAIL, email);
                    } else { currentUser.put(USER_EMAIL, username + "@facebook.com"); }

                    List<String>empty = new ArrayList<>();
                    currentUser.put(USER_FULLNAME, name);
                    currentUser.put(USER_IS_REPORTED, false);
                    currentUser.put(USER_IS_FOLLOWING, empty);
                    currentUser.put(USER_NOT_INTERESTING_FOR, empty);
                    currentUser.put(USER_MUTED_BY, empty);
                    currentUser.put(USER_IS_VERIFIED, false);
                    currentUser.put(USER_BLOCKED_BY, empty);

                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.i(TAG, "NEW USER signed up and logged in through Facebook...");

                        // SAVE AVATAR FROM FACEBOOK IMAGE ------------------------------------------
                        if (!facebookID.matches("")) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        URL imageURL = new URL("https://graph.facebook.com/" + facebookID + "/picture?type=large");
                                        Bitmap avatarBm = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        avatarBm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();
                                        ParseFile imageFile = new ParseFile("image.jpg", byteArray);
                                        currentUser.put(USER_AVATAR, imageFile);
                                        currentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException error) {
                                                Log.i(TAG, "... AND AVATAR SAVED!");
                                                hideHUD();

                                                // Go to Home
                                                startActivity(new Intent(Intro.this, Home.class));
                                            }
                                        });

                                    } catch (IOException error) { error.printStackTrace(); }
                                }}, 1000); // 1 second



                        // SAVE DEFAULT AVATAR ------------------------------------------
                        } else {
                            Bitmap logoBm = BitmapFactory.decodeResource(Intro.this.getResources(), R.drawable.default_avatar);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            logoBm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            ParseFile imageFile = new ParseFile("image.jpg", byteArray);
                            currentUser.put(USER_AVATAR, imageFile);
                            currentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException error) {
                                    Log.i(TAG, "... AND LOGO AVATAR SAVED!");
                                    hideHUD();

                                    // Go to Home
                                    startActivity(new Intent(Intro.this, Home.class));
                            }});
                        }// ./ If

                    }}); // end saveInBackground


                } catch(JSONException e){ e.printStackTrace(); }

            }}); // end graphRequest



        Bundle parameters = new Bundle();
        parameters.putString("fields", "email, name, picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }
    // END FACEBOOK GRAPH REQUEST --------------------------------------------------------------------


    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();

    }
}// @end
