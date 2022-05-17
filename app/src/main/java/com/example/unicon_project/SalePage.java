package com.example.unicon_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SalePage extends AppCompatActivity implements View.OnClickListener {

    Button complete_btn;
    SaleProduct newproduct;
    EditText home_address, deposit_price, month_price,live_period_start,live_period_end;
    EditText maintenance_cost, room_size, specific;
    FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    FirebaseAuth mauth = FirebaseAuth.getInstance();
    String curdate;
    LinearLayout deposit,month_rent;

    private Map<String, Boolean > maintains;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_page);


        //새로운 판매글 클래스 생성
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
         deposit = findViewById(R.id.deposit);
         month_rent = findViewById(R.id.month_rent);


        maintains = newproduct.getMaintains();

        deposit.setOnClickListener(this);
        month_rent.setOnClickListener(this);



        complete_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {


                 // Create a new user with a first and last name


                 newproduct.setHome_adress(home_address.getText().toString());
                 newproduct.setMonth_rent_price(month_price.getText().toString());
                 newproduct.setDeposit_price(deposit_price.getText().toString());
                 newproduct.setLive_period_start(live_period_start.getText().toString());
                 newproduct.setLive_period_end(live_period_end.getText().toString());
                 newproduct.setMaintenance_cost(maintenance_cost.getText().toString());
                 newproduct.setRoom_size(room_size.getText().toString());
                 newproduct.setSpecific(specific.getText().toString());

                curdate = String.valueOf(System.currentTimeMillis());
                newproduct.setProductId(curdate);


                 mstore.collection("SaleProducts").document( curdate)
                         .set(newproduct)
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void unused) {
                                 Log.e("***","업로드 성공");

                             }
                         });



             }
         });



    }



    @Override
    public void onClick(View view) {
        switch ( view.getId()){
            case R.id.deposit:
                if( !newproduct.getDeposit() ){
                    newproduct.setDeposit(true);
                    deposit.setBackgroundColor(Color.BLUE); }
                else{
                    newproduct.setDeposit(false);
                    deposit.setBackgroundColor(Color.WHITE); }
                break;
            case R.id.month_rent:
                if( !newproduct.getMonth_rent() ){
                    newproduct.setMonth_rent(true);
                    month_rent.setBackgroundColor(Color.BLUE); }
                else{
                    newproduct.setMonth_rent(false);
                    month_rent.setBackgroundColor(Color.WHITE); }
                break;


        }
    }


}

