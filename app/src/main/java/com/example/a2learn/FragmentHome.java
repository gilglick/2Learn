package com.example.a2learn;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentHome extends Fragment {
    private CardArrayAdapter arrayAdapter;
    private List<Card> rowItems;
    private List<Student> update;
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    private ListenerRegistration listen;
    private Student student;

    //private CollectionReference collectionReference =  fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE);
    public FragmentHome(Student student) {
        this.student = student;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipecard_activity, container, false);
        ImageButton likeButton = view.findViewById(R.id.likebtn);
        ImageButton disLikeButton = view.findViewById(R.id.dislikebtn);
        SwipeFlingAdapterView flingContainer = view.findViewById(R.id.frame);
        rowItems = new ArrayList<>();
        arrayAdapter = new CardArrayAdapter(getActivity(), R.layout.item, rowItems);
        flingContainer.setAdapter(arrayAdapter);

        getUpdateData();
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                fireStoreDatabase.updateListField(student.getEmail(), FireStoreDatabase.MATCH_STORGE, "optionalMatches", rowItems.get(0).getEmail());
                fireStoreDatabase.getDatabase().collection(FireStoreDatabase.MATCH_STORGE)
                        .document(rowItems.get(0).getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Match match = task.getResult().toObject(Match.class);
                            if (match != null) {
                                List<String> list = match.getOptionalMatches();
                                if (list.contains(student.getEmail())) {
                                    Toast.makeText(getActivity(), "MATCH!!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            rowItems.remove(0);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

                // i add match to database
                // if the other user also has me
                //check if rowItems.get(0).getEmail() have student in his oppMatchArr
                // if he does
                // move to fragmentChat\match
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                if (itemsInAdapter == 0 || rowItems.size() == 0) {
                    new AlertDialog.Builder(getActivity()).setTitle("Oops!").setMessage("You watched all your currently optional matches.").setPositiveButton(R.string.refresh, (dialog, which) -> {
                    }).setNegativeButton(R.string.come_back_in_another_time, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                }
            }

            @Override
            public void onScroll(float v) {

            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show());

        return view;
    }

    public void getUpdateData() {
        listen = fireStoreDatabase.getDatabase().collection(FireStoreDatabase.STUDENT_STORAGE)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    Toast.makeText(getActivity(), "Something changed", Toast.LENGTH_SHORT).show();
                    List<DocumentChange> list = null;
                    if (queryDocumentSnapshots != null) {
                        list = queryDocumentSnapshots.getDocumentChanges();
                        for (DocumentChange doc : list) {
                            Student student = doc.getDocument().toObject(Student.class);
                            rowItems.add(new Card(student));
                        }
                    }
                    if (arrayAdapter != null)
                        arrayAdapter.notifyDataSetChanged();

                });
    }

    @Override
    public void onStop() {
        super.onStop();
        listen.remove();
    }
}
