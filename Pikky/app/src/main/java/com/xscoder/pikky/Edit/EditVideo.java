package com.xscoder.pikky.Edit;

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

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xscoder.pikky.R;
import com.xscoder.pikky.Share.ShareScreen;

import mp4composer.FillMode;
import mp4composer.composer.Mp4Composer;
import mp4composer.filter.GlBrightnessFilter;
import mp4composer.filter.GlContrastFilter;
import mp4composer.filter.GlFilter;
import mp4composer.filter.GlGammaFilter;
import mp4composer.filter.GlGrayScaleFilter;
import mp4composer.filter.GlHueFilter;
import mp4composer.filter.GlInvertFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;


import mp4composer.filter.GlSepiaFilter;
import mp4composer.filter.GlSharpenFilter;
import mp4composer.filter.GlVignetteFilter;
import videoeffects.BrightnessEffect;
import videoeffects.ContrastEffect;
import videoeffects.GammaEffect;
import videoeffects.GreyScaleEffect;
import videoeffects.HueEffect;
import videoeffects.InvertColorsEffect;
import videoeffects.NoEffect;
import videoeffects.SepiaEffect;
import videoeffects.SharpnessEffect;
import videoeffects.VignetteEffect;
import videoeffects.view.VideoSurfaceView;


import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.filterNamesList;
import static com.xscoder.pikky.Configurations.getImageOrientation;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.mustDismiss;
import static com.xscoder.pikky.Configurations.osLight;
import static com.xscoder.pikky.Configurations.scaleBitmapToMaxSize;
import static com.xscoder.pikky.Configurations.scaleBitmapToMaxSizeRotate;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;
import static com.xscoder.pikky.Configurations.videoFiltersThumbnails;
import static com.xscoder.pikky.Configurations.videoToShareURL;

public class EditVideo extends AppCompatActivity {

    /*--- VIEWS ---*/
    RelativeLayout videoContainerLayout;
    VideoSurfaceView surfaceView = null;
    MediaPlayer mediaPlayer;

    int ht = 0;

    /*--- VARIABLES ---*/
    int videoDuration = 0;
    String tempVideoPath;
    Context ctx = this;




