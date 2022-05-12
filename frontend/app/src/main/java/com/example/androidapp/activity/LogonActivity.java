package com.example.androidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.andreabaccega.widget.FormEditText;
import com.example.androidapp.R;
import com.example.androidapp.entity.OptionItems;
import com.example.androidapp.request.user.LogonRequest;
import com.example.androidapp.util.Hint;
import com.example.androidapp.util.Valid;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    }

    @OnClick(R.id.logon)
    public void clickLogon() {
        Log.d("logon", "注册成功");
        return;
    }

    /******************************
     ************ 事件 ************
     ******************************/
    @OnClick(R.id.returnButton)
    public void onClickReturnToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
