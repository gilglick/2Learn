package com.example.a2learn;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

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
            downloadFromDatabase(card_item.getEmail(), image);
        }

        return convertView;
    }

    public void downloadFromDatabase(String email, ImageView image) {
        FireStoreHelper fireStoreHelper = new FireStoreHelper();
        StorageReference storageReference = fireStoreHelper.getmStorageRef().child(FireStoreHelper.PROFILE_STORAGE);
        storageReference.child(email).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri uri = task.getResult();
                Toast.makeText(getContext(), "" + uri, Toast.LENGTH_SHORT).show();
                Picasso.get().load(uri).into(image);
            }
        });
    }
}
