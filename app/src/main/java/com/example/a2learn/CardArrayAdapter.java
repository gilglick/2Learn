package com.example.a2learn;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CardArrayAdapter extends android.widget.ArrayAdapter<Card> {
    public CardArrayAdapter(Context context, int resourceId, List<Card> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Card card_item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        TextView askForHelp = convertView.findViewById(R.id.AskHelpTextView);
        TextView offerForHelp= convertView.findViewById(R.id.OfferHelpTextView);
        ImageView image = convertView.findViewById(R.id.image);
        if(card_item != null) {
            name.setText(card_item.getFullName());
            askForHelp.setText(card_item.getUserNeedHelpListStringFormat());
            offerForHelp.setText(card_item.getUserOfferListStringFormat());
            Picasso.get().load(card_item.getStudent().getUri()).into(image);
            //image.setImageResource(R.drawable.img);
//            downloadFromDatabase(new FireStoreHelperCallback<Uri>() {
//
//                @Override
//                public void onFinish(Uri result) {
//                    Picasso.get().load(result.toString()).into(image);
//                    Toast.makeText(getContext(), "" +result.getPath(), Toast.LENGTH_SHORT).show();
//
//                }
//            },card_item.getEmail(),image);

        }

        return convertView;
    }

 //   public void downloadFromDatabase(FireStoreHelperCallback<Uri> callback, String email, ImageView image) {
//        Log.i(TAG, "downloadFromDatabase: " + email);
//        FireStoreHelper fireStoreHelper = new FireStoreHelper();
//        StorageReference storageReference = fireStoreHelper.getmStorageRef().child(FireStoreHelper.PROFILE_STORAGE);
//        storageReference.child(email).getDownloadUrl().addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                Uri uri = task.getResult();
//                callback.onFinish(uri);
//            }
//        });
//    }
//
//    public interface FireStoreHelperCallback<T>{
//        void onFinish(T result);
//    }

}
