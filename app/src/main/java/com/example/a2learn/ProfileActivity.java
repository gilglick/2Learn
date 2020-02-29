package com.example.a2learn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView userImage;
    private TextView userName;
    private TextView email;
    private TextView phoneNumber;
    private TextView dateOfBirth;
    private Spinner needHelpSpinnerList;
    private Spinner giveHelpSpinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
//        userName = findViewById(R.id.userNameTextView);
//        email = findViewById(R.id.emailTextView);
//        phoneNumber = findViewById(R.id.phoneNumeberEditText);
//        dateOfBirth = findViewById(R.id.dateOfBirthEditText);
//
//        Student stud = getIntent().getParcelableExtra("USER");
//        createProfile(stud);
//        Log.i("Test",stud + "");
//    }
//
//    private void createProfile(Student stud) {
//
//        userName.setText(stud.getFullName());
//        email.setText(stud.getEmail());
//        phoneNumber.setText(stud.getPhoneNumber());
//        dateOfBirth.setText(stud.getDateOfBirth());
    }
}
