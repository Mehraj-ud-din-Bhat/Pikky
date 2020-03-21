package com.xscoder.pikky.Home.Activities;

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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xscoder.pikky.Camera.SquareCamera;
import com.xscoder.pikky.Edit.TextureVideoView;
import com.xscoder.pikky.Home.Fragments.PagerAdapter;
import com.xscoder.pikky.Massiging.Notifications;
import com.xscoder.pikky.R;
import com.xscoder.pikky.Search.SearchScreen;
import com.xscoder.pikky.Userprofile.AccountProfile.Account;
import com.xscoder.pikky.Userprofile.UserProfile;
import com.xscoder.pikky.loginSignUp.Activity.Intro;
import com.xscoder.pikky.loginSignUp.ModalClasses.LoginResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.xscoder.pikky.Configurations.FOLLOW_CLASS_NAME;
import static com.xscoder.pikky.Configurations.FOLLOW_CURRENT_USER;
import static com.xscoder.pikky.Configurations.FOLLOW_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.MAIN_COLOR;
import static com.xscoder.pikky.Configurations.MAXIMUM_RECORDING_TIME;
import static com.xscoder.pikky.Configurations.MOMENTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.MOMENTS_FOLLOWED_BY;
import static com.xscoder.pikky.Configurations.MOMENTS_REPORTED_BY;
import static com.xscoder.pikky.Configurations.MOMENTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.MOMENTS_VIDEO;
import static com.xscoder.pikky.Configurations.MULTIPLE_PERMISSIONS;
import static com.xscoder.pikky.Configurations.POSTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.POSTS_FOLLOWED_BY;
import static com.xscoder.pikky.Configurations.POSTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.USER_NOT_INTERESTING_FOR;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.convertVideoToBytes;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.getRealPathFromURI;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.lobster;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.permissions;
import static com.xscoder.pikky.Configurations.sendPushNotification;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class Home extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    /*--- VIEWS ---*/
    TextView titleTxt;
    ///CircleImageView yourMomentImg;
    ImageView plusIcon;
    ListView postsListView;
    //   SwipeRefreshLayout refreshControl;
    // RelativeLayout noPostsLayout, videoContainerLayout;
    TextureVideoView videoView;
    Button dismVideoButt, findPeopleButt, instantButt;
    ProgressBar videoProgBar;


    /*--- VARIABLES ---*/
    Context ctx = this;
    List<ParseObject> postsArray = new ArrayList<>();
    List<ParseObject> momentsArray = new ArrayList<>();
    List<ParseUser> interestingPeopleArray;
    boolean isYourMoment = false;
    ParseObject yourMomentObj = new ParseObject(MOMENTS_CLASS_NAME);
    //-----------------------------------------------
    // MARK - OPEN CAMERA GET RECORDED VIDEO
    //-----------------------------------------------
    int VIDEO_CAMERA = 0;


    // Tablayout code Added By Mehraj

    TabLayout tabLayout;
    ViewPager viewPager;
    //end @meh
    String videoPath = null;


    // ------------------------------------------------
    // MARK: - QUERY POSTS
    // ------------------------------------------------
//    void queryPosts() {
//        postsArray = new ArrayList<>();
//        showHUD(ctx);
//        final ParseUser currentUser = ParseUser.getCurrentUser();
//        List<String>curruObjID = new ArrayList<>();
//        curruObjID.add(currentUser.getObjectId());
//
//        // Launch Query
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(POSTS_CLASS_NAME);
//        query.whereContainedIn(POSTS_FOLLOWED_BY, curruObjID);
//        query.whereNotContainedIn(POSTS_REPORTED_BY, curruObjID);
//        query.orderByDescending(POSTS_CREATED_AT);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> objects, ParseException e) {
//                if (e == null) {
//                    hideHUD();
//                    postsArray = objects;
//                    Log.i(TAG, "POSTS ARRAY: " + postsArray.size());
//
//                    if (postsArray.size() == 0) {
//                        noPostsLayout.setVisibility(View.VISIBLE);
//
//                    } else {
//                        noPostsLayout.setVisibility(View.INVISIBLE);
//
//
//                        // Check for mutedBy and blockedBy arrays in the database
//                        for (int i=0; i<postsArray.size(); i++) {
//                            // Parse Object
//                            final ParseObject pObj = postsArray.get(i);
//
//                            // Get userPointer
//                            Objects.requireNonNull(pObj.getParseObject(POSTS_USER_POINTER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//                                public void done(ParseObject userPointer, ParseException e) {
//
//                                    List<String>mutedBy = userPointer.getList(USER_MUTED_BY);
//                                    assert mutedBy != null;
//                                    List<String>blockedBy = userPointer.getList(USER_BLOCKED_BY);
//                                    assert blockedBy != null;
//
//                                    if (mutedBy.contains(currentUser.getObjectId()) || blockedBy.contains(currentUser.getObjectId())){
//                                        postsArray.remove(pObj);
//                                        showDataInListView();
//                                    } else { showDataInListView(); }
//
//
//                            }});// end userPointer
//
//                        }// ./ For
//
//                    }// ./ If
//
//
//                    // Query Interesting people
//                    queryInterestingPeople();
//
//
//                // error
//                } else { hideHUD(); simpleAlert(e.getMessage(), ctx);
//        }}});
//    }


    //-----------------------------------------------
    // MARK - SHOW DATA IN LISTVIEW
    //-----------------------------------------------
