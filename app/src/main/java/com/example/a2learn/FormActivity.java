package com.example.a2learn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2learn.model.Match;
import com.example.a2learn.model.Rating;
import com.example.a2learn.model.Student;
import com.example.a2learn.utility.DialogDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class FormActivity extends AppCompatActivity {
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    private EditText mFullName, mEmail, mPassword, mConfirmPassword, mDateOfBirth, mPhoneNumber;
    private AutoCompleteTextView mAcademicInstitution;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private Student student;

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
        mAcademicInstitution = findViewById(R.id.academeInstitution);
        mDateOfBirth = findViewById(R.id.dateOfBirth);
        progressBar = findViewById(R.id.progressBar);
        registerButton = findViewById(R.id.registerButton);

        String[] academicInstitution = getApplication().getResources().getStringArray(R.array.institution);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplication(), R.layout.course_item, R.id.custom_list_item, academicInstitution);
        mAcademicInstitution.setAdapter(arrayAdapter);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAcademicInstitution.setAdapter(arrayAdapter);


        inProgress(false);

        registerButton.setOnClickListener(e -> {
            boolean validName, validEmail, validPassword, validConfirmPassword, validInput;
            validName = Validation.isValidName(mFullName.getText().toString());
            validEmail = Validation.isValidEmail(mEmail.getText().toString());
            validPassword = Validation.isValidPassword(mPassword.getText().toString());
            validConfirmPassword = mConfirmPassword.getText().toString().compareTo(mPassword.getText().toString()) == 0;
            validInput = validName && validEmail && validPassword && validConfirmPassword;
            if (!validName) {
                mFullName.setError(getString(R.string.invalid_name_form));
            }
            if (!validEmail) {
                mEmail.setError(getString(R.string.invalid_email_form));
            }
            if (!validPassword) {
                mPassword.setError(getString(R.string.invalid_password_form));
            }
            if (!validConfirmPassword) {
                mConfirmPassword.setError(getString(R.string.invalid_confirm_password_form));

            }
            if (validInput) {
                inProgress(true);
                student = new Student(
                        mFullName.getText().toString(),
                        mEmail.getText().toString(),
                        mAcademicInstitution.getText().toString(),
                        mDateOfBirth.getText().toString(),
                        mPhoneNumber.getText().toString());
                firebaseAuth.createUserWithEmailAndPassword(mEmail.getText().toString(),
                        mPassword.getText().toString()).addOnSuccessListener(authResult -> {
                    fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE)
                            .document(student.getEmail()).set(new Match()).addOnCompleteListener(task -> {
                                syncMatchDatabase();

                    });
                    fireStoreDatabase.getDatabase().collection(FireStoreDatabase.RATING)
                            .document(student.getEmail()).set(new Rating());
                    startActivity(new Intent(this, LoginActivity.class));
                }).addOnFailureListener(e1 ->
                        Toast.makeText(getApplicationContext(), getString(R.string.register_failed), Toast.LENGTH_SHORT).show());
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

    /**
     * Get activates while the user press the registration button
     */
    public void inProgress(boolean isUploading) {
        if (isUploading) {
            progressBar.setVisibility(View.VISIBLE);
            registerButton.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }


    private void syncMatchDatabase() {
        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documentReferences = task.getResult().getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentReferences) {
                            fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE)
                                    .document(student.getEmail())
                                    .update(FireStoreDatabase.MATCHES + "." + encodeDot(documentSnapshot.getId()), false);
                            fireStoreDatabase.getDatabase()
                                    .collection(FireStoreDatabase.MATCH_STORGE)
                                    .document(documentSnapshot.getId())
                                    .update(FireStoreDatabase.MATCHES + "." + encodeDot(student.getEmail()), false);
                        }
                        fireStoreDatabase.addStudent(student);
                    }
                });
    }

    private String encodeDot(String caller) {
        return caller.replace('.', ':');
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
