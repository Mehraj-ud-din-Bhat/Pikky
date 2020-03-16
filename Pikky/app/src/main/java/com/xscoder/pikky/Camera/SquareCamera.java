package com.xscoder.pikky.Camera;

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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xscoder.pikky.Edit.AutoFitTextureView;
import com.xscoder.pikky.Edit.EditImage;
import com.xscoder.pikky.Edit.EditVideo;
import com.xscoder.pikky.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.xscoder.pikky.Configurations.getRealPathFromURI;
import static com.xscoder.pikky.Configurations.hud;
import static com.xscoder.pikky.Configurations.screenWidth;
import static com.xscoder.pikky.Configurations.simpleAlert;
import static com.xscoder.pikky.Configurations.videoToShareURL;
import static com.xscoder.pikky.Configurations.MAXIMUM_RECORDING_TIME;
import static com.xscoder.pikky.Configurations.TAG;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.mustDismiss;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.scaleBitmapToMaxSize;
import static com.xscoder.pikky.Configurations.showHUD;

public class SquareCamera extends AppCompatActivity {

    /*--- VIEWS ---*/
    RelativeLayout toolsView, topView;
    Button takePicVideoButt, switchCameraButt, flashButt, galleryButt, photoCamButt, videoCamButt;
    TextView recordingTimeTxt, titleTxt;



    /*--- VARIABLES ---*/
    Context ctx = this;
    int mCameraLensFacingDirection = 0;
    boolean isFlashON = true;
    boolean isPhoto = true;

    public static final String CAMERA_BACK = "0";
    private String mCameraId;
    private AutoFitTextureView mTextureView;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private Size mPreviewSize;

    // Video Camera
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;


    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private boolean mIsRecordingVideo;


