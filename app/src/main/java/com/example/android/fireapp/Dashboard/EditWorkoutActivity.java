package com.example.android.fireapp.Dashboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.fireapp.DialogFragments.DialogDatePicker;
import com.example.android.fireapp.DialogFragments.DialogTimePicker;
import com.example.android.fireapp.DialogFragments.EffortAlertDialogPicker;
import com.example.android.fireapp.DialogFragments.WorkoutDurationDialog;
import com.example.android.fireapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditWorkoutActivity extends AppCompatActivity implements DialogDatePicker.DatePickerDialogInput
        ,TrainingTypes.TrainingTypePickerDialogInput,DialogTimePicker.TimePickerDialogInput,
        WorkoutDurationDialog.DurationDialogListener,
        EffortAlertDialogPicker.EffortPickerDialogListener{

    private TextView usernameTV;
    private TextView dateTV;
    private TextView titleTV;
    private TextView durationTV;
    private TextView effortLevelTV;
    private TextView typeTV;
    private TextView timeTV;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String userID;
    private String userName;
    private String userSquad;
    private String workoutDocRef;
    private String workoutEventsDocRef;
    private Button saveWorkoutEditButtom;
    private Timestamp activityTimeStamp;
    private Map<String, Object> userTrainingMap;
    private String docID;
    private Date origDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);

        //set all fields
        dateTV = findViewById(R.id.textViewDatepicker);
        titleTV = findViewById(R.id.editTextTrainingTitle);
        durationTV = findViewById(R.id.durationSelectorTV);
        effortLevelTV = findViewById(R.id.effortSelectorTV);
        typeTV = findViewById(R.id.textViewTrainingTypePicker);
        timeTV = findViewById(R.id.textViewTimePicker);
        saveWorkoutEditButtom = findViewById(R.id.saveEditWorkoutBtn);
        mFireBaseAuth = mFireBaseAuth.getInstance();
        userID = mFireBaseAuth.getCurrentUser().getUid();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        Intent myIntent = getIntent(); // gets the previously created intent
        workoutDocRef = myIntent.getStringExtra("workoutDocRef");
