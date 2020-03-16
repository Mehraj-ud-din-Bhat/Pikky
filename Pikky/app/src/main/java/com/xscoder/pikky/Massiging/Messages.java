package com.xscoder.pikky.Massiging;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.DeleteCallback;
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
import com.xscoder.pikky.R;
import com.xscoder.pikky.Userprofile.UserProfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.xscoder.pikky.Configurations.INSTANTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.INSTANTS_ID;
import static com.xscoder.pikky.Configurations.INSTANTS_RECEIVER;
import static com.xscoder.pikky.Configurations.INSTANTS_SENDER;
import static com.xscoder.pikky.Configurations.MESSAGES_CLASS_NAME;
import static com.xscoder.pikky.Configurations.MESSAGES_CREATED_AT;
import static com.xscoder.pikky.Configurations.MESSAGES_IMAGE;
import static com.xscoder.pikky.Configurations.MESSAGES_MESSAGE;
import static com.xscoder.pikky.Configurations.MESSAGES_MESSAGE_ID;
import static com.xscoder.pikky.Configurations.MESSAGES_RECEIVER;
import static com.xscoder.pikky.Configurations.MESSAGES_SENDER;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BLOCKED_BY;
import static com.xscoder.pikky.Configurations.USER_CLASS_NAME;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.allowPush;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.scaleBitmapToMaxSize;
import static com.xscoder.pikky.Configurations.showAdMobInterstitial;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;
import static com.xscoder.pikky.Configurations.timeAgoSinceDate;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messages extends AppCompatActivity {

    /*--- VIEWS ---*/
    TextView usernameTxt;
    CircleImageView avatarImg;
    Button backButt, optionsButt, sendMessageButt, sendPhotoButt, dismissPreviewButt;
    RelativeLayout imgPreviewLayout;
    ImageView imgPreview;
    ListView messagesListView;
    EditText messageTxt;



    /*--- VARIABLES ---*/
    Context ctx = this;
    ParseUser userObj;
    List<ParseObject>messagesArray = new ArrayList<>();
    List<ParseObject>instantsArray = new ArrayList<>();
    int skip = 0;
    String lastMessage = null;
    Timer refreshTimer = new Timer();
    Bitmap imageToSend = null;




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();



        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        backButt = findViewById(R.id.messBackButt);
        optionsButt = findViewById(R.id.messOptionsButt);
        usernameTxt = findViewById(R.id.messUsernameTxt);
        usernameTxt.setTypeface(osSemibold);
        avatarImg = findViewById(R.id.messAvatarImg);
        sendMessageButt = findViewById(R.id.messSendMessageButt);
        sendPhotoButt = findViewById(R.id.messSendPhotoButt);
        imgPreviewLayout = findViewById(R.id.messImgPreviewLayout);
        imgPreview = findViewById(R.id.messImgPreview);
        messagesListView = findViewById(R.id.messMessagesListView);
        messageTxt = findViewById(R.id.messMessageTxt);
        messageTxt.setTypeface(osRegular);
        dismissPreviewButt = findViewById(R.id.messDismissPreviewButt);


        // Hide Views
        imgPreviewLayout.setVisibility(View.GONE);
        sendMessageButt.setVisibility(View.INVISIBLE);


        // Get Parse Object's data
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String objectID = extras.getString("userID");
        userObj = (ParseUser) ParseObject.createWithoutData(USER_CLASS_NAME, objectID);
        try {
            userObj.fetchIfNeeded().getParseObject(USER_CLASS_NAME);

            // Username
            usernameTxt.setText(userObj.getString(USER_USERNAME));
            // Avatar
            getParseImage(avatarImg, userObj, USER_AVATAR);


            // Call query
            queryMessages();

            // Start refresh Timer to query Messages periodically
            startRefreshTimer();





            //-----------------------------------------------
            // MARK - RECEIVER AVATAR -> SHOW USER PROFILE
            //-----------------------------------------------
            avatarImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, UserProfile.class);
                    Bundle extras = new Bundle();
                    extras.putString("objectID", userObj.getObjectId());
                    i.putExtras(extras);
                    startActivity(i);
            }});



            //-----------------------------------------------
            // MARK - MESSAGE TXT WATCHER
            //-----------------------------------------------
            messageTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count != 0) {
                        sendMessageButt.setVisibility(View.VISIBLE);
                    } else {
                        sendMessageButt.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) { }});


            //-----------------------------------------------
            // MARK - SEND MESSAGE BUTTON
            //-----------------------------------------------
            sendMessageButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messageTxt.getText().toString().matches("")) {
                        simpleAlert("You must type something or send a picture!", ctx);
                    } else {
                        sendMessage();
            }}});



            //-----------------------------------------------
            // MARK - SEND A PHOTO BUTT
            //-----------------------------------------------
            sendPhotoButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alert  = new AlertDialog.Builder(ctx);
                    alert.setTitle("Select source")
                            .setIcon(R.drawable.logo)
                            .setItems(new CharSequence[] {
                                    "Take a photo",
                                    "Pick from Gallery"
                            }, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: openCamera(); break;
                                        case 1: openGallery(); break;
                            }}})
                            .setNegativeButton("Cancel", null);
                    alert.create().show();
            }});





            //-----------------------------------------------
            // MARK - OPTIONS BUTTON
            //-----------------------------------------------
            optionsButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Check blocked users array
                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    final List<String>blockedBy = userObj.getList(USER_BLOCKED_BY);

                    // Set blockUser title
                    final String blockTitle;
                    assert blockedBy != null;
                    if (blockedBy.contains(currentUser.getObjectId())){ blockTitle = "Unblock User";
                    } else { blockTitle = "Block User"; }


                    // Fire alert
                    AlertDialog.Builder alert  = new AlertDialog.Builder(ctx);
                    alert.setTitle("Select option")
                            .setIcon(R.drawable.logo)
                            .setItems(new CharSequence[] {
                                    blockTitle,
                                    "Delete Chat"
                            }, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {


                                        // BLOCK/UNBLOCK USER -----------------------------------------------------------
                                        case 0:
                                            // Block User
                                            if (!blockedBy.contains(currentUser.getObjectId()) ){
                                                blockedBy.add(currentUser.getObjectId());
                                                final HashMap<String, Object> params = new HashMap<>();
                                                params.put("userId", userObj.getObjectId());
                                                params.put("blockedBy", blockedBy);
                                                ParseCloud.callFunctionInBackground("blockUnblockUser", params, new FunctionCallback<Object>() {
                                                    @Override
                                                    public void done(Object object, ParseException e) {
                                                        if (e == null) {
                                                            simpleAlert("You have blocked @" + userObj.getString(USER_USERNAME), ctx);
                                                        // error
                                                        } else { simpleAlert(e.getMessage(), ctx); }
                                                }});// ./ ParseCloud


                                            // Unblock User
                                            } else {
                                                blockedBy.remove(currentUser.getObjectId());

                                                final HashMap<String, Object> params = new HashMap<>();
                                                params.put("userId", userObj.getObjectId());
                                                params.put("blockedBy", blockedBy);
                                                ParseCloud.callFunctionInBackground("blockUnblockUser", params, new FunctionCallback<Object>() {
                                                    @Override
                                                    public void done(Object object, ParseException e) {
                                                        if (e == null) {
                                                            simpleAlert("You have unblocked @" + userObj.getString(USER_USERNAME), ctx);
                                                        // error
                                                        } else { simpleAlert(e.getMessage(), ctx); }
                                                }});// ./ ParseCloud

                                            }// ./ If
                                            break;





                                        // DELETE CHAT ---------------------------------------------------------------
                                        case 1:

                                            // Fire alert
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                            alert.setMessage("Are you sure you want to delete this Chat? \n@" + userObj.getString(USER_FULLNAME) + " will no longer see these messages.")
                                                    .setTitle(R.string.app_name)
                                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            // Delete all Messages of this chat
                                                            for (int i=0; i<messagesArray.size(); i++){
                                                                ParseObject iObj = messagesArray.get(i);
                                                                iObj.deleteInBackground();
                                                            }

                                                            // Delete Instant Object in the Instants class
                                                            String messId1 = currentUser.getObjectId() + userObj.getObjectId();
                                                            String messId2 = userObj.getObjectId() + currentUser.getObjectId();
                                                            String[] ids = { messId1, messId2 };
                                                            ParseQuery<ParseObject>query = ParseQuery.getQuery(INSTANTS_CLASS_NAME);
                                                            query.whereContainedIn(INSTANTS_ID, Arrays.asList(ids));
                                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                                @Override
                                                                public void done(List<ParseObject> objects, ParseException e) {
                                                                    if (e == null) {
                                                                        instantsArray = objects;
                                                                        ParseObject iObj = instantsArray.get(0);
                                                                        iObj.deleteInBackground(new DeleteCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if (e == null) {
                                                                                    Log.i(TAG, "INSTANT OBJ DELETED!");
                                                                                    finish();
                                                                                // error
                                                                                } else {
                                                                                    simpleAlert(e.getMessage(), ctx);
                                                                        }}});// ./ deleteInBackground


                                                                    // error
                                                                    } else { simpleAlert(e.getMessage(), ctx);
                                                            }}});// ./ Query

                                                    }})
                                                    .setNegativeButton("Cancel", null)
                                                    .setCancelable(false)
                                                    .setIcon(R.drawable.logo);
                                            alert.create().show();
                                            break;
                                    }}})

                            .setNegativeButton("Cancel", null);
                    alert.create().show();

                }});



        } catch (ParseException e) { e.printStackTrace(); }




        //-----------------------------------------------
        // MARK - DISMISS IMAGE PREVIEW BUTTON
        //-----------------------------------------------
        dismissPreviewButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {  imgPreviewLayout.setVisibility(View.GONE);  }});



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







    //-----------------------------------------------
    // MARK - QUERY MESSAGES
    //-----------------------------------------------
    void queryMessages() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String messId1 = currentUser.getObjectId() + userObj.getObjectId();
        String messId2 = userObj.getObjectId() + currentUser.getObjectId();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(MESSAGES_CLASS_NAME);
        String[] ids = { messId1, messId2 };
        query.whereContainedIn(MESSAGES_MESSAGE_ID, Arrays.asList(ids));
        query.orderByAscending(MESSAGES_CREATED_AT);
        query.setSkip(skip);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    messagesArray.addAll(objects);
                    if (objects.size() == 100) {
                        skip = skip + 100;
                        queryMessages();
                    }

                    showDataInListView();

                    // Scroll TableView down to the bottom
                    if (messagesArray.size() != 0) {
                        // Scroll ListView to bottom after 1.5 sec
                        int delay = 1500;
                        new Timer().scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollListViewToBottom();
                                    }}); }}, delay, delay);
                    }

                // error
                } else { simpleAlert(e.getMessage(), ctx);
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
            @SuppressLint("InflateParams")
            @Override
            public View getView(int position, View cell, ViewGroup parent) {
                if (cell == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    cell = inflater.inflate(R.layout.cell_message, null);
                }
                final View finalCell = cell;


                //-----------------------------------------------
                // MARK - INITIALIZE VIEWS
                //-----------------------------------------------
                final RelativeLayout senderCell = finalCell.findViewById(R.id.senderCell);
                final RelativeLayout receiverCell = finalCell.findViewById(R.id.receiverCell);


                // Get Parse object
                final ParseObject mObj = messagesArray.get(position);
                final ParseUser currentUser = ParseUser.getCurrentUser();

                // userPointer
                Objects.requireNonNull(mObj.getParseObject(MESSAGES_SENDER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject userPointer, ParseException e) {

                        //-----------------------------------------------
                        // MARK - SENDER MESSAGE CELL
                        //-----------------------------------------------
                        if (userPointer.getObjectId().matches(currentUser.getObjectId())) {
                            senderCell.setVisibility(View.VISIBLE);
                            receiverCell.setVisibility(View.INVISIBLE);

                            // Message
                            TextView messTxt = finalCell.findViewById(R.id.sMessTxt);
                            messTxt.setTypeface(osRegular);
                            messTxt.setText(mObj.getString(MESSAGES_MESSAGE));

                            // Date
                            TextView dateTxt = finalCell.findViewById(R.id.sDateTxt);
                            dateTxt.setTypeface(osRegular);
                            dateTxt.setText(timeAgoSinceDate(mObj.getCreatedAt()));

                            // Image
                            ImageView messImage = finalCell.findViewById(R.id.sImage);
                            messImage.setVisibility(View.GONE);

                            if (mObj.get(MESSAGES_IMAGE) != null) {
                                messImage.setClipToOutline(true);
                                messImage.setVisibility(View.VISIBLE);
                                messTxt.setVisibility(View.INVISIBLE);

                                getParseImage(messImage, mObj, MESSAGES_IMAGE);

                                messImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        imgPreviewLayout.setVisibility(View.VISIBLE);
                                        getParseImage(imgPreview, mObj, MESSAGES_IMAGE);
                                }});

                            // Hide messImage
                            } else { messImage.setVisibility(View.GONE); }





                        //-----------------------------------------------
                        // MARK - RECEIVER MESSAGE CELL
                        //-----------------------------------------------
                        } else {
                            senderCell.setVisibility(View.INVISIBLE);
                            receiverCell.setVisibility(View.VISIBLE);

                            // Message
                            TextView messTxt = finalCell.findViewById(R.id.rMessTxt);
                            messTxt.setTypeface(osRegular);
                            messTxt.setText(mObj.getString(MESSAGES_MESSAGE));

                            // Date
                            TextView dateTxt = finalCell.findViewById(R.id.rDateTxt);
                            dateTxt.setTypeface(osRegular);
                            dateTxt.setText(timeAgoSinceDate(mObj.getCreatedAt()));

                            // Image
                            ImageView messImage = finalCell.findViewById(R.id.rImage);
                            messImage.setVisibility(View.GONE);

                            if (mObj.get(MESSAGES_IMAGE) != null) {
                                messImage.setClipToOutline(true);
                                messImage.setVisibility(View.VISIBLE);
                                messTxt.setVisibility(View.INVISIBLE);

                                getParseImage(messImage, mObj, MESSAGES_IMAGE);

                                messImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        imgPreviewLayout.setVisibility(View.VISIBLE);
                                        getParseImage(imgPreview, mObj, MESSAGES_IMAGE);
                                }});

                            // Hide messImage
                            } else { messImage.setVisibility(View.GONE); }

                        }// ./ If

                    }});// end userPointer


            return cell;
            }
            @Override public int getCount() { return messagesArray.size(); }
            @Override public Object getItem(int position) { return messagesArray.get(position); }
            @Override public long getItemId(int position) { return position; }
        }

        // Set Adapter
        messagesListView.setAdapter(new ListAdapter(ctx));

    }






    //-----------------------------------------------
    // MARK - SEND MESSAGE
    //-----------------------------------------------
    void sendMessage() {

        lastMessage = messageTxt.getText().toString();
        sendMessageButt.setVisibility(View.INVISIBLE);

        final ParseObject mObj = new ParseObject(MESSAGES_CLASS_NAME);
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // Prepare data
        mObj.put(MESSAGES_SENDER, currentUser);
        mObj.put(MESSAGES_RECEIVER, userObj);
        mObj.put(MESSAGES_MESSAGE_ID, currentUser.getObjectId() + userObj.getObjectId());
        mObj.put(MESSAGES_MESSAGE, messageTxt.getText().toString());

        // Attach an Image (if any)
        if (imageToSend != null) {
            showHUD(ctx);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageToSend.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ParseFile imageFile = new ParseFile("image.jpg", byteArray);
            mObj.put(MESSAGES_IMAGE, imageFile);

            mObj.put(MESSAGES_MESSAGE, "[Photo]");
            lastMessage = "[Photo]";
        }


        // Saving...
        mObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    hideHUD();
                    dismissKeyboard();

                    // Call fiunction
                    updateInstants();

                    // Add message to the array (it's temporary, before a new query gets automatically called)
                    messagesArray.add(mObj);
                    messagesListView.invalidateViews();
                    messagesListView.refreshDrawableState();
                    scrollListViewToBottom();

                    // Reset variables
                    imageToSend = null;
                    startRefreshTimer();
                    messageTxt.setText("");

                    // Send Push notification
                    final String pushMessage = currentUser.getString(USER_FULLNAME) + ": '" + lastMessage + "'";

                    if (allowPush) {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("userObjectID", userObj.getObjectId());
                        params.put("data", pushMessage);
                        ParseCloud.callFunctionInBackground("pushAndroid", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if (e == null) {
                                    Log.i("log-", "PUSH SENT TO: " + userObj.getString(USER_USERNAME) + "\nMESSAGE: " + pushMessage);

                                // error
                                } else { simpleAlert(e.getMessage(), ctx);
                        }}});// ./ ParseCloud
                    }

                // error
                } else {
                    hideHUD();
                    simpleAlert(e.getMessage(), ctx);
            }}});
    }



    // ------------------------------------------------
    // MARK: - UPDATE THE INSTANTS CLASS
    // ------------------------------------------------
    void updateInstants() {
        final ParseUser currentUser = ParseUser.getCurrentUser();

        String messId1 = currentUser.getObjectId() + userObj.getObjectId();
        String messId2 = userObj.getObjectId() + currentUser.getObjectId();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery(INSTANTS_CLASS_NAME);
        String[] ids = {messId1, messId2};
        query.whereContainedIn(INSTANTS_ID, Arrays.asList(ids));

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    instantsArray = objects;

                    ParseObject iObj = new ParseObject(INSTANTS_CLASS_NAME);
                    if (instantsArray.size() != 0) { iObj = instantsArray.get(0); }

                    // Prepare data
                    iObj.put(INSTANTS_SENDER, currentUser);
                    iObj.put(INSTANTS_RECEIVER, userObj);
                    iObj.put(INSTANTS_ID, currentUser.getObjectId() + userObj.getObjectId());

                    // Saving...
                    iObj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("log-", "LAST MESS SAVED: " + lastMessage);
                            } else {
                                simpleAlert(e.getMessage(), ctx);
                    }}});

                // error
                } else { simpleAlert(e.getMessage(), ctx);
        }}});
    }






    //-----------------------------------------------
    // MARK - CAMERA AND GALLERY FUNCTIONS
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

        if (resultCode == Activity.RESULT_OK) {
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


            // Scale and Set image To send
            assert bm != null;
            imageToSend = scaleBitmapToMaxSize(600, bm);

            // Call function and attach an image
            sendMessage();
        }
    }





    //-----------------------------------------------
    // MARK - SCROLL LISTVIEW TO BOTTOM
    //-----------------------------------------------
    void scrollListViewToBottom() {
        messagesListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messagesListView.setStackFromBottom(true);
    }



    //-----------------------------------------------
    // MARK - REFRESH TIMER TO QUERY MESSAGES
    //-----------------------------------------------
    void startRefreshTimer() {
        int delay = 20 * 1000;
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                runOnUiThread(new Runnable() { @Override public void run() {

                    skip = 0;
                    messagesArray = new ArrayList<>();

                    // Call query
                    queryMessages();
                    Log.i(TAG, "REFRESH MESSAGES!");

        }});}}, delay, delay);
    }



    //-----------------------------------------------
    // MARK - DISMISS KEYBOARD
    //-----------------------------------------------
    void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(messageTxt.getWindowToken(), 0);
        messageTxt.clearFocus();
    }



    //-----------------------------------------------
    // MARK - RESET VARIABLES
    //-----------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageToSend = null;
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
        Log.i(TAG, "REFRESH TIMER - ON DESTROY: " + null + " -- IMAGE TO SEND: " + imageToSend);
    }

}// ./ end
