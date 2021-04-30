package com.endimuceku.groupup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

// Fragment for the events tab of the main activity
public class EventFragment extends Fragment {

    private Activity activity;
    private RecyclerView mEventGroupList;
    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseRecyclerOptions<EventGroup> options;
    private EventGroupAdapter eventGroupAdapter;
    private DatabaseReference ref;

    private MaterialButton joinEventButton;
    private MaterialButton leaveEventButton;
    private MaterialButton deleteEventButton;

    private SearchView searchView;
    private AutoCompleteTextView eventType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialise activity, context, authentication and user
        activity = getActivity();
        context = getContext();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Set up database reference
        ref = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("events");

        // Initialise intent and view
        Intent startCreateEventActivityIntent = new Intent(activity, CreateEventActivity.class);
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        // Create a clickable icon which leads to the activity for creating new events
        ImageView createEventIcon = (ImageView) view.findViewById(R.id.createEventImageView);
        createEventIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(startCreateEventActivityIntent);
            }
        });

        // Initialise recyclerview
        mEventGroupList = (RecyclerView) view.findViewById(R.id.eventRecyclerView);
        mEventGroupList.setHasFixedSize(true);
        mEventGroupList.setLayoutManager(new LinearLayoutManager(context));

        // Set recyclerview adapter
        options = new FirebaseRecyclerOptions.Builder<EventGroup>().setQuery(ref, EventGroup.class).build();
        eventGroupAdapter = new EventGroupAdapter(options);
        mEventGroupList.setAdapter(eventGroupAdapter);

        // Create a search bar which updates a query when text is typed into it
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }

            @Override
            public boolean onQueryTextChange(String query) {
                search(query);
                return true;
            }
        });

        // Initialise event type drop down filter
        eventType = (AutoCompleteTextView) view.findViewById(R.id.event_type_filter_ed);

        String[] dropDownOptions =
                {"All Events", "Food & Drink", "Exercise", "Games", "Hangout",
                        "Cinema", "Concert", "Sports", "Party", "Shopping", "Other"};

        ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, dropDownOptions);

        eventType.setAdapter(adapter);
        eventType.setThreshold(1);

        // Update a query when a drop down option is selected
        eventType.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Query query;
                if(eventType.getAdapter().getItem(position).equals("All Events")) {
                    query = ref;
                } else {
                    query = ref.orderByChild("eventType").equalTo(eventType.getText().toString());
                }

                FirebaseRecyclerOptions<EventGroup> filterOptions = new FirebaseRecyclerOptions.Builder<EventGroup>()
                        .setQuery(query, EventGroup.class)
                        .setLifecycleOwner(getViewLifecycleOwner())
                        .build();

                // Reset adapter with new query
                EventGroupAdapter eventGroupSearchAdapter = new EventGroupAdapter(filterOptions);
                mEventGroupList.setAdapter(eventGroupSearchAdapter);
                eventGroupSearchAdapter.notifyDataSetChanged();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // Updates the list of events to match the search term
    private void search(String s) {
        Log.d("Search term:", s);

        // Check if search query is empty
        Query query;
        if(!s.isEmpty()){
            query = ref.orderByChild("eventTitle").startAt(s).endAt(s + "\uf8ff");
        } else {
            query = ref;
        }

        FirebaseRecyclerOptions<EventGroup> searchOptions = new FirebaseRecyclerOptions.Builder<EventGroup>()
                .setQuery(query, EventGroup.class)
                .setLifecycleOwner(this)
                .build();

        // Reset adapter with new query
        EventGroupAdapter eventGroupSearchAdapter = new EventGroupAdapter(searchOptions);
        mEventGroupList.setAdapter(eventGroupSearchAdapter);
        eventGroupSearchAdapter.notifyDataSetChanged();
    }

    // Tell the app to start getting data from the database on activity start
    @Override
    public void onStart() {
        super.onStart();
        eventGroupAdapter.startListening();
    }

    // Tell the app to stop getting data from the database on activity stop
    @Override
    public void onStop() {
        super.onStop();
        eventGroupAdapter.stopListening();
    }

    // Adapter for event data from the database
    public class EventGroupAdapter extends FirebaseRecyclerAdapter<EventGroup, EventGroupAdapter.EventGroupViewHolder> {

        public EventGroupAdapter(@NonNull FirebaseRecyclerOptions<EventGroup> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull EventGroupAdapter.EventGroupViewHolder holder, int position, @NonNull EventGroup model) {
            // Update card with information from the database
            holder.setEventTitle(model.getEventTitle());
            holder.setEventDescription(model.getEventDescription());
            holder.setEventDateAndTime(model.getEventDate(), model.getEventTime());
            holder.setLocation(model.getLocation());
            holder.setPostcode(model.getPostcode());
            holder.setAddressLine1(model.getAddressLine1());
            holder.setAddressLine2(model.getAddressLine2());
            holder.setAddressLine3(model.getAddressLine3());
            holder.setEventType(model.getEventType());

            // Initialise event buttons
            joinEventButton = holder.itemView.findViewById(R.id.join_group_button);
            leaveEventButton = holder.itemView.findViewById(R.id.leave_group_button);
            deleteEventButton = holder.itemView.findViewById(R.id.delete_group_button);

            // Join the event when the button is clicked
            joinEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Query query = ref.orderByChild("eventTitle").equalTo(model.getEventTitle());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            EventGroup eg = snapshot.getChildren().iterator().next().getValue(EventGroup.class);
                            String key = snapshot.getChildren().iterator().next().getKey();
                            if (eg.isMember(user.getUid())) {
                                Toast.makeText(context, "You have already joined this group.", Toast.LENGTH_SHORT).show();
                            } else {
                                Map<String, String> hashMap = eg.getUsers();
                                hashMap.put(user.getUid(), user.getDisplayName());
                                ref.child(key).child("users").setValue(hashMap);
                                Toast.makeText(context, "Group " + model.getEventTitle() + " joined.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            });

            // Leave the event when the button is clicked
            leaveEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Query query = ref.orderByChild("eventTitle").equalTo(model.getEventTitle());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            EventGroup eg = snapshot.getChildren().iterator().next().getValue(EventGroup.class);
                            String key = snapshot.getChildren().iterator().next().getKey();
                            if (eg.isCreator(user.getUid())) {
                                Toast.makeText(context, "A group owner cannot leave a group, they can only delete it.", Toast.LENGTH_SHORT).show();
                            } else if (eg.isMember(user.getUid())) {
                                ref.child(key).child("users").child(user.getUid()).removeValue();
                                Toast.makeText(context, "Group " + model.getEventTitle() + " left.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "You cannot leave a group you haven't joined.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            });

            // Delete the event when the button is clicked
            deleteEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Query query = ref.orderByChild("eventTitle").equalTo(model.getEventTitle());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            EventGroup eg = snapshot.getChildren().iterator().next().getValue(EventGroup.class);
                            String key = snapshot.getChildren().iterator().next().getKey();
                            if (eg.isCreator(user.getUid())) {
                                ref.child(key).removeValue();
                                Toast.makeText(context, "Group " + model.getEventTitle() + " deleted.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Only the owner can delete a group.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            });

            // Update card image based on event type
            switch (model.getEventType()) {
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
            // Get card layout from xml file
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event_group, parent, false);
            return new EventGroupAdapter.EventGroupViewHolder(view);
        }

        class EventGroupViewHolder extends RecyclerView.ViewHolder {

            public EventGroupViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            // Setters
            public void setEventTitle(String eventTitle) {
                TextView eventTitleTextView = (TextView) itemView.findViewById(R.id.event_title_card_textview);
                eventTitleTextView.setText(eventTitle);
            }

            public void setEventDescription(String eventDescription) {
                TextView eventDescriptionTextView = (TextView) itemView.findViewById(R.id.event_description_card_textview);
                eventDescriptionTextView.setText(eventDescription);
            }

            public void setEventDateAndTime(String date, String time) {
                TextView dateAndTimeTextView = (TextView) itemView.findViewById(R.id.date_and_time_card_textview);
                String textToBeSet = date + ", " + time;
                dateAndTimeTextView.setText(textToBeSet);
            }

            public void setLocation(String location) {
                TextView locationTextView = (TextView) itemView.findViewById(R.id.location_card_textview);
                locationTextView.setText(location);
            }

            public void setPostcode(String postcode) {
                TextView postcodeTextView = (TextView) itemView.findViewById(R.id.postcode_card_textview);
                postcodeTextView.setText(postcode);
            }

            public void setAddressLine1(String addressLine1) {
                TextView addressLine1TextView = (TextView) itemView.findViewById(R.id.address_line_1_card_textview);
                addressLine1TextView.setText(addressLine1);
            }

            public void setAddressLine2(String addressLine2) {
                TextView addressLine2TextView = (TextView) itemView.findViewById(R.id.address_line_2_card_textview);
                addressLine2TextView.setText(addressLine2);
            }

            public void setAddressLine3(String addressLine3) {
                TextView addressLine3TextView = (TextView) itemView.findViewById(R.id.address_line_3_card_textview);
                addressLine3TextView.setText(addressLine3);
            }

            public void setEventType(String eventType) {
                TextView eventTypeTextView = (TextView) itemView.findViewById(R.id.event_type_card_textview);
                eventTypeTextView.setText(eventType);
            }

            public void setImage(int image) {
                ImageView imageView = (ImageView) itemView.findViewById(R.id.cardImageView);
                imageView.setImageResource(image);
            }

        }

    }

}