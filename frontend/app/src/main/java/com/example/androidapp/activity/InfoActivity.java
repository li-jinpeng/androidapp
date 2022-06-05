package com.example.androidapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import com.bumptech.glide.Glide;
import com.example.androidapp.R;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidapp.adapter.HomeAdapter;
import com.example.androidapp.adapter.HomepagePagerAdapter;
import com.example.androidapp.fragment.main.DashboardFragment;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.HomeDetail;
import com.example.androidapp.util.PostDetail;
import com.example.androidapp.util.visit_detail;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity {

    private HomepagePagerAdapter pagerAdapter;
    private HomeDetail info;
    private Handler handler;
    private InfoActivity that = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        show();
        TextView title=findViewById(R.id.visit_homepage_title1);
        title.setText("个人主页");
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ImageView imgAvatar = findViewById(R.id.img_avatar);
        TextView name=findViewById(R.id.homepage_name);
        TextView numFocus=findViewById(R.id.num_focus);
        TextView numFocused=findViewById(R.id.num_focused);
        ViewPager viewPager=findViewById(R.id.view_pager);
        tabLayout.addTab(tabLayout.newTab().setText("个人动态"));
        tabLayout.addTab(tabLayout.newTab().setText("个人信息"));
        tabLayout.setBackgroundColor(Color.WHITE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                JCVideoPlayer.releaseAllVideos();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });



        handler = new Handler(){
            @Override
            public  void handleMessage(Message msg){
                switch (msg.what){
                    case 1:
                        try {
                            URL url = new URL(info.ava);
                            Glide.with(that).load(url).into(imgAvatar);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        numFocus.setText(info.follow);
                        numFocused.setText(info.followed);
                        name.setText(info.name);
                        pagerAdapter = new HomepagePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), info,Global.user_id);
                        viewPager.setAdapter(pagerAdapter);
                        break;
                    default: break;
                }
            }
        };


    }

    public void show(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder()
                            .add("id",Global.user_id);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.SERVER_URL + "/user/get/home/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    info = gson.fromJson(responseData,new TypeToken<HomeDetail>(){}.getType());
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


}