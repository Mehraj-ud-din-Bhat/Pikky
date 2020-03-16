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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xscoder.pikky.Edit.TextureVideoView;
import com.xscoder.pikky.Userprofile.UserProfile;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.xscoder.pikky.Configurations.MOMENTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.MOMENTS_REPORTED_BY;
import static com.xscoder.pikky.Configurations.MOMENTS_USER_POINTER;
import static com.xscoder.pikky.Configurations.MOMENTS_VIDEO;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.simpleAlert;

import de.hdodenhof.circleimageview.CircleImageView;

public class WatchVideo extends AppCompatActivity {

    /*--- VIEWS ---*/
    TextureVideoView videoView;
    ProgressBar videoProgBar, videoLoadingProg;
    Button dismissButt, optionsButt;
    CircleImageView avatarImg;
    TextView usernameTxt;



    /*--- VARIABLES ---*/
    Context ctx = this;
    ParseObject mObj;
    int videoDuration;




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_video);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        videoView = findViewById(R.id.wvVideoView);

        videoProgBar = findViewById(R.id.wvVideoProgressBar);
        videoProgBar.setProgress(0);
        videoProgBar.setMax(100);

        videoLoadingProg = findViewById(R.id.videoLoadingProg);
        dismissButt = findViewById(R.id.wvDismissButt);
        optionsButt = findViewById(R.id.wvOptionsButt);
        avatarImg = findViewById(R.id.wvAvatarImg);
        usernameTxt = findViewById(R.id.wvIUsernameTxt);
        usernameTxt.setTypeface(osSemibold);


        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        // Get objectID from previous .java
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String objectID = extras.getString("objectID");
        mObj = ParseObject.createWithoutData(MOMENTS_CLASS_NAME, objectID);
        try { mObj.fetchIfNeeded().getParseObject(MOMENTS_CLASS_NAME);

            final ParseUser currentUser = ParseUser.getCurrentUser();

            // Get userPointer
            Objects.requireNonNull(mObj.getParseObject(MOMENTS_USER_POINTER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                public void done(final ParseObject userPointer, ParseException e) {

                    // Video URL
                    final ParseFile videoFile = mObj.getParseFile(MOMENTS_VIDEO);
                    assert videoFile != null;
                    String videoURL = videoFile.getUrl();

                    // Avatar
                    getParseImage(avatarImg, userPointer, USER_AVATAR);

                    // Username
                    usernameTxt.setText(userPointer.getString(USER_USERNAME));


                    //-----------------------------------------------
                    // MARK - PLAY VIDEO
                    //-----------------------------------------------
                    videoView.setScaleType(TextureVideoView.ScaleType.TOP);
                    videoView.setDataSource(videoURL);

                    // Play Video
                    videoView.setListener(new TextureVideoView.MediaPlayerListener() {
                        @Override
                        public void onVideoPrepared() {
                            videoDuration = videoView.getDuration();
                            Log.i(TAG, "VIDEO DURATION IN SEC.: " + videoDuration/1000);
                            videoView.play();
                            videoLoadingProg.setVisibility(View.INVISIBLE);

                            // Fire Progress Timer
                            fireProgressTimer();
                        }

                        @Override
                        public void onVideoEnd() { }
                    });

                    // Tap to Replay video
                    videoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (videoView.isPlaying()) { videoView.stop(); }
                            videoView.seekTo(0);
                            videoProgBar = findViewById(R.id.wvVideoProgressBar);
                            videoView.play();
                    }});




                    //-----------------------------------------------
                    // MARK - OPTIONS BUTTON
                    //-----------------------------------------------
                    optionsButt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Pause video and get data
                            if (videoView.isPlaying()) { videoView.pause(); }
                            final List<String>reportedBy = mObj.getList(MOMENTS_REPORTED_BY);

                            // Fire alert
                            AlertDialog.Builder alert  = new AlertDialog.Builder(ctx);
                            alert.setTitle("Select option")
                                    .setIcon(R.drawable.logo)
                                    .setItems(new CharSequence[] {
                                                    "Replay",
                                                    "Report Video",
                                                    "Exit"
                                    }, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {

                                                // REPLAY VIDEO
                                                case 0:
                                                    videoView.seekTo(0);
                                                    videoProgBar = findViewById(R.id.wvVideoProgressBar);
                                                    videoView.play();
                                                    break;


                                                // REPORT VIDEO
                                                case 1:
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                                    alert.setMessage("Are you sure you want to report this video to the Admin?")
                                                        .setTitle(R.string.app_name)
                                                        .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                assert reportedBy != null;
                                                                reportedBy.add(currentUser.getObjectId());
                                                                mObj.put(MOMENTS_REPORTED_BY, reportedBy);
                                                                mObj.saveInBackground(new SaveCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if (e == null) {
                                                                            simpleAlert("Thanks for reporting this video!\nWe'll check it out within 24 hours.", ctx);
                                                                        } else {
                                                                        simpleAlert(e.getMessage(), ctx);
                                                                }}});
                                                        }})
                                                        .setNegativeButton("Cancel", null)
                                                        .setCancelable(false)
                                                        .setIcon(R.drawable.logo);
                                                    alert.create().show();
                                                    break;


                                                // EXIT
                                                case 2:
                                                    videoView.invalidate();
                                                    finish();
                                                    break;

                                    }}})
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Keep playing video
                                            videoView.play();
                                    }})
                                    .setCancelable(false);
                            alert.create().show();
                    }});




                    //-----------------------------------------------
                    // MARK - AVATAR IMAGE
                    //-----------------------------------------------
                    avatarImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (videoView.isPlaying()) {
                                videoView.stop();
                                videoView.invalidate();
                            }

                            if (!userPointer.getObjectId().matches(currentUser.getObjectId()) ){
                                Intent i = new Intent(ctx, UserProfile.class);
                                Bundle extras = new Bundle();
                                extras.putString("objectID", userPointer.getObjectId());
                                i.putExtras(extras);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(ctx, UserProfile.class);
                                Bundle extras = new Bundle();
                                extras.putBoolean("isCurrentUser", true);
                                i.putExtras(extras);
                                startActivity(i);
                            }
                    }});



            }});// end userPointer

        } catch (ParseException e) { e.printStackTrace(); }







        //-----------------------------------------------
        // MARK - DISMISS BUTTON
        //-----------------------------------------------
        dismissButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              videoView.stop();
              videoView.invalidate();
              finish();
        }});


    }// ./ onCreate




    //-----------------------------------------------
    // MARK - UPDATE VIDEO PROGRESS BAR
    //-----------------------------------------------
    private Timer timer;
    private void fireProgressTimer(){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVideoProgBar();
            }});
        }};
        timer.schedule(task, 0, 500);
    }
    private void updateVideoProgBar(){
        if (videoProgBar.getProgress() >= 100) { timer.cancel(); }
        int current = videoView.getCurrentPosition();
        int progress = current * 100 / videoDuration;
        videoProgBar.setProgress(progress);
    }




}// ./end
