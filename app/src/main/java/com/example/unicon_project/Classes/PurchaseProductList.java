package com.example.unicon_project.Classes;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PurchaseProductList {

    private static PurchaseProductList purchaseProductList = new PurchaseProductList();
    private List<PurchaseProduct> purchaseList = new ArrayList<>();
    private List<Double> dist = new ArrayList<>();
    public static PurchaseProductList getInstance(){return purchaseProductList;}

    public List<PurchaseProduct> getPurchaseList() {
        return purchaseList;
    }

    public void setPurchaseList(List<PurchaseProduct> purchaseList){this.purchaseList = purchaseList;}

    public List<Double> getDist() {
        return dist;
    }

    public void setDist(List<Double> dist) {
        this.dist = dist;
    }

    private void swap(int v,int e){
        PurchaseProduct temp;
        temp=purchaseList.get(v);
        purchaseList.set(v,purchaseList.get(e));
        purchaseList.set(e,temp);
        Double dtemp;
        dtemp=dist.get(v);
        dist.set(v,dist.get(e));
        dist.set(e,dtemp);
    }

    public void sort(int type) {
        int _size = purchaseList.size();
        if (type == 1) {
            //가까운
            for (int i = 0; i < _size; i++) {
                for (int j = 0; j < _size - 1; j++) {
                    if (dist.get(j) > dist.get(j + 1))
                        swap(j, j + 1);
                }
            }
        }

        for (int i = 0; i < _size; i++) {
            Log.e("Sort", i + dist.get(i).toString());
        }
    }
}
