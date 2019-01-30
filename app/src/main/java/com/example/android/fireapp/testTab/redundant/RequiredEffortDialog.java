package com.example.android.fireapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.webianks.library.scroll_choice.ScrollChoice;

import org.w3c.dom.Text;

public class RequiredEffortDialog extends DialogFragment {

    private MyNumberPicker effortPicker;
    public interface RequiredEffortDialogInput{
        void onRequiredEffortSelected(int effort);
    }
    //todo fix picker - when nothing moves, value can t be selected
    public RequiredEffortDialog.RequiredEffortDialogInput mRequiredEffortPickerInput;

    public void setRequiredEffortDialogInput(RequiredEffortDialog.RequiredEffortDialogInput requiredEffortPicker){
        this.mRequiredEffortPickerInput = requiredEffortPicker;
    }





    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.efforort_level_dialog,null);
         effortPicker =  v.findViewById(R.id.effortLevelNumPicker);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.efforort_level_dialog, null));
        // Add action buttons
        builder.setMessage("Select effort level")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        effortPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                                mRequiredEffortPickerInput.onRequiredEffortSelected(i1);
                                Log.d("NUM", "onValueChange: ");
                            }
                        });
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.efforort_level_dialog,null);
        effortPicker =  v.findViewById(R.id.effortLevelNumPicker);
        effortPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mRequiredEffortPickerInput.onRequiredEffortSelected(i1);
                Log.d("NUM", "onValueChange: ");

            }
        });
    }
}
