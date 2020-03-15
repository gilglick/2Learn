package com.example.a2learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a2learn.model.Card;
import com.example.a2learn.model.Match;
import com.example.a2learn.model.Rating;
import com.example.a2learn.model.Student;
import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.ListenerRegistration;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            onMatchListener();
            flingContainer.getTopCardListener().selectRight();
        });

        disLikeButton.setOnClickListener(v -> flingContainer.getTopCardListener().selectLeft());

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
                onMatchListener();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (itemsInAdapter > 0) {
                    progressBar.setVisibility(View.INVISIBLE);
                    searching.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searching.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(float v) {

            }
        });

        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show());

        return view;
    }


    private void cardsListener() {
        listen = fireStoreDatabase.getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots != null) {
                        List<DocumentChange> list = queryDocumentSnapshots.getDocumentChanges();
                        for (DocumentChange doc : list) {
                            Student currentStudent = doc.getDocument().toObject(Student.class);
                            if (!currentStudent.equals(student)) {
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
                        }
                    }
                });
    }

    private void onMatchListener() {
        String caller, callee;
        caller = student.getEmail();
        callee = rowItems.get(0).getEmail();
        // get callee database
        update(callee, caller, false);
        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE)
                .document(caller).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Match studentMatches = task.getResult().toObject(Match.class);
                if (studentMatches != null) {
                    Map<String, Boolean> map = studentMatches.getOptionalMatches();
                    if (map.containsKey(encodeDot(callee))) {
                        update(caller, callee, true);
                        update(callee, caller, true);
                        Toast.makeText(getActivity(), "MATCH!!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }


    private void update(String caller, String callee, boolean match) {
        fireStoreDatabase.getDatabase()
                .collection(FireStoreDatabase.MATCH_STORGE)
                .document(caller)
                .update(FireStoreDatabase.MATCHES + "." + encodeDot(callee), match);
    }


    private String encodeDot(String userId) {
        return userId.replace('.', ':');
    }


    @Override
    public void onStop() {
        super.onStop();
        listen.remove();
    }

}
