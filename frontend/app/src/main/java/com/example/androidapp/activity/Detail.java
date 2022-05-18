package com.example.androidapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.androidapp.R;

public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        GridView gridView = (GridView) findViewById(R.id.GridView);
        TextView text = findViewById(R.id.text);
        text.setText("  最近在 android 项目中，遇到需要动态 设置GridView 列数的问题，实现如下： 首先大体估算一下 item宽度.");
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