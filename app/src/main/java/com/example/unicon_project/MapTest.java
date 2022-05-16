package com.example.unicon_project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.provider.Settings;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MapTest extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener,   SearchView.OnQueryTextListener,  GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Marker selectedMarker = null;

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
    private LocationManager locationManager;
    private UiSettings mUiSettings;
    private SearchView searchView;
    private ImageView iv_search_current_camera_position;
    private ImageView iv_center;
    private ImageView iv_detail;

    private LocationRequest locationRequest;

    private LatLng currentCameraPosition;
    private Geocoder geocoder;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);
        //
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
        searchView=findViewById(R.id.et_search);
        searchView.setOnQueryTextListener(this);
        geocoder = new Geocoder(MapTest.this);

        iv_search_current_camera_position=findViewById(R.id.iv_search_current_camera_position);
        iv_search_current_camera_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentCameraPosition();
            }
        });

        iv_detail = findViewById(R.id.iv_detail);
        iv_detail.setVisibility(View.GONE);



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

        mMap.setOnMyLocationButtonClickListener(this);

        //마커 테스트
        setCustomMarkerView();
        //*마커 테스트

        mMap.setOnMarkerClickListener(this);

        mMap.setOnCameraIdleListener(this);

        //mMap.setOnCameraMoveStartedListener(this);

        mMap.setOnCameraMoveListener(this);
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
                    currentCameraPosition =    currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
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
                getSampleMarkerList();
            }
        });


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
            //tv_marker.setBackgroundResource(R.drawable);
            tv_marker.setTextColor(Color.BLUE);
        }
        else{
            //tv_marker.setBackgroundResource(R.drawable);
            tv_marker.setTextColor(Color.BLACK);
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(Integer.toString(price));
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this,marker_root_view)));

        Log.e(TAG,"addMarker"+position);
        return mMap.addMarker(markerOptions);
    }

    private Marker addMarker(Marker marker, boolean isSelectedMarker){

        LatLng position = marker.getPosition();
        int price =Integer.parseInt(marker.getTitle());
        Item temp= new Item(new LatLng(position.latitude,position.longitude),price);

        return addMarker(temp,isSelectedMarker);
    }

    private void getSampleMarkerList(){
        LatLng latlng = new LatLng(currentPosition.latitude-0.0001,currentPosition.longitude-0.0001);
        sampleList.add(new Item(latlng,1000));
        latlng = new LatLng(currentPosition.latitude-0.0001,currentPosition.longitude+0.0001);
        sampleList.add(new Item(latlng,2000));
        latlng = new LatLng(currentPosition.latitude+0.0001,currentPosition.longitude-0.0001);
        sampleList.add(new Item(latlng,3000));
        latlng = new LatLng(currentPosition.latitude+0.0001,currentPosition.longitude+0.0001);
        sampleList.add(new Item(latlng,4000));

        for(Item markerItem : sampleList){
            Log.e(TAG,"sampleList"+markerItem.getLatLng());
            addMarker(markerItem,false);
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        iv_detail.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onMyLocationButtonClick() {
        currentCameraPosition = currentPosition = mMap.getCameraPosition().target;
        iv_center.setVisibility(View.GONE);
        Log.e(TAG,"onMyLocationButtonClick : "+currentPosition);
        return false;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        String location = searchView.getQuery().toString();
        List<Address> addressList = null;

        if(location != null || !location.equals("")){
            try {
                addressList = geocoder.getFromLocationName(location, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        return false;
    }

    void getCurrentCameraPosition(){
        mMap.addMarker(new MarkerOptions().position(currentCameraPosition));

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
                iv_detail.setVisibility(View.GONE);
                addMarker(selectedMarker,false);
                selectedMarker.remove();
            }


        }
    }

    @Override
    public void onCameraMove() {

    }
}