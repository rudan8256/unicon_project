package com.example.unicon_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SaleList extends AppCompatActivity {

    private RecyclerView mPostRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<SaleProduct> mDatas;
    SaleProductAdapter saleProductAdapter;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_list);

        mPostRecyclerView = findViewById(R.id.salelist_recy);
        swipeRefreshLayout=findViewById(R.id.refresh_board);


        updateDatas();

        if(saleProductAdapter != null) {
            saleProductAdapter.setOnItemClickListener(new SaleProductAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent intent = new Intent(getApplicationContext(), SaleProductPage.class);
                    intent.putExtra("select_data", mDatas.get(position));
                    startActivity(intent);
                }
            });
        }



    }


    public void updateDatas() {
        mDatas = new ArrayList<>();//
        mStore.collection("SaleProducts")//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    mDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                        SaleProduct product = snap.toObject(SaleProduct.class);
                                        mDatas.add(product);//여기까지가 게시글에 해당하는 데이터 적용
                                    }
                                } else {
                                }
                                saleProductAdapter = new SaleProductAdapter( mDatas);
                                mPostRecyclerView.setAdapter(saleProductAdapter);

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
}