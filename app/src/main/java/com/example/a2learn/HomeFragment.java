package com.example.a2learn;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private CardArrayAdapter arrayAdapter;
    private List<Card> rowItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipecard_activity, container, false);
        SwipeFlingAdapterView flingContainer = view.findViewById(R.id.frame);
        rowItems = new ArrayList<>();
        Card item = new Card(new Student("aviv","aviv","aviv","aviv","aviv"));
        rowItems.add(item);

        arrayAdapter = new CardArrayAdapter(getActivity(), R.layout.item, rowItems);
        flingContainer.setAdapter(arrayAdapter);
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
                List<String> askHelp = new ArrayList<>();
                askHelp.add("Aviv");
                List<String> offerHelp = new ArrayList<>();
                offerHelp.add("Aviv");

                Card item = new Card(new Student("aviv","aviv","aviv","aviv","aviv"));
                item.getStudent().setNeedHelpList(askHelp);
                item.getStudent().setGiveHelpList(offerHelp);
                rowItems.add(item);
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
            }

            @Override
            public void onScroll(float v) {

            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show());
FireStoreHelper fireStoreHelper = new FireStoreHelper();
fireStoreHelper.getAllStudents((result) -> {
    result.forEach(student->rowItems.add(new Card(student)));
    arrayAdapter.clear();
    arrayAdapter.notifyDataSetChanged();
});
        return view;
    }


}
