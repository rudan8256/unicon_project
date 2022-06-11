package com.unicon.unicon_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.unicon.unicon_project.Adapters.SaleProductAdapter;
import com.unicon.unicon_project.Classes.SaleProduct;
import com.unicon.unicon_project.Classes.SaleProductList;
import com.unicon.unicon_project.Classes.User;
import com.unicon.unicon_project.Pages.SaleList;
import com.unicon.unicon_project.Pages.SaleProductPage;

import java.util.ArrayList;
import java.util.List;

public class LikeListActivity extends AppCompatActivity implements SaleProductAdapter.OnItemClickListener {

    private List<SaleProduct> mDatas;
    SaleProductList saleProductList = SaleProductList.getInstance();
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<String> userLikes = new ArrayList<>();
    private SaleProductAdapter saleProductAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_list);

        recyclerView = findViewById(R.id.salelist_recy);
        saleProductAdapter = new SaleProductAdapter(null,getApplicationContext());
        saleProductAdapter.setOnItemClickListener(this);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (mAuth.getCurrentUser() != null) {//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            mStore.collection("User").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {

                                User Userdata = task.getResult().toObject(User.class);


                                userLikes =  Userdata.getLikedProductID();


                                updateDatas();

                            }
                        }
                    });
        }

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

                        SaleProduct curdata =  snap.toObject(SaleProduct.class);

                        if(userLikes.contains(curdata.getProductId())) {
                            mDatas.add(curdata);
                        }

                    }

                    if(mDatas.size()>0) {

                        Log.e("###", String.valueOf(mDatas.get(0).getProductId()));
                        saleProductAdapter.setmDatas(mDatas);
                        saleProductAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(saleProductAdapter);
                    }

                }
            }
        });

    }

    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(getApplicationContext(), SaleProductPage.class);
        intent.putExtra("select_data", mDatas.get(position));
        startActivity(intent);
    }

}