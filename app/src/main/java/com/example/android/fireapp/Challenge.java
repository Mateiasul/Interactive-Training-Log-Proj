package com.example.android.fireapp;

import java.util.Date;

public class Challenge {

    private String summary;
    private String creatorUser;
    private Date date;

    public String getChallengeDocRef() {
        return challengeDocRef;
    }

    public void setChallengeDocRef(String challengeDocRef) {
        this.challengeDocRef = challengeDocRef;
    }

    private String challengeDocRef;


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(String creatorUser) {
        this.creatorUser = creatorUser;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Challenge(String summary, String creatorUser, Date date, String challengeDocRef) {
        this.summary = summary;
        this.creatorUser = creatorUser;
        this.date = date;
        this.challengeDocRef = challengeDocRef;
    }


}
