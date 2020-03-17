package com.example.a2learn.utility;

import android.util.Log;

import com.example.a2learn.model.SocialMedia;
import com.example.a2learn.model.Student;
import com.example.a2learn.model.StudentSetting;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public final class FireStoreDatabase {
    private static final String TAG = "FireStoreDatabase";
    private static FireStoreDatabase fireStoreDatabase;
    private FirebaseFirestore database;
    private FirebaseAuth authDatabase;
    private FirebaseStorage storageDatabase;

    public static final String STUDENT_STORAGE = "profiles";
    public static final String SOCIAL_MEDIA_STORAGE = "socialMedia";
    public static final String SETTING_STORAGE = "settings";
    public static final String PROFILE_IMAGES_STORAGE = "profileImages";
    public static final String MATCH_STORGE = "matches";
    public static final String RATING = "rating";

    public static final String CURRENT_RATING = "currentRating";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String GIVE_HELP_LIST = "giveHelpList";
    public static final String NEED_HELP_LIST = "needHelpList";
    public static final String IMAGE_URI = "uri";
    public static final String DISPLAY_PHONE = "displayPhone";
    public static final String DISPLAY_EMAIL = "displayEmail";
    public static final String DISPLAY_DATA = "displayDate";
    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";
    public static final String LINKEDIN = "linkedin";
    public static final String MATCHES = "optionalMatches";


    private FireStoreDatabase() {
        database = FirebaseFirestore.getInstance();
        authDatabase = FirebaseAuth.getInstance();
        storageDatabase = FirebaseStorage.getInstance();
        storageDatabase = FirebaseStorage.getInstance();
    }

    public static FireStoreDatabase getInstance() {
        if (fireStoreDatabase == null) {
            fireStoreDatabase = new FireStoreDatabase();
        }
        return fireStoreDatabase;
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }

    public FirebaseStorage getStorageDatabase() {
        return storageDatabase;
    }

    public FirebaseAuth getFirebaseAuth() {
        return authDatabase;
    }


    public void addStudent(Student student) {
        getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE).document(student.getEmail()).set(student).
                addOnSuccessListener(aVoid -> Log.i(TAG, "onSuccess: ")).
                addOnFailureListener(e -> Log.i(TAG, "onFailure: "));
    }


    public void updateField(String docId, String path, String field, String newValue) {
        getDatabase().collection(path).document(docId).update(field, newValue);
    }


    public void updateListField(String docId, String path, String field, String newValue) {
        getDatabase().collection(path).document(docId).update(field, FieldValue.arrayUnion(newValue));
    }

    public void removeListField(String docId, String path, String field, String newValue) {
        getDatabase().collection(path).document(docId).update(field, FieldValue.arrayRemove(newValue));
    }

    public void writeSocialMediaSetting(String userId, SocialMedia socialMedia) {
        getDatabase()
                .collection(FireStoreDatabase.SOCIAL_MEDIA_STORAGE)
                .document(userId).set(socialMedia)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Social media has been write to storage successfully! "))
                .addOnFailureListener(e -> Log.d(TAG, "Failed to upload social media object! "));
    }

    public void writeUserSetting(String userId, StudentSetting studentSetting) {
        getDatabase()
                .collection(FireStoreDatabase.SETTING_STORAGE)
                .document(userId).set(studentSetting)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Setting has been write to storage successfully! "))
                .addOnFailureListener(e -> Log.d(TAG, "Failed to upload user's setting! "));
    }

    public void getUserImageFromDatabase(Student student) {
        if (!student.getUri().matches("")) {
            StorageReference storageReference = fireStoreDatabase.getStorageDatabase().getReference().child((student.getEmail()));
            storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    student.setUri(task.getResult().toString());
                }
            }).addOnFailureListener(e -> Log.d(TAG, "getImageFromDatabase: " + "Download image failed"));
        }

    }
    public String encodeDot(String caller) {
        return caller.replace('.', ':');
    }

}

