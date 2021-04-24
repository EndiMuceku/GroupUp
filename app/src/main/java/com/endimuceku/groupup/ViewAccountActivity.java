package com.endimuceku.groupup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Activity for viewing account details
public class ViewAccountActivity extends AppCompatActivity {

    private String email;
    private String username;

    private TextView mEmail;
    private TextView mUsername;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        // Initialise authentication and user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Load details
        mEmail = findViewById(R.id.view_account_email);
        mUsername = findViewById(R.id.view_account_display_name);

        email = "Email: " + user.getEmail();
        username = "Username: " + user.getDisplayName();

        mEmail.setText(email);
        mUsername.setText(username);

    }

    // Starts the update account activity when the change account details button is clicked
    public void changeAccountDetailsButtonClicked(View view) {
        Intent startUpdateAccountActivity = new Intent(this, UpdateAccountActivity.class);
        startActivity(startUpdateAccountActivity);
    }

    // Logs the user out of the application when the log out button is clicked
    public void logOutButtonClicked(View view) {
        mAuth.signOut();
        Intent startLogInActivity = new Intent(this, LogInActivity.class);
        startLogInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startLogInActivity);
    }

}