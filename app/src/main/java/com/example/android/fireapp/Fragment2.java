package com.example.android.fireapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Fragment2 extends Fragment {
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private View v;
    private BarChart barChart;
    private int usersPerSquad;
    private ArrayList<String> userNames;
    private ArrayList<DocumentReference> userDocsReference;
    private ArrayList<String> eventTypes;
    private  ArrayList<BarEntry> barEntries;
    private int athleteNumber;
    private String currentUserSquad;
    private  Map<String, UserGraphEntries> userTrainingMap;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userTrainingMap  = new HashMap<>();
        userNames = new ArrayList<>();
        barEntries = new ArrayList<>();
        userDocsReference = new ArrayList<>();
        usersPerSquad = 0;
        athleteNumber = 0;
        mFireBaseAuth = mFireBaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        v = inflater.inflate(R.layout.fragment_challanges, container, false);
        barChart = v.findViewById(R.id.chart1);
        barChart.setMaxVisibleValueCount(40);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);
        userNames = new ArrayList<>();
        eventTypes = new ArrayList<>();
        final String userID = mFireBaseAuth.getCurrentUser().getUid();

        //retreive current user's squad based on userID
        mFirebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    //store squad
                    currentUserSquad = task.getResult().getString("SquadName");
                }
                else
                {
                    //display error
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(getActivity(), "couldn't retrieve squad name  " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //need to be rechecked
        //call get events, when get events finished
        //Callback made so prepare data can be used
        getEvents(new FirestoreCallback() {
            @Override
            public void onCallback(Map<String, UserGraphEntries> userTrainingMap) {
                prepareData();
            }
        });

            return v;
    }

    private interface FirestoreCallback
    {
        void onCallback(Map<String, UserGraphEntries> userTrainingMap);
    }


    private void getEvents(final FirestoreCallback callback)
    {

        //TODO replace hardcoded squad value
        mFirebaseFirestore.collection("Events")
                .whereEqualTo("User Squad", "420 Squad")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //if completed and successful
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userName = document.get("User Name").toString();
                                if(userTrainingMap.containsKey(userName))
                                {
                                    UserGraphEntries userGraphEntrie = userTrainingMap.get(userName);
                                    checkAndUpdateWorkoutType(document, userGraphEntrie);
                                    userTrainingMap.put(userName,userGraphEntrie);

                                }
                                else{
                                    userNames.add(userName);
                                    UserGraphEntries userGraphEntrie = new UserGraphEntries(userName,0,0,0,0,0,0);
                                    checkAndUpdateWorkoutType(document, userGraphEntrie);
                                    userTrainingMap.put(userName,userGraphEntrie);
                                }
                            }
                        }
                        else {
                            Log.d("USER", "Error getting documents: ", task.getException());
                        }
                        callback.onCallback(userTrainingMap);
                    }
                });
    }

    private void checkAndUpdateWorkoutType(QueryDocumentSnapshot document, UserGraphEntries userGraphEntrie) {
        if(document.get("Training Type").equals("Gym"))
        {
            userGraphEntrie.setGymWorkoutsAmount(userGraphEntrie.getGymWorkoutsAmount() + 1);
        }
        else if(document.get("Training Type").equals("Sailing"))
        {
            userGraphEntrie.setSailingWorkoutsAmount(userGraphEntrie.getSailingWorkoutsAmount() + 1);
        }
        else if(document.get("Training Type").equals("Running"))
        {
            userGraphEntrie.setRunningWorkoutsAmount(userGraphEntrie.getRunningWorkoutsAmount() + 1);
        }
    }


    private void retrieveUsers(final FirestoreCallback callback) {
        mFirebaseFirestore.collection("Users")
                .whereEqualTo("SquadName", "420 Squad")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //if completed and successful
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //for each document/user in the collection
                                //count the users
                                usersPerSquad++;
                                //store their last names - to be used as graph labels
                                String userName = document.get("LastName").toString();
                                userNames.add(userName);
                                //for each retrieved user, get the reference for their events collection
                                DocumentReference documentReference = document.getReference();
                                userDocsReference.add(documentReference);
                            }
                        }
                        else {
                            Log.d("USER", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void prepareData()
    {

        int entryNumber = 0;

        //for each entry in the map created, create a bar entry that contains the training details
        for(Map.Entry<String, UserGraphEntries> entry : userTrainingMap.entrySet()) {
            UserGraphEntries userGraphEntrie = entry.getValue();
            int gymAmount = userGraphEntrie.getGymWorkoutsAmount();
            int sailingAmount = userGraphEntrie.getSailingWorkoutsAmount();
            int runningAmount = userGraphEntrie.getRunningWorkoutsAmount();
            barEntries.add(new BarEntry(entryNumber,new float[]{gymAmount,sailingAmount,runningAmount}));
            entryNumber++;

            // do what you have to do here
            // In your case, another loop.
        }


        //TODO some sort of safety necessary for multi-threading , might need revising
            setData(barEntries);

    }

    //TODO recheck graph tutorial to learn more about the weird functions
    public void setData(ArrayList<BarEntry> barChartEntries) {
        barChart = v.findViewById(R.id.chart1);
        barChart.setMaxVisibleValueCount(40);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);

        //set graph description
        BarDataSet barDataSet = new BarDataSet(barChartEntries, "Training per squad");

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        //TODO find better colors
        //set colors and their labels
        //TODO crashes if tapped repeatedly
        //barDataSet.setColors(new int[]{ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.back1), ContextCompat.getColor(getActivity(), R.color.com_facebook_blue)});
        barDataSet.setColors(new int[] { R.color.Red, R.color.Green, R.color.Yellow},getActivity());
        barDataSet.setStackLabels(new String[] {"Gym","Sail","Run"});
        barChart.setData(barData);

        //get the set of keys from the hashmap and convert them to an array
        Set<String> keys = userTrainingMap.keySet();
        String[] names = keys.toArray(new String[keys.size()]);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1);

        //used the given array of keys to set labels
        xAxis.setValueFormatter(new XAxisValueFormatter(names));

        //TODO maybe add hours value on the side? / minutes?
        //redraw graph every time?
        barChart.invalidate();
    }
}
