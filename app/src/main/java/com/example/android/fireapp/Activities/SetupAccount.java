package com.example.android.fireapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.fireapp.DialogFragments.DialogDatePicker;
import com.example.android.fireapp.DialogFragments.SquadNamesDialog;
import com.example.android.fireapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SetupAccount extends AppCompatActivity implements DialogDatePicker.DatePickerDialogInput ,
         SquadNamesDialog.SquadNamesPickerDialogInput {

    private EditText mDateofBirth;
    private Button mSaveAccountButton;
    private EditText mFirstName;
    private EditText mLastName;

    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private EditText mHeight;
    private TextView dobPicker;
    private int completedChallenges;
    private TextView squadName;
    private EditText mWeight;
    private Spinner heightsSpinner;
    private Spinner weightsSpinner;
    private CheckBox coachCheckBox;
    private Boolean isCoach;
    private Date creationDate = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        final Spinner heightsSpinner =  findViewById(R.id.HeightSpinner);
        final Spinner weightsSpinner =  findViewById(R.id.WeightSpinner);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterH = ArrayAdapter.createFromResource(this,
                R.array.Heights, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterW = ArrayAdapter.createFromResource(this,
                R.array.Weights, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        heightsSpinner.setAdapter(adapterH);
        weightsSpinner.setAdapter(adapterW);

        coachCheckBox = findViewById(R.id.isCoachCheckBox);
        mFirstName = findViewById(R.id.editText_firstName);
        mLastName = findViewById(R.id.editText_secondName);
        mSaveAccountButton = findViewById(R.id.buttonAccountSettup);
        mFireBaseAuth = mFireBaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        final String userID = mFireBaseAuth.getCurrentUser().getUid();
        mHeight = findViewById(R.id.editText_Height);
        mWeight = findViewById(R.id.editText_Weight);
        dobPicker = findViewById(R.id.textViewDOBPicker);
        dobPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        squadName = findViewById(R.id.squadName);
        squadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSquadNamesDialog(view);
            }
        });
        mFirebaseFirestore.collection("Users").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().exists())
                            {
                                //empty data could end in null pointer exception
                                mFirstName.setText(task.getResult().getString("FirstName"));
                                mLastName.setText(task.getResult().getString("LastName"));
                                mWeight.setText(task.getResult().getLong("Weight").toString());
                                mHeight.setText(task.getResult().getLong("Height").toString());
                                dobPicker.setText(task.getResult().getString("DateOfBirth"));
                                squadName.setText(task.getResult().getString("SquadName"));
                                completedChallenges = task.getResult().getLong("Completed Challenges").intValue();
                                creationDate = task.getResult().getDate("Account creation date");
                                isCoach = Boolean.parseBoolean(task.getResult().getString("Coach"));
                                coachCheckBox.setChecked(isCoach);
                            }

                        }
                        else
                        {
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText(SetupAccount.this, "error: " + errorMsg, Toast.LENGTH_LONG).show();

                        }
                    }
                });

        mSaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String first_name = mFirstName.getText().toString();
                String last_name = mLastName.getText().toString();
                String dateOfBirth = dobPicker.getText().toString();
//                String user_height = mHeight.getText().toString();
//                String user_weight = mWeight.getText().toString();
                int user_height = Integer.parseInt(mHeight.getText().toString());
                int user_weight = Integer.parseInt(mWeight.getText().toString());

                String weightSpinner = weightsSpinner.getSelectedItem().toString();
                String heightSpinner = heightsSpinner.getSelectedItem().toString();
                Boolean isCoach = coachCheckBox.isChecked();
//                user_height += heightSpinner;
//                user_weight += weightSpinner;

                //get current date - for account creation
                //if no date field exists means new account get current date
                //else keep the current existing date
                Calendar calendar = Calendar.getInstance();
                Date date;
                if(creationDate == null)
                {
                    //if newly created account - make an initial entry to weightData
                    date = calendar.getTime();
                    Map<String, Object> dataWPointsMap = new HashMap<>();
                    dataWPointsMap.put("xValue",date);
                    dataWPointsMap.put("yValue",user_weight);
                    saveInitialWeightDataPoint(dataWPointsMap, userID);

                    Map<String, Object> dataHPointsMap = new HashMap<>();
                    dataHPointsMap.put("xValue",date);
                    dataHPointsMap.put("yValue",user_height);
                    saveInitialHeightDataPoint(dataHPointsMap,userID);
                }
                else
                {
                    date = creationDate;
                }

                String squad_name = squadName.getText().toString();
                if(!first_name.isEmpty() && !last_name.isEmpty())
                {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("FirstName",first_name);
                    userMap.put("LastName",last_name);
                    userMap.put("Weight",user_weight);
                    userMap.put("Height",user_height);
                    userMap.put("Weight unit",weightSpinner);
                    userMap.put("Height unit",heightSpinner);
                    userMap.put("DateOfBirth",dateOfBirth);
                    userMap.put("SquadName",squad_name);
                    userMap.put("Coach",isCoach.toString());
                    userMap.put("Completed Challenges",completedChallenges);
                    userMap.put("Account creation date",date);

                    mFirebaseFirestore.collection("Users").document(userID).set(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(SetupAccount.this, "Account Updated Successful", Toast.LENGTH_LONG).show();
                                        SendToStart();
                                    }
                                    else
                                    {
                                        String errorMsg = task.getException().getMessage();
                                        Toast.makeText(SetupAccount.this, "error: " + errorMsg, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                }
                SendToStart();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbarSetup);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Setup");
    }

    private void saveInitialWeightDataPoint(Map<String, Object> dataPointsMap, String userID) {
        mFirebaseFirestore.collection("Users").document(userID).collection("Weight Data").add(dataPointsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SetupAccount.this, "datapoint created successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SetupAccount.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private void saveInitialHeightDataPoint(Map<String, Object> dataPointsMap, String userID) {
        mFirebaseFirestore.collection("Users").document(userID).collection("Height Data").add(dataPointsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SetupAccount.this, "datapoint created successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SetupAccount.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void SendToStart() {
        Intent mainIntent = new Intent(SetupAccount.this,MainActivity.class);
        //mainIntent.putExtra("Coach",isCoach);
        startActivity(mainIntent);
        finish();
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DialogDatePicker();
        ((DialogDatePicker) newFragment).setDatePickerDialogInput(SetupAccount.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(Date date) {
        //TODO UPDATE DATE - commented out
        //dobPicker.setText(date);
    }





    @Override
    public void onSquadNamesSelected(String squadName) {
        this.squadName.setText(squadName);

    }
    private void showSquadNamesDialog(View v) {
        DialogFragment newFragment = new SquadNamesDialog();
        ((SquadNamesDialog) newFragment).setSquadNamesPickerDialogInput(SetupAccount.this);
        newFragment.show(getSupportFragmentManager(), "SquadNames");
    }
}
