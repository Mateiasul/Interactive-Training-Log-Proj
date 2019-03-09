package com.example.android.fireapp.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.fireapp.Activities.MainActivity;
import com.example.android.fireapp.DialogFragments.DialogDatePicker;
import com.example.android.fireapp.DialogFragments.DialogTimePicker;
import com.example.android.fireapp.DialogFragments.EffortAlertDialogPicker;
import com.example.android.fireapp.DialogFragments.WorkoutDurationDialog;
import com.example.android.fireapp.R;
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
import java.util.List;
import java.util.Map;

public class AddWorkoutActivityMDC extends AppCompatActivity implements DialogDatePicker.DatePickerDialogInput
        ,TrainingTypes.TrainingTypePickerDialogInput,DialogTimePicker.TimePickerDialogInput,
         WorkoutDurationDialog.DurationDialogListener,
        EffortAlertDialogPicker.EffortPickerDialogListener {



    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String userName;
    private String userSquad;
    private Timestamp activityTimeStamp;
    private Button saveActivityButton;
    private TextInputEditText textInputEditTextTitle;
    private TextInputEditText textInputEditTextDate;
    private TextInputEditText textInputEditTextTime;
    private TextInputEditText textInputEditTextType;

    private TextInputEditText textInputEditTextDuration;
    private TextInputEditText textInputEditTextEffort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout_mdc);

            mFireBaseAuth = mFireBaseAuth.getInstance();
            mFirebaseFirestore = FirebaseFirestore.getInstance();

            final String userID = mFireBaseAuth.getCurrentUser().getUid();
            // Inflate the layout for this fragment

                saveActivityButton = findViewById(R.id.addWorkoutSaveBtn);
                textInputEditTextTitle = findViewById(R.id.title_edit_text);
                textInputEditTextDate = findViewById(R.id.date_edit_text);
                textInputEditTextTime = findViewById(R.id.time_edit_text);
                textInputEditTextType = findViewById(R.id.type_edit_text);
                textInputEditTextDuration = findViewById(R.id.duration_edit_text);
                textInputEditTextEffort = findViewById(R.id.effort_edit_text);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Mateiasul's App");

        textInputEditTextDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialog(view);
                }
            });
        textInputEditTextType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditDialog(view);
                }
            });
        textInputEditTextTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   showTimePickerDialog(view);
                }
            });

        textInputEditTextDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDurationSelectDialog();
            }
        });
        textInputEditTextEffort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEffortLevelDialog();
            }
        });


        View rootView = findViewById(android.R.id.content);

        final List<TextInputLayout> textInputLayouts = com.example.android.fireapp.testTab.Utils.findViewsWithType(
                rootView, TextInputLayout.class);

        saveActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean noErrors = true;
                for (TextInputLayout textInputLayout : textInputLayouts) {
                    String editTextString = textInputLayout.getEditText().getText().toString();
                    if (editTextString.isEmpty()) {
                        textInputLayout.setError(getResources().getString(R.string.error_string));
                        noErrors = false;
                    } else {
                        textInputLayout.setError(null);
                    }
                }

                if (noErrors) {
                    Toast.makeText(AddWorkoutActivityMDC.this, "no empty fields", Toast.LENGTH_SHORT).show();
                        getUserNameSquad(new AddWorkoutActivityMDC.FirestoreCallback() {
                            @Override
                            public void onCallback(String userName, String userSquad) {
                                String trainingTitle = textInputEditTextTitle.getText().toString();
                                String trainingType = textInputEditTextType.getText().toString();
                                String trainingTime = textInputEditTextTime.getText().toString();
                                String trainingDuration = textInputEditTextDuration.getText().toString();
                                //TODO use firestore timestamp for date, maybe even time, so events can be sorted - done i think
                                int effortLevelSelectedValue = Integer.parseInt(textInputEditTextEffort.getText().toString());


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
                                            Toast.makeText(AddWorkoutActivityMDC.this, "Training Logged Successful", Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(AddWorkoutActivityMDC.this, "log unsuccessful  " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                mFirebaseFirestore.collection("Users").document(userID).collection("Events")
                                        .add(userTrainingMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(AddWorkoutActivityMDC.this, "Training Logged Successful", Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(AddWorkoutActivityMDC.this, "log unsuccessful  " + errorMessage, Toast.LENGTH_SHORT).show();
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

    //retrieve values from effort dialog
    @Override
    public void onEffortSelected(int value) {
        textInputEditTextEffort.setText(Integer.toString(value));
    }


    //retrieve values from duration dialog
    @Override
    public void onDurationSelected(int hour, int minutes, int seconds) {
        String time = Integer.toString(hour) + "h  " + Integer.toString(minutes) + "m";
        textInputEditTextDuration.setText(time);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private interface FirestoreCallback
    {
        void onCallback(String userName, String userSquad);
    }



    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DialogDatePicker();
        ((DialogDatePicker) newFragment).setDatePickerDialogInput(AddWorkoutActivityMDC.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(java.util.Date date) {

        Calendar cal = Calendar.getInstance();
        activityTimeStamp = new Timestamp(date);
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String date_str = df.format(date);
        textInputEditTextDate.setText(date_str);
    }

    private void SendToStart() {
        Intent mainIntent = new Intent(AddWorkoutActivityMDC.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new DialogTimePicker();
        ((DialogTimePicker) newFragment).setTimePickerDialogInput(AddWorkoutActivityMDC.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showEditDialog(View v) {
        DialogFragment newFragment = new TrainingTypes();
        ((TrainingTypes) newFragment).setTrainingTypePickerDialogInput(AddWorkoutActivityMDC.this);
        newFragment.show(getSupportFragmentManager(), "TrainingTypes");
    }



    //retrieve values from training Type dialog
    @Override
    public void onTrainingTypeSelected(String training) {

        textInputEditTextType.setText(training);
    }


    //retrieve values from time dialog
    @Override
    public void onTimeSelected(String time) {
        textInputEditTextTime.setText(time);
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



  /*  //retrieve values from duration dialog
*//*
    @Override
*//*
*//*    public void onDurationSelected(int hour, int minutes, int seconds) {
        String time = Integer.toString(hour) + "h  " + Integer.toString(minutes) + "m";
        durationtV.setText(time);
    }*/

}

