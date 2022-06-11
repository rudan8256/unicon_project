package com.unicon.unicon_project.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.unicon.unicon_project.ChattingActivity;
import com.unicon.unicon_project.Classes.PurchaseProduct;
import com.unicon.unicon_project.Classes.User;
import com.unicon.unicon_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PurchaseProductPage extends AppCompatActivity {

    PurchaseProduct select_data;
    String chattingUserID;
    private TextView home_address, deposit_price_max, month_price_min, month_price_max, live_period_start, live_period_end;
    private TextView maintenance_cost, room_size_min, room_size_max, specific, structure;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LinearLayout deposit, month_rent, negotiable, elec_cost, gas_cost, water_cost, internet_cost;
    private LinearLayout elec_boiler, gas_boiler, induction, aircon, washer, refrigerator, closet, gasrange, highlight;
    private LinearLayout convenience_store, subway, parking;
    private TextView text_deposit, text_month_rent, text_negotiable, text_elec_cost, text_gas_cost, text_water_cost, text_internet_cost;
    private TextView text_elec_boiler, text_gas_boiler, text_induction, text_aircon, text_washer, text_refrigerator, text_closet, text_gasrange, text_highlight;
    private TextView text_convenience_store, text_subway, text_parking;
    private boolean isLiked;
    private ArrayList<String> image_urllist;
    private ArrayList<String> Likes= new ArrayList<>();
    private ImageView likeButton;
    private Button btn_purchase_chatting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_product_page);

        Intent intent = getIntent();
        select_data = (PurchaseProduct) intent.getSerializableExtra("select_data");

        Construter();
        WritingData();
        GetUsername();

        btn_purchase_chatting = findViewById(R.id.btn_purchase_chatting);
        btn_purchase_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("chattingID", "");
                intent.putExtra("productID", select_data.getProductId());
                intent.putExtra("writerID", select_data.getWriterId());
                intent.putExtra("homeAddress", select_data.getHome_address());
                intent.putExtra("chattingUserID", chattingUserID);
                startActivity(intent);
            }
        });


        likeButton = findViewById(R.id.like_button);
        if (mAuth.getCurrentUser() != null) {//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            mstore.collection("User").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {

                                User Userdata = task.getResult().toObject(User.class);


                                Likes = (ArrayList<String>) Userdata.getLikedProductID();



                                if (Likes != null) {
                                    isLiked = Likes.contains(select_data.getProductId());

                                    if (isLiked)
                                        likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                                    else
                                        likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                } else {
                                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                    isLiked = false;
                                }

                            }
                        }
                    });
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLiked) {
                    Likes.remove(select_data.getProductId());
                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);

                } else {
                    Likes.add(select_data.getProductId());
                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
                isLiked = !isLiked;

                mstore.collection("User").document(select_data.getWriterId())
                        .update("likedProductID", Likes)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
    }

    private void Construter() {
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
        closet = findViewById(R.id.closet);
        gasrange = findViewById(R.id.gasrange);
        highlight = findViewById(R.id.highlight);
        convenience_store = findViewById(R.id.convenience_store);
        subway = findViewById(R.id.subway);
        parking = findViewById(R.id.parking);

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
        structure.setText(select_data.getStructure());

        if (select_data.getDeposit()) {
            deposit.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_deposit.setTextColor(Color.WHITE);
        }
        if (select_data.getMonth_rent()) {
            month_rent.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_month_rent.setTextColor(Color.WHITE);
        }
        if (select_data.getNegotiable()) {
            negotiable.setBackgroundResource(R.drawable.sale_purchase_color_round15);
            text_negotiable.setTextColor(Color.WHITE);
        }


        if (select_data.getMaintains().get("elec_cost")) {
            elec_cost.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_elec_cost.setTextColor(Color.WHITE);
        }

        if (select_data.getMaintains().get("gas_cost")) {
            gas_cost.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_gas_cost.setTextColor(Color.WHITE);
        }
        if (select_data.getMaintains().get("water_cost")) {
            water_cost.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_water_cost.setTextColor(Color.WHITE);
        }
        if (select_data.getMaintains().get("internet_cost")) {
            internet_cost.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_internet_cost.setTextColor(Color.WHITE);
        }

        if (select_data.getOptions().get("gas_boiler")) {
            gas_boiler.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_gas_boiler.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("induction")) {
            induction.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_induction.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("aircon")) {
            aircon.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_aircon.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("washer")) {
            washer.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_washer.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("refrigerator")) {
            refrigerator.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_refrigerator.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("closet")) {
            closet.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_closet.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("gasrange")) {
            gasrange.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_gasrange.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("highlight")) {
            highlight.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_highlight.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("convenience_store")) {
            convenience_store.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_convenience_store.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("subway")) {
            subway.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_subway.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("parking")) {
            parking.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_parking.setTextColor(Color.WHITE);
        }
        if (select_data.getOptions().get("elec_boiler")) {
            elec_boiler.setBackgroundResource(R.drawable.sale_purchase_color_round6);
            text_elec_boiler.setTextColor(Color.WHITE);
        }
    }

    private void GetUsername()
    {
        /* 닉네임 가져오기 */
        mstore.collection("User").document(select_data.getWriterId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        User myUser = document.toObject(User.class);
                        chattingUserID = myUser.getUsername();
                    }
                }
            }
        });
    }
}