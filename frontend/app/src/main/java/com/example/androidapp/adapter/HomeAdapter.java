package com.example.androidapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidapp.R;
import com.example.androidapp.activity.Detail;
import com.example.androidapp.fragment.homepage.SelfInfoFragment;
import com.example.androidapp.fragment.main.HomeFragment;
import com.example.androidapp.util.PostDetail;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * 主页ViewPager适配器
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    private List<PostDetail> data;
    private Context context;
    private View inflater;
    /*构造函数*/
    public HomeAdapter(Context context, List<PostDetail> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder，绑定每一项的布局为item
        inflater= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        MyViewHolder holder = new MyViewHolder(inflater);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Detail.class);
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        bindView(holder,position);
        //通过点击改变状态

    }

    @Override
    public int getItemCount() {
        //返回数据总条数
        return data.size();
    }

    //内部类，绑定控件
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image,avatar;
        TextView content,user_name,date,title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            avatar = itemView.findViewById(R.id.avatar);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            user_name = itemView.findViewById(R.id.user_name);
            date = itemView.findViewById(R.id.time);

        }
    }

    //自定义方法，用于绑定数据
    public void bindView(@NonNull MyViewHolder holder, int position){
        String imageUrl = data.get(position).imagePath;
        Glide.with(context).load(imageUrl).into(holder.image);
        imageUrl = data.get(position).avatar;
        Glide.with(context).load(imageUrl).into(holder.avatar);
        holder.title.setText("<"+data.get(position).title+">");
        holder.content.setText(data.get(position).content);
        holder.user_name.setText(data.get(position).sender);
        holder.date.setText(data.get(position).time);
    }

}
