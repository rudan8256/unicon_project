package com.example.unicon_project.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.unicon_project.Classes.PurchaseProduct;
import com.example.unicon_project.Classes.SaleProduct;
import com.example.unicon_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PurchaseProductAdapter extends RecyclerView.Adapter<PurchaseProductAdapter.ViewHolder> {

    private List<PurchaseProduct> mDatas;
    public OnItemClickListener mListener = null;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    private Context mContext = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, deposit, monthCostMin, monthCostMax, roomSizeMin, roomSizeMax, structure, address, living_start, living_end;

        ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.post_title);
            deposit = itemView.findViewById(R.id.post_deposit);
            monthCostMin = itemView.findViewById(R.id.post_month_cost_min);
            monthCostMax = itemView.findViewById(R.id.post_month_cost_max);
            roomSizeMin = itemView.findViewById(R.id.post_roomSize_min);
            roomSizeMax = itemView.findViewById(R.id.post_roomSize_max);
            structure = itemView.findViewById(R.id.post_structure);
            address = itemView.findViewById(R.id.post_address);
            living_end = itemView.findViewById(R.id.post_living_end);
            living_start = itemView.findViewById(R.id.post_living_start);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(view, pos);
                        }
                    }
                }
            });
        }

        public TextView getTextView() {
            return title;
        }
    }

    public PurchaseProductAdapter(List<PurchaseProduct> mdatas, Context context) {
        this.mDatas = mdatas;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_purchaseproduct, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (mDatas.get(position).getMonth_rent()) {
            viewHolder.title.setText("월세");
        } else if (mDatas.get(position).getDeposit()) {
            viewHolder.title.setText("전세");
        } else viewHolder.title.setText("가격협상가능");

        viewHolder.deposit.setText(mDatas.get(position).getDeposit_price_max());
        viewHolder.roomSizeMin.setText(mDatas.get(position).getRoom_size_min());
        viewHolder.roomSizeMax.setText(mDatas.get(position).getRoom_size_max());
        viewHolder.structure.setText(mDatas.get(position).getStructure());
        viewHolder.address.setText(mDatas.get(position).getHome_address());
        viewHolder.living_start.setText(mDatas.get(position).getLive_period_start());
        viewHolder.living_end.setText(mDatas.get(position).getLive_period_end());
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) return 0;

        return mDatas.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setmDatas(List<PurchaseProduct> mDatas) {
        this.mDatas = mDatas;
    }
}
