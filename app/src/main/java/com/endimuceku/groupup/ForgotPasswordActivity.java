package com.endimuceku.groupup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

// Activity for resetting the user's password
public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout mEmailInput;
    private String email;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialise context and authentication
        context = getApplicationContext();
        mAuth = FirebaseAuth.getInstance();

    }

    // Method that runs when the Reset Password button is clicked, sends an email link to reset the user's password
    public void submitPasswordRequest(View view) {
        // Get input data
        mEmailInput = (TextInputLayout) findViewById(R.id.email_input_fp);
        email = mEmailInput.getEditText().getText().toString();

        // Check if the email address is valid
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // Send password reset email
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Display a message if the task is or is not successful
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "ERROR: Email not sent.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            // Display an error message if the email is not valid
            mEmailInput.setError("Invalid email address.");
        }

    }
}