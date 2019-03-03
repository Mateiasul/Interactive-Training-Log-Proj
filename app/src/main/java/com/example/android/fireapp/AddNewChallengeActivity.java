package com.example.android.fireapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNewChallengeActivity extends AppCompatActivity {

    private TextView challangeDescTV;
    private TextView challengeSumTV;
    private  String userID;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFireBaseAuth;
    private FloatingActionButton FAB_addChallenge;
    private String currentUserName;
    private Button addChallengeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFireBaseAuth = mFireBaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        userID = mFireBaseAuth.getCurrentUser().getUid();
        setContentView(R.layout.activity_add_new_challenge);
        challangeDescTV = findViewById(R.id.ChallangeDescriptionBox);
        challengeSumTV = findViewById(R.id.ChallengeSummaryTV);
        Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Challenge");
        addChallengeButton = findViewById(R.id.addChallengeSaveBtn);

        mFirebaseFirestore.collection("Users").document(userID).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    if(task.getResult().exists())
                                    {
                                        currentUserName = task.getResult().get("LastName").toString();
                                    }
                                }
                            }
                        });

        addChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String challengeDesc = challangeDescTV.getText().toString();
                String challengeSum = challengeSumTV.getText().toString();
                if(!challengeDesc.isEmpty() && !challengeSum.isEmpty())
                {
                    Map<String, Object> newChallengeMap = new HashMap<>();
                    newChallengeMap.put("Challenge Date",date);
                    newChallengeMap.put("Challenge Description",challengeDesc);
                    newChallengeMap.put("Challenge Summary",challengeSum);
                    newChallengeMap.put("Creator Name",currentUserName);
                    newChallengeMap.put("Creator ID",userID);
                    newChallengeMap.put("Participants",0);

                    mFirebaseFirestore.collection("Challenges")
                            .add(newChallengeMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(AddNewChallengeActivity.this, "Challenge Logged Successful", Toast.LENGTH_SHORT).show();
                                Intent addChallengeIntent = new Intent(AddNewChallengeActivity.this,BottomNav.class);
                                addChallengeIntent.putExtra("Fragment","Challenges");
                                startActivity(addChallengeIntent);
                                finish();

                            }
                            else
                            {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(AddNewChallengeActivity.this, "Challenge log unsuccessful  " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(AddNewChallengeActivity.this, "All fields must be entered", Toast.LENGTH_SHORT).show();
                }

            }
        });





    }


}
