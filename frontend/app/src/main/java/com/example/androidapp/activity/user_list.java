package com.example.androidapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.androidapp.R;
import com.example.androidapp.adapter.UserListAdapter;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.visit_detail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.os.Handler;
import android.os.Message;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class user_list extends AppCompatActivity {
    private int type;
    private UserListAdapter useradapter;
    private user_list that = this;
    private List<visit_detail> post_list;
    private RecyclerView mRecyclerView;
    private UserListAdapter mAdapter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        if(intent.getStringExtra("type").equals("关注")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FormBody.Builder builder = new  FormBody.Builder().add("id", Global.user_id);
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(Global.SERVER_URL + "/user/follows/")
                                .post(builder.build())
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Gson gson = new Gson();
                        post_list = gson.fromJson(responseData,new TypeToken<List<visit_detail>>(){}.getType());
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();}
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FormBody.Builder builder = new  FormBody.Builder().add("id", Global.user_id);
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(Global.SERVER_URL + "/user/followeds/")
                                .post(builder.build())
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Log.d("TAG", "进入被关注"+responseData);
                        Gson gson = new Gson();
                        post_list = gson.fromJson(responseData,new TypeToken<List<visit_detail>>(){}.getType());
                        Message msg = new Message();
                        msg.what = 2;
                        handler.sendMessage(msg);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }
        TextView text = findViewById(R.id.title);
        handler = new Handler(){ //创建Handler
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){ //区分不同的消息，对不同进度条组件执行操作
                    case 1:
                        mRecyclerView = findViewById(R.id.recycleView);
                        mAdapter = new UserListAdapter(that,post_list );
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(that));
                        text.setText("关注列表");
                        break;
                    case 2:
                        mRecyclerView = findViewById(R.id.recycleView);
                        mAdapter = new UserListAdapter(that,post_list );
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(that));
                        text.setText("粉丝列表");
                        break;
                    default:
                        break;
                }
            }
        };
    }
}