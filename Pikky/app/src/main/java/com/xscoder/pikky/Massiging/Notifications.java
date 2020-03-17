package com.xscoder.pikky.Massiging;

/*==================================================
     Pikky

     © XScoder 2018
     All Rights reserved

     RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
     YOU WILL BE LEGALLY PROSECUTED

==================================================*/

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xscoder.pikky.Camera.SquareCamera;
import com.xscoder.pikky.Home.Activities.Home;
import com.xscoder.pikky.R;
import com.xscoder.pikky.Search.SearchScreen;
import com.xscoder.pikky.Userprofile.Account;
import com.xscoder.pikky.Userprofile.UserProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.xscoder.pikky.Configurations.NOTIFICATIONS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.NOTIFICATIONS_CREATED_AT;
import static com.xscoder.pikky.Configurations.NOTIFICATIONS_CURRENT_USER;
import static com.xscoder.pikky.Configurations.NOTIFICATIONS_OTHER_USER;
import static com.xscoder.pikky.Configurations.NOTIFICATIONS_TEXT;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BLOCKED_BY;
import static com.xscoder.pikky.Configurations.USER_CLASS_NAME;
import static com.xscoder.pikky.Configurations.USER_CREATED_AT;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_IS_VERIFIED;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;
import static com.xscoder.pikky.Configurations.timeAgoSinceDate;

public class Notifications extends AppCompatActivity {

    /*--- VIEWS ---*/
    TextView titletxt;
    ListView notificationsListView;
    Button backButt;




