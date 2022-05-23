package com.example.unicon_project;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaleProduct implements Serializable {

    /*
    주소
    월세 , 전세
    월세시 ( 보증금 , 월세 )
    전세시 (보증금)

    집주인 전대차 동의 비동의 여부
   입주기간
   방내부사진
   관리비
   전기세, 가스 , 수도 ,인터넷
   면적
   층 , 구조
   옵션( 전기보일러 가스보일러 인덕션
   에어컨 세탁기 냉장고 옷장 가스레인지 하이라이트
   편세권 역세권 주차가능)
   개인의견 (소음 , 낙후 , 집주인  평점 선그래프 )
   상세설명
     */

    private String home_adress;
    private Boolean month_rent,deposit;
    private String month_rent_price , deposit_price;
    private String live_period_start, live_period_end;
    private ArrayList<String> images_url;
    private String maintenance_cost;
    private String room_size;
    private String floor,structure;
    private String specific;
    private Map<String, Boolean > maintains;
    private Map<String, Boolean > options;
    private Map<String,String> personal_proposal;
    private String productId;




    public SaleProduct(){
        this.home_adress="" ;
        this.month_rent_price="";
        this.deposit_price="";
        this.live_period_start="";
        this.live_period_end="";
        this.images_url= new ArrayList<>();
        this.maintenance_cost ="";
        this.room_size="";
        this.floor="";
        this.structure="";
        this.specific = "";
        this.maintains = new HashMap<>();
        this.options = new HashMap<>();
        this.personal_proposal = new HashMap<>();
        this.month_rent = false;
        this.deposit = false;
        this.productId = "";
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

        //개인 의견
        this.personal_proposal.put("noise","");
        this.personal_proposal.put("destroy","");
        this.personal_proposal.put("homeowner","");

    }
    public SaleProduct(String home_adress, Boolean month_rent, Boolean deposit, String month_rent_price, String deposit_price, String live_period_start, String live_period_end, ArrayList<String> images_url, String maintenance_cost, String room_size, String floor, String structure, String specific, Map<String, Boolean> maintains, Map<String, Boolean> options, Map<String, String> personal_proposal, String productId) {
        this.home_adress = home_adress;
        this.month_rent = month_rent;
        this.deposit = deposit;
        this.month_rent_price = month_rent_price;
        this.deposit_price = deposit_price;
        this.live_period_start = live_period_start;
        this.live_period_end = live_period_end;
        this.images_url = images_url;
        this.maintenance_cost = maintenance_cost;
        this.room_size = room_size;
        this.floor = floor;
        this.structure = structure;
        this.specific = specific;
        this.maintains = maintains;
        this.options = options;
        this.personal_proposal = personal_proposal;
        this.productId = productId;
    }



    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public String getHome_adress() {
        return home_adress;
    }

    public void setHome_adress(String home_adress) {
        this.home_adress = home_adress;
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

    public ArrayList<String> getImages_url() {
        return images_url;
    }

    public void setImages_url(ArrayList<String> images_url) {
        this.images_url = images_url;
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

    public Map<String, String> getPersonal_proposal() {
        return personal_proposal;
    }

    public void setPersonal_proposal(Map<String, String> personal_proposal) {
        this.personal_proposal = personal_proposal;
    }


}
