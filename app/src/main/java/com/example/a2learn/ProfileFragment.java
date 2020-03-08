package com.example.a2learn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.example.a2learn.com.exmaple.a2learn.utility.CircleTransform;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment implements ProfileEditDialog.UpdateCallback {
    private FireStoreHelper fireStoreHelper = new FireStoreHelper();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference(FireStoreHelper.PROFILE_STORAGE);

    private Student student;
    private TextView userEmail;
    private TextView userName;
    private TextView userLocation;
    private TextView userPhoneNumber;
    private TextView userDate;
    private TextView userNeedHelpTextView;
    private TextView userOfferHelpTextView;
    private ImageView userImage;
    private static Uri imageUri;
    private static final int PICK_IMAGE_GALLERY = 1;
    private static final int PICK_IMAGE_CAMERA = 0;


    ProfileFragment(Student student) {
        this.student = student;
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
        ProfileEditDialog profileEditDialog = new ProfileEditDialog(Objects.requireNonNull(getActivity()));
        initializeProfile();
        editButton.setOnClickListener(v -> {
            profileEditDialog.setCallback(this);
            profileEditDialog.show();
        });
        cameraButton.setOnClickListener(v -> selectImage(getContext()));
        facebookImage.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com"));
            startActivity(browserIntent);
        });
        twitterImage.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com"));
            startActivity(browserIntent);
        });
        linkedin.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com"));
            startActivity(browserIntent);
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
                    writeToDatabase();
                    break;
                case PICK_IMAGE_CAMERA:
                    Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    userImage.setImageBitmap(photo);
                    break;
            }
        }
    }

    private void initializeProfile() {
        getImageFromDatabase();
        userName.setText(student.getFullName());
        userEmail.setText(student.getEmail());
        userLocation.setText(student.getLocation());
        userPhoneNumber.setText(student.getPhoneNumber());
        userDate.setText(student.getDateOfBirth());
        userOfferHelpTextView.setText(student.userOfferListStringFormat());
        userNeedHelpTextView.setText(student.userNeedHelpListStringFormat());

    }

    private void updateUserUriField(String uri) {
        fireStoreHelper.updateField(student.getEmail(), FireStoreHelper.IMAGE_URI, uri);
    }


    private void writeToDatabase() {
        storageReference = storageReference.child((student.getEmail()));
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
                    updateUserUriField(downloadUri.toString());
                }
            }
        });
    }

    private void getImageFromDatabase() {
        storageReference = storageReference.child((student.getEmail()));
        storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri uri = task.getResult();
                Picasso.get().load(uri).transform(new CircleTransform()).into(userImage);
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Download image failed", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void notifyProfileOnUpdate(int fieldToUpdate, String newValue) {
        switch (fieldToUpdate) {
            case Utility.USER_NAME_INDICATOR:
                student.setFullName(userName.getText().toString());
                fireStoreHelper.updateField(student.getEmail(), FireStoreHelper.FULL_NAME, newValue);
                userName.setText(newValue);
                break;
            case Utility.PHONE_NUMBER_INDICATOR:
                student.setPhoneNumber(userPhoneNumber.getText().toString());
                fireStoreHelper.updateField(student.getEmail(), FireStoreHelper.PHONE_NUMBER, newValue);
                userPhoneNumber.setText(newValue);
                break;
            case Utility.FACEBOOK_INDICATOR:
                break;
            case Utility.TWITTER_INDICATOR:
                break;
            case Utility.LINKEDIN_INDICATOR:
                break;
        }


    }

}





