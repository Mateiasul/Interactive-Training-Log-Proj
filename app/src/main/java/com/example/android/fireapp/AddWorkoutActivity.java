package com.example.android.fireapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddWorkoutActivity extends AppCompatActivity implements DialogDatePicker.DatePickerDialogInput
        ,TrainingTypes.TrainingTypePickerDialogInput,DialogTimePicker.TimePickerDialogInput,
        RequiredEffortDialog.RequiredEffortDialogInput, NumberPicker.OnValueChangeListener{

    private TextView duration;
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
            duration = findViewById(R.id.durationSelectorTV);

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

            final WorkoutDurationDialog workoutDurationDialogFragment = new WorkoutDurationDialog();
            duration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    workoutDurationDialogFragment.show(getSupportFragmentManager(), "fireeeeee");
                }
            });

            effortLevelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEffortLevelDialog();
                }
            });

            //when button clicked
            saveActivityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //get values from all the text fields
                    if (mTrainingTitle.getText().toString().isEmpty() || textViewDatePick.getText().toString().isEmpty()
                            || textViewTrainingType.getText().toString().isEmpty() || mtextViewTimePicker.getText().toString().isEmpty()
                            || Integer.toString(efforPicker.getValue()).isEmpty())
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

                                //TODO use firestore timestamp for date, maybe even time, so events can be sorted
                                int effortLevelSelectedValue = efforPicker.getValue();
                                //create new activity object
/*                    ActivityLogs log = new ActivityLogs(trainingTitle,trainingType,
                            trainingDate,trainingTime,
                            effortLevelSelectedValue);*/
                                //TODO add all compulsory fields - done

                                //make a map to be integrated in firebase cloud
                                //rather than using the activity object i just used the retrieved data
                                Map<String, Object> userTrainingMap = new HashMap<>();
                                userTrainingMap.put("Training Title",trainingTitle);
                                userTrainingMap.put("Training Date",activityTimeStamp);
                                userTrainingMap.put("Training Type",trainingType);
                                userTrainingMap.put("Training Time",trainingTime);
                                userTrainingMap.put("Effort Level",effortLevelSelectedValue);
                                userTrainingMap.put("User Name",userName);
                                userTrainingMap.put("User Squad",userSquad);



                                //TODO might need to make an entire different collection for the events
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


    @Override
    public void onTrainingTypeSelected(String training) {

        textViewTrainingType.setText(training);
    }



    @Override
    public void onTimeSelected(String time) {
        mtextViewTimePicker.setText(time);
    }




    @Override
    public void onRequiredEffortSelected(int effort) {
        effortLevelTV.setText(Integer.toString(effort));


    }

    public void showEffortLevelDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddWorkoutActivity.this);

        // Get the layout inflater
        LayoutInflater inflater = AddWorkoutActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.efforort_level_dialog,null);
        efforPicker = v.findViewById(R.id.effortLevelNumPicker);
        builder.setView(v);
        builder.setMessage("Select effort level")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(" Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        efforPicker.setOnValueChangedListener(this);
        builder.show();
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        effortLevelTV.setText(Integer.toString(i1));
    }

}

