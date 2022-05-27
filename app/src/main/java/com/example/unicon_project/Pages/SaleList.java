package com.example.unicon_project.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.unicon_project.Adapters.SaleProductAdapter;
import com.example.unicon_project.Classes.SaleProduct;
import com.example.unicon_project.Classes.SaleProductList;
import com.example.unicon_project.Manager.MyLocationManager;
import com.example.unicon_project.Pages.SaleProductPage;
import com.example.unicon_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SaleList extends AppCompatActivity implements SaleProductAdapter.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private RecyclerView mPostRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<SaleProduct> mDatas;
    SaleProductAdapter saleProductAdapter;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    SaleProductList saleProductList = SaleProductList.getInstance();
    MyLocationManager myLocationManager = MyLocationManager.getInstance();
    private String filter = "";
    private Spinner filterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_list);

        mPostRecyclerView = findViewById(R.id.salelist_recy);
        swipeRefreshLayout=findViewById(R.id.refresh_board);
        saleProductAdapter = new SaleProductAdapter(null,getApplicationContext());
        mPostRecyclerView.setAdapter(saleProductAdapter);
        saleProductAdapter.setOnItemClickListener(this);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filterSpinner = findViewById(R.id.spinner_filter);
        filterSpinner.setOnItemSelectedListener(this);

        updateDatas();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDatas();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    public void updateDatas() {
        mDatas = new ArrayList<>();//
        List<Double> dist = new ArrayList<>();
        mStore.collection("SaleProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task!=null){
                    mDatas.clear();
                    Log.e("###","쿼리개수 : "+task.getResult().getDocuments().size());
                    for(DocumentSnapshot snap : task.getResult().getDocuments()){
                        mDatas.add(snap.toObject(SaleProduct.class));
                    }
                    for(SaleProduct item : mDatas){
                        dist.add(myLocationManager.getDist(myLocationManager.getLatLng(getApplicationContext(),item.getHome_adress()),myLocationManager.getMyCurrentPosition()));
                    }
                    saleProductList.setDist(dist);
                    saleProductList.setSaleList(mDatas);
                    saleProductAdapter.setmDatas(saleProductList.getSaleList());
                    saleProductAdapter.notifyDataSetChanged();
                }
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
}