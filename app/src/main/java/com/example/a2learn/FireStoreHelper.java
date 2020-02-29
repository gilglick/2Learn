package com.example.a2learn;

import android.util.Log;


import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FireStoreHelper {
    private static final String TAG = "FireStoreHelper";
    private static final String USER_COLLECTION = "users";
    private static final String EMAIL = "email";
    private static final String FULL_NAME = "full name";
    private static final String PHONE = "phone";
    private static final String DATE_OF_BIRTH = "date of birth";
    private static final String STUD_DESCRIPTION = "student description";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public void addStudent(Student student) {
        Map<String, Object> user = new HashMap<>();
        user.put(EMAIL, student.getEmail());
        user.put(FULL_NAME, student.getFullName());
        user.put(PHONE, student.getPhoneNumber());
        user.put(DATE_OF_BIRTH, student.getDateOfBirth());
        user.put(STUD_DESCRIPTION, student.getStudentDescription());

        db.collection(USER_COLLECTION).document(student.getEmail()).
                set(user).addOnSuccessListener(aVoid -> {
        }).addOnFailureListener(e -> Log.i(TAG, "onFailure: "));
    }

    public void getStudent(String userEmail) {

        db.collection("users").document(userEmail)
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Student student = new Student(
                        documentSnapshot.getString(FULL_NAME),
                        documentSnapshot.getString(EMAIL),
                        documentSnapshot.getString(DATE_OF_BIRTH),
                        documentSnapshot.getString(PHONE));
                Log.i(TAG, "onSuccess: " + student);
            }
        })
                .addOnFailureListener(e -> { });
    }


}
