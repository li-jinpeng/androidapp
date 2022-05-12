package com.example.androidapp.fragment.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.androidapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 展示信息界面1
 */
public class SelfInfoFragment extends Fragment {

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.account)
    TextView account;

    @BindView(R.id.department)
    TextView department;

    @BindView(R.id.major)
    TextView major;

    @BindView(R.id.student_number)
    TextView studentNumber;


    @BindView(R.id.phone)
    TextView phone;

    @BindView(R.id.email)
    TextView email;

    @BindView(R.id.address)
    TextView address;

    @BindView(R.id.introduction)
    TextView introduction;

    private Unbinder unbinder;

    public SelfInfoFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_self_info, container, false);
        unbinder = ButterKnife.bind(this, view);
       // setInfo();
        account.setText("ljp");
        name.setText("ljp");
        department.setText("software");
        major.setText("software");
        studentNumber.setText("2019013254");
        phone.setText("15660603091");
        email.setText("lijp9@mails.cn");
        address.setText("紫荆一号楼");
        introduction.setText("一个大三学生");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //if (getActivity() instanceof MainActivity) setInfo();
    }

    public void setInfo() {
        //Activity activity = getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

