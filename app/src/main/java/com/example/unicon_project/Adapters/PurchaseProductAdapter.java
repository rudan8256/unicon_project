package com.example.unicon_project.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.unicon_project.Classes.PurchaseProduct;
import com.example.unicon_project.R;

import java.util.List;

public class PurchaseProductAdapter extends RecyclerView.Adapter<PurchaseProductAdapter.ViewHolder> {

    private List<PurchaseProduct> mDatas;
    public OnItemClickListener mListener = null;


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.post_title);

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

    public PurchaseProductAdapter(List<PurchaseProduct> mdatas) {
        this.mDatas = mdatas;
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
        viewHolder.getTextView().setText(mDatas.get(position).getHome_address());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}
