package com.xscoder.pikky.loginSignUp.Activity;

/*==================================================
 Pikky

 © XScoder 2018
 All Rights reserved

 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
 YOU WILL BE LEGALLY PROSECUTED

==================================================*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import com.xscoder.pikky.R;
import com.xscoder.pikky.loginSignUp.ModalClasses.PasswordValidator;
import com.xscoder.pikky.setting.TermsOfUse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

public class SignUp extends AppCompatActivity {

    /* Views */
    EditText userFirstNameTxt, userLastNameTxt, userPhoneNumberTxt, userEmailTxt, userPasswordTxt;
    RadioButton agreeRadioButt;
    TextInputLayout textInputLayoutUserFirstName, textInputLayoutUserLastName, textInputLayoutUserPhoneNumber, textInputLayoutUserEmail, textInputLayoutUserPassword;
    ImageView profileImage;
    Uri uri;
    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint({"SetTextI18n", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        // Init views
        userFirstNameTxt = findViewById(R.id.userFirstNamesingup);
        userLastNameTxt = findViewById(R.id.userLastNamesingup);
        userPhoneNumberTxt = findViewById(R.id.userPhoneNumbersingup);
        userEmailTxt = findViewById(R.id.userEmailIdtxtsingup);
        userPasswordTxt = findViewById(R.id.userPasswordsignup);
        profileImage = findViewById(R.id.profile_imageView_signup);

        userFirstNameTxt.setTypeface(osRegular);
        userLastNameTxt.setTypeface(osRegular);
        userPhoneNumberTxt.setTypeface(osRegular);
        userEmailTxt.setTypeface(osRegular);
        userPasswordTxt.setTypeface(osRegular);

        //init ouBoxes of EditText
        textInputLayoutUserFirstName = findViewById(R.id.outlinedTextUserFirstNamesignup);
        textInputLayoutUserLastName = findViewById(R.id.outlinedTextuserLastNamesignup);
        textInputLayoutUserPhoneNumber = findViewById(R.id.outlinedTextFieldPhoneNumbersignup);
        textInputLayoutUserEmail = findViewById(R.id.outlinedTextFieldUserEmailIdsignup);
        textInputLayoutUserPassword = findViewById(R.id.outlinedTextuserPasswordsignup);
        removeErrorMessgae();// Removers error message if has error and user presses back focus


        agreeRadioButt = findViewById(R.id.supAgreeRadioButt);
        agreeRadioButt.setChecked(false);

        // Get App name
        TextView titleTxt = findViewById(R.id.suTitleTxt);
        titleTxt.setTypeface(osSemibold);
        titleTxt.setText("Sign up to " + getString(R.string.app_name));

        //-----------------------------------------------
        // Profile Image Click Handler
        //-----------------------------------------------

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(SignUp.this);
                } catch (Exception e) {

                }


            }
        });


        //-----------------------------------------------
        // End  Image Click Handler Code and profile picket
        //-----------------------------------------------



        //-----------------------------------------------
        // MARK - SIGN UP BUTTON
        //-----------------------------------------------
        Button signupButt = findViewById(R.id.signUpButt2);
        signupButt.setTypeface(osBold);
        signupButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userFirstNameTxt.getText().toString().matches("") || userLastNameTxt.getText().toString().matches("") ||
                        userEmailTxt.getText().toString().matches("") || userPhoneNumberTxt.getText().toString().matches("") ||
                        userPasswordTxt.getText().toString().isEmpty()
                ) {

                    //simpleAlert("You must fill all the fields to Sign Up!", SignUp.this);

                    if (userFirstNameTxt.getText().toString().isEmpty()) {
                        textInputLayoutUserFirstName.setError("First Name Required");
                        return;
                    }
                    if (userLastNameTxt.getText().toString().isEmpty()) {
                        textInputLayoutUserLastName.setError("Last Name Required");
                        return;
                    }
                    if (userPhoneNumberTxt.getText().toString().isEmpty()) {
                        textInputLayoutUserPhoneNumber.setError("Phone Number");
                        return;
                    }


                    if (userEmailTxt.getText().toString().isEmpty()) {
                        textInputLayoutUserEmail.setError("Email Required");
                        return;
                    }


                    if (userLastNameTxt.getText().toString().isEmpty()) {
                        textInputLayoutUserLastName.setError("Last Name Required");
                        return;
                    }

                    if (userPasswordTxt.getText().toString().isEmpty()) {
                        textInputLayoutUserPassword.setError("Password Required");
                        return;
                    }


                } else {

                    // All fields are filled phone
                    if (userPhoneNumberTxt.getText().toString().length() < 10) {
                        textInputLayoutUserPhoneNumber.setError("Phone Number Should be 10 digits");
                    } else {
                        // All fields are filled email validation
                        if (isValid(userEmailTxt.getText().toString())) {
                            // All fields are filled Password Validation
                            PasswordValidator pv = new PasswordValidator();
                            if (!pv.validate(userPasswordTxt.getText().toString())) {
                                textInputLayoutUserPassword.setError("Password should contain both small latter,capital latter,digit,special symbol");
                            } else {


                                if (agreeRadioButt.isChecked()) {
                                    // Everything is correct proceed with image conevrsion
                                    removeErrorMessgae();
                                    try {
                                        doSignUp(userFirstNameTxt.getText().toString(), userLastNameTxt.getText().toString(), userEmailTxt.getText().toString()
                                                , userPhoneNumberTxt.getText().toString(), userPasswordTxt.getText().toString(), "jsjjs");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    showHUD(SignUp.this);
                                    dismissKeyboard();

                                } else {
                                    removeErrorMessgae();
                                    simpleAlert("You must agree to our Terms of Use and Privacy Policy", SignUp.this);
                                }
                            }

                        } else {
                            textInputLayoutUserEmail.setError("Invalid Email");
                            return;
                        }
                    }


                   /*     final ParseUser currentUser = new ParseUser();  XCODERS CODE FOR SIGN UP
                        List<String>empty = new ArrayList<>();

                        currentUser.setUsername(usernameTxt.getText().toString());
                        currentUser.setPassword(passwordTxt.getText().toString());
                        currentUser.setEmail(emailTxt.getText().toString());
                        currentUser.put(USER_FULLNAME, fullnameTxt.getText().toString());
                        currentUser.put(USER_USERNAME_LOWERCASE, usernameTxt.getText().toString().toLowerCase());
                        currentUser.put(USER_IS_REPORTED, false);
                        currentUser.put(USER_IS_FOLLOWING, empty);
                        currentUser.put(USER_NOT_INTERESTING_FOR, empty);
                        currentUser.put(USER_MUTED_BY, empty);
                        currentUser.put(USER_IS_VERIFIED, false);
                        currentUser.put(USER_BLOCKED_BY, empty);

                        currentUser.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException error) {
                                if (error == null) {

                                    // Save default avatar
                                    Bitmap bitmap = BitmapFactory.decodeResource(SignUp.this.getResources(), R.drawable. default_avatar);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    ParseFile imageFile = new ParseFile("avatar.jpg", byteArray);
                                    currentUser.put(USER_AVATAR, imageFile);
                                    currentUser.saveInBackground();

                                    hideHUD();

                                    // Enter the to Home screen
                                    startActivity(new Intent(SignUp.this, Home.class));

                                // error
                                } else {
                                    hideHUD();
                                    simpleAlert(error.getMessage(), SignUp.this);
                                }
                            }
                        });

                         */
                    // You haven't agreed with terms of Use
                }
            }


        });


        //-----------------------------------------------
        // MARK - TERMS OF USE BUTTON
        //-----------------------------------------------
        Button touButt = findViewById(R.id.touButt);
        touButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, TermsOfUse.class));
            }
        });


        //-----------------------------------------------
        // MARK - DISMISS BUTTON
        //-----------------------------------------------
        Button dismissButt = findViewById(R.id.signupDismissButt);
        dismissButt.setTypeface(osSemibold);
        dismissButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }// ./ onCreate()


    //-----------------------------------------------
    // MARK - DISMISS KEYBOARD
    //-----------------------------------------------
    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
