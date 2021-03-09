package com.endimuceku.groupup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class EventFragment extends Fragment {

    private Activity activity;
    private RecyclerView mEventGroupList;
    private Context context;

    private MaterialButton button;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    EventGroupAdapter eventGroupAdapter;

    DatabaseReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();
        context = getContext();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        ref = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("events");

        Intent startCreateEventActivityIntent = new Intent(activity, CreateEventActivity.class);

        View view = inflater.inflate(R.layout.fragment_event, container, false);
        ImageView button = (ImageView) view.findViewById(R.id.createEventImageView);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(startCreateEventActivityIntent);
            }
        });

        mEventGroupList = (RecyclerView) view.findViewById(R.id.eventRecyclerView);
        mEventGroupList.setHasFixedSize(true);
        mEventGroupList.setLayoutManager(new LinearLayoutManager(context));

        FirebaseRecyclerOptions<EventGroup> options = new FirebaseRecyclerOptions.Builder<EventGroup>().setQuery(ref, EventGroup.class).build();
        eventGroupAdapter = new EventGroupAdapter(options);
        mEventGroupList.setAdapter(eventGroupAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    @Override public void onStart()
    {
        super.onStart();
        eventGroupAdapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override public void onStop()
    {
        super.onStop();
        eventGroupAdapter.stopListening();
    }

    public class EventGroupAdapter extends FirebaseRecyclerAdapter<EventGroup, EventGroupAdapter.EventGroupViewHolder> {

        public EventGroupAdapter(@NonNull FirebaseRecyclerOptions<EventGroup> options) {
            super(options);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onBindViewHolder(@NonNull EventGroupAdapter.EventGroupViewHolder holder, int position, @NonNull EventGroup model) {
            holder.setEventTitle(model.getEventTitle());
            holder.setEventDescription(model.getEventDescription());
            holder.setEventDateAndTime(model.getEventDate(), model.getEventTime());
            holder.setLocation(model.getLocation());
            holder.setPostcode(model.getPostcode());
            holder.setAddressLine1(model.getAddressLine1());
            holder.setAddressLine2(model.getAddressLine2());
            holder.setAddressLine3(model.getAddressLine3());
            holder.setEventType(model.getEventType());

        button = holder.itemView.findViewById(R.id.join_group_button);

        Query query = ref.orderByChild("eventTitle").equalTo(model.getEventTitle());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EventGroup eg = snapshot.getChildren().iterator().next().getValue(EventGroup.class);
                if(eg.isCreator(user.getEmail())){
                    button.setText(R.string.delete_group_button);
                    button.setBackgroundColor(Color.parseColor("#e53935"));
                    button.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.delete_group_icon, 0, 0, 0);
                } else if (eg.isMember(user.getEmail())){
                    button.setText(R.string.leave_group_button);
                    button.setBackgroundColor(Color.parseColor("#e53935"));
                    button.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.leave_group_icon, 0, 0 , 0);
                } else {
                    button.setText(R.string.join_group_button);
                    button.setBackgroundColor(Color.parseColor("#4caf50"));
                    button.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.join_group_icon, 0, 0 , 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = ref.orderByChild("eventTitle").equalTo(model.getEventTitle());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        EventGroup eg = snapshot.getChildren().iterator().next().getValue(EventGroup.class);
                        String key = snapshot.getChildren().iterator().next().getKey();
                        if(eg.isCreator(user.getEmail())){
                            ref.child(key).removeValue();
                            Toast.makeText(context, "Group " + model.getEventTitle() + " deleted.", Toast.LENGTH_LONG).show();
                        } else if (eg.isMember(user.getEmail())){
                            eg.removeUser(user.getDisplayName());
                            ref.child(key).setValue(eg);
                            Toast.makeText(context, "Group " + model.getEventTitle() + " left.", Toast.LENGTH_LONG).show();
                        } else {
                            eg.addUser(user.getDisplayName(), user.getEmail());
                            ref.child(key).setValue(eg);
                            Toast.makeText(context, "Group " + model.getEventTitle() + " joined.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

            switch(model.getEventType()) {
                case "Food & Drink":
                    holder.setImage(R.drawable.food_and_drink);
                    break;
                case "Exercise":
                    holder.setImage(R.drawable.exercise);
                    break;
                case "Games":
                    holder.setImage(R.drawable.games);
                    break;
                case "Hangout":
                    holder.setImage(R.drawable.hangout);
                    break;
                case "Cinema":
                    holder.setImage(R.drawable.cinema);
                    break;
                case "Concert":
                    holder.setImage(R.drawable.concert);
                    break;
                case "Sports":
                    holder.setImage(R.drawable.sports);
                    break;
                case "Party":
                    holder.setImage(R.drawable.party);
                    break;
                case "Shopping":
                    holder.setImage(R.drawable.shopping);
                    break;
                default:
                    holder.setImage(R.drawable.other);
            }

        }

        @NonNull
        @Override
        public EventGroupAdapter.EventGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event_group, parent, false);
            return new EventGroupAdapter.EventGroupViewHolder(view);
        }

        class EventGroupViewHolder extends RecyclerView.ViewHolder {

            public EventGroupViewHolder(@NonNull View itemView){
                super(itemView);
            }

            public void setEventTitle (String eventTitle) {
                TextView eventTitleTextView = (TextView) itemView.findViewById(R.id.event_title_card_textview);
                eventTitleTextView.setText(eventTitle);
            }

            public void setEventDescription (String eventDescription) {
                TextView eventDescriptionTextView = (TextView) itemView.findViewById(R.id.event_description_card_textview);
                eventDescriptionTextView.setText(eventDescription);
            }

            public void setEventDateAndTime (String date, String time) {
                TextView dateAndTimeTextView = (TextView) itemView.findViewById(R.id.date_and_time_card_textview);
                String textToBeSet = date + ", " + time;
                dateAndTimeTextView.setText(textToBeSet);
            }

            public void setLocation (String location) {
                TextView locationTextView = (TextView) itemView.findViewById(R.id.location_card_textview);
                locationTextView.setText(location);
            }

            public void setPostcode (String postcode) {
                TextView postcodeTextView = (TextView) itemView.findViewById(R.id.postcode_card_textview);
                postcodeTextView.setText(postcode);
            }

            public void setAddressLine1 (String addressLine1){
                TextView addressLine1TextView = (TextView) itemView.findViewById(R.id.address_line_1_card_textview);
                addressLine1TextView.setText(addressLine1);
            }

            public void setAddressLine2 (String addressLine2){
                TextView addressLine2TextView = (TextView) itemView.findViewById(R.id.address_line_2_card_textview);
                addressLine2TextView.setText(addressLine2);
            }

            public void setAddressLine3 (String addressLine3){
                TextView addressLine3TextView = (TextView) itemView.findViewById(R.id.address_line_3_card_textview);
                addressLine3TextView.setText(addressLine3);
            }

            public void setEventType (String eventType){
                TextView eventTypeTextView = (TextView) itemView.findViewById(R.id.event_type_card_textview);
                eventTypeTextView.setText(eventType);
            }

            public void setImage(int image){
                ImageView imageView = (ImageView) itemView.findViewById(R.id.cardImageView);
                imageView.setImageResource(image);
            }

        }

    }

}