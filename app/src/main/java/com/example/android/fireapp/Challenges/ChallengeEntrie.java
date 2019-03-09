package com.example.android.fireapp.Challenges;

import java.util.Date;

public class ChallengeEntrie {

    public String getCreatorName() {
        return creatorName;
    }

    private final String creatorName;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    private Date entryTime;
    private int score;

    public ChallengeEntrie(String userName, Date entryTime, int score, String duration, String summary,
        String creatorName) {
        this.userName = userName;
        this.entryTime = entryTime;
        this.score = score;
        this.duration = duration;
        this.summary = summary;
        this.creatorName = creatorName;

    }

    private String duration;
 private String summary;
}
