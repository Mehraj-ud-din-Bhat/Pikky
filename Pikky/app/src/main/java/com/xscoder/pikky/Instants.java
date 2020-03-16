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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xscoder.pikky.Massiging.Messages;
import com.xscoder.pikky.Massiging.NewMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import static com.xscoder.pikky.Configurations.INSTANTS_CLASS_NAME;
import static com.xscoder.pikky.Configurations.INSTANTS_ID;
import static com.xscoder.pikky.Configurations.INSTANTS_RECEIVER;
import static com.xscoder.pikky.Configurations.INSTANTS_SENDER;
import static com.xscoder.pikky.Configurations.INSTANTS_UPDATED_AT;
import static com.xscoder.pikky.Configurations.USER_AVATAR;
import static com.xscoder.pikky.Configurations.USER_BLOCKED_BY;
import static com.xscoder.pikky.Configurations.USER_CLASS_NAME;
import static com.xscoder.pikky.Configurations.USER_USERNAME;
import static com.xscoder.pikky.Configurations.getParseImage;
import static com.xscoder.pikky.Configurations.hideHUD;
import static com.xscoder.pikky.Configurations.osBold;
import static com.xscoder.pikky.Configurations.osRegular;
import static com.xscoder.pikky.Configurations.showHUD;
import static com.xscoder.pikky.Configurations.simpleAlert;
import static com.xscoder.pikky.Configurations.timeAgoSinceDate;

import de.hdodenhof.circleimageview.CircleImageView;

public class Instants extends AppCompatActivity {

    /*--- VIEWS ---*/
    Button backButt, newMessageButt;
    TextView noResultsTxt;
    ListView instantsListView;



    /*--- VARIABLES ---*/
    Context ctx = this;
    List<ParseObject> instantsArray = new ArrayList<>();
    int skip = 0;




    //-----------------------------------------------
    // MARK - ON START
    //-----------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();

        skip = 0;
        instantsArray = new ArrayList<>();

