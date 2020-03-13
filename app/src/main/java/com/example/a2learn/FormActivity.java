package com.example.a2learn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a2learn.model.Match;
import com.example.a2learn.model.Student;
import com.google.firebase.auth.FirebaseAuth;


public class FormActivity extends AppCompatActivity {

    private EditText mFullName, mEmail, mPassword, mConfirmPassword, mDateOfBirth, mPhoneNumber;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeration_form);
        firebaseAuth = FirebaseAuth.getInstance();
        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirmPassword);
        mPhoneNumber = findViewById(R.id.phoneNumber);
        mDateOfBirth = findViewById(R.id.dateOfBirth);
        progressBar = findViewById(R.id.progressBar);
        registerButton = findViewById(R.id.registerButton);
        inProgress(false);

        registerButton.setOnClickListener(e -> {
            boolean validName, validEmail, validPassword, validConfirmPassword, validDateOfBirth, validInput;
            validName = Validation.isValidName(mFullName.getText().toString());
            validEmail = Validation.isValidEmail(mEmail.getText().toString());
            validPassword = Validation.isValidPassword(mPassword.getText().toString());
            validConfirmPassword = mConfirmPassword.getText().toString().compareTo(mPassword.getText().toString()) == 0;
            validDateOfBirth = Validation.isValidDate(mDateOfBirth.getText().toString());
            validInput = validName && validEmail && validPassword && validConfirmPassword && validDateOfBirth;
            if (!validName) {
                mFullName.setError("Name cannot be empty");
            }
            if (!validEmail) {
                mEmail.setError("Not valid email address ");
            }
            if (!validPassword) {
                mPassword.setError("Not valid password format");
            }
            if (!validConfirmPassword) {
                mConfirmPassword.setError("Password don't match");
            }
            if (!validDateOfBirth) {
                mDateOfBirth.setError("Date of birth cannot be empty");
            }
            if (validInput) {
                inProgress(true);
                Student stud = new Student(
                        mFullName.getText().toString(),
                        mEmail.getText().toString(),
                        "Afeka Academic College of Engineering",
                        mDateOfBirth.getText().toString(),
                        mPhoneNumber.getText().toString());
                Log.i("tag", stud + "");
                firebaseAuth.createUserWithEmailAndPassword(mEmail.getText().toString(),
                        mPassword.getText().toString()).addOnSuccessListener(authResult -> {
                    FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
                    fireStoreDatabase.addStudent(stud);
                    fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE)
                            .document(stud.getEmail()).set(new Match());
                    startActivity(new Intent(this, LoginActivity.class));
                }).addOnFailureListener(e1 ->
                        Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show());
            }

        });


        mDateOfBirth.setOnTouchListener((v, event) ->
        {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (mDateOfBirth.getRight() - mDateOfBirth.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    DialogDate.createCalender(this).show();
                    return true;
                }
            }
            return false;
        });

        DialogDate.mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = DialogDate.dateFormat(year, month, day);
            mDateOfBirth.setText(date);
        };
    }


    public void inProgress(boolean isUploading) {
        if (isUploading) {
            progressBar.setVisibility(View.VISIBLE);
            registerButton.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
        }
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
