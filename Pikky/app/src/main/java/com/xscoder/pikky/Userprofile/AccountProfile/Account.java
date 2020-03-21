package com.xscoder.pikky.Userprofile.AccountProfile;

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
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import com.xscoder.pikky.Camera.SquareCamera;
import com.xscoder.pikky.Home.Activities.Home;
import com.xscoder.pikky.Massiging.Notifications;
import com.xscoder.pikky.R;
import com.xscoder.pikky.Search.SearchScreen;
import com.xscoder.pikky.Userprofile.AccountProfile.Fragments.AccountProfilePagerAdapter;
import com.xscoder.pikky.Userprofile.EditProfile;
import com.xscoder.pikky.loginSignUp.Activity.Intro;
import com.xscoder.pikky.loginSignUp.ModalClasses.LoginResponse;
import com.xscoder.pikky.setting.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import API.API_URLS;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.xscoder.pikky.Configurations.MULTIPLE_PERMISSIONS;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.permissions;


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
    List<ParseObject> followersArray = new ArrayList<>();
    List<ParseObject> followingArray = new ArrayList<>();
    int skip, skip2, skip3 = 0;
    boolean isCurrentUser = false;
    int screenW, screenH;

    LoginResponse userDetail;
    // Code by Mehraj
    TabLayout tabLayout;
    ViewPager viewPager;

    //-----------------------------------------------
    // MARK - ON START
    //-----------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();

        // Call Queries
        // showUserDetails(); commented by @mehraj


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // queryFollowers();
                //queryFollowing();

            }
        }, 500);


        //CODE BY @MEHRAJ
        // Get User Details
        SharedPreferences sp = getSharedPreferences("LoginResponse", Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("LoginResponse", "");
        this.userDetail = gson.fromJson(json, LoginResponse.class);

        if (userDetail != null) {
            if (!(userDetail.isUserExists())) {
                startActivity(new Intent(ctx, Intro.class));
                this.finish();
                // Toast.makeText(this,userDetail.getUserProfile().getFirst_name(),Toast.LENGTH_LONG).show();

                //-----------------------------------------------
                // MARK - CURRENT USER IS LOGGED IN
                //-----------------------------------------------
            } else {
                showUserDetails();
            }
        }


    }


    // ------------------------------------------------
    // MARK: - QUERY FOLLOWERS & FOLLOWING PEOPLE
    // ------------------------------------------------

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

        this.getSupportActionBar().hide();


        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenH = displayMetrics.heightPixels;
        screenW = displayMetrics.widthPixels;
        Log.i("log-", "SCREEN'S WIDTH: " + screenW);
        Log.i("log-", "SCREEN'S HEIGHT: " + screenH);


        //   final ParseUser currentUser = ParseUser.getCurrentUser();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        settingsButt = findViewById(R.id.accSettingsButt);
        backButt = findViewById(R.id.accBackButt);
        avatarImg = findViewById(R.id.accAvatarImg);
        fullnameTxt = findViewById(R.id.accFullNameTxt);
        verifiedBadge = findViewById(R.id.accVerifiedBadge);

        //fullnameTxt.setTypeface(R.font.montserrat);
        usernameTxt = findViewById(R.id.accUsernameTxt);
        usernameTxt.setTypeface(osRegular);
        initTabLayout();
        editProfileButton();


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
            }
        });

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, SearchScreen.class));
            }
        });

        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    startActivity(new Intent(ctx, SquareCamera.class));
                }
            }
        });

        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Notifications.class));
            }
        });


        //-----------------------------------------------
        // MARK - BACK BUTTON {If Removed}
        //-----------------------------------------------

        backButt.setVisibility(View.VISIBLE);
        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //-----------------------------------------------
        // MARK - SETTINGS BUTTON
        //-----------------------------------------------
        settingsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Settings.class));
            }
        });


    }// ./ onCreate

    // ------------------------------------------------
    // MARK: - SHOW USER'S DETAILS
    // ------------------------------------------------
    @SuppressLint("SetTextI18n")
    void showUserDetails() {


        //@mehraj
        try {
            Picasso.get().load(API_URLS.BASE_URL + "/" + userDetail.getUserProfile().getEmail_id() + ".png").placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(avatarImg);
            //   Toast.makeText(this,"USER"+userDetail.getUserProfile().getHiquik_id()+ userDetail.getUserProfile().getFirst_name(),Toast.LENGTH_LONG).show();
            fullnameTxt.setText(userDetail.getUserProfile().getFirst_name() + " " + userDetail.getUserProfile().getLast_name());
            usernameTxt.setText("@" + userDetail.getUserProfile().getHiquik_id());
            //    verifiedBadge.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }


    }

    //-----------------------------------------------
    // MARK - REFRESH DATA
    //-----------------------------------------------
    @Override
    public void onRefresh() {
        //resetVariables();
        //queryPosts();
        //if (refreshControl.isRefreshing()) { refreshControl.setRefreshing(false); }
    }

    //-----------------------------------------------
    // MARK - CHECK FOR PERMISSIONS
    //-----------------------------------------------
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), MULTIPLE_PERMISSIONS);
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

    void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_account);
        viewPager = (ViewPager) findViewById(R.id.viewPager_account);
        tabLayout.addTab(tabLayout.newTab().setText("About"));
        tabLayout.addTab(tabLayout.newTab().setText("Fallowing"));
        tabLayout.addTab(tabLayout.newTab().setText("Media"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        roundCornerCoverImage();

        final AccountProfilePagerAdapter myadapter = new AccountProfilePagerAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(myadapter);
        //  viewPager.setAdapter(myadapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    void roundCornerCoverImage() {
        ImageView img = findViewById(R.id.profile_cover_img);

        Bitmap mbitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.cover)).getBitmap();
        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 70, 70, mpaint);

        img.setImageBitmap(imageRounded);

        android.view.ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
        layoutParams.width = Resources.getSystem().getDisplayMetrics().widthPixels;

        // will be changed when image will be loaded from server
        // layoutParams.height = 80;
        img.setLayoutParams(layoutParams);
    }


    void editProfileButton() {
        TextView btn_edit_profile = findViewById(R.id.profile_edit_btn_tv);
        try {
            btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Account.this, EditProfile.class));
                }
            });
        } catch (Exception e) {
        }

    }
}// ./end
