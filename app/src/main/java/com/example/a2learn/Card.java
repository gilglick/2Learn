package com.example.a2learn;

import android.widget.ImageView;

public class Card {
    private Student student;

    public Card(Student student) {
        this.student = student;
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