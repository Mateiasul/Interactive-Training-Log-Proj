package com.example.android.fireapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.fireapp.DialogFragments.DialogDatePicker;
import com.example.android.fireapp.DialogFragments.SquadNamesDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private TextView squadName;
    private EditText mWeight;
    private Spinner heightsSpinner;
    private Spinner weightsSpinner;
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
                             /*   String firstName = task.getResult().getString("FirstName");
                                String lastName = task.getResult().getString("LastName");
                                String weight = task.getResult().getString("Weight");
                                String height = task.getResult().getString("Height");
                                String dateOfBirth = task.getResult().getString("DateOfBirth");*/

                                mFirstName.setText(task.getResult().getString("FirstName"));
                                mLastName.setText(task.getResult().getString("LastName"));
                                mWeight.setText(task.getResult().getString("Weight"));
                                mHeight.setText(task.getResult().getString("Height"));
                                dobPicker.setText(task.getResult().getString("DateOfBirth"));
                                squadName.setText(task.getResult().getString("SquadName"));
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
                String user_height = mHeight.getText().toString();
                String user_weight = mWeight.getText().toString();
                String weightSpinner = weightsSpinner.getSelectedItem().toString();
                String heightSpinner = heightsSpinner.getSelectedItem().toString();
                user_height += heightSpinner;
                user_weight += weightSpinner;

                String squad_name = squadName.getText().toString();
                if(!first_name.isEmpty() && !last_name.isEmpty())
                {
                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("FirstName",first_name);
                    userMap.put("LastName",last_name);
                    userMap.put("Weight",user_weight);
                    userMap.put("Height",user_height);
                    userMap.put("DateOfBirth",dateOfBirth);
                    userMap.put("SquadName",squad_name);
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
    private void SendToStart() {
        Intent mainIntent = new Intent(SetupAccount.this,BottomNav.class);
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
