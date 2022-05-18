package com.example.androidapp.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.androidapp.R;
import com.example.androidapp.activity.PublicActivity;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class HomeFragment extends Fragment {

    private Unbinder unbinder;
    @BindView(R.id.imageButton)
    ImageView imageview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);
        Initview();
        return root;
    }

    private void Initview(){
        this.registerForContextMenu(imageview);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_publishmenu,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //该方法对菜单的item进行监听
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1: //发布动态
                Intent intent = new Intent();
                intent.setClass(getActivity(), PublicActivity.class);
                startActivity(intent);
                break;
            case R.id.menu2:
                Log.v("2","2");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
