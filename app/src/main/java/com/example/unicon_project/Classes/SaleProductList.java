package com.example.unicon_project.Classes;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SaleProductList {

    private static SaleProductList saleProductList = new SaleProductList();
    private List<SaleProduct> saleList = new ArrayList<>();
    private List<Double> dist = new ArrayList<>();
    public static SaleProductList getInstance(){
        return saleProductList;
    }

    public List<SaleProduct> getSaleList() {
        return saleList;
    }

    public void setSaleList(List<SaleProduct> saleList) {
        this.saleList = saleList;
    }

    public List<Double> getDist() {
        return dist;
    }

    public void setDist(List<Double> dist) {
        this.dist = dist;
    }

    private void swap(int v,int e){
        SaleProduct temp;
        temp=saleList.get(v);
        saleList.set(v,saleList.get(e));
        saleList.set(e,temp);
        Double dtemp;
        dtemp=dist.get(v);
        dist.set(v,dist.get(e));
        dist.set(e,dtemp);
    }

    public void sort(int type){
        int _size = saleList.size();
        if(type==1){
            //가격 낮은

            for(int i=0;i<_size;i++){
                for(int j=0;j<_size-1;j++){
                    if(Integer.parseInt(saleList.get(j).getMonth_rent_price())>Integer.parseInt(saleList.get(j+1).getMonth_rent_price()))
                        swap(j,j+1);
                }
            }
        }
        else if(type==2){
            //가격 높은
            for(int i=0;i<_size;i++){
                for(int j=0;j<_size-1;j++){
                    if(Integer.parseInt(saleList.get(j).getMonth_rent_price())<Integer.parseInt(saleList.get(j+1).getMonth_rent_price()))
                        swap(j,j+1);
                }
            }
        }
        else if(type==3){
            //가까운
            for(int i=0;i<_size;i++){
                for(int j=0;j<_size-1;j++){
                    if(dist.get(j)>dist.get(j+1))
                        swap(j,j+1);
                }
            }
        }
        else if(type==4){
            //넓은
            for(int i=0;i<_size;i++){
                for(int j=0;j<_size-1;j++){
                    if(Integer.parseInt(saleList.get(j).getRoom_size())<Integer.parseInt(saleList.get(j+1).getRoom_size()))
                        swap(j,j+1);
                }
            }
        }
        else if(type==5){
            //좁은
            for(int i=0;i<_size;i++){
                for(int j=0;j<_size-1;j++){
                    if(Integer.parseInt(saleList.get(j).getRoom_size())>Integer.parseInt(saleList.get(j+1).getRoom_size()))
                        swap(j,j+1);
                }
            }
        }
        for(int i=0;i<_size;i++){
            Log.e("Sort",i+dist.get(i).toString());
        }
    }

}
