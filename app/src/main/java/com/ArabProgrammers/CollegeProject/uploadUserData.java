package com.ArabProgrammers.CollegeProject;

public class uploadUserData {

    private String userId;
    private String userName;
    private String userPhoneNumber;
    private String userAnotherPhoneNumber;
    private String userCountry;
    private String userImageURL;
    private String userImageThumbnailsURL;
    private String userAccountType;
    private String userProfessionTypeOne;
    private String userProfessionTypeTwo;
    private String userShopTypeOne;
    private String userShopTypeTwo;
    private double userLocationLatitude;
    private double userLocationLongitude;
    private double userTotalRate;


    public uploadUserData() {

        //requierd empty constructor
    }


    public uploadUserData(String userId, String userName, String userPhoneNumber,
                          String userAnotherPhoneNumber, String userCountry, String userImageURL
            , String userImageThumbnailsURL, String userAccountType, String userProfessionTypeOne
            , String userProfessionTypeTwo, String userShopTypeOne, String userShopTypeTwo,
                          double userLocationLatitude, double userLocationLongitude , double userTotalRate) {

        this.userId = userId;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userAnotherPhoneNumber = userAnotherPhoneNumber;
        this.userCountry = userCountry;
        this.userImageURL = userImageURL;
        this.userImageThumbnailsURL = userImageThumbnailsURL;
        this.userAccountType = userAccountType;
        this.userProfessionTypeOne = userProfessionTypeOne;
        this.userProfessionTypeTwo = userProfessionTypeTwo;
        this.userShopTypeOne = userShopTypeOne;
        this.userShopTypeTwo = userShopTypeTwo;
        this.userLocationLatitude=userLocationLatitude;
        this.userLocationLongitude=userLocationLongitude;
        this.userTotalRate =userTotalRate;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserAnotherPhoneNumber() {
        return userAnotherPhoneNumber;
    }

    public void setUserAnotherPhoneNumber(String userAnotherPhoneNumber) {
        this.userAnotherPhoneNumber = userAnotherPhoneNumber;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public String getUserImageThumbnailsURL() {
        return userImageThumbnailsURL;
    }

    public void setUserImageThumbnailsURL(String userImageThumbnailsURL) {
        this.userImageThumbnailsURL = userImageThumbnailsURL;
    }

    public String getUserAccountType() {
        return userAccountType;
    }

    public void setUserAccountType(String userAccountType) {
        this.userAccountType = userAccountType;
    }

    public String getUserProfessionTypeOne() {
        return userProfessionTypeOne;
    }

    public void setUserProfessionTypeOne(String userProfessionTypeOne) {
        this.userProfessionTypeOne = userProfessionTypeOne;
    }

    public String getUserProfessionTypeTwo() {
        return userProfessionTypeTwo;
    }

    public void setUserProfessionTypeTwo(String userProfessionTypeTwo) {
        this.userProfessionTypeTwo = userProfessionTypeTwo;
    }

    public String getUserShopTypeOne() {
        return userShopTypeOne;
    }

    public void setUserShopTypeOne(String userShopTypeOne) {
        this.userShopTypeOne = userShopTypeOne;
    }

    public String getUserShopTypeTwo() {
        return userShopTypeTwo;
    }

    public void setUserShopTypeTwo(String userShopTypeTwo) {
        this.userShopTypeTwo = userShopTypeTwo;
    }
    public double getUserLocationLatitude() {
        return userLocationLatitude;
    }

    public void setUserLocationLatitude(double userLocationLatitude) {
        this.userLocationLatitude = userLocationLatitude;
    }

    public double getUserLocationLongitude() {
        return userLocationLongitude;
    }

    public void setUserLocationLongitude(double userLocationLongitude) {
        this.userLocationLongitude = userLocationLongitude;
    }

    public double getUserTotalRate() {
        return userTotalRate;
    }

    public void setUserTotalRate(double userTotalRate) {
        this.userTotalRate = userTotalRate;
    }
}
