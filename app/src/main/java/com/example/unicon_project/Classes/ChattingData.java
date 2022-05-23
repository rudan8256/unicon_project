package com.example.unicon_project.Classes;

public class ChattingData {
    public String msg;
    public String time;
    public String uid;

    public ChattingData() {
        msg = new String();
        time = new String();
        uid = new String();
    }

    public ChattingData(String msg, String time, String uid)
    {
        this.msg = msg;
        this.time = time;
        this.uid = uid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
