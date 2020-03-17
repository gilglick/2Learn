package com.example.a2learn.utility;

import com.example.a2learn.R;
import com.example.a2learn.model.Rating;
import com.example.a2learn.model.Student;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.NonNull;

public class RatingDialog extends Dialog {
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    private RatingBar ratingBar;
    private Student caller;
    private Student callee;
    private float currentRate;

    public RatingDialog(@NonNull Context context, Student caller, Student callee) {
        super(context);
        this.caller = caller;
        this.callee = callee;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_dialog);
        ratingBar = findViewById(R.id.customRatingBar);
        Button rateMe = findViewById(R.id.rateMe);
        ratingBar.setNumStars(5);
        ratingBar.setOnRatingBarChangeListener((rateBar, rating, fromUser) -> currentRate = ratingBar.getRating());
        rateMe.setOnClickListener(v -> {
            updateRating(callee.getEmail(), caller.getEmail(), currentRate);
            calculateRating(callee);
        });
    }

    private void updateRating(String caller, String callee, float rating) {
        fireStoreDatabase.getDatabase()
                .collection(FireStoreDatabase.RATING)
                .document(caller)
                .update("ratings" + "." + fireStoreDatabase.encodeDot(callee), rating);
    }



    private void calculateRating(Student callee) {
        FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
        fireStoreDatabase.getDatabase().collection("rating").document(callee.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Rating rating = task.getResult().toObject(Rating.class);
                        if (rating != null) {
                            float rate = rating.calcRating();
                            fireStoreDatabase.getDatabase().collection("rating")
                                    .document(callee.getEmail()).update("currentRating", rate);
                        }
                    }
                });
    }
}
