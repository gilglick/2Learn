package com.example.a2learn.model;


import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Student {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String academicInstitution;
    private String uri;
    private List<String> giveHelpList;
    private List<String> needHelpList;

    public Student() {

    }

    public Student(String fullName, String email, String academicInstitution, String dateOfBirth, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.academicInstitution = academicInstitution;
        this.uri = "";
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


    public List<String> getGiveHelpList() {
        return giveHelpList;
    }

    public List<String> getNeedHelpList() {
        return needHelpList;
    }

    public void setGiveHelpList(List<String> giveHelpList) {
        this.giveHelpList = giveHelpList;
    }

    public void setNeedHelpList(List<String> needHelpList) {
        this.needHelpList = needHelpList;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public void setAcademicInstitution(String academicInstitution) {
        this.academicInstitution= academicInstitution;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAcademicInstitution() {
        return academicInstitution;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + email.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return email.equals(student.email);
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

    public String userNeedHelpListStringFormat() {
        StringBuilder sb = new StringBuilder();
        for (String course : needHelpList) {
            sb.append(course).append("\n");
        }
        return sb.toString();
    }

    public String userOfferListStringFormat() {
        StringBuilder sb = new StringBuilder();
        for (String course : giveHelpList) {
            sb.append(course).append("\n");
        }
        return sb.toString();
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
    public String getDefaultUri(){
        return "gs://learn-d79df.appspot.com/profileImages/defaultImage.JPG";
    }

}
