package com.ArabProgrammers.CollegeProject;

public class uploadDistanceByKm {

    private int km;
    private String userId;

    public uploadDistanceByKm() {
    }

    public uploadDistanceByKm(int km, String userId) {
        this.km = km;
        this.userId = userId;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
