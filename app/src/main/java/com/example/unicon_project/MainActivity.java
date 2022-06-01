package com.example.unicon_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.unicon_project.Adapters.ChattingListAdapter;
import com.example.unicon_project.Adapters.SaleProductAdapter;
import com.example.unicon_project.Authentic.SessionCallBack;
import com.example.unicon_project.Authentic.SignInActivity;
import com.example.unicon_project.Classes.ChattingListData;
import com.example.unicon_project.Classes.RecommendCondition;
import com.example.unicon_project.Classes.SaleProduct;
import com.example.unicon_project.Pages.ChattingListActivity;
import com.example.unicon_project.Pages.MapTest;
import com.example.unicon_project.Pages.PurchaseList;
import com.example.unicon_project.Pages.PurchasePage;
import com.example.unicon_project.Pages.RecommendPage;
import com.example.unicon_project.Pages.SaleList;
import com.example.unicon_project.Pages.SalePage;
import com.example.unicon_project.Pages.SaleProductPage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout ToSalePage, ToPurchasePage, ToSaleList, ToPurchaseList;
    Button  ToMapTest, btn_log_out;
    ImageView  btn_toChatting;
    FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // *** [ 장준승 ]채팅기능 관계로 추가 ***
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database =FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    final FirebaseUser user = mFirebaseAuth.getInstance().getCurrentUser();
    String uid;
    // *********************************


    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private long clickTime = 0;
    private SessionCallBack mSessionCallback = new SessionCallBack();
    private GoogleSignInClient mGoogleSignInClient;
    Dialog login_dialog;

    private LinearLayout recommend_condition;
    private RecyclerView recommend_list;
    private List<SaleProduct> mDatas =new ArrayList<>();
    private List<Integer> dataScore = new ArrayList<>();
    SaleProductAdapter saleProductAdapter;
    private Dialog condition_dialog;
    RecommendCondition preUserdata;

    private EditText deposit_price_max, month_price_min,month_price_max,live_period_start,live_period_end;
    private EditText maintenance_cost, room_size_min,room_size_max;
    private String structure;
    private LinearLayout deposit,month_rent;
    public Map<String, Integer> structure_sel_map = new HashMap<>();
    private Spinner structureSpinner;

    private ImageView day_first, day_last;
    private DatePickerDialog datePickerDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToSalePage = findViewById(R.id.to_SalePage);
        ToPurchasePage = findViewById(R.id.to_PurchasePage);
        ToMapTest = findViewById(R.id.to_MapTest);
        ToSaleList = findViewById(R.id.to_SaleList);
        ToPurchaseList = findViewById(R.id.to_PurchaseList);
        btn_toChatting = findViewById(R.id.btn_toChatting);



        reccommend_start();

        ToSalePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SalePage.class);
                startActivity(intent);
            }
        });
        ToPurchasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchasePage.class);
                startActivity(intent);
            }
        });

        ToMapTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapTest.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up,R.anim.not_move);

            }
        });

        ToSaleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SaleList.class);
                startActivity(intent);
            }
        });
        ToPurchaseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchaseList.class);
                startActivity(intent);
            }
        });

        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail() // email addresses도 요청함
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        btn_log_out = findViewById(R.id.btn_log_out);
        btn_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Session.getCurrentSession().checkAndImplicitOpen()) {
                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            super.onSessionClosed(errorResult);
                            Log.e("###", "onSessionClosed: " + errorResult.getErrorMessage());

                        }

                        @Override
                        public void onCompleteLogout() {
                            if (mSessionCallback != null) {
                                Session.getCurrentSession().removeCallback(mSessionCallback);
                            }
                        }
                    });
                }
                if (firebaseAuth.getCurrentUser() != null) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.e("###", "user UID: " + user.getUid());
                        user.getIdToken(true)
                                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                                        if (task.isSuccessful()) {
                                            String idToken = task.getResult().getToken();
                                        }
                                    }
                                });
                    }
                    firebaseAuth.signOut();
                    mGoogleSignInClient.revokeAccess();

                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        });

        //다이얼로그 생성
        login_dialog= new Dialog(this);
        login_dialog.setContentView(R.layout.dialog_yologinpage);
        login_dialog.setCanceledOnTouchOutside(true);
        login_dialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });
        login_dialog.findViewById(R.id.dialog_canclebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_dialog.dismiss();
            }
        });




            btn_toChatting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(firebaseAuth.getCurrentUser() != null) {
                        Intent intent = new Intent(MainActivity.this, ChattingListActivity.class);
                        startActivity(intent);
                    }
                    else{
                        login_dialog.show();
                    }

                }
            });




    }

    private  void reccommend_start(){
        recommend_condition=findViewById(R.id.recommend_condition);
        recommend_list=findViewById(R.id.recommend_list);


        structure_sel_map.put("상관없음",0);
        structure_sel_map.put("오픈형 원룸",1);
        structure_sel_map.put("분리형 원룸(방1, 거실)",2);
        structure_sel_map.put("복층형 원룸",3);
        structure_sel_map.put("투룸",4);
        structure_sel_map.put("쓰리룸",5);

        Log.e("$$$$", String.valueOf(structure_sel_map.get("오픈형 원룸")));

        preUserdata = new RecommendCondition();


        condition_dialog= new Dialog(this);
        condition_dialog.setContentView(R.layout.dialog_reccondition);
        condition_dialog.setCanceledOnTouchOutside(true);

        Construter();
        searchInFB();


        recommend_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }



    // 뒤로가기 버튼 2번 누를 시에 앱 종료
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SystemClock.elapsedRealtime() - clickTime < 2000) {
                finish();
                overridePendingTransition(0, 0);
                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplication(), "한번 더 클릭하시면 앱을 종료합니다", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser() != null) {

            btn_toChatting.setBackground(getResources().getDrawable(R.drawable.ic_baseline_chat_bubble_24));

            uid = user.getUid();
            reference.child("chattingList").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (final DataSnapshot data : snapshot.getChildren()) {
                        ChattingListData gds = data.getValue(ChattingListData.class);
                        if (gds.getUnread() != 0)
                            btn_toChatting.setBackground(getResources().getDrawable(R.drawable.ic_baseline_mark_chat_unread_24));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void searchInFB(){


        mstore.collection("Usercondition")
                .whereEqualTo("writerId",firebaseAuth.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            preUserdata =document.toObject(RecommendCondition.class);
                            Log.e("@@@@@@","들어옴?");

                            deposit_price_max.setText(preUserdata.getMonth_rentprice_max());
                            month_price_max.setText(preUserdata.getMonth_rentprice_max());
                            month_price_min.setText(preUserdata.getMonth_rentprice_min());
                            live_period_start.setText(preUserdata.getLive_period_start());
                            live_period_end.setText(preUserdata.getLive_period_end());

                            maintenance_cost.setText(preUserdata.getMaintenance_cost());
                            structure = preUserdata.getStructure();
                            int cur_seldata_num;


                            if (structure != "") {

                                cur_seldata_num = structure_sel_map.get(structure);


                                structureSpinner.setSelection(cur_seldata_num);
                            }

                            room_size_max.setText(preUserdata.getRoom_size_max());
                            room_size_min.setText(preUserdata.getRoom_size_min());


                            if (preUserdata.getDeposit()) {
                                deposit.setBackgroundColor(Color.BLUE);
                            } else {
                                deposit.setBackgroundColor(Color.WHITE);
                            }


                            if (preUserdata.getMonth_rent()) {
                                month_rent.setBackgroundColor(Color.BLUE);
                            } else {
                                month_rent.setBackgroundColor(Color.WHITE);
                            }


                            updateDatas();
                        }
                    }
                });

    }

    public void updateDatas() {
        mDatas.clear();//
        dataScore.clear();
        mstore.collection("SaleProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task!=null){
                    mDatas.clear();
                    Log.e("###","쿼리개수 : "+task.getResult().getDocuments().size());
                    for(DocumentSnapshot snap : task.getResult().getDocuments()){

                        SaleProduct curdata = snap.toObject(SaleProduct.class);


                        int cur_score= Judge(curdata);
                        if(cur_score > 1000) {



                            Log.e("$$$$", String.valueOf(cur_score));


                            mDatas.add(curdata);
                            dataScore.add(cur_score);
                        }

                    }
                }

                for(int i=0;i<mDatas.size()-1;i++){
                    for(int j=0;j<mDatas.size()-1;j++){
                        if(dataScore.get(j)>dataScore.get(j+1))
                            swap(j,j+1);
                    }
                }

                saleProductAdapter = new SaleProductAdapter( mDatas, getApplicationContext());
                recommend_list.setAdapter(saleProductAdapter);
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

    private void swap(int v,int e){
        SaleProduct temp;
        temp=mDatas.get(v);
        mDatas.set(v,mDatas.get(e));
        mDatas.set(e,temp);

        int score_temp;
        score_temp=dataScore.get(v);
        dataScore.set(v,dataScore.get(e));
        dataScore.set(e,score_temp);
    }

    private int Judge(SaleProduct curdata){

        int total=0;

        String cur_data_start= curdata.getLive_period_start().replace("/","");
        String cur_data_end= curdata.getLive_period_end().replace("/","");
        String pre_data_start = preUserdata.getLive_period_start().replace("/","");
        String pre_data_end =preUserdata.getLive_period_end().replace("/","");



        //완변 조건매물 토탈 4000

        if(  !(preUserdata.getDeposit() == curdata.getDeposit() || preUserdata.getMonth_rent() == curdata.getMonth_rent())){
            return -1;
        }
        else if(Integer.parseInt(pre_data_end) <Integer.parseInt(cur_data_start) || Integer.parseInt(cur_data_end) < Integer.parseInt(pre_data_start) ){
            return  -1;
        }
        else if( ! preUserdata.getStructure().equals(curdata.getStructure()) && ! preUserdata.getStructure().equals("상관없음")){
            return  -1;
        }
        else{

            if( Integer.parseInt(preUserdata.getDeposit_price_max()) >= Integer.parseInt(curdata.getDeposit_price())){
                total += 1000;
            }

            if( Integer.parseInt(preUserdata.getMonth_rentprice_max()) >= Integer.parseInt(curdata.getMonth_rent_price()) &&
                    Integer.parseInt(preUserdata.getMonth_rentprice_min()) <= Integer.parseInt(curdata.getMonth_rent_price()) ){
                total += 1000;

            }
            else if (Integer.parseInt(preUserdata.getMonth_rentprice_max()) < Integer.parseInt(curdata.getMonth_rent_price())){
                if(Integer.parseInt(curdata.getMonth_rent_price()) - Integer.parseInt(preUserdata.getMonth_rentprice_max()) <=5){
                    total += 500 - 100*(Integer.parseInt(curdata.getMonth_rent_price()) - Integer.parseInt(preUserdata.getMonth_rentprice_max()));

                }
                else{
                    return -1;
                }

            }
            else{
                if( Integer.parseInt(preUserdata.getMonth_rentprice_min()) - Integer.parseInt(curdata.getMonth_rent_price()) <=5) {
                    total += 500 - 100 * (Integer.parseInt(preUserdata.getMonth_rentprice_min()) - Integer.parseInt(curdata.getMonth_rent_price()));
                }
                else{
                    return -1;
                }

            }

            if(Integer.parseInt(preUserdata.getMaintenance_cost()) >= Integer.parseInt(curdata.getMaintenance_cost())){
                total += 1000;
            }

            if( Integer.parseInt(preUserdata.getRoom_size_min()) <= Integer.parseInt(curdata.getRoom_size()) &&
                    Integer.parseInt(preUserdata.getRoom_size_max()) >= Integer.parseInt(curdata.getRoom_size())
            ){
                total += 1000;
            }



            return total;
        }



    }

    private void showDialog() {


        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        if(month<10 && day <10){
                            live_period_start.setText( year+"/0"+month+"/0"+day);
                        }
                        else if(month<10 ){
                            live_period_start.setText( year+"/0"+month+"/"+day);
                        }
                        else if(day<10 ){
                            live_period_start.setText( year+"/"+month+"/0"+day);
                        }
                        else {
                            live_period_start.setText(year + "/" + month + "/" + day);
                        }
                    }
                }, mYear, mMonth, mDay);

        DatePickerDialog datePickerDialog1 = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        if(month<10 && day <10){
                            live_period_end.setText( year+"/0"+month+"/0"+day);
                        }
                        else if(month<10 ){
                            live_period_end.setText( year+"/0"+month+"/"+day);
                        }
                        else if(day<10 ){
                            live_period_end.setText( year+"/"+month+"/0"+day);
                        }
                        else {
                            live_period_end.setText(year + "/" + month + "/" + day);
                        }
                    }
                }, mYear, mMonth, mDay);

        day_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.show();
            }
        });

        day_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                datePickerDialog1.show();
            }
        });


        set_Clicklistner();


        condition_dialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                preUserdata.setMonth_rentprice_max(month_price_max.getText().toString());
                preUserdata.setMonth_rentprice_min(month_price_min.getText().toString());
                preUserdata.setDeposit_price_max(deposit_price_max.getText().toString());

                preUserdata.setLive_period_start(live_period_start.getText().toString());
                preUserdata.setLive_period_end(live_period_end.getText().toString());
                preUserdata.setMaintenance_cost(maintenance_cost.getText().toString());
                preUserdata.setRoom_size_max(room_size_max.getText().toString());
                preUserdata.setRoom_size_min(room_size_min.getText().toString());

                preUserdata.setStructure(structure);
                preUserdata.setWriterId(firebaseAuth.getUid());



                mstore.collection("Usercondition").document(firebaseAuth.getUid())
                        .set(preUserdata)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("*****","업로드 성공");
                                condition_dialog.dismiss();
                            }
                        });


            }
        });
        condition_dialog.show();
    }


    @Override
    public void onClick(View view) {
        switch ( view.getId()){

            case R.id.deposit:
                if( !preUserdata.getDeposit() ){
                    preUserdata.setDeposit(true); deposit.setBackgroundColor(Color.BLUE); }
                else{
                    preUserdata.setDeposit(false);deposit.setBackgroundColor(Color.WHITE); }
                break;
            case R.id.month_rent:
                if( !preUserdata.getMonth_rent() ){
                    preUserdata.setMonth_rent(true);month_rent.setBackgroundColor(Color.BLUE); }
                else{
                    preUserdata.setMonth_rent(false);month_rent.setBackgroundColor(Color.WHITE); }
                break;

        }
    }

    private void Construter(){


        deposit = condition_dialog.findViewById(R.id.deposit);
        month_rent = condition_dialog.findViewById(R.id.month_rent);
        deposit_price_max = condition_dialog.findViewById(R.id.deposit_price_max);
        month_price_min= condition_dialog.findViewById(R.id.month_price_min);
        month_price_max = condition_dialog.findViewById(R.id.month_price_max);

        live_period_start = condition_dialog.findViewById(R.id.live_period_start);
        live_period_end = condition_dialog.findViewById(R.id.live_period_end);

        maintenance_cost = condition_dialog.findViewById(R.id.maintenance_cost);

        room_size_min = condition_dialog.findViewById(R.id.room_size_min);
        room_size_max = condition_dialog.findViewById(R.id.room_size_max);

        day_first = condition_dialog.findViewById(R.id.day_first);
        day_last = condition_dialog.findViewById(R.id.day_last);




        structureSpinner = condition_dialog.findViewById(R.id.structure);


        structureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                structure = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    private void set_Clicklistner(){

        deposit.setOnClickListener(this);
        month_rent.setOnClickListener(this);

    }
}