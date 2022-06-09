package com.unicon.unicon_project.Classes;

public class RecommendCondition {


    private Boolean month_rent,deposit;
    private String month_rentprice_max, month_rentprice_min, deposit_price_max;
    private String live_period_start, live_period_end;
    private String maintenance_cost;
    private String room_size_max,room_size_min;
    private String structure;

    private String writerId;
    private String productId;

    public RecommendCondition(){

        this.month_rentprice_max="";
        this.month_rentprice_min="";
        this.deposit_price_max="";
        this.live_period_start="";
        this.live_period_end="";

        this.maintenance_cost ="";
        this.room_size_max="";
        this.room_size_min="";
        this.structure="";

        this.month_rent = false;
        this.deposit = false;

        this.productId = "";
        this.writerId = "";

    }

    public Boolean getMonth_rent() {
        return month_rent;
    }

    public void setMonth_rent(Boolean month_rent) {
        this.month_rent = month_rent;
    }

    public Boolean getDeposit() {
        return deposit;
    }

    public void setDeposit(Boolean deposit) {
        this.deposit = deposit;
    }

    public String getMonth_rentprice_max() {
        return month_rentprice_max;
    }

    public void setMonth_rentprice_max(String month_rentprice_max) {
        this.month_rentprice_max = month_rentprice_max;
    }

    public String getMonth_rentprice_min() {
        return month_rentprice_min;
    }

    public void setMonth_rentprice_min(String month_rentprice_min) {
        this.month_rentprice_min = month_rentprice_min;
    }

    public String getDeposit_price_max() {
        return deposit_price_max;
    }

    public void setDeposit_price_max(String deposit_price_max) {
        this.deposit_price_max = deposit_price_max;
    }

    public String getLive_period_start() {
        return live_period_start;
    }

    public void setLive_period_start(String live_period_start) {
        this.live_period_start = live_period_start;
    }

    public String getLive_period_end() {
        return live_period_end;
    }

    public void setLive_period_end(String live_period_end) {
        this.live_period_end = live_period_end;
    }

    public String getMaintenance_cost() {
        return maintenance_cost;
    }

    public void setMaintenance_cost(String maintenance_cost) {
        this.maintenance_cost = maintenance_cost;
    }

    public String getRoom_size_max() {
        return room_size_max;
    }

    public void setRoom_size_max(String room_size_max) {
        this.room_size_max = room_size_max;
    }

    public String getRoom_size_min() {
        return room_size_min;
    }

    public void setRoom_size_min(String room_size_min) {
        this.room_size_min = room_size_min;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }



}
