package com.unicon.unicon_project.Classes;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    private String username,usertoken;
    private RecommendCondition usercondition;
    private ArrayList<String> likedProductID;
    private HashMap<String, String> likedProductHash;
    private int declare_time;

    public User(){
        this.username ="";
        this.usertoken ="";
        this.declare_time=0;
        this.usercondition = new RecommendCondition();
        this.likedProductID = new ArrayList<>();
        this.likedProductHash = new HashMap<>();
    }

    public User(String username, String usertoken, RecommendCondition usercondition, ArrayList<String> likedProductID, HashMap<String, String> likedProductHash, int declare_time) {
        this.username = username;
        this.usertoken = usertoken;
        this.usercondition = usercondition;
        this.likedProductID = likedProductID;
        this.likedProductHash = likedProductHash;
        this.declare_time = declare_time;
    }

    public int getDeclare_time() {
        return declare_time;
    }

    public void setDeclare_time(int declare_time) {
        this.declare_time = declare_time;
    }

    public ArrayList<String> getLikedProductID() {
        return likedProductID;
    }

    public void setLikedProductID(ArrayList<String> likedProductID) {
        this.likedProductID = likedProductID;
    }

    public HashMap<String, String> getLikedProductHash() {
        return likedProductHash;
    }

    public void setLikedProductHash(HashMap<String, String> likedProductHash) {
        this.likedProductHash = likedProductHash;
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
