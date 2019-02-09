package com.example.android.fireapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.fireapp.DialogFragments.DialogDatePicker;
import com.example.android.fireapp.DialogFragments.DialogTimePicker;
import com.example.android.fireapp.DialogFragments.EffortAlertDialogPicker;
import com.example.android.fireapp.DialogFragments.WorkoutDurationDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddWorkoutActivity extends AppCompatActivity implements DialogDatePicker.DatePickerDialogInput
        ,TrainingTypes.TrainingTypePickerDialogInput,DialogTimePicker.TimePickerDialogInput,
         WorkoutDurationDialog.DurationDialogListener,
        EffortAlertDialogPicker.EffortPickerDialogListener {

    private TextView durationtV;
    private TextView effortLevelTV;
    private NumberPicker efforPicker;
    private TextView textViewDatePick;
    private TextView textViewTrainingType;
    private EditText mTrainingTitle;
    private ImageButton mAddTrainingImgButton;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private TextView mtextViewTimePicker;
    private String userName;
    private String userSquad;
    private Timestamp activityTimeStamp;
    private Button saveActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

            mFireBaseAuth = mFireBaseAuth.getInstance();
            mFirebaseFirestore = FirebaseFirestore.getInstance();

            final String userID = mFireBaseAuth.getCurrentUser().getUid();
            // Inflate the layout for this fragment

            saveActivityButton = findViewById(R.id.addWorkoutSaveBtn);
            effortLevelTV = findViewById(R.id.effortSelectorTV);
            mtextViewTimePicker = findViewById(R.id.textViewTimePicker);
            textViewDatePick = findViewById(R.id.textViewDatepicker);
            mTrainingTitle = findViewById(R.id.editTextTrainingTitle);
            textViewTrainingType = findViewById(R.id.textViewTrainingTypePicker);
            mAddTrainingImgButton = findViewById(R.id.addtrainingButton);
            durationtV = findViewById(R.id.durationSelectorTV);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Mateiasul's App");

            textViewDatePick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialog(view);
                }
            });
            textViewTrainingType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditDialog(view);
                }
            });
            mtextViewTimePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   showTimePickerDialog(view);
                }
            });
            durationtV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDurationSelectDialog();
                }
            });
            effortLevelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEffortLevelDialog();
                }
            });

            //when button clicked
            saveActivityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //get values from all the text fields
                    //check if empty
                    if (mTrainingTitle.getText().toString().isEmpty() || textViewDatePick.getText().toString().isEmpty()
                            || textViewTrainingType.getText().toString().isEmpty() || mtextViewTimePicker.getText().toString().isEmpty()
                            || effortLevelTV.toString().isEmpty() | durationtV.toString().isEmpty())
                    {
                        Toast.makeText(AddWorkoutActivity.this, "all fields must be entered", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        getUserNameSquad(new FirestoreCallback() {
                            @Override
                            public void onCallback(String userName, String userSquad) {
                                String trainingTitle = mTrainingTitle.getText().toString();
                                String trainingType = textViewTrainingType.getText().toString();
                                String trainingTime = mtextViewTimePicker.getText().toString();
                                String trainingDuration = durationtV.getText().toString();
                                //TODO use firestore timestamp for date, maybe even time, so events can be sorted - done i think
                                int effortLevelSelectedValue = Integer.parseInt(effortLevelTV.getText().toString());


                                //TODO add all compulsory fields - done

                                //make a map to be integrated in firebase cloud
                                //rather than using the activity object i just used the retrieved data
                                Map<String, Object> userTrainingMap = new HashMap<>();
                                userTrainingMap.put("Training Title",trainingTitle);
                                userTrainingMap.put("Training Date",activityTimeStamp);
                                userTrainingMap.put("Training Type",trainingType);
                                userTrainingMap.put("Training Time",trainingTime);
                                userTrainingMap.put("Training Duration",trainingDuration);
                                userTrainingMap.put("Effort Level",effortLevelSelectedValue);
                                userTrainingMap.put("User Name",userName);
                                userTrainingMap.put("User Squad",userSquad);



                                //TODO might need to make an entire different collection for the events-doneish
                                //retrieve the collection  that needs to be updated - and add the new created map
//                    mFirebaseFirestore.collection("Users").document(userID).collection("Events")

                                mFirebaseFirestore.collection("Events")
                                        .add(userTrainingMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(AddWorkoutActivity.this, "Training Logged Successful", Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(AddWorkoutActivity.this, "log unsuccessful  " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                   mFirebaseFirestore.collection("Users").document(userID).collection("Events")
                                        .add(userTrainingMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(AddWorkoutActivity.this, "Training Logged Successful", Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(AddWorkoutActivity.this, "log unsuccessful  " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        },userID);

                        SendToStart();

                    }
                }
            });
    }


    private void getUserNameSquad(final FirestoreCallback callback, String userId) {
        mFirebaseFirestore.collection("Users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            userName = task.getResult().get("LastName").toString();
                            userSquad = task.getResult().get("SquadName").toString();
                        }
                        callback.onCallback(userName,userSquad);
                    }
                });
    }




    private interface FirestoreCallback
    {
        void onCallback(String userName, String userSquad);
    }



    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DialogDatePicker();
        ((DialogDatePicker) newFragment).setDatePickerDialogInput(AddWorkoutActivity.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(java.util.Date date) {

        Calendar cal = Calendar.getInstance();
        activityTimeStamp = new Timestamp(date);
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String date_str = df.format(date);
        textViewDatePick.setText(date_str);
    }

    private void SendToStart() {
        Intent mainIntent = new Intent(AddWorkoutActivity.this,BottomNav.class);
        startActivity(mainIntent);
        finish();
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new DialogTimePicker();
        ((DialogTimePicker) newFragment).setTimePickerDialogInput(AddWorkoutActivity.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showEditDialog(View v) {
        DialogFragment newFragment = new TrainingTypes();
        ((TrainingTypes) newFragment).setTrainingTypePickerDialogInput(AddWorkoutActivity.this);
        newFragment.show(getSupportFragmentManager(), "TrainingTypes");
    }



    //retrieve values from training Type dialog
    @Override
    public void onTrainingTypeSelected(String training) {

        textViewTrainingType.setText(training);
    }


    //retrieve values from time dialog
    @Override
    public void onTimeSelected(String time) {
        mtextViewTimePicker.setText(time);
    }

    public void openEffortLevelDialog()
    {
        //open effort dialog
        EffortAlertDialogPicker effortLevelDialog = new EffortAlertDialogPicker();
        effortLevelDialog.show(getSupportFragmentManager(),"effort level dialog");
    }

    public void openDurationSelectDialog()
    {
        //open duration dialog
        WorkoutDurationDialog workoutDurationDialogFragment = new WorkoutDurationDialog();
        workoutDurationDialogFragment.show(getSupportFragmentManager(),"duration dialog fragment");
    }

    //retrieve values from effort dialog
    @Override
    public void onEffortSelected(int value) {
        effortLevelTV.setText(Integer.toString(value));
    }


    //retrieve values from duration dialog
    @Override
    public void onDurationSelected(int hour, int minutes, int seconds) {
        String time = Integer.toString(hour) + "h  " + Integer.toString(minutes) + "m";
        durationtV.setText(time);
    }

}

