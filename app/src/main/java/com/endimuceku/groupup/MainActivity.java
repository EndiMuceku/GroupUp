package com.endimuceku.groupup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputLayout mEmailInput;
    private TextInputLayout mPasswordInput;

    private String email;
    private String password;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        context = getApplicationContext();

    }

    public void startCreateAccountActivity(View view) {
        Intent createAccountActivityIntent = new Intent(this, CreateAccountActivity.class);
        startActivity(createAccountActivityIntent);
    }

    public void startForgotPasswordActivity(View view) {
        Intent forgotPasswordActivityIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotPasswordActivityIntent);
    }

    public void logInButtonClicked(View view) {
        mEmailInput = (TextInputLayout) findViewById(R.id.email_input);
        email = mEmailInput.getEditText().getText().toString();

        mPasswordInput = (TextInputLayout) findViewById(R.id.password_input);
        password = mPasswordInput.getEditText().getText().toString();

        Intent eventActivityIntent = new Intent(this, EventActivity.class);

        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user.isEmailVerified()){
                                    startActivity(eventActivityIntent);
                                    finish();
                                } else {
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
            mEmailInput.setError("Invalid e-mail address.");
        }

    }

}