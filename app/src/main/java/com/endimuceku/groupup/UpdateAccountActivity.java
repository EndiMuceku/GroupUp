package com.endimuceku.groupup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.w3c.dom.Text;

public class UpdateAccountActivity extends AppCompatActivity {

    private String username;

    private TextInputLayout mUsername;

    private ImageView mImageView;
    private Uri mSelectedImage;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mUsername = (TextInputLayout) findViewById(R.id.display_name_input);
        mUsername.getEditText().setText(user.getDisplayName());

    }

    public void updateAccountButtonClicked(View view) {
        username = mUsername.getEditText().getText().toString();
        if (!username.equals(user.getDisplayName())) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();
            user.updateProfile(profileUpdates);
            Toast.makeText(this, "Username updated.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}