    /*--- VARIABLES ---*/
    Context ctx = this;
    List<ParseObject>notificationsArray = new ArrayList<>();
    int skip = 0;
    boolean isBlockedUsers;

    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions= new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO
    };



    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITLIZE VIEWS
        //-----------------------------------------------
        titletxt = findViewById(R.id.notTitleTxt);
        backButt = findViewById(R.id.notBackButt);
        notificationsListView = findViewById(R.id.notNotificationsListView);




        //-----------------------------------------------
        // MARK - TAB BAR BUTTONS
        //-----------------------------------------------
        Button tab1 = findViewById(R.id.tab_one);
        Button tab2 = findViewById(R.id.tab_two);
        Button tab3 = findViewById(R.id.tab_three);
        Button tab4 = findViewById(R.id.tab_five);

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {  startActivity(new Intent(ctx, Home.class));  }});

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  startActivity(new Intent(ctx, SearchScreen.class));  }});

        tab3.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {  if (checkPermissions()) { startActivity(new Intent(ctx, SquareCamera.class));  }}});

        tab4.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {  startActivity(new Intent(ctx, Account.class));  }});



        // Get data
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        isBlockedUsers = extras.getBoolean("isBlockedUsers");
        Log.i(TAG, "isBlockedUsers: " + isBlockedUsers);


        // Query Notifications
        queryNotifications();



        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }});


    }// ./ onCreate





    //-----------------------------------------------
    // MARK - QUERY NOTIFICATIONS
    //-----------------------------------------------
    @SuppressLint("SetTextI18n")
    void queryNotifications() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> currObjID = new ArrayList<>();
        currObjID.add(currentUser.getObjectId());
        showHUD(ctx);

        // Query Notifications
        if (!isBlockedUsers) {
            titletxt.setText("Notifications");
            backButt.setVisibility(View.INVISIBLE);

            // Query
            ParseQuery<ParseObject> query = ParseQuery.getQuery(NOTIFICATIONS_CLASS_NAME);
            query.whereEqualTo(NOTIFICATIONS_OTHER_USER, currentUser);
            query.orderByDescending(NOTIFICATIONS_CREATED_AT);
            query.setSkip(skip);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        hideHUD();

                        notificationsArray.addAll(objects);
                        if (objects.size() == 100) {
                            skip = skip + 100;
                            queryNotifications();
                        }

                        showDataInListView();
                    // error
                    } else {
                        hideHUD();
                        simpleAlert(e.getMessage(), ctx);
            }}});


        // Query Users blocked by currentUser
        } else {
            titletxt.setText("Blocked users");
            backButt.setVisibility(View.VISIBLE);

            // Query
            ParseQuery<ParseObject>query = ParseQuery.getQuery(USER_CLASS_NAME);
            query.whereContainedIn(USER_BLOCKED_BY, currObjID);
            query.orderByDescending(USER_CREATED_AT);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> users, ParseException e) {
                    if (e == null) {
                        hideHUD();
                        notificationsArray = users;
                        showDataInListView();
                    // error
                    } else {
                        hideHUD();
                        simpleAlert(e.getMessage(), ctx);
                    }
                }
            });

        }// ./ If

    }




    //-----------------------------------------------
    // MARK - SHOW DATA IN LISTVIEW
    //-----------------------------------------------
    void showDataInListView() {
        class SwipeListAdapter extends BaseSwipeAdapter {
            private Context mContext;
            private SwipeListAdapter(Context mContext) {
                this.mContext = mContext;
            }
            @Override public int getSwipeLayoutResourceId(int position) {
                return R.id.cSwipeLayout;
            }

            // SWIPE ITEMS ----------------------------------------------------------
            @Override
            public View generateView(final int position, ViewGroup parent) {
                @SuppressLint("InflateParams") View swipeSection = LayoutInflater.from(mContext).inflate(R.layout.cell_notification, null);
                SwipeLayout swipeLayout = swipeSection.findViewById(getSwipeLayoutResourceId(position));
                swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {}});


                //-----------------------------------------------
                // MARK - DELETE NOTIFICATION
                //-----------------------------------------------
                if (!isBlockedUsers) {
                    swipeSection.findViewById(R.id.cnotDeleteButt).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ParseObject nObj = notificationsArray.get(position);
                            nObj.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    // Reload query
                                    // queryNotifications();

                                    notificationsArray.remove(position);
                                    notificationsListView.invalidateViews();
                                    notificationsListView.refreshDrawableState();
                            }});

                    }});// ./ swipeSection

                }// ./ If isBlockedUsers

            return swipeSection;
            }


            // CELL -------------------------------------------------------
            @SuppressLint("SetTextI18n")
            @Override
            public void fillValues(final int position, View cell) {

                // Parse Object
                final ParseObject nObj = notificationsArray.get(position);

                //-----------------------------------------------
                // MARK - INITIALIZE VIEWS
                //-----------------------------------------------
                final CircleImageView avatarImg = cell.findViewById(R.id.cnotAvatarimg);
                final ImageView verifiedBadge = cell.findViewById(R.id.cnotVerifiedBadge);
                final TextView usernameTxt = cell.findViewById(R.id.cnotUsernameTxt);
                usernameTxt.setTypeface(osBold);
                final TextView dateTxt = cell.findViewById(R.id.cnotDateTxt);
                dateTxt.setTypeface(osRegular);
                final TextView notificationTxt = cell.findViewById(R.id.cnotNotificationTxt);
                notificationTxt.setTypeface(osRegular);



                // Show Notifications --------------------------------------------
                if (!isBlockedUsers) {

                    // userPointer
                    Objects.requireNonNull(nObj.getParseObject(NOTIFICATIONS_CURRENT_USER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        public void done(final ParseObject userPointer, ParseException e) {
                            if (e == null) {

                                // Verified Badge
                                boolean isVerified = userPointer.getBoolean(USER_IS_VERIFIED);
                                if (isVerified) {
                                    verifiedBadge.setVisibility(View.VISIBLE);
                                } else {
                                    verifiedBadge.setVisibility(View.INVISIBLE);
                                }
                                // Avatar
                                getParseImage(avatarImg, userPointer, USER_AVATAR);
                                // Username
                                usernameTxt.setText("@" + userPointer.getString(USER_USERNAME));
                                // Date
                                Date nDate = nObj.getCreatedAt();
                                dateTxt.setText(timeAgoSinceDate(nDate));
                                // Notification
                                notificationTxt.setText(nObj.getString(NOTIFICATIONS_TEXT));


                                //-----------------------------------------------
                                // MARK - SHOW USER'S PROFILE
                                //-----------------------------------------------
                                avatarImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(ctx, UserProfile.class);
                                        Bundle extras = new Bundle();
                                        extras.putString("objectID", userPointer.getObjectId());
                                        i.putExtras(extras);
                                        startActivity(i);
                                }});

                            // error
                            } else {  simpleAlert(e.getMessage(), ctx);
                    }}});// end userPointer




                // Show Blocked Users --------------------------------------------
                } else {
                    final ParseUser uObj = (ParseUser) notificationsArray.get(position);

                    // Avatar
                    getParseImage(avatarImg, uObj, USER_AVATAR);
                    // Username
                    usernameTxt.setText("@" + uObj.getString(USER_USERNAME));
                    // Full name
                    dateTxt.setText(uObj.getString(USER_FULLNAME));
                    // Tap to Unblock
                    notificationTxt.setText("Tap Avatar to Unblock");

                    //-----------------------------------------------
                    // MARK - UNBLOCK USER
                    //-----------------------------------------------
                    avatarImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                            alert.setMessage("Do you want to unblock @" + uObj.getString(USER_USERNAME) + "?")
                                .setTitle(R.string.app_name)

                                .setPositiveButton("Unblock", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showHUD(ctx);

                                        ParseUser currentUser = ParseUser.getCurrentUser();
                                        List<String>blockedBy = uObj.getList(USER_BLOCKED_BY);
                                        assert blockedBy != null;
                                        blockedBy.remove(currentUser.getObjectId());

                                        final HashMap<String, Object> params = new HashMap<>();
                                        params.put("userId", uObj.getObjectId());
                                        params.put("blockedBy", blockedBy);
                                        ParseCloud.callFunctionInBackground("blockUnblockUser", params, new FunctionCallback<Object>() {
                                            @Override
                                            public void done(Object object, ParseException e) {
                                                if (e == null) {
                                                    hideHUD();
                                                    simpleAlert("You have unblocked @" + uObj.getString(USER_USERNAME) + "!", ctx);

                                                    notificationsArray.remove(position);
                                                    notificationsListView.invalidateViews();
                                                    notificationsListView.refreshDrawableState();

                                                // error
                                                } else {
                                                    hideHUD();
                                                    simpleAlert(e.getMessage(), ctx);
                                        }}});// ./ ParseCloud
                                }})

                                .setNegativeButton("Cancel", null)
                                .setCancelable(false)
                                .setIcon(R.drawable.logo);
                            alert.create().show();
                    }});
                }

            }

            @Override public int getCount() { return notificationsArray.size(); }
            @Override public Object getItem(int position) { return notificationsArray.get(position); }
            @Override public long getItemId(int position) { return position; }
        }

        // Set adapter
        notificationsListView.setAdapter(new SwipeListAdapter(ctx));
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

}// ./ end
