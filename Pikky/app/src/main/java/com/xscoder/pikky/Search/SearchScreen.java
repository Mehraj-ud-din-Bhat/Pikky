package com.xscoder.pikky.Search;

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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xscoder.pikky.Configurations;
import com.xscoder.pikky.Home.Home;
import com.xscoder.pikky.Massiging.Notifications;
import com.xscoder.pikky.R;
import com.xscoder.pikky.Camera.SquareCamera;
import com.xscoder.pikky.Userprofile.Account;
import com.xscoder.pikky.posts.PostDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.xscoder.pikky.Configurations.POSTS_BOOKMARKED_BY;
import static com.xscoder.pikky.Configurations.POSTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.POSTS_CREATED_AT;
import static com.xscoder.pikky.Configurations.POSTS_IMAGE;
import static com.xscoder.pikky.Configurations.POSTS_KEYWORDS;
import static com.xscoder.pikky.Configurations.POSTS_LIKED_BY;
import static com.xscoder.pikky.Configurations.POSTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.POSTS_VIDEO;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.USER_BLOCKED_BY;
import static com.xscoder.pikky.Configurations.USER_MUTED_BY;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.showAdMobInterstitial;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class SearchScreen extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /*--- VIEWS ---*/
    GridView searchGridView;
    EditText searchTxt;
    Button cancelSearchButton, backButt;
    SwipeRefreshLayout refreshControl;
    TextView tagTxt;
    List<Button>tagButtons = new ArrayList<>();



    /*--- VARIABLES ---*/
    Context ctx = this;
    List<ParseObject>postsArray = new ArrayList<>();
    String searchText;
    boolean isTag = false;
    int skip = 0;

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
        setContentView(R.layout.search_screen);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        searchGridView = findViewById(R.id.ssSearchGridView);
        searchTxt = findViewById(R.id.ssSearchTxt);
        searchTxt.setTypeface(osRegular);
        cancelSearchButton = findViewById(R.id.ssCancelSearchButt);
        cancelSearchButton.setTypeface(osSemibold);
        backButt = findViewById(R.id.ssBackButt);
        tagTxt= findViewById(R.id.ssTagTxt);

        // Tag Buttons array
        tagButtons.add((Button) findViewById(R.id.ssTagButt1));
        tagButtons.add((Button) findViewById(R.id.ssTagButt2));
        tagButtons.add((Button) findViewById(R.id.ssTagButt3));
        tagButtons.add((Button) findViewById(R.id.ssTagButt4));
        tagButtons.add((Button) findViewById(R.id.ssTagButt5));
        tagButtons.add((Button) findViewById(R.id.ssTagButt6));
        tagButtons.add((Button) findViewById(R.id.ssTagButt7));
        tagButtons.add((Button) findViewById(R.id.ssTagButt8));

        // Refresh control
        refreshControl = findViewById(R.id.refreshControl);
        refreshControl.setOnRefreshListener(this);



        //-----------------------------------------------
        // MARK - TAB BAR BUTTONS
        //-----------------------------------------------
        Button tab1 = findViewById(R.id.tab_one);
        Button tab2 = findViewById(R.id.tab_three);
        Button tab3 = findViewById(R.id.tab_four);
        Button tab4 = findViewById(R.id.tab_five);

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { startActivity(new Intent(ctx, Home.class)); }});

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { if (checkPermissions()) { startActivity(new Intent(ctx, SquareCamera.class)); } }});

        tab3.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { startActivity(new Intent(ctx, Notifications.class)); }});

        tab4.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { startActivity(new Intent(ctx, Account.class)); }});



        // Get data from previous Activity
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        searchText = extras.getString("searchText");
        isTag = extras.getBoolean("isTag");
        if (searchText == null) { searchText = ""; }

        // Show Back button and Tag label in case this screen gets shown from a tag search from another screen
        if (isTag) {
            backButt.setVisibility(View.VISIBLE);
            searchTxt.setVisibility(View.INVISIBLE);
            cancelSearchButton.setVisibility(View.INVISIBLE);
            tagTxt.setVisibility(View.VISIBLE);
            tagTxt.setText(searchText);
        }


        // Call query
        postsArray = new ArrayList<>();
        skip = 0;
        queryPosts();



        //-----------------------------------------------
        // MARK - TEXT CHANGED LISTENER
        //-----------------------------------------------
        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                cancelSearchButton.setVisibility(View.VISIBLE);
            }

            @Override public void afterTextChanged(Editable editable) { }
        });


        //-----------------------------------------------
        // MARK - SEARCH BY KEYWORDS
        //-----------------------------------------------
        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    searchText = searchTxt.getText().toString().toLowerCase();
                    dismissKeyboard();

                    // Call query
                    postsArray = new ArrayList<>();
                    skip = 0;
                    queryPosts();

                    return true;
                } return false;
        }});




        //-----------------------------------------------
        // MARK - CANCEL SEARCH BUTTON
        //-----------------------------------------------
        cancelSearchButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              cancelSearchButton.setVisibility(View.INVISIBLE);
              searchTxt.setText("");
              searchText = "";
              dismissKeyboard();
        }});




        //-----------------------------------------------
        // MARK - TAG BUTTONS
        //-----------------------------------------------
        for (final Button butt:tagButtons) {
            butt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tagStr = butt.getText().toString();
                    searchText = tagStr;
                    searchTxt.setText(tagStr);
                    tagTxt.setText(tagStr);

                    // Call query
                    postsArray = new ArrayList<>();
                    skip = 0;
                    queryPosts();
            }});
        }



        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});



        //-----------------------------------------------
        // MARK - CALL ADMOB ADS
        //-----------------------------------------------
        showAdMobInterstitial(ctx);


    }// ./ onCreate





    // ------------------------------------------------
    // MARK: - QUERY POSTS
    // ------------------------------------------------
    @SuppressLint("SetTextI18n")
    void queryPosts() {
        if (postsArray.size() < 100) { showHUD(ctx); }

        // Show Back button and Tag label in case this screen gets shown from a tag search from another screen
        if (isTag) {
            backButt.setVisibility(View.VISIBLE);
            searchTxt.setVisibility(View.INVISIBLE);
            cancelSearchButton.setVisibility(View.INVISIBLE);
            tagTxt.setVisibility(View.VISIBLE);
            tagTxt.setText(searchText);
        }

        final ParseUser currentUser = ParseUser.getCurrentUser();
        List<String>currObjID = new ArrayList<>();
        currObjID.add(currentUser.getObjectId());
        Log.i(TAG, "SEARCH TEXT: " + searchText);


        // Launch query
        ParseQuery<ParseObject>query = ParseQuery.getQuery(POSTS_CLASS_NAME);

        // Search for Posts by keyboards or tags
        if (!searchText.matches("") &&
                !searchText.matches("_LIKED_POSTS_") &&
                !searchText.matches("_BOOKMARKED_POSTS_") ){
            String[] one = searchText.toLowerCase().split(" ");
            List<String> keywords = new ArrayList<>(Arrays.asList(one));

            query.whereContainedIn(POSTS_KEYWORDS, keywords);
            Log.i(Configurations.TAG, "KEYWORDS: " + keywords);
        }

        // Search for Posts liked by currentUser
        if (searchText.matches("_LIKED_POSTS_") ){
            tagTxt.setText("Liked Posts");

            query.whereContainedIn(POSTS_LIKED_BY, currObjID);
        }

        // Search for Posts bookmarked by currentUser
        if (searchText.matches("_BOOKMARKED_POSTS_") ){
            tagTxt.setText("Bookmarked Posts");
            query.whereContainedIn(POSTS_BOOKMARKED_BY, currObjID);
        }

        query.whereNotContainedIn(USER_BLOCKED_BY, currObjID);

        query.orderByDescending(POSTS_CREATED_AT);
        query.setSkip(skip);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    postsArray.addAll(objects);
                    if (objects.size() == 100) {
                        skip = skip + 100;
                        queryPosts();

                    // Reload Data
                    } else {
                        hideHUD();
                        showDataInGridView();
                    }

                // error
                } else { hideHUD(); simpleAlert(e.getMessage(), ctx);
        }}});
    }





    // ------------------------------------------------
    // MARK: - SHOW DATA IN GRIDVIEW
    // ------------------------------------------------
    void showDataInGridView() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i(TAG, "POST ARRAY SIZE: " + postsArray.size());

        class GridAdapter extends BaseAdapter {
            private Context context;
            private GridAdapter(Context context) {
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
                    cell = inflater.inflate(R.layout.cell_post_search, null);
                }


                //-----------------------------------------------
                // MARK - INITIALIZE VIEWS
                //-----------------------------------------------
                RelativeLayout cellLayout = cell.findViewById(R.id.cpsCellLayout);
                final ImageView postImg = cell.findViewById(R.id.cpsPostImg);
                final ImageView videoicon = cell.findViewById(R.id.cpsVideoIcon);

                // Set cell size
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                cellLayout.getLayoutParams().height = width / 3; // number os cells

                // Parse Obj
                final ParseObject pObj = postsArray.get(position);

                // userPointer
                Objects.requireNonNull(pObj.getParseObject(POSTS_USER_POINTER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject userPointer, ParseException e) {
                        if (e == null) {

                            // Check for Blocked Users or Muted Posts
                            List<String> blockedBy = userPointer.getList(USER_BLOCKED_BY);
                            List<String> mutedBy = userPointer.getList(USER_MUTED_BY);
                            assert blockedBy != null;
                            assert mutedBy != null;
                            if (blockedBy.contains(currentUser.getObjectId()) || mutedBy.contains(currentUser.getObjectId()) ){
                                postsArray.remove(pObj);
                                showDataInGridView();
                            
                            } else {
                                // Post Image
                                getParseImage(postImg, pObj, POSTS_IMAGE);

                                // Check if Post is a Video
                                if (pObj.getParseFile(POSTS_VIDEO) != null) { videoicon.setVisibility(View.VISIBLE);
                                } else { videoicon.setVisibility(View.INVISIBLE); }
                            
                            }// ./ If
                            
                            
                        // error
                        } else { simpleAlert(e.getMessage(), ctx);
                }}});// ./ userPointer


            return cell;
            }
            @Override public int getCount() { return postsArray.size(); }
            @Override public Object getItem(int position) { return postsArray.get(position); }
            @Override public long getItemId(int position) { return position; }
        }

        // Set its adapter
        searchGridView.setAdapter(new GridAdapter(ctx));
        searchGridView.setNumColumns(3); // number of columns


        //-----------------------------------------------
        // MARK - TAP CELL
        //-----------------------------------------------
        searchGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
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
        if (!isTag) {
            searchText = "";
            searchTxt.setText("");
            tagTxt.setText("");
            cancelSearchButton.setVisibility(View.INVISIBLE);
            dismissKeyboard();
        }

        // Call query
        postsArray = new ArrayList<>();
        skip = 0;
        queryPosts();

        if (refreshControl.isRefreshing()) { refreshControl.setRefreshing(false); }
    }




    //-----------------------------------------------
    // MARK - DISMISS KEYBOARD
    //-----------------------------------------------
    void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(searchTxt.getWindowToken(), 0);
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
