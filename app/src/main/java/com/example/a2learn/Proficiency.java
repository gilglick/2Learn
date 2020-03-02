package com.example.a2learn;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Proficiency extends Fragment {
    private static final String TAG = "MainActivity";

    private static final String COURES_KEY = "course";
    private static final String COURSE_NUMBER = "number";

    private EditText editTextCourseName;
    private EditText getEditTextCourseNumber;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.proficiency_fragment,container,false);
        editTextCourseName = view.findViewById(R.id.courseNameEditText);
        getEditTextCourseNumber = view.findViewById(R.id.courseIdEditText);

        return view;
    }

    public void saveNote(View v){
        String courseName = editTextCourseName.getText().toString();
        String courseId = getEditTextCourseNumber.getText().toString();

        Map<String,Object> note = new HashMap<>();
        note.put(COURES_KEY,courseName);
        note.put(COURSE_NUMBER,courseId);

        db.collection("courses").document("course").set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Course uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Course uploaded failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadNote(View v){

        db.collection("courses").document("course").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String courseName = documentSnapshot.getString(COURES_KEY);
                    String courseId = documentSnapshot.getString(COURSE_NUMBER);
                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Not working", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public void updateCourseId(View v){

        DocumentReference docRef = db.collection("courses").document("course");
        String newCourseId = getEditTextCourseNumber.getText().toString();
        //Map<String,Object> note = new HashMap<>();
        //note.put(COURSE_NUMBER,newCourseId);
        //docRef.set(note, SetOptions.merge());
        docRef.update(COURSE_NUMBER,newCourseId);

    }

    public void writeObject(View v){
        DocumentReference docRef = db.collection("courses").document("course");
        Course course = new Course.Builder().setCourseName(editTextCourseName.getText().toString()).
                setCourseNumber(getEditTextCourseNumber.getText().toString()).build();
        docRef.set(course);
    }

    public void readObject(View v){
        DocumentReference docRef = db.collection("courses").document("course");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Course course = documentSnapshot.toObject(Course.class);
                    Toast.makeText(getActivity(), "" + course , Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Failed" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