        // Call query
        queryInstants();
    }




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instants);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();


        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        backButt = findViewById(R.id.instBackButt);
        newMessageButt = findViewById(R.id.instNewMessageButt);
        noResultsTxt = findViewById(R.id.instNoresultsTxt);
        noResultsTxt.setTypeface(osBold);
        instantsListView = findViewById(R.id.instInstantsListView);




        //-----------------------------------------------
        // MARK - NEW MESSAGE BUTTON
        //-----------------------------------------------
        newMessageButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(ctx, NewMessage.class));
        }});



        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }// ./ onCreate()






    // ------------------------------------------------
    // MARK: - QUERY INSTANTS
    // ------------------------------------------------
    void queryInstants() {
        instantsArray = new ArrayList<>();
        instantsListView.invalidateViews();
        instantsListView.refreshDrawableState();
        showHUD(ctx);
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Query
        ParseQuery<ParseObject>query = ParseQuery.getQuery(INSTANTS_CLASS_NAME);
        query.include(USER_CLASS_NAME);
        query.whereContains(INSTANTS_ID, currentUser.getObjectId());
        query.orderByDescending(INSTANTS_UPDATED_AT);
        query.setSkip(skip);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    hideHUD();

                    instantsArray.addAll(objects);
                    if (objects.size() == 100) {
                        skip = skip + 100;
                        queryInstants();
                    }

                    if (instantsArray.size() == 0) {
                        noResultsTxt.setVisibility(View.VISIBLE);
                    } else {
                        noResultsTxt.setVisibility(View.INVISIBLE);
                        showDataInListView();
                    }

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
             @SuppressLint({"SetTextI18n", "InflateParams"})
             @Override
             public View getView(int position, View cell, ViewGroup parent) {
                 if (cell == null) {
                     LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                     assert inflater != null;
                     cell = inflater.inflate(R.layout.cell_instant, null);
                 }

                 //-----------------------------------------------
                 // MARK - INITIALIZE VIEWS
                 //-----------------------------------------------
                 final CircleImageView avatarImg = cell.findViewById(R.id.cinstAvatarImg);
                 final TextView usernameTxt = cell.findViewById(R.id.cinstUsernameTxt);
                 usernameTxt.setTypeface(osBold);
                 TextView lastMessageTxt = cell.findViewById(R.id.cinstLastMessageTxt);
                 lastMessageTxt.setTypeface(osRegular);

                 // Parse Object
                 final ParseObject iObj = instantsArray.get(position);

                 // currentUser
                 final ParseUser currentUser = ParseUser.getCurrentUser();

                 // Date
                 lastMessageTxt.setText("Last message: " + timeAgoSinceDate(iObj.getUpdatedAt()));


                 // senderUser
                 Objects.requireNonNull(iObj.getParseObject(INSTANTS_SENDER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                     public void done(final ParseObject senderUser, ParseException e) {

                         // receiverUser
                         Objects.requireNonNull(iObj.getParseObject(INSTANTS_RECEIVER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                             public void done(ParseObject receiverUser, ParseException e) {

                                 // Avatar Image of the User you're chatting with
                                 if (senderUser.getObjectId().matches(currentUser.getObjectId())) {
                                     getParseImage(avatarImg, receiverUser, USER_AVATAR);
                                     usernameTxt.setText(receiverUser.getString(USER_USERNAME));
                                 } else {
                                     getParseImage(avatarImg, senderUser, USER_AVATAR);
                                     usernameTxt.setText(senderUser.getString(USER_USERNAME));
                                 }

                         }});// ./ receiverUser

                  }});// ./ senderUser



             return cell;
             }
             @Override public int getCount() { return instantsArray.size(); }
             @Override public Object getItem(int position) { return instantsArray.get(position); }
             @Override public long getItemId(int position) { return position; }
         }

         // Set Adapter
         instantsListView.setAdapter(new ListAdapter(ctx));


         //-----------------------------------------------
         // MARK - CHAT WITH USER
         //-----------------------------------------------
         instantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                 // Parse Object
                 final ParseObject iObj = instantsArray.get(position);
                 // currentUser
                 final ParseUser currentUser = ParseUser.getCurrentUser();


                 // senderUser
                 Objects.requireNonNull(iObj.getParseObject(INSTANTS_SENDER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                     public void done(final ParseObject senderUser, ParseException e) {

                         // receiverUser
                         Objects.requireNonNull(iObj.getParseObject(INSTANTS_RECEIVER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                             public void done(final ParseObject receiverUser, ParseException e) {
                                 if (e == null) {

                                     List<String> blockedBy;
                                     Intent i = new Intent(ctx, Messages.class);
                                     Bundle extras = new Bundle();

                                     // Chat with receiverUser
                                     if (senderUser.getObjectId().matches(currentUser.getObjectId())) {
                                         extras.putString("userID", receiverUser.getObjectId());

                                         blockedBy = senderUser.getList(USER_BLOCKED_BY);
                                         assert blockedBy != null;
                                         if (blockedBy.contains(receiverUser.getObjectId())) {
                                             simpleAlert("@" + receiverUser.getString(USER_USERNAME) + " has blocked you!", ctx);
                                         } else {
                                             i.putExtras(extras);
                                             startActivity(i);
                                         }

                                     // Chat with senderUser
                                     } else {
                                         extras.putString("userID", senderUser.getObjectId());
                                         blockedBy = receiverUser.getList(USER_BLOCKED_BY);
                                         assert blockedBy != null;
                                         if (blockedBy.contains(senderUser.getObjectId())) {
                                             simpleAlert("@" + senderUser.getString(USER_USERNAME) + " has blocked you!", ctx);
                                         } else {
                                             i.putExtras(extras);
                                             startActivity(i);
                                         }
                                     }

                                 // error
                                 } else { simpleAlert(e.getMessage(), ctx);
                         }}});// ./ receiverUser

                 }});// ./ senderUser

         }});

    }




}// ./ end
