package com.example.unicon_project.Classes;

public class ChattingListData {
    String chattingName;
    String userName;
    String productID;
    String chattingID;

    public String getChattingID() {
        return chattingID;
    }

    public void setChattingID(String chattingID) {
        this.chattingID = chattingID;
    }

    int unread;

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public ChattingListData() {
        chattingName = new String();
        userName = new String();
        productID = new String();
        chattingID = new String();
        unread = 0;
    }

    public ChattingListData(String chattingName, String userName, String productID, String chattingID, int unread)
    {
        this.chattingName = chattingName;
        this.userName = userName;
        this.productID = productID;
        this.chattingID = chattingID;
        this.unread = unread;
    }

    public String getChattingName() {
        return chattingName;
    }

    public void setChattingName(String chattingName) {
        this.chattingName = chattingName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
