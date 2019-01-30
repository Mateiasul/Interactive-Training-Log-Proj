package com.example.android.fireapp.testTab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.fireapp.DialogFragments.EffortAlertDialogPicker;
import com.example.android.fireapp.R;

public class Main2ActivitydialogTest extends AppCompatActivity
implements EffortAlertDialogPicker.EffortPickerDialogListener {

    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_activitydialog_test);

        textView = findViewById(R.id.numView);
        button = findViewById(R.id.button2dialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });


    }
    public void openDialog()
    {
        EffortAlertDialogPicker effortDialogPicker = new EffortAlertDialogPicker();
        effortDialogPicker.show(getSupportFragmentManager(),"example dialog");
    }

    @Override
    public void applyNumber(int value) {
        textView.setText(Integer.toString(value));

    }
}
