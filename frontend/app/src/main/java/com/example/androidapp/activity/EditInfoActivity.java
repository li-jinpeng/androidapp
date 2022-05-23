package com.example.androidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.example.androidapp.R;
import com.example.androidapp.entity.OptionItems;
import com.example.androidapp.request.user.LogonRequest;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.Hint;
import com.example.androidapp.util.PostDetail;
import com.example.androidapp.util.Valid;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 注册
 */
public class EditInfoActivity extends BaseActivity {
    /******************************
     ************ 变量 ************
     ******************************/

    /******************************
     ************ 方法 ************
     ******************************/
    @BindView(R.id.logon2_name)
    FormEditText nameEditText;

    @BindView(R.id.logon2_school)
    FormEditText intro_;

    @BindView(R.id.logon)
    Button btn;

    private String name;
    private String intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        ButterKnife.bind(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            name = nameEditText.getText().toString();
                            intro = intro_.getText().toString();
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("id",Global.user_id)
                                    .add("name", name)
                                    .add("intro",intro);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/user/edit/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            btn.setText("修改完成");
                            Intent intent = new Intent(EditInfoActivity.this,MainActivity.class);
                            intent.putExtra("message","info");
                            startActivity(intent);
                        }catch (Exception e){

                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        Button bt1 = findViewById(R.id.returnButton);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditInfoActivity.this,MainActivity.class);
                intent.putExtra("message","info");
                startActivity(intent);
            }
        });

    }


}
