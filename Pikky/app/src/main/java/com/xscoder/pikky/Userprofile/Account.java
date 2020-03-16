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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xscoder.pikky.Home.Home;
import com.xscoder.pikky.Massiging.Follow;
import com.xscoder.pikky.Massiging.Notifications;
import com.xscoder.pikky.posts.PostDetails;
import com.xscoder.pikky.R;
import com.xscoder.pikky.Search.SearchScreen;
import com.xscoder.pikky.setting.Settings;
import com.xscoder.pikky.Camera.SquareCamera;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.xscoder.pikky.Configurations.MULTIPLE_PERMISSIONS;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.FOLLOW_CLASS_NAME;
import static com.xscoder.pikky.Configurations.FOLLOW_CURRENT_USER;
import static com.xscoder.pikky.Configurations.FOLLOW_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.POSTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.POSTS_CREATED_AT;
import static com.xscoder.pikky.Configurations.POSTS_IMAGE;
import static com.xscoder.pikky.Configurations.POSTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.POSTS_VIDEO;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BIO;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_IS_VERIFIED;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.USER_WEBSITE;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.permissions;
import static com.xscoder.pikky.Configurations.roundLargeNumber;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class Account extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /*--- VIEWS ---*/
    Button followersButt, followingButt, websiteButt, settingsButt, backButt;
    CircleImageView avatarImg;
    TextView fullnameTxt, usernameTxt, bioTxt, followingCountTxt, followersCountTxt, postsCountTxt;
    GridView postsGridView;
    SwipeRefreshLayout refreshControl;
    ImageView verifiedBadge;


    /*--- VARIABLES ---*/
    Context ctx = this;
    List<ParseObject> postsArray = new ArrayList<>();
    List<ParseObject>followersArray = new ArrayList<>();
    List<ParseObject>followingArray = new ArrayList<>();
    int skip, skip2, skip3 = 0;
    boolean isCurrentUser = false;
    int screenW, screenH;


    //-----------------------------------------------
    // MARK - ON START
    //-----------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();

        resetVariables();

        // Call Queries
        showUserDetails();

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
        setContentView(R.layout.account);
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


        final ParseUser currentUser = ParseUser.getCurrentUser();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        settingsButt = findViewById(R.id.accSettingsButt);
        backButt = findViewById(R.id.accBackButt);
        avatarImg = findViewById(R.id.accAvatarImg);
        fullnameTxt = findViewById(R.id.accFullNameTxt);
        fullnameTxt.setTypeface(osBold);
        usernameTxt = findViewById(R.id.accUsernameTxt);
        usernameTxt.setTypeface(osRegular);
        bioTxt = findViewById(R.id.accBioTxt);
        bioTxt.setTypeface(osRegular);
        followingCountTxt = findViewById(R.id.accFollowingCountTxt);
        followingCountTxt.setTypeface(osBold);
        followersCountTxt = findViewById(R.id.accFollowersCountTxt);
        followersCountTxt.setTypeface(osBold);
        postsCountTxt = findViewById(R.id.accPostsCountTxt);
        postsCountTxt.setTypeface(osBold);
        followersButt = findViewById(R.id.accFollowersButt);
        followingButt = findViewById(R.id.accFollowingButt);
        websiteButt = findViewById(R.id.accWebsiteButt);
        websiteButt.setTypeface(osRegular);
        verifiedBadge = findViewById(R.id.accVerifiedBadge);
        postsGridView = findViewById(R.id.accPostsGridView);

        // RefreshControl
        refreshControl = findViewById(R.id.refreshControl);
        refreshControl.setOnRefreshListener(this);



        //-----------------------------------------------
        // MARK - TAB BAR BUTTONS
        //-----------------------------------------------
        Button tab1 = findViewById(R.id.tab_one);
        Button tab2 = findViewById(R.id.tab_two);
        Button tab3 = findViewById(R.id.tab_three);
        Button tab4 = findViewById(R.id.tab_four);

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Home.class));
            }});

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, SearchScreen.class));
            }});

        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) { startActivity(new Intent(ctx, SquareCamera.class)); }
            }});

        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Notifications.class));
        }});



        // Get extras
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        isCurrentUser = extras.getBoolean("isCurrentUser");


        // Call query
        queryPosts();


        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        if (isCurrentUser) {
            backButt.setVisibility(View.VISIBLE);
            backButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { finish(); }});
        }



        //-----------------------------------------------
        // MARK - SETTINGS BUTTON
        //-----------------------------------------------
        settingsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Settings.class));
        }});



        // ------------------------------------------------
        // MARK: - FOLLOWERS BUTTON
        // ------------------------------------------------
        followersButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, Follow.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isFollowers", true);
                extras.putString("objectID", currentUser.getObjectId());
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
                extras.putString("objectID", currentUser.getObjectId());
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



    }// ./ onCreate




    // ------------------------------------------------
    // MARK: - SHOW USER'S DETAILS
    // ------------------------------------------------
    @SuppressLint("SetTextI18n")
    void showUserDetails() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Verified Badge
        if (currentUser.getBoolean(USER_IS_VERIFIED) ){
            verifiedBadge.setVisibility(View.VISIBLE);
        } else { verifiedBadge.setVisibility(View.INVISIBLE); }
        // Avatar
        getParseImage(avatarImg, currentUser, USER_AVATAR);
        // Full name
        fullnameTxt.setText(currentUser.getString(USER_FULLNAME));
        // Username
        usernameTxt.setText("@" + currentUser.getString(USER_USERNAME));
        // Bio
        if (currentUser.getString(USER_BIO) != null) { bioTxt.setText(currentUser.getString(USER_BIO));
        } else { bioTxt.setText(""); }
        // Website
        if (currentUser.getString(USER_WEBSITE) != null) { websiteButt.setText(currentUser.getString(USER_WEBSITE));
        } else { websiteButt.setText(""); }
    }





    // ------------------------------------------------
    // MARK: - QUERY FOLLOWERS & FOLLOWING PEOPLE
    // ------------------------------------------------
    void queryFollowers() {
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // Query Followers
        ParseQuery<ParseObject> query = ParseQuery.getQuery(FOLLOW_CLASS_NAME);
        query.whereEqualTo(FOLLOW_IS_FOLLOWING, currentUser);
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
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // Query Following
        ParseQuery<ParseObject>query = ParseQuery.getQuery(FOLLOW_CLASS_NAME);
        query.whereEqualTo(FOLLOW_CURRENT_USER, currentUser);
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
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject>query = ParseQuery.getQuery(POSTS_CLASS_NAME);
        query.whereEqualTo(POSTS_USER_POINTER, currentUser);
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



    // -----------------------------------------
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
        postsGridView.setNumColumns(3);


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
    // MARK - CHECK FOR PERMISSIONS
    //-----------------------------------------------
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "ALL PERMISSIONS GRANTED!");

                // Enter SquareCamera screen
                startActivity(new Intent(ctx, SquareCamera.class));

            } else {
                StringBuilder perStr = new StringBuilder();
                for (String per : permissions) {
                    perStr.append("\n").append(per);
                }
            }
        }
    }

}// ./end
