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

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mEmail = findViewById(R.id.view_account_email);
        mUsername = findViewById(R.id.view_account_display_name);

        email = "Email: " + user.getEmail();
        username = "Username: " + user.getDisplayName();

        mEmail.setText(email);
        mUsername.setText(username);

    }

    public void changeAccountDetailsButtonClicked(View view) {
        Intent startUpdateAccountActivity = new Intent(this, UpdateAccountActivity.class);
        startActivity(startUpdateAccountActivity);
    }

    public void logOutButtonClicked(View view) {
        mAuth.signOut();
        Intent startLogInActivity = new Intent(this, LogInActivity.class);
        startLogInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startLogInActivity);
    }


    public void deleteAccountButtonClicked(View view) {
        Intent startLogInActivity = new Intent(this, LogInActivity.class);
        startLogInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startLogInActivity);
    }
}