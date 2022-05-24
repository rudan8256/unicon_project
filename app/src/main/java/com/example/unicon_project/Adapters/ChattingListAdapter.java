package com.example.unicon_project.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unicon_project.ChattingActivity;
import com.example.unicon_project.Classes.ChattingListData;
import com.example.unicon_project.R;

import java.util.ArrayList;

public class ChattingListAdapter extends BaseAdapter {
    ArrayList<ChattingListData> items;
    String uid;
    Context context;

    public ChattingListAdapter(ArrayList<ChattingListData> items, String uid, Context context){
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
        view = inflater.inflate(R.layout.item_chatting_list, viewGroup, false);

        TextView tv_chattingList_chattingname = view.findViewById(R.id.tv_chattingList_chattingname);
        tv_chattingList_chattingname.setText(items.get(i).getChattingName());

        TextView tv_chattingList_username = view.findViewById(R.id.tv_chattingList_username);
        tv_chattingList_username.setText(items.get(i).getUserName());

        return view;
    }

}
