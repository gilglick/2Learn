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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class FragmentMatch extends Fragment implements StudentAdapter.OnFragmentLoader {

    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private static volatile  boolean isLoaded = false;
    private Student student;

    public FragmentMatch (Student student)
    {
        this.student = student;
        studentList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        studentAdapter = new StudentAdapter(getContext(), studentList);
        studentAdapter.setFragmentLoader(FragmentMatch.this);
        recyclerView.setAdapter(studentAdapter);
        matchesListener();

        return view;
    }

    private void readAllMatchFromStorage() {
            FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
            fireStoreDatabase.getAllStudents(result -> {
                studentList.clear();
                studentList.addAll(result);
                studentAdapter.notifyDataSetChanged();

            });

    }

    private void matchesListener() {
        readAllMatchFromStorage();
        FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE)
                .addSnapshotListener((queryDocumentSnapshots, e)
                        -> {
                    Log.i(TAG, "matchesListener: ");
                });

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
