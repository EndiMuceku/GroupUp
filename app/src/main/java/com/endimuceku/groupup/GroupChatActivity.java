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
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private String eventKey;
    private Toolbar toolbar;
    private ImageView groupIcon;
    private ImageView sendMessageIcon;
    private TextView groupTitle;
    private EditText messageText;

    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private RecyclerView mGroupMessages;
    private Context context;

    private ArrayList<GroupChatMessage> groupChatMessages;
    private GroupChatMessageAdapter groupChatMessageAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_group_chat);

        context = getApplicationContext();

        Intent intent = getIntent();
        eventKey = intent.getStringExtra("eventKey");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        groupChatMessages = new ArrayList<>();

        ref = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("events");

        toolbar = (Toolbar) findViewById(R.id.group_chat_toolbar);
        groupIcon = (ImageView) findViewById(R.id.groupChatIcon);
        sendMessageIcon = (ImageView) findViewById(R.id.sendMessageIcon);
        groupTitle = (TextView) findViewById(R.id.group_chat_textview);
        messageText = (EditText) findViewById(R.id.editTextMessage);

        mGroupMessages = (RecyclerView) findViewById(R.id.message_recyclerview);
        mGroupMessages.setHasFixedSize(true);
        mGroupMessages.setLayoutManager(new LinearLayoutManager(context));

        ref.orderByKey().equalTo(eventKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child(eventKey).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatMessages.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    GroupChatMessage msg = ds.getValue(GroupChatMessage.class);
                    groupChatMessages.add(msg);
                }
                groupChatMessageAdapter = new GroupChatMessageAdapter(GroupChatActivity.this, groupChatMessages);
                mGroupMessages.setAdapter(groupChatMessageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendMessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = messageText.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    // Implement Message Storing
                    String timestamp = Long.toString(System.currentTimeMillis());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", user.getUid());
                    hashMap.put("message", text);
                    hashMap.put("timestamp", timestamp);

                    ref.child(eventKey).child("messages").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            messageText.setText("");
                        }
                    });

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}