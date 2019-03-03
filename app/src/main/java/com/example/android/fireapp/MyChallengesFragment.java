package com.example.android.fireapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.fireapp.DialogFragments.CompleteWorkoutDialog;
import com.example.android.fireapp.DialogFragments.WorkoutDurationDialog;
import com.example.android.fireapp.adapters.MyChallengesListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MyChallengesFragment extends Fragment implements CompleteWorkoutDialog.CompleteWorkoutListener{
    private View view;
    private Boolean isCoach;
    private String userID;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFireBaseAuth;
    private ArrayList<Challenge> myChallengeList;
    private RecyclerView recyclerView;
    private ListView myChallengesListView;
    private int currentChallenge;
    private String toBeDeletedRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_challenges, container, false);
        mFireBaseAuth = mFireBaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        userID = mFireBaseAuth.getCurrentUser().getUid();
        myChallengeList = new ArrayList<>();
        myChallengesListView = view.findViewById(R.id.myChallengesListView);
        mFirebaseFirestore.collection("Challenges Entries").whereEqualTo("User ID",userID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
   /*                         int score =  ((Long) document.get("User Score")).intValue();
                            String time = document.get("User Time").toString();*/
                            String creatorName = document.get("Creator Name").toString();
                            String challengeSummary = document.get("Challenge Summary").toString();
                            Date entryDate = (Date) document.get("Entry Time");
                            String challengeDocRef = document.getId();
                            Challenge challenge = new Challenge(challengeSummary,creatorName,entryDate,challengeDocRef);
                            myChallengeList.add(challenge);
                        }
                        myChallengesListView.setAdapter(new MyChallengesListAdapter(getActivity(), myChallengeList,completeCommunication));
                        //listView.setAdapter(new ChallengesAdapter(getActivity(),challengeList,communication));

                    }
                }
            }
        });




        // Inflate the layout for this fragment
        return view;
    }



    MyChallengesListAdapter.CompleteCommunication completeCommunication = new MyChallengesListAdapter.CompleteCommunication() {
        @Override
        public void completeCommunication(int position) {
            currentChallenge = position;
                //open duration dialog
                CompleteWorkoutDialog completeWorkoutDialog = new CompleteWorkoutDialog();
                completeWorkoutDialog.setTargetFragment(MyChallengesFragment.this,1);
            completeWorkoutDialog.show(getFragmentManager(), "Completedialog");
        }
    };


    @Override
    public void onCompletedWorkoutSelected(String score, String time) {
        Map<String, Object> newChallengeMap = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();
        String coachName = myChallengeList.get(currentChallenge).getCreatorUser();
        final String docRef = myChallengeList.get(currentChallenge).getChallengeDocRef();
        String challengeSummary =myChallengeList.get(currentChallenge).getSummary();

        newChallengeMap.put("Challenge Completed Date",date);
        newChallengeMap.put("Challenge Summary",challengeSummary);
        newChallengeMap.put("Creator Name",coachName);
        newChallengeMap.put("Sailor Score",time);
        newChallengeMap.put("Sailor Time",score);
        newChallengeMap.put("Challenge Reference",docRef);
        newChallengeMap.put("Sailor ID",userID);

        mFirebaseFirestore.collection("Completed Challenges")
                .add(newChallengeMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful())
                {

 /*                   mFirebaseFirestore.collection("Challenge Entries")
                            .whereEqualTo("User ID",userID)
                            .whereEqualTo("Challenge ID",docRef).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                if(!task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        toBeDeletedRef = document.getReference().toString();
                                    }
                                }
                            }
                        }
                    });*/
                                    mFirebaseFirestore.collection("Challenges Entries").document(docRef).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(getActivity(), "delete successful", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                    //refresh fragment
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(MyChallengesFragment.this).attach(MyChallengesFragment.this).commit();


                    Toast.makeText(getActivity(), "Challenge Logged Successful", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(getActivity(), "Challenge log unsuccessful  " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
