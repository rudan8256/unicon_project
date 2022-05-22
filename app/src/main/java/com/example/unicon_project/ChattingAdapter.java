package com.example.unicon_project;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChattingAdapter extends BaseAdapter {
    ArrayList<ChattingData> items;
    String uid;
    Context context;

    public ChattingAdapter(ArrayList<ChattingData> items, String uid, Context context){
        this.items = items;
        this.context = context;
        this.uid = uid;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_chatting, viewGroup, false);

        TextView tv_chatting_msg = view.findViewById(R.id.tv_chatting_msg);
        tv_chatting_msg.setText(items.get(i).msg);

        TextView tv_chatting_time = view.findViewById(R.id.tv_chatting_time);
        tv_chatting_time.setText(items.get(i).time);

        TextView tv_chatting_id = view.findViewById(R.id.tv_chatting_id);
        if(items.get(i).uid.equals(this.uid))
        {
            tv_chatting_id.setText("나");
            tv_chatting_id.setGravity(Gravity.RIGHT);
            tv_chatting_msg.setGravity(Gravity.RIGHT);
            tv_chatting_time.setGravity(Gravity.RIGHT);
        }else{
            tv_chatting_id.setText("상대방");
            tv_chatting_id.setGravity(Gravity.LEFT);
            tv_chatting_msg.setGravity(Gravity.LEFT);
            tv_chatting_time.setGravity(Gravity.LEFT);
        }

        return view;
    }
}

