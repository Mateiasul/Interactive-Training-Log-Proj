package com.example.android.fireapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class DialogDatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public interface DatePickerDialogInput{
        void onDateSelected(Date date);
    }

    public DatePickerDialogInput mDatePickerInput;

    public void setDatePickerDialogInput(DatePickerDialogInput datePicker){
        this.mDatePickerInput = datePicker;
    }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog  = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,this, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            return dialog;
        }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        // Do something with the date chosen by the user
        //might not need this anymore with the new date implementation
        //month = month + 1;
        //String date =  day + "/" + month + "/" + year ;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date date = cal.getTime();

        mDatePickerInput.onDateSelected(date); //Changed
    }



}


