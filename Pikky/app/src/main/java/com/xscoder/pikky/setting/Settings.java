package com.xscoder.pikky.setting;

/*==================================================
     Pikky

     © XScoder 2018
     All Rights reserved

     RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
     YOU WILL BE LEGALLY PROSECUTED

==================================================*/

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.xscoder.pikky.Massiging.Notifications;
import com.xscoder.pikky.R;
import com.xscoder.pikky.Search.SearchScreen;
import com.xscoder.pikky.Userprofile.EditProfile;
import com.xscoder.pikky.loginSignUp.Activity.Intro;

import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.xscoder.pikky.Configurations.MAIN_COLOR;
import static com.xscoder.pikky.Configurations.SUPPORT_EMAIL_ADDRESS;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.allowPush;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class Settings extends AppCompatActivity {

    /*--- VIEWS ---*/
    Button resetPasswordButt, likedPostsButt, blockedUsersButt, bookmarkedPostsButt, touButt, contactUsButt, editProfileButt, logoutButt;
    Switch pushNotificationsSWitch;
    TextView pushNotifTxt;


    /*--- VARIABLES ---*/
    Context ctx = this;



    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint({"SetTextI18n", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        resetPasswordButt = findViewById(R.id.settResetPasswordButt);
        resetPasswordButt.setTypeface(osRegular);
        likedPostsButt = findViewById(R.id.settLikedPostsButt);
        likedPostsButt.setTypeface(osRegular);
        blockedUsersButt = findViewById(R.id.settBlockedUsersButt);
        blockedUsersButt.setTypeface(osRegular);
        bookmarkedPostsButt = findViewById(R.id.settBookmarkedPostsButt);
        bookmarkedPostsButt.setTypeface(osRegular);
        pushNotificationsSWitch = findViewById(R.id.settPushNotificationsSwitch);
        pushNotifTxt = findViewById(R.id.settPushNotifTxt);
        pushNotifTxt.setTypeface(osRegular);
        touButt = findViewById(R.id.settTOUButt);
        touButt.setTypeface(osRegular);
        contactUsButt = findViewById(R.id.settContactUsButt);
        contactUsButt.setTypeface(osRegular);
        editProfileButt = findViewById(R.id.settEditProfileButt);
        editProfileButt.setTypeface(osSemibold);

        // Logout Button
        logoutButt = findViewById(R.id.settLogoutButt);
        logoutButt.setTypeface(osSemibold);
        ParseUser currentUser = ParseUser.getCurrentUser();
        logoutButt.setText("Log out of @" + currentUser.getString(USER_USERNAME));


        // Push Notifications Switch state
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        allowPush = prefs.getBoolean("allowPush", true);
        if (allowPush) {
            pushNotificationsSWitch.setChecked(true);
        } else {
            pushNotificationsSWitch.setChecked(false);
        }
        Log.i("log-", "ALLOW PUSH: " + prefs.getBoolean("allowPush", allowPush) );





        //-----------------------------------------------
        // MARK - RESET PASSWORD BUTTON
        //-----------------------------------------------
        resetPasswordButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                alert.setTitle(R.string.app_name);
                alert.setMessage("Type the email address you've used to sign up.\n(Please note that if you've signed in with Facebook, you will not be able to reset your password");

                // EditText
                final EditText editTxt = new EditText(ctx);
                editTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                editTxt.setTextSize(12);
                editTxt.setTypeface(osRegular);
                editTxt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(MAIN_COLOR)));
                alert.setView(editTxt)

                    .setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ParseUser.requestPasswordResetInBackground(editTxt.getText().toString(), new RequestPasswordResetCallback() {
                        public void done(ParseException error) {
                            if (error == null) {
                                simpleAlert("Thanks, you are going to shortly get an email with a link to reset your password!", ctx);
                             } else {
                                simpleAlert(error.getMessage(), ctx);
                        }}});
                    }})
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false);
                alert.show();
        }});



        // ------------------------------------------------
        // MARK: - LIKED POSTS
        // ------------------------------------------------
        likedPostsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(ctx, SearchScreen.class);
               Bundle extras = new Bundle();
               extras.putString("searchText", "_LIKED_POSTS_");
               extras.putBoolean("isTag", true);
               i.putExtras(extras);
               startActivity(i);
        }});



        // ------------------------------------------------
        // MARK: - BOOKMARKED POSTS
        // ------------------------------------------------
        bookmarkedPostsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, SearchScreen.class);
                Bundle extras = new Bundle();
                extras.putString("searchText", "_BOOKMARKED_POSTS_");
                extras.putBoolean("isTag", true);
                i.putExtras(extras);
                startActivity(i);
        }});




        // ------------------------------------------------
        // MARK: - BLOCKED USERS BUTTON
        // ------------------------------------------------
        blockedUsersButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, Notifications.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isBlockedUsers", true);
                i.putExtras(extras);
                startActivity(i);
            }
        });



        //-----------------------------------------------
        // MARK - PUSH NOTIFICATIONS SWITCH
        //-----------------------------------------------
        pushNotificationsSWitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                allowPush = !allowPush;

                if (!allowPush) {
                    prefs.edit().putBoolean("allowPush", false).apply();
                    pushNotificationsSWitch.setChecked(false);
                } else {
                    prefs.edit().putBoolean("allowPush", true).apply();
                    pushNotificationsSWitch.setChecked(true);
                }
                Log.i("log-", "ALLOW PUSH - onSwitchChange: " + allowPush);
        }});



        //-----------------------------------------------
        // MARK - TERMS OF USE BUTTON
        //-----------------------------------------------
        touButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, TermsOfUse.class));
        }});




        // ------------------------------------------------
        // MARK: - CONTACT US BUTTON
        // ------------------------------------------------
        contactUsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", SUPPORT_EMAIL_ADDRESS);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                simpleAlert("Our Support email address has been copied to your clipboard.\nOpen your favorite Mail client and drop us a message, we'll get back to you asap!", ctx);
            }
        });


        //-----------------------------------------------
        // MARK - EDIT PROFILE BUTTON
        //-----------------------------------------------
        editProfileButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, EditProfile.class));
        }});



        //-----------------------------------------------
        // MARK - LOGOUT BUTTON
        //-----------------------------------------------
        logoutButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                alert.setMessage("Are you sure you want to logout?")
                    .setTitle(R.string.app_name)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showHUD(ctx);
                            ParseUser.logOutInBackground(new LogOutCallback() {
                                @Override
                                public void done(ParseException e) {
                                    hideHUD();
                                    startActivity(new Intent(ctx, Intro.class));
                            }});
                    }})
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .setIcon(R.drawable.logo);
                alert.create().show();
        }});




        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.settBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }// ./ onCreate




}// ./ end
