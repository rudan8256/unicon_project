package com.example.unicon_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MapTest extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient=null;
    private Marker currentMarker =null;

    private static final String TAG = "MapTest";
    private static final int GPS_ENABLE_REQUEST_CODE=2001;
    private static final int PERMISSIONS_REQUEST_ACCESE_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS=1000;//1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain =false;
    boolean mRequestingLocationUpdates = false;
    private FusedLocationProviderClient mFusedLocationClient;
    Location mCurrentLocation;
    boolean mMoveMapByUser=true;
    boolean mMoveMapByApi = true;
    LatLng currentPosition;
    ArrayList<Item> sampleList= new ArrayList<>();
    private View marker_root_view;
    private TextView tv_marker;

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);

        //
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //

        //
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //

        mActivity = this;

        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mGoogleApiClient.isConnected()){
            if(!mRequestingLocationUpdates)startLocationUpdates();
        }

        if(askPermissionOnceAgain){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                askPermissionOnceAgain=false;
                checkPermissions();
            }
        }
    }

    private void startLocationUpdates(){
        if(!checkLocationServicesStatus()){
            showDialogForLocationServiceSetting();
        }
        else{
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED&&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                Log.e(TAG,"startLocationUpdate : 퍼미션 없음");
                return;
            }
            Log.e(TAG,"startLocationUpdate : call FusedLocationApi.requestLocationUpdates");
            //이거 안될수도 있음
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest,this::onLocationChanged);
            mRequestingLocationUpdates=true;
            mMap.setMyLocationEnabled(true);
        }
    }

    private void stopLocationUpdates(){

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRational = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_DENIED&&fineLocationRational)
            showDialogForPermission("앱을 실행하려면 권한을 허가하셔야합니다.");
        else if(hasFineLocationPermission == PackageManager.PERMISSION_DENIED && !fineLocationRational)
            showDialogForPermissionSetting("앱을 실행하려면 설정에서 해당 앱의 위치 권한을 허가하셔야합니다.");
        else if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED){
            Log.e(TAG,"위치 권한 가지고있음");

            if(mGoogleApiClient.isConnected()==false){
                Log.e(TAG,"api 연결");

                mGoogleApiClient.connect();
            }
        }

    }



    private void showDialogForPermissionSetting(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapTest.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:"+mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESE_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.create().show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(MapTest.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESE_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.create().show();
    }



    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MapTest.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +"위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent,GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap=googleMap;

        LatLng Seoul = new LatLng(37.560001,126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Seoul);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);

        setCustomMarkerView();

        getSampleMarkerList();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Seoul,10));

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

    //implementation 'com.google.anddroid.matterial:com.google.android.material:materail:1.4.0-alpha-02'
    private void getCurrentPosition(){


    }

    //마커 변경
    private void setCustomMarkerView(){
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker,null);
        tv_marker= marker_root_view.findViewById(R.id.tv_price);
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

    private Marker addMarker(Item item, boolean isSelectedMarker){
        LatLng position = item.getLatLng();
        int price =item.getPrice();
        String formatted = NumberFormat.getCurrencyInstance().format(price);
        Log.e(TAG,"addMarker"+formatted);
        tv_marker.setText(formatted);

        if(isSelectedMarker){

        }
        else{

        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(Integer.toString(price));
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this,marker_root_view)));

        Log.e(TAG,"addMarker"+position);
        return mMap.addMarker(markerOptions);
    }

    private void getSampleMarkerList(){
        LatLng latlng = new LatLng(37.56,126.97);
        sampleList.add(new Item(latlng,1000));
        latlng = new LatLng(37.561,126.97);
        sampleList.add(new Item(latlng,1000));
        latlng = new LatLng(37.562,126.97);
        sampleList.add(new Item(latlng,1000));
        latlng = new LatLng(37.563,126.97);
        sampleList.add(new Item(latlng,1000));

        for(Item markerItem : sampleList){
            Log.e(TAG,"sampleList"+markerItem.getLatLng());
            addMarker(markerItem,false);
        }
    }
}