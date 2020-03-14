package com.example.a2learn.model;

import java.util.ArrayList;
import java.util.List;

public class Rating {
    private List<Double> ratings;

    public Rating(){
        ratings = new ArrayList<>();
    }

    public void addRate(double rate){
        ratings.add(rate);
    }

}
