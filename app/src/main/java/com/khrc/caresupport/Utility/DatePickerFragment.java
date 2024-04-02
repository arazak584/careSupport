package com.khrc.caresupport.Utility;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final String bundleKey;
    private final Calendar calendar;

    public DatePickerFragment(final String bundleKey, final Calendar calendar){
        this.bundleKey = bundleKey;
        this.calendar = calendar;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(requireContext(), AlertDialog.THEME_TRADITIONAL, this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        final String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(c.getTime());
        Bundle result = new Bundle();
        result.putString(bundleKey, selectedDate);

        getParentFragmentManager().setFragmentResult("requestKey", result);

    }
}
