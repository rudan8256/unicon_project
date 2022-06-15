package com.unicon.unicon_project.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unicon.unicon_project.Adapters.MultiImageAdapter;
import com.unicon.unicon_project.Authentic.SignInActivity;
import com.unicon.unicon_project.Authentic.SignUpActivity;
import com.unicon.unicon_project.ChattingActivity;
import com.unicon.unicon_project.Classes.User;
import com.unicon.unicon_project.ImageViewpager;
import com.unicon.unicon_project.ProgressDialog;
import com.unicon.unicon_project.R;
import com.unicon.unicon_project.Classes.SaleProduct;
import com.unicon.unicon_project.ViewpagerImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaleProductPage extends AppCompatActivity implements OnMapReadyCallback {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private GoogleMap mMap;
    SaleProduct select_data;
    private Button complete_btn;
    CardView btn_sale_chatting;
    private TextView home_address, deposit_price, month_price, live_period_start, live_period_end, title_page;
    private TextView maintenance_cost, room_size, specific, floor, structure, deposit_month;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LinearLayout deposit, month_rent, elec_cost, gas_cost, water_cost, internet_cost;
    private LinearLayout elec_boiler, gas_boiler, induction, aircon, washer, refrigerator, closet, gasrange, highlight;
    private LinearLayout convenience_store, subway, parking;

    private ArrayList<String> image_urllist;
    private ArrayList<Uri> uriList = new ArrayList<Uri>();
    MultiImageAdapter photoadapter;
    private RecyclerView photo_list;
    private Switch owner_switch;

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private ArrayList<Uri> images = new ArrayList<Uri>();
    private int curnumImage;
    int declare_time;
    private String str_declare;

    GestureDetector detector;
    ProgressDialog progressDialog;
    Dialog login_dialog, declare_dialog;

    String chattingUserID;

    private ArrayList<String> Likes= new ArrayList<>();
    private ImageView likeButton, back_acticity, declareButton;
    private boolean isLiked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_product_page);
        //Google Map ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Google Map ready done.

        progressDialog = new ProgressDialog(SaleProductPage.this);
        Intent intent = getIntent();
        select_data = (SaleProduct) intent.getSerializableExtra("select_data");

        photo_list = findViewById(R.id.photo_list);
        owner_switch = findViewById(R.id.owner_switch);

        progressDialog.show();
        Construter();
        WritingData();
        Image_Load();
        Dialog_Load();

        owner_switch.setClickable(false);

        if (select_data.getMaintains().get("owner_agree")) {
            owner_switch.setChecked(true);
            owner_switch.setSwitchTextAppearance(getApplicationContext(), R.style.SwitchTextAppearance_isclick);
        } else {
            owner_switch.setSwitchTextAppearance(getApplicationContext(), R.style.SwitchTextAppearance);
        }

        //뒤로가기
        back_acticity =findViewById(R.id.back_acticity);

        back_acticity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* 닉네임 가져오기 */
        mstore.collection("User").document(select_data.getWriterId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User myUser = document.toObject(User.class);
                        chattingUserID = myUser.getUsername();
                    }
                }
            }
        });


        likeButton = findViewById(R.id.like_button);
        if (mAuth.getCurrentUser() != null) {//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            mstore.collection("User").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {

                                User Userdata = task.getResult().toObject(User.class);


                                Likes = (ArrayList<String>) Userdata.getLikedProductID();



                                if (Likes != null) {
                                    isLiked = Likes.contains(select_data.getProductId());

                                    if (isLiked)
                                        likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                                    else
                                        likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                } else {
                                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                    isLiked = false;
                                }

                            }
                        }
                    });
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLiked) {
                    Likes.remove(select_data.getProductId());
                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);

                } else {
                    Likes.add(select_data.getProductId());
                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
                isLiked = !isLiked;

                mstore.collection("User").document(mAuth.getCurrentUser().getUid())
                        .update("likedProductID", Likes)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });

        //신고기능
        declareButton = findViewById(R.id.declare_button);
        mstore.collection("Declare").document(select_data.getProductId()+mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot =task.getResult();
                            if(documentSnapshot.exists()){
                                declareButton.setClickable(false);
                                declareButton.setImageResource(R.drawable.ic_declare_black);
                            }
                        }
                    }
                });
        Map<String, Object> obj = new HashMap<>();
        declare_dialog = new Dialog(this);
        declare_dialog.setContentView(R.layout.dialog_edit_declaration);
        declare_dialog.setCanceledOnTouchOutside(true);
        declare_dialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText) declare_dialog.findViewById(R.id.text_declare);
                str_declare = text.getText().toString();
                if(str_declare.equals("")){
                    Toast.makeText(SaleProductPage.this, "신고 사유를 적어주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    mstore.collection("User").whereEqualTo("usertoken", select_data.getWriterId()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    User userdata = document.toObject(User.class);
                                    declare_time=userdata.getDeclare_time();
                                    mstore.collection("User").document(select_data.getWriterId()).update("declare_time", declare_time+1);
                                }
                            }
                        });
                    obj.put("warning", str_declare);
                    mstore.collection("Declare").document(select_data.getProductId()+mAuth.getCurrentUser().getUid())
                            .set(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e("###", "declare");
                        }
                    });
                    declareButton.setImageResource(R.drawable.ic_declare_black);
                    declareButton.setClickable(false);
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
        declareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declare_dialog.show();
            }
        });

        btn_sale_chatting = findViewById(R.id.btn_sale_chatting);
        btn_sale_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                    intent.putExtra("chattingID", "");
                    intent.putExtra("productID", select_data.getProductId());
                    intent.putExtra("writerID", select_data.getWriterId());
                    intent.putExtra("homeAddress", select_data.getHome_adress());
                    intent.putExtra("chattingUserID", chattingUserID);
                    startActivity(intent);
                } else {
                    login_dialog.show();
                }
            }
        });


        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent motionEvent) {
                Log.d("####", "시작");
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
                // onDown < onShowPress < onLongPress
                // 중간 정도 시간동안 눌렀을 때 호출
                Log.d("####", "중간 누르기");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                // 터치가 끝났을 때 호출
                Log.d("####", "터치끝");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                Log.d("####", "스크롤");
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                Intent intent = new Intent(getApplicationContext(), ImageViewpager.class);
                intent.putExtra("uri", uriList);
                intent.putExtra("uri_Num", String.valueOf(curnumImage));
                startActivity(intent);
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float x, float y) {
                // 속도
                return false;
            }
        });

    }

    private void Dialog_Load() {
        //다이얼로그 생성
        login_dialog = new Dialog(this);
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
    }

    private void Image_Load() {
        image_urllist = select_data.getImages_url();


        for (String image_url : image_urllist) {
            Log.d("####", "image_url : " + image_url);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();

            Log.d("###", "최종 사진 주소 : " + "post_image/" + image_url + ".jpg");
            //StorageReference submitImage = storageReference.child("post_image/" + image_url + ".jpg");
            storageReference.child("post_image/" + image_url + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    uriList.add(uri);

                    if (uriList.size() == image_urllist.size()) {
                        Viewpager_start();
                        progressDialog.dismiss();
                    }

                    //  if(uriList.size() == image_urllist.size()) { ((MainActivity)MainActivity.maincontext).progressOFF(); }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 실패
                }
            });
        }
    }

    private void Viewpager_start() {
        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);


        images = uriList;
        curnumImage = 0;

        Log.e("&&&&", String.valueOf(images.size()));

        sliderViewPager.setOffscreenPageLimit(1);
        sliderViewPager.setAdapter(new ViewpagerImageAdapter(this, images));

        sliderViewPager.setCurrentItem(curnumImage);
        setCurrentIndicator(curnumImage);

        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

