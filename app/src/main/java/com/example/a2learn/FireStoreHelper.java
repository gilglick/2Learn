package com.example.a2learn;

import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireStoreHelper {
    private static final String TAG = "FireStoreHelper";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("profiles");
    private Student studentFromStore;

    public CollectionReference getCollectionReference() {
        return collectionReference;
    }

    public void addStudent(Student student) {
        collectionReference.document(student.getEmail()).set(student).
                addOnSuccessListener(aVoid -> Log.i(TAG, "onSuccess: ")).
                addOnFailureListener(e -> Log.i(TAG, "onFailure: "));
    }



    public void updateFullName(String docId,String newName){
        final String FULL_NAME = "fullName";
        collectionReference.document(docId).update(FULL_NAME,newName);
    }

    public void updateLocation(String docId,String newLocation){
        final String LOCATION = "location";
        collectionReference.document(docId).update(LOCATION,newLocation);
    }

    public void updatePhone(String docId,String newPhoneNumber){
        final String PHONE_NUMBER = "phoneNumber";
        collectionReference.document(docId).update(PHONE_NUMBER,newPhoneNumber);
    }
    //String fullName, String email, String location, String dateOfBirth, String phoneNumber
    public void updateDateOfBirth(String docId,String newDateOfBirth){
        final String DATE_OF_BIRTH= "dateOfBirth";
        collectionReference.document(docId).update(DATE_OF_BIRTH,newDateOfBirth);
    }

    public void updateDescription(String docId, String newDescription){
        final String DESCRIPTION = "description";
        collectionReference.document(docId).update(DESCRIPTION,newDescription);
    }


    public void addCourseToHelpList(String docId,String courseName){
        final String HELP_LIST = "giveHelpCourse";
        collectionReference.document(docId).update(HELP_LIST, FieldValue.arrayUnion(courseName));
    }

    public void addCourseToNeedList(String docId, String courseName){
        final String HELP_LIST = "needHelpCourse";
        collectionReference.document(docId).update(HELP_LIST, FieldValue.arrayUnion(courseName));
    }

    private interface FirebaseCallback {
        void onListCallBack(List<Course> courseList);
        void onStudentCallBack(Student student);
    }
}

