package com.example.a2learn.model;

public class StudentSetting {

    private boolean displayEmail;
    private boolean displayPhone;
    private boolean displayDate;

    public StudentSetting(){
        setDisplayEmail(true);
        setDisplayPhone(true);
        setDisplayDate(true);
    }

    public boolean isDisplayEmail() {
        return displayEmail;
    }

    public boolean isDisplayPhone() {
        return displayPhone;
    }

    public boolean isDisplayDate() {
        return displayDate;
    }

    public void setDisplayEmail(boolean displayEmail) {
        this.displayEmail = displayEmail;
    }

    public void setDisplayPhone(boolean displayPhone) {
        this.displayPhone = displayPhone;
    }

    public void setDisplayDate(boolean displayDate) {
        this.displayDate = displayDate;
    }
}
