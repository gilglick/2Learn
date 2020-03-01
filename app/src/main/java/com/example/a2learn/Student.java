package com.example.a2learn;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Student {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String location;
    private String description;
    private List<Course> giveHelpList;
    private List<Course> needHelpList;

    public Student(){

    }
    public Student(String fullName, String email, String location, String dateOfBirth, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.description = "";
        giveHelpList = new ArrayList<>();
        needHelpList = new ArrayList<>();
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public List<Course> getGiveHelpList() {
        return giveHelpList;
    }

    public List<Course> getNeedHelpList() {
        return needHelpList;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + email.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Student other = (Student) obj;
        return true;
    }

    @Override
    public String toString() {
        return "Student: " + "\n" +
                "fullName: " + fullName + "\n"
                + "email: " + email + "\n"
                + "phone: " + phoneNumber + "\n"
                + "date of birth: " + dateOfBirth + "\n"
                + "student description: " + "\n";
    }


}
