package com.ArabProgrammers.CollegeProject;

public class uploadUserRating {

    private int usersRatedFiveStar;
    private int usersRatedFourStar;
    private int usersRatedThreeStar;
    private int usersRatedTwoStar;
    private int usersRatedOneStar;

    public uploadUserRating() {
    }

    public uploadUserRating(int usersRatedFiveStar, int usersRatedFourStar, int usersRatedThreeStar, int usersRatedTwoStar, int usersRatedOneStar) {
        this.usersRatedFiveStar = usersRatedFiveStar;
        this.usersRatedFourStar = usersRatedFourStar;
        this.usersRatedThreeStar = usersRatedThreeStar;
        this.usersRatedTwoStar = usersRatedTwoStar;
        this.usersRatedOneStar = usersRatedOneStar;
    }


    public int getUsersRatedFiveStar() {
        return usersRatedFiveStar;
    }

    public void setUsersRatedFiveStar(int usersRatedFiveStar) {
        this.usersRatedFiveStar = usersRatedFiveStar;
    }

    public int getUsersRatedFourStar() {
        return usersRatedFourStar;
    }

    public void setUsersRatedFourStar(int usersRatedFourStar) {
        this.usersRatedFourStar = usersRatedFourStar;
    }

    public int getUsersRatedThreeStar() {
        return usersRatedThreeStar;
    }

    public void setUsersRatedThreeStar(int usersRatedThreeStar) {
        this.usersRatedThreeStar = usersRatedThreeStar;
    }

    public int getUsersRatedTwoStar() {
        return usersRatedTwoStar;
    }

    public void setUsersRatedTwoStar(int usersRatedTwoStar) {
        this.usersRatedTwoStar = usersRatedTwoStar;
    }

    public int getUsersRatedOneStar() {
        return usersRatedOneStar;
    }

    public void setUsersRatedOneStar(int usersRatedOneStar) {
        this.usersRatedOneStar = usersRatedOneStar;
    }
}
