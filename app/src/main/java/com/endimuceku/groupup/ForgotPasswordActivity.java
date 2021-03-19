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

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout mEmailInput;
    private String email;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        context = getApplicationContext();

        mAuth = FirebaseAuth.getInstance();

    }

    public void submitPasswordRequest(View view) {
        mEmailInput = (TextInputLayout) findViewById(R.id.email_input_fp);
        email = mEmailInput.getEditText().getText().toString();

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "ERROR: Email not sent.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            mEmailInput.setError("Invalid email address.");
        }

    }
}