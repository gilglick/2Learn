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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.a2learn.utility.CircleTransform;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentProfile extends Fragment implements DialogSetting.UpdateCallback {
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();

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
    private DialogSetting dialogSetting;
    private static final int PICK_IMAGE_GALLERY = 1;
    private static final int PICK_IMAGE_CAMERA = 0;

    FragmentProfile(Student student) {
        this.student = student;
        this.studentSetting = new StudentSetting();
    }

    public FragmentProfile() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userImage = view.findViewById(R.id.userImage);
        Button cameraButton = view.findViewById(R.id.cameraButton);
        userEmail = view.findViewById(R.id.emailTextView);
        CardView editButton = view.findViewById(R.id.editButton);
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
        cameraButton.setOnClickListener(v -> selectImage(getContext()));
        facebookImage.setOnClickListener(v -> openWebPage(socialMedia.getFacebook()));
        twitterImage.setOnClickListener(v -> openWebPage(socialMedia.getTwitter()));
        linkedin.setOnClickListener(v -> openWebPage(socialMedia.getLinkedin()));

        editButton.setOnClickListener(v -> {
            if (dialogSetting == null) {
                dialogSetting = new DialogSetting(Objects.requireNonNull(getActivity()), studentSetting);
            }
            dialogSetting.setCallback(this);
            dialogSetting.show();
        });

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
        userLocation.setText(student.getLocation());
        userPhoneNumber.setText(student.getPhoneNumber());
        userDate.setText(student.getDateOfBirth());
        userOfferHelpTextView.setText(student.userOfferListStringFormat());
        userNeedHelpTextView.setText(student.userNeedHelpListStringFormat());
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
                    uploadImageToStorage();
                    break;
                case PICK_IMAGE_CAMERA:
                    Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    userImage.setImageBitmap(photo);
                    uploadImageToStorage();

                    break;
            }
        }
    }


    private void uploadImageToStorage() {
        StorageReference storageReference = fireStoreDatabase.getStorageDatabase().getReference(FireStoreDatabase.PROFILE_IMAGES_STORAGE).child((student.getEmail()));
        storageReference.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_SHORT).show();
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                if (downloadUri != null) {
                    student.setUri(downloadUri.toString());
                    fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.STUDENT_STORAGE, FireStoreDatabase.IMAGE_URI, downloadUri.toString());
                }
            }
        });
    }

    private void getImageFromDatabase() {
        StorageReference storageReference = fireStoreDatabase.getStorageDatabase().getReference(FireStoreDatabase.PROFILE_IMAGES_STORAGE).child((student.getEmail()));
        storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Uri uri = task.getResult();
                Picasso.get().load(uri).transform(new CircleTransform()).into(userImage);
            } else {
                Picasso.get().load(R.drawable.no_picture_circle).transform(new CircleTransform()).into(userImage);
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Download image failed", Toast.LENGTH_SHORT).show());
    }

    private void readSocialMediaFromStorage() {
        fireStoreDatabase.getDatabase()
                .collection(FireStoreDatabase.SOCIAL_MEDIA_STORAGE)
                .document(student.getEmail())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        socialMedia = documentSnapshot.toObject(SocialMedia.class);
                    } else {
                        socialMedia = new SocialMedia(Utility.FACEBOOK_URL, Utility.TWITTER_URL, Utility.LINKEDIN_URL);
                        fireStoreDatabase.writeSocialMediaSetting(student.getEmail(), socialMedia);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: "));
    }


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
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: "));
    }


    private void openWebPage(String url) {

        Uri webPage = Uri.parse(url);

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            webPage = Uri.parse("http://" + url);
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, webPage);
        startActivity(browserIntent);

    }

    @Override
    public void notifyProfileOnUpdate(int fieldToUpdate, String newValue) {
        switch (fieldToUpdate) {
            case Utility.PHONE_NUMBER_INDICATOR:
                student.setPhoneNumber(userPhoneNumber.getText().toString());
                fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.STUDENT_STORAGE, FireStoreDatabase.PHONE_NUMBER, newValue);
                userPhoneNumber.setText(newValue);
                break;
            case Utility.FACEBOOK_INDICATOR:
                fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.SOCIAL_MEDIA_STORAGE, FireStoreDatabase.FACEBOOK, newValue);
                socialMedia.setFacebook(newValue);
                break;
            case Utility.TWITTER_INDICATOR:
                fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.SOCIAL_MEDIA_STORAGE, FireStoreDatabase.TWITTER, newValue);
                socialMedia.setTwitter(newValue);
                break;
            case Utility.LINKEDIN_INDICATOR:
                fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.SOCIAL_MEDIA_STORAGE, FireStoreDatabase.LINKEDIN, newValue);
                socialMedia.setLinkedin(newValue);
                break;
        }


    }

    @Override
    public void notifyOnUserPreferenceChanged(String field, boolean checked) {
        fireStoreDatabase.getDatabase()
                .collection(FireStoreDatabase.SETTING_STORAGE)
                .document(student.getEmail())
                .update(field, checked)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: field update successfully. "))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: failed to change the field. "));
    }

}





