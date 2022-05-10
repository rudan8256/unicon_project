package com.example.unicon_project;


import com.google.android.gms.maps.model.LatLng;

public class Item {
    LatLng latLng;
    int price;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Item(LatLng latLng, int price) {
        this.latLng = latLng;
        this.price = price;
    }
}