//    void showDataInListView() {
//        class ListAdapter extends BaseAdapter {
//            private Context context;
//
//            private ListAdapter(Context context) {
//                super();
//                this.context = context;
//            }
//
//            @SuppressLint("InflateParams")
//            @Override
//            public View getView(final int position, View cell, ViewGroup parent) {
//                if (cell == null) {
//                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    assert inflater != null;
//                    cell = inflater.inflate(R.layout.cell_post, null);
//                }
//                final View finalCell = cell;
//
//                // Parse Object
//                final ParseObject pObj = postsArray.get(position);
//
//                // Current User
//                final ParseUser currentUser = ParseUser.getCurrentUser();
//
//                // Get userPointer
//                Objects.requireNonNull(pObj.getParseObject(POSTS_USER_POINTER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//                    public void done(final ParseObject userPointer, ParseException e) {
//                        if (e == null) {
//
//                            //-----------------------------------------------
//                            // MARK - INITIALIZE VIEWS
//                            //-----------------------------------------------
//                            TextView usernameTxt = finalCell.findViewById(R.id.cpUsernameTxt);
//                            usernameTxt.setTypeface(osBold);
//                            ImageView verifiedBadge = finalCell.findViewById(R.id.cpVerifiedBadge);
//                            TextView locationtxt = finalCell.findViewById(R.id.cpLocationTxt);
//                            locationtxt.setTypeface(osRegular);
//                            ImageView avatarImg = finalCell.findViewById(R.id.cpAvatarImg);
//                            final ImageView yourAvatarImg = finalCell.findViewById(R.id.cpYourAvatarImg);
//                            final ImageView postImg = finalCell.findViewById(R.id.cpPostImg);
//                            Button playVideoButt = finalCell.findViewById(R.id.cpPlayVideoButt);
//                            TextView postTextTxt = finalCell.findViewById(R.id.cpPostTextTxt);
//                            postTextTxt.setTypeface(osRegular);
//                            final TextView likesTxt = finalCell.findViewById(R.id.cpLikesTxt);
//                            likesTxt.setTypeface(osRegular);
//                            final TextView commentsTxt = finalCell.findViewById(R.id.cpCommentsTxt);
//                            commentsTxt.setTypeface(osRegular);
//                            final Button likeButt = finalCell.findViewById(R.id.cpLikeButt);
//                            final Button commentsButt = finalCell.findViewById(R.id.cpCommentButt);
//                            final Button bookmarkButt = finalCell.findViewById(R.id.cpBookmarkButt);
//                            final EditText addCommentTxt = finalCell.findViewById(R.id.cpAddCommentTxt);
//                            addCommentTxt.setTypeface(osRegular);
//                            TextView dateTxt = finalCell.findViewById(R.id.cpDateTxt);
//                            dateTxt.setTypeface(osRegular);
//                            Button optionsButt = finalCell.findViewById(R.id.cpOptionsButt);
//
//
//                            //-----------------------------------------------
//                            // MARK - SET POST IMAGE SIZE
//                            //-----------------------------------------------
//                            DisplayMetrics displayMetrics = new DisplayMetrics();
//                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                            int W = displayMetrics.widthPixels;
//
//                            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(W, W);
//                            lp2.setMargins(0, 10, 0,0);
//                            lp2.addRule(RelativeLayout.BELOW, avatarImg.getId());
//                            lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                            postImg.setLayoutParams(lp2);
//
//
//
//                            //-----------------------------------------------
//                            // MARK - SHOW DATA
//                            //-----------------------------------------------
//
//                            // Username
//                            usernameTxt.setText(userPointer.getString(USER_USERNAME));
//
//                            // Verified Badge
//                            boolean isVerified = userPointer.getBoolean(USER_IS_VERIFIED);
//                            if (isVerified) { verifiedBadge.setVisibility(View.VISIBLE);
//                            } else { verifiedBadge.setVisibility(View.INVISIBLE); }
//
//                            // Post Location
//                            if (pObj.getString(POSTS_LOCATION) != null) { locationtxt.setText(pObj.getString(POSTS_LOCATION));
//                            } else { locationtxt.setText(""); }
//
//                            // UserPointer Avatar
//                            getParseImage(avatarImg, userPointer, USER_AVATAR);
//
//                            // Current User Avatar
//                            getParseImage(yourAvatarImg, currentUser, USER_AVATAR);
//
//                            // Post's Image or Video Thumbnail
//                            getParseImage(postImg, pObj, POSTS_IMAGE);
//
//                            // Post's Video
//                            if (pObj.getParseFile(POSTS_VIDEO) != null){
//                                playVideoButt.setVisibility(View.VISIBLE);
//                            } else {
//                                playVideoButt.setVisibility(View.INVISIBLE);
//                            }
//
//                            // Post text
//                            postTextTxt.setText(pObj.getString(POSTS_TEXT));
//
//                            // Likes
//                            int likes = pObj.getInt(POSTS_LIKES);
//                            likesTxt.setText(roundLargeNumber(likes));
//                            final List<String>likedBy = pObj.getList(POSTS_LIKED_BY);
//                            assert likedBy != null;
//                            if (likedBy.contains(currentUser.getObjectId()) ){
//                                likeButt.setBackgroundResource(R.drawable.liked_butt);
//                            } else {
//                                likeButt.setBackgroundResource(R.drawable.like_butt);
//                            }
//
//                            // Check if Commenting is ON or OFF
//                            boolean canComment = pObj.getBoolean(POSTS_CAN_COMMENT);
//                            if (canComment) {
//                                // Show views
//                                commentsButt.setVisibility(View.VISIBLE);
//                                commentsTxt.setVisibility(View.VISIBLE);
//                                yourAvatarImg.setVisibility(View.VISIBLE);
//                                addCommentTxt.setVisibility(View.VISIBLE);
//                            } else {
//                                // Hide views
//                                commentsButt.setVisibility(View.INVISIBLE);
//                                commentsTxt.setVisibility(View.INVISIBLE);
//                                yourAvatarImg.setVisibility(View.INVISIBLE);
//                                addCommentTxt.setVisibility(View.INVISIBLE);
//                            }
//
//                            // Comments
//                            int comments = pObj.getInt(POSTS_COMMENTS);
//                            commentsTxt.setText(roundLargeNumber(comments));
//
//                            // Saved
//                            List<String>bookmarkedBy = pObj.getList(POSTS_BOOKMARKED_BY);
//                            assert bookmarkedBy != null;
//                            if (bookmarkedBy.contains(currentUser.getObjectId()) ){
//                                bookmarkButt.setBackgroundResource(R.drawable.bookmarked_butt);
//                            } else {
//                                bookmarkButt.setBackgroundResource(R.drawable.bookmark_butt);
//                            }
//
//                            // Date
//                            Date aDate = pObj.getCreatedAt();
//                            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMM yyy");
//                            dateTxt.setText(df.format(aDate).toUpperCase());
//
//                            // Tags
//                            List<String> tagsArray;
//                            if (pObj.getList(POSTS_TAGS) != null) {
//                                tagsArray = pObj.getList(POSTS_TAGS);
//                                // Log.i(TAG, "TAGS: " + tagsArray);
//
//                                LinearLayout tagsLayout = finalCell.findViewById(R.id.cpTagsLayout);
//                                tagsLayout.setOrientation(LinearLayout.HORIZONTAL);
//                                tagsLayout.removeAllViews();
//
//                                assert tagsArray != null;
//                                for (int i = 0; i<tagsArray.size(); i++) {
//                                    // Create Buttons
//                                    final Button tButt = new Button(ctx);
//                                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getResources().getDisplayMetrics());
//                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
//                                    lp.setMargins(3, 0, 3, 0);
//                                    tButt.setLayoutParams(lp);
//
//                                    tButt.setText(tagsArray.get(i));
//                                    tButt.setTypeface(osItalic);
//                                    tButt.setTextColor(Color.parseColor("#777777"));
//                                    tButt.setBackgroundColor(Color.TRANSPARENT);
//                                    tButt.setTextSize(10);
//                                    tButt.setAllCaps(false);
//                                    tButt.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                String tag = tButt.getText().toString();
//                                                Log.i(TAG, "SELECTED TAG: " + tag);
//
//                                                Intent i = new Intent(ctx, SearchScreen.class);
//                                                Bundle extras = new Bundle();
//                                                extras.putString("searchText", tag);
//                                                extras.putBoolean("isTag", true);
//                                                i.putExtras(extras);
//                                                startActivity(i);
//                                    }});
//
//                                    // Add button to the layout
//                                    tagsLayout.addView(tButt);
//
//                                }// ./For loop
//
//                            }// ./ If
//
//
//
//
//                            // ------------------------------------------------
//                            // MARK: - USER'S AVATAR IMAGE
//                            // ------------------------------------------------
//                            avatarImg.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                        if (!userPointer.getObjectId().matches(currentUser.getObjectId()) ){
//                                            Intent i = new Intent(ctx, UserProfile.class);
//                                            Bundle extras = new Bundle();
//                                            extras.putString("objectID", userPointer.getObjectId());
//                                            i.putExtras(extras);
//                                            startActivity(i);
//
//                                        } else {
//                                            Intent i = new Intent(ctx, Account.class);
//                                            Bundle extras = new Bundle();
//                                            extras.putBoolean("isCurrentUser", true);
//                                            i.putExtras(extras);
//                                            startActivity(i);
//                                        }
//                            }});
//
//
//
//
//                            //-----------------------------------------------
//                            // MARK - ADD A COMMENT FROM POST'S CELL
//                            //-----------------------------------------------
//                            addCommentTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                                    @Override
//                                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                                        if (actionId == EditorInfo.IME_ACTION_SEND) {
//                                            final String comment = addCommentTxt.getText().toString();
//
//                                            showHUD(ctx);
//
//                                            ParseObject cObj = new ParseObject(COMMENTS_CLASS_NAME);
//                                            List<String>empty = new ArrayList<>();
//
//                                            // Prepare Data
//                                            cObj.put(COMMENTS_CURRENT_USER, currentUser);
//                                            cObj.put(COMMENTS_POST_POINTER, pObj);
//                                            cObj.put(COMMENTS_COMMENT, comment);
//                                            cObj.put(COMMENTS_LIKES, 0);
//                                            cObj.put(COMMENTS_LIKED_BY, empty);
//                                            cObj.put(COMMENTS_REPORTED_BY, empty);
//
//                                            // Save data
//                                            cObj.saveInBackground(new SaveCallback() {
//                                                @Override
//                                                public void done(ParseException e) {
//                                                    if (e == null) {
//
//                                                        hideHUD();
//                                                        Toast.makeText(ctx, "Comment sent!", Toast.LENGTH_SHORT).show();
//
//                                                        // Increment the Post's comments
//                                                        pObj.increment(POSTS_COMMENTS, 1);
//                                                        pObj.saveInBackground();
//
//                                                        // Push Notification + save Notifcation
//                                                        String push = "@" + currentUser.getString(USER_USERNAME) + " commented on your Post: '" + pObj.getString(POSTS_TEXT) + "'";
//                                                        sendPushNotification(push, (ParseUser) userPointer, ctx);
//
//                                                        // Dismiss keyboard
//                                                        addCommentTxt.setText("");
//                                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                                        assert imm != null;
//                                                        imm.hideSoftInputFromWindow(addCommentTxt.getWindowToken(), 0);
//
//                                                    // error
//                                                    } else { hideHUD(); simpleAlert(e.getMessage(), ctx);
//                                            }}});
//
//                                            return true;
//                                        } return false;
//                                }});
//
//
//
//
//
//
//                                // ------------------------------------------------
//                                // MARK: - POST OPTIONS BUTTON
//                                // ------------------------------------------------
//                                optionsButt.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//
//                                        // MARK: - OPTIONS FOR POSTS - BY OTHER USERS
//                                        // ------------------------------------------------
//                                        if (!userPointer.getObjectId().matches(currentUser.getObjectId()) ) {
//
//                                            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
//                                            alert.setTitle("Select Option")
//                                                    .setIcon(R.drawable.logo)
//                                                    .setItems(new CharSequence[]{
//                                                            "Share post",
//                                                            "Pick from Gallery",
//                                                            "Mute Posts"
//
//                                                    }, new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            switch (which) {
//
//                                                                // Share Post --------------------------------------------------------------------------------------------------
//                                                                case 0:
//                                                                    Bitmap bitmap = ((BitmapDrawable) postImg.getDrawable()).getBitmap();
//                                                                    Uri uri = getImageUri(bitmap, ctx);
//                                                                    Intent intent = new Intent(Intent.ACTION_SEND);
//                                                                    intent.setType("image/jpeg");
//                                                                    intent.putExtra(Intent.EXTRA_STREAM, uri);
//                                                                    intent.putExtra(Intent.EXTRA_TEXT, pObj.getString(POSTS_TEXT));
//                                                                    startActivity(Intent.createChooser(intent, "Share on..."));
//                                                                    break;
//
//
//                                                                // Report Post --------------------------------------------------------------------------------------------------
//                                                                case 1:
//                                                                    AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
//                                                                    alert.setMessage("Are you sure you want to report this Post?")
//                                                                            .setTitle(R.string.app_name)
//
//                                                                            // Report
//                                                                            .setPositiveButton("Report as inappropriate", new DialogInterface.OnClickListener() {
//                                                                                @Override
//                                                                                public void onClick(DialogInterface dialog, int which) {
//                                                                                    List<String> reportedBy = pObj.getList(POSTS_REPORTED_BY);
//                                                                                    assert reportedBy != null;
//                                                                                    reportedBy.add(currentUser.getObjectId());
//                                                                                    pObj.put(POSTS_REPORTED_BY, reportedBy);
//                                                                                    pObj.saveInBackground();
//                                                                                    simpleAlert("Thanks for reporting this Post to the Admin! We'll take action for it withint 24 hours.", ctx);
//                                                                            }})
//
//                                                                            // Cancel
//                                                                            .setNegativeButton("Cancel", null)
//                                                                            .setCancelable(false)
//                                                                            .setIcon(R.drawable.logo);
//                                                                    alert.create().show();
//                                                                    break;
//
//
//                                                                // Mute Posts --------------------------------------------------------------------------------------------------
//                                                                case 2:
//
//                                                                    AlertDialog.Builder alert2 = new AlertDialog.Builder(ctx);
//                                                                    alert2.setMessage("Want to mute @" + userPointer.getString(USER_USERNAME) + "'s Posts?\nYou can still unmute this User from his/her Profile.")
//                                                                            .setTitle(R.string.app_name)
//
//                                                                            // Mute
//                                                                            .setPositiveButton("Mute", new DialogInterface.OnClickListener() {
//                                                                                @Override
//                                                                                public void onClick(DialogInterface dialog, int which) {
//                                                                                    List<String> mutedBy = userPointer.getList(USER_MUTED_BY);
//                                                                                    assert mutedBy != null;
//                                                                                    mutedBy.add(currentUser.getObjectId());
//
//                                                                                    HashMap<String, Object> params = new HashMap<>();
//                                                                                    params.put("userId", userPointer.getObjectId());
//                                                                                    params.put("mutedBy", mutedBy);
//
//                                                                                    ParseCloud.callFunctionInBackground("muteUnmuteUser", params, new FunctionCallback<ParseUser>() {
//                                                                                        public void done(ParseUser user, ParseException error) {
//                                                                                            if (error == null) {
//                                                                                                Log.i(TAG, userPointer.getString(USER_FULLNAME) + " has been muted");
//                                                                                                // Relaunch query
//                                                                                                queryPosts();
//
//                                                                                            // error
//                                                                                            } else { simpleAlert(error.getMessage(), ctx);
//                                                                                    }}});
//                                                                            }})
//
//                                                                            // Cancel
//                                                                            .setNegativeButton("cancel", null)
//                                                                            .setCancelable(false)
//                                                                            .setIcon(R.drawable.logo);
//                                                                    alert2.create().show();
//                                                                    break;
//
//                                                            }}})// ./ switch
//                                                    .setCancelable(false)
//                                                    .setNegativeButton("Cancel", null);
//                                            alert.create().show();
//
//
//
//
//
//
//                                        // MARK: - OPTIONS FOR POST - BY CURRENT USER
//                                        // ------------------------------------------------
//                                        } else {
//                                            // Check if can comment
//                                            final boolean canComment = pObj.getBoolean(POSTS_CAN_COMMENT);
//                                            final String[] commenting = {""};
//                                            if (canComment) { commenting[0] = "Turn Comments Off";
//                                            } else { commenting[0] = "Turn Comments On"; }
//
//                                             AlertDialog.Builder alert  = new AlertDialog.Builder(ctx);
//                                             alert.setTitle("Select Option")
//                                                     .setIcon(R.drawable.logo)
//                                                     .setItems(new CharSequence[] {
//                                                             commenting[0],
//                                                                        "Edit Post",
//                                                                        "Share Post",
//                                                                        "Delete Post"
//                                                     }, new DialogInterface.OnClickListener() {
//                                                         public void onClick(DialogInterface dialog, int which) {
//                                                             switch (which) {
//
//                                                                 // Turn Comments On/Off --------------------------------------------------------------------------------------------------
//                                                                 case 0:
//                                                                     // Comments OFF
//                                                                     if (canComment) {
//                                                                         pObj.put(POSTS_CAN_COMMENT, false);
//                                                                         pObj.saveInBackground();
//                                                                         commenting[0] = "Turn Comments On";
//                                                                         // Hide views
//                                                                         commentsButt.setVisibility(View.INVISIBLE);
//                                                                         commentsTxt.setVisibility(View.INVISIBLE);
//                                                                         yourAvatarImg.setVisibility(View.INVISIBLE);
//                                                                         addCommentTxt.setVisibility(View.INVISIBLE);
//
//                                                                     // Comments ON
//                                                                     } else {
//                                                                     pObj.put(POSTS_CAN_COMMENT, true);
//                                                                     pObj.saveInBackground();
//                                                                     commenting[0] = "Turn Comments Off";
//                                                                     // Show Views
//                                                                         commentsButt.setVisibility(View.VISIBLE);
//                                                                         commentsTxt.setVisibility(View.VISIBLE);
//                                                                         yourAvatarImg.setVisibility(View.VISIBLE);
//                                                                         addCommentTxt.setVisibility(View.VISIBLE);
//
//                                                                     }
//                                                                     break;
//
//
//                                                                 // Edit Post --------------------------------------------------------------------------------------------------
//                                                                 case 1:
//                                                                     // Pass objectID to the ShareScreen
//                                                                     Intent i = new Intent(ctx, ShareScreen.class);
//                                                                     Bundle extras = new Bundle();
//                                                                     extras.putString("objectID", pObj.getObjectId());
//
//                                                                     Bitmap bm = ((BitmapDrawable)postImg.getDrawable()).getBitmap();
//                                                                     ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                                                     bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                                                                     byte[] byteArray = stream.toByteArray();
//                                                                     extras.putByteArray("imageToShare", byteArray);
//
//                                                                     i.putExtras(extras);
//                                                                     startActivity(i);
//                                                                     break;
//
//
//                                                                 // Share Post --------------------------------------------------------------------------------------------------
//                                                                 case 2:
//                                                                     Bitmap bitmap = ((BitmapDrawable) postImg.getDrawable()).getBitmap();
//                                                                     Uri uri = getImageUri(bitmap, ctx);
//                                                                     Intent intent = new Intent(Intent.ACTION_SEND);
//                                                                     intent.setType("image/jpeg");
//                                                                     intent.putExtra(Intent.EXTRA_STREAM, uri);
//                                                                     intent.putExtra(Intent.EXTRA_TEXT, pObj.getString(POSTS_TEXT));
//                                                                     startActivity(Intent.createChooser(intent, "Share on..."));
//                                                                     break;
//
//
//
//                                                                 // Delete Post --------------------------------------------------------------------------------------------------
//                                                                 case 3:
//                                                                     AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
//                                                                     alert.setMessage("Are you sur you want to delete this Post?")
//                                                                         .setTitle(R.string.app_name)
//
//                                                                         // Delete
//                                                                         .setPositiveButton("Delete Post", new DialogInterface.OnClickListener() {
//                                                                             @Override
//                                                                             public void onClick(DialogInterface dialog, int which) {
//                                                                                 pObj.deleteInBackground(new DeleteCallback() {
//                                                                                     @Override
//                                                                                     public void done(ParseException e) {
//                                                                                         postsArray.remove(position);
//                                                                                         postsListView.invalidateViews();
//                                                                                         postsListView.refreshDrawableState();
//                                                                                 }});
//                                                                         }})
//
//                                                                         // Cancel
//                                                                         .setNegativeButton("Cancel", null)
//                                                                         .setCancelable(false)
//                                                                         .setIcon(R.drawable.logo);
//                                                                     alert.create().show();
//                                                                     break;
//
//                                                             }}})// ./ switch
//
//                                                     // CANCEL BUTTON
//                                                     .setNegativeButton("Cancel", null)
//                                                     .setCancelable(false);
//                                             alert.create().show();
//
//
//                                        }// ./ if
//                                }});
//
//
//
//
//
//
//
//                                //-----------------------------------------------
//                                // MARK - LIKE POST BUTTON
//                                //-----------------------------------------------
//                                likeButt.setOnClickListener(new View.OnClickListener() {
//                                  @Override
//                                  public void onClick(View view) {
//                                      List<String>likedBy = pObj.getList(POSTS_LIKED_BY);
//
//                                      // UNLIKE POST
//                                      assert likedBy != null;
//                                      if (likedBy.contains(currentUser.getObjectId()) ){
//                                          likedBy.remove(currentUser.getObjectId());
//                                          pObj.increment(POSTS_LIKES, -1);
//                                          pObj.put(POSTS_LIKED_BY, likedBy);
//                                          pObj.saveInBackground();
//
//                                          likeButt.setBackgroundResource(R.drawable.like_butt);
//                                          int likes = pObj.getInt(POSTS_LIKES);
//                                          likesTxt.setText(roundLargeNumber(likes));
//
//                                      // LIKE POST
//                                      } else {
//                                          likedBy.add(currentUser.getObjectId());
//                                          pObj.increment(POSTS_LIKES,  1);
//                                          pObj.put(POSTS_LIKED_BY, likedBy);
//                                          pObj.saveInBackground();
//
//                                        likeButt.setBackgroundResource(R.drawable.liked_butt);
//                                        int likes = pObj.getInt(POSTS_LIKES);
//                                        likesTxt.setText(roundLargeNumber(likes));
//
//                                        // Send Push Notification & save Notification
//                                        String push = "@" + currentUser.getString(USER_USERNAME) + " liked your Post: '" + pObj.getString(POSTS_TEXT) + "'";
//                                        sendPushNotification(push, (ParseUser) userPointer, ctx);
//                                      }
//                                }});
//
//
//
//
//                                // ------------------------------------------------
//                                // MARK: - COMMENT POST BUTTON
//                                // ------------------------------------------------
//                                commentsButt.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent i = new Intent(ctx, Comments.class);
//                                        Bundle extras = new Bundle();
//                                        extras.putString("objectID", pObj.getObjectId());
//                                        i.putExtras(extras);
//                                        startActivity(i);
//                                }});
//
//
//
//
//                                // ------------------------------------------------
//                                // MARK: - BOOKMARK POST BUTTON
//                                // ------------------------------------------------
//                                bookmarkButt.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        List<String>bookmarkedBy = pObj.getList(POSTS_BOOKMARKED_BY);
//
//                                        // Un-bookmark Post
//                                        assert bookmarkedBy != null;
//                                        if (bookmarkedBy.contains(currentUser.getObjectId()) ){
//                                            bookmarkedBy.remove(currentUser.getObjectId());
//                                            pObj.put(POSTS_BOOKMARKED_BY, bookmarkedBy);
//                                            pObj.saveInBackground();
//
//                                            bookmarkButt.setBackgroundResource(R.drawable.bookmark_butt);
//
//                                        // Bookmark Post
//                                        } else{
//                                            bookmarkedBy.add(currentUser.getObjectId());
//                                            pObj.put(POSTS_BOOKMARKED_BY, bookmarkedBy);
//                                            pObj.saveInBackground();
//
//                                            bookmarkButt.setBackgroundResource(R.drawable.bookmarked_butt);
//                                        }
//                                }});
//
//
//
//
//                                //-----------------------------------------------
//                                // MARK - PLAY VIDEO BUTTON
//                                //-----------------------------------------------
//                                playVideoButt.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                        // Get video URL
//                                        final ParseFile videoFile = pObj.getParseFile(POSTS_VIDEO);
//                                        assert videoFile != null;
//                                        String videoURL = videoFile.getUrl();
//                                        videoProgBar.setVisibility(View.VISIBLE);
//
//                                        // Get screen size
//                                        DisplayMetrics displayMetrics = new DisplayMetrics();
//                                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                                        int W = displayMetrics.widthPixels;
//
//                                        // Setup videoView's size to square
//                                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
//                                        lp.width = W;
//                                        lp.height = W;
//                                        lp.addRule(RelativeLayout.BELOW, dismVideoButt.getId());
//                                        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                                        videoView.setLayoutParams(lp);
//                                        Log.i("log-", "VIDEOVIEW WIDTH: " + videoView.getWidth());
//                                        Log.i("log-", "VIDEOVIEW HEIGHT: " + videoView.getHeight());
//
//
//                                        // Show Video Layout
//                                        showVideoLayout();
//
//                                        videoView.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
//                                        videoView.setDataSource(videoURL);
//                                        videoView.setLooping(true);
//                                        videoView.setListener(new TextureVideoView.MediaPlayerListener() {
//                                            @Override
//                                            public void onVideoPrepared() {
//                                                videoProgBar.setVisibility(View.INVISIBLE);
//                                                videoView.play();
//                                            }
//
//                                            @Override
//                                            public void onVideoEnd() { }
//                                        });
//
//                                        //-----------------------------------------------
//                                        // MARK - DISMISS VIDEO BUTTON
//                                        //-----------------------------------------------
//                                        dismVideoButt.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                videoView.stop();
//                                                videoView.invalidate();
//                                                hideVideoLayout();
//                                        }});
//                                }});
//
//                    // error
//                    } else { simpleAlert(e.getMessage(), ctx);
//                }}});// end userPointer
//
//
//            return cell;
//            }
//
//            @Override public int getCount() { return postsArray.size(); }
//            @Override public Object getItem(int position) { return postsArray.get(position); }
//            @Override public long getItemId(int position) { return position; }
//        }
//
//        // Set adapter
//        postsListView.setAdapter(new ListAdapter(ctx));
//    }


    //-----------------------------------------------
    // MARK - SHOW/HIDE VIDEO LAYOUT
    //-----------------------------------------------
