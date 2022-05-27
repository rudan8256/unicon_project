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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;

import com.example.unicon_project.Adapters.SaleProductAdapter;
import com.example.unicon_project.Classes.RecommendCondition;
import com.example.unicon_project.Classes.SaleProduct;
import com.example.unicon_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RecommendPage extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout recommend_condition;
    private RecyclerView recommend_list;
    private List<SaleProduct> mDatas;
    SaleProductAdapter saleProductAdapter;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Dialog condition_dialog;
    RecommendCondition preUserdata;

    private  EditText deposit_price, month_price,live_period_start,live_period_end;
    private EditText maintenance_cost, room_size;
    private String curdate;
    private LinearLayout deposit,month_rent, elec_cost, gas_cost, water_cost, internet_cost;
    private LinearLayout elec_boiler, gas_boiler, induction, aircon, washer, refrigerator, closet, gasrange,highlight;
    private LinearLayout convenience_store, subway, parking;
    private Map<String, Boolean > maintains,options;
    private Switch owner_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_page);

        recommend_condition=findViewById(R.id.recommend_condition);
        recommend_list=findViewById(R.id.recommend_list);


        preUserdata = new RecommendCondition();


        condition_dialog= new Dialog(this);
        condition_dialog.setContentView(R.layout.dialog_reccondition);
        condition_dialog.setCanceledOnTouchOutside(true);

        Construter();

        recommend_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 showDialog();
            }
        });

        updateDatas();
        searchInFB();



    }

    public void searchInFB(){


            mStore.collection("Usercondition")
                    .whereIn("writerId", Collections.singletonList(mAuth.getUid()))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    preUserdata =document.toObject(RecommendCondition.class);
                                }

                                month_price.setText(preUserdata.getMonth_rent_price());
                               deposit_price.setText(preUserdata.getDeposit_price());
                                live_period_start.setText(preUserdata.getLive_period_start());
                                live_period_end.setText(preUserdata.getLive_period_end());
                               maintenance_cost.setText(preUserdata.getMaintenance_cost());
                                room_size.setText(preUserdata.getRoom_size());

                                    if( preUserdata.getDeposit() ){
                                        deposit.setBackgroundColor(Color.BLUE); }
                                    else{
                                        deposit.setBackgroundColor(Color.WHITE); }


                                    if(  preUserdata.getMonth_rent() ){
                                        month_rent.setBackgroundColor(Color.BLUE); }
                                    else{
                                        month_rent.setBackgroundColor(Color.WHITE); }


                                    if( preUserdata.getMaintains().get("elec_cost") ){
                                       elec_cost.setBackgroundColor(Color.BLUE);
                                    } else{
                                        elec_cost.setBackgroundColor(Color.WHITE);
                                    }

                                    if(  preUserdata.getMaintains().get("gas_cost") ){
                                       gas_cost.setBackgroundColor(Color.BLUE);
                                    } else{
                                       gas_cost.setBackgroundColor(Color.WHITE);
                                    }

                                    if( preUserdata.getMaintains().get("water_cost") ){
                                         water_cost.setBackgroundColor(Color.BLUE);
                                    } else{
                                       water_cost.setBackgroundColor(Color.WHITE);
                                    }

                                    if( preUserdata.getMaintains().get("internet_cost") ){
                                        internet_cost.setBackgroundColor(Color.BLUE);
                                    } else{
                                        internet_cost.setBackgroundColor(Color.WHITE);
                                    }

                                    if( preUserdata.getOptions().get("elec_boiler") ){
                                       elec_boiler.setBackgroundColor(Color.BLUE);
                                    } else{
                                      elec_boiler.setBackgroundColor(Color.WHITE);
                                    }


                                    if( preUserdata.getOptions().get("gas_boiler") ){
                                        gas_boiler.setBackgroundColor(Color.BLUE);
                                    } else{
                                       gas_boiler.setBackgroundColor(Color.WHITE);
                                    }

                                    if( preUserdata.getOptions().get("induction") ){
                                        induction.setBackgroundColor(Color.BLUE);
                                    } else{
                                         induction.setBackgroundColor(Color.WHITE);
                                    }

                                    if( preUserdata.getOptions().get("aircon") ){
                                        aircon.setBackgroundColor(Color.BLUE);
                                    } else{
                                        aircon.setBackgroundColor(Color.WHITE);
                                    }

                                    if( preUserdata.getOptions().get("washer") ){
                                        washer.setBackgroundColor(Color.BLUE);
                                    } else{
                                         washer.setBackgroundColor(Color.WHITE);
                                    }

                                    if( preUserdata.getOptions().get("refrigerator") ){
                                         refrigerator.setBackgroundColor(Color.BLUE);
                                    } else{
                                        ;  refrigerator.setBackgroundColor(Color.WHITE);
                                    }

                                    if(  preUserdata.getOptions().get("closet") ){
                                        closet.setBackgroundColor(Color.BLUE);
                                    } else{
                                       closet.setBackgroundColor(Color.WHITE);
                                    }

                                    if( preUserdata.getOptions().get("gasrange") ){
                                        gasrange.setBackgroundColor(Color.BLUE);
                                    } else{
                                         gasrange.setBackgroundColor(Color.WHITE);
                                    }

                                    if(  preUserdata.getOptions().get("highlight") ){
                                         highlight.setBackgroundColor(Color.BLUE);
                                    } else{
                                         highlight.setBackgroundColor(Color.WHITE);
                                    }

                                    if(  preUserdata.getOptions().get("convenience_store") ){
                                        convenience_store.setBackgroundColor(Color.BLUE);
                                    } else{
                                         convenience_store.setBackgroundColor(Color.WHITE);
                                    }

                                    if(  preUserdata.getOptions().get("subway") ){
                                         subway.setBackgroundColor(Color.BLUE);
                                    } else{
                                        subway.setBackgroundColor(Color.WHITE);
                                    }

                                    if(  preUserdata.getOptions().get("parking") ){
                                         parking.setBackgroundColor(Color.BLUE);
                                    } else{
                                         parking.setBackgroundColor(Color.WHITE);
                                    }


                            }

                        }
                    });


    }

    public void updateDatas() {
        mDatas = new ArrayList<>();//
        mStore.collection("SaleProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task!=null){
                    mDatas.clear();
                    Log.e("###","쿼리개수 : "+task.getResult().getDocuments().size());
                    for(DocumentSnapshot snap : task.getResult().getDocuments()){

                        SaleProduct curdata = snap.toObject(SaleProduct.class);
                        if(Judge(curdata)) {}
                        mDatas.add(curdata);
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

    private boolean Judge(SaleProduct curdata){

        return true;
    }

    private void showDialog() {
        condition_dialog.show();


        set_Clicklistner();

        
        maintains= preUserdata.getMaintains();
        options = preUserdata.getOptions();

        condition_dialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                

                preUserdata.setMonth_rent_price(month_price.getText().toString());
                preUserdata.setDeposit_price(deposit_price.getText().toString());
                preUserdata.setLive_period_start(live_period_start.getText().toString());
                preUserdata.setLive_period_end(live_period_end.getText().toString());
                preUserdata.setMaintenance_cost(maintenance_cost.getText().toString());
                preUserdata.setRoom_size(room_size.getText().toString());

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
            case R.id.elec_cost:
                if( !maintains.get("elec_cost") ){
                    maintains.put("elec_cost",true); elec_cost.setBackgroundColor(Color.BLUE);
                } else{
                    maintains.put("elec_cost",false);elec_cost.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.gas_cost:
                if( !maintains.get("gas_cost") ){
                    maintains.put("gas_cost",true); gas_cost.setBackgroundColor(Color.BLUE);
                } else{
                    maintains.put("gas_cost",false);gas_cost.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.water_cost:
                if( !maintains.get("water_cost") ){
                    maintains.put("water_cost",true); water_cost.setBackgroundColor(Color.BLUE);
                } else{
                    maintains.put("water_cost",false); water_cost.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.internet_cost:
                if( !maintains.get("internet_cost") ){
                    maintains.put("internet_cost",true); internet_cost.setBackgroundColor(Color.BLUE);
                } else{
                    maintains.put("internet_cost",false); internet_cost.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.elec_boiler:
                if( !options.get("elec_boiler") ){
                    options.put("elec_boiler",true); elec_boiler.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("elec_boiler",false);elec_boiler.setBackgroundColor(Color.WHITE);
                }
                break;

            case R.id.gas_boiler:
                if( !options.get("gas_boiler") ){
                    options.put("gas_boiler",true); gas_boiler.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("gas_boiler",false); gas_boiler.setBackgroundColor(Color.WHITE);
                }
                break;

            case R.id.induction:
                if( !options.get("induction") ){
                    options.put("induction",true); induction.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("induction",false); induction.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.aircon:
                if( !options.get("aircon") ){
                    options.put("aircon",true); aircon.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("aircon",false); aircon.setBackgroundColor(Color.WHITE);
                }
                break;

            case R.id.washer:
                if( !options.get("washer") ){
                    options.put("washer",true); washer.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("washer",false); washer.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.refrigerator:
                if( !options.get("refrigerator") ){
                    options.put("refrigerator",true);  refrigerator.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("refrigerator",false);  refrigerator.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.closet:
                if( !options.get("closet") ){
                    options.put("closet",true);  closet.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("closet",false);  closet.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.gasrange:
                if( !options.get("gasrange") ){
                    options.put("gasrange",true);  gasrange.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("gasrange",false); gasrange.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.highlight:
                if( !options.get("highlight") ){
                    options.put("highlight",true);  highlight.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("highlight",false); highlight.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.convenience_store:
                if( !options.get("convenience_store") ){
                    options.put("convenience_store",true); convenience_store.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("convenience_store",false); convenience_store.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.subway:
                if( !options.get("subway") ){
                    options.put("subway",true); subway.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("subway",false); subway.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.parking:
                if( !options.get("parking") ){
                    options.put("parking",true); parking.setBackgroundColor(Color.BLUE);
                } else{
                    options.put("parking",false); parking.setBackgroundColor(Color.WHITE);
                }
                break;

        }
    }

    private void Construter(){


        deposit_price = condition_dialog.findViewById(R.id.deposit_price);
        month_price = condition_dialog.findViewById(R.id.month_price);
        live_period_start = condition_dialog.findViewById(R.id.live_period_start);
        live_period_end = condition_dialog.findViewById(R.id.live_period_end);
        maintenance_cost = condition_dialog.findViewById(R.id.maintenance_cost);
        room_size = condition_dialog.findViewById(R.id.room_size);

        deposit = condition_dialog.findViewById(R.id.deposit);
        month_rent = condition_dialog.findViewById(R.id.month_rent);
        elec_boiler = condition_dialog.findViewById(R.id.elec_boiler);
        elec_cost = condition_dialog.findViewById(R.id.elec_cost);
        gas_cost = condition_dialog.findViewById(R.id.gas_cost);
        water_cost = condition_dialog.findViewById(R.id.water_cost);
        internet_cost = condition_dialog.findViewById(R.id.internet_cost);
        gas_boiler = condition_dialog.findViewById(R.id.gas_boiler);
        induction = condition_dialog.findViewById(R.id.induction);
        aircon = condition_dialog.findViewById(R.id.aircon);
        washer = condition_dialog.findViewById(R.id.washer);
        refrigerator = condition_dialog.findViewById(R.id.refrigerator);
        closet =condition_dialog.findViewById(R.id.closet);
        gasrange = condition_dialog.findViewById(R.id.gasrange);
        highlight = condition_dialog.findViewById(R.id.highlight);
        convenience_store =condition_dialog.findViewById(R.id.convenience_store);
        subway = condition_dialog.findViewById(R.id.subway);
        parking = condition_dialog.findViewById(R.id.parking);

        owner_switch=condition_dialog.findViewById(R.id.owner_switch);


    }

    private void set_Clicklistner(){

        deposit.setOnClickListener(this);
        month_rent.setOnClickListener(this);
        elec_boiler.setOnClickListener(this);
        elec_cost.setOnClickListener(this);
        gas_cost.setOnClickListener(this);
        water_cost.setOnClickListener(this);
        internet_cost.setOnClickListener(this);
        gas_boiler.setOnClickListener(this);
        induction.setOnClickListener(this);
        aircon.setOnClickListener(this);
        washer.setOnClickListener(this);
        refrigerator.setOnClickListener(this);
        closet.setOnClickListener(this);
        gasrange.setOnClickListener(this);
        highlight.setOnClickListener(this);
        convenience_store.setOnClickListener(this);
        subway.setOnClickListener(this);
        parking.setOnClickListener(this);
    }

}