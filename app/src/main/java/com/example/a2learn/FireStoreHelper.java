package com.example.a2learn;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

// change singleton - from aviv
public final class FireStoreHelper {
    private static final String TAG = "FireStoreHelper";
    static final String FULL_NAME = "fullName";
    static final String LOCATION = "location";
    static final String PHONE_NUMBER = "phoneNumber";
    static final String DATE_OF_BIRTH = "dateOfBirth";
    static final String GIVE_HELP_LIST = "giveHelpList";
    static final String NEED_HELP_LIST = "needHelpList";
    static final String PROFILE_STORAGE = "profileImages";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference collectionReference = db.collection("profiles");
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public CollectionReference getCollectionReference() {
        return collectionReference;
    }

    public StorageReference getmStorageRef() {
        return mStorageRef;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void addStudent(Student student) {
        collectionReference.document(student.getEmail()).set(student).
                addOnSuccessListener(aVoid -> Log.i(TAG, "onSuccess: ")).
                addOnFailureListener(e -> Log.i(TAG, "onFailure: "));
    }


    public void updateField(String docId, String field, String newValue) {
        collectionReference.document(docId).update(field, newValue);
    }


    public void updateListField(String docId, String field, String newValue) {
        collectionReference.document(docId).update(field, FieldValue.arrayUnion(newValue));
    }

    public void removeListField(String docId, String field, String newValue) {
        collectionReference.document(docId).update(field, FieldValue.arrayRemove(newValue));
    }

    public void getAllStudents(FireStoreHelperCallback<List<Student>> callback){
        //Logic ve balagan
        //->result-> callback.onFinish(result);
    }

    public interface FireStoreHelperCallback<T>{
        void onFinish(T result);
    }
}

