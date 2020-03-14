package com.example.a2learn.model;

import java.util.HashMap;
import java.util.Map;

public class Rating {
    private Map<String, Float> ratings = new HashMap<>();
    private float currentRating;

    public Rating() {
        this.currentRating = 0F;
    }

    public Map<String, Float> getRatings() {
        return ratings;
    }


    public float calcRating() {
        float rating = 0;
        for (Float rate : ratings.values()) {
            rating += rate;
        }
        return rating / ratings.size();
    }


    public float getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(Float currentRating) {
        this.currentRating = currentRating;
    }
}
