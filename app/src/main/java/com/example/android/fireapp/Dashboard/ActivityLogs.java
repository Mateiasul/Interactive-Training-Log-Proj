package com.example.android.fireapp.Dashboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityLogs {


    private String activityType;
    private Date activityDate;
    private String activityTime;
    private String activityTitle;
    private String docReference;

    public String getUserSquad() {
        return userSquad;
    }

    public void setUserSquad(String userSquad) {
        this.userSquad = userSquad;
    }

    private String userSquad;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public String getDocReference() {
        return docReference;
    }

    public void setDocReference(String docReference) {
        this.docReference = docReference;
    }


    public String getActivityDuration() {
        return activityDuration;
    }

    public void setActivityDuration(String activityDuration) {
        this.activityDuration = activityDuration;
    }

    private String activityDuration;
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


    public Date getActivityDateFormat() {
        return  activityDate;
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
                        Date activityDate, String activityTime,String activityDuration,
                        int activityEffortLevel, String docReference, String userName,String userSquad) {
        this.activityTitle = activityTitle;
        this.activityType = activityType;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.activityDuration = activityDuration;
        this.activityEffortLevel = activityEffortLevel;
        this.docReference = docReference;
        this.userName = userName;
        this.userSquad = userSquad;
    }
}
