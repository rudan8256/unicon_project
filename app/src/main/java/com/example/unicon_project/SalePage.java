package com.example.unicon_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;

public class SalePage extends AppCompatActivity {

    Button complete_btn;
    SaleProduct newproduct;
    EditText home_address, deposit_price, month_price,live_period_start,live_period_end;
    EditText maintenance_cost, room_size, specific;
    FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    FirebaseAuth mauth = FirebaseAuth.getInstance();
    String curdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_page);



         newproduct = new SaleProduct();

         complete_btn = findViewById(R.id.complete_btn);
         home_address = findViewById(R.id.home_address);
         deposit_price = findViewById(R.id.deposit_price);
         month_price = findViewById(R.id.month_price);
         live_period_start = findViewById(R.id.live_period_start);
         live_period_end = findViewById(R.id.live_period_end);
         maintenance_cost = findViewById(R.id.maintenance_cost);
         room_size = findViewById(R.id.room_size);
         specific = findViewById(R.id.specific);



         complete_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {


                 newproduct.setHome_adress(home_address.getText().toString());
                 newproduct.setMonth_rent_price(month_price.getText().toString());
                 newproduct.setDeposit_price(deposit_price.getText().toString());
                 newproduct.setLive_period_start(live_period_start.getText().toString());
                 newproduct.setLive_period_end(live_period_end.getText().toString());
                 newproduct.setMaintenance_cost(maintenance_cost.getText().toString());
                 newproduct.setRoom_size(room_size.getText().toString());
                 newproduct.setSpecific(specific.getText().toString());

                curdate = String.valueOf(System.currentTimeMillis());


                 mstore.collection("SaleProducts").document(mauth.getUid() + curdate).set(newproduct);



             }
         });



    }
}