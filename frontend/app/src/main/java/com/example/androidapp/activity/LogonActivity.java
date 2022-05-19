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
public class LogonActivity extends BaseActivity {
    /******************************
     ************ 变量 ************
     ******************************/

    /******************************
     ************ 方法 ************
     ******************************/
    @BindView(R.id.logon2_name)
    FormEditText nameEditText;

    @BindView(R.id.logon2_school)
    FormEditText schoolEditText;

    @BindView(R.id.logon1_account)
    FormEditText accountEditText;

    @BindView(R.id.logon1_password)
    FormEditText passwordEditText;

    @BindView(R.id.logon1_repeat_password)
    FormEditText repeatPasswordEditText;

    @BindView(R.id.logon)
    Button btn;

    private int lock = 0;
    private String account;
    private String password;
    private String name;
    private String intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);
        ButterKnife.bind(this);
        // 添加验证
        accountEditText.addValidator(new Valid.AccountValidator());
        accountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                accountEditText.testValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        passwordEditText.addValidator(new Valid.PasswordValidator());
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.testValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        repeatPasswordEditText.addValidator(new Valid.PasswordValidator());
        repeatPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                repeatPasswordEditText.testValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // 添加验证
        nameEditText.addValidator(new Valid.NotBlankValidator());
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameEditText.testValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        schoolEditText.addValidator(new Valid.NotBlankValidator());
        schoolEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                schoolEditText.testValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            account = accountEditText.getText().toString();
                            password = passwordEditText.getText().toString();
                            name = nameEditText.getText().toString();
                            intro = schoolEditText.getText().toString();
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("name", name)
                                    .add("password", password)
                                    .add("account",account)
                                    .add("intro",intro);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/user/init/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            btn.setText("注册完成");
                            Intent intent = new Intent(LogonActivity.this,LoginActivity.class);
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
                Intent intent = new Intent(LogonActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

    }


}