/*
        workoutEventsDocRef = myIntent.getStringExtra("workoutEventsDocRef");
*/





        //retrieve the data for the event that was previously selected
        mFirebaseFirestore.collection("Users").document(userID)
                .collection("Events").document(workoutDocRef)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    //populate all the textviews with the already existing data
                    DocumentSnapshot document = task.getResult();
                    titleTV.setText(document.get("Training Title").toString());
                    dateTV.setText(document.get("Training Date").toString());

                    //date stored as timestamp - needs formatting
                    dateTV.setText(getFormatedDate(document));

                    timeTV.setText(document.get("Training Time").toString());
                    typeTV.setText(document.get("Training Type").toString());
                    durationTV.setText(document.get("Training Duration").toString());
                    effortLevelTV.setText(document.get("Effort Level").toString());
                    userName = document.get("User Name").toString();
                    userSquad = document.get("User Squad").toString();
                 }
            }
        });

        //when a particular field is clicked
        //display the specific dialog necessary to make a selection
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        typeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(view);
            }
        });
        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });
        durationTV.setOnClickListener(new View.OnClickListener() {
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

        //when the save button is clicked
        saveWorkoutEditButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //get values from all the text fields
                //check if empty
                if (titleTV.getText().toString().isEmpty() || dateTV.getText().toString().isEmpty()
                        || typeTV.getText().toString().isEmpty() || timeTV.getText().toString().isEmpty()
                        || effortLevelTV.toString().isEmpty() | durationTV.toString().isEmpty()) {
                    Toast.makeText(EditWorkoutActivity.this, "all fields must be entered", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                        //retrieve the data from all the fields, regardless if they were updated or not
                        String trainingTitle = titleTV.getText().toString();
                        String trainingType = typeTV.getText().toString();
                        String trainingTime = timeTV.getText().toString();
                        String trainingDuration = durationTV.getText().toString();
                        int effortLevelSelectedValue = Integer.parseInt(effortLevelTV.getText().toString());

                        //create a map with the "new" data
                        userTrainingMap = new HashMap<>();
                        userTrainingMap.put("Training Title",trainingTitle);
                        userTrainingMap.put("Training Date",activityTimeStamp);
                        userTrainingMap.put("Training Type",trainingType);
                        userTrainingMap.put("Training Time",trainingTime);
                        userTrainingMap.put("Training Duration",trainingDuration);
                        userTrainingMap.put("Effort Level",effortLevelSelectedValue);
                        userTrainingMap.put("User Name",userName);
                        userTrainingMap.put("User Squad",userSquad);

                    //pass the map through the query to update the existing workout
                        mFirebaseFirestore.collection("Users").document(userID)
                            .collection("Events").document(workoutDocRef)
                            .update(userTrainingMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(EditWorkoutActivity.this, "Edit successful User", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(EditWorkoutActivity.this, "edit error User", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                        //callback used to retrieve document id from the EVENTS table
                        //once id retrieved update the specific document as per latest changes
                        getEventsDocID(new RetrieveAllEventIDCallback() {
                            @Override
                            public void onAllEventIdCallback(String workoutEventsDocID) {
                                mFirebaseFirestore.collection("Events").document(workoutEventsDocID)
                                        .update(userTrainingMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(EditWorkoutActivity.this, "Edit successful Events", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(EditWorkoutActivity.this, "edit error Events", Toast.LENGTH_SHORT).show();
                                        }
                                        //once update attempted, finish activity
                                        finish();
                                    }

                                });
                            }
                        });



                    }
            }
        });

    }

    private void getEventsDocID(final RetrieveAllEventIDCallback retrieveAllEventIDCallback) {
        //gets the selected workout and the callback variable
        //compares the selected workout's date with all the dates from EVENTS and retrieve matchs
        mFirebaseFirestore.collection("Events")
                .whereEqualTo("Training Date",origDate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    //go through all the retrieved docs and store ID
                    //should only be one as date is accurate to the second
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        docID = document.getId();
                    }

                }
                //when query is completed and id retrieved call the callback method
                retrieveAllEventIDCallback.onAllEventIdCallback(docID);
            }
        });
    }

    private interface RetrieveAllEventIDCallback
    {
        //implementation for the Callback interface - used to check if a method completed
        void onAllEventIdCallback(String eventId);
    }




    private String getFormatedDate(DocumentSnapshot document) {
        Date date = (Date) document.get("Training Date");
        origDate = date;
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        activityTimeStamp = new Timestamp(date);


        return df.format(date);
    }

    @Override
    public void onTimeSelected(String time) {
        timeTV.setText(time);

    }

    @Override
    public void onEffortSelected(int value) {
        effortLevelTV.setText(Integer.toString(value));
    }

    @Override
    public void onDurationSelected(int hour, int minutes, int seconds) {
        String time = Integer.toString(hour) + "h  " + Integer.toString(minutes) + "m";
        durationTV.setText(time);
    }

    @Override
    public void onTrainingTypeSelected(String training) {
        typeTV.setText(training);

    }

    @Override
    public void onDateSelected(Date date) {
        Calendar cal = Calendar.getInstance();
        activityTimeStamp = new Timestamp(date);
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String date_str = df.format(date);
        dateTV.setText(date_str);
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

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new DialogTimePicker();
        ((DialogTimePicker) newFragment).setTimePickerDialogInput(EditWorkoutActivity.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showEditDialog(View v) {
        DialogFragment newFragment = new TrainingTypes();
        ((TrainingTypes) newFragment).setTrainingTypePickerDialogInput(EditWorkoutActivity.this);
        newFragment.show(getSupportFragmentManager(), "TrainingTypes");
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DialogDatePicker();
        ((DialogDatePicker) newFragment).setDatePickerDialogInput(EditWorkoutActivity.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
