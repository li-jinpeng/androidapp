package com.example.androidapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.andreabaccega.widget.FormEditText;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.androidapp.R;
import com.example.androidapp.entity.ApplicationInfo;
import com.example.androidapp.entity.OptionItems;
import com.example.androidapp.entity.RecruitmentInfo;
import com.example.androidapp.entity.ShortProfile;
import com.example.androidapp.request.follow.GetFanlistRequest;
import com.example.androidapp.request.follow.GetWatchlistRequest;
import com.example.androidapp.request.intention.GetApplyIntentionDetailRequest;
import com.example.androidapp.request.intention.GetApplyIntentionRequest;
import com.example.androidapp.request.intention.GetRecruitIntentionDetailRequest;
import com.example.androidapp.request.intention.GetRecruitIntentionRequest;
import com.example.androidapp.request.user.GetInfoPictureRequest;
import com.example.androidapp.request.user.GetInfoPlusRequest;
import com.example.androidapp.request.user.GetInfoRequest;
import com.example.androidapp.request.user.LoginRequest;
import com.example.androidapp.util.BasicInfo;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.Hint;
import com.example.androidapp.util.LoginCache;
import com.example.androidapp.util.Valid;
import com.rubengees.introduction.IntroductionBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {
    /******************************
     ************ 变量 ************
     ******************************/

    @BindView(R.id.login_account)
    FormEditText accountEditText;

    @BindView(R.id.login_password)
    FormEditText passwordEditText;

    IntroductionBuilder introductionBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        // 权限申请
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        boolean hasLogin = sharedPreferences.getBoolean("hasLogin", false);
        if (hasLogin) {
            String type = sharedPreferences.getString("type", "");
            String account = sharedPreferences.getString("account", "");
            String password = sharedPreferences.getString("password", "");
            if (type.equals("") || account.equals("") || password.equals("")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hasLogin", false);
                editor.commit();
            } else {
                Global.INTRO = false;
                Hint.startActivityLoad(this);
                //new LoginRequest(this.handleLogin, type, account, password).send();
            }
        }

        // 引导页设置
        if (Global.INTRO) {
            introductionBuilder = new IntroductionBuilder(this);
            introductionBuilder.withSlides(Hint.generateSlides()).introduceMyself();
            Global.INTRO = false;
        }

    }

    /******************************
     ************ 事件 ************
     ******************************/
    @OnClick(R.id.introduce)
    public void onClickIntroduce() {
        introductionBuilder = new IntroductionBuilder(this);
        introductionBuilder.withSlides(Hint.generateSlides()).introduceMyself();
    }


    @OnClick(R.id.login)
    public void onClickLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.logon)
    public void onClickLogon() {
        Intent intent = new Intent(this, LogonActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
