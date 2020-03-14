package com.example.a2learn.model;

public class Card {
    private Student student;
    private Rating rating;
    public Card(Student student,Rating rating) {
        this.student = student;
        this.rating = rating;
    }

    public Rating getRating() {
        return rating;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getEmail() {
        return student.getEmail();
    }

    public String getFullName() {
        return student.getFullName();
    }

    public String getUserNeedHelpListStringFormat() {
        return student.userNeedHelpListStringFormat();
    }

    public String getUserOfferListStringFormat() {
        return student.userOfferListStringFormat();
    }

    @Override
    public boolean equals(Object  o){
        if(!(o instanceof Card))
            return false;
        Card c = (Card)o;
        return student.equals(c.getStudent());
    }
}