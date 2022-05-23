package com.example.unicon_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PurchaseProduct implements Serializable {

    private String home_address;
    private Boolean month_rent, deposit, negotiable;
    private String deposit_price_max;
    private String month_price_min, month_price_max;
    private String live_period_start, live_period_end;
    private String maintenance_cost;
    private String room_size_min, room_size_max;
    private String structure;
    private String specific;
    private Map<String, Boolean> maintains;
    private Map<String, Boolean> options;
    private String productId;

    public PurchaseProduct() {
        this.home_address = "";
        this.deposit_price_max = "";
        this.month_price_min = "";
        this.month_price_max = "";
        this.live_period_start = "";
        this.live_period_end = "";
        this.maintenance_cost = "";
        this.room_size_min = "";
        this.room_size_max = "";
        this.structure = "";
        this.specific = "";
        this.maintains = new HashMap<>();
        this.options = new HashMap<>();
        this.month_rent = false;
        this.deposit = false;
        this.negotiable = false;
        this.productId = "";

        //집주인 동의여부 ,  관리비 옵션
        this.maintains.put("elec_cost", false);
        this.maintains.put("gas_cost", false);
        this.maintains.put("water_cost", false);
        this.maintains.put("internet_cost", false);

        //세부옵션
        this.options.put("elec_boiler", false);
        this.options.put("gas_boiler", false);
        this.options.put("induction", false);
        this.options.put("aircon", false);
        this.options.put("washer", false);
        this.options.put("refrigerator", false);
        this.options.put("closet", false);
        this.options.put("gasrange", false);
        this.options.put("highlight", false);
        this.options.put("convenience_store", false);
        this.options.put("subway", false);
        this.options.put("parking", false);

    }

    public String getHome_address() {
        return home_address;
    }

    public void setHome_address(String home_address) {
        this.home_address = home_address;
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

    public Boolean getNegotiable() {
        return negotiable;
    }

    public void setNegotiable(Boolean negotiable) {
        this.negotiable = negotiable;
    }

    public String getDeposit_price_max() {
        return deposit_price_max;
    }

    public void setDeposit_price_max(String deposit_price_max) {
        this.deposit_price_max = deposit_price_max;
    }

    public String getMonth_rent_price_min() {
        return month_price_min;
    }

    public void setMonth_rent_price_min(String month_rent_price_min) {
        this.month_price_min = month_rent_price_min;
    }

    public String getMonth_rent_price_max() {
        return month_price_max;
    }

    public void setMonth_rent_price_max(String month_rent_price_max) {
        this.month_price_max = month_rent_price_max;
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

    public void setMaintenance_cost(String maintenance_cost_max) {
        this.maintenance_cost = maintenance_cost_max;
    }

    public String getRoom_size_min() {
        return room_size_min;
    }

    public void setRoom_size_min(String room_size_min) {
        this.room_size_min = room_size_min;
    }

    public String getRoom_size_max() {
        return room_size_max;
    }

    public void setRoom_size_max(String room_size_max) {
        this.room_size_max = room_size_max;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getSpecific() {
        return specific;
    }

    public void setSpecific(String specific) {
        this.specific = specific;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
