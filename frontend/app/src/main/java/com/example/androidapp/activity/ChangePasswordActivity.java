package com.example.androidapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.andreabaccega.widget.FormEditText;
import com.example.androidapp.R;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.Valid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity {


    @BindView(R.id.logon1_password)
    FormEditText passwordEditText;

    @BindView(R.id.logon)
    Button btn;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        // 添加验证
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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            password = passwordEditText.getText().toString();
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("password", password)
                                    .add("id",Global.user_id);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/user/change_password/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            btn.setText("修改成功");
                            Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);
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
                Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);
                intent.putExtra("message","info");
                startActivity(intent);
            }
        });

    }
}