package com.unicon.unicon_project.Manager;

/*
    [ ChattingManager ]
        - 각 유저의 uid를 통해서 가지고 있는 채팅방 리스트를 추가/삭제/조회합니다.

 */

import android.util.Log;

import androidx.annotation.NonNull;

import com.unicon.unicon_project.Classes.ChattingListData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChattingManager {
    FirebaseDatabase database =FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference addDatabase;
    ArrayList<ChattingListData> items = new ArrayList<>();
    boolean _isChattingListExist;

    public String generateChattingID(String productID, String uid)
    {
        return productID + uid;
    }

    public boolean isChattingListExist(String uid, String productID)
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
                        if(gds.getProductID().equals(productID))
                        {
                            //존재하는 경우 true 처리
                            _isChattingListExist = true;
                            return;
                        }
                    }
                }
            }
        });

        return _isChattingListExist;
    }


    public ArrayList<ChattingListData> getChattingListData(String uid){
        reference.child("chattingList").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                items.clear();
                for (final DataSnapshot data : snapshot.getChildren()) {
                    ChattingListData gds = data.getValue(ChattingListData.class);
                    items.add(gds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return items;
    }

    public void pushChattingListData(ChattingListData data, String uid){
        reference.child("chattingList").child(uid).push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //삽입 완료시
                }
            }
        });
    }
}
