package com.xscoder.pikky.Massiging;

/*==================================================
 Pikky

 © XScoder 2018
 All Rights reserved

 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
 YOU WILL BE LEGALLY PROSECUTED

======================================================*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xscoder.pikky.Massiging.Messages;
import com.xscoder.pikky.R;

import java.util.List;
import java.util.Objects;

import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BLOCKED_BY;
import static com.xscoder.pikky.Configurations.USER_CLASS_NAME;
import static com.xscoder.pikky.Configurations.USER_FULLNAME;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.USER_USERNAME_LOWERCASE;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.osSemibold;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewMessage extends AppCompatActivity {

    /*--- VIEWS ---*/
    EditText searchEditText;
    ListView usersListView;
    Button backButt, cancelButt;



    /*--- VARIABLES ---*/
    Context ctx = this;
    List<ParseUser>usersArray;





    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_message);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALZIE VIEWS
        //-----------------------------------------------
        backButt = findViewById(R.id.nmessBackButt);
        usersListView = findViewById(R.id.nmessUsersListView);
        searchEditText = findViewById(R.id.nmessSearchEditText);
        searchEditText.setTypeface(osRegular);
        cancelButt = findViewById(R.id.nmessCancelButt);
        cancelButt.setTypeface(osSemibold);



        //-----------------------------------------------
        // MARK - EDIT TEXT WATCHER
        //-----------------------------------------------
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    cancelButt.setVisibility(View.VISIBLE);
                }
            }

            @Override public void afterTextChanged(Editable editable) { }
        });


        //-----------------------------------------------
        // MARK - SEARCH BY KEYWORDS
        //-----------------------------------------------
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (!searchEditText.getText().toString().matches("")) {
                        String uLowercase = searchEditText.getText().toString().toLowerCase();
                        queryUsers(uLowercase);
                        dismissKeyboard();
                    }

                    return true;
                } return false;
        }});


        //-----------------------------------------------
        // MARK - CANCEL BUTTON
        //-----------------------------------------------
        cancelButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              searchEditText.setText("");
              cancelButt.setVisibility(View.INVISIBLE);
              dismissKeyboard();
        }});



        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});



    }// ./ onCreate





    // ------------------------------------------------
    // MARK: - QUERY USERS
    // ------------------------------------------------
    void queryUsers(String searchStr) {
        showHUD(ctx);
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseUser>query = ParseQuery.getQuery(USER_CLASS_NAME);
        query.whereContains(USER_USERNAME_LOWERCASE, searchStr);
        query.whereNotEqualTo(USER_USERNAME, currentUser.getString(USER_USERNAME));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    hideHUD();
                    usersArray = users;
                    showDataInListView();

            // error
            } else {
                hideHUD();
                simpleAlert(e.getMessage(), ctx);
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
                     cell = inflater.inflate(R.layout.cell_instant, null);
                 }

                 // Parse User
                 ParseUser uObj = usersArray.get(position);

                 //-----------------------------------------------
                 // MARK - INITIALIZE VIEWS
                 //-----------------------------------------------
                 final CircleImageView avatarImg = cell.findViewById(R.id.cinstAvatarImg);
                 final TextView usernameTxt = cell.findViewById(R.id.cinstUsernameTxt);
                 usernameTxt.setTypeface(osBold);
                 TextView lastMessageTxt = cell.findViewById(R.id.cinstLastMessageTxt);
                 lastMessageTxt.setTypeface(osRegular);

                 // Username
                 usernameTxt.setText(uObj.getString(USER_USERNAME));

                 // Full Name
                 lastMessageTxt.setText(uObj.getString(USER_FULLNAME));

                 // Avatar
                 getParseImage(avatarImg, uObj, USER_AVATAR);


             return cell;
             }
             @Override public int getCount() { return usersArray.size(); }
             @Override public Object getItem(int position) { return usersArray.get(position); }
             @Override public long getItemId(int position) { return position; }
         }

         // Set Adapter
         usersListView.setAdapter(new ListAdapter(ctx));



         //-----------------------------------------------
         // MARK - START CHATTING WITH SELECTED USER
         //-----------------------------------------------
         usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                 // Parse User
                 ParseUser uObj = usersArray.get(position);
                 List<String>blockedBy = uObj.getList(USER_BLOCKED_BY);
                 ParseUser currentUser = ParseUser.getCurrentUser();

                 assert blockedBy != null;
                 if (blockedBy.contains(currentUser.getObjectId()) ){
                     simpleAlert("@" + uObj.getString(USER_USERNAME) + " has blocked you!", ctx);

                 } else {
                     Intent i = new Intent(ctx, Messages.class);
                     Bundle extras = new Bundle();
                     extras.putString("userID", uObj.getObjectId());
                     i.putExtras(extras);
                     startActivity(i);
                 }

         }});
    }





    //-----------------------------------------------
    // MARK - DIMSISS KEYBOARD
    //-----------------------------------------------
    void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

}// ./ end
