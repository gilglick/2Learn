package com.example.a2learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class ProficiencyFragment extends Fragment {
    private FireStoreHelper fireStoreHelper = new FireStoreHelper();
    private Student student;
    private boolean giveHelpActive, needHelpActive;
    private String currentCourse;

    public ProficiencyFragment(Student student) {
        this.student = student;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.proficiency_fragment, container, false);
        AutoCompleteTextView coursesEditText = view.findViewById(R.id.courseNameEditText);
        RadioButton needHelpRadioButton = view.findViewById(R.id.radioButtonNeedHelp);
        RadioButton giveHelpRadioButton = view.findViewById(R.id.radioButtonGiveHelp);
        CardView addCourse = view.findViewById(R.id.addCourseCard);
        CardView removeCourse = view.findViewById(R.id.removeCourseCard);
        String[] courses = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.courses);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.course_item, R.id.custom_list_item, courses);
        coursesEditText.setAdapter(arrayAdapter);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesEditText.setAdapter(arrayAdapter);

        coursesEditText.setOnItemClickListener((parent, view1, position, id) -> {
            Toast.makeText(getActivity(),
                    arrayAdapter.getItem(position),
                    Toast.LENGTH_SHORT).show();
            currentCourse = arrayAdapter.getItem(position);
        });

        giveHelpRadioButton.setOnClickListener(v -> {
            giveHelpActive = true;
            needHelpActive = false;

        });

        needHelpRadioButton.setOnClickListener(v -> {
            needHelpActive = true;
            giveHelpActive = false;

        });

        addCourse.setOnClickListener(v -> {
            if (giveHelpActive) {
                fireStoreHelper.getCollectionReference().document(student.getEmail()).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        student = documentSnapshot.toObject(Student.class);
                        if (student != null && student.getNeedHelpList() != null) {
                            if (!student.getNeedHelpList().contains(currentCourse)) {
                                student.getGiveHelpList().add(currentCourse);
                                fireStoreHelper.updateListField(student.getEmail(), FireStoreHelper.GIVE_HELP_LIST, currentCourse);
                                Toast.makeText(getActivity(), "Course" + currentCourse + " added successfully. ", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "Course" + currentCourse + "already exist in your need help list", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } else if (needHelpActive) {
                fireStoreHelper.getCollectionReference().document(student.getEmail()).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        student = documentSnapshot.toObject(Student.class);
                        if (student != null && student.getNeedHelpList() != null) {
                            if (!student.getGiveHelpList().contains(currentCourse)) {
                                student.getNeedHelpList().add(currentCourse);
                                fireStoreHelper.updateListField(student.getEmail(), FireStoreHelper.NEED_HELP_LIST, currentCourse);
                                Toast.makeText(getActivity(), "Course" + currentCourse + " added successfully. ", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "Course" + currentCourse + "already exist in your give help list", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        removeCourse.setOnClickListener(v -> {
            if (giveHelpActive) {
                fireStoreHelper.removeListField(student.getEmail(), FireStoreHelper.GIVE_HELP_LIST, currentCourse);
                Toast.makeText(getActivity(), "Course: " + currentCourse + " remove successfully from your helping list", Toast.LENGTH_LONG).show();
            } else if (needHelpActive) {
                fireStoreHelper.removeListField(student.getEmail(), FireStoreHelper.NEED_HELP_LIST, currentCourse);
                Toast.makeText(getActivity(), "Course: " + currentCourse + " remove successfully from your giving help list", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }


}
