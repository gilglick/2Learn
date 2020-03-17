package com.example.a2learn.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2learn.adapters.CardArrayAdapter;
import com.example.a2learn.utility.FireStoreDatabase;
import com.example.a2learn.R;
import com.example.a2learn.model.Card;
import com.example.a2learn.model.Match;
import com.example.a2learn.model.Rating;
import com.example.a2learn.model.Student;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentHome extends Fragment {
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    private CardArrayAdapter arrayAdapter;
    private List<Card> rowItems;
    private ListenerRegistration listen;
    private Student student;
    private ProgressBar progressBar;
    private TextView searching;

    public FragmentHome() {
    }

    public FragmentHome(Student student) {
        this.student = student;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipecard_activity, container, false);
        ImageButton likeButton = view.findViewById(R.id.likebtn);
        ImageButton disLikeButton = view.findViewById(R.id.dislikebtn);
        progressBar = view.findViewById(R.id.progressBarCards);
        searching = view.findViewById(R.id.searchingCardsTextView);
        SwipeFlingAdapterView flingContainer = view.findViewById(R.id.frame);
        rowItems = new ArrayList<>();
        arrayAdapter = new CardArrayAdapter(getActivity(), R.layout.item, rowItems);
        flingContainer.setAdapter(arrayAdapter);
        cardsListener();
        likeButton.setOnClickListener(v -> {
            likeButton.setEnabled(false);
            flingContainer.getTopCardListener().selectRight();
            likeButton.setEnabled(true);
        });
        disLikeButton.setOnClickListener(v -> {
            likeButton.setEnabled(false);
            flingContainer.getTopCardListener().selectLeft();
            likeButton.setEnabled(true);
        });
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                onMatchUpdate();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (itemsInAdapter > 0) {
                    progressBar.setVisibility(View.INVISIBLE);
                    searching.setVisibility(View.INVISIBLE);
                    likeButton.setEnabled(true);
                    disLikeButton.setEnabled(true);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searching.setVisibility(View.VISIBLE);
                    likeButton.setEnabled(false);
                    disLikeButton.setEnabled(false);

                }

            }

            @Override
            public void onScroll(float v) {

            }
        });

        return view;
    }


    private void cardsListener() {
        listen = fireStoreDatabase.getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots != null) {
                        List<DocumentChange> list = queryDocumentSnapshots.getDocumentChanges();
                        for (DocumentChange doc : list) {
                            Student currentStudent = doc.getDocument().toObject(Student.class);
                            if (student != null && !currentStudent.equals(student)) {
                                fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE)
                                        .document(student.getEmail()).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        Match matchList = task.getResult().toObject(Match.class);
                                        if (matchList != null && matchList.getOptionalMatches() != null) {
                                            Boolean bool = matchList.getOptionalMatches().get(encodeDot(currentStudent));
                                            if (bool != null && bool.equals(false)) {
                                                addCard(currentStudent);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void onMatchUpdate() {
        Student caller, callee;
        caller = student;
        callee = rowItems.get(0).getStudent();
        update(caller, callee);
        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE)
                .document(callee.getEmail()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Match studentMatches = task.getResult().toObject(Match.class);
                if (studentMatches != null) {
                    Boolean bool = studentMatches.getOptionalMatches().get(encodeDot(caller));
                    if (bool != null && bool.equals(true)) {
                        update(caller, callee);
                        createMatchView(caller, callee);
                    }
                }
            }
            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();
        });
    }

    private void update(Student caller, Student callee) {
        fireStoreDatabase.getDatabase()
                .collection(FireStoreDatabase.MATCH_STORGE)
                .document(caller.getEmail())
                .update(FireStoreDatabase.MATCHES + "." + encodeDot(callee), true);
    }


    private String encodeDot(Student caller) {
        return caller.getEmail().replace('.', ':');
    }

    private void addCard(Student currentStudent) {
        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.RATING)
                .document(currentStudent.getEmail())
                .get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful() && task1.getResult() != null) {
                Rating rating = task1.getResult().toObject(Rating.class);
                if (rating != null && !currentStudent.equals(student)) {
                    rowItems.add(new Card(currentStudent, rating));
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        listen.remove();
    }

    @SuppressLint("SetTextI18n")
    private void createMatchView(Student caller, Student callee) {
        LayoutInflater li = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.match_pop_up_layout, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        ImageView callerImage = view.findViewById(R.id.callerImage);
        ImageView calleeImage = view.findViewById(R.id.calleeImage);
        TextView descriptionMatch = view.findViewById(R.id.match_description);
        descriptionMatch.setText(caller.getFullName() + " " + getString(R.string.you_have_match) + " " + callee.getFullName());
        if (!caller.getUri().matches(""))
            Picasso.get().load(caller.getUri()).into(callerImage);
        if (!callee.getUri().matches(""))
            Picasso.get().load(callee.getUri()).into(calleeImage);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

}
