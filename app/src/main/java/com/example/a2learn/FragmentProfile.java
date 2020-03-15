package com.example.a2learn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2learn.model.SocialMedia;
import com.example.a2learn.model.Student;
import com.example.a2learn.model.StudentSetting;
import com.example.a2learn.utility.CircleTransform;

import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentProfile extends Fragment {
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    private StorageReference storageReference = fireStoreDatabase.getStorageDatabase().getReference(FireStoreDatabase.PROFILE_IMAGES_STORAGE);
    private Student student;
    private SocialMedia socialMedia;
    private StudentSetting studentSetting;
    private TextView userEmail;
    private TextView userName;
    private TextView userLocation;
    private TextView userPhoneNumber;
    private TextView userDate;
    private TextView userNeedHelpTextView;
    private TextView userOfferHelpTextView;
    private ImageView userImage;
    private Uri imageUri;
    private boolean readOnly;
    private static final int PICK_IMAGE_GALLERY = 1;
    private static final int PICK_IMAGE_CAMERA = 0;

    FragmentProfile(Student student) {
        this.student = student;
    }

    FragmentProfile(Student student, boolean readOnly) {
        this.student = student;
        this.readOnly = readOnly;
    }

    public FragmentProfile() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userImage = view.findViewById(R.id.userImage);
        userEmail = view.findViewById(R.id.emailTextView);
        userName = view.findViewById(R.id.userNameProfile);
        userLocation = view.findViewById(R.id.userLocationProfile);
        userPhoneNumber = view.findViewById(R.id.phoneNumberProfile);
        userDate = view.findViewById(R.id.dateOfBirthProfile);
        userOfferHelpTextView = view.findViewById(R.id.offerHelpList);
        userNeedHelpTextView = view.findViewById(R.id.needHelpList);
        ImageView facebookImage = view.findViewById(R.id.facebook);
        ImageView twitterImage = view.findViewById(R.id.twitter);
        ImageView linkedin = view.findViewById(R.id.linkedin);
        initializeProfile();
        facebookImage.setOnClickListener(v -> openWebPage(socialMedia.getFacebook()));
        twitterImage.setOnClickListener(v -> openWebPage(socialMedia.getTwitter()));
        linkedin.setOnClickListener(v -> openWebPage(socialMedia.getLinkedin()));


        view.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view12 = getActivity().getCurrentFocus();
            if (imm != null && view12 != null) {
                imm.hideSoftInputFromWindow(view12.getWindowToken(), 0);
            }
            return true;
        });
        return view;
    }

    private void initializeProfile() {
        getImageFromDatabase();
        readSocialMediaFromStorage();
        readUserSettingFromStorage();
        userName.setText(student.getFullName());
        userEmail.setText(student.getEmail());
        userLocation.setText(student.getAcademicInstitution());
        userPhoneNumber.setText(student.getPhoneNumber());
        userDate.setText(student.getDateOfBirth());
        userOfferHelpTextView.setText(student.userOfferListStringFormat());
        userNeedHelpTextView.setText(student.userNeedHelpListStringFormat());
        userImage.setOnClickListener(v -> {
            if (!readOnly)
                selectImage(getContext());
        });

    }


    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, PICK_IMAGE_CAMERA);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE_GALLERY);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case PICK_IMAGE_GALLERY:
                    imageUri = data.getData();
                    Picasso.get().load(imageUri).transform(new CircleTransform()).into(userImage);
                    uploadImageToDatabase();
                    break;
                case PICK_IMAGE_CAMERA:
                    Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    userImage.setImageBitmap(photo);
                    uploadImageToDatabase();

                    break;
            }
        }
    }


    /** Upload user's image to the database */
    private void uploadImageToDatabase() {
        storageReference = storageReference.child((student.getEmail()));
        storageReference.putFile(imageUri).continueWithTask(task -> storageReference.getDownloadUrl()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                if (downloadUri != null) {
                    student.setUri(downloadUri.toString());
                    fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.STUDENT_STORAGE, FireStoreDatabase.IMAGE_URI, downloadUri.toString());
                }
            }
        });
    }

    /**
     * Get user image from the database
     */
    private void getImageFromDatabase() {
        if (!student.getUri().matches("")) {
            storageReference = storageReference.child((student.getEmail()));
            storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Uri uri = task.getResult();
                    Picasso.get().load(uri).transform(new CircleTransform()).into(userImage);
                }
            }).addOnFailureListener(e -> Log.d(TAG, "getImageFromDatabase: " + "Download image failed"));
        } else {
            Picasso.get().load(R.drawable.no_picture_circle).transform(new CircleTransform()).into(userImage);
        }

    }

    /**
     * Read the social media url from the database
     */
    private void readSocialMediaFromStorage() {
        fireStoreDatabase.getDatabase()
                .collection(FireStoreDatabase.SOCIAL_MEDIA_STORAGE)
                .document(student.getEmail())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        socialMedia = documentSnapshot.toObject(SocialMedia.class);
                    } else {
                        socialMedia = new SocialMedia(getString(R.string.facebook),getString(R.string.twitter), getString(R.string.linkedin));
                        fireStoreDatabase.writeSocialMediaSetting(student.getEmail(), socialMedia);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + "Failed to read social media"));
    }

    /**
     * Read the user setting from the database
     */
    private void readUserSettingFromStorage() {
        fireStoreDatabase.getDatabase()
                .collection(FireStoreDatabase.SETTING_STORAGE)
                .document(student.getEmail())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        studentSetting = documentSnapshot.toObject(StudentSetting.class);
                    } else {
                        studentSetting = new StudentSetting();
                        fireStoreDatabase.writeUserSetting(student.getEmail(), studentSetting);
                    }
                    if (readOnly) {
                        setReadOnlyView();
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + "Failed to read user's setting from database"));
    }


    /**
     * Open the web while pressing on facebook, linkedin and twitter.
     */
    private void openWebPage(String url) {
        Uri webPage = Uri.parse(url);
        if (!url.startsWith(getString(R.string.http)) && !url.startsWith(getString(R.string.https))) {
            webPage = Uri.parse(getString(R.string.http) + url);
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, webPage);
        startActivity(browserIntent);

    }

    /**
     * Set user view to read only mode
     */
    private void setReadOnlyView() {
        if (!studentSetting.isDisplayEmail()) {
            userEmail.setVisibility(View.INVISIBLE);
        }
        if (!studentSetting.isDisplayPhone()) {
            userPhoneNumber.setVisibility(View.INVISIBLE);
        }
        if (!studentSetting.isDisplayDate()) {
            userDate.setVisibility(View.INVISIBLE);
        }
    }


}





