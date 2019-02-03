package com.example.android.fireapp;

public class UserGraphEntries {

    private String userName;
    private int gymWorkoutsAmount;
    private int sailingWorkoutsAmount;
    private int skiingWorkoutsAmount;
    private int walkingWorkoutsAmount;
    private int hikingWorkoutsAmount;
    private int runningWorkoutsAmount;


    public UserGraphEntries(String userName, int gymWorkoutsAmount, int sailingWorkoutsAmount, int skiingWorkoutsAmount, int walkingWorkoutsAmount, int hikingWorkoutsAmount, int runningWorkoutsAmount) {
        this.userName = userName;
        this.gymWorkoutsAmount = gymWorkoutsAmount;
        this.sailingWorkoutsAmount = sailingWorkoutsAmount;
        this.skiingWorkoutsAmount = skiingWorkoutsAmount;
        this.walkingWorkoutsAmount = walkingWorkoutsAmount;
        this.hikingWorkoutsAmount = hikingWorkoutsAmount;
        this.runningWorkoutsAmount = runningWorkoutsAmount;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getGymWorkoutsAmount() {
        return gymWorkoutsAmount;
    }

    public void setGymWorkoutsAmount(int gymWorkoutsAmount) {
        this.gymWorkoutsAmount = gymWorkoutsAmount;
    }

    public int getSailingWorkoutsAmount() {
        return sailingWorkoutsAmount;
    }

    public void setSailingWorkoutsAmount(int sailingWorkoutsAmount) {
        this.sailingWorkoutsAmount = sailingWorkoutsAmount;
    }

    public int getSkiingWorkoutsAmount() {
        return skiingWorkoutsAmount;
    }

    public void setSkiingWorkoutsAmount(int skiingWorkoutsAmount) {
        this.skiingWorkoutsAmount = skiingWorkoutsAmount;
    }

    public int getWalkingWorkoutsAmount() {
        return walkingWorkoutsAmount;
    }

    public void setWalkingWorkoutsAmount(int walkingWorkoutsAmount) {
        this.walkingWorkoutsAmount = walkingWorkoutsAmount;
    }

    public int getHikingWorkoutsAmount() {
        return hikingWorkoutsAmount;
    }

    public void setHikingWorkoutsAmount(int hikingWorkoutsAmount) {
        this.hikingWorkoutsAmount = hikingWorkoutsAmount;
    }

    public int getRunningWorkoutsAmount() {
        return runningWorkoutsAmount;
    }

    public void setRunningWorkoutsAmount(int runningWorkoutsAmount) {
        this.runningWorkoutsAmount = runningWorkoutsAmount;
    }



}
