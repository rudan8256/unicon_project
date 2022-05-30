package com.example.unicon_project.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.unicon_project.Adapters.SaleProductAdapter;
import com.example.unicon_project.Classes.RecommendCondition;
import com.example.unicon_project.Classes.SaleProduct;
import com.example.unicon_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPage extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout recommend_condition;
    private RecyclerView recommend_list;
    private List<SaleProduct> mDatas =new ArrayList<>();
    private List<Integer> dataScore = new ArrayList<>();
    SaleProductAdapter saleProductAdapter;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Dialog condition_dialog;
    RecommendCondition preUserdata;

    private  EditText deposit_price_max, month_price_min,month_price_max,live_period_start,live_period_end;
    private EditText maintenance_cost, room_size_min,room_size_max;
    private String structure;
    private LinearLayout deposit,month_rent;
    public Map<String, Integer> structure_sel_map = new HashMap<>();
    private Spinner structureSpinner;


    private boolean search_complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_page);

        recommend_condition=findViewById(R.id.recommend_condition);
        recommend_list=findViewById(R.id.recommend_list);

        structure_sel_map.put("상관없음",0);
        structure_sel_map.put("오픈형 원룸",1);
        structure_sel_map.put("분리형 원룸(방1, 거실)",2);
        structure_sel_map.put("복층형 원룸",3);
        structure_sel_map.put("투룸",4);
        structure_sel_map.put("쓰리룸",5);

        Log.e("$$$$", String.valueOf(structure_sel_map.get("오픈형 원룸")));

        preUserdata = new RecommendCondition();


        condition_dialog= new Dialog(this);
        condition_dialog.setContentView(R.layout.dialog_reccondition);
        condition_dialog.setCanceledOnTouchOutside(true);

        Construter();
        searchInFB();



        recommend_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 showDialog();
            }
        });




    }

    public void searchInFB(){


            mStore.collection("Usercondition")
                    .whereEqualTo("writerId",mAuth.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                preUserdata =document.toObject(RecommendCondition.class);
                                Log.e("@@@@@@","들어옴?");

                                deposit_price_max.setText(preUserdata.getMonth_rentprice_max());
                                month_price_max.setText(preUserdata.getMonth_rentprice_max());
                                month_price_min.setText(preUserdata.getMonth_rentprice_min());
                                live_period_start.setText(preUserdata.getLive_period_start());
                                live_period_end.setText(preUserdata.getLive_period_end());

                                maintenance_cost.setText(preUserdata.getMaintenance_cost());
                                structure = preUserdata.getStructure();
                                int cur_seldata_num;


                                if (structure != "") {

                                    cur_seldata_num = structure_sel_map.get(structure);


                                    structureSpinner.setSelection(cur_seldata_num);
                                }

                                room_size_max.setText(preUserdata.getRoom_size_max());
                                room_size_min.setText(preUserdata.getRoom_size_min());


                                if (preUserdata.getDeposit()) {
                                    deposit.setBackgroundColor(Color.BLUE);
                                } else {
                                    deposit.setBackgroundColor(Color.WHITE);
                                }


                                if (preUserdata.getMonth_rent()) {
                                    month_rent.setBackgroundColor(Color.BLUE);
                                } else {
                                    month_rent.setBackgroundColor(Color.WHITE);
                                }


                                updateDatas();
                            }
                        }
                    });

    }

    public void updateDatas() {
        mDatas.clear();//
        dataScore.clear();
        mStore.collection("SaleProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task!=null){
                    mDatas.clear();
                    Log.e("###","쿼리개수 : "+task.getResult().getDocuments().size());
                    for(DocumentSnapshot snap : task.getResult().getDocuments()){

                        SaleProduct curdata = snap.toObject(SaleProduct.class);


                        int cur_score= Judge(curdata);
                        if(cur_score > 1000) {



                            Log.e("$$$$", String.valueOf(cur_score));


                            mDatas.add(curdata);
                            dataScore.add(cur_score);
                        }

                    }
                }

                for(int i=0;i<mDatas.size()-1;i++){
                    for(int j=0;j<mDatas.size()-1;j++){
                        if(dataScore.get(j)>dataScore.get(j+1))
                            swap(j,j+1);
                    }
                }

                saleProductAdapter = new SaleProductAdapter( mDatas, getApplicationContext());
                recommend_list.setAdapter(saleProductAdapter);
                saleProductAdapter.setOnItemClickListener(new SaleProductAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent intent = new Intent(getApplicationContext(), SaleProductPage.class);
                        intent.putExtra("select_data", mDatas.get(position));
                        startActivity(intent);
                    }
                });
            }
        });
    }
    private void swap(int v,int e){
        SaleProduct temp;
        temp=mDatas.get(v);
        mDatas.set(v,mDatas.get(e));
        mDatas.set(e,temp);

        int score_temp;
        score_temp=dataScore.get(v);
        dataScore.set(v,dataScore.get(e));
        dataScore.set(e,score_temp);
    }


    private int Judge(SaleProduct curdata){

        int total=0;

        String cur_data_start= curdata.getLive_period_start().replace("/","");
        String cur_data_end= curdata.getLive_period_end().replace("/","");
        String pre_data_start = preUserdata.getLive_period_start().replace("/","");
        String pre_data_end =preUserdata.getLive_period_end().replace("/","");


        //완변 조건매물 토탈 4000


        if(  !(preUserdata.getDeposit() == curdata.getDeposit() || preUserdata.getMonth_rent() == curdata.getMonth_rent())){
           return -1;
        }
        else if(Integer.parseInt(pre_data_start) <=Integer.parseInt(cur_data_start) &&Integer.parseInt(cur_data_end) <= Integer.parseInt(pre_data_end) ){
            return  -1;
        }
        else if( ! preUserdata.getStructure().equals(curdata.getStructure()) && ! preUserdata.getStructure().equals("상관없음")){
            return  -1;
        }
        else{

            if( Integer.parseInt(preUserdata.getDeposit_price_max()) >= Integer.parseInt(curdata.getDeposit_price())){
                total += 1000;
            }

            if( Integer.parseInt(preUserdata.getMonth_rentprice_max()) >= Integer.parseInt(curdata.getMonth_rent_price()) &&
                    Integer.parseInt(preUserdata.getMonth_rentprice_min()) <= Integer.parseInt(curdata.getMonth_rent_price()) ){
                total += 1000;
            }
            else if (Integer.parseInt(preUserdata.getMonth_rentprice_max()) < Integer.parseInt(curdata.getMonth_rent_price())){
               if(Integer.parseInt(curdata.getMonth_rent_price()) - Integer.parseInt(preUserdata.getMonth_rentprice_max()) <=5){
                   total += 500 - 100*(Integer.parseInt(curdata.getMonth_rent_price()) - Integer.parseInt(preUserdata.getMonth_rentprice_max()));
               }
            }
            else{
                if( Integer.parseInt(preUserdata.getMonth_rentprice_max()) - Integer.parseInt(curdata.getMonth_rent_price()) <=5){
                    total += 500 - 100* (Integer.parseInt(preUserdata.getMonth_rentprice_max()) - Integer.parseInt(curdata.getMonth_rent_price()));
                }
            }

            if(preUserdata.getStructure().equals(curdata.getStructure())){
                total += 1000;
            }

            if( Integer.parseInt(preUserdata.getRoom_size_min()) <= Integer.parseInt(curdata.getRoom_size()) &&
                    Integer.parseInt(preUserdata.getRoom_size_max()) >= Integer.parseInt(curdata.getRoom_size())
            ){
                total += 1000;
            }



            return total;
        }



    }

    private void showDialog() {
        condition_dialog.show();


        set_Clicklistner();


        condition_dialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                

                preUserdata.setMonth_rentprice_max(month_price_max.getText().toString());
                preUserdata.setMonth_rentprice_min(month_price_min.getText().toString());
                preUserdata.setDeposit_price_max(deposit_price_max.getText().toString());

                preUserdata.setLive_period_start(live_period_start.getText().toString());
                preUserdata.setLive_period_end(live_period_end.getText().toString());
                preUserdata.setMaintenance_cost(maintenance_cost.getText().toString());
                preUserdata.setRoom_size_max(room_size_max.getText().toString());
                preUserdata.setRoom_size_min(room_size_min.getText().toString());

                preUserdata.setStructure(structure);
                preUserdata.setWriterId(mAuth.getUid());



                mStore.collection("Usercondition").document(mAuth.getUid())
                        .set(preUserdata)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("*****","업로드 성공");
                               condition_dialog.dismiss();
                            }
                        });


            }
        });
    }


    @Override
    public void onClick(View view) {
        switch ( view.getId()){

            case R.id.deposit:
                if( !preUserdata.getDeposit() ){
                    preUserdata.setDeposit(true); deposit.setBackgroundColor(Color.BLUE); }
                else{
                    preUserdata.setDeposit(false);deposit.setBackgroundColor(Color.WHITE); }
                break;
            case R.id.month_rent:
                if( !preUserdata.getMonth_rent() ){
                    preUserdata.setMonth_rent(true);month_rent.setBackgroundColor(Color.BLUE); }
                else{
                    preUserdata.setMonth_rent(false);month_rent.setBackgroundColor(Color.WHITE); }
                break;

        }
    }

    private void Construter(){


        deposit = condition_dialog.findViewById(R.id.deposit);
        month_rent = condition_dialog.findViewById(R.id.month_rent);
        deposit_price_max = condition_dialog.findViewById(R.id.deposit_price_max);
        month_price_min= condition_dialog.findViewById(R.id.month_price_min);
        month_price_max = condition_dialog.findViewById(R.id.month_price_max);

        live_period_start = condition_dialog.findViewById(R.id.live_period_start);
        live_period_end = condition_dialog.findViewById(R.id.live_period_end);

        maintenance_cost = condition_dialog.findViewById(R.id.maintenance_cost);

        room_size_min = condition_dialog.findViewById(R.id.room_size_min);
        room_size_max = condition_dialog.findViewById(R.id.room_size_max);




        structureSpinner = condition_dialog.findViewById(R.id.structure);


        structureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                structure = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    private void set_Clicklistner(){

        deposit.setOnClickListener(this);
        month_rent.setOnClickListener(this);

    }

}