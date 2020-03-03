package com.example.a2learn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2learn.com.exmaple.a2learn.utility.CircleTransform;
import com.example.a2learn.com.exmaple.a2learn.utility.CompressImageHelper;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private FireStoreHelper fireStoreHelper = new FireStoreHelper();
    private StorageReference ref = fireStoreHelper.getmStorageRef().child(FireStoreHelper.PROFILE_STORAGE);
    private Student student;
    private Button editButton;
    private TextView userEmail;
    private EditText userName;
    private EditText userLocation;
    private EditText userPhoneNumber;
    private EditText userDate;
    private TextView userNeedHelpTextView;
    private TextView userOfferHelpTextView;
    private ImageView userImage;
    private boolean editMode = false;
    private static Uri imageUri;
    private static final int PICK_IMAGE_GALLERY = 1;
    private static final int PICK_IMAGE_CAMERA = 0;

    ProfileFragment(Student student) {
        this.student = student;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userImage = view.findViewById(R.id.userImage);
        Button cameraButton = view.findViewById(R.id.cameraButton);
        userEmail = view.findViewById(R.id.emailTextView);
        editButton = view.findViewById(R.id.editButton);
        userName = view.findViewById(R.id.userNameProfile);
        userLocation = view.findViewById(R.id.userLocationProfile);
        userPhoneNumber = view.findViewById(R.id.phoneNumberProfile);
        userDate = view.findViewById(R.id.dateOfBirthProfile);
        userOfferHelpTextView = view.findViewById(R.id.offerHelpList);
        userNeedHelpTextView = view.findViewById(R.id.needHelpList);
        ImageView facebookImage = view.findViewById(R.id.facebook);
        ImageView twitterImage = view.findViewById(R.id.twitter);
        ImageView linkedin = view.findViewById(R.id.linkedin);
        initializeUserProfile();
        setEditPermissions(false);
        editButton.setOnClickListener(v -> {
            if (editMode) {
                updateUserField();
            }
            editMode = !editMode;
            setEditPermissions(editMode);
        });

        cameraButton.setOnClickListener(v -> selectImage(getContext()));
        facebookImage.setOnClickListener(v -> Toast.makeText(getActivity(), "Facebook", Toast.LENGTH_SHORT).show());
        twitterImage.setOnClickListener(v -> Toast.makeText(getActivity(), "Twitter", Toast.LENGTH_SHORT).show());
        linkedin.setOnClickListener(v -> Toast.makeText(getActivity(), "Linkedin", Toast.LENGTH_SHORT).show());
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEditPermissions(boolean editMode) {
        editButton.setBackgroundResource(editMode ? R.drawable.edit_mode : R.drawable.edit);
        userName.setEnabled(editMode);
        userLocation.setEnabled(editMode);
        userPhoneNumber.setEnabled(editMode);
        userDate.setEnabled(editMode);
        userDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, editMode ? R.drawable.ic_date_range_black_24dp : 0, 0);
        userDate.setOnTouchListener((v, event) ->
        {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP && userDate.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                if (event.getRawX() >= (userDate.getRight() - userDate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    DateDialog.createCalender(getActivity()).show();
                    return true;
                }
            }
            return false;
        });
        DateDialog.mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            userDate.setText(DateDialog.dateFormat(year, month, day));
        };
    }


    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, PICK_IMAGE_CAMERA);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Sellect Picture"), PICK_IMAGE_GALLERY);
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
                    Toast.makeText(getActivity(), "from camera", Toast.LENGTH_SHORT).show();
                    updateUserImageInStorage();
                    getUserImageFromStorage();
                    break;
                case PICK_IMAGE_CAMERA:
//                    imageUri = data.getData();
//                    Picasso.get().load(imageUri).transform(new CircleTransform()).into(userImage);
                    break;
            }
        }
    }

    private void initializeUserProfile() {
        getUserImageFromStorage();
        userName.setText(student.getFullName());
        userEmail.setText(student.getEmail());
        userLocation.setText(student.getLocation());
        userPhoneNumber.setText(student.getPhoneNumber());
        userDate.setText(student.getDateOfBirth());
        userOfferHelpTextView.setText(student.userOfferListStringFormat());
        userNeedHelpTextView.setText(student.userNeedHelpListStringFormat());

    }

    private void updateUserField() {
        if (!userName.getText().toString().equals(student.getFullName())) {
            student.setFullName(userName.getText().toString());
            fireStoreHelper.updateField(student.getEmail(), FireStoreHelper.FULL_NAME, userName.getText().toString());
        }
        if (!userLocation.getText().toString().equals(student.getLocation())) {
            student.setLocation(userLocation.getText().toString());
            fireStoreHelper.updateField(student.getEmail(), FireStoreHelper.LOCATION, userLocation.getText().toString());
        }
        if (!userPhoneNumber.getText().toString().equals(student.getPhoneNumber())) {
            student.setPhoneNumber(userPhoneNumber.getText().toString());
            fireStoreHelper.updateField(student.getEmail(), FireStoreHelper.PHONE_NUMBER, userPhoneNumber.getText().toString());
        }
        if (!userDate.getText().toString().equals(student.getDateOfBirth())) {
            student.setDateOfBirth(userDate.getText().toString());
            fireStoreHelper.updateField(student.getEmail(), FireStoreHelper.DATE_OF_BIRTH, userDate.getText().toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void updateUserImageInStorage() {
        StorageReference reference = ref.child(student.getEmail());
        byte[] byteStream = CompressImageHelper.compressImage(getActivity(), imageUri);
        if (byteStream != null) {
            UploadTask uploadTask = reference.putBytes(byteStream);
            uploadTask.addOnSuccessListener(taskSnapshot -> Toast.makeText(getActivity(), "Uploaded Failed", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(exception -> Toast.makeText(getActivity(), "Uploaded Failed", Toast.LENGTH_SHORT).show());
        }
    }

    private void getUserImageFromStorage() {
        StorageReference reference = ref.child(student.getEmail());
        reference.getDownloadUrl().
                addOnSuccessListener(uri -> Picasso.get().load(uri).
                transform(new CircleTransform()).into(userImage))
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Falied get the image", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateUserField();

    }
}




