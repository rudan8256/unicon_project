package com.unicon.unicon_project.Manager;

import static java.lang.Math.abs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Locale;

public class MyLocationManager {

    private static final String TAG = "MyLocationManager";
    private static MyLocationManager myLocationManager = new MyLocationManager();
    public static MyLocationManager getInstance(){
        return myLocationManager;
    }
    private static LatLng myCurrentPosition = new LatLng(37,132);

    private Geocoder geocoder;

    private FusedLocationProviderClient mFusedLocationClient;

    public double getDist(LatLng p1, LatLng p2) {
        Location l1= new Location("p1");
        l1.setLatitude(p1.latitude);
        l1.setLongitude(p1.longitude);

        Location l2= new Location("p2");
        l2.setLatitude(p2.latitude);
        l2.setLongitude(p2.longitude);

        return l1.distanceTo(l2);
    }

    public LatLng getCurrentPositionFromGPS(Context context, Activity activity){

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        Log.e(TAG,"currentPosition : 들어옴");

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Log.e(TAG,"currentPosition : 권한은있음");

        mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {
                    myCurrentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.e(TAG, "currentPosition : success" + myCurrentPosition);
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
                myCurrentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                Log.e(TAG, "currentPosition : complete"+task.getResult().toString());

            }
        });
        return myCurrentPosition;

    }


    public boolean cmpLatLng(LatLng p1,LatLng p2){
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

    public static LatLng getMyCurrentPosition() {
        return myCurrentPosition;
    }

    public static void setMyCurrentPosition(LatLng myCurrentPosition) {
        MyLocationManager.myCurrentPosition = myCurrentPosition;
    }

    public LatLng getLatLng(Context context,String str){
        geocoder= new Geocoder(context,Locale.KOREAN);
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
}
