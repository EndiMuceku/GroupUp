package com.endimuceku.groupup;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GroupChatMessageAdapter extends RecyclerView.Adapter<GroupChatMessageAdapter.MessageViewHolder>{

    private static final int MSG_OTHER = 0;
    private static final int MSG_USER = 1;

    private ArrayList<GroupChatMessage> groupChatMessages;

    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference ref;

    public GroupChatMessageAdapter(Context context, ArrayList<GroupChatMessage> groupChatMessages) {
        this.context = context;
        this.groupChatMessages = groupChatMessages;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_OTHER){
            view = LayoutInflater.from(context).inflate(R.layout.third_person_messages, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.first_person_messages, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        GroupChatMessage groupChatMessage = groupChatMessages.get(position);

        String message = groupChatMessage.getMessage();
        String senderID = groupChatMessage.getSender();
        String timestamp = groupChatMessage.getTimestamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String time = DateFormat.format("dd/MM/yy hh:mm aa", cal).toString();

        holder.message.setText(message);
        holder.timestamp.setText(time);

        // get sender display name
        ref = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("users");
        ref.orderByKey().equalTo(senderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String displayName = "" + ds.getValue();
                    if (!displayName.equals(user.getDisplayName())) {
                        holder.sender.setText(displayName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return groupChatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mAuth.getUid().equals(groupChatMessages.get(position).getSender())){
            return MSG_USER;
        } else {
            return MSG_OTHER;
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView sender;
        private TextView message;
        private TextView timestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.message_sender);
            message = itemView.findViewById(R.id.text_message);
            timestamp = itemView.findViewById(R.id.message_timestamp);

        }
    }


}
