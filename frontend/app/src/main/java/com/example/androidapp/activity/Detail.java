package com.example.androidapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.androidapp.R;
import com.example.androidapp.adapter.HomeAdapter;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.PostDetail;
import com.example.androidapp.util.PostDetailDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Detail extends AppCompatActivity {

    private String id;
    private TextView name,date,text;
    private CircleImageView ava;
    private String responseData;
    private Gson gson;
    private PostDetailDetail postInfo;
    private int lock = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        setContentView(R.layout.activity_detail);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        text = findViewById(R.id.text);
        ava = findViewById(R.id.imageButton);
        //lock = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder().add("id", id).add("user_id", Global.user_id);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.SERVER_URL + "/post/detail/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    postInfo = gson.fromJson(responseData,new TypeToken<PostDetailDetail>(){}.getType());
                    name.setText(postInfo.sendname);
                    date.setText(postInfo.date);
                    text.setText(postInfo.text);
                    //homeAdapter = new HomeAdapter(getActivity(),post_list);
                    //recycleView.setAdapter(homeAdapter);
                    //lock = 1;
                }catch (Exception e){

                    e.printStackTrace();
                }
            }
        }).start();
        /*while (lock==0)
            continue;
        Glide.with(this).load(postInfo.ava).into(ava);*/
        GridView gridView = (GridView) findViewById(R.id.GridView);
        ArrayList<HashMap<String,Object>> meumList = new ArrayList<HashMap<String, Object>>();
        for(int i = 1; i < 3; i++){
            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("ItemImage",R.drawable.ic_photoholder);
            map.put("ItemText",""+i);
            meumList.add(map);
        }
        // 构建数据适配器
        SimpleAdapter saItem = new SimpleAdapter(this,
                meumList,
                R.layout.item,
                new String[]{"ItemImage","ItemText"},
                new int[]{R.id.ItemImage,R.id.ItemText});
        // 应用适配器
        gridView.setAdapter(saItem);

        GridView gridView1 = (GridView) findViewById(R.id.com);
        ArrayList<HashMap<String,Object>> meumList1 = new ArrayList<HashMap<String, Object>>();

        for(int i = 1; i < 4; i++){
            HashMap<String,Object> map1 = new HashMap<String, Object>();
            map1.put("imageButton",R.drawable.ic_avatarholder);
            map1.put("com_name","评论者"+i);
            map1.put("com_date","2022-5-18 11:20");
            map1.put("com_text","第"+i+"条评论最近在 android 项目中，遇到需要动态 设置GridView 列数的问题，实现如下");
            meumList1.add(map1);
        }
        // 构建数据适配器
        SimpleAdapter saItem1 = new SimpleAdapter(this,
                meumList1,
                R.layout.item1,
                new String[]{"imageButton","com_name","com_date","com_text"},
                new int[]{R.id.imageButton,R.id.com_name,R.id.com_date,R.id.com_text});
        // 应用适配器
        gridView1.setAdapter(saItem1);

    }

}