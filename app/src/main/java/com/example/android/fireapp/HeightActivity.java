package com.example.android.fireapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.fireapp.DialogFragments.DialogDatePicker;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeightActivity extends AppCompatActivity implements  DialogDatePicker.DatePickerDialogInput {
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String userID;
    private int userWeight;
    private int userHeight;
    private EditText weightInput;
    private Button updateGraphButton;
    private TextInputEditText textInputEditTextDate;
    private TextInputEditText textInputEditTextHeight;
    private Date currentDate;
    private Timestamp activityTimeStamp;
    private GraphView weightGraph;
    private LineGraphSeries<DataPoint> series;
    private LineChart linechart;
    private Date accountCreationDate;
    private ArrayList<String> arrayList;
    private ArrayList<Entry> yValues = new ArrayList<Entry>();
    private DateFormat df = new SimpleDateFormat("MM-dd-yy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);
        mFireBaseAuth = mFireBaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();
        userID = mFireBaseAuth.getCurrentUser().getUid();
        //weightInput = findViewById(R.id.height_edit_textGraph);
        updateGraphButton = findViewById(R.id.updateGraphHeight);
        textInputEditTextDate = findViewById(R.id.date_edit_textGraph);
        textInputEditTextHeight = findViewById(R.id.height_edit_textGraph);


        //set activity toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Height Stats");
        setSupportActionBar(toolbar);


        retrieveUserDetails(new UserDetailsCallback() {
            @Override
            public void onUserDetailsCallback() {
                //init first entry with user initial weight
                linechart = findViewById(R.id.lineChartHeight);


                getExistingHeightDataPoints(new HeightDataRetrievedCallback() {
                    @Override
                    public void onHeightDataRetrievedCallback() {


                       // String date_str = df.format(accountCreationDate);

                       // arrayList.add(date_str);

                        linechart.setVisibleXRangeMinimum(2);
                        linechart.getDescription().setEnabled(false);
                       // linechart.setScaleXEnabled(true);
                        //linechart.fitScreen();

                        XAxis xAxis = linechart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextSize(12f);
                        xAxis.setTextColor(Color.RED);
                        xAxis.setDrawAxisLine(true);
                        xAxis.setDrawGridLines(true);
                        xAxis.setCenterAxisLabels(false);
                        xAxis.setGranularity(1f); // one hour
                        xAxis.setLabelCount(4);
                        xAxis.setValueFormatter(new XAxisValueFormatter(arrayList.toArray(new String[0])));


                        LineDataSet lineDataSet = new LineDataSet(yValues, "Height Data Set");
                        lineDataSet.setLineWidth(5f);
                        lineDataSet.setFillAlpha(110);
                        lineDataSet.setCircleRadius(7f);
                        lineDataSet.setDrawCircleHole(false);
                        lineDataSet.setValueTextSize(10f);


                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add(lineDataSet);


                        LineData data = new LineData(dataSets);
                        data.notifyDataChanged();
                        linechart.setData(data);
                        linechart.invalidate();
                    }
                });


            }
        });


        View rootView = findViewById(android.R.id.content);

        final List<TextInputLayout> textInputLayouts = com.example.android.fireapp.testTab.Utils.findViewsWithType(
                rootView, TextInputLayout.class);

//        saveActivityButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean noErrors = true;
//                for (TextInputLayout textInputLayout : textInputLayouts) {
//                    String editTextString = textInputLayout.getEditText().getText().toString();
//                    if (editTextString.isEmpty()) {
//                        textInputLayout.setError(getResources().getString(R.string.error_string));
//                        noErrors = false;
//                    } else {
//                        textInputLayout.setError(null);
//                    }
//                }


        updateGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean noErrors = true;
                for (TextInputLayout textInputLayout : textInputLayouts) {
                    String editTextString = textInputLayout.getEditText().getText().toString();
                    if (editTextString.isEmpty()) {
                        textInputLayout.setError(getResources().getString(R.string.error_string));
                        noErrors = false;
                    } else {
                        textInputLayout.setError(null);
                    }

            }
            if(noErrors)
            {
                int newHeight = 0;
                newHeight = Integer.parseInt(textInputEditTextHeight.getText().toString());
                String date_str = df.format(currentDate);
                addEntry(newHeight);
            }

//                if(currentDate != null && newWeight >= 0)
//                {
//                    String date_str = df.format(currentDate);
//                    addEntry(newWeight);
//                }
//                else
//                {
//                    Toast.makeText(HeightWeightActivity.this, "Invalid entries", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        textInputEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

    }

    private void getExistingHeightDataPoints(final HeightDataRetrievedCallback callback) {
        mFirebaseFirestore.collection("Users").document(userID).collection("Height Data")
                .orderBy("xValue",Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int indexI = 0;
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            int yValue = document.getLong("yValue").intValue();
                            Date date = document.getDate("xValue");
                            String date_str = df.format(date);

                            yValues.add(new Entry(indexI,yValue));
                            arrayList.add(date_str);
                            indexI++;
                        }
                        callback.onHeightDataRetrievedCallback();
                    }
                });
    }

    //this method calls the database and retrieves necessary user details for the chart
    private void retrieveUserDetails(final UserDetailsCallback callback) {
        mFirebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    userHeight = task.getResult().getLong("Height").intValue();
                    userWeight = task.getResult().getLong("Weight").intValue();
                    accountCreationDate = task.getResult().getDate("Account creation date");
                }
                callback.onUserDetailsCallback();
            }
        });
    }


    private void addEntry(int newHeight) {

        String date_str = df.format(currentDate);

        arrayList.add(date_str);
        LineData data = linechart.getData();

        if (data == null) {
            data = new LineData();
            linechart.setData(data);
        }


        // choose a random dataSet
        int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
        ILineDataSet randomSet = data.getDataSetByIndex(randomDataSetIndex);
        float value = (float) (Math.random() * 50) + 50f * (randomDataSetIndex + 1);

        data.addEntry(new Entry(randomSet.getEntryCount(), newHeight), randomDataSetIndex);
        data.notifyDataChanged();
        XAxis xAxis = linechart.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter(arrayList.toArray(new String[0])));
        // let the chart know it's data has changed
        linechart.notifyDataSetChanged();

        linechart.setVisibleXRangeMaximum(4);
        //linechart.setScaleXEnabled(true);
        //linechart.setScaleEnabled(true);
            linechart.fitScreen();
//
//            // this automatically refreshes the chart (calls invalidate())
        linechart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);

        Map<String,Object> dataPoints = new HashMap<>();
        dataPoints.put("xValue",currentDate);
        dataPoints.put("yValue",newHeight);
        mFirebaseFirestore.collection("Users").document(userID).collection("Height Data")
                .add(dataPoints).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(HeightActivity.this, "Entry logged successful", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(HeightActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DialogDatePicker();
        ((DialogDatePicker) newFragment).setDatePickerDialogInput(HeightActivity.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(Date date) {
        Calendar cal = Calendar.getInstance();
        activityTimeStamp = new Timestamp(date);
        currentDate = date;
        String date_str = df.format(date);
        textInputEditTextDate.setText(date_str);
    }

    public interface UserDetailsCallback{
        void onUserDetailsCallback();
    }

    public interface HeightDataRetrievedCallback {
        void onHeightDataRetrievedCallback();
    }

}
