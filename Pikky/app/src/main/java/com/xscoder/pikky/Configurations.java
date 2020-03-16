package com.xscoder.pikky;

/*==================================================
     Pikky

     © XScoder 2018
     All Rights reserved

     RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
     YOU WILL BE LEGALLY PROSECUTED

==================================================*/

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;


import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.facebook.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageKuwaharaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;

public class Configurations extends Application {


    // IMPORTANT: Replace the red strings below with your own App ID and Client Key of your app on back4app.com
    public static String PARSE_APP_ID = "NiI9IlcotzDntclbyWGW6tGAAdm6jJVhToaSHKJA";
    public static String PARSE_CLIENT_KEY = "xeSziCQTN1icyV2U5PXWcfo0w9DEKacW9BbbZsyP";



    // ------------------------------------------------
    // MARK: - REPLACE THE FAKE EMAIL ADRESS BELOW WITH THE ONE YOU WANT PEOPLE TO CONTACT YOU FOR SUPPORT OR ANY QUESTIONS ABOUT YOUR APP
    // ------------------------------------------------
    public static String SUPPORT_EMAIL_ADDRESS = "support@mydomain.com";



    // ------------------------------------------------
    // This is the default maximum recording time for Videos [in Seconds]
    // You can edit the value below as you wish, but please keep in mind that if you increase the default value, it'll affect the uploading speed and time of a new Post
    // ------------------------------------------------
    public static int MAXIMUM_RECORDING_TIME = 20;


    // ------------------------------------------------
    // MARK: - CHANGE THE RGB VALUES OF THE COLOR INSTANCES BELOW AS YOU WISH
    // ------------------------------------------------
    public static String MAIN_COLOR =  "#00a2ff";
    public static String GREY = "#bababa";



    //-----------------------------------------------
    // MARK - LIST OF GPUIMAGE FILTERS
    //-----------------------------------------------
    public static PointF centerPoint = new PointF(0.5f, 0.5f);
    public static GPUImageFilter[] filtersList = {
            new GPUImageFilter(),               //0
            new GPUImageContrastFilter(2.0f),   //1
            new GPUImageColorInvertFilter(),    //2
            new GPUImageGrayscaleFilter(),      //3
            new GPUImageVignetteFilter(centerPoint, new float[] {0.0f, 0.0f, 0.0f}, 0.3f, 0.75f),   //4
            new GPUImageHueFilter(90.0f),       //5
            new GPUImageBrightnessFilter(0.3f), //6
            new GPUImageSepiaFilter(),          //7
            new GPUImageSharpenFilter(2.0f),    //8
            new GPUImageHighlightShadowFilter(1.0f, 1.5f),  //9
            new GPUImageCrosshatchFilter(),     //10
            new GPUImageKuwaharaFilter(),       //11
            new GPUImageSketchFilter(),         //12
            new GPUImageToonFilter(),           //13
    };


    //-----------------------------------------------
    // MARK - LIST OF FILTER NAMES
    //-----------------------------------------------
    public static String[] filterNamesList = {
            "None",     //0
            "Contrast", //1
            "Invert",   //2
            "Gray",     //3
            "Vigny",    //4
            "Hue",      //5
            "Light",    //6
            "Sepia",    //7
            "Sharp",    //8
            "Shadow",   //9
            "Cross",    //10
            "Kuwaha",   //11
            "Sketch",   //12
            "Toon",     //13
    };



    //-----------------------------------------------
    // MARK - LIST OF VIDEO FILTERS THUMBNAILS
    //-----------------------------------------------
    public static  int[] videoFiltersThumbnails = {
            R.drawable.f0,
            R.drawable.f1,
            R.drawable.f2,
            R.drawable.f3,
            R.drawable.f4,
            R.drawable.f5,
            R.drawable.f6,
            R.drawable.f7,
            R.drawable.f8,
            R.drawable.f9,
    };




    //-----------------------------------------------
    // // DECLARE FONT FILES /* The font files are into the app/src/main/assets/font folder */
    //-----------------------------------------------
    public static Typeface osBold, osSemibold, osRegular, osExtraBold, osLight, osItalic, lobster;








    /*----------------------------------------------------------------------------------------
        THE LINES OF CODE BELOW MUST NOT BE EDITED, OTHERWISE THE APP MAY STOP WORKING PROPERLY
    -----------------------------------------------------------------------------------------*/

