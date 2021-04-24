package com.endimuceku.groupup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Activity for logging in
public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputLayout mEmailInput;
    private TextInputLayout mPasswordInput;

    private String email;
    private String password;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialise authentication and context
        mAuth = FirebaseAuth.getInstance();
        context = getApplicationContext();

    }

    // Starts the create account activity when the highlighted text for creating a new account is clicked
    public void startCreateAccountActivity(View view) {
        Intent createAccountActivityIntent = new Intent(this, CreateAccountActivity.class);
        startActivity(createAccountActivityIntent);
    }

    // Starts the reset password activity when the highlighted text for resetting the user's password is clicked
    public void startForgotPasswordActivity(View view) {
        Intent forgotPasswordActivityIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotPasswordActivityIntent);
    }

    // Method that logs in the user when the log in button is pressed
    public void logInButtonClicked(View view) {
        // Load input data
        mEmailInput = (TextInputLayout) findViewById(R.id.email_input);
        email = mEmailInput.getEditText().getText().toString();

        mPasswordInput = (TextInputLayout) findViewById(R.id.password_input);
        password = mPasswordInput.getEditText().getText().toString();

        // Create new intent
        Intent eventActivityIntent = new Intent(this, MainActivity.class);

        // Check if the email address is valid
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // Sign the user in
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                // Check if the user's email is verified
                                if (user.isEmailVerified()){
                                    // Start the main activity
                                    startActivity(eventActivityIntent);
                                    finish();
                                } else {
                                    // Display a message if the user hasn't verified their email
                                    Toast.makeText(context, "Authentication failed. Please verify your email first.",
                                            Toast.LENGTH_LONG).show();
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(context, "Authentication failed. Incorrect log in details supplied.",
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        } else {
            // Display an error message if the user's email address is not valid
            mEmailInput.setError("Invalid e-mail address.");
        }

    }

}