//    void showVideoLayout() {
//        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(videoContainerLayout.getLayoutParams());
//        marginParams.setMargins(0, 0, 0, 0);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//        videoContainerLayout.setLayoutParams(layoutParams);
//    }
//    void hideVideoLayout() {
//        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(videoContainerLayout.getLayoutParams());
//        marginParams.setMargins(0, 10000, 0, 0);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//        videoContainerLayout.setLayoutParams(layoutParams);
//    }


    //-----------------------------------------------
    // MARK - QUERY MOMENTS
    //-----------------------------------------------
//    void queryMoments() {
//        Log.i(TAG, "QUERY MOMENTS...");
//
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        List<String>currUserObjID = new ArrayList<>();
//        currUserObjID.add(currentUser.getObjectId());
//
//        // Clear moments array
//        momentsArray = new ArrayList<>();
//
//        // Launch query
//        ParseQuery<ParseObject>query = ParseQuery.getQuery(MOMENTS_CLASS_NAME);
//        query.whereContainedIn(MOMENTS_FOLLOWED_BY, currUserObjID);
//        query.orderByDescending(MOMENTS_CREATED_AT);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                if (e == null) {
//
//                    // Some Moment exists
//                    if (objects.size() != 0) {
//                        momentsArray = objects;
//
//                        // Scrollview and embedded Layout
//                        final LinearLayout momentsLayout = findViewById(R.id.hMomentsLayout);
//                        momentsLayout.setOrientation(LinearLayout.HORIZONTAL);
//                        momentsLayout.removeAllViews();
//
//                        for (int i = 0; i < momentsArray.size(); i++) {
//                            final int finalI = i;
//
//                            final ParseObject mObj = objects.get(i);
//
//                            // Get userPointer
//                            Objects.requireNonNull(mObj.getParseObject(MOMENTS_USER_POINTER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//                                public void done(final ParseObject userPointer, ParseException e) {
//
//                                    // Size variables
//                                    int W = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
//                                    int H = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
//                                    int H20 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
//
//                                    // Start with a container Layout
//                                    RelativeLayout container = new RelativeLayout(ctx);
//                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(W, LinearLayout.LayoutParams.MATCH_PARENT);
//                                    lp.setMargins(20, 0, 20, 0);
//                                    container.setLayoutParams(lp);
//
//
//                                    // ------------------------------------------------
//                                    // MARK: - WATCH MOMENT'S VIDEO IMAGE
//                                    // ------------------------------------------------
//                                    CircleImageView avImg = new CircleImageView(ctx);
//                                    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(W, H);
//                                    lp2.addRule(RelativeLayout.CENTER_VERTICAL);
//                                    avImg.setLayoutParams(lp2);
//                                    container.addView(avImg);
//
//                                    avImg.setId(finalI);
//                                    avImg.setBorderWidth(2);
//                                    avImg.setBorderColor(Color.parseColor(MAIN_COLOR));
//
//                                    // Get avatar image
//                                    getParseImage(avImg, userPointer, USER_AVATAR);
//                                    // onClick
//                                    avImg.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Intent i = new Intent(ctx, WatchVideo.class);
//                                            Bundle extras = new Bundle();
//                                            extras.putString("objectID", mObj.getObjectId());
//                                            i.putExtras(extras);
//                                            startActivity(i);
//                                        }
//                                    });
//
//
//                                    //-----------------------------------------------
//                                    // MARK - USERNAME TXT
//                                    //-----------------------------------------------
//                                    TextView uTxt = new TextView(ctx);
//                                    RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(W, H20);
//                                    lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                                    lp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                                    lp3.setMargins(0, 0, 0, 0);
//                                    uTxt.setLayoutParams(lp3);
//                                    container.addView(uTxt);
//
//                                    uTxt.setTypeface(osItalic);
//                                    uTxt.setTextSize(10);
//                                    uTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                                    uTxt.setText(userPointer.getString(USER_USERNAME));
//
//
//                                    // Add container to momentsLayout
//                                    momentsLayout.addView(container);
//
//                            }});// end userPointer
//
//                        }// ./For loop
//
//                    }// ./ If
//
//            // error
//            } else { simpleAlert(e.getMessage(),ctx);
//
//        }}});// ./ query
//
//
//
//        // Check if you have posted a Moment
//        ParseQuery<ParseObject>query2 = ParseQuery.getQuery(MOMENTS_CLASS_NAME);
//        query2.whereEqualTo(MOMENTS_USER_POINTER, currentUser);
//        query2.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects2, ParseException e) {
//                if (e == null) {
//
//                    // You have a posted Moment
//                    if (objects2.size() != 0) {
//                        yourMomentObj = objects2.get(0);
//                        isYourMoment = true;
//                        plusIcon.setVisibility(View.INVISIBLE);
//                        yourMomentImg.setBorderColor(Color.parseColor(MAIN_COLOR));
//                    // You haven't posted a Moment
//                    } else{
//                        isYourMoment = false;
//                        plusIcon.setVisibility(View.VISIBLE);
//                        yourMomentImg.setBorderColor(Color.parseColor(GREY));
//                    }
//
//                // error
//                } else { simpleAlert(e.getMessage(), ctx);
//
//        }}});// ./ query2
//    }


    //-----------------------------------------------
    // MARK - QUERY INTERESTING PEOPLE
    //-----------------------------------------------
