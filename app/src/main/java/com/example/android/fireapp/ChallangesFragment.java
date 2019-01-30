package com.example.android.fireapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

//TODO display charts on a per squad basis user should only be able to see his squad performance and no other
//TODO this should be moved to a different unique activity/fragment and not stay here.
/**
 * A simple {@link Fragment} subclass.
 */
public class ChallangesFragment extends Fragment {


    public ChallangesFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private View v;
    private BarChart barChart;
    private int usersPerSquad;
    private ArrayList<String> userNames;
    private ArrayList<String> eventTypes;
    private  ArrayList<BarEntry> barEntries;
    private int athleteNumber;
    private String currentUserSquad;
  /*  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userNames = new ArrayList<>();
        barEntries = new ArrayList<>();
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

        mFirebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                 currentUserSquad = task.getResult().getString("SquadName");
                }
                else
                {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(getActivity(), "couldn't retrieve squad name  " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //TODO might need to add some sort of handler - if squad is not yet retrieve can t start the next search


        //TODO uppercase User Squad, delete
        //retrieve the collection of all users - with a specific squad
        *//*TODO squad should not be hardcoded string - get logged user and based on his current squad
          display the appropriate graph
        *//*

        mFirebaseFirestore.collection("Users")
                .whereEqualTo("SquadName", currentUserSquad)
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
                                eventTypes = new ArrayList<>();
                                //store their last names - to be used as graph labels
                                String userName = document.get("LastName").toString();
                                userNames.add(userName);
                                //for each retrieved user, get the reference for their events collection
                                DocumentReference reference = document.getReference();
                                CollectionReference collectionReference = reference.collection("Events");
                                //Log.d("USER", "Error getting documents: " + collectionReference.getPath(), task.getException());
                                Task<QuerySnapshot> taskEvents = collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                       if(task.isSuccessful())
                                       {
                                           //if events retrieved completely and successful
                                           //empty previous event types if any
                                           eventTypes.clear();
                                        for(QueryDocumentSnapshot eventDoc : task.getResult())
                                        {
                                            //for each document.event
                                            String trainingType = eventDoc.get("Training Type").toString();
                                            eventTypes.add(trainingType);
                                            //retrieve the type and store it
                                        }
                                        prepareData();
                                        }
                                    }
                                });
                            }
                        }
                        else {
                            Log.d("USER", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return v;
    }

    public void prepareData()
    {

        //values representing amount of activities done
        float gymActivitiesAmount = 0;
        float sailingActivitiesAmount = 0;
        float RunningActivitiesAmount = 0;
        //TODO more accurate - represent hours rather than just the amount
        for (int i = 0; i<eventTypes.size(); i++)
        {
            if(eventTypes.get(i).equals("Gym"))
                gymActivitiesAmount++;
            else if(eventTypes.get(i).equals("Sailing"))
                sailingActivitiesAmount++;
            else if(eventTypes.get(i).equals("Running"))
                RunningActivitiesAmount++;

            //add an entry with x,y parameters
        }
        barEntries.add(new BarEntry(athleteNumber,new float[]{gymActivitiesAmount,sailingActivitiesAmount,RunningActivitiesAmount}));
        athleteNumber++;
        //TODO some sort of safety necessary for multi-threading , might need revising
        if(athleteNumber == usersPerSquad)
        {//if all users retrieved and their events queried successfully- then go and set the graph
            setData(barEntries);
        }
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
        barDataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.back1), getResources().getColor(R.color.com_facebook_button_background_color)});
        barDataSet.setStackLabels(new String[] {"Gym","Sail","Run"});
        barChart.setData(barData);
        String[] userNamesArray = userNames.toArray(new String[0]);

        //String[] userNamesArray = {"Matei", "Andrei", "rares", "Dan", "Bob", "andy"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter(userNamesArray));
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        //TODO maybe add hours value on the side? / minutes?
        //redraw graph every time?
        barChart.invalidate();
    }*/

}
