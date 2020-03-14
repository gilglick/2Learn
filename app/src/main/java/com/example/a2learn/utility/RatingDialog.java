package com.example.a2learn.utility;

import com.example.a2learn.FireStoreDatabase;
import com.example.a2learn.R;
import com.google.firebase.firestore.FieldValue;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.RatingBar;

import androidx.annotation.NonNull;



public class RatingDialog extends Dialog {
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    private RatingBar ratingBar;
    private String userId;
    
    public RatingDialog(@NonNull Context context,String userId) {
        super(context);
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raring_dialog);
        ratingBar = findViewById(R.id.customRatingBar);
        ratingBar.setMax(5);
        ratingBar.setOnRatingBarChangeListener((rateBar, rating, fromUser) -> {
           updateRating();
        });
    }

    private void updateRating(){
        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.RATING)
                .document(userId)
                .update("ratings", FieldValue.arrayUnion(ratingBar.getRating()));
    }

}
