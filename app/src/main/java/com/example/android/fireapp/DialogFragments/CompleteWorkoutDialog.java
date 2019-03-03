package com.example.android.fireapp.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.example.android.fireapp.R;

import org.w3c.dom.Text;

public class CompleteWorkoutDialog extends DialogFragment {

/*    public static CompleteWorkoutDialog newInstance(int title) {
        CompleteWorkoutDialog frag = new CompleteWorkoutDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        frag.setArguments(args);
        return frag;
    }*/

    public interface CompleteWorkoutListener
    {
        void onCompletedWorkoutSelected(String score,String time);

    }

    private CompleteWorkoutListener completeWorkoutListener;
    private TextInputEditText completeScore;
    private TextInputEditText completeTime;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.complete_workout_dialog,null);
        builder.setView(view);
        completeScore = view.findViewById(R.id.score_edit_text);
        completeTime = view.findViewById(R.id.time_edit_text);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        // Add action buttons
        builder.setMessage("")
                .setPositiveButton("Ok ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        completeWorkoutListener.onCompletedWorkoutSelected(completeScore.getText().toString(), completeTime.getText().toString());
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
            completeWorkoutListener = (CompleteWorkoutListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement CompleteWorkoutListener");
        }
    }



}