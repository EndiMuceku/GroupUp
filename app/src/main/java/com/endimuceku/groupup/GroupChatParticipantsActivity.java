package com.endimuceku.groupup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.NoSuchElementException;

// Activity for displaying group chat participants
public class GroupChatParticipantsActivity extends AppCompatActivity {

    private String eventKey;
    private Toolbar toolbar;
    private ImageView groupIcon;
    private ImageView backIcon;
    private TextView groupTitle;

    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private RecyclerView mParticipants;
    private Context context;

    private ArrayList<String> userIDs;
    private ArrayList<String> displayNames;
    private ParticipantAdapter participantAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_group_chat_participants);

        // Initialise context
        context = getApplicationContext();

        // Get the intent and Firebase Realtime Database event key
        Intent intent = getIntent();
        eventKey = intent.getStringExtra("eventKey");

        // Initialise authentication and user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Initialise arrays
        userIDs = new ArrayList<>();
        displayNames = new ArrayList<>();

        // Set up database reference
        ref = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("events");

        // Load displayed components
        toolbar = (Toolbar) findViewById(R.id.group_chat_participants_toolbar);
        groupIcon = (ImageView) findViewById(R.id.groupChatParticipantsIcon);
        backIcon = (ImageView) findViewById(R.id.back_icon_2);
        groupTitle = (TextView) findViewById(R.id.group_chat_participants_textview);

        // Initialise recyclerview
        mParticipants = (RecyclerView) findViewById(R.id.participants_recyclerview);
        mParticipants.setHasFixedSize(true);
        mParticipants.setLayoutManager(new LinearLayoutManager(context));

        // Set the group chat title and icon
        ref.orderByKey().equalTo(eventKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // try/catch statement to prevent application crash
                try {
                    EventGroup eg = snapshot.getChildren().iterator().next().getValue(EventGroup.class);
                    groupTitle.setText(eg.getEventTitle());

                    switch (eg.getEventType()) {
                        case "Food & Drink":
                            groupIcon.setImageResource(R.drawable.food_and_drink);
                            break;
                        case "Exercise":
                            groupIcon.setImageResource(R.drawable.exercise);
                            break;
                        case "Games":
                            groupIcon.setImageResource(R.drawable.games);
                            break;
                        case "Hangout":
                            groupIcon.setImageResource(R.drawable.hangout);
                            break;
                        case "Cinema":
                            groupIcon.setImageResource(R.drawable.cinema);
                            break;
                        case "Concert":
                            groupIcon.setImageResource(R.drawable.concert);
                            break;
                        case "Sports":
                            groupIcon.setImageResource(R.drawable.sports);
                            break;
                        case "Party":
                            groupIcon.setImageResource(R.drawable.party);
                            break;
                        case "Shopping":
                            groupIcon.setImageResource(R.drawable.shopping);
                            break;
                        default:
                            groupIcon.setImageResource(R.drawable.other);
                    }



                } catch (NoSuchElementException e) {
                    Log.d("Catch: ", "NoSuchElementException triggered.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Load messages from database and display them on the recyclerview
        ref.child(eventKey).child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIDs.clear();
                displayNames.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String userID = (String) ds.getKey();
                    String displayName = (String) ds.getValue();
                    userIDs.add(userID);
                    displayNames.add(displayName);
                }
                participantAdapter = new ParticipantAdapter(GroupChatParticipantsActivity.this, userIDs, displayNames, eventKey);
                mParticipants.setAdapter(participantAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Method which returns user to previous screen when back button is clicked
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}