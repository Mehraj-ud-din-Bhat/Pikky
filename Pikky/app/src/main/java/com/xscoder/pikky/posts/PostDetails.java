package com.xscoder.pikky.posts;

/*==================================================
     Pikky

     © XScoder 2018
     All Rights reserved

     RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
     YOU WILL BE LEGALLY PROSECUTED

==================================================*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.xscoder.pikky.R;
import com.xscoder.pikky.Search.SearchScreen;
import com.xscoder.pikky.Share.ShareScreen;
import com.xscoder.pikky.Edit.TextureVideoView;
import com.xscoder.pikky.Userprofile.AccountProfile.Account;
import com.xscoder.pikky.Userprofile.UserProfile;
import com.xscoder.pikky.comments.Comments;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.xscoder.pikky.Configurations.POSTS_BOOKMARKED_BY;
import static com.xscoder.pikky.Configurations.POSTS_CAN_COMMENT;
import static com.xscoder.pikky.Configurations.POSTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.POSTS_COMMENTS;
import static com.xscoder.pikky.Configurations.POSTS_IMAGE;
import static com.xscoder.pikky.Configurations.POSTS_LIKED_BY;
import static com.xscoder.pikky.Configurations.POSTS_LIKES;
import static com.xscoder.pikky.Configurations.POSTS_LOCATION;
import static com.xscoder.pikky.Configurations.POSTS_REPORTED_BY;
import static com.xscoder.pikky.Configurations.POSTS_TAGS;
import static com.xscoder.pikky.Configurations.POSTS_TEXT;
import static com.xscoder.pikky.Configurations.POSTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.POSTS_VIDEO;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_IS_VERIFIED;
import static com.xscoder.pikky.Configurations.USER_MUTED_BY;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.getImageUri;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osItalic;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.roundLargeNumber;
import static com.xscoder.pikky.Configurations.sendPushNotification;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class PostDetails extends AppCompatActivity {

    /*--- VIEWS ---*/
    ListView postListView;
    RelativeLayout videoContainerLayout;
    TextureVideoView videoView;
    Button dismVideoButt;
    ProgressBar videoProgBar;



    /*--- VARIABLES ---*/
    Context ctx = this;
    ParseObject pObj;
    List<ParseObject>postArray = new ArrayList<>();



    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        postListView = findViewById(R.id.pdPostListView);

        videoContainerLayout = findViewById(R.id.pdVideoContainerLayout);
        hideVideoLayout();

        videoView = findViewById(R.id.pdVideoView);
        dismVideoButt = findViewById(R.id.pdDismissVideoLayoutButt);
        videoProgBar = findViewById(R.id.pdVideoProgressBar);
        videoProgBar.setVisibility(View.VISIBLE);




        // Get data
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String objectID = extras.getString("objectID");
        pObj = ParseObject.createWithoutData(POSTS_CLASS_NAME, objectID);
        try { pObj.fetchIfNeeded().getParseObject(POSTS_CLASS_NAME);

            postArray.add(pObj);

            // Call method
            showPostDetails();

        } catch (ParseException e) { e.printStackTrace(); }



        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.pdBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }// ./ onCreate







    //-----------------------------------------------
    // MARK - SHOW POST DETAILS
    //-----------------------------------------------
    void showPostDetails() {
        class ListAdapter extends BaseAdapter {
            private Context context;

            private ListAdapter(Context context) {
                super();
                this.context = context;
            }

            @SuppressLint("InflateParams")
            @Override
            public View getView(final int position, View cell, ViewGroup parent) {
                if (cell == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    cell = inflater.inflate(R.layout.cell_post, null);
                }
                final View finalCell = cell;

                // Parse Object
                final ParseObject pObj = postArray.get(0);

                // Current User
                final ParseUser currentUser = ParseUser.getCurrentUser();

                // Get userPointer
                Objects.requireNonNull(pObj.getParseObject(POSTS_USER_POINTER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    public void done(final ParseObject userPointer, ParseException e) {
                        if (e == null) {

                            //-----------------------------------------------
                            // MARK - INITIALIZE VIEWS
                            //-----------------------------------------------
                            TextView usernameTxt = finalCell.findViewById(R.id.cpUsernameTxt);
                            usernameTxt.setTypeface(osBold);
                            ImageView verifiedBadge = finalCell.findViewById(R.id.cpVerifiedBadge);
                            TextView locationtxt = finalCell.findViewById(R.id.cpLocationTxt);
                            locationtxt.setTypeface(osRegular);
                            ImageView avatarImg = finalCell.findViewById(R.id.cpAvatarImg);
                            final ImageView postImg = finalCell.findViewById(R.id.cpPostImg);
                            Button playVideoButt = finalCell.findViewById(R.id.cpPlayVideoButt);
                            TextView postTextTxt = finalCell.findViewById(R.id.cpPostTextTxt);
                            postTextTxt.setTypeface(osRegular);
                            final TextView likesTxt = finalCell.findViewById(R.id.cpLikesTxt);
                            likesTxt.setTypeface(osRegular);
                            final TextView commentsTxt = finalCell.findViewById(R.id.cpCommentsTxt);
                            commentsTxt.setTypeface(osRegular);
                            final Button likeButt = finalCell.findViewById(R.id.cpLikeButt);
                            final Button commentsButt = finalCell.findViewById(R.id.cpCommentButt);
                            final Button bookmarkButt = finalCell.findViewById(R.id.cpBookmarkButt);
                            TextView dateTxt = finalCell.findViewById(R.id.cpDateTxt);
                            dateTxt.setTypeface(osRegular);
                            Button optionsButt = finalCell.findViewById(R.id.cpOptionsButt);

                            // Hide views
                            ImageView yourAvatarImg = finalCell.findViewById(R.id.cpYourAvatarImg);
                            yourAvatarImg.setVisibility(View.GONE);
                            EditText addCommentTxt = finalCell.findViewById(R.id.cpAddCommentTxt);
                            addCommentTxt.setVisibility(View.GONE);


                            //-----------------------------------------------
                            // MARK - SET POST IMAGE SIZE
                            //-----------------------------------------------
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int W = displayMetrics.widthPixels;

                            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(W, W);
                            lp2.setMargins(0, 10, 0,0);
                            lp2.addRule(RelativeLayout.BELOW, avatarImg.getId());
                            lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            postImg.setLayoutParams(lp2);



                            //-----------------------------------------------
                            // MARK - SHOW DATA
                            //-----------------------------------------------
                            // Username
                            usernameTxt.setText(userPointer.getString(USER_USERNAME));

                            // Verified Badge
                            boolean isVerified = userPointer.getBoolean(USER_IS_VERIFIED);
                            if (isVerified) { verifiedBadge.setVisibility(View.VISIBLE);
                            } else { verifiedBadge.setVisibility(View.INVISIBLE); }

                            // Post Location
                            if (pObj.getString(POSTS_LOCATION) != null) { locationtxt.setText(pObj.getString(POSTS_LOCATION));
                            } else { locationtxt.setText(""); }

                            // UserPointer Avatar
                            getParseImage(avatarImg, userPointer, USER_AVATAR);


                            // Post's Image or Video Thumbnail
                            getParseImage(postImg, pObj, POSTS_IMAGE);

                            // Post's Video
                            if (pObj.getParseFile(POSTS_VIDEO) != null){
                                playVideoButt.setVisibility(View.VISIBLE);
                            } else {
                                playVideoButt.setVisibility(View.INVISIBLE); }

                            // Post text
                            postTextTxt.setText(pObj.getString(POSTS_TEXT));

                            // Likes
                            int likes = pObj.getInt(POSTS_LIKES);
                            likesTxt.setText(roundLargeNumber(likes));
                            final List<String>likedBy = pObj.getList(POSTS_LIKED_BY);
                            assert likedBy != null;
                            if (likedBy.contains(currentUser.getObjectId()) ){
                                likeButt.setBackgroundResource(R.drawable.liked_butt);
                            } else {
                                likeButt.setBackgroundResource(R.drawable.like_butt);
                            }

                            // Check if Commenting is ON or OFF
                            boolean canComment = pObj.getBoolean(POSTS_CAN_COMMENT);
                            if (canComment) {
                                // Show views
                                commentsButt.setVisibility(View.VISIBLE);
                                commentsTxt.setVisibility(View.VISIBLE);
                            } else {
                                // Hide views
                                commentsButt.setVisibility(View.INVISIBLE);
                                commentsTxt.setVisibility(View.INVISIBLE);
                            }

                            // Comments
                            int comments = pObj.getInt(POSTS_COMMENTS);
                            commentsTxt.setText(roundLargeNumber(comments));

                            // Saved
                            List<String>bookmarkedBy = pObj.getList(POSTS_BOOKMARKED_BY);
                            assert bookmarkedBy != null;
                            if (bookmarkedBy.contains(currentUser.getObjectId()) ){
                                bookmarkButt.setBackgroundResource(R.drawable.bookmarked_butt);
                            } else {
                                bookmarkButt.setBackgroundResource(R.drawable.bookmark_butt);
                            }

                            // Date
                            Date aDate = pObj.getCreatedAt();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMM yyy");
                            dateTxt.setText(df.format(aDate).toUpperCase());

                            // Tags
                            List<String> tagsArray;
                            if (pObj.getList(POSTS_TAGS) != null) {
                                tagsArray = pObj.getList(POSTS_TAGS);
                                Log.i(TAG, "TAGS: " + tagsArray);

                                LinearLayout tagsLayout = finalCell.findViewById(R.id.cpTagsLayout);
                                tagsLayout.setOrientation(LinearLayout.HORIZONTAL);
                                tagsLayout.removeAllViews();

                                assert tagsArray != null;
                                for (int i = 0; i<tagsArray.size(); i++) {
                                    // Create Buttons
                                    final Button tButt = new Button(ctx);
                                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getResources().getDisplayMetrics());
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
                                    lp.setMargins(3, 0, 3, 0);
                                    tButt.setLayoutParams(lp);

                                    tButt.setText(tagsArray.get(i));
                                    tButt.setTypeface(osItalic);
                                    tButt.setTextColor(Color.parseColor("#777777"));
                                    tButt.setBackgroundColor(Color.parseColor("#00000000"));
                                    tButt.setTextSize(10);
                                    tButt.setAllCaps(false);
                                    tButt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String tag = tButt.getText().toString();
                                            Log.i(TAG, "SELECTED TAG: " + tag);

                                            Intent i = new Intent(ctx, SearchScreen.class);
                                            Bundle extras = new Bundle();
                                            extras.putString("searchText", tag);
                                            extras.putBoolean("isTag", true);
                                            i.putExtras(extras);
                                            startActivity(i);
                                        }});

                                    // Add button to the layout
                                    tagsLayout.addView(tButt);

                                }// ./For loop

                            }// ./ If




                            // ------------------------------------------------
                            // MARK: - USER'S AVATAR IMAGE
                            // ------------------------------------------------
                            avatarImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (!userPointer.getObjectId().matches(currentUser.getObjectId()) ){
                                        Intent i = new Intent(ctx, UserProfile.class);
                                        Bundle extras = new Bundle();
                                        extras.putString("objectID", userPointer.getObjectId());
                                        i.putExtras(extras);
                                        startActivity(i);

                                    } else {
                                        Intent i = new Intent(ctx, Account.class);
                                        Bundle extras = new Bundle();
                                        extras.putBoolean("isCurrentUser", true);
                                        i.putExtras(extras);
                                        startActivity(i);
                                    }
                                }});



                            // ------------------------------------------------
                            // MARK: - POST OPTIONS BUTTON
                            // ------------------------------------------------
                            optionsButt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    // MARK: - OPTIONS FOR POSTS - BY OTHER USERS
                                    // ------------------------------------------------
                                    if (!userPointer.getObjectId().matches(currentUser.getObjectId()) ) {

                                        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                        alert.setTitle("Select Option")
                                                .setIcon(R.drawable.logo)
                                                .setItems(new CharSequence[]{
                                                        "Share post",
                                                        "Pick from Gallery",
                                                        "Mute Posts"

                                                }, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        switch (which) {

                                                            // Share Post --------------------------------------------------------------------------------------------------
                                                            case 0:
                                                                Bitmap bitmap = ((BitmapDrawable) postImg.getDrawable()).getBitmap();
                                                                Uri uri = getImageUri(bitmap, ctx);
                                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                                intent.setType("image/jpeg");
                                                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                                                intent.putExtra(Intent.EXTRA_TEXT, pObj.getString(POSTS_TEXT));
                                                                startActivity(Intent.createChooser(intent, "Share on..."));
                                                                break;


                                                            // Report Post --------------------------------------------------------------------------------------------------
                                                            case 1:
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                                                alert.setMessage("Are you sure you want to report this Post?")
                                                                        .setTitle(R.string.app_name)

                                                                        // Report
                                                                        .setPositiveButton("Report as inappropriate", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                List<String> reportedBy = pObj.getList(POSTS_REPORTED_BY);
                                                                                assert reportedBy != null;
                                                                                reportedBy.add(currentUser.getObjectId());
                                                                                pObj.put(POSTS_REPORTED_BY, reportedBy);
                                                                                pObj.saveInBackground();
                                                                                simpleAlert("Thanks for reporting this Post to the Admin! We'll take action for it withint 24 hours.", ctx);
                                                                            }})

                                                                        // Cancel
                                                                        .setNegativeButton("Cancel", null)
                                                                        .setCancelable(false)
                                                                        .setIcon(R.drawable.logo);
                                                                alert.create().show();
                                                                break;


                                                            // Mute Posts --------------------------------------------------------------------------------------------------
                                                            case 2:
                                                                AlertDialog.Builder alert2 = new AlertDialog.Builder(ctx);
                                                                alert2.setMessage("Want to mute @" + userPointer.getString(USER_USERNAME) + "'s Posts?\nYou can still unmute this User from his/her Profile.")
                                                                        .setTitle(R.string.app_name)

                                                                        // Mute
                                                                        .setPositiveButton("Mute", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                List<String> mutedBy = userPointer.getList(USER_MUTED_BY);
                                                                                assert mutedBy != null;
                                                                                mutedBy.add(currentUser.getObjectId());

                                                                                HashMap<String, Object> params = new HashMap<>();
                                                                                params.put("userId", userPointer.getObjectId());
                                                                                params.put("mutedBy", mutedBy);

                                                                                ParseCloud.callFunctionInBackground("muteUnmuteUser", params, new FunctionCallback<ParseUser>() {
                                                                                    public void done(ParseUser user, ParseException error) {
                                                                                        if (error == null) {
                                                                                            simpleAlert("@" + userPointer.getString(USER_USERNAME) + "'s Posts have been muted", ctx);
                                                                                        // error
                                                                                        } else { simpleAlert(error.getMessage(), ctx);
                                                                                }}});
                                                                            }})

                                                                        // Cancel
                                                                        .setNegativeButton("cancel", null)
                                                                        .setCancelable(false)
                                                                        .setIcon(R.drawable.logo);
                                                                alert2.create().show();
                                                                break;

                                                        }}})// ./ switch
                                                .setCancelable(false)
                                                .setNegativeButton("Cancel", null);
                                        alert.create().show();






                                    // MARK: - OPTIONS FOR POST - BY CURRENT USER
                                    // ------------------------------------------------
                                    } else {
                                        // Check if can comment
                                        final boolean canComment = pObj.getBoolean(POSTS_CAN_COMMENT);
                                        final String[] commenting = {""};
                                        if (canComment) { commenting[0] = "Turn Comments Off";
                                        } else { commenting[0] = "Turn Comments On"; }

                                        AlertDialog.Builder alert  = new AlertDialog.Builder(ctx);
                                        alert.setTitle("Select Option")
                                                .setIcon(R.drawable.logo)
                                                .setItems(new CharSequence[] {
                                                        commenting[0],
                                                        "Edit Post",
                                                        "Share Post",
                                                        "Delete Post"
                                                }, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        switch (which) {

                                                            // Turn Comments On/Off --------------------------------------------------------------------------------------------------
                                                            case 0:
                                                                // Comments OFF
                                                                if (canComment) {
                                                                    pObj.put(POSTS_CAN_COMMENT, false);
                                                                    pObj.saveInBackground();
                                                                    commenting[0] = "Turn Comments On";
                                                                    // Hide views
                                                                    commentsButt.setVisibility(View.INVISIBLE);
                                                                    commentsTxt.setVisibility(View.INVISIBLE);

                                                                // Comments ON
                                                                } else {
                                                                    pObj.put(POSTS_CAN_COMMENT, true);
                                                                    pObj.saveInBackground();
                                                                    commenting[0] = "Turn Comments Off";
                                                                    // Show Views
                                                                    commentsButt.setVisibility(View.VISIBLE);
                                                                    commentsTxt.setVisibility(View.VISIBLE);
                                                                }
                                                                break;


                                                            // Edit Post --------------------------------------------------------------------------------------------------
                                                            case 1:
                                                                // Pass objectID to the ShareScreen
                                                                Intent i = new Intent(ctx, ShareScreen.class);
                                                                Bundle extras = new Bundle();
                                                                extras.putString("objectID", pObj.getObjectId());

                                                                Bitmap bm = ((BitmapDrawable)postImg.getDrawable()).getBitmap();
                                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                                                byte[] byteArray = stream.toByteArray();
                                                                extras.putByteArray("imageToShare", byteArray);

                                                                i.putExtras(extras);
                                                                startActivity(i);
                                                                break;


                                                            // Share Post --------------------------------------------------------------------------------------------------
                                                            case 2:
                                                                Bitmap bitmap = ((BitmapDrawable) postImg.getDrawable()).getBitmap();
                                                                Uri uri = getImageUri(bitmap, ctx);
                                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                                intent.setType("image/jpeg");
                                                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                                                intent.putExtra(Intent.EXTRA_TEXT, pObj.getString(POSTS_TEXT));
                                                                startActivity(Intent.createChooser(intent, "Share on..."));
                                                                break;



                                                            // Delete Post --------------------------------------------------------------------------------------------------
                                                            case 3:
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                                                alert.setMessage("Are you sur you want to delete this Post?")
                                                                        .setTitle(R.string.app_name)

                                                                        // Delete
                                                                        .setPositiveButton("Delete Post", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                pObj.deleteInBackground(new DeleteCallback() {
                                                                                    @Override
                                                                                    public void done(ParseException e) {
                                                                                        finish();
                                                                                    }});
                                                                            }})

                                                                        // Cancel
                                                                        .setNegativeButton("Cancel", null)
                                                                        .setCancelable(false)
                                                                        .setIcon(R.drawable.logo);
                                                                alert.create().show();
                                                                break;

                                                        }}})// ./ switch

                                                // CANCEL BUTTON
                                                .setNegativeButton("Cancel", null)
                                                .setCancelable(false);
                                        alert.create().show();


                                    }// ./ if
                                }});







                            //-----------------------------------------------
                            // MARK - LIKE POST BUTTON
                            //-----------------------------------------------
                            likeButt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    List<String>likedBy = pObj.getList(POSTS_LIKED_BY);

                                    // UNLIKE POST
                                    assert likedBy != null;
                                    if (likedBy.contains(currentUser.getObjectId()) ){
                                        likedBy.remove(currentUser.getObjectId());
                                        pObj.increment(POSTS_LIKES, -1);
                                        pObj.put(POSTS_LIKED_BY, likedBy);
                                        pObj.saveInBackground();

                                        likeButt.setBackgroundResource(R.drawable.like_butt);
                                        int likes = pObj.getInt(POSTS_LIKES);
                                        likesTxt.setText(roundLargeNumber(likes));

                                        // LIKE POST
                                    } else {
                                        likedBy.add(currentUser.getObjectId());
                                        pObj.increment(POSTS_LIKES,  1);
                                        pObj.put(POSTS_LIKED_BY, likedBy);
                                        pObj.saveInBackground();

                                        likeButt.setBackgroundResource(R.drawable.liked_butt);
                                        int likes = pObj.getInt(POSTS_LIKES);
                                        likesTxt.setText(roundLargeNumber(likes));

                                        // Send Push Notification & save Notification
                                        String push = "@" + currentUser.getString(USER_USERNAME) + " liked your Post: '" + pObj.getString(POSTS_TEXT) + "'";
                                        sendPushNotification(push, (ParseUser) userPointer, ctx);
                                    }
                                }});




                            // ------------------------------------------------
                            // MARK: - COMMENT POST BUTTON
                            // ------------------------------------------------
                            commentsButt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(ctx, Comments.class);
                                    Bundle extras = new Bundle();
                                    extras.putString("objectID", pObj.getObjectId());
                                    i.putExtras(extras);
                                    startActivity(i);
                                }});




                            // ------------------------------------------------
                            // MARK: - BOOKMARK POST BUTTON
                            // ------------------------------------------------
                            bookmarkButt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<String>bookmarkedBy = pObj.getList(POSTS_BOOKMARKED_BY);

                                    // Un-bookmark Post
                                    assert bookmarkedBy != null;
                                    if (bookmarkedBy.contains(currentUser.getObjectId()) ){
                                        bookmarkedBy.remove(currentUser.getObjectId());
                                        pObj.put(POSTS_BOOKMARKED_BY, bookmarkedBy);
                                        pObj.saveInBackground();

                                        bookmarkButt.setBackgroundResource(R.drawable.bookmark_butt);

                                        // Bookmark Post
                                    } else{
                                        bookmarkedBy.add(currentUser.getObjectId());
                                        pObj.put(POSTS_BOOKMARKED_BY, bookmarkedBy);
                                        pObj.saveInBackground();

                                        bookmarkButt.setBackgroundResource(R.drawable.bookmarked_butt);
                                    }
                                }});




                            //-----------------------------------------------
                            // MARK - PLAY VIDEO BUTTON
                            //-----------------------------------------------
                            playVideoButt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    // Get video URL
                                    final ParseFile videoFile = pObj.getParseFile(POSTS_VIDEO);
                                    assert videoFile != null;
                                    String videoURL = videoFile.getUrl();
                                    videoProgBar.setVisibility(View.VISIBLE);

                                    // Get screen size
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                    int W = displayMetrics.widthPixels;

                                    // Setup videoView's size to square
                                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
                                    lp.width = W;
                                    lp.height = W;
                                    lp.addRule(RelativeLayout.BELOW, dismVideoButt.getId());
                                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                    videoView.setLayoutParams(lp);
                                    Log.i("log-", "VIDEOVIEW WIDTH: " + videoView.getWidth());
                                    Log.i("log-", "VIDEOVIEW HEIGHT: " + videoView.getHeight());


                                    // Show Video Layout
                                    showVideoLayout();

                                    videoView.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
                                    videoView.setDataSource(videoURL);
                                    videoView.setLooping(true);
                                    videoView.setListener(new TextureVideoView.MediaPlayerListener() {
                                        @Override
                                        public void onVideoPrepared() {
                                            videoProgBar.setVisibility(View.INVISIBLE);
                                            videoView.play();
                                        }

                                        @Override
                                        public void onVideoEnd() { }
                                    });

                                    //-----------------------------------------------
                                    // MARK - DISMISS VIDEO BUTTON
                                    //-----------------------------------------------
                                    dismVideoButt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            videoView.stop();
                                            videoView.invalidate();
                                            hideVideoLayout();
                                        }});
                                }});



                            // error on userPointer
                        } else { simpleAlert(e.getMessage(), ctx);
                        }}});// end userPointer


                return cell;
            }

            @Override public int getCount() { return postArray.size(); }
            @Override public Object getItem(int position) { return postArray.get(position); }
            @Override public long getItemId(int position) { return position; }
        }

        // Set adapter
        postListView.setAdapter(new ListAdapter(ctx));
    }



    //-----------------------------------------------
    // MARK - SHOW/HIDE VIDEO LAYOUT
    //-----------------------------------------------
    void showVideoLayout() {
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(videoContainerLayout.getLayoutParams());
        marginParams.setMargins(0, 0, 0, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
        videoContainerLayout.setLayoutParams(layoutParams);
    }
    void hideVideoLayout() {
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(videoContainerLayout.getLayoutParams());
        marginParams.setMargins(0, 10000, 0, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
        videoContainerLayout.setLayoutParams(layoutParams);
    }


}// ./ end