    /* PARSE DASHBOARD CLASSES & COLUMNS */
    public static String  USER_CLASS_NAME = "_User";
    public static String  USER_USERNAME = "username";
    public static String  USER_USERNAME_LOWERCASE = "usernameLowercase";
    public static String  USER_EMAIL = "email";
    public static String  USER_FULLNAME = "fullName";
    public static String  USER_AVATAR = "avatar";
    public static String  USER_IS_REPORTED = "isReported";
    public static String USER_WEBSITE = "website";
    public static String USER_BIO = "bio";
    public static String USER_IS_VERIFIED = "isVerified";
    public static String USER_IS_FOLLOWING = "isFollowing";
    public static String USER_MUTED_BY = "mutedBy";
    public static String USER_BLOCKED_BY = "blockedBy";
    public static String USER_NOT_INTERESTING_FOR = "notInterestingFor";
    public static String USER_CREATED_AT = "createdAt";

    public static String POSTS_CLASS_NAME = "Posts";
    public static String POSTS_USER_POINTER = "userPointer";
    public static String POSTS_IMAGE = "image";
    public static String POSTS_VIDEO = "video";
    public static String POSTS_LIKES = "likes";
    public static String POSTS_COMMENTS = "comments";
    public static String POSTS_BOOKMARKED_BY = "bookmarkedBy";
    public static String POSTS_LIKED_BY = "likedBy";
    public static String POSTS_TEXT = "text";
    public static String POSTS_LOCATION = "location";
    public static String POSTS_TAGS = "tags";
    public static String POSTS_REPORTED_BY = "reportedBy";
    public static String POSTS_FOLLOWED_BY = "followedBy";
    public static String POSTS_KEYWORDS = "keywords";
    public static String POSTS_CAN_COMMENT = "canComment";
    public static String POSTS_CREATED_AT = "createdAt";

    public static String FOLLOW_CLASS_NAME = "Follow";
    public static String FOLLOW_CURRENT_USER = "currentUser";
    public static String FOLLOW_IS_FOLLOWING = "isFollowing";

    public static String MOMENTS_CLASS_NAME = "Moments";
    public static String MOMENTS_USER_POINTER = "userPointer";
    public static String MOMENTS_VIDEO = "video";
    public static String MOMENTS_FOLLOWED_BY = "followedBy";
    public static String MOMENTS_REPORTED_BY = "reportedBy";
    public static String MOMENTS_CREATED_AT = "createdAt";

    public static String COMMENTS_CLASS_NAME = "Comments";
    public static String COMMENTS_CURRENT_USER = "currentUser";
    public static String COMMENTS_COMMENT = "comment";
    public static String COMMENTS_POST_POINTER = "postPointer";
    public static String COMMENTS_LIKES = "likes";
    public static String COMMENTS_LIKED_BY = "likedBy";
    public static String COMMENTS_REPORTED_BY = "reportedBy";
    public static String COMMENTS_CREATED_AT = "createdAt";

    public static String NOTIFICATIONS_CLASS_NAME = "Notifications";
    public static String NOTIFICATIONS_TEXT = "text";
    public static String NOTIFICATIONS_CURRENT_USER = "currentUser";
    public static String NOTIFICATIONS_OTHER_USER = "otherUser";
    public static String NOTIFICATIONS_CREATED_AT = "createdAt";

    public static String  MESSAGES_CLASS_NAME = "Messages";
    public static String  MESSAGES_SENDER = "sender";
    public static String  MESSAGES_RECEIVER = "receiver";
    public static String  MESSAGES_MESSAGE_ID = "messageID";
    public static String  MESSAGES_MESSAGE = "message";
    public static String  MESSAGES_IMAGE = "image";
    public static String  MESSAGES_CREATED_AT = "createdAt";

    public static String  INSTANTS_CLASS_NAME = "Instants";
    public static String  INSTANTS_SENDER = "sender";
    public static String  INSTANTS_RECEIVER = "receiver";
    public static String  INSTANTS_ID = "instantID";
    public static String  INSTANTS_UPDATED_AT = "updatedAt";

    boolean isParseInitialized = false;

    public void onCreate() {
        super.onCreate();

        if (!isParseInitialized) {
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(String.valueOf(PARSE_APP_ID))
                    .clientKey(String.valueOf(PARSE_CLIENT_KEY))
                    .server("https://parseapi.back4app.com")
                    .build()
            );
            Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
            ParseUser.enableAutomaticUser();
            isParseInitialized = true;


            // Init Facebook Utils
            ParseFacebookUtils.initialize(this);
        }


