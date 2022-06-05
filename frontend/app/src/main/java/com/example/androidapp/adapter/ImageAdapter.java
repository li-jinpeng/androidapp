package com.example.androidapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidapp.R;
import com.example.androidapp.util.Global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private Context context;
    private List<String> data;
    private View inflater;
    private String t;
    public ImageAdapter(Context context,List<String> urls) {
        this.context = context;
        t = urls.get(0);
        this.data = Arrays.asList(t.split("[;]"));

    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder，绑定每一项的布局为item
        inflater= LayoutInflater.from(context).inflate(R.layout.pic_item,parent,false);
        MyViewHolder holder = new MyViewHolder(inflater);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        bindView(holder,position);
    }

    public int getItemCount() {
        //返回数据总条数
        if (data.get(0).equals(""))
            return 0;
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.ItemImage);
        }
    }

    public void bindView(@NonNull MyViewHolder holder, int position) {
        String imageUrl = data.get(position);
        Glide.with(context).load(imageUrl).into(holder.img);
    }
}
