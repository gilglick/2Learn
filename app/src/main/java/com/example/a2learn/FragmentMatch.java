package com.example.a2learn;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class FragmentMatch extends Fragment implements StudentAdapter.OnFragmentLoader {
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private Student student;

    public FragmentMatch() {
    }

    public FragmentMatch(Student student) {
        this.student = student;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(getContext(), studentList);
        studentAdapter.setFragmentLoader(FragmentMatch.this);
        recyclerView.setAdapter(studentAdapter);
        readAllMatchFromStorage();
        return view;
    }

    private void readAllMatchFromStorage() {
        fireStoreDatabase.getDatabase()
                .collection(FireStoreDatabase.MATCH_STORGE)
                .document(student.getEmail()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Match studentMatches = task.getResult().toObject(Match.class);
                if (studentMatches != null) {
                    Map<String, Boolean> map = studentMatches.getOptionalMatches();
                    map.forEach((key, hasMatch) -> {
                        if (hasMatch) {
                            fireStoreDatabase.getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE)
                                    .document(decodeDot(key)).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful() && task1.getResult() != null) {
                                    Student stud = task1.getResult().toObject(Student.class);
                                    studentList.add(stud);
                                    studentAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private String decodeDot(String userId) {
        return userId.replace(':', '.');
    }

    @Override
    public void triggerFragment(Student student) {
        FragmentChat fragmentChat = new FragmentChat();
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_fragment, fragmentChat)
                .addToBackStack(null)
                .commit();
    }
}
