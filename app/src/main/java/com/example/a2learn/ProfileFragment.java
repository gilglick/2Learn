package com.example.a2learn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private Button editButton;
    private EditText userNameProfile;
    private EditText userLocationProfile;
    private EditText phoneNumber;
    private EditText date;
    private EditText userAbout;
    private ImageView imgCapture;
    private boolean active = false;
    private static final int PICK_IMAGE_GALLERY = 1;
    private static final int PICK_IMAGE_CAMERA = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imgCapture = view.findViewById(R.id.userImage);
        Button cameraButton = view.findViewById(R.id.cameraButton);
        editButton = view.findViewById(R.id.editButton);
        userNameProfile = view.findViewById(R.id.userNameProfile);
        userLocationProfile = view.findViewById(R.id.userLocationProfile);
        phoneNumber = view.findViewById(R.id.phoneNumberProfile);
        date = view.findViewById(R.id.dateOfBirthProfile);
        userAbout = view.findViewById(R.id.userAboutProfile);
        ImageView facebookImage = view.findViewById(R.id.facebook);
        ImageView twitterImage = view.findViewById(R.id.twitter);
        ImageView linkedin = view.findViewById(R.id.linkedin);
        setEditPermissions(false);

        editButton.setOnClickListener(v -> {
            active = !active;
            setEditPermissions(active);

        });

        cameraButton.setOnClickListener(v -> selectImage(getContext()));
        facebookImage.setOnClickListener(v -> Toast.makeText(getActivity(), "Facebook", Toast.LENGTH_SHORT).show());
        twitterImage.setOnClickListener(v -> Toast.makeText(getActivity(), "Twitter", Toast.LENGTH_SHORT).show());
        linkedin.setOnClickListener(v -> Toast.makeText(getActivity(), "Linkedin", Toast.LENGTH_SHORT).show());
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEditPermissions(boolean active) {
        editButton.setBackgroundResource(active ? R.drawable.edit_mode : R.drawable.edit);
        userNameProfile.setEnabled(active);
        userLocationProfile.setEnabled(active);
        phoneNumber.setEnabled(active);
        userAbout.setEnabled(active);
        date.setEnabled(active);
        date.setCompoundDrawablesWithIntrinsicBounds(0, 0, active ? R.drawable.ic_date_range_black_24dp : 0, 0);
        date.setOnTouchListener((v, event) ->
        {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP && date.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                if (event.getRawX() >= (date.getRight() - date.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    DateDialog.createCalender(getActivity()).show();
                    return true;
                }
            }
            return false;
        });
        DateDialog.mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            date.setText(DateDialog.dateFormat(year, month, day));
        };


    }


    private boolean isProfileChanged(Student student) {
        return isFieldChanged(userNameProfile.getText().toString(), student.getFullName())
                || isFieldChanged(userLocationProfile.getText().toString(), student.getLocation())
                || isFieldChanged(phoneNumber.getText().toString(), student.getPhoneNumber())
                || isFieldChanged(date.getText().toString(), student.getDateOfBirth())
                || isFieldChanged(userAbout.getText().toString(), student.getStudentDescription());
    }

    private boolean isFieldChanged(String currentProfile, String currentField) {
        return !currentProfile.equals(currentField);
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
                startActivityForResult(Intent.createChooser(gallery, "Sellect Picture"), PICK_IMAGE_GALLERY);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            imgCapture.setImageURI(data.getData());
            Uri imageUri = data.getData();
            Picasso.get().load(imageUri).transform(new CircleTransform()).into(imgCapture);
        } else if (requestCode == PICK_IMAGE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Objects.requireNonNull(data.getExtras()).get("data");
                imgCapture.setImageBitmap((Bitmap) data.getExtras().get("data"));
            }
        }
    }


}




/*
  @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(" ");
                alert.setMessage("Message");

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // Do something with value!
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
 */