//        imm.hideSoftInputFromWindow(usernameTxt.getWindowToken(), 0);
//        imm.hideSoftInputFromWindow(passwordTxt.getWindowToken(), 0);
//        imm.hideSoftInputFromWindow(emailTxt.getWindowToken(), 0);
    }


    // Code by mehraj
    // Remove Error Form EditTexts
    void doLogin() {


    }


    public void removeErrorMessgae() {

        userFirstNameTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // textInputLayoutUserName.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
                    if (textInputLayoutUserFirstName.isErrorEnabled()) {
                        textInputLayoutUserFirstName.setError(null);
                    }

                } else {
                    //  textInputLayoutUserName.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                }
            }
        });

        userLastNameTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
                    if (textInputLayoutUserLastName.isErrorEnabled()) {
                        textInputLayoutUserLastName.setError(null);
                    }

                } else {
                    //  textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                }
            }
        });

        userPhoneNumberTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
                    if (textInputLayoutUserPhoneNumber.isErrorEnabled()) {
                        textInputLayoutUserPhoneNumber.setError(null);
                    }

                } else {
                    //  textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                }
            }
        });


        userEmailTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
                    if (textInputLayoutUserEmail.isErrorEnabled()) {
                        textInputLayoutUserEmail.setError(null);
                    }

                } else {
                    //  textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                }
            }
        });


        userPasswordTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
                    if (textInputLayoutUserPassword.isErrorEnabled()) {
                        textInputLayoutUserPassword.setError(null);
                    }

                } else {
                    //  textInputLayoutPassword.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                }
            }
        });

        //Remove directly

        if (textInputLayoutUserPassword.isErrorEnabled()) {
            textInputLayoutUserPassword.setError(null);
        }
        if (textInputLayoutUserEmail.isErrorEnabled()) {
            textInputLayoutUserEmail.setError(null);

        }
        if (textInputLayoutUserPhoneNumber.isErrorEnabled()) {
            textInputLayoutUserPhoneNumber.setError(null);
        }
        if (textInputLayoutUserLastName.isErrorEnabled()) {
            textInputLayoutUserLastName.setError(null);
        }
        if (textInputLayoutUserFirstName.isErrorEnabled()) {
            textInputLayoutUserFirstName.setError(null);
        }
    }

    public boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        //  super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri imageUri = CropImage.getPickImageResultUri(this, data);
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    this.uri = imageUri;

                    // requestPermissions(new String[]{Manifest.permission.C2D_MESSAGE});
                }


                CropImage.activity(imageUri).start(this);
            }
        }

        //Crop result
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                profileImage.setImageURI(uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    void doSignUp(String userFname, String userLname, String userEmail, String userPhone, String userPassword, String userProfileBase64) throws IOException {
        com.xscoder.pikky.loginSignUp.Operations.SignUp signUp = new com.xscoder.pikky.loginSignUp.Operations.SignUp(this);
        signUp.doSignUp(userFname, userLname, userEmail, userPhone, userPassword, convertImageToStringForServer(uri));

    }


// Image Converter

    public String convertImageToStringForServer(Uri imageBitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (imageBitmap != null) {
            Bitmap image = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            image.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            return null;
        }
    }

    //End Code by Mehraj


} // @end
