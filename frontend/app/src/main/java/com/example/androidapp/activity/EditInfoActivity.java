package com.example.androidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.andreabaccega.widget.FormEditText;
import com.example.androidapp.R;
import com.example.androidapp.util.Global;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 更改信息
 */
public class EditInfoActivity extends BaseActivity {


    @BindView(R.id.edit_name)
    FormEditText name;

    @BindView(R.id.edit_department)
    FormEditText dep;

    @BindView(R.id.edit_signature)
    FormEditText signature;

    @BindView(R.id.edit_student_number)
    FormEditText num;

    @BindView(R.id.edit_phone)
    FormEditText phone;

    @BindView(R.id.edit_address)
    FormEditText address;

    @BindView(R.id.edit_intro)
    FormEditText intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        TextView btn1 = findViewById(R.id.returnButton1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditInfoActivity.this,MainActivity.class);
                intent.putExtra("message","info");
                startActivity(intent);
            }
        });

        TextView btn = findViewById(R.id.edit_info_save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String name_ = name.getText().toString();
                            String dep_ = dep.getText().toString();
                            String signature_ = signature.getText().toString();
                            String num_ = num.getText().toString();
                            String phone_ = phone.getText().toString();
                            String address_ = address.getText().toString();
                            String intro_ = intro.getText().toString();
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("id", Global.user_id)
                                    .add("name", name_)
                                    .add("signature", signature_)
                                    .add("dep", dep_)
                                    .add("num", num_)
                                    .add("intro", intro_)
                                    .add("phone", phone_)
                                    .add("address", address_);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/user/edit/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            btn.setText("成功");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }


}
