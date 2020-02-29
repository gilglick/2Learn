package com.example.a2learn;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import java.util.HashMap;

public class Student implements Parcelable {
    private String fullName;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;
    private String studentDescription;
    private ImageView image;
    private HashMap<Course, Integer> hashmap = new HashMap<Course, Integer>();

    public Student(String fullName, String email, String dateOfBirth, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
    }


    protected Student(Parcel in) {
        fullName = in.readString();
        email = in.readString();
        dateOfBirth = in.readString();
        phoneNumber = in.readString();
        studentDescription = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };


    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getStudentDescription() {
        return studentDescription;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }



    public ImageView getImage() {
        return image;
    }

    public HashMap<Course, Integer> getHashmap() {
        return hashmap;
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
                + "student description: " + studentDescription + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(dateOfBirth);
        dest.writeString(phoneNumber);
        dest.writeString(studentDescription);
    }
}
