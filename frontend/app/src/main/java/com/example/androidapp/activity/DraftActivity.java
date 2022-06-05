package com.example.androidapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.adapter.DraftAdapter;
import com.example.androidapp.adapter.HomeAdapter;
import com.example.androidapp.util.DraftDetail;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.PostDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DraftActivity extends AppCompatActivity {
    private DraftAdapter draftAdapter;
    private Unbinder unbinder;
    private String host = "http://101.43.128.148:9999";
    private String responseData;
    private Gson gson;
    private List<DraftDetail> post_list;

    Handler handler;
    Runnable runnable;
    @BindView(R.id.rcv_draft)
    RecyclerView recycleView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);
        unbinder = ButterKnife.bind(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recycleView.setLayoutManager(manager);
        runnable = new Runnable() {
            public void run() {
                draftAdapter = new DraftAdapter(DraftActivity.this,post_list);
                recycleView.setAdapter(draftAdapter);
            }
        };
        Init();
        handler = new Handler();
    }
    @OnClick(R.id.top_draft)
    public void onClickReturnTo() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("message", "public");
        startActivityForResult(intent, 1);
    }
    protected void Init(){
        Thread t = new Thread (new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody builder = new  FormBody.Builder()
                            .add("user_id", Global.user_id)
                            .build();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(host + "/draft/get/")
                            .post(builder)
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    Log.e("drafts",responseData);
                    gson = new Gson();
                    post_list = gson.fromJson(responseData,new TypeToken<List<DraftDetail>>(){}.getType());


                }catch (Exception e){
                    e.printStackTrace();
                }
                handler.post(runnable);
            }
        });
        t.start();
    }



}

