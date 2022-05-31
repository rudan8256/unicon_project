package com.example.unicon_project.Adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unicon_project.ImageViewpager;
import com.example.unicon_project.R;
import com.example.unicon_project.Classes.SaleProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SaleProductAdapter extends RecyclerView.Adapter<SaleProductAdapter.ViewHolder> {


    private List<SaleProduct> mDatas;
    public OnItemClickListener mListener = null;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private ArrayList<String > image_urllist;
    private ArrayList<Uri> uriList = new ArrayList<Uri>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    private Context mContext = null ;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title,deposit,monthcost,roomsize,structure,address,living_start,living_end;
        private ImageView house_imgview;

       ViewHolder(View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View

            title = itemView.findViewById(R.id.post_title);
            deposit = itemView.findViewById(R.id.post_deposit);
            monthcost = itemView.findViewById(R.id.post_monthcost);
            roomsize = itemView.findViewById(R.id.post_roomsize);
            structure = itemView.findViewById(R.id.post_structure);
            address = itemView.findViewById(R.id.post_address);
            living_end = itemView.findViewById(R.id.post_living_end);
            living_start=itemView.findViewById(R.id.post_living_start);
            house_imgview=itemView.findViewById(R.id.house_imgview);


            house_imgview.setClipToOutline(true);


           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   int pos = getAdapterPosition() ;
                   if (pos != RecyclerView.NO_POSITION) {
                       if(mListener != null){
                           mListener.onItemClick(v, pos);

                       }
                   }
               }
           });

        }

        public TextView getTextView() {
            return title;
        }
    }


    public SaleProductAdapter(List<SaleProduct> mdatas, Context context) {
        this.mDatas = mdatas;
        this.mContext = context;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_saleproduct, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if(mDatas.get(position).getMonth_rent()){  viewHolder.title.setText("월세");}
        else{ viewHolder.title.setText("전세"); }

        viewHolder.deposit.setText(mDatas.get(position).getDeposit_price());
        viewHolder.monthcost.setText(" / "+mDatas.get(position).getMonth_rent_price());
        viewHolder.roomsize.setText(mDatas.get(position).getRoom_size());
        viewHolder.structure.setText(mDatas.get(position).getStructure());
        viewHolder.address.setText(mDatas.get(position).getHome_name());
        viewHolder.living_start.setText(mDatas.get(position).getLive_period_start());
        viewHolder.living_end.setText("~"+mDatas.get(position).getLive_period_end());


        if(mDatas.get(position).getImages_url().size()>0) {
            String imagedata = mDatas.get(position).getImages_url().get(0);

            viewHolder.house_imgview.setPadding(0,0,0,0);

            //StorageReference submitImage = storageReference.child("post_image/" + image_url + ".jpg");
            storageReference.child("post_image/" + imagedata + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Glide.with(mContext)
                            .load(uri)
                            .into( viewHolder.house_imgview);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 실패
                }
            });
        }


    }

    private void Image_Load(String imagedata){



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDatas==null)return 0;

        return mDatas.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public void setmDatas(List<SaleProduct> mDatas) {
        this.mDatas = mDatas;
    }
}