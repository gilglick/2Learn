package com.example.a2learn;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public final class FireStoreDatabase {
    private static final String TAG = "FireStoreDatabase";

    // Database path
    static final String STUDENT_STORAGE = "profiles";
    static final String SOCIAL_MEDIA_STORAGE = "socialMedia";
    static final String SETTING_STORAGE = "settings";
    static final String PROFILE_IMAGES_STORAGE = "profileImages";
    static final String MATCH_STORGE = "matches";

    // Student attributes
    private List<Student> studentList = new ArrayList<>();
    private List<Student> updateData = new ArrayList<>();
    static final String FULL_NAME = "fullName";
    static final String LOCATION = "location";
    static final String PHONE_NUMBER = "phoneNumber";
    static final String DATE_OF_BIRTH = "dateOfBirth";
    static final String GIVE_HELP_LIST = "giveHelpList";
    static final String NEED_HELP_LIST = "needHelpList";
    static final String IMAGE_URI = "uri";

    // User setting attributes
    static final String DISPLAY_PHONE = "displayPhone";
    static final String DISPLAY_EMAIL = "displayEmail";
    static final String DISPLAY_DATA = "displayDate";
    static final String FACEBOOK = "facebook";
    static final String TWITTER = "twitter";
    static final String LINKEDIN = "linkedin";

    // Match attributes
    static final String MATCHES = "optionalMatches";
    // Database
    private static FireStoreDatabase fireStoreDatabase;
    private FirebaseFirestore database;
    private FirebaseAuth authDatabase;
    private FirebaseStorage storageDatabase;
    private FireStoreHelperCallback<SocialMedia> socialMediaCallback;
    private FireStoreHelperCallback<StudentSetting> userSettingCallback;


    private FireStoreDatabase() {
        database = FirebaseFirestore.getInstance();
        authDatabase = FirebaseAuth.getInstance();
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

    public void getUpdateData(FireStoreHelperCallback<List<Student>> callback) {
            getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE).addSnapshotListener((queryDocumentSnapshots, e) -> {
                List<DocumentChange> list = null;
                if (queryDocumentSnapshots != null) {
                    list = queryDocumentSnapshots.getDocumentChanges();
                    for (DocumentChange doc : list) {
                        updateData.add(doc.getDocument().toObject(Student.class));
                    }
                    callback.onFinish(updateData);
                }
            });
    }
    public void getAllStudents(FireStoreHelperCallback<List<Student>> callback) {
        getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Student student = document.toObject(Student.class);
                    studentList.add(student);
                }
                callback.onFinish(studentList);
                studentList.clear();
            }
        });
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

    public void readSocialMediaSetting(String userId, SocialMedia socialMedia) {
        getDatabase().collection(FireStoreDatabase.SOCIAL_MEDIA_STORAGE).document(userId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                SocialMedia socialMediaFromStorage = task.getResult().toObject(SocialMedia.class);
                socialMediaCallback.onFinish(socialMediaFromStorage);
            }
        }).addOnFailureListener(e -> Log.d(TAG, "Failed to download social media object from storage."));
    }

    public void readeUserSetting(String userId, StudentSetting studentSetting) {
        getDatabase().collection(FireStoreDatabase.SOCIAL_MEDIA_STORAGE).document(userId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                StudentSetting studentSettingFromStorage = task.getResult().toObject(StudentSetting.class);
                userSettingCallback.onFinish(studentSettingFromStorage);
            }
        }).addOnFailureListener(e -> Log.d(TAG, "Failed to download social media object from storage."));
    }

    public interface FireStoreHelperCallback<T> {
        void onFinish(T result);
    }
}

