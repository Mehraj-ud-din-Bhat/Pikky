package com.xscoder.pikky.Userprofile;

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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xscoder.pikky.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;


import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BIO;
import static com.xscoder.pikky.Configurations.USER_EMAIL;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.USER_USERNAME_LOWERCASE;
import static com.xscoder.pikky.Configurations.USER_WEBSITE;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.saveParseImage;
import static com.xscoder.pikky.Configurations.scaleBitmapToMaxSize;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    /*--- VIEWS ---*/
    Button updateButt;
    CircleImageView avatarImg;
    EditText fullnameTxt, usernameTxt, websiteTxt, bioTxt, emailTxt;
    TextView charactersCountTxt;


    /*--- VARIABLES ---*/
    Context ctx = this;




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        updateButt = findViewById(R.id.epUpdateButt);
        avatarImg = findViewById(R.id.epAvatarimg);
        fullnameTxt = findViewById(R.id.epFullnameTxt);
        fullnameTxt.setTypeface(osRegular);
        usernameTxt = findViewById(R.id.epUsernameTxt);
        usernameTxt.setTypeface(osRegular);
        websiteTxt = findViewById(R.id.epWebsiteTxt);
        websiteTxt.setTypeface(osRegular);
        bioTxt = findViewById(R.id.epBioTxt);
        bioTxt.setTypeface(osRegular);
        charactersCountTxt = findViewById(R.id.epCharactersCountTxt);
        emailTxt = findViewById(R.id.epEmailTxt);
        emailTxt.setTypeface(osRegular);



        // Show currentUser's details
        showCurrentUserDetails();



        //-----------------------------------------------
        // MARK - COUNT CHARACTERS FOR THE BIO TXT
        //-----------------------------------------------
        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int counter = 80-s.length();
                charactersCountTxt.setText(String.valueOf(counter));

                if (bioTxt.getText().toString().contains("\n")) { dismissKeyboard(); }
            }
            public void afterTextChanged(Editable s) { }
        };
        bioTxt.addTextChangedListener(textWatcher);





        //-----------------------------------------------
        // MARK - UPLOAD AVATAR IMAGE BUTTON
        //-----------------------------------------------
        avatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert  = new AlertDialog.Builder(ctx);
                alert.setTitle("SELECT SOURCE")
                        .setIcon(R.drawable.logo)
                        .setItems(new CharSequence[] {
                                        "Take a photo",
                                        "Pick an image from Gallery"
                        }, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: openCamera();   break;
                                    case 1: openGallery();  break;
                        }}})
                        .setNegativeButton("Cancel", null);
                alert.create().show();
        }});




        //-----------------------------------------------
        // MARK - UPDATE PROFILE BUTTON
        //-----------------------------------------------
        updateButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              ParseUser currentUser = ParseUser.getCurrentUser();

              if (!fullnameTxt.getText().toString().matches("") || !usernameTxt.getText().toString().matches("") || !emailTxt.getText().toString().matches("") ){
                  showHUD(ctx);
                  dismissKeyboard();

                  // Prepare data
                  currentUser.put(USER_FULLNAME,  fullnameTxt.getText().toString());
                  currentUser.put(USER_USERNAME, usernameTxt.getText().toString());
                  currentUser.put(USER_USERNAME_LOWERCASE, usernameTxt.getText().toString().toLowerCase());
                  if (!websiteTxt.getText().toString().matches("") ){ currentUser.put(USER_WEBSITE, websiteTxt.getText().toString()); }
                  if (!bioTxt.getText().toString().matches("") ){ currentUser.put(USER_BIO, bioTxt.getText().toString()); }
                  currentUser.put(USER_EMAIL, emailTxt.getText().toString());

                  // Avatar
                  saveParseImage(avatarImg, currentUser, USER_AVATAR);

                  // Saving block
                  currentUser.saveInBackground(new SaveCallback() {
                      @Override
                      public void done(ParseException e) {
                          if (e == null) {
                              hideHUD();
                              simpleAlert("Your profile has been updated!", ctx);
                          } else {
                              hideHUD();
                              simpleAlert(e.getMessage(), ctx);
                  }}});


              // Username, Full name and email fields must not be empty!
              } else { simpleAlert("Full name, Username and Em,ail adress fields must not be empty!", ctx); }
        }});





        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.epBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }//* ./ onCreate






    // ------------------------------------------------
    // MARK: - SHOW CURRENT USER'S DETAILS
    // ------------------------------------------------
    void showCurrentUserDetails() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Avatar
        getParseImage(avatarImg, currentUser, USER_AVATAR);
        // Full name
        fullnameTxt.setText(currentUser.getString(USER_FULLNAME));
        // Username
        usernameTxt.setText(currentUser.getString(USER_USERNAME));
        // Website
        if (currentUser.getString(USER_WEBSITE) != null){ websiteTxt.setText(currentUser.getString(USER_WEBSITE));
        } else { websiteTxt.setText(""); }
        // Bio
        if (currentUser.getString(USER_BIO) != null){
            bioTxt.setText(currentUser.getString(USER_BIO));
            int leftChars = 80 - bioTxt.getText().toString().length();
            charactersCountTxt.setText(String.valueOf(leftChars));
        }
        // Email adress
        emailTxt.setText(currentUser.getString(USER_EMAIL));
    }






    //-----------------------------------------------
    // MARK - IMAGE PICKED DELEGATE
    //-----------------------------------------------
    int CAMERA = 0;
    int GALLERY = 1;
    Uri imageURI;
    File file;

    // OPEN CAMERA
    public void openCamera() {
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        imageURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, CAMERA);
    }

    // OPEN GALLERY
    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY);
    }

    // IMAGE PICKED DELEGATE -----------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bm = null;

            // Image from Camera
            if (requestCode == CAMERA) {

                try {
                    File f = file;
                    ExifInterface exif = new ExifInterface(f.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    int angle = 0;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) { angle = 90; }
                    else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) { angle = 180; }
                    else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) { angle = 270; }
                    Log.i("log-", "ORIENTATION: " + orientation);

                    Matrix mat = new Matrix();
                    mat.postRotate(angle);

                    Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
                    assert bmp != null;
                    bm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
                }
                catch (IOException | OutOfMemoryError e) { Log.i("log-", e.getMessage()); }


            // Image from Gallery
            } else if (requestCode == GALLERY) {
                try { bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                } catch (IOException e) { e.printStackTrace(); }
            }


            // Set image
            assert bm != null;
            Bitmap scaledBm = scaleBitmapToMaxSize(300, bm);
            avatarImg.setImageBitmap(scaledBm);
        }
    }


    //-----------------------------------------------
    // MARK - DIMSISS KEYBOARD
    //-----------------------------------------------
    void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(fullnameTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(usernameTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(websiteTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(bioTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(emailTxt.getWindowToken(), 0);
    }



}// ./ end
