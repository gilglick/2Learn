package com.example.a2learn.model;

public class Card {
    private Student student;
    private Rating rating;

    public Card(Student student,Rating rating) {
        this.student = student;
        this.rating = rating;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Rating getRating() {
        return rating;
    }



    @Override
    public boolean equals(Object  o){
        if(!(o instanceof Card))
            return false;
        Card c = (Card)o;
        return student.equals(c.getStudent());
    }
}