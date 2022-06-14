package com.unicon.unicon_project.Classes;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseProduct implements Serializable {

    private String home_address;
    private List<Double> home_latlng_double;
    private LatLng home_latlng;
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
    private String writerId;
    private String home_name;
    private Timestamp timestamp;

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
        this.writerId="";
        this.home_name = "";
        this.timestamp = null;

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


    public PurchaseProduct(String home_address, Boolean month_rent, Boolean deposit, Boolean negotiable, String deposit_price_max, String month_price_min, String month_price_max, String live_period_start, String live_period_end, String maintenance_cost, String room_size_min, String room_size_max, String structure, String specific, Map<String, Boolean> maintains, Map<String, Boolean> options, String productId, String writerId, String home_name, Timestamp timestamp,List<Double> home_latlng_double) {
        this.home_address = home_address;
        this.home_latlng_double = home_latlng_double;
        this.home_latlng=getHome_latlng(home_latlng_double);
        this.month_rent = month_rent;
        this.deposit = deposit;
        this.negotiable = negotiable;
        this.deposit_price_max = deposit_price_max;
        this.month_price_min = month_price_min;
        this.month_price_max = month_price_max;
        this.live_period_start = live_period_start;
        this.live_period_end = live_period_end;
        this.maintenance_cost = maintenance_cost;
        this.room_size_min = room_size_min;
        this.room_size_max = room_size_max;
        this.structure = structure;
        this.specific = specific;
        this.maintains = maintains;
        this.options = options;
        this.productId = productId;
        this.writerId = writerId;
        this.home_name = home_name;
        this.timestamp = timestamp;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getHome_name() {
        return home_name;
    }

    public void setHome_name(String home_name) {
        this.home_name = home_name;
    }

    public String getMonth_price_min() {
        return month_price_min;
    }

    public void setMonth_price_min(String month_price_min) {
        this.month_price_min = month_price_min;
    }

    public String getMonth_price_max() {
        return month_price_max;
    }

    public void setMonth_price_max(String month_price_max) {
        this.month_price_max = month_price_max;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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

    public List<Double> getHome_latlng_double() {
        return home_latlng_double;
    }

    public void setHome_latlng_double(List<Double> home_latlng_double) {
        this.home_latlng_double = home_latlng_double;
    }

    public com.google.android.gms.maps.model.LatLng getHome_latlng(List<Double> p) {
        LatLng ret = new LatLng(p.get(0),p.get(1));
        return ret;
    }

    public void setHome_latlng(LatLng home_latlng) {
        this.home_latlng = home_latlng;
    }
}
