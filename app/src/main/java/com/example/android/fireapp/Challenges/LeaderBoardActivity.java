package com.example.android.fireapp.Challenges;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ListView;

import com.example.android.fireapp.Adapters.CompletedChallengesAdapter;
import com.example.android.fireapp.Adapters.LeaderBoardListAdapter;
import com.example.android.fireapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity implements LeaderBoardListAdapter.CompletedChallenteListener{
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private ArrayList<LeaderBoardEntry> leaderBoardEntryList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        mFireBaseAuth = mFireBaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.leaderboardListView);
        final String userID = mFireBaseAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("LeaderBoard");

        mFirebaseFirestore.collection("Users").orderBy("Completed Challenges",Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    int id = 1;
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        String lastName = document.get("LastName").toString();
                        String firstName = document.get("FirstName").toString();
                        String userName = lastName + " " + firstName;
                        int completedChallenges = document.getLong("Completed Challenges").intValue();
                        LeaderBoardEntry leaderBoardEntry = new LeaderBoardEntry(completedChallenges,userName,id);
                        leaderBoardEntryList.add(leaderBoardEntry);
                        id ++;
                    }

                    listView.setAdapter(new LeaderBoardListAdapter(LeaderBoardActivity.this, leaderBoardEntryList,LeaderBoardActivity.this));

                }
            }
        });



    }

    @Override
    public void completedClick(int position) {

    }
}
