package com.endimuceku.groupup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

// Activity for updating account details
public class UpdateAccountActivity extends AppCompatActivity {

    private String username;

    private TextInputLayout mUsername;

    private ImageView mImageView;
    private Uri mSelectedImage;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        // Initialise authentication and user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Load user's display name
        mUsername = (TextInputLayout) findViewById(R.id.display_name_input);
        mUsername.getEditText().setText(user.getDisplayName());

        // Set up database reference
        ref = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("users");
        ref.keepSynced(true);

    }

    // Updates the user's username when the update account details button is clicked
    public void updateAccountButtonClicked(View view) {
        // Get username from input form
        username = mUsername.getEditText().getText().toString();
        // Checks if username has been changed
        if (!username.equals(user.getDisplayName())) {
            // Change the user's username
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        ref.child(user.getUid()).setValue(username);
                    }
                }
            });

            // Display a message telling the user that their username has been changed
            Toast.makeText(this, "Username updated.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}