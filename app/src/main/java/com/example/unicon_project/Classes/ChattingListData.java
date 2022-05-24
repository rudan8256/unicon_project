package com.example.unicon_project.Classes;

public class ChattingListData {
    String chattingName;
    String userName;
    String productID;

    public ChattingListData() {
        chattingName = new String();
        userName = new String();
        productID = new String();
    }

    public ChattingListData(String chattingName, String userName, String productID)
    {
        this.chattingName = chattingName;
        this.userName = userName;
        this.productID = productID;
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
