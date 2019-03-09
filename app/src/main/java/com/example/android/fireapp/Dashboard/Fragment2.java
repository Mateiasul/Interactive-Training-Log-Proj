package com.example.android.fireapp.Dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.fireapp.R;
import com.example.android.fireapp.UserGraphEntries;
import com.example.android.fireapp.XAxisValueFormatter;
import com.example.android.fireapp.Adapters.RecyclerViewAdapter;
import com.example.android.fireapp.Adapters.UserNamesRecyclerViewAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Fragment2 extends Fragment implements RecyclerViewAdapter.RecyclerViewAdapterInterface,
        UserNamesRecyclerViewAdapter.UserRecyclerViewAdapterInterface {
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private View v;
    private BarChart barChart;
    private BarChart indivBarChart;
    private BarChart allEventsBarChart;

    private ArrayList<String> userNames;
    private ArrayList<DocumentReference> userDocsReference;
    private ArrayList<String> eventTypes;
    private ArrayList<String> squadNames;
    private ArrayList<String> squadRecyclerViewNames = new ArrayList<>();
    private  ArrayList<BarEntry> barEntries;
    private  ArrayList<BarEntry> allEventsBarEntries;
    private  ArrayList<BarEntry> allUserEventsBarEntries;
    private  ArrayList<BarEntry> indivUserEventsBarEntries;

    private String currentUserSquad;
    private  Map<String, UserGraphEntries> userTrainingMap;
    private  Map<String, UserGraphEntries> squadWorkoutMap;
    private  Map<String, UserGraphEntries> allUsersWorkoutMap;
    private  Boolean isCoach;
    private  String userID;
    private ArrayList<ActivityLogs> workouts;
    private Map<Integer, Integer> workoutLogMap;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userTrainingMap = new HashMap<>();
        squadWorkoutMap = new HashMap<>();
        userNames = new ArrayList<>();
        barEntries = new ArrayList<>();
        allEventsBarEntries = new ArrayList<>();
        allUserEventsBarEntries = new ArrayList<>();
        indivUserEventsBarEntries = new ArrayList<>();
        userDocsReference = new ArrayList<>();
        mFireBaseAuth = mFireBaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        v = inflater.inflate(R.layout.fragment2_overall_layout, container, false);
        allEventsBarChart = v.findViewById(R.id.chart2);
        indivBarChart = v.findViewById(R.id.chart3);
        barChart = v.findViewById(R.id.chart1);
        userNames = new ArrayList<>();
        eventTypes = new ArrayList<>();
        userID = mFireBaseAuth.getCurrentUser().getUid();

        mFirebaseFirestore.collection("Users").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                isCoach = Boolean.parseBoolean(task.getResult().get("Coach").toString());
                            }
                        }
                    }
                });

        checkUserIsCoach(new IsCoachCallback() {
            @Override
            public void onIsCoachCallback(Boolean isCoach) {
                if (!isCoach) {
                    //toDO decide later if this needs to be viewable for everyone
                    indivBarChart.setVisibility(View.GONE);
                    //retreive current user's squad based on userID
                    mFirebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                //store squad
                                currentUserSquad = task.getResult().getString("SquadName");

                                //using a callback retrieve all the user sided base events, base on its squad
                                getUserEvents(new FirestoreCallback() {
                                    @Override
                                    public void onCallback(Map<String, UserGraphEntries> userTrainingMap) {
                                        if (!userTrainingMap.isEmpty()) {
                                            //once the getUserEvents is completed, if there is data
                                            //ready the data to be displayed in the bard chart
                                            prepareData();
                                        } else {
                                            //if there's no data - don t display the empty barchart
                                            barChart.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                //display error
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(getActivity(), "couldn't retrieve squad name  " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                    getAllEvents(new getSquadsNameCallback() {
                        @Override
                        public void ongetSquadsNameCallback() {
                        }
                    });
                } else {
                    getAllEvents(new getSquadsNameCallback() {
                        @Override
                        public void ongetSquadsNameCallback() {
                            indivBarChart.setVisibility(View.GONE);
                            barChart.setVisibility(View.GONE);
                            initRyclcerView(v);

                        }
                    });
                }
            }
        });
        /*
         */

        return v;


    }


    private void initRyclcerView(View v)
    {
        LinearLayoutManager layoutManager =  new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView = v.findViewById(R.id.squadsRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<String> keys = new ArrayList<>(squadWorkoutMap.keySet());

        RecyclerViewAdapter recyclerViewAdapter =  new RecyclerViewAdapter(keys,getActivity(),this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initUserRyclcerView(View v)
    {
        LinearLayoutManager layoutManager =  new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView = v.findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<String> keys = new ArrayList<>(userTrainingMap.keySet());

        UserNamesRecyclerViewAdapter userNamesRecyclerViewAdapter =  new UserNamesRecyclerViewAdapter(keys,getActivity(),this);
        recyclerView.setAdapter(userNamesRecyclerViewAdapter);
    }

    private void checkUserIsCoach(final IsCoachCallback isCoachCallback) {
        mFirebaseFirestore.collection("Users").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().exists())
                            {
                                isCoach = Boolean.parseBoolean(task.getResult().get("Coach").toString());
                                isCoachCallback.onIsCoachCallback(isCoach);
                            }
                        }
                    }
                });
    }


    private void getAllEvents(final getSquadsNameCallback callback) {
        mFirebaseFirestore.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //if completed and successful
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userSquad = document.get("User Squad").toString();
                                //for each retrieved event store data based on each squad
                                if(squadWorkoutMap.containsKey(userSquad))
                                {
                                    //if the map already contains an entry for a specific squad
                                    //update the existing data accordingly
                                    UserGraphEntries userGraphEntrie = squadWorkoutMap.get(userSquad);
                                    checkAndUpdateWorkoutType(document, userGraphEntrie);
                                    squadWorkoutMap.put(userSquad,userGraphEntrie);

                                }
                                else{
                                    //if there's no data yet for a squad, create a new entry
                                    //initialise everything to 0, and then update it accordingly
                                    userNames.add(userSquad);
                                    UserGraphEntries userGraphEntrie = new UserGraphEntries(userSquad,0,0,0,0,0,0);
                                    checkAndUpdateWorkoutType(document, userGraphEntrie);
                                    squadWorkoutMap.put(userSquad,userGraphEntrie);
                                }
                            }
                            //once the map is created prepare the map data to be displayed in the bar chart
                            prepareAllEventsData();
                            callback.ongetSquadsNameCallback();

                        }
                        else {
                            Log.d("USER", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onItemClicked(String squadName) {
        Toast.makeText(getActivity(), squadName, Toast.LENGTH_SHORT).show();
        currentUserSquad = squadName;
        userTrainingMap = new HashMap<>();
        barChart.setVisibility(View.VISIBLE);
        getUserEvents(new FirestoreCallback() {
            @Override
            public void onCallback(Map<String, UserGraphEntries> userTrainingMap) {
                if (!userTrainingMap.isEmpty()) {
                    //once the getUserEvents is completed, if there is data
                    //ready the data to be displayed in the bard chart
                    prepareData();
                    initUserRyclcerView(v);
                }
            }
        });
        //generate next graph


    }

    //when item is clicked in the user recycler view, draw indiv sailor grapw
    @Override
    public void onUserItemClicked(String name) {
        indivBarChart.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
        setIndividualData(name);

    }


    //get all the events on a per squad basis
    private void getUserEvents(final FirestoreCallback callback)
    {

        //TODO replace hardcoded squad value - done
        mFirebaseFirestore.collection("Events")
                .whereEqualTo("User Squad", currentUserSquad)
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
        if(document.get("Training Type").equals("Walking"))
        {
            userGraphEntrie.setWalkingWorkoutsAmount(userGraphEntrie.getWalkingWorkoutsAmount() + 1);
        }
        else if(document.get("Training Type").equals("Hiking"))
        {
            userGraphEntrie.setHikingWorkoutsAmount(userGraphEntrie.getHikingWorkoutsAmount() + 1);
        }
        else if(document.get("Training Type").equals("Skiing"))
        {
            userGraphEntrie.setSkiingWorkoutsAmount(userGraphEntrie.getSkiingWorkoutsAmount() + 1);
        }
    }


    public void prepareData()
    {

        int entryNumber = 0;
        barEntries = new ArrayList<>();
        //for each entry in the map created, create a bar entry that contains the training details
        for(Map.Entry<String, UserGraphEntries> entry : userTrainingMap.entrySet()) {
            UserGraphEntries userGraphEntrie = entry.getValue();
            int gymAmount = userGraphEntrie.getGymWorkoutsAmount();
            int sailingAmount = userGraphEntrie.getSailingWorkoutsAmount();
            int runningAmount = userGraphEntrie.getRunningWorkoutsAmount();
            int walkingAmount = userGraphEntrie.getWalkingWorkoutsAmount();
            int skiingAmount = userGraphEntrie.getSkiingWorkoutsAmount();
            int hikingAmount = userGraphEntrie.getHikingWorkoutsAmount();

            barEntries.add(new BarEntry(entryNumber,new float[]{gymAmount,sailingAmount,runningAmount
                                                                ,walkingAmount,skiingAmount,
                                                                hikingAmount}));
            entryNumber++;

            // do what you have to do here
            // In your case, another loop.
        }
        //TODO some sort of safety necessary for multi-threading , might need revising
            setData(barEntries);

    }


    public void setData(ArrayList<BarEntry> barChartEntries) {
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
        barDataSet.setColors(new int[] { R.color.Red, R.color.Green, R.color.Yellow,
                R.color.Brown, R.color.Pink, R.color.Blue},getActivity());
        barDataSet.setStackLabels(new String[] {"Gym","Sail","Run","Walk","Ski","Hike"});


        barChart.setData(barData);

        //get the set of keys from the hashmap and convert them to an array
        Set<String> keys = userTrainingMap.keySet();
        String[] names = keys.toArray(new String[keys.size()]);

/*        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1);

        //used the given array of keys to set labels
        xAxis.setValueFormatter(new XAxisValueFormatter(names));

        //TODO maybe add hours value on the side? / minutes?
        //redraw graph every time? /does nothing
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }


    public void prepareAllEventsData()
    {

        int entryNumber = 0;
        allEventsBarEntries.clear();
        //for each entry in the map created, create a bar entry that contains the training details
        for(Map.Entry<String, UserGraphEntries> entry : squadWorkoutMap.entrySet()) {
            UserGraphEntries userGraphEntrie = entry.getValue();
            int gymAmount = userGraphEntrie.getGymWorkoutsAmount();
            int sailingAmount = userGraphEntrie.getSailingWorkoutsAmount();
            int runningAmount = userGraphEntrie.getRunningWorkoutsAmount();
            int walkingAmount = userGraphEntrie.getWalkingWorkoutsAmount();
            int skiingAmount = userGraphEntrie.getSkiingWorkoutsAmount();
            int hikingAmount = userGraphEntrie.getHikingWorkoutsAmount();

            allEventsBarEntries.add(new BarEntry(entryNumber,new float[]{gymAmount,sailingAmount,runningAmount
                    ,walkingAmount,skiingAmount,
                    hikingAmount}));
            entryNumber++;

        }
        //once the bar entries all create it, pass them to be displayed
        setAllEventsData(allEventsBarEntries);

    }

    public void setAllEventsData(ArrayList<BarEntry> barChartEntries) {
        allEventsBarChart.setMaxVisibleValueCount(40);
        allEventsBarChart.setDrawValueAboveBar(true);
        allEventsBarChart.setPinchZoom(false);
        allEventsBarChart.setDrawGridBackground(true);

        //set graph description
        BarDataSet barDataSet = new BarDataSet(barChartEntries, "Training per squad");

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        //TODO find better colors
        //set colors and their labels
        //TODO crashes if tapped repeatedly
        //barDataSet.setColors(new int[]{ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.back1), ContextCompat.getColor(getActivity(), R.color.com_facebook_blue)});
        barDataSet.setColors(new int[] { R.color.Red, R.color.Green, R.color.Yellow,
                R.color.Brown, R.color.Pink, R.color.Blue},getActivity());
        barDataSet.setStackLabels(new String[] {"Gym","Sail","Run","Walk","Ski","Hike"});
        allEventsBarChart.setData(barData);

        //get the set of keys from the hashmap and convert them to an array
        Set<String> keys = squadWorkoutMap.keySet();
        String[] names = keys.toArray(new String[keys.size()]);


        XAxis xAxis = allEventsBarChart.getXAxis();
        xAxis.setGranularity(1f);

        //used the given array of keys to set labels
        xAxis.setValueFormatter(new XAxisValueFormatter(names));


        //TODO maybe add hours value on the side? / minutes?
        //redraw graph every time?
        allEventsBarChart.invalidate();
    }


    //sets data for the 3rd chart that represnts workouts per sailor, on a monthly basis
    public void setIndividualData(String name) {


        workoutLogMap = new HashMap<>();
        for(int i = 0;i<12;i++)
        {
            //initialise all 12 entries to 0 as a start point
            workoutLogMap.put(i,0);
        }

        retrieveUserEventsAndDates(name, new RetrieveUserDateEventsCallback() {
            @Override
            public void onUserDateEventsCallback(Map<Integer, Integer> userDateWorkouts) {
                //clear map every time, as different sailors might be needed to be displayed
                indivUserEventsBarEntries.clear();
                for(Map.Entry<Integer,Integer> entry : userDateWorkouts.entrySet())
                {
                    //x values represents the month, Y values represent no of workouts done
                    indivUserEventsBarEntries.add(new BarEntry(entry.getKey(),new float[]{entry.getValue()}));
                }
                //labels for xAxis 1 for each month
                final ArrayList<String> xLabel = new ArrayList<>();
                xLabel.add("Jan");
                xLabel.add("Feb");
                xLabel.add("Mar");
                xLabel.add("Apr");
                xLabel.add("May");
                xLabel.add("Jun");
                xLabel.add("Jul");
                xLabel.add("Aug");
                xLabel.add("Sep");
                xLabel.add("Oct");
                xLabel.add("Nov");
                xLabel.add("Dec");

                //init chart and set default values
                indivBarChart.setDrawValueAboveBar(true);
                indivBarChart.setPinchZoom(false);
                indivBarChart.setDrawGridBackground(true);

                //set graph description
                BarDataSet barDataSet = new BarDataSet(indivUserEventsBarEntries, "Training per sailor");
                BarData barData = new BarData(barDataSet);
                barData.setBarWidth(0.9f);
                indivBarChart.setData(barData);

                //get the set of keys from the hashmap and convert them to an array
                String[] names = xLabel.toArray(new String[xLabel.size()]);


                XAxis xAxis = indivBarChart.getXAxis();
                xAxis.setValueFormatter(new XAxisValueFormatter(names));
                xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                xAxis.setGranularity(1f);

                //redraw graph every time?*/
                indivBarChart.invalidate();
            }
        });
    }

    //based on the clicked user on the recycler view, pass it onwards, check for events registered with that name
    //and then store in a map the number of events done, based on their month
    private void retrieveUserEventsAndDates(String name, final RetrieveUserDateEventsCallback callback) {
        mFirebaseFirestore.collection("Events").whereEqualTo("User Name",name)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //for each workout get the month in which was done
                            Date date = (Date) document.get("Training Date");
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            int month = cal.get(Calendar.MONTH);
                            //update a map based on month , key is month, value- is no of activities done
                            if(workoutLogMap.containsKey(month))
                            {
                                //as map was previously initialised all values will be contained
                                //for each workout increase value by 1
                                int currentValue = workoutLogMap.get(month);
                                currentValue += 1;
                                workoutLogMap.put(month,currentValue);
                            }
                        }
                        callback.onUserDateEventsCallback(workoutLogMap);
                    }
                }

            }
        });
    }


    private interface FirestoreCallback
    {
        void onCallback(Map<String, UserGraphEntries> userTrainingMap);
    }

    private interface getSquadsNameCallback
    {
        void ongetSquadsNameCallback();
    }

    private interface IsCoachCallback
    {
        void onIsCoachCallback(Boolean isCoach);
    }

    private interface RetrieveUserDateEventsCallback
    {
        void onUserDateEventsCallback(Map<Integer, Integer> userDateWorkouts);
    }



}
