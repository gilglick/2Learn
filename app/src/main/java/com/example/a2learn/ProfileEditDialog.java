package com.example.a2learn;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;


import java.util.Objects;

// In this class i need to fix the error message which right now is not correct
public class ProfileEditDialog extends Dialog {
    private EditText userName;
    private EditText userPhoneNumber;
    private EditText userFacebook;
    private EditText userTwitter;
    private EditText userLinkedin;
    private UpdateCallback updateCallback;
    private SparseArray<String> sparseArray = new SparseArray<>();

    ProfileEditDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_popup);
        setCanceledOnTouchOutside(false);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userName = findViewById(R.id.userNameDialogEditText);
        Switch userNameSwitch = findViewById(R.id.userNameDialogSwitch);
        userPhoneNumber = findViewById(R.id.phoneNumberDialogEditText);
        Switch userPhoneNumberSwitch = findViewById(R.id.phoneNumberDialogSwitch);
        userFacebook = findViewById(R.id.facebookDialogEditText);
        Switch userFacebookSwitch = findViewById(R.id.facebookDialogSwitch);
        userTwitter = findViewById(R.id.twitterDialogEditText);
        Switch userTwitterSwitch = findViewById(R.id.twitterDialogSwitch);
        userLinkedin = findViewById(R.id.linkedinDialogEditText);
        Switch userLinkedinSwitch = findViewById(R.id.linkedinDialogSwitch);
        CardView confirmEdit = findViewById(R.id.confirmChangesButton);
        CardView exitExit = findViewById(R.id.cancelChanges);
        Switch[] switches = {userNameSwitch, userPhoneNumberSwitch, userFacebookSwitch, userTwitterSwitch, userLinkedinSwitch};
        EditText[] editTexts = {userName, userPhoneNumber, userFacebook, userTwitter, userLinkedin};
        initSwitches(switches);
        initEditTexts(editTexts);
        userNameSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userName.setEnabled(isChecked));
        userPhoneNumberSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userPhoneNumber.setEnabled(isChecked));
        userFacebookSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userFacebook.setEnabled(isChecked));
        userTwitterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userTwitter.setEnabled(isChecked));
        userLinkedinSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userLinkedin.setEnabled(isChecked));

        confirmEdit.setOnClickListener(v -> {
            boolean hasChanged = false, hasError = false;
            for (int i = 0; i < switches.length; i++) {
                if (switches[i].isChecked()) {
                    if (!changeField(switches[i], editTexts[i], i)) {
                        hasError = true;
                    } else {
                        hasChanged = true;
                    }
                }
            }
            if (hasChanged && !hasError) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    int key = sparseArray.keyAt(i);
                    updateCallback.notifyProfileOnUpdate(key, sparseArray.get(key));
                }
                displayUpdateAlert();
            }
        });
        exitExit.setOnClickListener(v -> {
            initSwitches(switches);
            initEditTexts(editTexts);
            dismiss();
        });


    }


    private boolean changeField(Switch userSwitch, EditText editText, int indicator) {
        boolean validData = false;
        if (userSwitch.isChecked() && updateCallback != null) {
            String newValue = editText.getText().toString();
            switch (indicator) {
                case Utility.USER_NAME_INDICATOR:
                    validData = Validation.isValidName(newValue);
                    break;
                case Utility.PHONE_NUMBER_INDICATOR:
                    validData = Validation.isValidPhoneNumber(newValue);
                    break;
                case Utility.FACEBOOK_INDICATOR:
                    validData = Validation.isValidFacebookUrl(newValue);
                    break;
                case Utility.TWITTER_INDICATOR:
                    validData = Validation.isValidTwitterUrl(newValue);
                    break;
                case Utility.LINKEDIN_INDICATOR:
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your profile has been updated")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        alert.show();
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

    void setCallback(UpdateCallback update) {
        this.updateCallback = update;

    }

    interface UpdateCallback {
        void notifyProfileOnUpdate(int field, String newValue);
    }


}
