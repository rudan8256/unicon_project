package com.unicon.unicon_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChattingActivity extends AppCompatActivity {
    FirebaseDatabase database =FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference addDatabase;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    ImageView iv_chatting_back;
    TextView tv_chatting_nickname;

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

    //??? ???????????? ???????????? UserID??? ??????
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
        ????????? ??? uuid??? roomInformation??? ???????????????.
         */

        Intent it = getIntent();
        chattingID = it.getExtras().get("chattingID").toString();
        productID = it.getExtras().get("productID").toString();
        writerID = it.getExtras().get("writerID").toString();
        homeAddress = it.getExtras().get("homeAddress").toString();
        if(it.getExtras().get("chattingUserID") != null)
            chattingUserID = it.getExtras().get("chattingUserID").toString();
        else
            chattingUserID = "anonymous";

        roomInformation = user.getUid();

        et_myMsg = findViewById(R.id.et_myMsg);
        btn_sendMsg = findViewById(R.id.btn_sendMsg);

        tv_chatting_nickname = findViewById(R.id.tv_chatting_nickname);
        tv_chatting_nickname.setText(chattingUserID+"????????? ??????");

        iv_chatting_back = findViewById(R.id.iv_chatting_back);
        iv_chatting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChattingActivity.this, ChattingListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /* ????????? ?????? */
        mstore.collection("User").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        User myUser = document.toObject(User.class);
                        myUserID = myUser.getUsername();
                        // ??????????????? ?????? chattingID??? ????????? ???????????? ????????????
                        isChattingListExist(writerID, chattingID, myUserID);
                    }
                }
            }
        });
        mstore.collection("User").document(writerID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        User myUser = document.toObject(User.class);
                        writerUserID = myUser.getUsername();
                        // ???????????? ?????? chattingID??? ????????? ???????????? ????????????
                        isChattingListExist(uid, chattingID, writerUserID);
                    }
                }
            }
        });

        //chattingID ??????
        if(chattingID.equals(""))
            chattingID = chattingManager.generateChattingID(productID, uid);

        //?????? ??????????????? unread 0?????? ?????????
        reference.child("chattingList").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    ChattingListData chattingListData = new ChattingListData();
                    for (final DataSnapshot data : task.getResult().getChildren()) {
                        ChattingListData cld = data.getValue(ChattingListData.class);

                        //chattingID??? ????????? ???????????? ????????????
                        if(cld.getChattingID().equals(chattingID))
                            chattingListData = cld;
                    }

                    chattingListData.setUnread(0);

                    if(chattingListData.getChattingID().length() > 0)
                    {
                        reference.child("chattingList").child(uid).child(chattingID).setValue(chattingListData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //?????? ?????????
                                }
                            }
                        });
                    }
                }
            }});


        gv = findViewById(R.id.gv_chatting);
        btn_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM??? dd??? hh:mm");
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
                            //?????? ?????????
                        }
                    }
                });

                //????????? ?????? = there ????????????
                if(uid == writerID)
                    // ?????? ????????? ????????? ???????????? ??????
                    there = chattingID.replaceAll(productID, "");
                else
                    // ?????? ????????? ????????? ???????????? ??????
                    there = writerID;

                // unread??? ????????? ???????????? 1 ???????????????
                reference.child("chattingList").child(there).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            ChattingListData chattingListData = new ChattingListData();
                            for (final DataSnapshot data : task.getResult().getChildren()) {
                                ChattingListData cld = data.getValue(ChattingListData.class);

                                //?????? ???????????? ???????????? ???????????? ????????????.
                                if(cld.getUserName().equals(uid))
                                    chattingListData = cld;
                            }

                            chattingListData.setUnread(chattingListData.getUnread()+1);

                            reference.child("chattingList").child(there).child(chattingID).setValue(chattingListData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //?????? ?????????
                                    }
                                }
                            });
                        }
                    }});

                et_myMsg.setText("");
            }
        });







        //GridView??? ?????? ??????
        getInfo();
    }

    public void isChattingListExist(String _uid, String _chattingID, String _userID)
    {
        // ?????? uid??? productID ??????????????? ??????????????? ???????????? ??? ?????? ????????????.
        // input ( _uid, _chattingID )
        _isChattingListExist = false;

        reference.child("chattingList").child(_uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                            //???????????? ?????? true ??????
                            _isChattingListExist = true;
                        }
                    }

                    if(!_isChattingListExist) {
                        ChattingListData data = new ChattingListData(homeAddress, _uid, productID, _chattingID, "", 0);
                        if(_uid == writerID)
                            // ?????? ????????? ????????? ???????????? ??????
                            data.setUserName(_chattingID.replaceAll(productID, ""));
                        else
                            // ?????? ????????? ????????? ???????????? ??????
                            data.setUserName(writerID);
                        data.setUserID(_userID);

                        reference.child("chattingList").child(_uid).child(_chattingID).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //?????? ?????????
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
                //????????? ???????????? ( ?????? ?????? ?????? )
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