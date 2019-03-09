package com.example.android.fireapp.Challenges;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.fireapp.Activities.LoginActivity;
import com.example.android.fireapp.Activities.MainActivity;
import com.example.android.fireapp.R;
import com.example.android.fireapp.Adapters.CompletedChallengesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class CompletedChallengesActivity extends AppCompatActivity implements CompletedChallengesAdapter.CompletedChallenteListener {
    private Boolean isCoach;
    private String userID;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFireBaseAuth;
    private ListView myCompletedChallengesListView;
    private int participants;
    private ArrayList<Challenge> myCompletedChallengeList;

    private interface ParticipantsCallBack
    {
        void onparticipantsCallBack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_challenges);

        mFireBaseAuth = mFireBaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        userID = mFireBaseAuth.getCurrentUser().getUid();

        myCompletedChallengeList = new ArrayList<>();
        myCompletedChallengesListView = findViewById(R.id.myCompletedChallengesListView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Completed Challenges");

        //retrieve all the completed challenges by the currently loged in user
        mFirebaseFirestore.collection("Completed Challenges").whereEqualTo("Sailor ID",userID).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        if(!task.getResult().isEmpty())
                                        {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String creatorName = document.get("Creator Name").toString();
                                                String challengeSummary = document.get("Challenge Summary").toString();
                                                Date entryDate = (Date) document.get("Challenge Completed Date");
                                                String challengeDocRef = document.get("Challenge Reference").toString();
                                                Challenge challenge = new Challenge(challengeSummary,creatorName,entryDate,challengeDocRef,0,document.getId());
                                                myCompletedChallengeList.add(challenge);
                                            }
                                        }
                                    }
                                    myCompletedChallengesListView.setAdapter(new CompletedChallengesAdapter(CompletedChallengesActivity.this, myCompletedChallengeList,CompletedChallengesActivity.this));

                                }
                            });

    }

    @Override
    public void completedClick(int position) {
        Toast.makeText(this, myCompletedChallengeList.get(position).getSummary(), Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(CompletedChallengesActivity.this, LeaderBoardActivity.class);
            startActivity(loginIntent);

    }

    public void retrieveParticipants(String challengeDocId, final ParticipantsCallBack callBack )
    {
        mFirebaseFirestore.collection("Challenges").document(challengeDocId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    participants = (int) task.getResult().get("Participants");
                }
                callBack.onparticipantsCallBack();
            }
        });
    }
}
