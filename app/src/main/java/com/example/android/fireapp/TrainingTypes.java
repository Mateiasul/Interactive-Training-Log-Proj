package com.example.android.fireapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.webianks.library.scroll_choice.ScrollChoice;
import java.util.ArrayList;
import java.util.List;
public class TrainingTypes extends DialogFragment  {


    public TrainingTypes(){

    }
    private TextView textViewTrainingType;
    private ScrollChoice scrollChoice;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private List<String> trainingTypes = new ArrayList<>();

    public interface TrainingTypePickerDialogInput{
        void onTrainingTypeSelected(String training);
    }

    public TrainingTypes.TrainingTypePickerDialogInput mTrainingTypePickerInput;

    public void setTrainingTypePickerDialogInput(TrainingTypes.TrainingTypePickerDialogInput trainingTypePicker){
        this.mTrainingTypePickerInput = trainingTypePicker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.training_type, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        scrollChoice =  view.findViewById(R.id.scroll_choice);

        //textViewTrainingType =  view.findViewById(R.id.training_types);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = mFirebaseFirestore.collection("Training Types");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        trainingTypes.add(document.getString("name").toString());
                        Log.d("testtt",document.getString("name").toString());
                    }

                }

                scrollChoice.addItems(trainingTypes,3);


                scrollChoice.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                        Log.d("webi",name);
                        getDialog().dismiss();
                        mTrainingTypePickerInput.onTrainingTypeSelected(name); //Changed

                    }
                });
            }
        });


    }

}


