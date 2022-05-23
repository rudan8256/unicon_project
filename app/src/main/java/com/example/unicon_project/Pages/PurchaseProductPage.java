package com.example.unicon_project.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.unicon_project.Classes.PurchaseProduct;
import com.example.unicon_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PurchaseProductPage extends AppCompatActivity {

    PurchaseProduct select_data;
    private Button complete_btn;
    private TextView home_address, deposit_price_max, month_price_min, month_price_max, live_period_start, live_period_end;
    private TextView maintenance_cost, room_size_min, room_size_max, specific;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private LinearLayout deposit, month_rent, negotiable, elec_cost, gas_cost, water_cost, internet_cost;
    private LinearLayout elec_boiler, gas_boiler, induction, aircon, washer, refrigerator, closet, gasrange, highlight;
    private LinearLayout convenience_store, subway, parking;

    private ArrayList<String > image_urllist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_product_page);

        Intent intent = getIntent();
        select_data = (PurchaseProduct) intent.getSerializableExtra("select_data");

        Construter();
        WritingData();
        Image_Load();
    }

    private void Image_Load(){
       // image_urllist = select_data.getImages_url();
    }

    private void Construter(){
        complete_btn = findViewById(R.id.complete_btn);
        home_address = findViewById(R.id.home_address);
        deposit_price_max = findViewById(R.id.deposit_price_max);
        month_price_min = findViewById(R.id.month_price_min);
        month_price_max = findViewById(R.id.month_price_max);
        live_period_start = findViewById(R.id.live_period_start);
        live_period_end = findViewById(R.id.live_period_end);
        maintenance_cost = findViewById(R.id.maintenance_cost);
        room_size_min = findViewById(R.id.room_size_min);
        room_size_max = findViewById(R.id.room_size_max);
        specific = findViewById(R.id.specific);

        deposit = findViewById(R.id.deposit);
        month_rent = findViewById(R.id.month_rent);
        negotiable = findViewById(R.id.negotiable);
        elec_boiler = findViewById(R.id.elec_boiler);
        elec_cost = findViewById(R.id.elec_cost);
        gas_cost = findViewById(R.id.gas_cost);
        water_cost = findViewById(R.id.water_cost);
        internet_cost = findViewById(R.id.internet_cost);
        gas_boiler = findViewById(R.id.gas_boiler);
        induction = findViewById(R.id.induction);
        aircon = findViewById(R.id.aircon);
        washer = findViewById(R.id.washer);
        refrigerator = findViewById(R.id.refrigerator);
        closet =findViewById(R.id.closet);
        gasrange = findViewById(R.id.gasrange);
        highlight = findViewById(R.id.highlight);
        convenience_store =findViewById(R.id.convenience_store);
        subway = findViewById(R.id.subway);
        parking = findViewById(R.id.parking);

    }

    private void WritingData(){

        //데이터 부여
        home_address.setText(select_data.getHome_address());
        deposit_price_max.setText(select_data.getDeposit_price_max());
        month_price_min.setText(select_data.getMonth_rent_price_min());
        month_price_max.setText(select_data.getMonth_rent_price_max());
        live_period_start.setText(select_data.getLive_period_start());
        live_period_end.setText(select_data.getLive_period_end());
        maintenance_cost.setText(select_data.getMaintenance_cost());
        room_size_min.setText(select_data.getRoom_size_min());
        room_size_max.setText(select_data.getRoom_size_max());
        specific.setText(select_data.getSpecific());

        if(select_data.getDeposit() ) deposit.setBackgroundColor(Color.BLUE);
        if(select_data.getMonth_rent() ) month_rent.setBackgroundColor(Color.BLUE);
        if(select_data.getNegotiable())negotiable.setBackgroundColor(Color.BLUE);


        if(select_data.getMaintains().get("elec_cost") ) elec_cost.setBackgroundColor(Color.BLUE);
        if(select_data.getMaintains().get("gas_cost") ) gas_cost.setBackgroundColor(Color.BLUE);
        if(select_data.getMaintains().get("water_cost") )water_cost.setBackgroundColor(Color.BLUE);
        if(select_data.getMaintains().get("internet_cost") )internet_cost.setBackgroundColor(Color.BLUE);

        if(select_data.getOptions().get("gas_boiler") ) gas_boiler.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("induction") ) induction.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("aircon") ) aircon.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("washer") ) washer.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("refrigerator") )refrigerator.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("closet") ) closet.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("gasrange") ) gasrange.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("highlight") ) highlight.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("convenience_store") ) convenience_store.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("subway") )  subway.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("parking") ) parking.setBackgroundColor(Color.BLUE);
        if(select_data.getOptions().get("elec_boiler") ) elec_boiler.setBackgroundColor(Color.BLUE);
    }

}