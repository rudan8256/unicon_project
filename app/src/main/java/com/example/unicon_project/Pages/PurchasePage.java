package com.example.unicon_project.Pages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unicon_project.Classes.PurchaseProduct;
import com.example.unicon_project.R;
import com.google.android.gms.common.api.Status;
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PurchasePage extends AppCompatActivity implements View.OnClickListener{

    private static final int FROM_ADDRESS = 100;
    private Button complete_btn;
    private PurchaseProduct newProduct;
    private EditText home_address, deposit_price_max, month_price_min, month_price_max;
    private EditText maintenance_cost, room_size_min, room_size_max, specific;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String curDate, structure;
    private LinearLayout deposit, month_rent, negotiable, elec_cost, gas_cost, water_cost, internet_cost;
    private LinearLayout elec_boiler, gas_boiler, induction, aircon, washer, refrigerator, closet, gasrange, highlight;
    private LinearLayout convenience_store, subway, parking;
    private Spinner structureSpinner;
    private ImageView day_first, day_last;
    private Map<String, Boolean> maintains, options;
    private DatePickerDialog datePickerDialog;
    private TextView live_period_start, live_period_end;
    private String str_live_period_start = "", str_live_period_end = "";
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_page);
        Places.initialize(getApplicationContext(), "AIzaSyBslpmgHhMBvhT2ZrhV7tX4kmT_3jDrPAA", Locale.KOREAN);
        storageReference= FirebaseStorage.getInstance().getReferenceFromUrl("gs://uniconproject-2be63.appspot.com/");

        //새로운 구매글 클래스 생성
        newProduct = new PurchaseProduct();
        maintains = newProduct.getMaintains();
        options = newProduct.getOptions();

        Construter();
        set_Clicklistner();

        structureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                structure=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        home_address.setFocusable(false);
        home_address.setOnClickListener(this);

        day_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(PurchasePage.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month = month + 1;
                                str_live_period_start = year+"/"+month+"/"+day;
                                live_period_start.setText(str_live_period_start);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        day_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(PurchasePage.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month = month + 1;
                                str_live_period_end = year+"/"+month+"/"+day;
                                live_period_end.setText(str_live_period_end);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Create a new user with a first and last name
                newProduct.setHome_address(home_address.getText().toString());
                newProduct.setMonth_rent_price_min(month_price_min.getText().toString());
                newProduct.setMonth_rent_price_max(month_price_max.getText().toString());
                newProduct.setDeposit_price_max(deposit_price_max.getText().toString());
                newProduct.setLive_period_start(str_live_period_start);
                newProduct.setLive_period_end(str_live_period_end);
                newProduct.setMaintenance_cost(maintenance_cost.getText().toString());
                newProduct.setRoom_size_min(room_size_min.getText().toString());
                newProduct.setRoom_size_max(room_size_max.getText().toString());
                newProduct.setSpecific(specific.getText().toString());
                newProduct.setStructure(structure);

                curDate = String.valueOf(System.currentTimeMillis());
                newProduct.setProductId(curDate + mAuth.getUid());

                mstore.collection("PurchaseProducts").document(curDate + mAuth.getUid())
                        .set(newProduct)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("###","업로드 성공");
                                finish();
                            }
                        });
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.home_address:
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(PurchasePage.this);
                startActivityForResult(intent, 100);
                break;
            case R.id.deposit:
                if( !newProduct.getDeposit() ){
                    newProduct.setDeposit(true); deposit.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    deposit.setSelected(true);}
                else{
                    newProduct.setDeposit(false);deposit.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    deposit.setSelected(false);}
                break;
            case R.id.month_rent:
                if( !newProduct.getMonth_rent() ){
                    newProduct.setMonth_rent(true);month_rent.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    month_rent.setSelected(true);}
                else{
                    newProduct.setMonth_rent(false);month_rent.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    month_rent.setSelected(false);}
                break;
            case R.id.negotiable:
                if(!newProduct.getNegotiable()){
                    newProduct.setNegotiable(true);negotiable.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    negotiable.setSelected(true);}
                else{
                    newProduct.setNegotiable(false);negotiable.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    negotiable.setSelected(false);}
                break;
            case R.id.elec_cost:
                if( !maintains.get("elec_cost") ){
                    maintains.put("elec_cost",true); elec_cost.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    elec_cost.setSelected(true);
                } else{
                    maintains.put("elec_cost",false);elec_cost.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    elec_cost.setSelected(false);
                }
                break;
            case R.id.gas_cost:
                if( !maintains.get("gas_cost") ){
                    maintains.put("gas_cost",true); gas_cost.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    gas_cost.setSelected(true);
                } else{
                    maintains.put("gas_cost",false);gas_cost.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    gas_cost.setSelected(false);
                }
                break;
            case R.id.water_cost:
                if( !maintains.get("water_cost") ){
                    maintains.put("water_cost",true); water_cost.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    water_cost.setSelected(true);
                } else{
                    maintains.put("water_cost",false); water_cost.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    water_cost.setSelected(false);
                }
                break;
            case R.id.internet_cost:
                if( !maintains.get("internet_cost") ){
                    maintains.put("internet_cost",true); internet_cost.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    internet_cost.setSelected(true);
                } else{
                    maintains.put("internet_cost",false); internet_cost.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    internet_cost.setSelected(false);
                }
                break;
            case R.id.elec_boiler:
                if( !options.get("elec_boiler") ){
                    options.put("elec_boiler",true); elec_boiler.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    elec_boiler.setSelected(true);
                } else{
                    options.put("elec_boiler",false);elec_boiler.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    elec_boiler.setSelected(false);
                }
                break;

            case R.id.gas_boiler:
                if( !options.get("gas_boiler") ){
                    options.put("gas_boiler",true); gas_boiler.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    gas_boiler.setSelected(true);
                } else{
                    options.put("gas_boiler",false); gas_boiler.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    gas_boiler.setSelected(false);
                }
                break;

            case R.id.induction:
                if( !options.get("induction") ){
                    options.put("induction",true); induction.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    induction.setSelected(true);
                } else{
                    options.put("induction",false); induction.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    induction.setSelected(false);
                }
                break;
            case R.id.aircon:
                if( !options.get("aircon") ){
                    options.put("aircon",true); aircon.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    aircon.setSelected(true);
                } else{
                    options.put("aircon",false); aircon.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    aircon.setSelected(false);
                }
                break;

            case R.id.washer:
                if( !options.get("washer") ){
                    options.put("washer",true); washer.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    washer.setSelected(true);
                } else{
                    options.put("washer",false); washer.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    washer.setSelected(false);
                }
                break;
            case R.id.refrigerator:
                if( !options.get("refrigerator") ){
                    options.put("refrigerator",true);  refrigerator.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    refrigerator.setSelected(true);
                } else{
                    options.put("refrigerator",false);  refrigerator.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    refrigerator.setSelected(false);
                }
                break;
            case R.id.closet:
                if( !options.get("closet") ){
                    options.put("closet",true);  closet.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    closet.setSelected(true);
                } else{
                    options.put("closet",false);  closet.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    closet.setSelected(false);
                }
                break;
            case R.id.gasrange:
                if( !options.get("gasrange") ){
                    options.put("gasrange",true);  gasrange.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    gasrange.setSelected(true);
                } else{
                    options.put("gasrange",false); gasrange.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    gasrange.setSelected(false);
                }
                break;
            case R.id.highlight:
                if( !options.get("highlight") ){
                    options.put("highlight",true);  highlight.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    highlight.setSelected(true);
                } else{
                    options.put("highlight",false); highlight.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    highlight.setSelected(false);
                }
                break;
            case R.id.convenience_store:
                if( !options.get("convenience_store") ){
                    options.put("convenience_store",true); convenience_store.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    convenience_store.setSelected(true);
                } else{
                    options.put("convenience_store",false); convenience_store.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    convenience_store.setSelected(false);
                }
                break;
            case R.id.subway:
                if( !options.get("subway") ){
                    options.put("subway",true); subway.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    subway.setSelected(true);
                } else{
                    options.put("subway",false); subway.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    subway.setSelected(false);
                }
                break;
            case R.id.parking:
                if( !options.get("parking") ){
                    options.put("parking",true); parking.setBackgroundResource(R.drawable.sale_purchase_color_round15);
                    parking.setSelected(true);
                } else{
                    options.put("parking",false); parking.setBackgroundResource(R.drawable.sale_purchase_white_round15);
                    parking.setSelected(false);
                }
                break;


        }
    }

    public void Construter(){
        complete_btn = findViewById(R.id.complete_btn);
        home_address = findViewById(R.id.home_address);
        deposit_price_max = findViewById(R.id.deposit_price_max);
        month_price_min = findViewById(R.id.month_price_min);
        month_price_max = findViewById(R.id.month_price_max);
        live_period_start = findViewById(R.id.live_period_start);
        live_period_end = findViewById(R.id.live_period_end);
        maintenance_cost = findViewById(R.id.maintenance_cost);
        room_size_min = findViewById(R.id.room_size_min);
        room_size_max = findViewById(R.id.room_size_max);
        specific = findViewById(R.id.specific);

        structureSpinner = findViewById(R.id.structure);
        deposit = findViewById(R.id.deposit);
        month_rent = findViewById(R.id.month_rent);
        negotiable = findViewById(R.id.negotiable);
        day_first = findViewById(R.id.day_first);
        day_last = findViewById(R.id.day_last);

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
        closet =findViewById(R.id.closet);
        gasrange = findViewById(R.id.gasrange);
        highlight = findViewById(R.id.highlight);
        convenience_store =findViewById(R.id.convenience_store);
        subway = findViewById(R.id.subway);
        parking = findViewById(R.id.parking);
    }

    public void set_Clicklistner(){

        deposit.setOnClickListener(this);
        month_rent.setOnClickListener(this);
        negotiable.setOnClickListener(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        }
    }
}