package com.example.android.fireapp;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class XAxisValueFormatter implements IAxisValueFormatter {

    private String[] stringArrayList;

    public XAxisValueFormatter(String[] stringArrayList) {
        this.stringArrayList = stringArrayList;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return stringArrayList[(int)value];
    }
}