    // Photo Camera
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };


    //-----------------------------------------------
    // MARK - CAMERA STATE CALLBACK
    //-----------------------------------------------
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            finish();
        }

    };


    //-----------------------------------------------
    // MARK - IMAGE READER
    //-----------------------------------------------
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private ImageReader mImageReader;
    private File picFilePath;
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), picFilePath));
        }};


    //-----------------------------------------------
    // MARK - CAPTURE SESSION
    //-----------------------------------------------
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    private int mState = STATE_PREVIEW;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private boolean isFlashSupported;
    private int mSensorOrientation;

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    Log.e("rotationpic","rotationpic11 "+afState);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        Log.e("rotationpic","rotationpic22 "+afState);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            Log.e("rotationpic","rotationpic33 "+mState);
                            captureStillPicture();
                        } else {
                            Log.e("rotationpic","rotationpic44 "+mState);
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };




    // Choose optimal size
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }
        // Log.i(TAG, "SIZE: " + bigEnough);
        // Log.i(TAG, "SIZE 2: " + notBigEnough);
        Log.i(TAG, "TextureView W: " + textureViewWidth);
        Log.i(TAG, "TextureView H: " + textureViewHeight);

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.i(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }



    //-----------------------------------------------
    // MARK - ON START
    //-----------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "MUST DISMISS - SQUARE CAMERA: " + mustDismiss);
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
        setContentView(R.layout.square_camera);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        toolsView = findViewById(R.id.sqcToolsView);
        mTextureView = findViewById(R.id.texture);
        takePicVideoButt = findViewById(R.id.sqcTakePicVideoButt);
        switchCameraButt = findViewById(R.id.sqcSwitchCameraButt);
        flashButt = findViewById(R.id.sqcFlashButt);
        galleryButt = findViewById(R.id.sqcGalleryButt);
        photoCamButt = findViewById(R.id.sqcPhotoCameraButt);
        videoCamButt = findViewById(R.id.sqcVideoCameraButt);
        recordingTimeTxt = findViewById(R.id.sqcRecordingTimeTxt);
        recordingTimeTxt.setTypeface(osSemibold);
        recordingTimeTxt.setVisibility(View.INVISIBLE);
        titleTxt = findViewById(R.id.sqcTitleTxt);
        topView = findViewById(R.id.sqcTopView);


        // Initialize Photo File
        picFilePath = new File(getExternalFilesDir(null), "pikky_pic.jpg");


        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;


        // Set size of the camera's TextureView
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(screenWidth, screenWidth);
        lp2.setMargins(0, 0, 0,0);
        lp2.addRule(RelativeLayout.BELOW, topView.getId());
        lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mTextureView.setLayoutParams(lp2);


        // Set Height of the ToolsView
        int toolsViewHeight = screenWidth/2 - 64;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) toolsView.getLayoutParams();
        lp.height = toolsViewHeight;
        toolsView.setLayoutParams(lp);
        Log.i(TAG,"TOOLS VIEW HEIGHT: " + toolsViewHeight);




        //-----------------------------------------------
        // MARK - GALLERY BUTTON
        //-----------------------------------------------
        galleryButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                @SuppressLint("IntentReset") Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*video/*");

                Intent chooserIntent = Intent.createChooser(pickIntent, "Choose from");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
                chooserIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAXIMUM_RECORDING_TIME);
                startActivityForResult(chooserIntent, GALLERY);
            }});




        //-----------------------------------------------
        // MARK - PHOTO CAMERA BUTTON
        //-----------------------------------------------
        photoCamButt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                isPhoto = true;
                closeCamera();
                reopenCamera();
                photoCamButt.setAlpha(1);
                videoCamButt.setAlpha((float) 0.5);
                recordingTimeTxt.setVisibility(View.INVISIBLE);
                titleTxt.setText("Photo");
        }});



        //-----------------------------------------------
        // MARK - VIDEO CAMERA BUTTON
        //-----------------------------------------------
        videoCamButt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                isPhoto = false;
                closeCamera();
                reopenCamera();
                photoCamButt.setAlpha((float) 0.5);
                videoCamButt.setAlpha(1);
                recordingTimeTxt.setVisibility(View.VISIBLE);
                titleTxt.setText("Video");
                recordingTimeTxt.setText("0");
        }});





        //-----------------------------------------------
        // MARK -  TAKE PICTURE/VIDEO BUTTON
        //-----------------------------------------------
        takePicVideoButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TAKE A PICTURE
                if (isPhoto) {
                    lockFocus();

                // RECORD/STOP A VIDEO
                } else {
                    if (mIsRecordingVideo) { stopRecordingVideo();
                    } else { startRecordingVideo(); }
                }
        }});




        //-----------------------------------------------
        // MARK - SWITCH CAMERA BUTTON
        //-----------------------------------------------
        switchCameraButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCameraLensFacingDirection == CameraCharacteristics.LENS_FACING_BACK) {
                    mCameraLensFacingDirection = CameraCharacteristics.LENS_FACING_FRONT;
                    closeCamera();
                    reopenCamera();

                } else if (mCameraLensFacingDirection == CameraCharacteristics.LENS_FACING_FRONT) {
                    mCameraLensFacingDirection = CameraCharacteristics.LENS_FACING_BACK;
                    closeCamera();
                    reopenCamera();
                }
        }});




        //-----------------------------------------------
        // MARK - FLASH BUTTON
        //-----------------------------------------------
        flashButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "IS FLASH AVAILABLE: " + isFlashSupported);
                try {
                    if (mCameraId.matches(CAMERA_BACK)) {
                        if (isFlashSupported) {
                            isFlashON = !isFlashON;

                            // Set Flash OFF
                            if (isFlashON) {
                                Log.i(TAG, "FLASH IS OFF");
                                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                                flashButt.setBackgroundResource(R.drawable.flash_off);

                                // Set Flash ON
                            } else {
                                Log.i(TAG, "FLASH IS ON");
                                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
                                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                                flashButt.setBackgroundResource(R.drawable.flash_on);
                            }
                            closeCamera();
                            reopenCamera();

                        }// ./ If isFlashSupported
                    }// ./ If CAMERA_BACK

                // error
                } catch (CameraAccessException e) { e.printStackTrace(); }

        }});




        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.sqcBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
        }});



    }// ./ onCreate






    //-----------------------------------------------
    // MARK - IMAGE/VIDEO GALLERY PICKER DELEGATE
    //-----------------------------------------------
    int GALLERY = 98;
    String videoPath = null;
    Uri fileURI = null;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bitmap bm;

            if (requestCode == GALLERY) {

                // Get fileURI
                fileURI = data.getData();
                assert fileURI != null;
                String us = fileURI.toString().replace("%2F", "/");
                String uriStr = us.replace("%3A", ":");
                Log.i(TAG, "FILE URI: " + uriStr);


                //-----------------------------------------------
                // MARK - IMAGE
                //-----------------------------------------------
                if (uriStr.contains("/images/") || uriStr.contains("image") ){
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        final Bitmap scaledBm = scaleBitmapToMaxSize(640, bm);

                        showHUD(ctx);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                // Convert scaledBm to byte array
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                scaledBm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byte[] byteArray = stream.toByteArray();

                                // Pass data to the Edit Image screen
                                Intent i = new Intent(ctx, EditImage.class);
                                Bundle extras = new Bundle();
                                extras.putByteArray("image", byteArray);
                                i.putExtras(extras);
                                startActivity(i);

                                hideHUD();

                            }}, 1000); // 1 second

                    } catch (IOException e) { e.printStackTrace(); }




                    //-----------------------------------------------
                    // MARK - VIDEO
                    //-----------------------------------------------
                } else {

                    // Check if uriStr is already a Video Path string
                    if (uriStr.startsWith("file:///")) {
                        videoPath = uriStr.replace("file://", "");
                    } else {
                        videoPath = getRealPathFromURI(fileURI, ctx);
                    }

                    videoToShareURL = videoPath;
                    Log.i("log-", "VIDEO PATH: " + videoToShareURL);

                    // Check video duration
                    MediaPlayer mp = MediaPlayer.create(this, fileURI);
                    int videoDuration = mp.getDuration();
                    mp.release();
                    Log.i("log-", "VIDEO DURATION: " + videoDuration);


                    // Video duration is within the maximum recording time
                    if (videoDuration < MAXIMUM_RECORDING_TIME * 1000) {

                        // Enter Edit Image screen
                        showHUD(SquareCamera.this);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                startActivity(new Intent(ctx, EditVideo.class));
                                hideHUD();

                            }}, 500); // 1 second


                        // Video exceeds the maximum allowed duration
                    } else {
                        simpleAlert("Your video is longer than " + MAXIMUM_RECORDING_TIME +
                                " seconds. Please choose a shorter video", SquareCamera.this);

                        // Reset variables and image
                        videoPath = null;
                        fileURI = null;
                    }

                }// ./ IF

            }// ./ requestCode = GALLERY

        }// ./ resultCode = OK

    }




    //-----------------------------------------------
    // MARK - SETUP CAMERA OUTPUTS
    //-----------------------------------------------
    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs(int width, int height) {

        Log.i(TAG, "IS PHOTO: " + isPhoto);

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            assert manager != null;
            for (String cameraId : manager.getCameraIdList()) {

                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // Set front or back camera
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == mCameraLensFacingDirection) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());

                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor coordinate.
                int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
                Log.i(TAG, "DISPLAY ROTATION - SETUP CAMERA OUTPUTS: " + displayRotation);

                //noinspection ConstantConditions
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.i(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) { maxPreviewWidth = MAX_PREVIEW_WIDTH; }
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) { maxPreviewHeight = MAX_PREVIEW_HEIGHT; }

                // mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth, maxPreviewHeight, largest);
                mPreviewSize = new Size(screenWidth,screenWidth);
                Log.i(TAG, "PREVIEW W: " + mPreviewSize.getWidth());
                Log.i(TAG, "PREVIEW H: " + mPreviewSize.getHeight());


                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                }

                // VIDEO CAMERA SETUP
                if (!isPhoto) {
                    mMediaRecorder = new MediaRecorder();
                    mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                }


                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                isFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                Log.i(TAG, "CAMERA ID: " + mCameraId);

                // Show/Hide Flash button
                if (mCameraId.matches("0")) {
                    flashButt.setVisibility(View.VISIBLE);
                } else {
                    flashButt.setVisibility(View.INVISIBLE);
                }

                return;
            }

        } catch (CameraAccessException e) { e.printStackTrace();
        } catch (NullPointerException e) {
            simpleAlert("This device does not support Camera2 API.", SquareCamera.this);
        }
    }



    //-----------------------------------------------
    // MARK - OPEN CAMERA
    //-----------------------------------------------
    private void openCamera(int width, int height) {

        if (ContextCompat.checkSelfPermission(SquareCamera.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        setUpCameraOutputs(width, height);
        configureTransform(width, height);

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            assert manager != null;

            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);


        } catch (CameraAccessException e) { e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }



    //-----------------------------------------------
    // MARK - CLOSE CAMERA
    //-----------------------------------------------
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }




    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mCaptureSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }




    private String recordedVideoPath;
    private CaptureRequest.Builder mPreviewBuilder;

    //-----------------------------------------------
    // MARK - CHOOSE VIDEO SIZE
    //-----------------------------------------------
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.i(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }




    //-----------------------------------------------
    // MARK - CREATE CAMERA PREVIEW SESSION
    //-----------------------------------------------
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                // setAutoFlash(mPreviewRequestBuilder);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(SquareCamera.this, "Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    //-----------------------------------------------
    // MARK - CONFIGURE TRANSFORM
    //-----------------------------------------------
    private void configureTransform(int viewWidth, int viewHeight) {
        Log.i(TAG, "viewWidth: " + viewWidth);
        Log.i(TAG, "viewHeight: " + viewHeight);

        Activity activity = SquareCamera.this;
        if (null == mTextureView || null == mPreviewSize) { return; }

        Log.i(TAG, "PREVIEW SIZE: " + mPreviewSize);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        Log.i(TAG,"ROTATION: " + rotation);

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }



    //-----------------------------------------------
    // MARK - SETUP MEDIA RECORDER
    //-----------------------------------------------
    private void setUpMediaRecorder() throws IOException {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (recordedVideoPath == null || recordedVideoPath.isEmpty()) {
            recordedVideoPath = getVideoFilePath(this);
        }
        mMediaRecorder.setOutputFile(recordedVideoPath);
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setMaxDuration(MAXIMUM_RECORDING_TIME * 1000);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Log.i(TAG, "ROTATION - CONFIGURE TRANSFORM: " + rotation);


        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                // mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                mMediaRecorder.setOrientationHint(90);
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        Log.i(TAG, "mSensorOrientation - CONFIGURE TRANSFORM: " + mSensorOrientation);


        mMediaRecorder.prepare();
    }


    //-----------------------------------------------
    // MARK - GET VIDEO FILE PATH
    //-----------------------------------------------
    private String getVideoFilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/pikky_video")) + ".mp4";
    }



    //-----------------------------------------------
    // MARK - START RECORDING A VIDEO
    //-----------------------------------------------
    private void startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mCaptureSession = cameraCaptureSession;
                    updatePreview();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Start recording
                            mIsRecordingVideo = true;
                            mMediaRecorder.start();
                            takePicVideoButt.setBackgroundResource(R.drawable.recording_butt);

                            fireTimer();
                        }});}

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Activity activity = SquareCamera.this;
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException | IOException e) { e.printStackTrace(); }

    }



    //-----------------------------------------------
    // MARK - TIMER REPEATING WHILE RECORDING VIDEO
    //-----------------------------------------------
    CountDownTimer recTimer;
    void fireTimer() {
        final int[] t = {0};
        recTimer = new CountDownTimer(MAXIMUM_RECORDING_TIME*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                recordingTimeTxt.bringToFront();
                t[0]++;
                recordingTimeTxt.setText(String.valueOf(t[0]));

                // Stop recording when Maximum time has been reached
                if (t[0] > MAXIMUM_RECORDING_TIME) {
                    stopRecordingVideo();
                }
            }


            public void onFinish() {
                stopRecordingVideo();

            }}.start();
    }






    //-----------------------------------------------
    // MARK - STOR RECORDING VIDEO
    //-----------------------------------------------
    private void stopRecordingVideo() {
        // Stop Timer
        if(recTimer != null) {
            recTimer.cancel();
            recTimer = null;
        }

        mIsRecordingVideo = false;
        takePicVideoButt.setBackgroundResource(R.drawable.snap_photo_butt);

        // Stop recording
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }
        } catch (Exception e) { e.getMessage(); }


        Log.i(TAG, "VIDEO SAVED: " + recordedVideoPath);
        videoToShareURL = recordedVideoPath;


        // Enter Edit Image screen
        showHUD(SquareCamera.this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SquareCamera.this, EditVideo.class);
                intent.putExtra("ht", mTextureView.getHeight());
                startActivity(intent);

                recordedVideoPath = null;
                hideHUD();

            }}, 500); // 1 second

    }


    private void closePreviewSession() {
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
    }



    //-----------------------------------------------
    // MARK - LOCK THE FOCUS FOR A STILL IMAGE CAPTURE
    //-----------------------------------------------
    private void lockFocus() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = STATE_WAITING_LOCK;


            if (mCameraLensFacingDirection == 1) {
                mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
                captureStillPicture();
                Log.i(TAG, "PHOTO FROM FRONT CAMERA");

            } else if (mCameraLensFacingDirection == 0){
                mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
                captureStillPicture();
                Log.i(TAG, "PHOTO FROM BACK CAMERA");
            }

        } catch (CameraAccessException e) { e.printStackTrace(); }
    }


    private void runPrecaptureSequence() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);

        } catch (CameraAccessException e) { e.printStackTrace(); }
    }



    //-----------------------------------------------
    // MARK - UNLOCK FOCUS
    //-----------------------------------------------
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            // setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);

            // error
        } catch (CameraAccessException e) { e.printStackTrace(); }
    }



    //-----------------------------------------------
    // MARK - CAPTURE STILL PICTURE
    //-----------------------------------------------
    int rotationPic = 0;
    private void captureStillPicture() {
        try {
            if (null == mCameraDevice) {
                return;
            }

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            // captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);


            // Flash
            if (!isFlashON) {
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
                captureBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_SINGLE);
            } else {
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
                captureBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
            }

            // Orientation
            rotationPic = getWindowManager().getDefaultDisplay().getRotation();

            Log.i(TAG,"rotationpic"+rotationPic);

            Log.i(TAG,"rotationpic55 "+getOrientation(rotationPic));
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotationPic));

            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {

                    Log.i(TAG, "PICTURE SAVED IN: " + picFilePath.toString());
                    unlockFocus();

                    // Call function to get Bitmap
                    getBitmap(picFilePath.toString());
                }};

            // Reset Capture Session
            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) { e.printStackTrace(); }
    }

    // Retrieves the JPEG orientation from the specified screen rotation.
    private int getOrientation(int rotation) {
        return (DEFAULT_ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }




    //-----------------------------------------------
    // MARK - GET BITMAP FROM FILE PATH PASS IT TO EDIT IMAGE SCREEN
    //-----------------------------------------------
    public void getBitmap(String path) {
        try {
            showHUD(SquareCamera.this);

            Bitmap bm;
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bm = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            assert bm != null;
            final Bitmap scaledBm = scaleBitmapToMaxSize(1024, bm);

            Bitmap rotBm = scaledBm;

            // Rotate Bitmap 90deg on Android 7.0 and higher
            if (Build.VERSION.SDK_INT >= 24) {
                Log.i(TAG, "PICTURE ROTATED 90 DEG ON ANDROID 7.0+");
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                rotBm = Bitmap.createBitmap(scaledBm, 0, 0, scaledBm.getWidth(), scaledBm.getHeight(), matrix, true);
            }
            final Bitmap finalRotBm = rotBm;


            // Handler
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    // Convert scaledBm to byte array
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    finalRotBm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    // Pass data to the Edit Image screen
                    Intent i = new Intent(SquareCamera.this, EditImage.class);
                    Bundle extras = new Bundle();
                    extras.putByteArray("image", byteArray);
                    extras.putInt("height", mTextureView.getHeight());
                    i.putExtras(extras);
                    startActivity(i);


                    hideHUD();

                }}, 1000); // 1 second

            // error
        } catch (Exception e) { e.printStackTrace(); }
    }



    //-----------------------------------------------
    // MARK - SAVE THE JPG PICTURE IN THE ANDROID DIRECTORY
    //-----------------------------------------------
    private static class ImageSaver implements Runnable {

        private final Image mImage;
        private final File picFilePath;

        ImageSaver(Image image, File file) {
            mImage = image;
            picFilePath = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(picFilePath);
                output.write(bytes);

            } catch (IOException e) { e.printStackTrace();
            } finally {
                mImage.close();

                if (null != output) {
                    try { output.close();
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }
        }

    }


    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }




    //-----------------------------------------------
    // MARK - REOPEN CAMERA
    //-----------------------------------------------
    private void reopenCamera() {
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }



    //-----------------------------------------------
    // MARK - START/STOP BACKGROUND THREAD
    //-----------------------------------------------
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //-----------------------------------------------
    // MARK - ON RESUME | ON PAUSE | ON STOP
    //-----------------------------------------------
    @Override
    public void onResume() {
        super.onResume();

        // Start Background thread
        startBackgroundThread();

        // Reopen Camera
        reopenCamera();
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (hud != null) { hideHUD(); }
    }




}// ./ end
