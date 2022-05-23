package com.example.unicon_project.Pages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.unicon_project.Adapters.PurchaseProductAdapter;
import com.example.unicon_project.Classes.PurchaseProduct;
import com.example.unicon_project.Pages.PurchaseProductPage;
import com.example.unicon_project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PurchaseList extends AppCompatActivity {

    private RecyclerView mPostRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<PurchaseProduct> mDatas;
    PurchaseProductAdapter purchaseProductAdapter;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        mPostRecyclerView = findViewById(R.id.salelist_recy);
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
    }
    public void updateDatas() {
        mDatas = new ArrayList<>();//
        mStore.collection("PurchaseProducts")//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    mDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                        PurchaseProduct product = snap.toObject(PurchaseProduct.class);
                                        mDatas.add(product);//여기까지가 게시글에 해당하는 데이터 적용
                                    }
                                } else {
                                }
                                purchaseProductAdapter = new PurchaseProductAdapter( mDatas);
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