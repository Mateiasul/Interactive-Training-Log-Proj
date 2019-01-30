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


public class EffortAlertDialogPicker extends DialogFragment {
    private NumberPicker numberPicker;



    private EffortPickerDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //inflater and layout used for custom dialog
        View view = inflater.inflate(R.layout.dialog_test_layout,null);

        builder.setView(view)
                .setMessage("Select Effort Level")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //if ok is clicked retrieve the value and pass it to the listener
                        int value = numberPicker.getValue();
                        listener.applyNumber(value);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    //do nothing, close the dialog
                    }
                });
        // Create the AlertDialog object and return it
        numberPicker = view.findViewById(R.id.effortLevelNumPickerTest);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        return builder.create();
    }

    //context will be the activity that the dialog is opened in
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EffortPickerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement example dialog listener");
        }
    }

    //interface to be implemented by activities that want to use the dialog
    //in order to be able to retrieve the selected value
    public interface EffortPickerDialogListener
    {
        //method to be implemented by activities that implement the interface
        void applyNumber(int value);
    }
}