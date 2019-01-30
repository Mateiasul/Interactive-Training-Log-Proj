package com.example.android.fireapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;


public  class DialogTimePicker extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {



    public interface TimePickerDialogInput{
        void onTimeSelected(String time);
    }

    public TimePickerDialogInput mTimePickerInput;

    public void setTimePickerDialogInput(TimePickerDialogInput datePicker){
        this.mTimePickerInput = datePicker;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog dialog = new TimePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth, this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        dialog.show();
        return dialog;

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the date chosen by the user
        String time = hourOfDay + " : " + minute;
        mTimePickerInput.onTimeSelected(time);
    }
}
