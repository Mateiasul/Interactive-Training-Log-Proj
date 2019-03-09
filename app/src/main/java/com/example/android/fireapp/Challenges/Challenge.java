package com.example.android.fireapp.Challenges;

import java.util.Date;

public class Challenge {

    private String summary;
    private String creatorUser;
    private Date date;

    public String getCurrentDocRef() {
        return currentDocRef;
    }

    public void setCurrentDocRef(String currentDocRef) {
        this.currentDocRef = currentDocRef;
    }

    private String currentDocRef;

    public int getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(int noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }

    private int noOfParticipants;

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

    public Challenge(String summary, String creatorUser, Date date, String challengeDocRef, int noOfParticipants,String currentDocRef) {
        this.summary = summary;
        this.creatorUser = creatorUser;
        this.date = date;
        this.challengeDocRef = challengeDocRef;
        this.noOfParticipants = noOfParticipants;
        this.currentDocRef = currentDocRef;
    }


}
