package com.example.android.fireapp.Challenges;

public class LeaderBoardEntry {

    private int completedChallenges;

    public LeaderBoardEntry(int completedChallenges, String lastName, int ID) {
        this.completedChallenges = completedChallenges;
        this.lastName = lastName;
        this.ID = ID;
    }

    private String lastName;
    private int ID;

    public int getCompletedChallenges() {
        return completedChallenges;
    }

    public void setCompletedChallenges(int completedChallenges) {
        this.completedChallenges = completedChallenges;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

}
