package com.endimuceku.groupup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupFragment extends Fragment {

    private Activity activity;
    private Context context;
    private RecyclerView mEventGroupChats;

    private ImageView accountSettingsIcon;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseRecyclerOptions<EventGroup> options;
    private GroupChatAdapter groupChatAdapter;
    private DatabaseReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();
        context = getContext();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        accountSettingsIcon = (ImageView) view.findViewById(R.id.accountSettingsIcon);
        accountSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startViewAccountActivityIntent = new Intent(activity, ViewAccountActivity.class);
                startActivity(startViewAccountActivityIntent);
            }
        });

        ref = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("events");

        mEventGroupChats = (RecyclerView) view.findViewById(R.id.groupChatRecyclerView);
        mEventGroupChats.setHasFixedSize(true);
        mEventGroupChats.setLayoutManager(new LinearLayoutManager(context));

        options = new FirebaseRecyclerOptions.Builder<EventGroup>().setQuery(ref, EventGroup.class).build();
        groupChatAdapter = new GroupChatAdapter(options);
        mEventGroupChats.setAdapter(groupChatAdapter);

        return view;
    }

    // Tell the app to start getting data from the database on activity start
    @Override
    public void onStart() {
        super.onStart();
        groupChatAdapter.startListening();
    }

    // Tell the app to stop getting data from the database on activity stop
    @Override
    public void onStop() {
        super.onStop();
        groupChatAdapter.stopListening();
    }

    public class GroupChatAdapter extends FirebaseRecyclerAdapter<EventGroup, GroupChatAdapter.EventGroupViewHolder> {

        private ArrayList<EventGroup> eventGroupList;

        public GroupChatAdapter(@NonNull FirebaseRecyclerOptions<EventGroup> options) {
            super(options);
            this.eventGroupList = eventGroupList;
        }

        @Override
        protected void onBindViewHolder(@NonNull GroupChatAdapter.EventGroupViewHolder holder, int position, @NonNull EventGroup model) {
            if (!model.isMember(user.getUid())) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                holder.setEventTitle(model.getEventTitle());

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

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, GroupChatActivity.class);

                        Query query = ref.orderByChild("eventTitle").equalTo(model.getEventTitle());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                EventGroup eg = snapshot.getChildren().iterator().next().getValue(EventGroup.class);
                                String key = snapshot.getChildren().iterator().next().getKey();
                                intent.putExtra("eventKey", key);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

            }
        }

        @NonNull
        @Override
        public GroupChatAdapter.EventGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_group_chat, parent, false);
            return new GroupChatAdapter.EventGroupViewHolder(view);
        }

        class EventGroupViewHolder extends RecyclerView.ViewHolder {

            public EventGroupViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void setEventTitle(String eventTitle) {
                TextView eventTitleTextView = (TextView) itemView.findViewById(R.id.group_chat_title);
                eventTitleTextView.setText(eventTitle);
            }

            public void setImage(int image) {
                ImageView imageView = (ImageView) itemView.findViewById(R.id.groupIconView);
                imageView.setImageResource(image);
            }

        }

    }

}