package com.unicon.unicon_project.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.unicon.unicon_project.Adapters.SaleProductAdapter;
import com.unicon.unicon_project.Adapters.TagAdapter;
import com.unicon.unicon_project.Classes.SaleProduct;
import com.unicon.unicon_project.Classes.SaleProductList;
import com.unicon.unicon_project.Classes.TagData;
import com.unicon.unicon_project.Manager.MyLocationManager;
import com.unicon.unicon_project.ProgressDialog;
import com.unicon.unicon_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleList extends AppCompatActivity implements SaleProductAdapter.OnItemClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    private RecyclerView mPostRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<SaleProduct> mDatas;
    private List<String> mTagList= new ArrayList<>();
    private TagAdapter tagAdapter;
    private RecyclerView tagRecyclerView;
    SaleProductAdapter saleProductAdapter;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    SaleProductList saleProductList = SaleProductList.getInstance();
    MyLocationManager myLocationManager = MyLocationManager.getInstance();
    private String filter = "";
    private Spinner filterSpinner;
    private TextView tv_emptytag;
    Dialog tagDialog;
    Boolean [][]checkTag = new Boolean[8][17];
    String [][]cities = TagData.getTagList();


    private ImageView day_first, day_last;
    private DatePickerDialog datePickerDialog;
    private EditText deposit_price_max, month_price_min,month_price_max,live_period_start,live_period_end;
    private EditText maintenance_cost, room_size_min,room_size_max;

    private String structure;
    private LinearLayout deposit,month_rent,filter_search, month_area;
    public Map<String, Integer> structure_sel_map = new HashMap<>();
    private Spinner structureSpinner;
    private Dialog condition_dialog;
     private String Month_rentprice_max, Month_rentprice_min, Deposit_price_max ,Live_period_start="" ,Live_period_end="" ,Maintenance_cost,
            Room_size_max , Room_size_min;
     private boolean deposit_bool=false,month_bool=false;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_list);

        progressDialog = new ProgressDialog(SaleList.this);

        tagDialog = new Dialog(SaleList.this);
        tagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tagDialog.setContentView(R.layout.dialog_tag);


        tagAdapter = new TagAdapter();
        tagRecyclerView = findViewById(R.id.citylist_recy);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        tagRecyclerView.setAdapter(tagAdapter);
        mPostRecyclerView = findViewById(R.id.salelist_recy);
        swipeRefreshLayout=findViewById(R.id.refresh_board);
        saleProductAdapter = new SaleProductAdapter(null,getApplicationContext());
        mPostRecyclerView.setAdapter(saleProductAdapter);
        saleProductAdapter.setOnItemClickListener(this);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filterSpinner = findViewById(R.id.spinner_filter);
        filterSpinner.setOnItemSelectedListener(this);
        tv_emptytag = findViewById(R.id.tv_emptytag);
        tv_emptytag.setOnClickListener(this);

        filter_search=findViewById(R.id.filter_search);

        filter_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        //condition dialog constructer
        condition_dialog= new Dialog(this);
        condition_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        condition_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        condition_dialog.setContentView(R.layout.dialog_reccondition);
        condition_dialog.setCanceledOnTouchOutside(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDatas();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Construter();

        updateDatas();

        tagAdapter.setOnItemClickListener(new TagAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String target = mTagList.get(position);
                Log.e("SaleList","태그클릭 ; "+target  );

                for(int i=0;i<8;i++){
                    for(int j=0;j<cities[i].length;j++){
                        int finalI = i;
                        int finalJ = j;
                        if(cities[finalI][finalJ].equals(target)){
                            checkTag[finalI][finalJ]=false;
                        }
                    }
                }
                mTagList.remove(position);
                tagAdapter.setmDatas(mTagList);
                Log.e("SaleList",mTagList.toString());
                tagAdapter.notifyDataSetChanged();
                updateDatas();

            }
        });

        for(int i=0;i<8;i++){
            for(int j=0;j<cities[i].length;j++)
                checkTag[i][j]=false;
        }
    }


    public void updateDatas() {

        progressDialog.show();
        mDatas = new ArrayList<>();//
        List<Double> dist = new ArrayList<>();
        mStore.collection("SaleProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task!=null){
                    mDatas.clear();
                    Log.e("###","쿼리개수 : "+task.getResult().getDocuments().size());
                    Log.e("###","여긴가?");
                    for(DocumentSnapshot snap : task.getResult().getDocuments()){
                        mDatas.add(snap.toObject(SaleProduct.class));
                        if(!mTagList.isEmpty()) {
                            boolean isContain =false;
                            for(String tag : mTagList) {
                                isContain = mDatas.get(mDatas.size() - 1).getHome_adress().contains(tag);
                                if(isContain)break;
                            }
                            if(!isContain)mDatas.remove(mDatas.size()-1);
                        }
                    }

                    Log.e("###","아닌가?");

                    for(SaleProduct item : mDatas){
                        dist.add(myLocationManager.getDist(item.getHome_latlng(item.getLatlng_double()),myLocationManager.getMyCurrentPosition()));
                    }

                    saleProductList.setDist(dist);
                    saleProductList.setSaleList(mDatas);
                    saleProductAdapter.setmDatas(saleProductList.getSaleList());
                    saleProductAdapter.notifyDataSetChanged();

                    progressDialog.dismiss();

                }
            }
        });

    }

    private void showDialog() {


        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        if(month<10 && day <10){
                            live_period_start.setText( year+"/0"+month+"/0"+day);
                        }
                        else if(month<10 ){
                            live_period_start.setText( year+"/0"+month+"/"+day);
                        }
                        else if(day<10 ){
                            live_period_start.setText( year+"/"+month+"/0"+day);
                        }
                        else {
                            live_period_start.setText(year + "/" + month + "/" + day);
                        }
                    }
                }, mYear, mMonth, mDay);

        DatePickerDialog datePickerDialog1 = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        if(month<10 && day <10){
                            live_period_end.setText( year+"/0"+month+"/0"+day);
                        }
                        else if(month<10 ){
                            live_period_end.setText( year+"/0"+month+"/"+day);
                        }
                        else if(day<10 ){
                            live_period_end.setText( year+"/"+month+"/0"+day);
                        }
                        else {
                            live_period_end.setText(year + "/" + month + "/" + day);
                        }
                    }
                }, mYear, mMonth, mDay);

        day_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.show();
            }
        });

        day_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                datePickerDialog1.show();
            }
        });

        condition_dialog.findViewById(R.id.cancle_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                condition_dialog.dismiss();
            }
        });


        set_Clicklistner();


        condition_dialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Month_rentprice_max=month_price_max.getText().toString();
               Month_rentprice_min=month_price_min.getText().toString();
                Deposit_price_max=deposit_price_max.getText().toString();

                Live_period_start=live_period_start.getText().toString();
                Live_period_end=live_period_end.getText().toString();
                Maintenance_cost=maintenance_cost.getText().toString();
                Room_size_max=room_size_max.getText().toString();
               Room_size_min=room_size_min.getText().toString();


               List<SaleProduct> filter_datas = new ArrayList<>();
                Log.e("@@@@@", String.valueOf(mDatas.size()));

                for(SaleProduct item : mDatas){

                    if(deposit_bool== item.getDeposit() || month_bool == item.getMonth_rent()) {
                        if(Judge(item) > 1) {
                            Log.e("@@@@@", String.valueOf(Judge(item)));
                            filter_datas.add(item);
                        }
                    }

                }
                saleProductAdapter.setmDatas(filter_datas);
                saleProductAdapter.notifyDataSetChanged();


                condition_dialog.dismiss();


            }
        });
        condition_dialog.show();
    }

    private int Judge(SaleProduct curdata){

        int total=0;

        String cur_data_start= curdata.getLive_period_start().replace("/","");
        String cur_data_end= curdata.getLive_period_end().replace("/","");
        String pre_data_start =Live_period_start.replace("/","");
        String pre_data_end =Live_period_end.replace("/","");



//        Log.e("@@@@@",cur_data_end+' '+cur_data_start);

        //완변 조건매물 토탈 4000


        if( ! cur_data_end.equals("") && !cur_data_start.equals("") && !pre_data_end.equals("") && !pre_data_start.equals("")) {
            if (Integer.parseInt(pre_data_end) < Integer.parseInt(cur_data_start) || Integer.parseInt(cur_data_end) < Integer.parseInt(pre_data_start)) {
                return -1;
            }
        }
        if(! structure.equals("")) {
            if (!structure.equals(curdata.getStructure()) && !structure.equals("상관없음")) {
                return -1;
            }
        }

        if(! Deposit_price_max.equals("")) {
            if (!(Integer.parseInt(Deposit_price_max) >= Integer.parseInt(curdata.getDeposit_price()))) {
                return -1;
            }
        }

        if ( ! Month_rentprice_max.equals("") &&! Month_rentprice_min.equals("")) {
            if (!(Integer.parseInt(Month_rentprice_max) >= Integer.parseInt(curdata.getMonth_rent_price()) &&
                    Integer.parseInt(Month_rentprice_min) <= Integer.parseInt(curdata.getMonth_rent_price()))) {
                return -1;
            }
        }

        if(! Maintenance_cost.equals("")) {
            if(Integer.parseInt(Maintenance_cost) < Integer.parseInt(curdata.getMaintenance_cost())){
                return -1;
            }
        }
        if(! Room_size_max.equals("") &&!Room_size_min.equals("")) {
            if (!(Integer.parseInt(Room_size_min) <= Integer.parseInt(curdata.getRoom_size()) &&
                    Integer.parseInt(Room_size_max) >= Integer.parseInt(curdata.getRoom_size()))
            ) {
                return -1;
            }
        }


            return 100;



    }

    private void set_Clicklistner(){

        deposit.setOnClickListener(this);
        month_rent.setOnClickListener(this);

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

        day_first = condition_dialog.findViewById(R.id.day_first);
        day_last = condition_dialog.findViewById(R.id.day_last);
        month_area = condition_dialog.findViewById(R.id.month_area);



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

    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(getApplicationContext(), SaleProductPage.class);
        intent.putExtra("select_data", saleProductList.getSaleList().get(position));
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        saleProductList.sort(i);
        saleProductAdapter.setmDatas(saleProductList.getSaleList());
        saleProductAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_emptytag:
                showTagDialog();
                break;
            case R.id.deposit:
                if( !deposit_bool ){
                    deposit_bool=true;
                    deposit.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    deposit.setSelected(true);

                    if( month_bool ) {
                        month_bool = false;
                        month_rent.setBackground(getDrawable(R.drawable.salepage_inputborder));
                        month_rent.setSelected(false);
                    }
                    month_area.setVisibility(View.GONE);
                  }
                else{
                    deposit_bool=false;
                    deposit.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    deposit.setSelected(false);

                    month_area.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.month_rent:
                if( !month_bool ){
                    month_bool=true;
                    month_rent.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    month_rent.setSelected(true);

                        if( deposit_bool ) {
                            deposit_bool=false;
                            deposit.setBackground(getDrawable(R.drawable.salepage_inputborder));
                            deposit.setSelected(false);
                        }
                    month_area.setVisibility(View.VISIBLE);
                   }
                else{
                    month_bool=false;
                    month_rent.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    month_rent.setSelected(false);

                    }
                break;

        }
    }

    public void showTagDialog(){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(tagDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        tagDialog.show();
        tagDialog.getWindow().setAttributes(lp);
        CheckBox [][] checkBoxes = new CheckBox[8][17];
        TextView tv_done = tagDialog.findViewById(R.id.tv_done);


        checkBoxes[0][0]=tagDialog.findViewById(R.id.checkBox_0_0);
        checkBoxes[0][1]=tagDialog.findViewById(R.id.checkBox_0_1);
        checkBoxes[0][2]=tagDialog.findViewById(R.id.checkBox_0_2);
        checkBoxes[0][3]=tagDialog.findViewById(R.id.checkBox_0_3);
        checkBoxes[0][4]=tagDialog.findViewById(R.id.checkBox_0_4);
        checkBoxes[0][5]=tagDialog.findViewById(R.id.checkBox_0_5);
        
        checkBoxes[1][0]=tagDialog.findViewById(R.id.checkBox_1_0);
        checkBoxes[1][1]=tagDialog.findViewById(R.id.checkBox_1_1);
        checkBoxes[1][2]=tagDialog.findViewById(R.id.checkBox_1_2);
        checkBoxes[1][3]=tagDialog.findViewById(R.id.checkBox_1_3);
        checkBoxes[1][4]=tagDialog.findViewById(R.id.checkBox_1_4);
        checkBoxes[1][5]=tagDialog.findViewById(R.id.checkBox_1_5);
        checkBoxes[1][6]=tagDialog.findViewById(R.id.checkBox_1_6);
        checkBoxes[1][7]=tagDialog.findViewById(R.id.checkBox_1_7);
        checkBoxes[1][8]=tagDialog.findViewById(R.id.checkBox_1_8);
        checkBoxes[1][9]=tagDialog.findViewById(R.id.checkBox_1_9);
        checkBoxes[1][10]=tagDialog.findViewById(R.id.checkBox_1_10);
        checkBoxes[1][11]=tagDialog.findViewById(R.id.checkBox_1_11);

        checkBoxes[2][0]=tagDialog.findViewById(R.id.checkBox_2_0);
        checkBoxes[2][1]=tagDialog.findViewById(R.id.checkBox_2_1);
        checkBoxes[2][2]=tagDialog.findViewById(R.id.checkBox_2_2);
        checkBoxes[2][3]=tagDialog.findViewById(R.id.checkBox_2_3);
        checkBoxes[2][4]=tagDialog.findViewById(R.id.checkBox_2_4);

        checkBoxes[3][0]=tagDialog.findViewById(R.id.checkBox_3_0);
        checkBoxes[3][1]=tagDialog.findViewById(R.id.checkBox_3_1);
        checkBoxes[3][2]=tagDialog.findViewById(R.id.checkBox_3_2);

        checkBoxes[4][0]=tagDialog.findViewById(R.id.checkBox_4_0);
        checkBoxes[4][1]=tagDialog.findViewById(R.id.checkBox_4_1);
        checkBoxes[4][2]=tagDialog.findViewById(R.id.checkBox_4_2);
        checkBoxes[4][3]=tagDialog.findViewById(R.id.checkBox_4_3);
        checkBoxes[4][4]=tagDialog.findViewById(R.id.checkBox_4_4);
        checkBoxes[4][5]=tagDialog.findViewById(R.id.checkBox_4_5);
        checkBoxes[4][6]=tagDialog.findViewById(R.id.checkBox_4_6);
        checkBoxes[4][7]=tagDialog.findViewById(R.id.checkBox_4_7);
        checkBoxes[4][8]=tagDialog.findViewById(R.id.checkBox_4_8);
        checkBoxes[4][9]=tagDialog.findViewById(R.id.checkBox_4_9);
        checkBoxes[4][10]=tagDialog.findViewById(R.id.checkBox_4_10);
        checkBoxes[4][11]=tagDialog.findViewById(R.id.checkBox_4_11);
        checkBoxes[4][12]=tagDialog.findViewById(R.id.checkBox_4_12);
        checkBoxes[4][13]=tagDialog.findViewById(R.id.checkBox_4_13);
        checkBoxes[4][14]=tagDialog.findViewById(R.id.checkBox_4_14);
        checkBoxes[4][15]=tagDialog.findViewById(R.id.checkBox_4_15);

        checkBoxes[5][0]=tagDialog.findViewById(R.id.checkBox_5_0);
        checkBoxes[5][1]=tagDialog.findViewById(R.id.checkBox_5_1);
        checkBoxes[5][2]=tagDialog.findViewById(R.id.checkBox_5_2);
        checkBoxes[5][3]=tagDialog.findViewById(R.id.checkBox_5_3);
        checkBoxes[5][4]=tagDialog.findViewById(R.id.checkBox_5_4);
        checkBoxes[5][5]=tagDialog.findViewById(R.id.checkBox_5_5);
        checkBoxes[5][6]=tagDialog.findViewById(R.id.checkBox_5_6);
        checkBoxes[5][7]=tagDialog.findViewById(R.id.checkBox_5_7);
        checkBoxes[5][8]=tagDialog.findViewById(R.id.checkBox_5_8);
        checkBoxes[5][9]=tagDialog.findViewById(R.id.checkBox_5_9);
        checkBoxes[5][10]=tagDialog.findViewById(R.id.checkBox_5_10);
        checkBoxes[5][11]=tagDialog.findViewById(R.id.checkBox_5_11);

        checkBoxes[6][0]=tagDialog.findViewById(R.id.checkBox_6_0);
        checkBoxes[6][1]=tagDialog.findViewById(R.id.checkBox_6_1);
        checkBoxes[6][2]=tagDialog.findViewById(R.id.checkBox_6_2);
        checkBoxes[6][3]=tagDialog.findViewById(R.id.checkBox_6_3);
        checkBoxes[6][4]=tagDialog.findViewById(R.id.checkBox_6_4);
        checkBoxes[6][5]=tagDialog.findViewById(R.id.checkBox_6_5);
        checkBoxes[6][6]=tagDialog.findViewById(R.id.checkBox_6_6);
        checkBoxes[6][7]=tagDialog.findViewById(R.id.checkBox_6_7);
        checkBoxes[6][8]=tagDialog.findViewById(R.id.checkBox_6_8);
        checkBoxes[6][9]=tagDialog.findViewById(R.id.checkBox_6_9);
        checkBoxes[6][10]=tagDialog.findViewById(R.id.checkBox_6_10);
        checkBoxes[6][11]=tagDialog.findViewById(R.id.checkBox_6_11);
        checkBoxes[6][12]=tagDialog.findViewById(R.id.checkBox_6_12);
        checkBoxes[6][13]=tagDialog.findViewById(R.id.checkBox_6_13);
        checkBoxes[6][14]=tagDialog.findViewById(R.id.checkBox_6_14);
        checkBoxes[6][15]=tagDialog.findViewById(R.id.checkBox_6_15);

        checkBoxes[7][0]=tagDialog.findViewById(R.id.checkBox_7_0);
        checkBoxes[7][1]=tagDialog.findViewById(R.id.checkBox_7_1);
        checkBoxes[7][2]=tagDialog.findViewById(R.id.checkBox_7_2);
        checkBoxes[7][3]=tagDialog.findViewById(R.id.checkBox_7_3);
        checkBoxes[7][4]=tagDialog.findViewById(R.id.checkBox_7_4);
        checkBoxes[7][5]=tagDialog.findViewById(R.id.checkBox_7_5);
        checkBoxes[7][6]=tagDialog.findViewById(R.id.checkBox_7_6);
        checkBoxes[7][7]=tagDialog.findViewById(R.id.checkBox_7_7);
        checkBoxes[7][8]=tagDialog.findViewById(R.id.checkBox_7_8);

        for(int i=0;i<8;i++){
            Log.e("check", String.valueOf(cities[i].length));
            Log.e("check", String.valueOf(checkTag[i].length));

            for(int j=0;j<cities[i].length;j++){
                int finalI = i;
                int finalJ = j;
                Log.e("check", String.valueOf(i)+String.valueOf(j)+cities[i][j]);
                Log.e("check", String.valueOf(finalI)+String.valueOf(finalJ)+checkTag[finalI][finalJ].toString());
                checkBoxes[finalI][finalJ].setChecked(checkTag[finalI][finalJ].booleanValue());
                checkBoxes[finalI][finalJ].setText(cities[finalI][finalJ]);
            }
        }

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTagList.clear();
                for(int i=0;i<8;i++){
                    for(int j=0;j<cities[i].length;j++){
                        int finalI = i;
                        int finalJ = j;
                        checkTag[finalI][finalJ]=checkBoxes[finalI][finalJ].isChecked();
                        if(checkTag[finalI][finalJ]) {
                            mTagList.add(cities[finalI][finalJ]);

                        }
                    }
                }



                updateDatas();

                tagDialog.dismiss();
                tagAdapter.setmDatas(mTagList);
                tagAdapter.notifyDataSetChanged();
            }
        });
    }


}