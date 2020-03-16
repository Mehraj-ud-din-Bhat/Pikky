package com.xscoder.pikky.Userprofile;

/*==================================================
 Pikky

 © XScoder 2018
 All Rights reserved


 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
 YOU WILL BE LEGALLY PROSECUTED

==================================================*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xscoder.pikky.Massiging.Follow;
import com.xscoder.pikky.posts.PostDetails;
import com.xscoder.pikky.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.xscoder.pikky.Configurations.FOLLOW_CLASS_NAME;
import static com.xscoder.pikky.Configurations.FOLLOW_CURRENT_USER;
import static com.xscoder.pikky.Configurations.FOLLOW_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.MAIN_COLOR;
import static com.xscoder.pikky.Configurations.MOMENTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.MOMENTS_FOLLOWED_BY;
import static com.xscoder.pikky.Configurations.MOMENTS_REPORTED_BY;
import static com.xscoder.pikky.Configurations.MOMENTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.POSTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.POSTS_CREATED_AT;
import static com.xscoder.pikky.Configurations.POSTS_FOLLOWED_BY;
import static com.xscoder.pikky.Configurations.POSTS_IMAGE;
import static com.xscoder.pikky.Configurations.POSTS_REPORTED_BY;
import static com.xscoder.pikky.Configurations.POSTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.POSTS_VIDEO;
import static com.xscoder.pikky.Configurations.TAG;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BIO;
import static com.xscoder.pikky.Configurations.USER_BLOCKED_BY;
import static com.xscoder.pikky.Configurations.USER_CLASS_NAME;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.USER_IS_VERIFIED;
import static com.xscoder.pikky.Configurations.USER_MUTED_BY;
import static com.xscoder.pikky.Configurations.USER_NOT_INTERESTING_FOR;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.USER_WEBSITE;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.roundLargeNumber;
import static com.xscoder.pikky.Configurations.sendPushNotification;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class UserProfile extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /*--- VIEWS ---*/
    CircleImageView avatarImg;
    TextView fullnameTxt, usernameTxt, bioTxt, followingCountTxt, followersCountTxt, postsCountTxt;
    Button followButt, followersButt, followingButt, websiteButt, optionsButt;
    GridView postsGridView;
    SwipeRefreshLayout refreshControl;
    ImageView verifiedBadge;



    /*--- VARIABLES ---*/
    Context ctx = this;
    ParseUser userObj;
    List<ParseObject>postsArray = new ArrayList<>();
    List<ParseObject>followersArray = new ArrayList<>();
    List<ParseObject>followingArray = new ArrayList<>();
    int screenW, screenH;
    int skip, skip2, skip3 = 0;





    //-----------------------------------------------
    // MARK - ON START
    //-----------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();

        resetVariables();

        // Call Queries
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                queryFollowers();
                queryFollowing();

        }}, 500);
    }



    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenH = displayMetrics.heightPixels;
        screenW = displayMetrics.widthPixels;
        Log.i("log-", "SCREEN'S WIDTH: " + screenW);
        Log.i("log-", "SCREEN'S HEIGHT: " + screenH);



        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        avatarImg = findViewById(R.id.upAvatarImg);
        fullnameTxt = findViewById(R.id.upFullNameTxt);
        fullnameTxt.setTypeface(osBold);
        usernameTxt = findViewById(R.id.upUsernameTxt);
        usernameTxt.setTypeface(osRegular);
        followButt = findViewById(R.id.upFollowButt);
        followButt.setTypeface(osSemibold);
        bioTxt = findViewById(R.id.upBioTxt);
        bioTxt.setTypeface(osRegular);
        followingCountTxt = findViewById(R.id.upFollowingCountTxt);
        followingCountTxt.setTypeface(osBold);
        followersCountTxt = findViewById(R.id.upFollowersCountTxt);
        followersCountTxt.setTypeface(osBold);
        postsCountTxt = findViewById(R.id.upPostsCountTxt);
        postsCountTxt.setTypeface(osBold);
        followersButt = findViewById(R.id.upFollowersButt);
        followingButt = findViewById(R.id.upFollowingButt);
        websiteButt = findViewById(R.id.upWebsiteButt);
        websiteButt.setTypeface(osRegular);
        optionsButt = findViewById(R.id.upOptionsButt);
        verifiedBadge = findViewById(R.id.upVerifiedBadge);
        postsGridView = findViewById(R.id.upPostsGridView);

        // RefreshControl
        refreshControl = findViewById(R.id.refreshControl);
        refreshControl.setOnRefreshListener(this);



        // Get extras
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        final String objectID = extras.getString("objectID");
        userObj = (ParseUser) ParseObject.createWithoutData(USER_CLASS_NAME, objectID);
        try { userObj.fetchIfNeeded().getParseObject(USER_CLASS_NAME);

            final ParseUser currentUser = ParseUser.getCurrentUser();

            // Call queries
            showUserDetails();
            queryPosts();



            // ------------------------------------------------
            // MARK: - FOLLOWERS BUTTON
            // ------------------------------------------------
            followersButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ctx, Follow.class);
                    Bundle extras = new Bundle();
                    extras.putBoolean("isFollowers", true);
                    extras.putString("objectID", userObj.getObjectId());
                    i.putExtras(extras);
                    startActivity(i);
            }});


            // ------------------------------------------------
            // MARK: - FOLLOWING BUTTON
            // ------------------------------------------------
            followingButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ctx, Follow.class);
                    Bundle extras = new Bundle();
                    extras.putBoolean("isFollowers", false);
                    extras.putString("objectID", userObj.getObjectId());
                    i.putExtras(extras);
                    startActivity(i);
            }});



            //-----------------------------------------------
            // MARK - WEBSITE BUTTON
            //-----------------------------------------------
            websiteButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!websiteButt.getText().toString().matches("") ){
                        if (websiteButt.getText().toString().startsWith("http://") || websiteButt.getText().toString().startsWith("https://") ){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(websiteButt.getText().toString())));
                        }
                    }
            }});




            // ------------------------------------------------
            // MARK: - FOLLOW BUTTON
            // ------------------------------------------------
            followButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<String>isFollowing = currentUser.getList(USER_IS_FOLLOWING);

                // MARK: - UN-FOLLOW THIS USER
                // ------------------------------------------------
                if (followButt.getText().toString().matches("Following")) {
                    // Update 'isFollowing' for currentUser
                    assert isFollowing != null;
                    isFollowing.remove(userObj.getObjectId());
                    currentUser.put(USER_IS_FOLLOWING, isFollowing);
                    currentUser.saveInBackground();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Query Follow and remove the row in the database
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(FOLLOW_CLASS_NAME);
                            query.whereEqualTo(FOLLOW_CURRENT_USER, currentUser);
                            query.whereEqualTo(FOLLOW_IS_FOLLOWING, userObj);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        ParseObject fObj = objects.get(0);
                                        fObj.deleteInBackground(new DeleteCallback() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void done(ParseException e) {
                                                followButt.setText("Follow");
                                                followButt.setTextColor(Color.parseColor(MAIN_COLOR));
                                            }
                                        });
                                        // error
                                    } else {
                                        simpleAlert(e.getMessage(), ctx);
                            }}});// ./ query Follow


                            // Query Posts of userObj and un-follow them
                            ParseQuery<ParseObject> query2 = ParseQuery.getQuery(POSTS_CLASS_NAME);
                            query2.whereEqualTo(POSTS_USER_POINTER, userObj);
                            query2.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.size() != 0) {
                                            for (int i = 0; i < objects.size(); i++) {
                                                ParseObject pObj = objects.get(i);
                                                List<String> followedBy = pObj.getList(POSTS_FOLLOWED_BY);
                                                assert followedBy != null;
                                                followedBy.remove(currentUser.getObjectId());
                                                pObj.put(POSTS_FOLLOWED_BY, followedBy);
                                                pObj.saveInBackground();
                                            }
                                        }
                                        // error
                                    } else {
                                        simpleAlert(e.getMessage(), ctx);
                            }}});// ./ Query Posts of userObj and un-follow them


                            // Remove currentUser's ID to followedBy in Moments
                            ParseQuery<ParseObject> query3 = ParseQuery.getQuery(MOMENTS_CLASS_NAME);
                            query3.whereEqualTo(MOMENTS_USER_POINTER, userObj);
                            query3.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.size() != 0) {
                                            ParseObject mObj = objects.get(0);
                                            List<String> followedBy = mObj.getList(MOMENTS_FOLLOWED_BY);
                                            assert followedBy != null;
                                            followedBy.remove(currentUser.getObjectId());
                                            mObj.put(MOMENTS_FOLLOWED_BY, followedBy);
                                            mObj.saveInBackground();
                                            Log.i(TAG, "FOLLOWED BY SAVED IN MOMENTS");
                                        }
                                        // error
                                    } else {
                                        simpleAlert(e.getMessage(), ctx);
                            }}});// ./ Remove currentUser's ID to followedBy in Moments


                        }});// ./ Dispatch async



                // MARK: - FOLLOW THIS USER
                // ------------------------------------------------
                } else {
                    // Update isFollowing for currentUser
                    assert isFollowing != null;
                    isFollowing.add(userObj.getObjectId());
                    currentUser.put(USER_IS_FOLLOWING, isFollowing);
                    currentUser.saveInBackground();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Add userObj to the Follow class
                            ParseObject fObj = new ParseObject(FOLLOW_CLASS_NAME);
                            fObj.put(FOLLOW_CURRENT_USER, currentUser);
                            fObj.put(FOLLOW_IS_FOLLOWING, userObj);
                            fObj.saveInBackground(new SaveCallback() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        followButt.setText("Following");
                                        followButt.setTextColor(Color.parseColor("#555555"));
                                    // error
                                    } else {
                                        simpleAlert(e.getMessage(), ctx);
                            }}});// ./ Add userObj to the Follow class


                            // Query Posts of userObj and follow them
                            ParseQuery<ParseObject>query2 = ParseQuery.getQuery(POSTS_CLASS_NAME);
                            query2.whereEqualTo(POSTS_USER_POINTER, userObj);
                            query2.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.size() != 0) {
                                            for (int i = 0; i < objects.size(); i++) {
                                                ParseObject pObj = objects.get(i);
                                                List<String> followedBy = pObj.getList(POSTS_FOLLOWED_BY);
                                                assert followedBy != null;
                                                followedBy.add(currentUser.getObjectId());
                                                pObj.put(POSTS_FOLLOWED_BY, followedBy);
                                                pObj.saveInBackground();
                                                Log.i(TAG, "FOLLOWED BY SAVED IN POSTS");
                                            }
                                        }
                                    // error
                                    } else {
                                    simpleAlert(e.getMessage(), ctx);
                            }}});// ./ Query Posts of userObj and follow them


                            // Add currentUser's ID to followedBy in Moments
                            ParseQuery<ParseObject>query3 = ParseQuery.getQuery(MOMENTS_CLASS_NAME);
                            query3.whereEqualTo(MOMENTS_USER_POINTER, userObj);
                            query3.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.size() != 0){
                                        for (int j=0; j<objects.size(); j++){
                                            ParseObject mObj = objects.get(j);
                                            List<String>followedBy = mObj.getList(MOMENTS_FOLLOWED_BY);
                                            assert followedBy != null;
                                            followedBy.add(currentUser.getObjectId());
                                            mObj.put(MOMENTS_FOLLOWED_BY, followedBy);
                                            mObj.saveInBackground();
                                            Log.i(TAG, "FOLLOWED BY SAVED IN MOMENTS");
                                        }
                                    }
                                // error
                                } else{
                                    simpleAlert(e.getMessage(), ctx);
                            }}});// ./ Add currentUser's ID to followedBy in Moments


                            // Set User as Not Interesting
                            List<String>notInterestingFor = userObj.getList(USER_NOT_INTERESTING_FOR);
                            assert notInterestingFor != null;
                            notInterestingFor.add(currentUser.getObjectId());
                            final HashMap<String, Object> params = new HashMap<>();
                            params.put("userId", userObj.getObjectId());
                            params.put("notInterestingFor", notInterestingFor);
                            ParseCloud.callFunctionInBackground("setUserNotInteresting", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {
                                    if (e != null) { Log.i(TAG, "ERROR: " + e.getMessage());
                            }}});


                            // Send Push notification
                            String pushMessage = "@" + currentUser.getString(USER_USERNAME) + " started following you";
                            sendPushNotification(pushMessage, userObj, ctx);

                    }});// ./ Dispatch async

                }// ./ if

            }});// ./ followButt





            // ------------------------------------------------
            // MARK: - OPTIONS BUTTON
            // ------------------------------------------------
            optionsButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Set Mute/Unmute title
                    final List<String>mutedBy = userObj.getList(USER_MUTED_BY);
                    String muteTitle;
                    assert mutedBy != null;
                    if (mutedBy.contains(currentUser.getObjectId()) ){ muteTitle = "Unmute Posts";
                    } else { muteTitle = "Mute Posts"; }

                    // Set Block/Unblock title
                    final List[] blockedBy = new List[]{ userObj.getList(USER_BLOCKED_BY) };
                    String blockTitle;
                    assert blockedBy[0] != null;
                    if (blockedBy[0].contains(currentUser.getObjectId()) ){ blockTitle = "Unblock User";
                    } else { blockTitle = "Block User"; }


                    // Fire Alert
                    AlertDialog.Builder alert  = new AlertDialog.Builder(ctx);
                    alert.setTitle("Select Option")
                            .setIcon(R.drawable.logo)
                            .setItems(new CharSequence[] {
                                            muteTitle,
                                            "Report User",
                                            blockTitle
                            }, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {


                                        // Mute/Unmute Posts --------------------------------------------------------------------------------
                                        case 0:
                                            String message;

                                            // Unmute Posts
                                            if (mutedBy.contains(currentUser.getObjectId()) ) {
                                                mutedBy.remove(currentUser.getObjectId());
                                                message = "You have unmuted @" + userObj.getString(USER_USERNAME) + "'s Posts";

                                            // Mute Posts
                                            } else {
                                                mutedBy.add(currentUser.getObjectId());
                                                message = "You have muted @" + userObj.getString(USER_USERNAME) + "'s Posts";
                                            }

                                            final HashMap<String, Object> params = new HashMap<>();
                                            params.put("userId", userObj.getObjectId());
                                            params.put("mutedBy", mutedBy);
                                            final String finalMessage = message;
                                            ParseCloud.callFunctionInBackground("muteUnmuteUser", params, new FunctionCallback<Object>() {
                                                @Override
                                                public void done(Object object, ParseException e) {
                                                    if (e == null) { simpleAlert(finalMessage, ctx);
                                                    } else { simpleAlert(e.getMessage(), ctx); }
                                            }});// ./ ParseCloud

                                        break;




                                        // Report user --------------------------------------------------------------
                                        case 1:
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                            alert.setMessage("Are you sure you want to report @" + userObj.getString(USER_USERNAME) + " as inappropriate?")
                                                .setTitle(R.string.app_name)
                                                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        showHUD(ctx);

                                                        final HashMap<String, Object> params = new HashMap<>();
                                                        params.put("userId", userObj.getObjectId());
                                                        params.put("reportMessage", "Inappropriate User");
                                                        ParseCloud.callFunctionInBackground("reportUser", params, new FunctionCallback<Object>() {
                                                            @Override
                                                            public void done(Object object, ParseException e) {
                                                                if (e == null) {

                                                                    // Query all Posts of this User and add currentUser's objectId to the reportedBy column
                                                                    ParseQuery<ParseObject>query = ParseQuery.getQuery(POSTS_CLASS_NAME);
                                                                    query.whereEqualTo(POSTS_USER_POINTER, userObj);
                                                                    query.findInBackground(new FindCallback<ParseObject>() {
                                                                        @Override
                                                                        public void done(List<ParseObject> objects, ParseException e) {
                                                                            if (e==null) {
                                                                                for (int i=0; i<objects.size(); i++){
                                                                                    ParseObject obj = objects.get(i);
                                                                                    List<String>reportedBy = obj.getList(POSTS_REPORTED_BY);
                                                                                    assert reportedBy != null;
                                                                                    reportedBy.add(currentUser.getObjectId());
                                                                                    obj.put(POSTS_REPORTED_BY, reportedBy);
                                                                                    obj.saveInBackground();
                                                                                }
                                                                            // error
                                                                            } else {
                                                                                hideHUD();
                                                                                simpleAlert(e.getMessage(), ctx);
                                                                    }}});


                                                                    // Query all Moments of this User and add currentUser's objectId to the reportedBy column
                                                                    ParseQuery<ParseObject>query2 = ParseQuery.getQuery(MOMENTS_CLASS_NAME);
                                                                    query2.whereEqualTo(MOMENTS_USER_POINTER, userObj);
                                                                    query2.findInBackground(new FindCallback<ParseObject>() {
                                                                        @Override
                                                                        public void done(List<ParseObject> objects, ParseException e) {
                                                                            if (e==null) {
                                                                                if (objects.size() != 0) {
                                                                                for (int i=0; i<objects.size(); i++) {
                                                                                    ParseObject obj = objects.get(i);
                                                                                    List<String> reportedBy = obj.getList(MOMENTS_REPORTED_BY);
                                                                                    assert reportedBy != null;
                                                                                    reportedBy.add(currentUser.getObjectId());
                                                                                    obj.put(MOMENTS_REPORTED_BY, reportedBy);
                                                                                    obj.saveInBackground();
                                                                                }
                                                                            }

                                                                            hideHUD();
                                                                            simpleAlert("Thanks for reporting @" + userObj.getString(USER_USERNAME) + ". We will check it out withint 24 hours!", ctx);

                                                                        // error
                                                                        } else {
                                                                            hideHUD();
                                                                            simpleAlert(e.getMessage(), ctx);
                                                                    }}});

                                                                // error
                                                                } else {
                                                                    hideHUD();
                                                                    Log.i(TAG, "ERROR: " + e.getMessage());
                                                        }}});
                                                }})
                                                .setNegativeButton("Cancel", null)
                                                .setCancelable(false)
                                                .setIcon(R.drawable.logo);
                                            alert.create().show();

                                            break;





                                            // Block/Unblock user --------------------------------------------------------------
                                            case 2:

                                                // Unblock User
                                                if (blockedBy[0].contains(currentUser.getObjectId()) ) {
                                                    blockedBy[0].remove(currentUser.getObjectId());

                                                    HashMap<String, Object> params2 = new HashMap<>();
                                                    params2.put("userId", userObj.getObjectId());
                                                    params2.put("blockedBy", blockedBy[0]);
                                                    ParseCloud.callFunctionInBackground("blockUnblockUser", params2, new FunctionCallback<Object>() {
                                                        @Override
                                                        public void done(Object object, ParseException e) {
                                                            if (e == null) {
                                                                simpleAlert("You have unblocked @" + userObj.getString(USER_USERNAME) + "!", ctx);
                                                             // error
                                                            } else { simpleAlert(e.getMessage(), ctx);
                                                    }}});// ./ ParseCloud



                                                // Block user
                                                } else {
                                                    AlertDialog.Builder alert2 = new AlertDialog.Builder(ctx);
                                                    alert2.setMessage("Are you sure you want to block @" + userObj.getString(USER_USERNAME) + "?\nHe/she won't be able to find your Posts, Moments and Profile on " + getString(R.string.app_name))
                                                            .setTitle(R.string.app_name)

                                                            .setPositiveButton("Block User", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    blockedBy[0] = userObj.getList(USER_BLOCKED_BY);
                                                                    blockedBy[0].add(currentUser.getObjectId());

                                                                    HashMap<String, Object> params3 = new HashMap<>();
                                                                    params3.put("userId", userObj.getObjectId());
                                                                    params3.put("blockedBy", blockedBy[0]);
                                                                    ParseCloud.callFunctionInBackground("blockUnblockUser", params3, new FunctionCallback<Object>() {
                                                                        @Override
                                                                        public void done(Object object, ParseException e) {
                                                                            if (e == null) {
                                                                                simpleAlert("You have blocked @" + userObj.getString(USER_USERNAME) + "!", ctx);
                                                                            // error
                                                                            } else { simpleAlert(e.getMessage(), ctx);
                                                                    }}});// ./ ParseCloud
                                                            }})

                                                            .setNegativeButton("Cancel", null)
                                                            .setCancelable(false)
                                                            .setIcon(R.drawable.logo);
                                                    alert2.create().show();

                                                }// ./ If

                                                break;

                            }}})
                            .setNegativeButton("Cancel", null);
                    alert.create().show();

            }});// ./ optionsButt


        } catch (ParseException e) { e.printStackTrace(); }



        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.upBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }// ./ onCreate





    // ------------------------------------------------
    // MARK: - SHOW USER'S DETAILS
    // ------------------------------------------------
    @SuppressLint("SetTextI18n")
    void showUserDetails() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Check if currentUser is following this User
        List<String>isFollowing = currentUser.getList(USER_IS_FOLLOWING);
        assert isFollowing != null;
        if (isFollowing.contains(userObj.getObjectId()) ){
            followButt.setText("Following");
            followButt.setTextColor(Color.parseColor("#555555"));
        } else {
            followButt.setText("Follow");
            followButt.setTextColor(Color.parseColor(MAIN_COLOR));
        }

        // Verified Badge
        if (userObj.getBoolean(USER_IS_VERIFIED) ){
            verifiedBadge.setVisibility(View.VISIBLE);
        } else { verifiedBadge.setVisibility(View.INVISIBLE); }

        // Avatar
        getParseImage(avatarImg, userObj, USER_AVATAR);

        // Full name
        fullnameTxt.setText(userObj.getString(USER_FULLNAME));

        // Username
        usernameTxt.setText("@" + userObj.getString(USER_USERNAME));

        // Bio
        if (userObj.getString(USER_BIO) != null) { bioTxt.setText(userObj.getString(USER_BIO));
        } else { bioTxt.setText(""); }

        // Website
        if (userObj.getString(USER_WEBSITE) != null) { websiteButt.setText(userObj.getString(USER_WEBSITE));
        } else { websiteButt.setText(""); }
    }





    // ------------------------------------------------
    // MARK: - QUERY FOLLOWERS & FOLLOWING PEOPLE
    // ------------------------------------------------
    void queryFollowers() {
        ParseQuery<ParseObject>query = ParseQuery.getQuery(FOLLOW_CLASS_NAME);
        query.whereEqualTo(FOLLOW_IS_FOLLOWING, userObj);
        query.setSkip(skip);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    followersArray.addAll(objects);
                    if (objects.size() == 100) {
                        skip = skip + 100;
                        queryFollowers();
                    }

                    int followers = followersArray.size();
                    followersCountTxt.setText(roundLargeNumber(followers));

                // error
                } else { simpleAlert(e.getMessage(), ctx);
        }}});// ./ query Followers
    }


    void queryFollowing() {
        ParseQuery<ParseObject>query = ParseQuery.getQuery(FOLLOW_CLASS_NAME);
        query.whereEqualTo(FOLLOW_CURRENT_USER, userObj);
        query.setSkip(skip2);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    followingArray.addAll(objects);
                    if (objects.size() == 100) {
                        skip2 = skip2 + 100;
                        queryFollowing();
                    }

                    int following = followingArray.size();
                    followingCountTxt.setText(roundLargeNumber(following));

                // error
                } else { simpleAlert(e.getMessage(), ctx);
        }}});// ./ query Following
    }




    //-----------------------------------------------
    // MARK - QUERY POSTS
    //-----------------------------------------------
    void queryPosts() {
        showHUD(ctx);

        ParseQuery<ParseObject>query = ParseQuery.getQuery(POSTS_CLASS_NAME);
        query.whereEqualTo(POSTS_USER_POINTER, userObj);
        query.setSkip(skip3);
        query.orderByDescending(POSTS_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    hideHUD();

                    postsArray.addAll(objects);
                    if (objects.size() == 100) {
                        skip3 = skip3 + 100;
                        queryPosts();
                    }

                    showDataInGridView();

                    // Posts number
                    if (postsArray.size() != 0) {
                        int posts = postsArray.size();
                        postsCountTxt.setText(String.valueOf(posts));
                    } else {
                        postsCountTxt.setText("0");
                    }

                // error
                } else {
                    hideHUD();
                    simpleAlert(e.getMessage(), ctx);
        }}});
    }


    // ------------------------------------------------
    // MARK: - SHOW DATA IN GRIDVIEW
    // ------------------------------------------------
    void showDataInGridView() {
        class GridAdapter extends BaseAdapter {
            private Context context;
            private GridAdapter(Context context) {
                super();
                this.context = context;
            }

            // CONFIGURE CELL
            @SuppressLint("InflateParams")
            @Override public View getView(int position, View cell, ViewGroup parent) {
                if (cell == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    cell = inflater.inflate(R.layout.cell_post_search, null);
                }

                //-----------------------------------------------
                // MARK - INITIALIZE VIEWS
                //-----------------------------------------------
                RelativeLayout cellLayout = cell.findViewById(R.id.cpsCellLayout);
                ImageView postImg = cell.findViewById(R.id.cpsPostImg);
                ImageView videoicon = cell.findViewById(R.id.cpsVideoIcon);

                // Set cell size
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) ctx).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                cellLayout.getLayoutParams().height = width / 3; // num of columns


                // Parse Object
                ParseObject pObj = postsArray.get(position);

                // Post Image
                getParseImage(postImg, pObj, POSTS_IMAGE);

                // Check if Post is a Video -> show video icon
                if (pObj.getParseFile(POSTS_VIDEO) != null){ videoicon.setVisibility(View.VISIBLE);
                } else { videoicon.setVisibility(View.INVISIBLE); }


            return cell;
            }
                @Override public int getCount() { return postsArray.size(); }
                @Override public Object getItem(int position) { return postsArray.get(position); }
                @Override public long getItemId(int position) { return position; }
        }

        // Set Adapter
        postsGridView.setAdapter(new GridAdapter(ctx));
        postsGridView.setNumColumns(3); // num of columns


        //-----------------------------------------------
        // MARK - SHOW POST'S DETAILS
        //-----------------------------------------------
        postsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Parse Object
                ParseObject pObj = postsArray.get(position);
                Intent i = new Intent(ctx, PostDetails.class);
                Bundle extras = new Bundle();
                extras.putString("objectID", pObj.getObjectId());
                i.putExtras(extras);
                startActivity(i);
        }});
    }




    //-----------------------------------------------
    // MARK - REFRESH DATA
    //-----------------------------------------------
    @Override
    public void onRefresh() {
        resetVariables();

        queryPosts();
        if (refreshControl.isRefreshing()) { refreshControl.setRefreshing(false); }
    }


    //-----------------------------------------------
    // MARK - RESET VARIABLES
    //-----------------------------------------------
    void resetVariables() {
        skip = 0;
        skip2 = 0;
        skip3 = 0;
        followersArray = new ArrayList<>();
        followingArray = new ArrayList<>();
        postsArray = new ArrayList<>();
    }

}// ./ end
