package com.example.a2learn.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2learn.utility.FireStoreDatabase;
import com.example.a2learn.R;
import com.example.a2learn.utility.Validation;
import com.example.a2learn.model.Student;
import com.example.a2learn.model.StudentSetting;

import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentSetting extends Fragment {
    private FireStoreDatabase fireStoreDatabase = FireStoreDatabase.getInstance();
    private EditText userPhoneNumber;
    private EditText userFacebook;
    private EditText userTwitter;
    private EditText userLinkedin;
    private CheckBox emailCheckBox;
    private CheckBox phoneNumberCheckBox;
    private CheckBox dateOfBirthCheckBox;
    private StudentSetting studentSetting;
    private Student student;
    private final int PHONE_NUMBER_INDICATOR = 0;
    private final int FACEBOOK_INDICATOR = 1;
    private final int TWITTER_INDICATOR = 2;
    private final int LINKEDIN_INDICATOR = 3;
    private SparseArray<String> sparseArray = new SparseArray<>();

    public FragmentSetting(Student student) {
        this.student = student;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        userPhoneNumber = view.findViewById(R.id.phoneNumberDialogEditText);
        Switch userPhoneNumberSwitch = view.findViewById(R.id.phoneNumberDialogSwitch);
        userFacebook = view.findViewById(R.id.facebookDialogEditText);
        Switch userFacebookSwitch = view.findViewById(R.id.facebookDialogSwitch);
        userTwitter = view.findViewById(R.id.twitterDialogEditText);
        Switch userTwitterSwitch = view.findViewById(R.id.twitterDialogSwitch);
        userLinkedin = view.findViewById(R.id.linkedinDialogEditText);
        Switch userLinkedinSwitch = view.findViewById(R.id.linkedinDialogSwitch);
        Button confirmEdit = view.findViewById(R.id.confirmChangesButton);
        emailCheckBox = view.findViewById(R.id.emailCheckBox);
        phoneNumberCheckBox = view.findViewById(R.id.phoneNumberCheckBox);
        dateOfBirthCheckBox = view.findViewById(R.id.dateOfBirthCheckBox);

        Switch[] switches = {userPhoneNumberSwitch, userFacebookSwitch, userTwitterSwitch, userLinkedinSwitch};
        EditText[] editTexts = {userPhoneNumber, userFacebook, userTwitter, userLinkedin};
        initSwitches(switches);
        initEditTexts(editTexts);
        initUserSetting();
        userPhoneNumberSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userPhoneNumber.setEnabled(isChecked));
        userFacebookSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userFacebook.setEnabled(isChecked));
        userTwitterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userTwitter.setEnabled(isChecked));
        userLinkedinSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userLinkedin.setEnabled(isChecked));
        emailCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> notifyOnUserPreferenceChanged(FireStoreDatabase.DISPLAY_EMAIL, isChecked));
        phoneNumberCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> notifyOnUserPreferenceChanged(FireStoreDatabase.DISPLAY_PHONE, isChecked));
        dateOfBirthCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> notifyOnUserPreferenceChanged(FireStoreDatabase.DISPLAY_DATA, isChecked));


        confirmEdit.setOnClickListener(v -> {
            boolean hasChanged = false, hasError = false;
            for (int i = 0; i < switches.length; i++) {
                if (switches[i].isChecked()) {
                    if (!validField(switches[i], editTexts[i], i)) {
                        hasError = true;
                    } else {
                        hasChanged = true;
                    }
                }
            }
            if (hasChanged && !hasError) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    int key = sparseArray.keyAt(i);
                    notifyProfileOnUpdate(key, sparseArray.get(key));
                }
                displayUpdateAlert();
            }
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


    private void initSwitches(Switch[] switches) {
        for (Switch s : switches) {
            s.setChecked(false);
        }
    }

    private void initEditTexts(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            editText.getText().clear();
            editText.setEnabled(false);
            editText.setError(null);
        }
    }

    private void initUserSetting() {
        fireStoreDatabase.getDatabase().collection(FireStoreDatabase.SETTING_STORAGE)
                .document(student.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        studentSetting = task.getResult().toObject(StudentSetting.class);
                        if (studentSetting != null) {
                            emailCheckBox.setChecked(studentSetting.isDisplayEmail());
                            phoneNumberCheckBox.setChecked(studentSetting.isDisplayPhone());
                            dateOfBirthCheckBox.setChecked(studentSetting.isDisplayDate());
                        }
                    }
                });

    }

    private boolean validField(Switch userSwitch, EditText editText, int indicator) {
        boolean validData = false;
        if (userSwitch.isChecked()) {
            String newValue = editText.getText().toString();
            switch (indicator) {
                case PHONE_NUMBER_INDICATOR:
                    validData = true;
                    break;
                case FACEBOOK_INDICATOR:
                    validData = Validation.isValidFacebookUrl(newValue);
                    break;
                case TWITTER_INDICATOR:
                    validData = Validation.isValidTwitterUrl(newValue);
                    break;
                case LINKEDIN_INDICATOR:
                    validData = Validation.isValidLinkedinkUrl(newValue);
                    break;
            }
            if (validData) {
                sparseArray.put(indicator, newValue);
            } else {
                editText.setError("" + indicator);
            }

        }
        return validData;
    }


    private void displayUpdateAlert() {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your profile has been updated")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> {
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void notifyProfileOnUpdate(int fieldToUpdate, String newValue) {
        switch (fieldToUpdate) {
            case PHONE_NUMBER_INDICATOR:
                fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.STUDENT_STORAGE, FireStoreDatabase.PHONE_NUMBER, newValue);
                break;
            case FACEBOOK_INDICATOR:
                fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.SOCIAL_MEDIA_STORAGE, FireStoreDatabase.FACEBOOK, newValue);
                break;
            case TWITTER_INDICATOR:
                fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.SOCIAL_MEDIA_STORAGE, FireStoreDatabase.TWITTER, newValue);
                break;
            case LINKEDIN_INDICATOR:
                fireStoreDatabase.updateField(student.getEmail(), FireStoreDatabase.SOCIAL_MEDIA_STORAGE, FireStoreDatabase.LINKEDIN, newValue);
                break;
        }
    }

    private void notifyOnUserPreferenceChanged(String field, boolean checked) {
        fireStoreDatabase.getDatabase()
                .collection(FireStoreDatabase.SETTING_STORAGE)
                .document(student.getEmail())
                .update(field, checked)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: field update successfully. "))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: failed to change the field. "));
    }

}

