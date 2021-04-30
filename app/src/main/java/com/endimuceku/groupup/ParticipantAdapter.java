package com.endimuceku.groupup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private ArrayList<String> userIDs;
    private ArrayList<String> displayNames;

    private String eventKey;

    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference ref;

    public ParticipantAdapter(Context context, ArrayList<String> userIDs, ArrayList<String> displayNames, String eventKey) {
        this.context = context;
        this.userIDs = userIDs;
        this.displayNames = displayNames;
        this.eventKey = eventKey;

        // Initialise authentication and user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Set up database reference
        ref = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("events");
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.card_participants, parent, false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        // Load emails and passwords
        String userID = userIDs.get(position);
        String displayName = displayNames.get(position);

        // Bind to card
        holder.participant.setText(displayName);

        // Set the group chat title and icon
        ref.orderByKey().equalTo(eventKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // try/catch statement to prevent application crash
                try {
                    EventGroup eg = snapshot.getChildren().iterator().next().getValue(EventGroup.class);
                    if (eg.isCreator(user.getUid())) {
                        if (!eg.isCreator(userID)) {
                            holder.kickButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ref.child(eventKey).child("users").child(userID).removeValue();
                                    Toast.makeText(context, "User " + displayName + " kicked from event.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            holder.creator.setText(R.string.creator);
                            holder.kickButton.setVisibility(View.GONE);
                        }
                    } else {
                        holder.kickButton.setVisibility(View.GONE);
                        if (eg.isCreator(userID)) {
                            holder.creator.setText(R.string.creator);
                        }
                    }

                } catch (NoSuchElementException e) {
                    Log.d("Catch: ", "NoSuchElementException triggered.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userIDs.size();
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder {

        private TextView participant;
        private TextView creator;

        private MaterialButton kickButton;

        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            participant = itemView.findViewById(R.id.participant_name);
            creator = itemView.findViewById(R.id.creator);
            kickButton = itemView.findViewById(R.id.kick_button);
        }
    }
}
