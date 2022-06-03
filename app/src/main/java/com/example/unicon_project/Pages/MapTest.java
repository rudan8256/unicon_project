package com.example.unicon_project.Pages;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unicon_project.Classes.Item;
import com.example.unicon_project.ProgressDialog;
import com.example.unicon_project.R;
import com.example.unicon_project.Classes.SaleProduct;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapTest extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener,  View.OnClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Marker selectedMarker = null;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private List<SaleProduct> mDatas=new ArrayList<>();
    private static final String TAG = "MapTest";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESE_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;//1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    private FusedLocationProviderClient mFusedLocationClient;
    Location mCurrentLocation;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByApi = true;
    LatLng currentPosition;
    ArrayList<Item> sampleList = new ArrayList<>();
    private View marker_root_view;
    private TextView tv_marker;
    private LinearLayout marker_body,month_area;
    private ImageView marker_tail;
    private LocationManager locationManager;
    private UiSettings mUiSettings;
    private TextView tv_search_current_camera_position;
    private ImageView iv_center;
    private LinearLayout iv_detail;
    private List<Address> addressList= Collections.emptyList();
    private LinearLayout et_auto;
    private int Boundary=1000;
    private LocationRequest locationRequest;
    private String productId;
    private LatLng currentCameraPosition;
    private Geocoder geocoder;
    private View lo_spinner;
    private String filter = "";
    private Spinner filterSpinner;

    private TextView title,deposit,monthcost,roomsize,structure,address,living_start,living_end;
    private ImageView house_imgview;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    private SaleProduct[] saleProduct = {new SaleProduct()};

    private ImageView day_first, day_last;
    private EditText deposit_price_max, month_price_min,month_price_max,live_period_start,live_period_end;
    private EditText maintenance_cost, room_size_min,room_size_max;
    private ImageView filter_search;
    private String filter_structure;
    private LinearLayout filter_deposit,month_rent;
    public Map<String, Integer> structure_sel_map = new HashMap<>();
    private Spinner structureSpinner;
    private Dialog condition_dialog;
    private String Month_rentprice_max, Month_rentprice_min, Deposit_price_max ,Live_period_start="" ,Live_period_end="" ,Maintenance_cost,
            Room_size_max , Room_size_min;
    private boolean deposit_bool=false,month_bool=false;
    ProgressDialog progressDialog;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);
        progressDialog = new ProgressDialog(MapTest.this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
        //
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        //
        iv_center=findViewById(R.id.iv_center);
        iv_center.setVisibility(View.GONE);
        mActivity = this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //
        /*searchView=findViewById(R.id.et_search);
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener(this);
        searchView.setIconifiedByDefault(true);*/
        et_auto = findViewById(R.id.et_autocomplete);
        et_auto.setFocusable(false);
        et_auto.setOnClickListener(this);

        //
        geocoder = new Geocoder(MapTest.this, Locale.KOREAN);


        tv_search_current_camera_position =findViewById(R.id.tv_search_current_camera_position);
        tv_search_current_camera_position.setOnClickListener(this);

        iv_detail = findViewById(R.id.iv_detail);

        iv_detail.setOnClickListener(this);

        filterSpinner = findViewById(R.id.spinner_filter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    Boundary=1000;
                }
                else if(i==1){
                    Boundary = 3000;
                }
                else if(i==2){
                    Boundary = 5000;
                }
                else if(i==3){
                    Boundary = 10000;
                }
                filter = adapterView.getItemAtPosition(i).toString();
                Log.e("###", String.valueOf(Boundary));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Places.initialize(getApplicationContext(),"AIzaSyBslpmgHhMBvhT2ZrhV7tX4kmT_3jDrPAA",Locale.KOREAN);


        //마커클릭시 리니어 요소들
        title =findViewById(R.id.post_title);
        deposit = findViewById(R.id.post_deposit);
        monthcost =findViewById(R.id.post_monthcost);
        roomsize = findViewById(R.id.post_roomsize);
        structure = findViewById(R.id.post_structure);
        address = findViewById(R.id.post_address);
        living_end = findViewById(R.id.post_living_end);
        living_start=findViewById(R.id.post_living_start);
        house_imgview=findViewById(R.id.house_imgview);

        house_imgview.setClipToOutline(true);


        //필터 클릭시
        filter_search = findViewById(R.id.filter_search_map);

        //condition dialog constructer
        condition_dialog= new Dialog(this);
        condition_dialog.setContentView(R.layout.dialog_reccondition);
        condition_dialog.setCanceledOnTouchOutside(true);

        Construter();

        filter_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        lo_spinner = findViewById(R.id.lo_spinner);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"자동완성");
        if(requestCode==100&&resultCode==RESULT_OK){
            //when success
            //Initialize plcae;

            Place place = Autocomplete.getPlaceFromIntent(data);
            //set Address on EditText
            Log.e(TAG,"자동완성 클릭 : "+place.getLatLng());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            //Set LocalityName

        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.e(TAG,"자동완성 클릭 : 실패");
            //Display toast
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_LONG).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            if (!mRequestingLocationUpdates) {

            }
        }

        if (askPermissionOnceAgain) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;
                getCurrentPosition();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 10));
            }
        }

    }


    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private void checkPermissions() {
//꼭 onCreate 나 onStart에서 실행시켜야됨
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );

        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.e(TAG,"OnMapReady시작");
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        mUiSettings.setMapToolbarEnabled(true);
       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Seoul);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);*/

        /*  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Seoul,10));*/

        getCurrentPosition();
        currentCameraPosition=currentPosition;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);


        //마커 테스트
        setCustomMarkerView();
        //*마커 테스트

        mMap.setOnMarkerClickListener(this);

        mMap.setOnCameraIdleListener(this);

        //mMap.setOnCameraMoveStartedListener(this);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e(TAG,"OnWindowFocusChanged");
        mMap.setPadding(0,lo_spinner.getTop(),0,0);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //현재위치 받아오기

    private void getCurrentPosition() {
        Log.e(TAG,"currentPosition : 들어옴");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.e(TAG,"currentPosition : 권한은있음");

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {
                    currentCameraPosition = currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
                    Log.e(TAG, "currentPosition : success" + currentPosition);
                }
                else  Log.e(TAG,"currentPosition : null");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "currentPosition : 실패");

            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                currentCameraPosition=currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                Log.e(TAG, "currentPosition : complete"+task.getResult().toString());

            }
        });


    }


    //마커 변경
    private void setCustomMarkerView(){
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker,null);
        tv_marker= marker_root_view.findViewById(R.id.tv_price);
        marker_body = marker_root_view.findViewById(R.id.marker_body);
        marker_tail = marker_root_view.findViewById(R.id.marker_tail);
    }

    private Bitmap createDrawableFromView(Context context, View view){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels,displayMetrics.heightPixels);
        view.layout(0,0,displayMetrics.widthPixels,displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),view.getMeasuredHeight(),Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private LatLng getLatLng(String str){
        Address address = null;
        LatLng latLng = null;
        try {
            address = geocoder.getFromLocationName(str,1).get(0);
            latLng = new LatLng(address.getLatitude(),address.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    private Marker addMarker(SaleProduct saleProduct, boolean isSelectedMarker){
        Log.e(TAG,"saleproduct ID : "+saleProduct.getProductId());
        LatLng position = getLatLng(saleProduct.getHome_adress());
        int price =Integer.parseInt(saleProduct.getMonth_rent_price());
        String formatted = NumberFormat.getCurrencyInstance().format(price);
        Log.e(TAG,"addMarker"+formatted);
        tv_marker.setText(price+"0,000원");

        if(isSelectedMarker){
            //tv_marker.setBackgroundResource(R.drawable);
            tv_marker.setTextColor(Color.WHITE);
            tv_marker.setBackground(getDrawable(R.drawable.marker_border_ispick));
            marker_body.setBackground(getDrawable(R.drawable.marker_border_ispick));
            marker_tail.setBackground(getDrawable(R.drawable.down_arrow_marker_ispick));
        }
        else{
            //tv_marker.setBackgroundResource(R.drawable);
            tv_marker.setTextColor(Color.BLACK);
            tv_marker.setBackgroundColor(Color.WHITE);
            marker_body.setBackground(getDrawable(R.drawable.marker_border));
            marker_tail.setBackground(getDrawable(R.drawable.down_arrow_marker));
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(Integer.toString(price));
        markerOptions.snippet(saleProduct.getProductId());
        Log.e(TAG,"snippet : "+markerOptions.getSnippet());
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this,marker_root_view)));

        Log.e(TAG,"addMarker"+position);
        return mMap.addMarker(markerOptions);
    }

    private Marker addMarker(Marker marker, boolean isSelectedMarker){
        LatLng position = marker.getPosition();
        int price =Integer.parseInt(marker.getTitle());
        SaleProduct saleProduct = new SaleProduct();
        for(int i=0;i<mDatas.size();i++){
            if(mDatas.get(i).getProductId().equals(marker.getSnippet())){

                saleProduct = mDatas.get(i);
                break;
            }
        }
        return addMarker(saleProduct,isSelectedMarker);
    }



    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        productId= marker.getSnippet();

        mStore.collection("SaleProducts").whereEqualTo("productId",productId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                saleProduct[0] = task.getResult().getDocuments().get(0).toObject(SaleProduct.class);
//                it.putExtra("select_data",saleProduct[0]);
//                startActivity(it);


                if(saleProduct[0].getMonth_rent()){  title.setText("월세");}
                else{ title.setText("전세"); }
                 deposit.setText(saleProduct[0].getDeposit_price());
              monthcost.setText(" / "+ saleProduct[0].getMonth_rent_price());
                roomsize.setText(saleProduct[0].getRoom_size());
                structure.setText(saleProduct[0].getStructure());
               address.setText(saleProduct[0].getHome_name());
                living_start.setText(saleProduct[0].getLive_period_start());
               living_end.setText("~"+saleProduct[0].getLive_period_end());

               Log.e("@@@@@", String.valueOf(saleProduct[0].getMonth_rent()));
                Log.e("@@@@@", String.valueOf(saleProduct[0].getDeposit_price()));


                if(saleProduct[0].getImages_url().size()>0) {
                    String imagedata = saleProduct[0].getImages_url().get(0);

                    house_imgview.setPadding(0,0,0,0);

                    //StorageReference submitImage = storageReference.child("post_image/" + image_url + ".jpg");
                    storageReference.child("post_image/" + imagedata + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into( house_imgview);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 실패
                        }
                    });
                }

                setDetailPosUp();

            }
        });


        changeSelectedMarker(marker);
        return false;
    }

    private void changeSelectedMarker(Marker marker){
        if(selectedMarker!=null){
            addMarker(selectedMarker,false);
            selectedMarker.remove();
        }
        if(marker!=null){
            selectedMarker=addMarker(marker,true);
            marker.remove();
        }
    }


    void getCurrentCameraPosition(){
        currentCameraPosition=mMap.getCameraPosition().target;

    }

    int abs(int a){
        if(a>=0)return a;
        else return -a;
    }
    boolean cmpLatLng(LatLng p1,LatLng p2){
        int lat1=(int)(p1.latitude*100000);
        int lat2=(int)(p2.latitude*100000);
        int lng1=(int)(p1.longitude*100000);
        int lng2=(int)(p2.longitude*100000);
        Log.e(TAG,"lat1 : "+lat1);
        Log.e(TAG,"lng1 : "+lng1);
        Log.e(TAG,"lat2 : "+lat2);
        Log.e(TAG,"lng2 : "+lng2);

        if(abs(lat1-lat2)>20){
            return false;
        }
        if(abs(lng1-lng2)>20){
            return false;
        }
        return true;
    }





    @Override
    public void onCameraIdle() {

        currentCameraPosition=mMap.getCameraPosition().target;
        if(currentPosition==null)currentPosition=currentCameraPosition;
        Log.e(TAG,"cameraPosition : "+ currentCameraPosition);
        Log.e(TAG,"currentPosition : "+ currentPosition);

        if(cmpLatLng(currentCameraPosition,currentPosition)){
            Log.e(TAG,"setVisibility : GONE");
            iv_center.setVisibility(View.GONE);
        }
        else{
            Log.e(TAG,"setVisibility : VISIBLE");
            iv_center.setVisibility(View.VISIBLE);
        }

        try {
            List<Address> addressList= geocoder.getFromLocation(currentCameraPosition.latitude,currentCameraPosition.longitude,1);
            if(addressList.size()!=0) {
                String string_center_address = addressList.get(0).toString();
                Log.e(TAG, "center_address : " + string_center_address);
            }
            else {
                Log.e(TAG, "center_address : null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(selectedMarker!=null) {
            if (!cmpLatLng(currentCameraPosition, selectedMarker.getPosition())) {
                setDetailPosDown();
                addMarker(selectedMarker,false);
                selectedMarker.remove();
            }


        }
    }


    double getDist(LatLng p1,LatLng p2) {
        Location l1= new Location("p1");
        l1.setLatitude(p1.latitude);
        l1.setLongitude(p1.longitude);

        Location l2= new Location("p2");
        l2.setLatitude(p2.latitude);
        l2.setLongitude(p2.longitude);

        return l1.distanceTo(l2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.et_autocomplete:
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(MapTest.this);
                startActivityForResult(intent,100);
                break;

            case R.id.tv_search_current_camera_position:
                progressDialog.show();
                if(currentCameraPosition==null){
                    Toast.makeText(getApplicationContext(),"잠시 후 다시 시도해주세요",Toast.LENGTH_SHORT).show();
                    break;
                }
                mMap.clear();
                mDatas.clear();
                getCurrentCameraPosition();

                mStore.collection("SaleProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task!=null) {
                            Log.e(TAG, "쿼리개수 : " + task.getResult().getDocuments().size());
                            for (DocumentSnapshot snap : task.getResult().getDocuments()) {
                                SaleProduct temp = snap.toObject(SaleProduct.class);
                                LatLng latlng=getLatLng(temp.getHome_adress());

                                if(getDist(currentCameraPosition,latlng)>Boundary)continue;

                                mDatas.add(temp);
                                addMarker(temp,false);

                            }
                            if(mDatas.isEmpty()||mDatas==null){
                                Log.e(TAG, "쿼리 : 비었음");

                            }


                        }

                        progressDialog.dismiss();
                    }
                });
                break;

            case R.id.iv_detail:

                Intent it = new Intent(this, SaleProductPage.class);
                it.putExtra("select_data",saleProduct[0]);
                startActivity(it);
                break;

            case R.id.deposit:
                if( !deposit_bool ){
                    deposit_bool=true;
                    deposit.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    deposit.setSelected(true);

                    if( month_bool ) {
                        month_bool = false;
                        month_rent.setBackground(getDrawable(R.drawable.salepage_inputborder));
                        month_rent.setSelected(false);
                    }
                    month_area.setVisibility(View.GONE);
                }
                else{
                    deposit_bool=false;
                    deposit.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    deposit.setSelected(false);

                    month_area.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.month_rent:
                if( !month_bool ){
                    month_bool=true;
                    month_rent.setBackground(getDrawable(R.drawable.salepage_inputborder_isclick));
                    month_rent.setSelected(true);

                    if( deposit_bool ) {
                        deposit_bool=false;
                        deposit.setBackground(getDrawable(R.drawable.salepage_inputborder));
                        deposit.setSelected(false);
                    }
                    month_area.setVisibility(View.VISIBLE);
                }
                else{
                    month_bool=false;
                    month_rent.setBackground(getDrawable(R.drawable.salepage_inputborder));
                    month_rent.setSelected(false);

                }
                break;
        }
    }

    void setDetailPosDown(){

        Log.e(TAG,"setDetailPostDown : 실행");
        iv_detail.animate().translationY(0);
        tv_search_current_camera_position.animate().translationY(0);

    }
    void setDetailPosUp(){
        ViewGroup vg = findViewById(R.id.map);
        int gap = tv_search_current_camera_position.getBottom()-iv_detail.getBottom();
        Log.e(TAG,"setDetailPostUp : 실행"+(gap));


        iv_detail.animate().translationY(gap);
        tv_search_current_camera_position.animate().translationY(gap);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.not_move,R.anim.slide_out_down);
    }

    private void showDialog() {


        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
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


                Month_rentprice_max=month_price_max.getText().toString();
                Month_rentprice_min=month_price_min.getText().toString();
                Deposit_price_max=deposit_price_max.getText().toString();

                Live_period_start=live_period_start.getText().toString();
                Live_period_end=live_period_end.getText().toString();
                Maintenance_cost=maintenance_cost.getText().toString();
                Room_size_max=room_size_max.getText().toString();
                Room_size_min=room_size_min.getText().toString();


                List<SaleProduct> filter_datas = new ArrayList<>();
                Log.e("@@@@@@", String.valueOf(mDatas.size()));

                for(SaleProduct item : mDatas){

                    if(deposit_bool== item.getDeposit() || month_bool == item.getMonth_rent()) {
                        Log.e("@@@@@@", "포문 진입 확인");

                        if(Judge(item) > 1) {
                            Log.e("@@@@@", String.valueOf(Judge(item)));
                            filter_datas.add(item);

                        }
                    }
                }

                mDatas.clear();
                mMap.clear();
                mDatas=filter_datas;

                Log.e("@@@@@@", "aa"+String.valueOf(filter_datas.size()));

                for (SaleProduct snap : mDatas) {
                    SaleProduct temp = snap;
                    LatLng latlng=getLatLng(temp.getHome_adress());

                    if(getDist(currentCameraPosition,latlng)>Boundary)continue;


                    addMarker(temp,false);

                }
                if(mDatas.isEmpty()||mDatas==null){
                    Log.e(TAG, "쿼리 : 비었음");

                }


                condition_dialog.dismiss();


            }
        });
        condition_dialog.show();
    }

    private int Judge(SaleProduct curdata){

        int total=0;

        String cur_data_start= curdata.getLive_period_start().replace("/","");
        String cur_data_end= curdata.getLive_period_end().replace("/","");
        String pre_data_start =Live_period_start.replace("/","");
        String pre_data_end =Live_period_end.replace("/","");



//        Log.e("@@@@@",cur_data_end+' '+cur_data_start);

        //완변 조건매물 토탈 4000


        if( ! cur_data_end.equals("") && !cur_data_start.equals("") && !pre_data_end.equals("") && !pre_data_start.equals("")) {
            if (Integer.parseInt(pre_data_end) < Integer.parseInt(cur_data_start) || Integer.parseInt(cur_data_end) < Integer.parseInt(pre_data_start)) {
                Log.e("@@@@@@", "날짜 ㅇ빠꾸");

                return -1;
            }
        }
        if(! filter_structure.equals("")) {
            if (!filter_structure.equals(curdata.getStructure()) && !filter_structure.equals("상관없음")) {

                Log.e("@@@@@@", "구조바꾸");
                Log.e("@@@@@@", filter_structure);

                return -1;
            }
        }

        if(! Deposit_price_max.equals("")) {
            if (!(Integer.parseInt(Deposit_price_max) >= Integer.parseInt(curdata.getDeposit_price()))) {
                Log.e("@@@@@@", "보증금바꾸");
                return -1;
            }
        }

        if ( ! Month_rentprice_max.equals("") &&! Month_rentprice_min.equals("")) {
            if (!(Integer.parseInt(Month_rentprice_max) >= Integer.parseInt(curdata.getMonth_rent_price()) &&
                    Integer.parseInt(Month_rentprice_min) <= Integer.parseInt(curdata.getMonth_rent_price()))) {

                Log.e("@@@@@@", "월세");
                Log.e("@@@@@@", "1"+Month_rentprice_max);
                Log.e("@@@@@@", "2"+Month_rentprice_min);
                Log.e("@@@@@@", "3"+curdata.getMonth_rent_price());
                return -1;
            }
        }

        if(! Maintenance_cost.equals("")) {
            if(Integer.parseInt(Maintenance_cost) < Integer.parseInt(curdata.getMaintenance_cost())){
                Log.e("@@@@@@", "관리비");
                return -1;
            }
        }
        if(! Room_size_max.equals("") &&!Room_size_min.equals("")) {
            if (!(Integer.parseInt(Room_size_min) <= Integer.parseInt(curdata.getRoom_size()) &&
                    Integer.parseInt(Room_size_max) >= Integer.parseInt(curdata.getRoom_size()))
            ) {
                Log.e("@@@@@@", "방사이즈");
                return -1;
            }
        }


        return 100;



    }

    private void set_Clicklistner(){

        deposit.setOnClickListener(this);
        month_rent.setOnClickListener(this);

    }

    private void Construter(){


        filter_deposit = condition_dialog.findViewById(R.id.deposit);
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
        month_area = condition_dialog.findViewById(R.id.month_area);


        structureSpinner = condition_dialog.findViewById(R.id.structure);


        structureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filter_structure = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }


}