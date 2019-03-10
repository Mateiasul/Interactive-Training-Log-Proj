package com.example.android.fireapp;

import java.util.Date;

public class LineChartDataPoint {

    public int getyValue() {
        return yValue;
    }

    public void setyValue(int yValue) {
        this.yValue = yValue;
    }

    public Date getxValue() {
        return xValue;
    }

    public void setxValue(Date xValue) {
        this.xValue = xValue;
    }

    private int yValue;
    private Date xValue;

    public LineChartDataPoint(int yValue, Date xValue) {
        this.yValue = yValue;
        this.xValue = xValue;
    }
}