//    void queryInterestingPeople() {
//        Log.i(TAG, "QUERY INTERESTING PEOPLE...");
//
//        // Current User
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        List<String>currObjID = new ArrayList<>();
//        currObjID.add(currentUser.getObjectId());
//
//        // Reset interestingPeople array
//        interestingPeopleArray = new ArrayList<>();
//
//        // No Interesting People Txt
//        final TextView noInterestingPeopleTxt = findViewById(R.id.iplNoPeopleTxt);
//        noInterestingPeopleTxt.setTypeface(osRegular);
//        noInterestingPeopleTxt.setVisibility(View.INVISIBLE);
//
//        // Array of Following
//        List<String>isFollowing = currentUser.getList(USER_IS_FOLLOWING);
//
//        // Query Interesting Users
//        ParseQuery<ParseUser>query = ParseQuery.getQuery(USER_CLASS_NAME);
//        query.whereNotContainedIn("objectId", isFollowing);
//        query.whereNotEqualTo("objectId", currentUser.getObjectId());
//        query.whereEqualTo(USER_IS_REPORTED, false);
//        query.whereNotContainedIn(USER_NOT_INTERESTING_FOR, currObjID);
//        query.setLimit(10);
//        query.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> objects, ParseException e) {
//                if (e == null) {
//                    interestingPeopleArray = objects;
//                    Log.i(TAG, "INTERESTING PEOPLE COUNT: " + interestingPeopleArray.size());
//
//                    // There's some interesting people
//                    if (interestingPeopleArray.size() != 0 ){
//                        for (int j = 0; j<interestingPeopleArray.size(); j++){
//                            ParseUser user = objects.get(j);
//                            Log.i(TAG, "INTERESTING USER: " + user.getString(USER_USERNAME));
//                            if (j == interestingPeopleArray.size()-1){
//                                showInterestingPeople();
//                            }
//                        }// ./ FOR loop
//
//                    // NO interesting people
//                    } else { noInterestingPeopleTxt.setVisibility(View.VISIBLE); }
//
//
//                } else {
//                    simpleAlert(e.getMessage(), ctx);
//        }}});
//    }
    Uri videoURI = null;

    //-----------------------------------------------
    // MARK - ON START
    //-----------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();


        //-----------------------------------------------
        // MARK - CURRENT USER NOT LOGGED IN!
        //-----------------------------------------------

        SharedPreferences sp = getSharedPreferences("LoginResponse", Activity.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sp.getString("LoginResponse", "");
        LoginResponse LogIn = gson.fromJson(json, LoginResponse.class);
        if (LogIn != null) {
            if (!(LogIn.isUserExists())) {
                startActivity(new Intent(ctx, Intro.class));
                this.finish();
                //-----------------------------------------------
                // MARK - CURRENT USER IS LOGGED IN
                //-----------------------------------------------
            } else {

                //   Toast.makeText(this,"HI"+LogIn.getUserProfile().getFirst_name(),Toast.LENGTH_LONG).show();
            }
        } else {

            startActivity(new Intent(ctx, Intro.class));
            this.finish();
        }

        {

//            ParseUser currentUser = ParseUser.getCurrentUser();
//
//            // Get allowPush
//            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
//            allowPush = prefs.getBoolean("allowPush", true);
//            Log.i(TAG, "ALLOW PUSH NOTIFICATIONS: " + allowPush);
//
//
//            final ParseInstallation inst = ParseInstallation.getCurrentInstallation();
//
//            // IMPORTANT: REPLACE "191162618244" WITH YOUR OWN GCM SENDER ID
//            inst.put("GCMSenderId", "191162618244");
//            inst.put("userID", ParseUser.getCurrentUser().getObjectId());
//            inst.put("username", ParseUser.getCurrentUser().getUsername());
//            inst.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//
//                    // Now save the deviceToken
//                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
//                    HashMap<String, Object> params = new HashMap<>(2);
//                    params.put("installationId", inst.getObjectId());
//                    params.put("deviceToken", deviceToken);
//                    ParseCloud.callFunctionInBackground("setDeviceToken", params, new FunctionCallback<Boolean>() {
//                        @Override
//                        public void done(java.lang.Boolean success, ParseException e) {
//                            if (e == null) { Log.i("log-", "REGISTERED FOR PUSH NOTIFICATIONS!");
//                            } else { Log.i(TAG, Objects.requireNonNull(e.getMessage()));
//                    }}});
//                }});


            // Your Moment's Avatar
            //     getParseImage(yourMomentImg, currentUser, USER_AVATAR);


            // Launch Query
            // queryMoments();
        }


    }// ./ onStart

    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        titleTxt = findViewById(R.id.hTitleTxt);
        titleTxt.setTypeface(lobster);
        titleTxt.setText(getString(R.string.app_name));
        // Initilize TabLayout
        initTabLayout();

        //-----------------------------------------------
        // MARK - TAB BAR BUTTONS
        //-----------------------------------------------
        Button tab1 = findViewById(R.id.tab_two);
        Button tab2 = findViewById(R.id.tab_three);
        Button tab3 = findViewById(R.id.tab_four);
        Button tab4 = findViewById(R.id.tab_five);
        Button searchButt = findViewById(R.id.search_butt_homeac);
        instantButt = findViewById(R.id.hInstantsButt);

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, SearchScreen.class));
                Toast.makeText(Home.this, "Click Rec", Toast.LENGTH_LONG).show();
            }
        });

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    startActivity(new Intent(ctx, SquareCamera.class));
                }
            }
        });

        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Notifications.class));
            }
        });

        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, Account.class));
            }
        });



        //-----------------------------------------------
        // MARK - LAUNCH QUERY FOR POSTS
        //-----------------------------------------------
