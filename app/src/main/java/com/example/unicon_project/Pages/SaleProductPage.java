package com.example.unicon_project.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.unicon_project.Adapters.MultiImageAdapter;
import com.example.unicon_project.ChattingActivity;
import com.example.unicon_project.ImageViewpager;
import com.example.unicon_project.R;
import com.example.unicon_project.Classes.SaleProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SaleProductPage extends AppCompatActivity {

    SaleProduct select_data;
    private Button complete_btn, btn_sale_chatting;
    private TextView home_address, deposit_price, month_price, live_period_start, live_period_end;
    private TextView maintenance_cost, room_size, specific, floor, structure;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private LinearLayout deposit, month_rent, elec_cost, gas_cost, water_cost, internet_cost;
    private LinearLayout elec_boiler, gas_boiler, induction, aircon, washer, refrigerator, closet, gasrange, highlight;
    private LinearLayout convenience_store, subway, parking;
    private TextView text_deposit, text_month_rent, text_negotiable, text_elec_cost, text_gas_cost, text_water_cost, text_internet_cost;
    private TextView text_elec_boiler, text_gas_boiler, text_induction, text_aircon, text_washer, text_refrigerator, text_closet, text_gasrange, text_highlight;
    private TextView text_convenience_store, text_subway, text_parking;

    private ArrayList<String> image_urllist;
    private ArrayList<Uri> uriList = new ArrayList<Uri>();
    MultiImageAdapter photoadapter;
    private RecyclerView photo_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_product_page);


        Intent intent = getIntent();
        select_data = (SaleProduct) intent.getSerializableExtra("select_data");

        photo_list = findViewById(R.id.photo_list);

        Construter();
        WritingData();
        Image_Load();


        // 채팅버튼 눌렀을 때
        btn_sale_chatting = findViewById(R.id.btn_sale_chatting);
        btn_sale_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("chattingID", "");
                intent.putExtra("productID", select_data.getProductId());
                intent.putExtra("writerID", select_data.getWriterId());
                intent.putExtra("homeAddress", select_data.getHome_adress());
                startActivity(intent);
            }
        });
    }

    private void Image_Load() {
        image_urllist = select_data.getImages_url();

        for (String image_url : image_urllist) {
            Log.d("####", "image_url : " + image_url);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();

            Log.d("###", "최종 사진 주소 : " + "post_image/" + image_url + ".jpg");
            //StorageReference submitImage = storageReference.child("post_image/" + image_url + ".jpg");
            storageReference.child("post_image/" + image_url + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    uriList.add(uri);
                    photoadapter = new MultiImageAdapter(uriList, getApplicationContext());
                    photo_list.setAdapter(photoadapter);
                    photo_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

                    photoadapter.setOnItemClickListener(new MultiImageAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int pos) {

                            Intent intent = new Intent(getApplicationContext(), ImageViewpager.class);
                            intent.putExtra("uri", uriList);
                            intent.putExtra("uri_Num", String.valueOf(pos));
                            startActivity(intent);
                        }
                    });

                    //  if(uriList.size() == image_urllist.size()) { ((MainActivity)MainActivity.maincontext).progressOFF(); }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 실패
                }
            });
        }
    }

    private void Construter() {
        complete_btn = findViewById(R.id.complete_btn);
        home_address = findViewById(R.id.home_address);
        deposit_price = findViewById(R.id.deposit_price);
        month_price = findViewById(R.id.month_price);
        live_period_start = findViewById(R.id.live_period_start);
        live_period_end = findViewById(R.id.live_period_end);
        maintenance_cost = findViewById(R.id.maintenance_cost);
        room_size = findViewById(R.id.room_size);
        specific = findViewById(R.id.specific);

        deposit = findViewById(R.id.deposit);
        month_rent = findViewById(R.id.month_rent);
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
        closet = findViewById(R.id.closet);
        gasrange = findViewById(R.id.gasrange);
        highlight = findViewById(R.id.highlight);
        convenience_store = findViewById(R.id.convenience_store);
        subway = findViewById(R.id.subway);
        parking = findViewById(R.id.parking);

        floor = findViewById(R.id.floor);
        structure = findViewById(R.id.structure);
        text_deposit = findViewById(R.id.text_deposit);
        text_month_rent = findViewById(R.id.text_month_rent);
        text_negotiable = findViewById(R.id.text_negotiable);
        text_elec_boiler = findViewById(R.id.text_elec_boiler);
        text_elec_cost = findViewById(R.id.text_elec_cost);
        text_gas_cost = findViewById(R.id.text_gas_cost);
        text_water_cost = findViewById(R.id.text_water_cost);
        text_internet_cost = findViewById(R.id.text_internet_cost);
        text_gas_boiler = findViewById(R.id.text_gas_boiler);
        text_induction = findViewById(R.id.text_induction);
        text_aircon = findViewById(R.id.text_aircon);
        text_washer = findViewById(R.id.text_washer);
        text_refrigerator = findViewById(R.id.text_refrigerator);
        text_closet = findViewById(R.id.text_closet);
        text_gasrange = findViewById(R.id.text_gasrange);
        text_highlight = findViewById(R.id.text_highlight);
        text_convenience_store = findViewById(R.id.text_convenience_store);
        text_subway = findViewById(R.id.text_subway);
        text_parking = findViewById(R.id.text_parking);
    }

    private void WritingData() {

        //데이터 부여
        home_address.setText(select_data.getHome_adress());
        deposit_price.setText(select_data.getDeposit_price());
        month_price.setText(select_data.getMonth_rent_price());
        live_period_start.setText(select_data.getLive_period_start());
        live_period_end.setText(select_data.getLive_period_end());
        maintenance_cost.setText(select_data.getMaintenance_cost());
        room_size.setText(select_data.getRoom_size());
        specific.setText(select_data.getSpecific());
        floor.setText(select_data.getFloor());
        structure.setText(select_data.getStructure());


        if (select_data.getDeposit()) {
            deposit.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_deposit.setTextColor(Color.WHITE);
        }
        if (select_data.getMonth_rent()) {
            month_rent.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_month_rent.setTextColor(Color.WHITE);
        }


        if (select_data.getMaintains().get("elec_cost")) {
            elec_cost.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_elec_cost.setTextColor(Color.WHITE);
        }

        if (select_data.getMaintains().get("gas_cost")) {
            gas_cost.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_gas_cost.setTextColor(Color.WHITE);
        }
        if (select_data.getMaintains().get("water_cost")) {
            water_cost.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_water_cost.setTextColor(Color.WHITE);
        }
        if (select_data.getMaintains().get("internet_cost")) {
            internet_cost.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_internet_cost.setTextColor(Color.WHITE);
        }

        if (select_data.getOptions().get("gas_boiler")) {
            gas_boiler.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_gas_boiler.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("induction")) {
            induction.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_induction.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("aircon")) {
            aircon.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_aircon.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("washer")) {
            washer.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_washer.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("refrigerator")) {
            refrigerator.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_refrigerator.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("closet")) {
            closet.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_closet.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("gasrange")) {
            gasrange.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_gasrange.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("highlight")) {
            highlight.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_highlight.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("convenience_store")) {
            convenience_store.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_convenience_store.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("subway")) {
            subway.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_subway.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("parking")) {
            parking.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_parking.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("elec_boiler")) {
            elec_boiler.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_elec_boiler.setTextColor(Color.WHITE);
        }
    }


}