        // Init fonts
        osBold = Typeface.createFromAsset(getAssets(),"font/OpenSans-Bold.ttf");
        osSemibold = Typeface.createFromAsset(getAssets(),"font/OpenSans-Semibold.ttf");
        osRegular = Typeface.createFromAsset(getAssets(),"font/OpenSans-Regular.ttf");
        osExtraBold = Typeface.createFromAsset(getAssets(),"font/OpenSans-ExtraBold.ttf");
        osLight = Typeface.createFromAsset(getAssets(),"font/OpenSans-Light.ttf");
        osItalic = Typeface.createFromAsset(getAssets(),"font/OpenSans-Italic.ttf");
        lobster = Typeface.createFromAsset(getAssets(),"font/Lobster.otf");


    }// end onCreate()


    //-----------------------------------------------
    // MARK - GLOBAL VARIABLES
    //-----------------------------------------------
    public static boolean mustDismiss = false;
    public static boolean allowPush = false;
    public static String videoToShareURL = "";
    public static String TAG = "log-";
    public static final int MULTIPLE_PERMISSIONS = 10;
    public static String[] permissions= new String[]{
          android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
          android.Manifest.permission.READ_EXTERNAL_STORAGE,
          android.Manifest.permission.CAMERA,
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.RECORD_AUDIO
    };
   public static int screenWidth;




   //-----------------------------------------------
    // MARK - CUSTOM HUD
    //-----------------------------------------------
    public static AlertDialog hud;
    public static void showHUD(Context ctx) {
        AlertDialog.Builder db = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.hud, null);
        db.setView(dialogView);
        db.setCancelable(true);
        hud = db.create();
        hud.show();
    }

    public static void hideHUD() {
        if (hud.isShowing()) { hud.dismiss(); }
    }



    //-----------------------------------------------
    // MARK - GET PARSE IMAGE
    //-----------------------------------------------
    public static void getParseImage(final ImageView imgView, ParseObject parseObj, String className) {
        ParseFile fileObject = parseObj.getParseFile(className);
        if (fileObject != null ) {
            fileObject.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException error) {
                    if (error == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bmp != null) {
                            imgView.setImageBitmap(bmp);
        }}}});}
    }



    //-----------------------------------------------
    // MARK - SAVE A PARSE IMAGE
    //-----------------------------------------------
    public static void saveParseImage(ImageView imgView, ParseObject parseObj, String className) {
        Bitmap bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile imageFile = new ParseFile("image.jpg", byteArray);
        parseObj.put(className, imageFile);
    }


    //-----------------------------------------------
    // MARK - GET IMAGE ORIENTATION
    //-----------------------------------------------
    public static int getImageOrientation (String imgPath) {
        try {
            ExifInterface ei = new ExifInterface(imgPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Log.e("orientation","orientation"+orientation);
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //-----------------------------------------------
    // MARK - SCALE BITMAP TO MAX SIZE
    //-----------------------------------------------
    public static Bitmap scaleBitmapToMaxSize(int maxSize, Bitmap bm) {
        int outWidth;
        int outHeight;
        int inWidth = bm.getWidth();
        int inHeight = bm.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

       return Bitmap.createScaledBitmap(bm, outWidth, outHeight, false);
    }


    //-----------------------------------------------
    // MARK - SCALE BITMAP TO MAX SIZE AND ROTATE
    //-----------------------------------------------
    public static Bitmap scaleBitmapToMaxSizeRotate(int maxSize, Bitmap bm, int rotation) {
        int outWidth;
        int outHeight;
        int inWidth = bm.getWidth();
        int inHeight = bm.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap bmp = Bitmap.createScaledBitmap(bm, outWidth, outHeight, false);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
    }


    //-----------------------------------------------
    // MARK - CROP BITMAP TO SQUARE
    //-----------------------------------------------
    public static Bitmap cropBitmapToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;


        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }



    //-----------------------------------------------
    // MARK - GET TIME AGO SINCE DATE
    //-----------------------------------------------
    public static String timeAgoSinceDate(Date date) {
        String dateString = "";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            String sentStr = (df.format(date));
            Date past = df.parse(sentStr);
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(seconds<60)  { dateString = seconds+" seconds ago";
            } else if(minutes<60)  { dateString = minutes+" minutes ago";
            } else if(hours<24) { dateString = hours+" hours ago";
            } else { dateString = days+" days ago"; }
        }
        catch (Exception j){ j.printStackTrace(); }
        return dateString;
    }




    //-----------------------------------------------
    // MARK - ROUND LARGE NUMBER INTO K, M, ETC.
    //-----------------------------------------------
    public static String roundLargeNumber(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }




    //-----------------------------------------------
    // MARK - SIMPLE ALERT DIALOG
    //-----------------------------------------------
    public static void simpleAlert(String mess, Context activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage(mess)
                .setTitle(R.string.app_name)
                .setPositiveButton("OK", null)
                .setIcon(R.drawable.logo);
        alert.create().show();
    }



    //-----------------------------------------------
    // MARK - SEND A PUSH NOTIFICATION AND SAVE NOTIFICATION
    //-----------------------------------------------
    public static void sendPushNotification(final String pushMessage, final ParseUser userObj, final Context ctx) {
        final ParseUser currentUser = ParseUser.getCurrentUser();

        if (allowPush) {
            final HashMap<String, Object> params = new HashMap<>();
            params.put("userObjectID", userObj.getObjectId());
            params.put("data", pushMessage);
            ParseCloud.callFunctionInBackground("pushAndroid", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object object, ParseException e) {
                    if (e == null) {
                        Log.i(TAG, "PUSH SENT TO: " + userObj.getString(USER_USERNAME) + "\nMESSAGE: " + pushMessage);
                    // error
                    } else {
                        simpleAlert(e.getMessage(), ctx);
            }}});
        }

        // ------------------------------------------------
        // MARK: - SAVE NOTIFICATION IN THE DATABASE
        // ------------------------------------------------
        ParseObject nObj = new ParseObject(Configurations.NOTIFICATIONS_CLASS_NAME);
        nObj.put(Configurations.NOTIFICATIONS_TEXT, pushMessage);
        nObj.put(Configurations.NOTIFICATIONS_CURRENT_USER, currentUser);
        nObj.put(Configurations.NOTIFICATIONS_OTHER_USER, userObj);
        nObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "NOTIFICATION SAVED IN THE DATABASE!");
                } else {
                    simpleAlert(e.getMessage(),ctx);
        }}});
    }



    //-----------------------------------------------
    // MARK - CONVERT VIDEO TO BYTES
    //-----------------------------------------------
    public static byte[] convertVideoToBytes(String videoPath){
        byte[] videoBytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(videoPath));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);

            videoBytes = baos.toByteArray();
        } catch (IOException e) { e.printStackTrace(); }
        return videoBytes;
    }


    //-----------------------------------------------
    // MARK - GET REAL PATH FROM FILE URI
    //-----------------------------------------------
    public static String getRealPathFromURI(Uri fileUri, Context ctx) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = ctx.getContentResolver().query(fileUri, filePathColumn, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        Log.i(TAG, "VIDEO PATH: " + filePath);
        return filePath;
    }


    //-----------------------------------------------
    // MARK - GET URI OF A STORED IMAGE
    //-----------------------------------------------
    public static Uri getImageUri(Bitmap bm, Context ctx) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), bm, "image", null);
        return Uri.parse(path);
    }



    //-----------------------------------------------
    // MARK - INTERSTITIAL AD IMPLEMENTATION
    //----------------------------------------------
    public static void showAdMobInterstitial(Context ctx) {
//        MobileAds.initialize(ctx, ctx.getString(R.string.ADMOB_APP_ID));
//        final InterstitialAd interstitialAd = new InterstitialAd(ctx);
//        interstitialAd.setAdUnitId(ctx.getString(R.string.ADMOB_INTERSTITIAL_UNIT_ID));
//        AdRequest requestForInterstitial = new AdRequest.Builder().build();
//        interstitialAd.loadAd(requestForInterstitial);
//        interstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                Log.i(TAG, "INTERSTITIAL is loaded!");
//                if (interstitialAd.isLoaded()) {
//                    interstitialAd.show();
//                }
//            }
//
//            @Override
//            public void onAdClosed() {
//                Log.i(TAG, "INTERSTITIAL is closed!");
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                Log.i(TAG, "INTERSTITIAL failed to load! error code: " + errorCode);
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                Log.i(TAG, "INTERSTITIAL left application!");
//            }
//
//            @Override
//            public void onAdOpened() {
//                Log.i(TAG, "INTERSTITIAL is opened!");
//            }
//        });
    }


}// ./ END
