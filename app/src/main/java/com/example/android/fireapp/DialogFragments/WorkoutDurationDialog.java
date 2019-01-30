package com.example.android.fireapp.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.example.android.fireapp.R;

public class WorkoutDurationDialog extends DialogFragment {

    private NumberPicker numberPickerHour;
    private NumberPicker numberPickerSec;
    private NumberPicker numberPickerMin;

    private DurationDialogListener durationDialogListener;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.duration_picker_dialog,null);
        builder.setView(view);
        numberPickerHour = view.findViewById(R.id.nPHour);
        numberPickerSec = view.findViewById(R.id.nPSeconds);
        numberPickerMin = view.findViewById(R.id.nPMinutes);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        // Add action buttons
        builder.setMessage("Select duration")
                .setPositiveButton("Ok ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int hourValue = numberPickerHour.getValue();
                        int minValue  = numberPickerMin.getValue();
                        int secValue  = numberPickerSec.getValue();
                        durationDialogListener.applyNumbers(hourValue,minValue,secValue);
                    }
                })
                .setNegativeButton(" Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            durationDialogListener = (DurationDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DurationDialogListener");
        }
    }


    public interface DurationDialogListener
    {
        void applyNumbers(int hour,int minutes,int seconds);
    }
}