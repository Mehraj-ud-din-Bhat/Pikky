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
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.cropBitmapToSquare;
import static com.xscoder.pikky.Configurations.filterNamesList;
import static com.xscoder.pikky.Configurations.filtersList;
import static com.xscoder.pikky.Configurations.mustDismiss;
import static com.xscoder.pikky.Configurations.osLight;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.scaleBitmapToMaxSize;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class EditImage extends AppCompatActivity {

    /*--- VIEWS ---*/
    ImageView originalImg;
    GPUImageView imageToFilter;
    TextView titleTxt;
    RelativeLayout bottomLayout;


    /*--- VARIABLES ---*/
    Bitmap originalBm;
    Context ctx = this;
    int height = 0;




    //-----------------------------------------------
    // MARK - ON START
    //-----------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        if (mustDismiss) { finish(); }
    }




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_image);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        titleTxt = findViewById(R.id.eiTitleTxt);
        titleTxt.setTypeface(osSemibold);
        originalImg = findViewById(R.id.eiOriginalImg);
        imageToFilter = findViewById(R.id.eiGPUimage);
        imageToFilter.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        bottomLayout  = findViewById(R.id.eiBottomLayout);



        //-----------------------------------------------
        // MARK - GET EXTRAS FROM THE SQUARE CAMERA SCREEN
        //-----------------------------------------------
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        height = extras.getInt("height", 0);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        originalBm = bm;
        originalImg.setImageBitmap(bm);
        imageToFilter.setImage(bm);

        Log.i(TAG, "BM W: " + originalBm.getWidth());
        Log.i(TAG, "BM H: " + originalBm.getHeight());


        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int W = displayMetrics.widthPixels;


        //-----------------------------------------------
        // MARK - SET ORIGINAL IMAGEVIEW SIZE
        //-----------------------------------------------
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) originalImg.getLayoutParams();
        lp.width = W;
        lp.height = W;
        originalImg.setLayoutParams(lp);
        originalImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageToFilter.setLayoutParams(lp);


        // create Filters Toolbar
        createFiltersToolbar();




        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.eiBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditImage.this);
                alert.setMessage("Are you sure you want to Exit?")
                        .setTitle(R.string.app_name)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false)
                        .setIcon(R.drawable.logo);
                alert.create().show();
            }});




        //-----------------------------------------------
        // MARK - NEXT BUTTON
        //-----------------------------------------------
        Button nextButt = findViewById(R.id.eiNextButt);
        nextButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // RENDER FILTERED GPUIMAGE AND PASS IT TO THE SHARE SCREEN
                Bitmap bm = imageToFilter.getGPUImage().getBitmapWithFilterApplied();
                Bitmap croppedBM = cropBitmapToSquare(bm);
                Bitmap scaledBM = scaleBitmapToMaxSize(640, croppedBM);

                Intent i = new Intent(ctx, ShareScreen.class);
                Bundle extras = new Bundle();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledBM.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                extras.putByteArray("imageToShare", byteArray);

                i.putExtras(extras);
                startActivity(i);
            }});


    }// ./ onCreate()






    //-----------------------------------------------
    // MARK - CREATE FILTERS TOOLBAR
    //-----------------------------------------------
    void createFiltersToolbar() {
        for (int i = 0; i< filtersList.length; i++) {
            LinearLayout layout = findViewById(R.id.eiFiltersLayout);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            // Setup GPUImageViews
            GPUImageView filterButt = new GPUImageView(this);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            lp.setMargins(5, 0, 5, 0);
            final int finalI = i;

            filterButt.setLayoutParams(lp);
            filterButt.setId(i);
            filterButt.setImage(originalBm);

            // Apply filter to the filterButt
            filterButt.setFilter(filtersList[i]);
            filterButt.requestRender();


            // APPLY FILTER ON CLICK
            filterButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageToFilter.setFilter(filtersList[finalI]);
                    imageToFilter.requestRender();
                }});


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

            filterButt.addView(filterNameTxt);


            // Add Filter Buttons to the FiltersLayout in the ScrollView
            layout.addView(filterButt);
        }
    }


}// ./ end


