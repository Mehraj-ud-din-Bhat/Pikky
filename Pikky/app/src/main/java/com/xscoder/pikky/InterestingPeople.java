package com.xscoder.pikky;


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

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
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
import static com.xscoder.pikky.Configurations.USER_CLASS_NAME;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.USER_IS_REPORTED;
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

public class InterestingPeople extends AppCompatActivity {

    /*--- VIEWS ---*/
    ListView intPeopleListView;



    /*--- VARIABLES ---*/
    Context ctx = this;
    List<ParseObject> peopleArray = new ArrayList<>();




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interesting_people);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();

        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        intPeopleListView = findViewById(R.id.ipIntPeopleListView);



        showHUD(ctx);
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String>isFollowing = currentUser.getList(USER_IS_FOLLOWING);
        List<String>currObjID = new ArrayList<>();
        currObjID.add(currentUser.getObjectId());

        //-----------------------------------------------
        // MARK - QUERY INTERESTING PEOPLE
        //-----------------------------------------------
        ParseQuery<ParseObject>query = ParseQuery.getQuery(USER_CLASS_NAME);
        query.whereNotContainedIn("objectId", isFollowing);
        query.whereNotEqualTo("objectId", currentUser.getObjectId());
        query.whereEqualTo(USER_IS_REPORTED, false);
        query.whereNotContainedIn(USER_NOT_INTERESTING_FOR, currObjID);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    hideHUD();
                    peopleArray = objects;
                    showdataInListView();
                // error
                } else {
                hideHUD();
                simpleAlert(e.getMessage(), ctx);
        }}});



        //-----------------------------------------------
        // MARK - BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.ipBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }// ./ onCreate




    //-----------------------------------------------
    // MARK - SHOW DATA IN LISTVIEW
    //-----------------------------------------------
    void showdataInListView() {
        class ListAdapter extends BaseAdapter {
            private Context context;

            private ListAdapter(Context context) {
                super();
                this.context = context;
            }

            // CONFIGURE CELL
            @SuppressLint({"SetTextI18n", "InflateParams"})
            @Override
            public View getView(final int position, View cell, ViewGroup parent) {
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

                // CurrentUser
                final ParseUser currentUser = ParseUser.getCurrentUser();


                // User
                final ParseUser aUser = (ParseUser) peopleArray.get(position);

                // Verified Badge
                boolean isVerified = aUser.getBoolean(USER_IS_VERIFIED);
                if (isVerified) { verifiedBadge.setVisibility(View.VISIBLE);
                } else { verifiedBadge.setVisibility(View.INVISIBLE); }

                // Username
                usernameTxt.setText(aUser.getString(USER_USERNAME));
                // Full name
                fullnameTxt.setText(aUser.getString(USER_FULLNAME));
                // Bio
                if (aUser.getString(USER_BIO) != null) {
                    bioTxt.setText(aUser.getString(USER_BIO));
                } else {
                    bioTxt.setText("");
                }
                // Avatar
                getParseImage(avatarImg, aUser, USER_AVATAR);

                // Follow Button
                followButt.setText("Follow");
                followButt.setBackgroundTintList(getResources().getColorStateList(R.color.main_color));



                //-----------------------------------------------
                // MARK - SHOW USER PROFILE BUTTON
                //-----------------------------------------------
                avatarImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ctx, UserProfile.class);
                        Bundle extras = new Bundle();
                        extras.putString("objectID", aUser.getObjectId());
                        i.putExtras(extras);
                        startActivity(i);
                }});



                //-----------------------------------------------
                // MARK - LONG CLICK ON AVATAR TO REMOVE INTERESTING USER
                //-----------------------------------------------
                avatarImg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showHUD(ctx);
                        List<String>notInterestingFor = aUser.getList(USER_NOT_INTERESTING_FOR);
                        assert notInterestingFor != null;
                        notInterestingFor.add(currentUser.getObjectId());
                        Log.i(TAG, "NOT INTERESTING ARRAY: " + notInterestingFor);

                        final HashMap<String, Object> params = new HashMap<>();
                        params.put("userId", aUser.getObjectId());
                        params.put("notInterestingFor", notInterestingFor);
                        ParseCloud.callFunctionInBackground("setUserNotInteresting", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if (e == null) {
                                    hideHUD();
                                    peopleArray.remove(position);
                                    intPeopleListView.invalidateViews();
                                    intPeopleListView.refreshDrawableState();
                                // error
                                } else {
                                    hideHUD();
                                    simpleAlert(e.getMessage(), ctx);
                        }}});
                        return false;
                }});





                // ------------------------------------------------
                // MARK: - FOLLOW BUTTON
                // ------------------------------------------------
                followButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> isFollowing = currentUser.getList(USER_IS_FOLLOWING);

                        // ------------------------------------------------
                        // MARK: - FOLLOW THIS USER
                        // ------------------------------------------------
                        assert isFollowing != null;
                        isFollowing.add(aUser.getObjectId());
                        currentUser.put(USER_IS_FOLLOWING, isFollowing);
                        currentUser.saveInBackground();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // Add userObj to the Follow class
                                ParseObject fObj = new ParseObject(FOLLOW_CLASS_NAME);
                                fObj.put(FOLLOW_CURRENT_USER, currentUser);
                                fObj.put(FOLLOW_IS_FOLLOWING, aUser);
                                fObj.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            peopleArray.remove(position);
                                            intPeopleListView.invalidateViews();
                                            intPeopleListView.refreshDrawableState();

                                            // error
                                        } else {
                                            simpleAlert(e.getMessage(), ctx);

                                }}});


                                // Query Posts of userObj and follow them
                                ParseQuery<ParseObject> query2 = ParseQuery.getQuery(POSTS_CLASS_NAME);
                                query2.whereEqualTo(POSTS_USER_POINTER, aUser);
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
                                        } else {
                                            simpleAlert(e.getMessage(), ctx);
                                        }
                                    }
                                });


                                // Add currentUser's ID to followedBy in Posts
                                ParseQuery<ParseObject> query3 = ParseQuery.getQuery(POSTS_CLASS_NAME);
                                query3.whereEqualTo(POSTS_USER_POINTER, aUser);
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
                                        } else {
                                            simpleAlert(e.getMessage(), ctx);
                                }}});


                                // Add currentUser's ID to followedBy in Moments
                                ParseQuery<ParseObject> query4 = ParseQuery.getQuery(MOMENTS_CLASS_NAME);
                                query4.whereEqualTo(MOMENTS_USER_POINTER, aUser);
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
                                        } else {
                                            simpleAlert(e.getMessage(), ctx);
                                }}});


                                // Set User as Not Interesting
                                List<String> notInterestingFor = aUser.getList(USER_NOT_INTERESTING_FOR);
                                assert notInterestingFor != null;
                                notInterestingFor.add(currentUser.getObjectId());
                                final HashMap<String, Object> params = new HashMap<>();
                                params.put("userId", aUser.getObjectId());
                                params.put("notInterestingFor", notInterestingFor);
                                ParseCloud.callFunctionInBackground("setUserNotInteresting", params, new FunctionCallback<Object>() {
                                    @Override
                                    public void done(Object object, ParseException e) {
                                        if (e != null) {
                                            simpleAlert(e.getMessage(), ctx);
                                }}});// ./ ParseCloud


                                // Send Push notification
                                String pushMessage = "@" + currentUser.getString(USER_USERNAME) + " started following you";
                                sendPushNotification(pushMessage, aUser, ctx);

                            }
                        });// ./ Dispatch async


                    }
                });// ./ followButt


            return cell;
            }

            @Override public int getCount() { return peopleArray.size(); }
            @Override public Object getItem(int position) { return peopleArray.get(position); }
            @Override public long getItemId(int position) { return position; }
        }

        // Set Adapter
        intPeopleListView.setAdapter(new ListAdapter(ctx));
    }


}// ./ end
