package com.example.unicon_project;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SaleProductPage extends AppCompatActivity {

    SaleProduct select_data;
    private Button complete_btn;
    private TextView home_address, deposit_price, month_price,live_period_start,live_period_end;
    private TextView maintenance_cost, room_size, specific;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private LinearLayout deposit,month_rent, elec_cost, gas_cost, water_cost, internet_cost;
    private LinearLayout elec_boiler, gas_boiler, induction, aircon, washer, refrigerator, closet, gasrange,highlight;
    private LinearLayout convenience_store, subway, parking;

    private ArrayList<String > image_urllist;
    private ArrayList<Uri> uriList = new ArrayList<Uri>();
    MultiImageAdapter photoadapter;
    private RecyclerView photo_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_product_page);


        Intent intent = getIntent();
        select_data = (SaleProduct) intent.getSerializableExtra("select_data");

        photo_list=findViewById(R.id.photo_list);



        Construter();
        WritingData();
        Image_Load();

    }
    private void Image_Load(){
        image_urllist = select_data.getImages_url();

        for(String image_url : image_urllist) {
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

                            Intent intent=new Intent(getApplicationContext(), ImageViewpager.class);
                            intent.putExtra("uri",uriList);
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

    private void Construter(){
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
        closet =findViewById(R.id.closet);
        gasrange = findViewById(R.id.gasrange);
        highlight = findViewById(R.id.highlight);
        convenience_store =findViewById(R.id.convenience_store);
        subway = findViewById(R.id.subway);
        parking = findViewById(R.id.parking);

    }

    private void WritingData(){

        //데이터 부여
        home_address.setText(select_data.getHome_adress());
        deposit_price.setText(select_data.getDeposit_price());
        month_price.setText(select_data.getMonth_rent_price());
        live_period_start.setText(select_data.getLive_period_start());
        live_period_end.setText(select_data.getLive_period_end());
        maintenance_cost.setText(select_data.getMaintenance_cost());
        room_size.setText(select_data.getRoom_size());
        specific.setText(select_data.getSpecific());

        if(select_data.getDeposit() ) deposit.setBackgroundColor(Color.BLUE);
        if(select_data.getMonth_rent() ) month_rent.setBackgroundColor(Color.BLUE);


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