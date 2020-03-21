package com.xscoder.pikky.comments;

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

import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xscoder.pikky.R;
import com.xscoder.pikky.Userprofile.AccountProfile.Account;
import com.xscoder.pikky.Userprofile.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.xscoder.pikky.Configurations.COMMENTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.COMMENTS_COMMENT;
import static com.xscoder.pikky.Configurations.COMMENTS_CREATED_AT;
import static com.xscoder.pikky.Configurations.COMMENTS_CURRENT_USER;
import static com.xscoder.pikky.Configurations.COMMENTS_LIKED_BY;
import static com.xscoder.pikky.Configurations.COMMENTS_LIKES;
import static com.xscoder.pikky.Configurations.COMMENTS_POST_POINTER;
import static com.xscoder.pikky.Configurations.COMMENTS_REPORTED_BY;
import static com.xscoder.pikky.Configurations.POSTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.POSTS_COMMENTS;
import static com.xscoder.pikky.Configurations.POSTS_TEXT;
import static com.xscoder.pikky.Configurations.POSTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_IS_VERIFIED;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.roundLargeNumber;
import static com.xscoder.pikky.Configurations.sendPushNotification;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;
import static com.xscoder.pikky.Configurations.timeAgoSinceDate;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comments extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener {

    /*--- VIEWS ---*/
    Button postCommentButt;
    ListView commentsListView;
    EditText commentTxt;
    CircleImageView avatarImg;
    SwipeRefreshLayout refreshControl;



    /*--- VARIABLES ---*/
    Context ctx = this;
    ParseObject pObj;
    List<ParseObject>commmentsArray = new ArrayList<>();
    int skip = 0;





    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        refreshControl = findViewById(R.id.refreshControl);
        refreshControl.setOnRefreshListener(this);
        postCommentButt = findViewById(R.id.commPostButt);
        postCommentButt.setTypeface(osBold);
        commentsListView = findViewById(R.id.commCommentsListView);
        commentTxt = findViewById(R.id.commCommentTxt);
        commentTxt.setTypeface(osRegular);
        avatarImg = findViewById(R.id.commmAvatarImg);


        // Your Avatar Image
        ParseUser currentUser = ParseUser.getCurrentUser();
        getParseImage(avatarImg, currentUser, USER_AVATAR);


        // Get extras
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String objectID = extras.getString("objectID");
        pObj = ParseObject.createWithoutData(POSTS_CLASS_NAME, objectID);
        try { pObj.fetchIfNeeded().getParseObject(POSTS_CLASS_NAME);

            // Query Comments
            queryComments();


            //-----------------------------------------------
            // MARK - EDIT TEXT WATCHER
            //-----------------------------------------------
            commentTxt.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    postCommentButt.setVisibility(View.VISIBLE);
                }
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                @Override public void afterTextChanged(Editable editable) { }
            });


            // DISMISS KEYBOARD ON DONE BUTTON
            commentTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        dismissKeyboard();
                        postCommentButt.setVisibility(View.INVISIBLE);

                        return true;
                    } return false;
            }});





            // ------------------------------------------------
            // MARK: - POST COMMENT BUTTON
            // ------------------------------------------------
            postCommentButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!commentTxt.getText().toString().matches("") ) {
                        showHUD(ctx);
                        ParseObject cObj = new ParseObject(COMMENTS_CLASS_NAME);
                        final ParseUser currentUser = ParseUser.getCurrentUser();
                        List<String>empty = new ArrayList<>();

                        // Prepare Data
                        cObj.put(COMMENTS_CURRENT_USER, currentUser);
                        cObj.put(COMMENTS_POST_POINTER, pObj);
                        cObj.put(COMMENTS_COMMENT, commentTxt.getText().toString());
                        cObj.put(COMMENTS_LIKES, 0);
                        cObj.put(COMMENTS_LIKED_BY, empty);
                        cObj.put(COMMENTS_REPORTED_BY, empty);

                        // Save data
                        cObj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    hideHUD();
                                    Toast.makeText(ctx, "Comment sent!", Toast.LENGTH_SHORT).show();

                                    // Increment the Post's comments
                                    pObj.increment(POSTS_COMMENTS, 1);
                                    pObj.saveInBackground();

                                    // Get userPointer
                                    Objects.requireNonNull(pObj.getParseObject(POSTS_USER_POINTER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                        public void done(ParseObject userPointer, ParseException e) {
                                            if (e == null) {
                                                hideHUD();
                                                dismissKeyboard();
                                                commentTxt.setText("");

                                                // Send Push Notification
                                                String pushMessage = "@" + currentUser.getString(USER_USERNAME) + " commented on your Post: '" + pObj.getString(POSTS_TEXT) + "'";
                                                sendPushNotification(pushMessage, (ParseUser) userPointer, ctx);

                                                // Recall query
                                                skip = 0;
                                                commmentsArray = new ArrayList<>();
                                                queryComments();

                                            // error
                                            } else { hideHUD(); simpleAlert(e.getMessage(), ctx);
                                    }}});// end userPointer

                                // error
                                } else {
                                    dismissKeyboard();
                                    hideHUD();
                                    simpleAlert(e.getMessage(), ctx);
                        }}});

                    // commenTxt text is empty
                    } else { simpleAlert("Type something!", ctx); }
                }});



        } catch (ParseException e) { e.printStackTrace(); }



        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.commBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }// ./ onCreate







    // ------------------------------------------------
    // MARK: - QUERY COMMENTS
    // ------------------------------------------------
    void queryComments() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String>currObjID = new ArrayList<>();
        currObjID.add(currentUser.getObjectId());
        showHUD(ctx);

        ParseQuery<ParseObject>query = ParseQuery.getQuery(COMMENTS_CLASS_NAME);
        query.whereEqualTo(COMMENTS_POST_POINTER, pObj);
        query.whereNotContainedIn(COMMENTS_REPORTED_BY, currObjID);
        query.orderByDescending(COMMENTS_CREATED_AT);
        query.setSkip(skip);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    hideHUD();

                    commmentsArray.addAll(objects);
                    if (objects.size() == 100) {
                        skip = skip + 100;
                        queryComments();
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
                     cell = inflater.inflate(R.layout.cell_comment, null);
                 }
                 final View finalCell = cell;


                 // Parse Object
                 final ParseObject cObj = commmentsArray.get(position);

                 // userPointer
                 Objects.requireNonNull(cObj.getParseObject(COMMENTS_CURRENT_USER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                     @SuppressLint("SetTextI18n")
                     public void done(final ParseObject userPointer, ParseException e) {
                         if (e == null) {

                             final ParseUser currentUser = ParseUser.getCurrentUser();

                             //-----------------------------------------------
                             // MARK - INITIALIZE VIEWS
                             //-----------------------------------------------
                             CircleImageView avatarImg = finalCell.findViewById(R.id.ccommAvatarImg);
                             ImageView verifiedBadge = finalCell.findViewById(R.id.ccommVerifiedBadge);
                             TextView usernameTxt = finalCell.findViewById(R.id.ccommUsernameTxt);
                             usernameTxt.setTypeface(osBold);
                             TextView commentTxt = finalCell.findViewById(R.id.ccommCommentTxt);
                             commentTxt.setTypeface(osRegular);
                             TextView dateTxt = finalCell.findViewById(R.id.ccommDateTxt);
                             dateTxt.setTypeface(osRegular);
                             final TextView likesTxt = finalCell.findViewById(R.id.ccommLikesTxt);
                             likesTxt.setTypeface(osRegular);
                             final Button likeButt = finalCell.findViewById(R.id.ccommLikeButt);
                             Button reportButt = finalCell.findViewById(R.id.ccommReportButt);



                             // Avatar
                             getParseImage(avatarImg, userPointer, USER_AVATAR);
                             // Verified Badge
                             boolean isVerified = userPointer.getBoolean(USER_IS_VERIFIED);
                             if (isVerified) { verifiedBadge.setVisibility(View.VISIBLE);
                             } else { verifiedBadge.setVisibility(View.INVISIBLE); }
                             // Username
                             usernameTxt.setText(userPointer.getString(USER_USERNAME));
                             // Date
                             dateTxt.setText(timeAgoSinceDate(cObj.getCreatedAt()));
                             // Comment text
                             commentTxt.setText(cObj.getString(COMMENTS_COMMENT));
                             // Likes
                             int likes = cObj.getInt(COMMENTS_LIKES);
                             likesTxt.setText(roundLargeNumber(likes) + " Likes");
                             // Liked By
                             final List<String>likedBy = cObj.getList(COMMENTS_LIKED_BY);
                             assert likedBy != null;
                             if (likedBy.contains(currentUser.getObjectId()) ){
                                 likeButt.setBackgroundResource(R.drawable.liked_butt);
                             } else {
                                 likeButt.setBackgroundResource(R.drawable.like_butt);
                             }


                             //-----------------------------------------------
                             // MARK - LIKE COMMENT BUTTON
                             //-----------------------------------------------
                             likeButt.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                    // UNLIKE
                                    if (likedBy.contains(currentUser.getObjectId()) ) {
                                         likedBy.remove(currentUser.getObjectId());
                                         cObj.put(COMMENTS_LIKED_BY, likedBy);
                                         cObj.increment(COMMENTS_LIKES, -1);
                                         cObj.saveInBackground();

                                         likeButt.setBackgroundResource(R.drawable.like_butt);
                                         likesTxt.setText(String.valueOf(cObj.getInt(COMMENTS_LIKES)) + " Likes");
                                    // LIKE
                                    } else {
                                         likedBy.add(currentUser.getObjectId());
                                         cObj.put(COMMENTS_LIKED_BY, likedBy);
                                         cObj.increment(COMMENTS_LIKES, 1);
                                         cObj.saveInBackground();

                                         likeButt.setBackgroundResource(R.drawable.liked_butt);
                                         likesTxt.setText(String.valueOf(cObj.getInt(COMMENTS_LIKES)) + " Likes");
                                    }
                             }});





                             //-----------------------------------------------
                             // MARK - REPORT COMMENT BUTTON
                             //-----------------------------------------------
                             reportButt.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                    final List<String>reportedBy = cObj.getList(COMMENTS_REPORTED_BY);
                                    assert reportedBy != null;

                                     // Fire alert
                                     AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                     alert.setMessage("Are you sure you want to report this Comment to the Admin?")
                                           .setTitle(R.string.app_name)
                                           .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialog, int which) {
                                                   reportedBy.add(currentUser.getObjectId());
                                                   cObj.put(COMMENTS_REPORTED_BY, reportedBy);
                                                   cObj.saveInBackground();

                                                   AlertDialog.Builder alert2 = new AlertDialog.Builder(ctx);
                                                   alert2.setMessage("Thanks for reporting this comment. We'll take action for it within 24h.")
                                                         .setTitle(R.string.app_name)
                                                         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {
                                                                 skip = 0;
                                                                 commmentsArray = new ArrayList<>();
                                                                 queryComments();
                                                         }})
                                                         .setCancelable(false)
                                                         .setIcon(R.drawable.logo)
                                                         .create().show();
                                           }})
                                           .setNegativeButton("Cancel", null)
                                           .setCancelable(false)
                                           .setIcon(R.drawable.logo)
                                           .create().show();
                             }});




                             //-----------------------------------------------
                             // MARK - AVATAR BUTTON
                             //-----------------------------------------------
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


                         // error
                         } else { simpleAlert(e.getMessage(), ctx);
                 }}});// end userPointer

             return cell;
             }

             @Override public int getCount() { return commmentsArray.size(); }
             @Override public Object getItem(int position) { return commmentsArray.get(position); }
             @Override public long getItemId(int position) { return position; }
         }


         // Set adapter
         commentsListView.setAdapter(new ListAdapter(ctx));
    }




    //-----------------------------------------------
    // MARK - DIMSISS KEYBOARD
    //-----------------------------------------------
    void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(commentTxt.getWindowToken(), 0);
        postCommentButt.setVisibility(View.INVISIBLE);
    }






    //-----------------------------------------------
    // MARK - REFRESH DATA
    //-----------------------------------------------
    @Override
    public void onRefresh() {
        skip = 0;
        commmentsArray = new ArrayList<>();

        queryComments();
        if (refreshControl.isRefreshing()) { refreshControl.setRefreshing(false); }
    }



}// ./ end
