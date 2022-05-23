package com.example.androidapp.activity;

import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.androidapp.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidapp.adapter.HomeAdapter;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.PostDetail;
import com.example.androidapp.util.visit_detail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class visit extends AppCompatActivity {
private visit_detail info;
private int lock = 0;
private Handler handler;
private visit that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        Intent intent = getIntent();
        String the_id = intent.getStringExtra("id");

        lock = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder()
                            .add("id",the_id).add("user_id",Global.user_id);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.SERVER_URL + "/user/get/detail/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    info = gson.fromJson(responseData,new TypeToken<visit_detail>(){}.getType());
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("user_id", Global.user_id)
                                    .add("follow_id",the_id);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/user/follow/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            Message msg = new Message();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        handler = new Handler(){ //创建Handler
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){ //区分不同的消息，对不同进度条组件执行操作
                    case 1:

                        CircleImageView image = findViewById(R.id.img_avatar);
                        try {
                            URL url = new URL(info.ava);
                            Glide.with(that).load(url).into(image);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        TextView name = findViewById(R.id.name_);
                        TextView name2 = findViewById(R.id.name);
                        name.setText(info.name);
                        name2.setText(info.name);
                        TextView acc = findViewById(R.id.account);
                        acc.setText(info.account);
                        TextView intro = findViewById(R.id.introduction);
                        intro.setText(info.intro);
                        btn.setText(info.follow);
                        break;
                    case 2:
                        Log.d("", "进来了");
                        if(btn.getText().toString().equals("已关注"))
                            btn.setText("未关注");
                        else{
                            btn.setText("已关注");
                        }
                    default:
                        break;
                }
            }
        };



    }


}