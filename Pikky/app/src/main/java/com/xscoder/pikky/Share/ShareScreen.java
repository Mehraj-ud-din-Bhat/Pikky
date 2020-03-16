package com.xscoder.pikky.Share;

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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xscoder.pikky.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.xscoder.pikky.Configurations.FOLLOW_CLASS_NAME;
import static com.xscoder.pikky.Configurations.FOLLOW_CURRENT_USER;
import static com.xscoder.pikky.Configurations.FOLLOW_IS_FOLLOWING;
import static com.xscoder.pikky.Configurations.POSTS_BOOKMARKED_BY;
import static com.xscoder.pikky.Configurations.POSTS_CAN_COMMENT;
import static com.xscoder.pikky.Configurations.POSTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.POSTS_COMMENTS;
import static com.xscoder.pikky.Configurations.POSTS_FOLLOWED_BY;
import static com.xscoder.pikky.Configurations.POSTS_IMAGE;
import static com.xscoder.pikky.Configurations.POSTS_KEYWORDS;
import static com.xscoder.pikky.Configurations.POSTS_LIKED_BY;
import static com.xscoder.pikky.Configurations.POSTS_LIKES;
import static com.xscoder.pikky.Configurations.POSTS_LOCATION;
import static com.xscoder.pikky.Configurations.POSTS_REPORTED_BY;
import static com.xscoder.pikky.Configurations.POSTS_TAGS;
import static com.xscoder.pikky.Configurations.POSTS_TEXT;
import static com.xscoder.pikky.Configurations.POSTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.POSTS_VIDEO;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.convertVideoToBytes;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.saveParseImage;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;
import static com.xscoder.pikky.Configurations.mustDismiss;
import static com.xscoder.pikky.Configurations.videoToShareURL;

public class ShareScreen extends AppCompatActivity implements LocationListener {

    /*--- VIEWS ---*/
    ImageView imageToShare;
    Button shareButt, currentLocationButt, removeLocationButt;
    EditText postTextTxt, tagsTxt;
    TextView charactersCountTxt;



