package com.unicon.unicon_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unicon.unicon_project.Adapters.ChattingAdapter;
import com.unicon.unicon_project.Classes.ChattingData;
import com.unicon.unicon_project.Classes.ChattingListData;
import com.unicon.unicon_project.Classes.User;
import com.unicon.unicon_project.Manager.ChattingManager;
import com.unicon.unicon_project.Pages.ChattingListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unicon.unicon_project.Pages.SaleProductPage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChattingActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference addDatabase;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    ImageView iv_chatting_back, user_filter;
    TextView tv_chatting_nickname;

    Dialog more_dialog, declare_dialog;

    String roomInformation;
    Button btn_sendMsg;

    EditText et_myMsg;

    //GridView Adapter
    GridView gv;
    ChattingAdapter adapter;
    ArrayList<ChattingData> items = new ArrayList<>();

    ChattingManager chattingManager = new ChattingManager();

    String uid;
    String productID, writerID, homeAddress, chattingUserID;
    String chattingID;
    String there;
    String str_declare;
    int declare_time;

    //내 닉네임과 상대방의 UserID를 저장
    String myUserID, writerUserID;

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
        if (it.getExtras().get("chattingUserID") != null)
            chattingUserID = it.getExtras().get("chattingUserID").toString();
        else
            chattingUserID = "anonymous";

        roomInformation = user.getUid();

        et_myMsg = findViewById(R.id.et_myMsg);
        btn_sendMsg = findViewById(R.id.btn_sendMsg);

        tv_chatting_nickname = findViewById(R.id.tv_chatting_nickname);
        tv_chatting_nickname.setText(chattingUserID + "님과의 채팅");

        iv_chatting_back = findViewById(R.id.iv_chatting_back);
        iv_chatting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChattingActivity.this, ChattingListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /* 닉네임 설정 */
        mstore.collection("User").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User myUser = document.toObject(User.class);
                        myUserID = myUser.getUsername();
                        // 상대방에게 해당 chattingID가 있는지 검사하고 추가하기
                        isChattingListExist(writerID, chattingID, myUserID);
                    }
                }
            }
        });
        mstore.collection("User").document(writerID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User myUser = document.toObject(User.class);
                        writerUserID = myUser.getUsername();
                        // 자신에게 해당 chattingID가 있는지 검사하고 추가하기
                        isChattingListExist(uid, chattingID, writerUserID);
                    }
                }
            }
        });

        //chattingID 생성
        if (chattingID.equals(""))
            chattingID = chattingManager.generateChattingID(productID, uid);

        //처음 들어왔을시 unread 0으로 초기화
        reference.child("chattingList").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    ChattingListData chattingListData = new ChattingListData();
                    for (final DataSnapshot data : task.getResult().getChildren()) {
                        ChattingListData cld = data.getValue(ChattingListData.class);

                        //chattingID가 똑같은 채팅방을 가져온다
                        if (cld.getChattingID().equals(chattingID))
                            chattingListData = cld;
                    }

                    chattingListData.setUnread(0);

                    if (chattingListData.getChattingID().length() > 0) {
                        reference.child("chattingList").child(uid).child(chattingID).setValue(chattingListData).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        if (task.isSuccessful()) {
                            //삽입 완료시
                        }
                    }
                });

                //상대방 정보 = there 가져오기
                if (uid == writerID)
                    // 현재 들어온 사람이 작성자인 경우
                    there = chattingID.replaceAll(productID, "");
                else
                    // 현재 들어온 사람이 구매자인 경우
                    there = writerID;

                // unread의 정보를 받아와서 1 증가시키기
                reference.child("chattingList").child(there).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            ChattingListData chattingListData = new ChattingListData();
                            for (final DataSnapshot data : task.getResult().getChildren()) {
                                ChattingListData cld = data.getValue(ChattingListData.class);

                                //내가 대상으로 되어있는 채팅방을 가져온다.
                                if (cld.getUserName().equals(uid))
                                    chattingListData = cld;
                            }

                            chattingListData.setUnread(chattingListData.getUnread() + 1);

                            reference.child("chattingList").child(there).child(chattingID).setValue(chattingListData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //삽입 완료시
                                    }
                                }
                            });
                        }
                    }
                });

                et_myMsg.setText("");
            }
        });

        //사용자 차단, 신고기능
        Map<String, Object> obj = new HashMap<>();
        declare_dialog=new Dialog(this);
        declare_dialog.setContentView(R.layout.dialog_edit_declaration);
        declare_dialog.setCanceledOnTouchOutside(true);
        declare_dialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText)declare_dialog.findViewById(R.id.text_declare);
                str_declare= text.getText().toString();
                if(str_declare.equals("")){
                    Toast.makeText(ChattingActivity.this, "신고 사유를 적어주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    mstore.collection("User").whereEqualTo("usertoken", writerID).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        User userdata = document.toObject(User.class);
                                        declare_time=userdata.getDeclare_time();
                                        mstore.collection("User").document(writerID).update("declare_time", declare_time+1);
                                    }
                                }
                            });
                    obj.put("warning", str_declare);
                    mstore.collection("Declare").document(writerID+chattingID)
                            .set(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e("###", "declare");
                        }
                    });
                    declare_dialog.dismiss();
                }
            }
        });
        declare_dialog.findViewById(R.id.dialog_cancelbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declare_dialog.dismiss();
            }
        });
        user_filter = findViewById(R.id.filter_user);
        more_dialog=new Dialog(this);
        more_dialog.setContentView(R.layout.dialog_more);
        more_dialog.setCanceledOnTouchOutside(true);
        more_dialog.findViewById(R.id.btn_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        more_dialog.findViewById(R.id.btn_declare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more_dialog.dismiss();
                declare_dialog.show();
            }
        });
        user_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more_dialog.show();
            }
        });


        //GridView에 정보 삽입
        getInfo();
    }

    public void isChattingListExist(String _uid, String _chattingID, String _userID) {
        // 해당 uid에 productID 채팅정보가 존재하는지 검사하여 그 값을 반환한다.
        // input ( _uid, _chattingID )
        _isChattingListExist = false;

        reference.child("chattingList").child(_uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (final DataSnapshot data : task.getResult().getChildren()) {
                        ChattingListData gds = data.getValue(ChattingListData.class);
                        Log.e("###", "result = " + gds.getProductID().equals(productID));
                        if (gds.getChattingID().equals(_chattingID)) {
                            //존재하는 경우 true 처리
                            _isChattingListExist = true;
                        }
                    }

                    if (!_isChattingListExist) {
                        ChattingListData data = new ChattingListData(homeAddress, _uid, productID, _chattingID, "", 0);
                        if (_uid == writerID)
                            // 현재 들어온 사람이 작성자인 경우
                            data.setUserName(_chattingID.replaceAll(productID, ""));
                        else
                            // 현재 들어온 사람이 구매자인 경우
                            data.setUserName(writerID);
                        data.setUserID(_userID);

                        reference.child("chattingList").child(_uid).child(_chattingID).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void getInfo() {
        reference.child("chatting").child(chattingID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (final DataSnapshot data : snapshot.getChildren()) {
                    ChattingData gds = data.getValue(ChattingData.class);
                    if (!gds.msg.equals("Agree")) {
                        items.add(gds);
                    } else {
                        if (gds.uid.equals(user.getUid())) {
                            Agree1 = true;
                        } else {
                            Agree2 = true;
                        }
                    }
                }

                adapter = new ChattingAdapter(items, uid, getApplicationContext());
                //데이터 업데이트 ( 중복 표시 방지 )
                adapter.notifyDataSetChanged();

                gv.setSelection(adapter.getCount() - 1);
                gv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}