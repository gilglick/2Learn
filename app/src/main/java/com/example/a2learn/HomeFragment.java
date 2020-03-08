package com.example.a2learn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private CardArrayAdapter arrayAdapter;
    private List<Card> rowItems;
    private List<Card> yoni;
    private FireStoreHelper fireStoreHelper = new FireStoreHelper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipecard_activity, container, false);
        ImageButton likeButton = view.findViewById(R.id.likebtn);
        ImageButton disLikeButton = view.findViewById(R.id.dislikebtn);
        SwipeFlingAdapterView flingContainer = view.findViewById(R.id.frame);

        rowItems = new ArrayList<>();
        yoni = new ArrayList<>();
        fireStoreHelper.getAllStudents((result) -> {
            arrayAdapter = new CardArrayAdapter(getActivity(), R.layout.item, rowItems);
            flingContainer.setAdapter(arrayAdapter);
            result.forEach(student -> rowItems.add(new Card(student)));
            arrayAdapter.notifyDataSetChanged();
        });

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(getActivity(), "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(getActivity(), "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                if (itemsInAdapter == 0) {
                    new AlertDialog.Builder(getActivity()).setTitle("Oops!").setMessage("You watched all your currently optional matches.").setPositiveButton(R.string.refresh, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rowItems.addAll(yoni);
                            arrayAdapter.notifyDataSetChanged();
                        }
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

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            fireStoreHelper.getCollectionReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    List<DocumentChange> list = null;
                    if (queryDocumentSnapshots != null) {
                        list = queryDocumentSnapshots.getDocumentChanges();
                        for(DocumentChange doc : list){
                            yoni.add(new Card(doc.getDocument().toObject(Student.class)));
                        }
                    }
                }
            });
        }
    }

}
