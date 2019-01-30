package com.example.android.fireapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityLogs {


    private String activityType;
    private Date activityDate;
    private String activityTime;
    private String activityTitle;
    private int activityEffortLevel;

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityDate() {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String date_str = df.format(activityDate);
        return date_str;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }


    public int getActivityEffortLevel() {
        return activityEffortLevel;
    }

    public void setActivityEffortLevel(int activityEffortLevel) {
        this.activityEffortLevel = activityEffortLevel;
    }


    public ActivityLogs(String activityTitle, String activityType,
                        Date activityDate, String activityTime,
                        int activityEffortLevel) {
        this.activityTitle = activityTitle;
        this.activityType = activityType;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.activityEffortLevel = activityEffortLevel;
    }
}