//        sliderViewPager.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                detector.onTouchEvent(motionEvent);
//
//                return false;
//            }
//        });


        setupIndicators(images.size());
    }




    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }

    private void Construter() {
        complete_btn = findViewById(R.id.complete_btn);
        home_address = findViewById(R.id.home_address);
        deposit_price = findViewById(R.id.deposit_price);
        month_price = findViewById(R.id.month_price);
        live_period_start = findViewById(R.id.live_period_start);
        live_period_end = findViewById(R.id.live_period_end);
        maintenance_cost = findViewById(R.id.maintenance_cost);
        room_size = findViewById(R.id.room_size);
        specific = findViewById(R.id.specific);

        deposit = findViewById(R.id.deposit);
        month_rent = findViewById(R.id.month_rent);
        elec_boiler = findViewById(R.id.elec_boiler);
        elec_cost = findViewById(R.id.elec_cost);
        gas_cost = findViewById(R.id.gas_cost);
        water_cost = findViewById(R.id.water_cost);
        internet_cost = findViewById(R.id.internet_cost);
        gas_boiler = findViewById(R.id.gas_boiler);
        induction = findViewById(R.id.induction);
        aircon = findViewById(R.id.aircon);
        washer = findViewById(R.id.washer);
        refrigerator = findViewById(R.id.refrigerator);
        closet = findViewById(R.id.closet);
        gasrange = findViewById(R.id.gasrange);
        highlight = findViewById(R.id.highlight);
        convenience_store = findViewById(R.id.convenience_store);
        subway = findViewById(R.id.subway);
        parking = findViewById(R.id.parking);

        floor = findViewById(R.id.floor);
        structure = findViewById(R.id.structure);

        title_page = findViewById(R.id.title_address);

        deposit_month = findViewById(R.id.deposit_month);
    }


    private void WritingData() {

        //데이터 부여
        home_address.setText(select_data.getHome_adress());

        if (select_data.getMonth_rent()) {
            deposit_month.setText("월세");
            month_price.setText("/" + select_data.getMonth_rent_price());
        } else {
            deposit_month.setText("전세");

        }

        deposit_price.setText(select_data.getDeposit_price());

        live_period_start.setText(select_data.getLive_period_start());
        live_period_end.setText(select_data.getLive_period_end());
        maintenance_cost.setText(select_data.getMaintenance_cost() + "만원");
        room_size.setText(select_data.getRoom_size() + "㎡");
        specific.setText(select_data.getSpecific());
        floor.setText(select_data.getFloor());
        structure.setText(select_data.getStructure());
        title_page.setText(select_data.getHome_name());


        if (select_data.getMaintains().get("elec_cost")) {
            elec_cost.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            elec_cost.setSelected(true);

        }

        if (select_data.getMaintains().get("gas_cost")) {
            gas_cost.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            gas_cost.setSelected(true);

        }
        if (select_data.getMaintains().get("water_cost")) {

            water_cost.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            water_cost.setSelected(true);
        }
        if (select_data.getMaintains().get("internet_cost")) {

            internet_cost.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            internet_cost.setSelected(true);
        }

        if (select_data.getOptions().get("gas_boiler")) {

            gas_boiler.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            gas_boiler.setSelected(true);
        }
        if (select_data.getOptions().get("induction")) {

            induction.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            induction.setSelected(true);
        }
        if (select_data.getOptions().get("aircon")) {

            aircon.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            aircon.setSelected(true);
        }
        if (select_data.getOptions().get("washer")) {

            washer.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            washer.setSelected(true);
        }
        if (select_data.getOptions().get("refrigerator")) {

            refrigerator.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            refrigerator.setSelected(true);
        }
        if (select_data.getOptions().get("closet")) {

            closet.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            closet.setSelected(true);
        }
        if (select_data.getOptions().get("gasrange")) {

            gasrange.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            gasrange.setSelected(true);
        }
        if (select_data.getOptions().get("highlight")) {

            highlight.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            highlight.setSelected(true);
        }
        if (select_data.getOptions().get("convenience_store")) {

            convenience_store.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            convenience_store.setSelected(true);
        }
        if (select_data.getOptions().get("subway")) {

            subway.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            subway.setSelected(true);
        }
        if (select_data.getOptions().get("parking")) {

            parking.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            parking.setSelected(true);
        }
        if (select_data.getOptions().get("elec_boiler")) {

            elec_boiler.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
            elec_boiler.setSelected(true);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(select_data.getHome_latlng(select_data.getLatlng_double()));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(select_data.getHome_latlng(select_data.getLatlng_double()),15));
    }
}