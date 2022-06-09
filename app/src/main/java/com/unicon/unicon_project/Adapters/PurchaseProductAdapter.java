package com.unicon.unicon_project.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.unicon.unicon_project.Classes.PurchaseProduct;
import com.unicon.unicon_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        private TextView title, deposit, monthCost, roomSize, structure, address, living, negotiable;

        ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.post_title);
            deposit = itemView.findViewById(R.id.post_deposit);
            monthCost = itemView.findViewById(R.id.post_month_cost);
            roomSize = itemView.findViewById(R.id.post_roomSize);
            structure = itemView.findViewById(R.id.post_structure);
            address = itemView.findViewById(R.id.post_address);
            living = itemView.findViewById(R.id.post_living);
            negotiable = itemView.findViewById(R.id.post_negotiable);

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (mDatas.get(position).getMonth_rent()) {
            viewHolder.title.setText("월세");
        } else if (mDatas.get(position).getDeposit()) {
            viewHolder.title.setText("전세");
        }
        if (!mDatas.get(position).getNegotiable()) {
            viewHolder.negotiable.setVisibility(View.GONE);
        }

        viewHolder.deposit.setText(mDatas.get(position).getDeposit_price_max() + "/");
        viewHolder.monthCost.setText(mDatas.get(position).getMonth_price_min() + "~" + mDatas.get(position).getMonth_price_max());
        viewHolder.roomSize.setText(mDatas.get(position).getRoom_size_min() + "㎡~" + mDatas.get(position).getRoom_size_max() + "㎡");
        viewHolder.structure.setText(mDatas.get(position).getStructure());
        viewHolder.address.setText(mDatas.get(position).getHome_address());
        viewHolder.living.setText(mDatas.get(position).getLive_period_start() + " ~ " + mDatas.get(position).getLive_period_end());
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
