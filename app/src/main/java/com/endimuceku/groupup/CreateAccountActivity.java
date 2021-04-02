package com.endimuceku.groupup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputLayout mEmailInput;
    private TextInputLayout mUsernameInput;
    private TextInputLayout mPasswordInput;
    private TextInputLayout mPassword2Input;

    private String email;
    private String username;
    private String password;
    private String password2;

    private Context context;

    private DatabaseReference ref;

    private Map<String, String> userHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        context = getApplicationContext();

        ref = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("users");

    }

    public void submitAccountCreationRequest(View view) {
        mEmailInput = (TextInputLayout) findViewById(R.id.email_input_ca);
        email = mEmailInput.getEditText().getText().toString();

        mUsernameInput = (TextInputLayout) findViewById(R.id.username_input_ca);
        username = mUsernameInput.getEditText().getText().toString();

        mPasswordInput = (TextInputLayout) findViewById(R.id.password_input_ca);
        password = mPasswordInput.getEditText().getText().toString();

        mPassword2Input = (TextInputLayout) findViewById(R.id.password_reinput_ca);
        password2 = mPassword2Input.getEditText().getText().toString();

        if(password.equals(password2) && password.length() >= 8 && username.length() >= 5 && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build();
                                user.updateProfile(profileUpdates);
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    String name = user.getDisplayName();

                                                    Toast.makeText(context, "Successfully created account for user " + name + ", please verify your email before signing in.", Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();

                                                    ref.child(user.getUid()).setValue(name);

                                                }
                                            }
                                        });


                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(context, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailInput.setError("Invalid e-mail address format.");
        } else if (username.length() < 5) {
            mUsernameInput.setError("Username should be at least 5 characters long.");
        } else if (!password.equals(password2)) {
            mPasswordInput.setError("Different passwords entered.");
            mPassword2Input.setError("Different passwords entered.");
        } else if (password.length() < 8){
            mPasswordInput.setError("Password should be at least 8 characters long.");
            mPassword2Input.setError("Password should be at least 8 characters long.");
        }
    }
}