package com.example.a2learn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.a2learn.model.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FragmentProficiency extends Fragment {
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    private Student student;
    private boolean giveHelpActive, needHelpActive;
    private String currentCourse;
    private FloatingActionButton floatingActionButton;
    private LinearLayout descriptionContent;
    private boolean isContentVisible = false;

    public FragmentProficiency() {

    }

    public FragmentProficiency(Student student) {
        this.student = student;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.proficiency_fragment, container, false);
        AutoCompleteTextView coursesEditText = view.findViewById(R.id.courseNameEditText);
        RadioButton needHelpRadioButton = view.findViewById(R.id.radioButtonNeedHelp);
        RadioButton giveHelpRadioButton = view.findViewById(R.id.radioButtonGiveHelp);
        CardView addCourse = view.findViewById(R.id.addCourseCard);
        CardView removeCourse = view.findViewById(R.id.removeCourseCard);
        floatingActionButton = view.findViewById(R.id.floatingInformation);
        descriptionContent = view.findViewById(R.id.descriptionContent);
        descriptionContent.setVisibility(View.INVISIBLE);
        String[] courses = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.courses);
        List<String> courseList = Arrays.asList(courses);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.course_item, R.id.custom_list_item, courses);
        coursesEditText.setAdapter(arrayAdapter);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesEditText.setAdapter(arrayAdapter);

        coursesEditText.setOnItemClickListener((parent, view1, position, id) -> currentCourse = arrayAdapter.getItem(position));

        floatingActionButton.setOnClickListener(v -> {
            if (!isContentVisible) {
                descriptionContent.setVisibility(View.VISIBLE);
            } else {
                descriptionContent.setVisibility(View.INVISIBLE);
            }
            isContentVisible = !isContentVisible;
        });
        giveHelpRadioButton.setOnClickListener(v -> {
            giveHelpActive = true;
            needHelpActive = false;

        });

        needHelpRadioButton.setOnClickListener(v -> {
            needHelpActive = true;
            giveHelpActive = false;

        });

        addCourse.setOnClickListener(v -> fireStoreDatabase.getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE).document(student.getEmail()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                student = documentSnapshot.toObject(Student.class);
                if (student != null && courseList.contains(currentCourse)) {
                    if (giveHelpActive) {
                        if (!student.getNeedHelpList().contains(currentCourse)) {
                            student.getGiveHelpList().add(currentCourse);
                            fireStoreDatabase.updateListField(student.getEmail(), FireStoreDatabase.STUDENT_STORAGE, FireStoreDatabase.GIVE_HELP_LIST, currentCourse);
                            Toast.makeText(getActivity(), "Course: " + "\"" + currentCourse + "\"" + " added successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Sorry, you are already need help in " + "\"" + currentCourse + "\"" + " course.", Toast.LENGTH_LONG).show();
                        }
                    } else if (needHelpActive) {
                        if (!student.getGiveHelpList().contains(currentCourse) && !student.getNeedHelpList().contains(currentCourse)) {
                            student.getNeedHelpList().add(currentCourse);
                            fireStoreDatabase.updateListField(student.getEmail(), FireStoreDatabase.STUDENT_STORAGE, FireStoreDatabase.NEED_HELP_LIST, currentCourse);
                            Toast.makeText(getActivity(), "Course" + "\"" + currentCourse + "\"" + " added successfully.", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getActivity(), "Sorry, you are already giving help in " + "\"" + currentCourse + "\"" + " course.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }));

        removeCourse.setOnClickListener(v -> fireStoreDatabase.getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE).document(student.getEmail()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                student = documentSnapshot.toObject(Student.class);
                if (student != null && courseList.contains(currentCourse)) {
                    if (giveHelpActive) {
                        if (!student.getGiveHelpList().contains(currentCourse)) {
                            Toast.makeText(getActivity(), "Course not exists in your list.", Toast.LENGTH_LONG).show();
                        } else {
                            fireStoreDatabase.removeListField(student.getEmail(), FireStoreDatabase.STUDENT_STORAGE, FireStoreDatabase.GIVE_HELP_LIST, currentCourse);
                            student.getGiveHelpList().remove(currentCourse);
                            Toast.makeText(getActivity(), "Course: " + "\"" + currentCourse + "\"" + " remove successfully from your list.", Toast.LENGTH_LONG).show();
                        }
                    } else if (needHelpActive) {
                        if (!student.getNeedHelpList().contains(currentCourse)) {
                            Toast.makeText(getActivity(), "Sorry you don't need help in " + "\"" + currentCourse + "\"" + " course.", Toast.LENGTH_LONG).show();
                        } else {
                            fireStoreDatabase.removeListField(student.getEmail(), FireStoreDatabase.STUDENT_STORAGE, FireStoreDatabase.NEED_HELP_LIST, currentCourse);
                            student.getNeedHelpList().remove(currentCourse);
                            Toast.makeText(getActivity(), "Course: " + "\"" + currentCourse + "\"" + " remove successfully list.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }));

        view.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view12 = getActivity().getCurrentFocus();
            if (imm != null && view12 != null) {
                imm.hideSoftInputFromWindow(view12.getWindowToken(), 0);
            }
            return true;
        });

        return view;

    }


}

