//package com.example.a2learn;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.lorentzos.flingswipe.SwipeFlingAdapterView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainActivity extends Activity {
////    private Card[] cards_data;
//    private CardArrayAdapter arrayAdapter;
//    private int i;
//
//    ListView listView;
//    List<Card> rowItems;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.swipecard_activity);
//
//        //choose your favorite adapter
//
//        rowItems = new ArrayList<Card>();
//        rowItems.add(new Card("12341234","Aviv"));
//        rowItems.add(new Card("123","Jonathna"));
//        rowItems.add(new Card("458","Gil"));
//
//        arrayAdapter = new CardArrayAdapter(this, R.layout.item, rowItems);
//        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
//
//
//        //set the listener and the adapter
//        flingContainer.setAdapter(arrayAdapter);
//        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
//            @Override
//            public void removeFirstObjectInAdapter() {
//                // this is the simplest way to delete an object from the Adapter (/AdapterView)
//                Log.d("LIST", "removed object!");
//                rowItems.remove(0);
//                arrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onLeftCardExit(Object dataObject) {
//                //Do something on the left!
//                //You also have access to the original object.
//                //If you want to use it just cast it (String) dataObject
//                Toast.makeText(MainActivity.this, "Left!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onRightCardExit(Object dataObject) {
//                Toast.makeText(MainActivity.this, "Right!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdapterAboutToEmpty(int itemsInAdapter) {
//                // Ask for more data here
//                Card item = new Card("Gil","Gil");
//                rowItems.add(item);
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
//            }
//
//            @Override
//            public void onScroll(float v) {
//
//            }
//        });
//
//        // Optionally add an OnItemClickListener
//        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> Toast.makeText(MainActivity.this, "Clicked!",Toast.LENGTH_SHORT).show());
//    }
//}