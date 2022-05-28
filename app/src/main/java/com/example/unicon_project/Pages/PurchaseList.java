package com.example.unicon_project.Pages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.unicon_project.Adapters.PurchaseProductAdapter;
import com.example.unicon_project.Classes.PurchaseProduct;
import com.example.unicon_project.Manager.MyLocationManager;
import com.example.unicon_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PurchaseList extends AppCompatActivity {

    private RecyclerView mPostRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<PurchaseProduct> mDatas;
    PurchaseProductAdapter purchaseProductAdapter;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    MyLocationManager myLocationManager = MyLocationManager.getInstance();

    private String filter = "";
    private Spinner filterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        mPostRecyclerView = findViewById(R.id.purchaselist_recy);
        swipeRefreshLayout=findViewById(R.id.refresh_board);


        updateDatas();

        if(purchaseProductAdapter != null) {
            purchaseProductAdapter.setOnItemClickListener(new PurchaseProductAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent intent = new Intent(getApplicationContext(), PurchaseProductPage.class);
                    intent.putExtra("select_data", mDatas.get(position));
                    startActivity(intent);
                }
            });
        }

        filterSpinner = findViewById(R.id.spinner_filter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filter = adapterView.getItemAtPosition(i).toString();
                Log.e("###", filter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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
        mStore.collection("PurchaseProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task!=null){
                    mDatas.clear();
                    Log.e("###","쿼리개수 : "+task.getResult().getDocuments().size());
                    for(DocumentSnapshot snap : task.getResult().getDocuments()){
                        mDatas.add(snap.toObject(PurchaseProduct.class));
                    }
                }
                purchaseProductAdapter = new PurchaseProductAdapter(mDatas);
                mPostRecyclerView.setAdapter(purchaseProductAdapter);
                purchaseProductAdapter.setOnItemClickListener(new PurchaseProductAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent intent = new Intent(getApplicationContext(), PurchaseProductPage.class);
                        intent.putExtra("select_data", mDatas.get(position));
                        startActivity(intent);
                    }
                });
            }
        });

    }
}