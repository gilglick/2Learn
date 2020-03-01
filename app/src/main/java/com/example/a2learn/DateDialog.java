package com.example.a2learn;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.util.Calendar;
import java.util.Objects;

public final class DateDialog {
    public static DatePickerDialog.OnDateSetListener mDateSetListener;

    public static String dateFormat(int year, int month, int day) {
        return month + "/" + day + "/" + year;
    }

    public static DatePickerDialog createCalender(Activity activity) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                activity,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener, year, month, day);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

}
