package com.unicon.unicon_project.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.unicon.unicon_project.Adapters.ChattingListAdapter;
import com.unicon.unicon_project.ChattingActivity;
import com.unicon.unicon_project.Classes.ChattingListData;
import com.unicon.unicon_project.MainActivity;
import com.unicon.unicon_project.ProgressDialog;
import com.unicon.unicon_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChattingListActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    GridView gv;
    ChattingListAdapter adapter;
    ArrayList<ChattingListData> items = new ArrayList<>();

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();

    ImageView iv_chattinglist_back;

    FirebaseDatabase database =FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    final FirebaseUser user = mFirebaseAuth.getInstance().getCurrentUser();
    String uid;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_list);

        uid = user.getUid();
        progressDialog= new ProgressDialog(ChattingListActivity.this);

        gv = findViewById(R.id.gv_chattingList);

        iv_chattinglist_back = findViewById(R.id.iv_chattinglist_back);
        iv_chattinglist_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChattingListActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        progressDialog.show();

        reference.child("chattingList").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                items.clear();
                for (final DataSnapshot data : snapshot.getChildren()) {
                    ChattingListData gds = data.getValue(ChattingListData.class);
                    items.add(gds);
                }

                adapter = new ChattingListAdapter(items, uid, getApplicationContext());
                //데이터 업데이트 ( 중복 표시 방지 )
                adapter.notifyDataSetChanged();

                gv.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("chattingID", items.get(i).getChattingID());
                intent.putExtra("productID", items.get(i).getProductID());
                intent.putExtra("writerID", items.get(i).getUserName());
                intent.putExtra("homeAddress", items.get(i).getChattingName());
                intent.putExtra("chattingUserID", items.get(i).getUserID());
                startActivity(intent);
            }
        });
    }


}