//        if (ParseUser.getCurrentUser().getObjectId() != null) { queryPosts(); }


        // ------------------------------------------------
        // MARK: - YOUR MOMENT IMAGE BUTTON
        // ------------------------------------------------
//        yourMomentImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Watch your Moment's video
//                if (isYourMoment) {
//
//                    // Pass objectID to the other Activity
//                    Intent i = new Intent(ctx, WatchVideo.class);
//                    Bundle extras = new Bundle();
//                    extras.putString("objectID", yourMomentObj.getObjectId());
//                    i.putExtras(extras);
//                    startActivity(i);
//
//                // Record a Moment's video
//                } else { openVideoCamera(); }
//        }});


        // ------------------------------------------------
        // MARK: - FIND PEOPLE BUTTON
        // ------------------------------------------------
//        findPeopleButt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ctx, InterestingPeople.class));
//        }});


        //-----------------------------------------------
        // MARK - INSTANT BUTTON
        //-----------------------------------------------
        instantButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(ctx, Instants.class));

                Toast.makeText(Home.this, "Clicked", Toast.LENGTH_LONG).show();
        }});


        //-----------------------------------------------
        // MARK - CALL ADMOB ADS
        //-----------------------------------------------


    }// end onCreate()

    //-----------------------------------------------
    // MARK - SHOW INTERESTING PEOPLE
    //-----------------------------------------------
    @SuppressLint("SetTextI18n")
    void showInterestingPeople() {
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // Scrollview and embedded Layout
        final LinearLayout interesingLayout = findViewById(R.id.iplIntPeopleLayout);
        interesingLayout.setOrientation(LinearLayout.HORIZONTAL);
        interesingLayout.removeAllViews();

        for (int i = 0; i < interestingPeopleArray.size(); i++) {
            final int finalI = i;
            final ParseUser user = interestingPeopleArray.get(i);

            // Size variables
            int W = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            int H = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
            int WH44 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getResources().getDisplayMetrics());
            int WH28 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics());
            int WH38 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38, getResources().getDisplayMetrics());
            int WH54 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54, getResources().getDisplayMetrics());

            // Start with a container Layout
            RelativeLayout container = new RelativeLayout(ctx);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(W, H);
            lp.setMargins(20, 0, 20, 0);
            container.setLayoutParams(lp);
            container.setBackgroundResource(R.drawable.rounded_edittext);


            // ------------------------------------------------
            // MARK: - AVATAR
            // ------------------------------------------------
            CircleImageView avImg = new CircleImageView(ctx);
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(WH44, WH44);
            lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp2.setMargins(0, 10, 0, 0);
            avImg.setLayoutParams(lp2);
            avImg.setId(finalI);
            container.addView(avImg);

            getParseImage(avImg, user, USER_AVATAR);
            avImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Pass objectID to the other Activity
                    Intent i = new Intent(ctx, UserProfile.class);
                    Bundle extras = new Bundle();
                    extras.putString("objectID", user.getObjectId());
                    i.putExtras(extras);
                    startActivity(i);
                }
            });


            //-----------------------------------------------
            // MARK - FULL NAME TXT
            //-----------------------------------------------
            TextView fullNameTxt = new TextView(ctx);
            RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp4.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp4.setMargins(5, WH54, 5, 0);
            fullNameTxt.setLayoutParams(lp4);
            container.addView(fullNameTxt);

            fullNameTxt.setTypeface(osBold);
            fullNameTxt.setText(user.getString(USER_FULLNAME));
            fullNameTxt.setMaxLines(1);
            fullNameTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            fullNameTxt.setTextSize(9);
            fullNameTxt.setTextColor(Color.parseColor("#333333"));
            fullNameTxt.setId(finalI);


            //-----------------------------------------------
            // MARK - REMOVE PEOPLE BUTTON
            //-----------------------------------------------
            Button removePeopleButt = new Button(ctx);
            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(WH28, WH28);
            lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp3.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp3.addRule(RelativeLayout.ALIGN_PARENT_END);
            lp3.setMargins(0, 0, 0, 0);
            removePeopleButt.setLayoutParams(lp3);
            container.addView(removePeopleButt);

            removePeopleButt.setBackgroundResource(R.drawable.dismiss_butt_black);

            removePeopleButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHUD(ctx);

                    // Set User as Not Interesting
                    ParseUser user = interestingPeopleArray.get(finalI);
                    List<String> notInterestingFor = user.getList(USER_NOT_INTERESTING_FOR);
                    assert notInterestingFor != null;
                    notInterestingFor.add(currentUser.getObjectId());
                    Log.i(TAG, "NOT INTERESTING ARRAY: " + notInterestingFor);

                    final HashMap<String, Object> params = new HashMap<>();
                    params.put("userId", user.getObjectId());
                    params.put("notInterestingFor", notInterestingFor);
                    ParseCloud.callFunctionInBackground("setUserNotInteresting", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if (e == null) {
                                hideHUD();
                                Log.i(TAG, "USER REMOVED");
                                interesingLayout.removeViewAt(finalI);

                            } else {
                                hideHUD();
                                Log.i(TAG, "ERROR: " + e.getMessage());
                            }
                        }
                    });

                    // Reset Array and ScrollView
                    interestingPeopleArray.remove(finalI);

                    // Recall function
                    showInterestingPeople();
                }
            });


            // ------------------------------------------------
            // MARK: - FOLLOW AN INTERESTING USER BUTTON
            // ------------------------------------------------
            Button followButt = new Button(ctx);
            RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WH38);
            lp5.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp5.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp5.setMargins(5, H - WH38, 5, 5);
            followButt.setLayoutParams(lp5);
            container.addView(followButt);

            followButt.setTypeface(osSemibold);
            followButt.setTextColor(Color.WHITE);
            followButt.setText("Follow");
            followButt.setTextSize(12);
            followButt.setTypeface(osSemibold);
            followButt.setAllCaps(false);
            followButt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(MAIN_COLOR)));

            followButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG, "USER INDEX: " + finalI);

                    // Parse User data
                    final ParseUser aUser = interestingPeopleArray.get(finalI);
                    List<String> isFollowing = currentUser.getList(USER_IS_FOLLOWING);
                    assert isFollowing != null;
                    isFollowing.add(aUser.getObjectId());
                    currentUser.put(USER_IS_FOLLOWING, isFollowing);
                    currentUser.saveInBackground();

                    showHUD(ctx);


                    // Dispatch Async
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Add Follow Object
                            ParseObject fObj = new ParseObject(FOLLOW_CLASS_NAME);
                            fObj.put(FOLLOW_CURRENT_USER, currentUser);
                            fObj.put(FOLLOW_IS_FOLLOWING, aUser);
                            fObj.saveInBackground();


                            // Add currentUser's ID to followedBy in Posts
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(POSTS_CLASS_NAME);
                            query.whereEqualTo(POSTS_USER_POINTER, aUser);
                            query.findInBackground(new FindCallback<ParseObject>() {
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
                                            Log.i(TAG, "followedBy SAVED IN 'Posts'");
                                        }
                                        // error
                                    } else {
                                        hideHUD();
                                        simpleAlert(e.getMessage(), ctx);
                                    }
                                }
                            });


                            // Add currentUser's ID to followedBy in Moments
                            ParseQuery<ParseObject> query2 = ParseQuery.getQuery(MOMENTS_CLASS_NAME);
                            query2.whereEqualTo(MOMENTS_USER_POINTER, aUser);
                            query2.findInBackground(new FindCallback<ParseObject>() {
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
                                                Log.i(TAG, "followedBy SAVED IN 'Moments'");
                                            }
                                        }
                                        // error
                                    } else {
                                        hideHUD();
                                        simpleAlert(e.getMessage(), ctx);
                                    }
                                }
                            });

                        }
                    });// ./ Duispatch Async


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
                            if (e == null) {

                                // Send Push Notification
                                String pushMessage = "@" + currentUser.getString(USER_USERNAME) + " started following you";
                                sendPushNotification(pushMessage, aUser, ctx);

                                // Reset Array and ScrollView
                                interesingLayout.removeViewAt(finalI);
                                interestingPeopleArray.remove(finalI);

                                hideHUD();

                                // Recall function
                                showInterestingPeople();

                                // error
                            } else {
                                hideHUD();
                                simpleAlert(e.getMessage(), ctx);
                            }
                        }
                    });// ./ ParseCloud


                }
            });// ./ followButt


            // Add container into interestingLayout
            interesingLayout.addView(container);

        }// ./For loop
    }

    public void openVideoCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAXIMUM_RECORDING_TIME);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, VIDEO_CAMERA);
    }

    // VIDEO - ON ACTIVITY RESULT ------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            // Video from Camera or Gallery
            if (requestCode == VIDEO_CAMERA) {
                videoURI = data.getData();
                videoPath = getRealPathFromURI(videoURI, ctx);
                // Log.i("log-", "VIDEO URI: " + videoURI);

                // Check video duration
                MediaPlayer mp = MediaPlayer.create(this, videoURI);
                int videoDuration = mp.getDuration();
                mp.release();
                Log.i("log-", "VIDEO DURATION: " + videoDuration / 1000);


                // Video duration is within the allowed maximum duration
                if (videoDuration < MAXIMUM_RECORDING_TIME * 1000) {

                    uploadMomentVideo();

                    // Moment Video is longer than the maximum duration
                } else {
                    simpleAlert("Your video is longer than " +
                            MAXIMUM_RECORDING_TIME +
                            " seconds. You have to take a shorter video", ctx);

                    // Reset variables and image
                    videoPath = null;
                    videoURI = null;
                }

            }// ./* IF VIDEO_CAMERA

        }// ./ IF Result OK
    }


    //-----------------------------------------------
    // MARK - UPLOAD MOMENT VIDEO
    //-----------------------------------------------
    void uploadMomentVideo() {
        showHUD(ctx);
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // Parse Object
        final ParseObject mObj = new ParseObject(MOMENTS_CLASS_NAME);
        final List<String> empty = new ArrayList<>();

        // Video file
        ParseFile videoFile = new ParseFile("moment.mp4", convertVideoToBytes(videoPath));
        mObj.put(MOMENTS_VIDEO, videoFile);
        mObj.put(MOMENTS_USER_POINTER, currentUser);

        // Query currentUser's Followers
        ParseQuery<ParseObject> query = ParseQuery.getQuery(FOLLOW_CLASS_NAME);
        query.whereEqualTo(FOLLOW_IS_FOLLOWING, currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    // currentUser has followers
                    if (objects.size() != 0) {
                        final List<String> followedBy = new ArrayList<>();

                        // Append Users Id's to followedBy
                        for (int i = 0; i < objects.size(); i++) {
                            final int finalI = i;

                            // Parse Object
                            ParseObject fObj = objects.get(i);

                            // userPointer
                            Objects.requireNonNull(fObj.getParseObject(FOLLOW_CURRENT_USER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject userPointer, ParseException e) {
                                    if (e == null) {
                                        // Append Users ID's
                                        followedBy.add(userPointer.getObjectId());
                                        // Log.i(TAG, "FOLLOWER USER ID: " + userPointer.getObjectId());

                                        // For loop ends -> Prepare Data to be saved
                                        if (finalI == objects.size() - 1) {
                                            // Prepare data
                                            Log.i(TAG, "FOLLOWED BY: " + followedBy);
                                            mObj.put(MOMENTS_FOLLOWED_BY, followedBy);
                                            mObj.put(MOMENTS_USER_POINTER, currentUser);
                                            mObj.put(MOMENTS_REPORTED_BY, empty);

                                            // Save Data
                                            mObj.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        hideHUD();
                                                    } else {
                                                        hideHUD();
                                                        simpleAlert(e.getMessage(), ctx);
                                                    }
                                                }
                                            });

                                        }// ./ IF ends

                                        // error on userPointer
                                    } else {
                                        hideHUD();
                                        simpleAlert(e.getMessage(), ctx);
                                    }
                                }
                            });// ./ userPointer

                        }// ./ FOR loop


                        // currentUser has NO followers
                    } else {
                        // Prepare data
                        mObj.put(MOMENTS_FOLLOWED_BY, empty);
                        mObj.put(MOMENTS_USER_POINTER, currentUser);
                        mObj.put(MOMENTS_REPORTED_BY, empty);

                        // Save Data
                        mObj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    hideHUD();
                                } else {
                                    hideHUD();
                                    simpleAlert(e.getMessage(), ctx);
                                }
                            }
                        });
                    }


                    // error in query
                } else {
                    hideHUD();
                    simpleAlert(e.getMessage(), ctx);
                }
            }
        });// ./ Query currentUser's Followers

    }


    //-----------------------------------------------
    // MARK - REFRESH DATA
    //-----------------------------------------------
    @Override
    public void onRefresh() {
        // Recall queries
        //  queryPosts();
        //   queryMoments();

        //  if (refreshControl.isRefreshing()) { refreshControl.setRefreshing(false); }
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

    // Code by @mehraj

    void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Fallowing"));
        tabLayout.addTab(tabLayout.newTab().setText("Popular"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final PagerAdapter myadapter = new PagerAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
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


    // End by @mehraj


}// ./ end