    /*--- VARIABLES ---*/
    Context ctx = this;
    String videoURL;
    List<String> tags = new ArrayList<>();
    ParseObject postObj;
    Location currentLocation;
    LocationManager locationManager;






    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_screen);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        imageToShare = findViewById(R.id.ssImageToShare);
        shareButt = findViewById(R.id.ssShareButt);
        shareButt.setTypeface(osSemibold);
        charactersCountTxt = findViewById(R.id.ssCharactersCountTxt);
        charactersCountTxt.setTypeface(osRegular);
        currentLocationButt = findViewById(R.id.ssCurrentLocationButton);
        currentLocationButt.setTypeface(osSemibold);
        removeLocationButt = findViewById(R.id.ssRemoveCurrentLocationButt);
        removeLocationButt.setVisibility(View.INVISIBLE);
        postTextTxt = findViewById(R.id.ssPostTextTxt);
        postTextTxt.setTypeface(osRegular);
        tagsTxt = findViewById(R.id.ssTagsTxt);
        tagsTxt.setTypeface(osSemibold);




        //-----------------------------------------------
        // MARK - GET EXTRAS
        //-----------------------------------------------
        Bundle extras = getIntent().getExtras();
        assert extras != null;

        // Get image
        byte[] byteArray = getIntent().getByteArrayExtra("imageToShare");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageToShare.setImageBitmap(bmp);


        // Get Video URL
        videoURL = extras.getString("videoURL");
        Log.i(TAG, "VIDEO TO SHARE: " + videoURL);

        // Get postObj
        String objectID = extras.getString("objectID");
        if (objectID != null) {
            postObj = ParseObject.createWithoutData(POSTS_CLASS_NAME, objectID);
            try { postObj.fetchIfNeeded().getParseObject(POSTS_CLASS_NAME);

                // In case you're editing a Post
                showPostData();

                Log.i(TAG, "EDITING A POST: " + postObj.getObjectId());


            } catch (ParseException e) { e.printStackTrace(); }
        }






        //-----------------------------------------------
        // MARK - COUNT CHARACTERS IN POST TXT
        //-----------------------------------------------
        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int counter = 300-s.length();
                charactersCountTxt.setText(String.valueOf(counter));

                if (postTextTxt.getText().toString().contains("\n")) { dismissKeyboard(); }
            }
            public void afterTextChanged(Editable s) { }
        };
        postTextTxt.addTextChangedListener(textWatcher);



        //-----------------------------------------------
        // MARK - DISMISS KEYBOARD ON RETURN
        //-----------------------------------------------
        postTextTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId== EditorInfo.IME_ACTION_DONE)) {
                    dismissKeyboard();
                    return true;
                }
                return false;
        }});






        //-----------------------------------------------
        // MARK - CURRENT LOCATION BUTTON
        //-----------------------------------------------
        currentLocationButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              // Check Location permission
              if (checkLocationPermission()) {
                  getCurrentLocation();
              // Ask for Location permission
              } else { ActivityCompat.requestPermissions(ShareScreen.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, 1); }
        }});


        //-----------------------------------------------
        // MARK - REMOVE LOCATION BUTTON
        //-----------------------------------------------
        removeLocationButt.setOnClickListener(new View.OnClickListener() {
          @SuppressLint("SetTextI18n")
          @Override
          public void onClick(View view) {
              removeLocationButt.setVisibility(View.INVISIBLE);
              currentLocationButt.setText("Add your current location");
              currentLocation = null;
        }});




        //-----------------------------------------------
        // MARK - SHARE BUTTON
        //-----------------------------------------------
        shareButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if (!postTextTxt.getText().toString().matches("") ){

                  showHUD(ctx);
                  final ParseUser currentUser = ParseUser.getCurrentUser();

                  // Parse Object
                  ParseObject pObj = new ParseObject(POSTS_CLASS_NAME);

                  // You are editing a Post:
                  if (postObj != null) { pObj = postObj; }
                  final ParseObject finalPObj = pObj;

                  dismissKeyboard();

                  // Query currentUser's Followers
                  ParseQuery<ParseObject> query = ParseQuery.getQuery(FOLLOW_CLASS_NAME);
                  query.whereEqualTo(FOLLOW_IS_FOLLOWING, currentUser);
                  query.findInBackground(new FindCallback<ParseObject>() {
                      public void done(final List<ParseObject> objects, ParseException e) {
                          if (e == null) {

                              // Add currentUser's objectId to followedBy
                              final List<String> followedBy = new ArrayList<>();
                              followedBy.add(currentUser.getObjectId());
                              Log.i(TAG, "FOLLOWERS: " +  objects.size());

                              // Save Data WITH Followers
                              if (objects.size() != 0) {
                                  for (int i = 0; i<objects.size(); i++) {

                                      // Parse Object
                                      ParseObject fObj = objects.get(i);

                                      // Get userPointer
                                      final int finalI = i;
                                      Objects.requireNonNull(fObj.getParseObject(FOLLOW_CURRENT_USER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                          public void done(ParseObject userPointer, ParseException e) {
                                              if (e == null) {

                                              followedBy.add(userPointer.getObjectId());

                                              // For loop ends -> Prepare Data to be saved
                                              if (finalI == objects.size()-1) {
                                                  finalPObj.put(POSTS_FOLLOWED_BY, followedBy);
                                                  sharePost(finalPObj);
                                              }

                                          // error
                                          } else {
                                              hideHUD();
                                              simpleAlert(e.getMessage(), ctx);
                                      }}});// ./ userPointer


                                  }// ./ For loop


                              // Save Data wiht NO followers
                              } else {
                                  finalPObj.put(POSTS_FOLLOWED_BY, followedBy);
                                  sharePost(finalPObj);
                              }


                              // error in query
                          } else {
                              hideHUD();
                              simpleAlert(e.getMessage(), ctx);
                  }}}); // ./ Query currentUser's Followers


                  // Post Text is empty
              } else { simpleAlert("You must type something as a caption!", ctx); }

        }});





        //-----------------------------------------------
        // MARK - DISMISS BUTTON
        //-----------------------------------------------
        Button dismissButt = findViewById(R.id.ssBackButt);
        dismissButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              dismissKeyboard();

              AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
              alert.setMessage("Are you sure you want to Exit?")
                  .setTitle(R.string.app_name)
                  .setPositiveButton("Discard File", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          // Delete video file - if any.
                          if (videoURL != null) {
                              File file = new File(videoURL);
                              file.delete();
                          }

                          mustDismiss = true;
                          finish();
                  }})
                  .setNegativeButton("Cancel", null)
                  .setCancelable(false)
                  .setIcon(R.drawable.logo);
              alert.create().show();
          }});



    }// ./ onCreate






    //-----------------------------------------------
    // MARK - SHOW POST'S DATA
    //-----------------------------------------------
    void showPostData() {
        getParseImage(imageToShare, postObj, POSTS_IMAGE);

        postTextTxt.setText(postObj.getString(POSTS_TEXT));

        List<String>tags = postObj.getList(POSTS_TAGS);
        StringBuilder tagsString = new StringBuilder();
        assert tags != null;
        for (String s : tags) { tagsString.append(s).append(" "); }
        tagsTxt.setText(tagsString.toString());

        if (postObj.getString(POSTS_LOCATION) != null){
            currentLocationButt.setText(postObj.getString(POSTS_LOCATION));
        }
    }






    //-----------------------------------------------
    // MARK - CHECK LOCATION PERMISSION
    //-----------------------------------------------
    private boolean checkLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                alert.setMessage("If you want to enable Location permission, enter Settings, search for " + getString(R.string.app_name) + " and enable Location.")
                      .setTitle(R.string.app_name)
                      .setPositiveButton("OK", null)
                      .setIcon(R.drawable.logo);
                alert.create().show();
            }
        }
    }



    //-----------------------------------------------
    // MARK - GET CURRENT LOCATION
    //-----------------------------------------------
    protected void getCurrentLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(provider);

        if (currentLocation != null) {

            getCityAndCountry();

        // Try getting Current Location one more time
        } else { locationManager.requestLocationUpdates(provider, 1000, 0, this); }
    }

    @Override
    public void onLocationChanged(Location location) {
        //remove location callback:
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);

        currentLocation = location;

        if (currentLocation != null) {

            getCityAndCountry();


        // NO CURRENT LOCATION FOUND...
        } else { simpleAlert("Failed to get your Location.\nEnter Settings and enable Location.", ctx); }
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override public void onProviderEnabled(String provider) {}
    @Override public void onProviderDisabled(String provider) {}


    //-----------------------------------------------
    // MARK - GET CITY AND COUNTRY FROM CURRENT LOCATION
    //-----------------------------------------------
    @SuppressLint("SetTextI18n")
    void getCityAndCountry() {
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            double lat = currentLocation.getLatitude();
            double lon = currentLocation.getLongitude();
            List<Address> addresses;
            addresses = geocoder.getFromLocation(lat, lon, 1);
            if (Geocoder.isPresent()) {
                Address returnAddress = addresses.get(0);
                String city = returnAddress.getLocality();
                String country = returnAddress.getCountryName();

                // Show City and Country in button
                currentLocationButt.setText(city + ", " + country );

                // Show Remove location button
                removeLocationButt.setVisibility(View.VISIBLE);

            } else { Toast.makeText(getApplicationContext(), "Geocoder not present!", Toast.LENGTH_SHORT).show(); }
        } catch (IOException e) { Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show(); }
    }







    //-----------------------------------------------
    // MARK - SHARE POST FUNCTION
    //-----------------------------------------------
    void sharePost(final ParseObject pObj) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String>empty = new ArrayList<>();

        // Prepare data
        pObj.put(POSTS_USER_POINTER, currentUser);
        if (pObj.getObjectId() == null) {
            pObj.put(POSTS_LIKES, 0);
            pObj.put(POSTS_COMMENTS, 0);
            pObj.put(POSTS_BOOKMARKED_BY, empty);
            pObj.put(POSTS_LIKED_BY, empty);
            pObj.put(POSTS_CAN_COMMENT, true);
            pObj.put(POSTS_REPORTED_BY, empty);
        }
        pObj.put(POSTS_TEXT, postTextTxt.getText().toString());

        // Current Location
        if (currentLocation != null) { pObj.put(POSTS_LOCATION, currentLocationButt.getText().toString()); }

        // Tags
        if (!tagsTxt.getText().toString().matches("") ) {
            String tagsString = tagsTxt.getText().toString().replace(",", "");
            tags = new ArrayList<>();
            String[] one = tagsString.toLowerCase().split(" ");
            tags.addAll(Arrays.asList(one));
            Log.i(TAG, "TAGS: "  +tags);
            pObj.put(POSTS_TAGS, tags);
        } else { pObj.put(POSTS_TAGS, empty); }

        // Keywords
        List<String> keywords = new ArrayList<>();
        String[] one = postTextTxt.getText().toString().toLowerCase().split(" ");
        String[] two = Objects.requireNonNull(currentUser.getString(USER_USERNAME)).toLowerCase().split(" ");
        keywords.addAll(Arrays.asList(one));
        keywords.addAll(Arrays.asList(two));
        keywords.addAll(tags);
        pObj.put(POSTS_KEYWORDS, keywords);


        // Save Data in background
        pObj.saveInBackground(new SaveCallback() {
            @Override public void done(ParseException e) {
                if (e == null) {

                    mustDismiss = true;

                    // Save image
                    saveParseImage(imageToShare, pObj, POSTS_IMAGE);
                    pObj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i(TAG, "VIDEO URL: " + videoURL);

                                // Save Video - if it exists
                                if (videoURL != null) {
                                    ParseFile videoFile = new ParseFile ("video.mp4", convertVideoToBytes(videoURL));
                                    pObj.put(POSTS_VIDEO, videoFile);
                                    pObj.saveInBackground(new SaveCallback() {
                                        @Override public void done(ParseException e) {
                                            if (e == null) {
                                                hideHUD();

                                                // Delete the temp video file from the Pictures directory
                                                File file = new File(videoURL);
                                                file.delete();

                                                videoToShareURL = "";
                                                finish();
                                            }}});
                                } else {
                                    hideHUD();
                                    finish();
                                }

                            // error
                            } else {
                                hideHUD();
                                simpleAlert(e.getMessage(), ctx);
                    }}});

                // error
                } else {
                    hideHUD();
                    simpleAlert(e.getMessage(), ctx);
        }}});
    }






    //-----------------------------------------------
    // MARK - DISMISS KEYBOARD
    //-----------------------------------------------
    void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(postTextTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tagsTxt.getWindowToken(), 0);
    }



}// ./ end
