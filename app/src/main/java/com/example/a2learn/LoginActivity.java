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

import com.example.a2learn.model.Rating;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;


public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configWindow();
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button signIn = findViewById(R.id.signIn);
        Button signUp = findViewById(R.id.signUp);
        calculateRating();
        signIn.setOnClickListener(v -> {
            signIn.setEnabled(false);
            if (hasInputError()) {
                signIn.setEnabled(true);
                return;
            }
            final String userEmail = email.getText().toString();
            final String userPassword = password.getText().toString();
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnSuccessListener(authResult ->
                            startActivity(new Intent(LoginActivity.this, ContainerActivity.class)
                                    .putExtra(getString(R.string.user_id), email.getText().toString())))
                    .addOnFailureListener(e ->
                            Toast.makeText(LoginActivity.this, getString(R.string.sign_in_error), Toast.LENGTH_LONG).show());
        });


        signUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, FormActivity.class)));
    }

    /**
     * Check and validate the user's input
     */
    private boolean hasInputError() {
        boolean validEmail, validPassword;
        validEmail = Validation.isValidEmail(email.getText().toString());
        validPassword = Validation.isValidPassword(password.getText().toString());
        if (!validEmail) {
            email.setError(getString(R.string.invalid_email_format));
        }
        if (!validPassword) {
            password.setError(getString(R.string.invalid_password_login));
        }
        return !validEmail || !validPassword;
    }

    /**
     * Calculating the rating of the users in application
     */
    public void calculateRating() {
        FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
        CollectionReference collectionReference = fireStoreDatabase.getDatabase().collection(FireStoreDatabase.RATING);
        collectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documentReferences = task.getResult().getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentReferences) {
                            Rating rating = documentSnapshot.toObject(Rating.class);
                            if (rating != null) {
                                collectionReference.document(documentSnapshot.getId()).update(FireStoreDatabase.CURRENT_RATING, rating.calcRating());
                            }
                        }
                    }
                });
    }

    /**
     * Configuration of login window
     */
    private void configWindow() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Hide the keyboard with pressing on the screen
     */
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