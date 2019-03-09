package com.example.android.fireapp.Challenges;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.fireapp.Activities.MainActivity;
import com.example.android.fireapp.DialogFragments.ScoreDialogPicker;
import com.example.android.fireapp.DialogFragments.WorkoutDurationDialog;
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

public class ChallengeDetailsActivity extends AppCompatActivity implements ScoreDialogPicker.ScoreDialogPickerListener,
               WorkoutDurationDialog.DurationDialogListener{

    private TextView creator_TV;
    private TextView description_TV;
    private TextView challengeSummary_TV;
    private TextView challengeDate;
    private TextView participants_TV;
    private TextView userChallengeTime;
    private TextView userChallengeScore;

    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String userID;
    private Button enrollChallengeBtn;
    private String challengeDocRef;
    private String userLastName;
    private String userSquad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_details);

        creator_TV = findViewById(R.id.detailedChallengeCreator);
        description_TV = findViewById(R.id.detailedChallengeDesc);
        challengeDate = findViewById(R.id.detailedChallengeDate);
        participants_TV = findViewById(R.id.detailedChallengeParticipants);
        userChallengeScore = findViewById(R.id.userChallengeScore);
        userChallengeTime = findViewById(R.id.userChallengeTime);
        enrollChallengeBtn = findViewById(R.id.enrollChallengeBtn);
        challengeSummary_TV = findViewById(R.id.detailedChallengeSum);
        mFireBaseAuth = mFireBaseAuth.getInstance();
        userID = mFireBaseAuth.getCurrentUser().getUid();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        Intent myIntent = getIntent(); // gets the previously created intent
        challengeDocRef = myIntent.getStringExtra("challengeDocRef");

        mFirebaseFirestore.collection("Challenges").document(challengeDocRef).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        if (task.getResult().exists())
                                        {
                                            creator_TV.setText(task.getResult().get("Creator Name").toString());
                                            description_TV.setText(task.getResult().get("Challenge Description").toString());
                                            challengeDate.setText(task.getResult().get("Challenge Date").toString());
                                            participants_TV.setText(task.getResult().get("Participants").toString());
                                            challengeSummary_TV.setText(task.getResult().get("Challenge Summary").toString());
                                        }
                                    }
                                }
                            });

        mFirebaseFirestore.collection("Users").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if (task.getResult().exists())
                            {
                                userLastName = task.getResult().get("LastName").toString();
                                userSquad = task.getResult().get("SquadName").toString();
                            }
                        }
                    }
                });

        userChallengeScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScoreDialog();
            }
        });

        userChallengeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDurationSelectDialog();
            }
        });
        enrollChallengeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String challengeTime = userChallengeTime.getText().toString();
                String challengeScore = userChallengeScore.getText().toString();
                if(!challengeTime.isEmpty() && !challengeScore.isEmpty())
                {
                    int userScore = Integer.parseInt(userChallengeScore.getText().toString());

                    Map<String, Object> newChallengeMap = new HashMap<>();
                    newChallengeMap.put("Challenge ID",challengeDocRef);
                    newChallengeMap.put("User ID",userID);
                    newChallengeMap.put("User Score",userScore);
                    newChallengeMap.put("User Time",userChallengeTime.getText().toString());
                    newChallengeMap.put("User LastName", userLastName);
                    newChallengeMap.put("User Squad", userSquad);
                    newChallengeMap.put("Challenge Summary", challengeSummary_TV.getText().toString());
                    newChallengeMap.put("Creator Name", creator_TV.getText().toString());

                    Calendar calendar = Calendar.getInstance();
                    final Date date = calendar.getTime();
                    newChallengeMap.put("Entry Time",date);

                    mFirebaseFirestore.collection("Challenges Entries")
                            .add(newChallengeMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ChallengeDetailsActivity.this, "Challenge Logged Successful", Toast.LENGTH_SHORT).show();
                                Intent addChallengeIntent = new Intent(ChallengeDetailsActivity.this,MainActivity.class);
                                addChallengeIntent.putExtra("Fragment","Challenges");
                                startActivity(addChallengeIntent);
                                finish();

                            }
                            else
                            {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(ChallengeDetailsActivity.this, "Challenge log unsuccessful  " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(ChallengeDetailsActivity.this, "All fields must be entered", Toast.LENGTH_SHORT).show();
                }



            }
        });

    }

    public void openScoreDialog()
    {
        //open effort dialog
        ScoreDialogPicker scoreDialog = new ScoreDialogPicker();
        scoreDialog.show(getSupportFragmentManager(),"effort level dialog");
    }

     public void openDurationSelectDialog()
     {
         //open duration dialog
         WorkoutDurationDialog workoutDurationDialogFragment = new WorkoutDurationDialog();
         workoutDurationDialogFragment.show(getSupportFragmentManager(),"duration dialog fragment");
     }

    @Override
    public void onScoreSelected(String value) {
        userChallengeScore.setText(value);
    }

    @Override
    public void onDurationSelected(int hour, int minutes, int seconds) {
        String time = Integer.toString(hour) + "h  " + Integer.toString(minutes) + "m";
        userChallengeTime.setText(time);
    }
}
