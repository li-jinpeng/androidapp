package com.example.androidapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.androidapp.util.Global;
import com.example.androidapp.util.PostDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


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
        TextView content,user_name,date,title,thumbs;
        LinearLayout image1;
        String post_id;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            avatar = itemView.findViewById(R.id.avatar);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            user_name = itemView.findViewById(R.id.user_name);
            date = itemView.findViewById(R.id.time);
            thumbs = itemView.findViewById(R.id.thumbs);
            image1 = itemView.findViewById(R.id.image1);
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), Detail.class);
                    intent.putExtra("id",post_id);
                    view.getContext().startActivity(intent);
                }
            });
            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FormBody.Builder builder = new  FormBody.Builder()
                                        .add("id", post_id)
                                        .add("type", "1")
                                        .add("user_id", Global.user_id)
                                        .add("reply_type", "2");
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url(Global.SERVER_URL + "/operator/edit/")
                                        .post(builder.build())
                                        .build();
                                Response response = client.newCall(request).execute();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });


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
        holder.thumbs.setText(data.get(position).thumbs);
        holder.post_id = data.get(position).id;
        if (data.get(position).thumb!=0)
            holder.thumbs.setTextColor(Color.GREEN);
        else
            holder.thumbs.setTextColor(Color.BLACK);
    }

}
