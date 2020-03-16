package com.xscoder.pikky.Massiging;

/*//////////////////////////////////////////////////////
     Pikky

     © XScoder 2018
     All Rights reserved

     RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
     YOU WILL BE LEGALLY PROSECUTED

//////////////////////////////////////////////////////*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xscoder.pikky.R;
import com.xscoder.pikky.Userprofile.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.xscoder.pikky.Configurations.FOLLOW_CLASS_NAME;
import static com.xscoder.pikky.Configurations.FOLLOW_CURRENT_USER;
import static com.xscoder.pikky.Configurations.FOLLOW_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.MOMENTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.MOMENTS_FOLLOWED_BY;
import static com.xscoder.pikky.Configurations.MOMENTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.POSTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.POSTS_FOLLOWED_BY;
import static com.xscoder.pikky.Configurations.POSTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BIO;
import static com.xscoder.pikky.Configurations.USER_BLOCKED_BY;
import static com.xscoder.pikky.Configurations.USER_CLASS_NAME;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.USER_IS_VERIFIED;
import static com.xscoder.pikky.Configurations.USER_NOT_INTERESTING_FOR;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.sendPushNotification;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

import de.hdodenhof.circleimageview.CircleImageView;

public class Follow extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /*--- VIEWS ---*/
    TextView titleTxt;
    ListView folowListView;
    SwipeRefreshLayout refreshControl;



    /*--- VARIABLES ---*/
    Context ctx = this;
    ParseUser userObj;
    boolean isFollowers;
    List<ParseObject>followArray = new ArrayList<>();
    int skip = 0;





    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        titleTxt = findViewById(R.id.follTitleTxt);
        folowListView = findViewById(R.id.follFollowListView);

        // RefreshControl
        refreshControl = findViewById(R.id.refreshControl);
        refreshControl.setOnRefreshListener(this);



        // Get data
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        isFollowers = extras.getBoolean("isFollowers");
        String objectID = extras.getString("objectID");

        userObj = (ParseUser) ParseUser.createWithoutData(USER_CLASS_NAME, objectID);
        try { userObj.fetchIfNeeded().getParseUser(USER_CLASS_NAME);

            // Log.i(TAG, "USER OBJ: " + userObj.getString(USER_USERNAME) + " -- IS FOLLOWERS: " + isFollowers);

            // Query Followers OR Following
            queryFollowersOrFollowing();

        } catch (ParseException e) { e.printStackTrace(); }



        //-----------------------------------------------
        // MARK - BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.follBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }// ./ onCreate




    // ------------------------------------------------
    // MARK: - QUERY FOLLOWERS OR FOLLOWING
    // ------------------------------------------------
    @SuppressLint("SetTextI18n")
    void queryFollowersOrFollowing() {
        showHUD(ctx);

        ParseQuery<ParseObject>query = ParseQuery.getQuery(FOLLOW_CLASS_NAME);

        // Query Followers
        if (isFollowers) {
            titleTxt.setText("Followers");
            query.whereEqualTo(FOLLOW_IS_FOLLOWING, userObj);

        // Query Following
        } else {
            titleTxt.setText("Following");
            query.whereEqualTo(FOLLOW_CURRENT_USER, userObj);
        }

        // Launch Query
        query.setSkip(skip);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    hideHUD();

                    followArray.addAll(objects);
                    if (objects.size() == 100) {
                        skip = skip + 100;
                        queryFollowersOrFollowing();
                    }

                    showDataInListView();

                // error
                } else {
                    hideHUD();
                    simpleAlert(e.getMessage(), ctx);
        }}});
    }


    //-----------------------------------------------
    // MARK - SHOW DATA IN LISTVIEW
    //-----------------------------------------------
    void showDataInListView() {
        class ListAdapter extends BaseAdapter {
             private Context context;
             private ListAdapter(Context context) {
                 super();
                 this.context = context;
             }

             // CONFIGURE CELL
             @SuppressLint("InflateParams")
             @Override
             public View getView(int position, View cell, ViewGroup parent) {
                 if (cell == null) {
                     LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                     assert inflater != null;
                     cell = inflater.inflate(R.layout.cell_people, null);
                 }

                 //-----------------------------------------------
                 // MARK - INITIALIZE VIEWS
                 //-----------------------------------------------
                 final CircleImageView avatarImg = cell.findViewById(R.id.cpAvatarImg);
                 final TextView usernameTxt = cell.findViewById(R.id.cpUsernameTxt);
                 usernameTxt.setTypeface(osBold);
                 final TextView fullnameTxt = cell.findViewById(R.id.cpFullnameTxt);
                 fullnameTxt.setTypeface(osRegular);
                 final TextView bioTxt = cell.findViewById(R.id.cpBioTxt);
                 bioTxt.setTypeface(osRegular);
                 final Button followButt = cell.findViewById(R.id.cpFollowButt);
                 final ImageView verifiedBadge = cell.findViewById(R.id.cpVerifiedBadge);


                 // Parse Object
                 ParseObject fObj = followArray.get(position);
                 final ParseUser currentUser = ParseUser.getCurrentUser();

                 // Get userPointer
                 ParseUser user;
                 if (isFollowers) {
                     user = fObj.getParseUser(FOLLOW_CURRENT_USER);
                 } else {
                     user = fObj.getParseUser(FOLLOW_IS_FOLLOWING);
                 }
                 assert user != null;
                 user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                     @SuppressLint("SetTextI18n")
                     @Override public void done(final ParseObject userPointer, ParseException e) {
                         if (e == null) {

                             // Hide followButton in case userPointer == currentUser
                             if (userPointer.getObjectId().matches(currentUser.getObjectId()) ){
                                 followButt.setVisibility(View.INVISIBLE);
                             }

                             // This User has blocked you
                             final List<String>blockedBy = currentUser.getList(USER_BLOCKED_BY);
                             assert blockedBy != null;
                             if (blockedBy.contains(userPointer.getObjectId()) ){
                                 usernameTxt.setText(userPointer.getString(USER_USERNAME));
                                 fullnameTxt.setText("UNAVAILABLE");
                                 bioTxt.setText("");
                                 avatarImg.setImageBitmap(null);
                                 avatarImg.setBackgroundColor(Color.parseColor("#555555"));
                                 followButt.setVisibility(View.INVISIBLE);

                             // You can see this User's info since it's not blocked
                             } else{
                                 // Verified Badge
                                 boolean isVerified = userPointer.getBoolean(USER_IS_VERIFIED);
                                 if (isVerified) { verifiedBadge.setVisibility(View.VISIBLE);
                                 } else { verifiedBadge.setVisibility(View.INVISIBLE); }

                                 // Username
                                 usernameTxt.setText(userPointer.getString(USER_USERNAME));
                                 // Fullname
                                 fullnameTxt.setText(userPointer.getString(USER_FULLNAME));
                                 // Bio
                                 if (userPointer.getString(USER_BIO) != null ){
                                     bioTxt.setText(userPointer.getString(USER_BIO));
                                 } else{
                                     bioTxt.setText("");
                                 }
                                 getParseImage(avatarImg, userPointer, USER_AVATAR);
                             }

                             // Follow Button
                             final List<String>isFollowing = currentUser.getList(USER_IS_FOLLOWING);
                             assert isFollowing != null;
                             if (isFollowing.contains(userPointer.getObjectId()) ){
                                 followButt.setText("Following");
                                 followButt.setBackgroundTintList(getResources().getColorStateList(R.color.grey));
                             } else{
                                 followButt.setText("Follow");
                                 followButt.setBackgroundTintList(getResources().getColorStateList(R.color.main_color));
                             }


                             //-----------------------------------------------
                             // MARK - SHOW USER PROFILE BUTTON
                             //-----------------------------------------------
                             avatarImg.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     // Access User's Profile
                                     if (!blockedBy.contains(userPointer.getObjectId()) ){
                                         // Pass objectID to the other Activity
                                         Intent i = new Intent(ctx, UserProfile.class);
                                         Bundle extras = new Bundle();
                                         extras.putString("objectID", userPointer.getObjectId());
                                         i.putExtras(extras);
                                         startActivity(i);
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
                                         if (followButt.getText().toString().matches("Following") ) {
                                             // Update isFollowing for currentUser
                                             assert isFollowing != null;
                                             isFollowing.remove(userPointer.getObjectId());
                                             currentUser.put(USER_IS_FOLLOWING, isFollowing);
                                             currentUser.saveInBackground();

                                             // Dispatch async
                                             runOnUiThread(new Runnable() {
                                                 @Override
                                                 public void run() {

                                                     // Query Follow and remove the row in the database
                                                     ParseQuery<ParseObject> query = ParseQuery.getQuery(FOLLOW_CLASS_NAME);
                                                     query.whereEqualTo(FOLLOW_CURRENT_USER, currentUser);
                                                     query.whereEqualTo(FOLLOW_IS_FOLLOWING, userPointer);
                                                     query.findInBackground(new FindCallback<ParseObject>() {
                                                         @Override
                                                         public void done(List<ParseObject> objects, ParseException e) {
                                                             if (e == null) {
                                                                 ParseObject fObj = objects.get(0);
                                                                 fObj.deleteInBackground(new DeleteCallback() {
                                                                     @Override
                                                                     public void done(ParseException e) {
                                                                         if (e == null) {
                                                                             followButt.setText("Follow");
                                                                             followButt.setBackgroundTintList(getResources().getColorStateList(R.color.main_color));
                                                                 }}});
                                                             // error
                                                             } else {
                                                                 simpleAlert(e.getMessage(), ctx);
                                                     }}});

                                                     // Query Posts of userObj and un-follow them
                                                     ParseQuery<ParseObject> query2 = ParseQuery.getQuery(POSTS_CLASS_NAME);
                                                     query2.whereEqualTo(POSTS_USER_POINTER, userPointer);
                                                     query2.findInBackground(new FindCallback<ParseObject>() {
                                                         @Override
                                                         public void done(List<ParseObject> objects, ParseException e) {
                                                             if (e == null) {
                                                                 for (int i = 0; i < objects.size(); i++) {
                                                                     ParseObject pObj = objects.get(i);
                                                                     List<String> followedBy = pObj.getList(POSTS_FOLLOWED_BY);
                                                                     assert followedBy != null;
                                                                     followedBy.remove(currentUser.getObjectId());
                                                                     pObj.put(POSTS_FOLLOWED_BY, followedBy);
                                                                     pObj.saveInBackground();
                                                                 }
                                                             // error
                                                             } else {
                                                                 simpleAlert(e.getMessage(), ctx);
                                                     }}});


                                                     // Remove currentUser's ID to followedBy in Moments
                                                     ParseQuery<ParseObject>query3 = ParseQuery.getQuery(MOMENTS_CLASS_NAME);
                                                     query3.whereEqualTo(MOMENTS_USER_POINTER, userPointer);
                                                     query3.findInBackground(new FindCallback<ParseObject>() {
                                                         @Override
                                                         public void done(List<ParseObject> objects, ParseException e) {
                                                             if (e == null) {
                                                                 if (objects.size() != 0){
                                                                 ParseObject mObj = objects.get(0);

                                                                 List<String>followedBy = mObj.getList(MOMENTS_FOLLOWED_BY);
                                                                     assert followedBy != null;
                                                                     followedBy.remove(currentUser.getObjectId());
                                                                 mObj.put(MOMENTS_FOLLOWED_BY, followedBy);
                                                                 mObj.saveInBackground();
                                                                 Log.i(TAG, "FOLLOWED BY SAVED IN MOMENTS");
                                                             }

                                                         // error
                                                         } else{ simpleAlert(e.getMessage(), ctx);
                                                     }}});


                                                 }});// ./ Dispatch async




                                         // MARK: - FOLLOW THIS USER
                                         // ------------------------------------------------
                                         } else {
                                             // Update isFollowing for currentUser
                                             assert isFollowing != null;
                                             isFollowing.add(userPointer.getObjectId());
                                             currentUser.put(USER_IS_FOLLOWING, isFollowing);
                                             currentUser.saveInBackground();

                                             runOnUiThread(new Runnable() {
                                                 @Override
                                                 public void run() {

                                                     // Add userObj to the Follow class
                                                     ParseObject fObj = new ParseObject(FOLLOW_CLASS_NAME);
                                                     fObj.put(FOLLOW_CURRENT_USER, currentUser);
                                                     fObj.put(FOLLOW_IS_FOLLOWING, userPointer);
                                                     fObj.saveInBackground(new SaveCallback() {
                                                         @Override
                                                         public void done(ParseException e) {
                                                             if (e == null) {
                                                                 followButt.setText("Following");
                                                                 followButt.setBackgroundTintList(getResources().getColorStateList(R.color.grey));
                                                             // error
                                                             } else { simpleAlert(e.getMessage(), ctx); }
                                                         }
                                                     });

                                                     // Query Posts of userObj and follow them
                                                     ParseQuery<ParseObject> query2 = ParseQuery.getQuery(POSTS_CLASS_NAME);
                                                     query2.whereEqualTo(POSTS_USER_POINTER, userPointer);
                                                     query2.findInBackground(new FindCallback<ParseObject>() {
                                                         @Override
                                                         public void done(List<ParseObject> objects, ParseException e) {
                                                             if (e == null) {
                                                                 for (int i = 0; i < objects.size(); i++) {
                                                                     ParseObject pObj = objects.get(i);
                                                                     List<String> followedBy = pObj.getList(POSTS_FOLLOWED_BY);
                                                                     assert followedBy != null;
                                                                     followedBy.add(currentUser.getObjectId());
                                                                     pObj.put(POSTS_FOLLOWED_BY, followedBy);
                                                                     pObj.saveInBackground();
                                                                 }
                                                             // error
                                                             } else { simpleAlert(e.getMessage(), ctx); }
                                                         }
                                                     });

                                                     // Add currentUser's ID to followedBy in Posts
                                                     ParseQuery<ParseObject> query3 = ParseQuery.getQuery(POSTS_CLASS_NAME);
                                                     query3.whereEqualTo(POSTS_USER_POINTER, userPointer);
                                                     query3.findInBackground(new FindCallback<ParseObject>() {
                                                         @Override
                                                         public void done(List<ParseObject> objects, ParseException e) {
                                                             if (e == null) {
                                                                 for (int i = 0; i < objects.size(); i++) {
                                                                     ParseObject pObj = objects.get(i);
                                                                     List<String> followedBy = pObj.getList(POSTS_FOLLOWED_BY);
                                                                     assert followedBy != null;
                                                                     followedBy.add(currentUser.getObjectId());
                                                                     pObj.put(POSTS_FOLLOWED_BY, followedBy);
                                                                     pObj.saveInBackground();
                                                                     Log.i(TAG, "FOLLOWED BY SAVED IN POSTS");
                                                                 }
                                                             // error
                                                             } else { simpleAlert(e.getMessage(), ctx); }
                                                         }
                                                     });


                                                     // Add currentUser's ID to followedBy in Moments
                                                     ParseQuery<ParseObject> query4 = ParseQuery.getQuery(MOMENTS_CLASS_NAME);
                                                     query4.whereEqualTo(MOMENTS_USER_POINTER, userPointer);
                                                     query4.findInBackground(new FindCallback<ParseObject>() {
                                                         @Override
                                                         public void done(List<ParseObject> objects, ParseException e) {
                                                             if (e == null) {
                                                                 if (objects.size() != 0) {
                                                                     for (int j = 0; j < objects.size(); j++) {
                                                                         ParseObject mObj = objects.get(j);
                                                                         List<String> followedBy = mObj.getList(MOMENTS_FOLLOWED_BY);
                                                                         assert followedBy != null;
                                                                         followedBy.add(currentUser.getObjectId());
                                                                         mObj.put(MOMENTS_FOLLOWED_BY, followedBy);
                                                                         mObj.saveInBackground();
                                                                         Log.i(TAG, "FOLLOWED BY SAVED IN MOMENTS");
                                                                     }
                                                                 }
                                                             // error
                                                             } else { simpleAlert(e.getMessage(), ctx); }
                                                         }
                                                     });


                                                     // Set User as Not Interesting
                                                     List<String> notInterestingFor = userPointer.getList(USER_NOT_INTERESTING_FOR);
                                                     assert notInterestingFor != null;
                                                     notInterestingFor.add(currentUser.getObjectId());
                                                     final HashMap<String, Object> params = new HashMap<>();
                                                     params.put("userId", userPointer.getObjectId());
                                                     params.put("notInterestingFor", notInterestingFor);
                                                     ParseCloud.callFunctionInBackground("setUserNotInteresting", params, new FunctionCallback<Object>() {
                                                         @Override
                                                         public void done(Object object, ParseException e) {
                                                             if (e == null) {

                                                                 // Send Push notification
                                                                 String pushMessage = "@" + currentUser.getString(USER_USERNAME) + " started following you";
                                                                 sendPushNotification(pushMessage, (ParseUser) userPointer, ctx);

                                                             // error
                                                             } else { simpleAlert(e.getMessage(), ctx);
                                                     }}});// ./ ParseCloud

                                                 }});// ./ Dispatch async

                                         }// ./ if followButt's Title


                                 }});// ./ followButt

                         // error
                         } else { simpleAlert(e.getMessage(), ctx);
                 }}});// end userPointer


             return cell;
             }

             @Override public int getCount() { return followArray.size(); }
             @Override public Object getItem(int position) { return followArray.get(position); }
             @Override public long getItemId(int position) { return position; }
         }


         // Set Adapter
         folowListView.setAdapter(new ListAdapter(ctx));
    }





    //-----------------------------------------------
    // MARK - REFRESH DATA
    //-----------------------------------------------
    @Override
    public void onRefresh() {
        skip = 0;
        followArray = new ArrayList<>();

        // Recall query
        queryFollowersOrFollowing();

        if (refreshControl.isRefreshing()) { refreshControl.setRefreshing(false); }
    }




}// ./ end
