package com.example.a2learn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configGameWindow();
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button signIn = findViewById(R.id.signIn);
        Button signUp = findViewById(R.id.signUp);

        signIn.setOnClickListener(v -> {
            if (hasInputError()) {
                return;
            }
            final String userEmail = email.getText().toString();
            final String userPassword = password.getText().toString();
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnSuccessListener(authResult ->
                            startActivity(new Intent(LoginActivity.this, ContainerActivity.class)
                                    .putExtra("userId", email.getText().toString())))
                    .addOnFailureListener(e ->
                            Toast.makeText(LoginActivity.this, "Sign in failed, forget your password?", Toast.LENGTH_LONG).show());
        });


        signUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, FormActivity.class)));
    }

    private boolean hasInputError() {
        boolean validEmail, validPassword;
        validEmail = Validation.isValidEmail(email.getText().toString());
        validPassword = Validation.isValidPassword(password.getText().toString());
        if (!validEmail) {
            email.setError("Not valid format of email");
        }
        if (!validPassword) {
            password.setError("Not valid password format");
        }
        return !validEmail && !validPassword;
    }

    private void configGameWindow() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return super.onTouchEvent(event);

    }
}