    //-----------------------------------------------
    // MARK - ON START
    //-----------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        if (mustDismiss) {
            mustDismiss = false;
            finish();
        }
    }



    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_video);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int W = displayMetrics.widthPixels;


        //-----------------------------------------------
        // MARK - GET DATA FROM SQUARE CAMERA ACTIVITY
        //-----------------------------------------------
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        ht = extras.getInt("height", 0);

        // Fetch the height of captured image
        if(ht == 0) {
            ht = W;
        } else {
            int width = View.MeasureSpec.getSize(W);
            int height = View.MeasureSpec.getSize(displayMetrics.heightPixels);
            int margin = (height - width) / 2;
            ht  = ht - margin;
        }



        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        videoContainerLayout = findViewById(R.id.evVideoContainerLayout);
        surfaceView = findViewById(R.id.mVideoSurfaceView);



        //-----------------------------------------------
        // MARK - SET VIDEO CONTAINER LAYOUT SIZE
        //-----------------------------------------------
        videoContainerLayout.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) videoContainerLayout.getLayoutParams();
        lp2.width = W;
        lp2.height = ht;
        videoContainerLayout.setLayoutParams(lp2);
        Log.i(TAG,"VIDEO H: " + ht);



        //-----------------------------------------------
        // MARK - INITIALIZE MEDIA PLAYER AND PLAY THE ORIGINAL VIDEO
        //-----------------------------------------------
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);

        // Get Video size
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoToShareURL);
        try {
            int videoW = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int videoH = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            videoDuration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            retriever.release();
            Log.i(TAG, "VIDEO SIZE: " + videoW + " - " + videoH);
            Log.i(TAG, "VIDEO DURATION: " + videoDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Play video with No effect
        try { mediaPlayer.setDataSource(videoToShareURL);
            surfaceView.init(mediaPlayer, new NoEffect());

        } catch (Exception e) { Log.i(TAG, e.getMessage(), e); }



        // CALL METHOD TO CREATE FILTERS TOOLBAR
        createFiltersToolbar();





        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.evBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditVideo.this);
                alert.setMessage("Are you sure you want to Exit?")
                        .setTitle(R.string.app_name)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }})
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false)
                        .setIcon(R.drawable.logo);
                alert.create().show();
            }});




        //-----------------------------------------------
        // MARK - NEXT BUTTON
        //-----------------------------------------------
        Button nextButt = findViewById(R.id.evNextButt);
        nextButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.isPlaying()) { mediaPlayer.stop(); }

                switch (filterIndex) {
                    case 0: renderVideoIntoMp4(videoToShareURL, new GlFilter()); break;
                    case 1: renderVideoIntoMp4(videoToShareURL, new GlContrastFilter()); break;
                    case 2: renderVideoIntoMp4(videoToShareURL, new GlInvertFilter()); break;
                    case 3: renderVideoIntoMp4(videoToShareURL, new GlGrayScaleFilter()); break;
                    case 4: renderVideoIntoMp4(videoToShareURL, new GlVignetteFilter()); break;
                    case 5: renderVideoIntoMp4(videoToShareURL, new GlHueFilter()); break;
                    case 6: renderVideoIntoMp4(videoToShareURL, new GlBrightnessFilter()); break;
                    case 7: renderVideoIntoMp4(videoToShareURL, new GlSepiaFilter()); break;
                    case 8: renderVideoIntoMp4(videoToShareURL, new GlSharpenFilter()); break;
                    case 9: renderVideoIntoMp4(videoToShareURL, new GlGammaFilter()); break;

                }
            }});


    }// ./ onCreate






    //-----------------------------------------------
    // MARK - CREATE FILTERS TOOLBAR
    //-----------------------------------------------
    int filterIndex = 0;
    void createFiltersToolbar() {
        for (int i = 0; i< videoFiltersThumbnails.length; i++) {
            LinearLayout layout = findViewById(R.id.evFiltersLayout);
            layout.setOrientation(LinearLayout.HORIZONTAL);


            // Buttons layout container
            RelativeLayout buttLayout = new RelativeLayout(this);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(width, height);
            lp1.setMargins(5, 0, 5, 0);
            buttLayout.setLayoutParams(lp1);


            // Setup Filter Buttons
            ImageView filterButt = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            lp.setMargins(5, 0, 5, 0);
            final int finalI = i;

            filterButt.setLayoutParams(lp);
            filterButt.setId(i);
            filterButt.setBackgroundResource(videoFiltersThumbnails[i]);


            // APPLY FILTER ON CLICK
            filterButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterIndex = v.getId();

                    if (surfaceView != null) {
                        surfaceView = null;
                        surfaceView = findViewById(R.id.mVideoSurfaceView);
                    }

                    assert surfaceView != null;
                    switch (finalI) {
                        case 0: surfaceView.init(mediaPlayer, new NoEffect());          break;
                        case 1: surfaceView.init(mediaPlayer, new ContrastEffect(0.6f)); break;
                        case 2: surfaceView.init(mediaPlayer, new InvertColorsEffect());    break;
                        case 3: surfaceView.init(mediaPlayer, new GreyScaleEffect());           break;
                        case 4: surfaceView.init(mediaPlayer, new VignetteEffect(0.5f));  break;
                        case 5: surfaceView.init(mediaPlayer, new HueEffect(90));   break;
                        case 6: surfaceView.init(mediaPlayer, new BrightnessEffect(1.4f));  break;
                        case 7: surfaceView.init(mediaPlayer, new SepiaEffect());  break;
                        case 8: surfaceView.init(mediaPlayer, new SharpnessEffect(0.8f));  break;
                        case 9: surfaceView.init(mediaPlayer, new GammaEffect(2.0f));  break;

                        default:break;
                    }

                }});

            buttLayout.addView(filterButt);



            // TextView with Filter names
            TextView filterNameTxt = new TextView(this);
            int height2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(width, height2);
            lp2.setMargins(5, 0, 5, 0);
            filterNameTxt.setLayoutParams(lp2);

            filterNameTxt.setText(filterNamesList[finalI]);
            filterNameTxt.setBackgroundColor(Color.parseColor("#777777"));
            filterNameTxt.setTypeface(osLight);
            filterNameTxt.setTextSize(11);
            filterNameTxt.setTextColor(Color.WHITE);
            filterNameTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            buttLayout.addView(filterNameTxt);


            // Add Filter Buttons and TextViews to the FiltersLayout in the ScrollView
            layout.addView(buttLayout);
        }
    }





    //-----------------------------------------------
    // MARK - RENDER VIDEO INTO .MP4
    //-----------------------------------------------
    void renderVideoIntoMp4(String filePath, GlFilter filter) {

        // Get Video's temporary path
        tempVideoPath = getVideoFilePath();
        Log.i(TAG, "VIDEO PATH: " + tempVideoPath);


        // Dispatch functions
        runOnUiThread(new Runnable() {
            @Override public void run() {
                showHUD(ctx);
            }});


        // Render video
        new Mp4Composer(filePath, tempVideoPath)
                // .rotation(Rotation.ROTATION_270)
                .size(640, 640)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .filter(filter)
                .mute(false)
                .flipHorizontal(false)
                .flipVertical(false)
                .listener(new Mp4Composer.Listener() {
                    @Override public void onProgress(double progress) {
                        Log.i(TAG, "SAVING PROGRESS: " + progress*100);
                    }

                    // Video has been rendered
                    @Override public void onCompleted() {

                        Log.i(TAG, "SAVED VIDEO PATH: " + tempVideoPath);

                        // Dispatch functions
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                enterShareScreen();
                            }});
                    }

                    @Override public void onCanceled() { }

                    // Error on rendering
                    @Override public void onFailed(final Exception e) {

                        // Dispatch functions
                        runOnUiThread(new Runnable() {
                            @Override public void run() {

                                hideHUD();
                                simpleAlert(e.getMessage(), ctx);
                            }});

                        Log.i(TAG, "RENDERING FAILED: " + e.getMessage());
                    }
                })
                .start();

    }
    public File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    public String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/temp_video.mp4";
    }




    //-----------------------------------------------
    // MARK - ENTER SHARE SCREEN
    //-----------------------------------------------
    void enterShareScreen() {
        // Get video thumbnail
        Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(tempVideoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);


        // Rotate image on some devices
        int rotationOrientation = getImageOrientation(tempVideoPath);
        Bitmap scaledBM;
        if(rotationOrientation == 0) {
            scaledBM = scaleBitmapToMaxSize(640, videoThumbnail);
        } else {
            scaledBM = scaleBitmapToMaxSizeRotate(640, videoThumbnail, rotationOrientation);
        }

        // Pass data to the Share screen
        Intent i = new Intent(ctx, ShareScreen.class);
        Bundle extras = new Bundle();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBM.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        extras.putByteArray("imageToShare", byteArray);
        extras.putString("videoURL", tempVideoPath);
        i.putExtras(extras);
        startActivity(i);
    }






    //-----------------------------------------------
    // MARK - ON STOP
    //-----------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");

        // if (mediaPlayer != null) {
        mediaPlayer.release();
        // }

        // Reset videoToShareURL
        videoToShareURL = "";
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");

        // if (mediaPlayer != null) {
        mediaPlayer.release();
        // }

        // Reset videoToShareURL
        videoToShareURL = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");

        if (surfaceView != null) { surfaceView.onResume(); }
    }




}// ./ end
