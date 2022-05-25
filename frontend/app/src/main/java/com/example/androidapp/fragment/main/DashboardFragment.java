package com.example.androidapp.fragment.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.androidapp.R;
import com.example.androidapp.activity.ChangePasswordActivity;
import com.example.androidapp.activity.EditInfoActivity;
import com.example.androidapp.activity.user_list;
import com.example.androidapp.adapter.HomepagePagerAdapter;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.HomeDetail;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 主界面主页子页
 */
public class DashboardFragment
        extends Fragment {

    private static final int REQUEST_CODE_CHOOSE = 11;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.homepage_name)
    TextView name;
    @BindView(R.id.num_focus)
    TextView numFocus;
    @BindView(R.id.num_focused)
    TextView numFocused;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.btn_edit)
    Button button;
    @BindView(R.id.btn_change_password)
    Button button1;
    @BindView(R.id.visit_homepage_title)
    TextView title;
    @BindView(R.id.visit_homepage_appbar)
    AppBarLayout app_bar;
    @BindView(R.id.left)
    LinearLayout left;
    @BindView(R.id.right)
    LinearLayout right;

    private HomepagePagerAdapter pagerAdapter;
    private int lock = 0;
    private HomeDetail info;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, root);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditInfoActivity.class);
                startActivity(intent);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });


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
                    lock = 1;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        while (lock ==0){
            continue;
        }

        try {
            URL url = new URL(info.ava);
            Glide.with(this).load(url).into(imgAvatar);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        title.setText("我的个人主页");
        numFocus.setText(info.follow);
        numFocused.setText(info.followed);
        name.setText(info.name);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), user_list.class);
                intent.putExtra("type","关注");
                getContext().startActivity(intent);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), user_list.class);
                intent.putExtra("type","被关注");
                getContext().startActivity(intent);
            }
        });



        tabLayout.addTab(tabLayout.newTab().setText("个人信息"));
        tabLayout.addTab(tabLayout.newTab().setText("个人动态"));
        tabLayout.setBackgroundColor(Color.WHITE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new HomepagePagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(),info);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(2);

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

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

}
