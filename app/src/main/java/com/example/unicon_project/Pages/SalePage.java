package com.example.unicon_project.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unicon_project.Adapters.MultiImageAdapter;
import com.example.unicon_project.Classes.Image_zoom;
import com.example.unicon_project.R;
import com.example.unicon_project.Classes.SaleProduct;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalePage extends AppCompatActivity implements View.OnClickListener {


    private static final int FROM_GALLERY = 200;
    private static final int FROM_ADDRESS = 100;
    private Button complete_btn;
    private SaleProduct newproduct;
    private EditText home_address, deposit_price, month_price, live_period_start, live_period_end;
    private EditText maintenance_cost, room_size, specific;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String curdate, floor, structure;
    private LinearLayout deposit, month_rent, elec_cost, gas_cost, water_cost, internet_cost;
    private LinearLayout elec_boiler, gas_boiler, induction, aircon, washer, refrigerator, closet, gasrange, highlight;
    private LinearLayout convenience_store, subway, parking;
    private Map<String, Boolean> maintains, options;
    private Map<String, String> personal_proposal;
    private Switch owner_switch;
    private Spinner floorSpinner, structureSpinner;

    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    private ArrayList<String> image_urllist = new ArrayList<>();
    private ArrayList<Uri> uriList = new ArrayList<>();
    MultiImageAdapter photoadapter;
    private RecyclerView photo_list;
    private TextView post_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_page);
        Places.initialize(getApplicationContext(), "AIzaSyBslpmgHhMBvhT2ZrhV7tX4kmT_3jDrPAA", Locale.KOREAN);

        //새로운 판매글 클래스 생성
        newproduct = new SaleProduct();
        maintains = newproduct.getMaintains();
        options = newproduct.getOptions();
        personal_proposal = newproduct.getPersonal_proposal();
        post_gallery = findViewById(R.id.post_gallery);
        photo_list = findViewById(R.id.photo_list);
        owner_switch = findViewById(R.id.owner_switch);

        floorSpinner = findViewById(R.id.floor);
        structureSpinner = findViewById(R.id.structure);

        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                floor = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        structureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                structure = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        owner_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    maintains.put("owner_agree", true);
                } else {
                    maintains.put("owner_agree", false);
                }
            }
        });


        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://uniconproject-2be63.appspot.com/");

        Construter();
        set_Clicklistner();


        //카메라 권한 요구
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다")
                .setDeniedMessage("거부하셨습니다")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        //사진 이동
        post_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useGallery();
            }
        });


        home_address.setFocusable(false);
        home_address.setOnClickListener(this);

        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if ( !deposit_price.equals("") ||  !month_price.equals("") || !home_address.equals("")|| !maintenance_cost.equals("")||
               ! room_size.equals("")|| !floor.equals("")|| !structure.equals("") ){

                    Toast.makeText(getApplicationContext(), "필수정보를 입력하세요", Toast.LENGTH_SHORT).show();

                   return;
                }
                // Create a new user with a first and last name
                newproduct.setHome_adress(home_address.getText().toString());
                newproduct.setMonth_rent_price(month_price.getText().toString());
                newproduct.setDeposit_price(deposit_price.getText().toString());
                newproduct.setLive_period_start(live_period_start.getText().toString());
                newproduct.setLive_period_end(live_period_end.getText().toString());
                newproduct.setMaintenance_cost(maintenance_cost.getText().toString());
                newproduct.setRoom_size(room_size.getText().toString());
                newproduct.setSpecific(specific.getText().toString());
                newproduct.setWriterId(mAuth.getUid());
                newproduct.setFloor(floor);
                newproduct.setStructure(structure);

                curdate = String.valueOf(System.currentTimeMillis());
                newproduct.setProductId(curdate + mAuth.getUid());


                UploadPhoto(uriList, 0);

                mstore.collection("SaleProducts").document(curdate + mAuth.getUid())
                        .set(newproduct)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("***", "업로드 성공");
                                finish();
                            }
                        });


            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_address:
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(SalePage.this);
                startActivityForResult(intent, 100);
                break;
            case R.id.deposit:
                if (!newproduct.getDeposit()) {
                    newproduct.setDeposit(true);
                    deposit.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    deposit.setSelected(true);
                } else {
                    newproduct.setDeposit(false);
                    deposit.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    deposit.setSelected(false);
                }
                break;
            case R.id.month_rent:
                if (!newproduct.getMonth_rent()) {
                    newproduct.setMonth_rent(true);
                    month_rent.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    month_rent.setSelected(true);
                } else {
                    newproduct.setMonth_rent(false);
                    month_rent.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    month_rent.setSelected(false);
                }
                break;
            case R.id.elec_cost:
                if (!maintains.get("elec_cost")) {
                    maintains.put("elec_cost", true);
                    elec_cost.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    elec_cost.setSelected(true);
                } else {
                    maintains.put("elec_cost", false);
                    elec_cost.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    elec_cost.setSelected(false);
                }
                break;
            case R.id.gas_cost:
                if (!maintains.get("gas_cost")) {
                    maintains.put("gas_cost", true);
                    gas_cost.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    gas_cost.setSelected(true);
                } else {
                    maintains.put("gas_cost", false);
                    gas_cost.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    gas_cost.setSelected(false);
                }
                break;
            case R.id.water_cost:
                if (!maintains.get("water_cost")) {
                    maintains.put("water_cost", true);
                    water_cost.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    water_cost.setSelected(true);
                } else {
                    maintains.put("water_cost", false);
                    water_cost.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    water_cost.setSelected(false);
                }
                break;
            case R.id.internet_cost:
                if (!maintains.get("internet_cost")) {
                    maintains.put("internet_cost", true);
                    internet_cost.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    internet_cost.setSelected(true);
                } else {
                    maintains.put("internet_cost", false);
                    internet_cost.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    internet_cost.setSelected(false);
                }
                break;
            case R.id.elec_boiler:
                if (!options.get("elec_boiler")) {
                    options.put("elec_boiler", true);
                    elec_boiler.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    elec_boiler.setSelected(true);
                } else {
                    options.put("elec_boiler", false);
                    elec_boiler.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    elec_boiler.setSelected(false);
                }
                break;

            case R.id.gas_boiler:
                if (!options.get("gas_boiler")) {
                    options.put("gas_boiler", true);
                    gas_boiler.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    gas_boiler.setSelected(true);
                } else {
                    options.put("gas_boiler", false);
                    gas_boiler.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    gas_boiler.setSelected(false);
                }
                break;

            case R.id.induction:
                if (!options.get("induction")) {
                    options.put("induction", true);
                    induction.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    induction.setSelected(true);
                } else {
                    options.put("induction", false);
                    induction.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    induction.setSelected(false);
                }
                break;
            case R.id.aircon:
                if (!options.get("aircon")) {
                    options.put("aircon", true);
                    aircon.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    aircon.setSelected(true);
                } else {
                    options.put("aircon", false);
                    aircon.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    aircon.setSelected(false);
                }
                break;

            case R.id.washer:
                if (!options.get("washer")) {
                    options.put("washer", true);
                    washer.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    washer.setSelected(true);
                } else {
                    options.put("washer", false);
                    washer.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    washer.setSelected(false);
                }
                break;
            case R.id.refrigerator:
                if (!options.get("refrigerator")) {
                    options.put("refrigerator", true);
                    refrigerator.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    refrigerator.setSelected(true);
                } else {
                    options.put("refrigerator", false);
                    refrigerator.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    refrigerator.setSelected(false);
                }
                break;
            case R.id.closet:
                if (!options.get("closet")) {
                    options.put("closet", true);
                    closet.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    closet.setSelected(true);
                } else {
                    options.put("closet", false);
                    closet.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    closet.setSelected(false);
                }
                break;
            case R.id.gasrange:
                if (!options.get("gasrange")) {
                    options.put("gasrange", true);
                    gasrange.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    gasrange.setSelected(true);
                } else {
                    options.put("gasrange", false);
                    gasrange.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    gasrange.setSelected(false);
                }
                break;
            case R.id.highlight:
                if (!options.get("highlight")) {
                    options.put("highlight", true);
                    highlight.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    highlight.setSelected(true);
                } else {
                    options.put("highlight", false);
                    highlight.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    highlight.setSelected(false);
                }
                break;
            case R.id.convenience_store:
                if (!options.get("convenience_store")) {
                    options.put("convenience_store", true);
                    convenience_store.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    convenience_store.setSelected(true);
                } else {
                    options.put("convenience_store", false);
                    convenience_store.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    convenience_store.setSelected(false);
                }
                break;
            case R.id.subway:
                if (!options.get("subway")) {
                    options.put("subway", true);
                    subway.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    subway.setSelected(true);
                } else {
                    options.put("subway", false);
                    subway.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    subway.setSelected(false);
                }
                break;
            case R.id.parking:
                if (!options.get("parking")) {
                    options.put("parking", true);
                    parking.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    parking.setSelected(true);
                } else {
                    options.put("parking", false);
                    parking.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    parking.setSelected(false);
                }
                break;


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

    }

    private void set_Clicklistner() {

        deposit.setOnClickListener(this);
        month_rent.setOnClickListener(this);
        elec_boiler.setOnClickListener(this);
        elec_cost.setOnClickListener(this);
        gas_cost.setOnClickListener(this);
        water_cost.setOnClickListener(this);
        internet_cost.setOnClickListener(this);
        gas_boiler.setOnClickListener(this);
        induction.setOnClickListener(this);
        aircon.setOnClickListener(this);
        washer.setOnClickListener(this);
        refrigerator.setOnClickListener(this);
        closet.setOnClickListener(this);
        gasrange.setOnClickListener(this);
        highlight.setOnClickListener(this);
        convenience_store.setOnClickListener(this);
        subway.setOnClickListener(this);
        parking.setOnClickListener(this);
    }

    private void useGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_ADDRESS) {
            if (resultCode == RESULT_OK) {
                //when success
                //Initialize plcae;

                Place place = Autocomplete.getPlaceFromIntent(data);
                home_address.setText(place.getAddress());
                //set Address on EditText
                //Set LocalityName

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                //Display toast
                Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == FROM_GALLERY) {
            uriList.clear(); // 초기화한번해주고
            if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {   // 이미지를 하나라도 선택한 경우
                if (data.getClipData() == null) {     // 이미지를 하나만 선택한 경우
                    Log.e("single choice: ", String.valueOf(data.getData()));
                    Uri imageUri = data.getData();
                    uriList.add(imageUri);

                } else {      // 이미지를 여러장 선택한 경우
                    ClipData clipData = data.getClipData();

                    if (clipData.getItemCount() > 10) {   // 선택한 이미지가 11장 이상인 경우
                        Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                    } else {   // 선택한 이미지가 1장 이상 10장 이하인 경우

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                            try {
                                uriList.add(imageUri);  //uri를 list에 담는다.

                            } catch (Exception e) {
                            }
                        }
                    }
                }

                photoadapter = new MultiImageAdapter(uriList, getApplicationContext());
                photo_list.setAdapter(photoadapter);
                photo_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                //사진 스토리지에 업로드
                //((MainActivity)MainActivity.maincontext).Onprogress(Post_write.this,"사진 업로드중");

                photoadapter.setOnItemClickListener(new MultiImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos) {

                        Intent intent = new Intent(getApplicationContext(), Image_zoom.class);
                        intent.putExtra("uri", uriList.get(pos));
                        startActivity(intent);
                    }
                });
            }
        }

    }


    private void UploadPhoto(ArrayList<Uri> uris, int n) {

        int i = 0;
        for (Uri uri : uris) {
            Log.d("###", "Uri 는: " + uri);
            String filename = mAuth.getUid() + "_" + System.currentTimeMillis() + n;
            StorageReference ref = storageReference.child("post_image/" + filename + ".jpg");
            image_urllist.add(filename);
            newproduct.setImages_url(image_urllist);

            Log.d("###", filename);

            UploadTask uploadTask;
            uploadTask = ref.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "업로드 성공", Toast.LENGTH_LONG).show();

                }
            });

            //++i;
            //if(uris.size() ==i)((MainActivity)MainActivity.maincontext).progressOFF();
        }
    }

    private void checkswitch() {

    }

}

