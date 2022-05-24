package com.example.unicon_project.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecommendCondition {


    private Boolean month_rent,deposit;
    private String month_rent_price , deposit_price;
    private String live_period_start, live_period_end;
    private String maintenance_cost;
    private String room_size;
    private String floor,structure;
    private Map<String, Boolean > maintains;
    private Map<String, Boolean > options;
    private String writerId;
    private String productId;

    public RecommendCondition(){

        this.month_rent_price="";
        this.deposit_price="";
        this.live_period_start="";
        this.live_period_end="";

        this.maintenance_cost ="";
        this.room_size="";
        this.floor="";
        this.structure="";
        this.maintains = new HashMap<>();
        this.options = new HashMap<>();
        this.month_rent = false;
        this.deposit = false;
        this.productId = "";
        this.writerId = "";

        //집주인 동의여부 ,  관리비 옵션
        this.maintains.put("owner_agree", false);
        this.maintains.put("elec_cost", false);
        this.maintains.put("gas_cost", false);
        this.maintains.put("water_cost", false);
        this.maintains.put("internet_cost", false);

        //세부옵션
        this.options.put("elec_boiler",false);
        this.options.put("gas_boiler",false);
        this.options.put("induction",false);
        this.options.put("aircon",false);
        this.options.put("washer",false);
        this.options.put("refrigerator",false);
        this.options.put("closet",false);
        this.options.put("gasrange",false);
        this.options.put("highlight",false);
        this.options.put("convenience_store",false);
        this.options.put("subway",false);
        this.options.put("parking",false);
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getMonth_rent_price() {
        return month_rent_price;
    }

    public void setMonth_rent_price(String month_rent_price) {
        this.month_rent_price = month_rent_price;
    }

    public String getDeposit_price() {
        return deposit_price;
    }

    public void setDeposit_price(String deposit_price) {
        this.deposit_price = deposit_price;
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

    public String getRoom_size() {
        return room_size;
    }

    public void setRoom_size(String room_size) {
        this.room_size = room_size;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public Map<String, Boolean> getMaintains() {
        return maintains;
    }

    public void setMaintains(Map<String, Boolean> maintains) {
        this.maintains = maintains;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Boolean> options) {
        this.options = options;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }
}
