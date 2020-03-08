package com.example.a2learn;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

// change singleton - from aviv
public final class FireStoreHelper {
    private List<Student> studentList = new ArrayList<>();
    private List<Student> watchedStudentList = new ArrayList<>();
    private static final String TAG = "FireStoreHelper";
    static final String FULL_NAME = "fullName";
    static final String LOCATION = "location";
    static final String PHONE_NUMBER = "phoneNumber";
    static final String DATE_OF_BIRTH = "dateOfBirth";
    static final String GIVE_HELP_LIST = "giveHelpList";
    static final String NEED_HELP_LIST = "needHelpList";
    static final String PROFILE_STORAGE = "profileImages";
    static final String IMAGE_URI = "uri";
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
            FireStoreHelper fireStoreHelper = new FireStoreHelper();
            fireStoreHelper.getCollectionReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && task.getResult() != null){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Student student = document.toObject(Student.class);
                            studentList.add(student);
                        }
                        callback.onFinish(studentList);
                        for(Student student : studentList){
                            Log.i("TAG", "onComplete: " + student);
                        }
                    }
                }
            });
    }

    public interface FireStoreHelperCallback<T>{
        void onFinish(T result);
    }
}

