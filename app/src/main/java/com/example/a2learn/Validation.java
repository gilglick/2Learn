package com.example.a2learn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public final class Validation {

    public static boolean isValidPassword(final String password) {
        /*

        ^                 # start-of-string
        (?=.*[0-9])       # a digit must occur at least once
        (?=.*[a-z])       # a lower case letter must occur at least once
        (?=.*[A-Z])       # an upper case letter must occur at least once
        (?=.*[@#$%^&+=])  # a special character must occur at least once
        (?=\S+$)          # no whitespace allowed in the entire string
        .{8,}             # anything, at least eight places though
        $                 # end-of-string
         */
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isValidEmail(String emailStr) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches() && emailStr.trim().length() != 0;
    }

    public static boolean isValidFacebookUrl(String facebookUrl) {
        final Pattern VALID_FACEBOOK_URL = Pattern.compile("((http|https)://)?(www[.])?facebook.com/.+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_FACEBOOK_URL.matcher(facebookUrl);
        return matcher.matches() && facebookUrl.trim().length() != 0;
    }

    public static boolean isValidTwitterUrl(String twitterUrl) {
        final Pattern VALID_FACEBOOK_URL = Pattern.compile("((http|https)://)?(www[.])?twitter.com/.+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_FACEBOOK_URL.matcher(twitterUrl);
        return matcher.matches() && twitterUrl.trim().length() != 0;
    }

    public static boolean isValidLinkedinkUrl(String linkedinUrl) {
        final Pattern VALID_FACEBOOK_URL = Pattern.compile("((http|https)://)?(www[.])?linkedin.com/.+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_FACEBOOK_URL.matcher(linkedinUrl);
        return matcher.matches() && linkedinUrl.trim().length() != 0;
    }

    public static boolean isValidName(String name) {
        return !name.trim().matches("");
    }

    public static boolean isValidDate(String date) {
        return !date.trim().matches("");
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.trim().length() != 0;
    }
}


