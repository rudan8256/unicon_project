package com.example.unicon_project.Classes;

public class User {

    private String username,usertoken;
    private RecommendCondition usercondition;


    public User(){
        this.username ="";
        this.usertoken ="";
        this.usercondition = new RecommendCondition();
    }

    public User(String username, String usertoken, RecommendCondition usercondition){
        this.username =username;
        this.usertoken =usertoken;
        this.usercondition = usercondition;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public RecommendCondition getUsercondition() {
        return usercondition;
    }

    public void setUsercondition(RecommendCondition usercondition) {
        this.usercondition = usercondition;
    }
}
