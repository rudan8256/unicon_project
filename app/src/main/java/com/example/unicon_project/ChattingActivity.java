package com.example.unicon_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.unicon_project.Adapters.ChattingAdapter;
import com.example.unicon_project.Classes.ChattingData;
import com.example.unicon_project.Classes.ChattingListData;
import com.example.unicon_project.Manager.ChattingManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChattingActivity extends AppCompatActivity {
    FirebaseDatabase database =FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference addDatabase;

    String roomInformation;
    Button btn_sendMsg;

    EditText et_myMsg;

    //GridView Adapter
    GridView gv;
    ChattingAdapter adapter;
    ArrayList<ChattingData> items = new ArrayList<>();

    ChattingManager chattingManager = new ChattingManager();

    String uid;
    String productID, writerID, homeAddress;
    String chattingID;

    boolean _isChattingListExist;
    Boolean Agree1, Agree2;

    final FirebaseUser user = mFirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        uid = user.getUid();

        Agree1 = Agree2 = false;

        /*
        추후에 방 uuid를 roomInformation에 저장합니다.
         */


        Intent it = getIntent();
        chattingID = it.getExtras().get("chattingID").toString();
        productID = it.getExtras().get("productID").toString();
        writerID = it.getExtras().get("writerID").toString();
        homeAddress = it.getExtras().get("homeAddress").toString();

        roomInformation = user.getUid();

        et_myMsg = findViewById(R.id.et_myMsg);
        btn_sendMsg = findViewById(R.id.btn_sendMsg);

        //chattingID 생성
        if(chattingID.equals(""))
            chattingID = chattingManager.generateChattingID(productID, uid);

        // 자신에게 해당 chattingID가 있는지 검사하고 추가하기
        isChattingListExist(uid, chattingID);
        // 상대방에게 해당 chattingID가 있는지 검사하고 추가하기
        isChattingListExist(writerID, chattingID);

        gv = findViewById(R.id.gv_chatting);
        btn_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 hh:mm");
                String millisInString = dateFormat.format(new Date());

                ChattingData chattingData = new ChattingData(
                        et_myMsg.getText().toString(),
                        millisInString,
                        user.getUid()
                );

                reference.child("chatting").child(chattingID).push().setValue(chattingData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //삽입 완료시
                        }
                    }

                });

                et_myMsg.setText("");
            }
        });

        //GridView에 정보 삽입
        getInfo();
    }

    public void isChattingListExist(String uid, String _chattingID)
    {
        // 해당 uid에 productID 채팅정보가 존재하는지 검사하여 그 값을 반환한다.
        _isChattingListExist = false;

        reference.child("chattingList").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    for (final DataSnapshot data : task.getResult().getChildren())
                    {
                        ChattingListData gds = data.getValue(ChattingListData.class);
                        Log.e("###", "result = "+gds.getProductID().equals(productID));
                        if(gds.getChattingID().equals(_chattingID))
                        {
                            //존재하는 경우 true 처리
                            _isChattingListExist = true;
                        }
                    }

                    if(!_isChattingListExist) {
                        ChattingListData data = new ChattingListData(homeAddress, uid, productID, _chattingID, 0);

                        reference.child("chattingList").child(uid).push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //삽입 완료시
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void getInfo(){
        reference.child("chatting").child(chattingID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (final DataSnapshot data : snapshot.getChildren()) {
                    ChattingData gds = data.getValue(ChattingData.class);
                    if(!gds.msg.equals("Agree"))
                    {
                        items.add(gds);
                    }else{
                        if(gds.uid.equals(user.getUid()))
                        {
                            Agree1 = true;
                        }else{
                            Agree2 = true;
                        }
                    }
                }

                adapter = new ChattingAdapter(items, uid, getApplicationContext());
                //데이터 업데이트 ( 중복 표시 방지 )
                adapter.notifyDataSetChanged();

                gv.scrollBy(items.size()-1, 0);

                gv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}