package com.example.a2learn;

@SuppressWarnings("WeakerAccess")
public class SocialMedia {

    private String facebook;
    private String twitter;
    private String linkedin;

    SocialMedia() {

    }

    SocialMedia(String facebook, String twitter, String linkedin) {
        this.facebook = facebook;
        this.twitter = twitter;
        this.linkedin = linkedin;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }
}


