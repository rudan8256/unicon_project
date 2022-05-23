package com.example.unicon_project.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.unicon_project.R;
import com.example.unicon_project.Classes.SaleProduct;

import java.util.List;

public class SaleProductAdapter extends RecyclerView.Adapter<SaleProductAdapter.ViewHolder> {


    private List<SaleProduct> mDatas;
    public OnItemClickListener mListener = null;



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

       ViewHolder(View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View

            title = itemView.findViewById(R.id.post_title);

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


    public SaleProductAdapter(List<SaleProduct> mdatas) {
        this.mDatas = mdatas;
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
        viewHolder.getTextView().setText(mDatas.get(position).getHome_adress());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

}