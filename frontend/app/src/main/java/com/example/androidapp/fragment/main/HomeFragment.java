package com.example.androidapp.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.adapter.HomeAdapter;
import com.example.androidapp.util.PostDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.FormBody;

public class HomeFragment extends Fragment {
    private HomeAdapter homeAdapter;
    private Unbinder unbinder;
    private Spinner spinner;
    private String spinner_content;
    private List<PostDetail> post_list;
    private List<PostDetail> temp_list;
    private RecyclerView recycleView;
    private EditText editText;
    private String temp;
    private String responseData;
    private Gson gson;
    private int lock;
    private String host = "http://101.43.128.148:8000";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);
        TextView text = root.findViewById(R.id.selectText);
        recycleView = root.findViewById(R.id.recycleView);
        editText = root.findViewById(R.id.search_view);
        spinner = root.findViewById(R.id.orderSpinner);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recycleView.setLayoutManager(manager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(host + "/post/index/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    post_list = gson.fromJson(responseData,new TypeToken<List<PostDetail>>(){}.getType());
                    homeAdapter = new HomeAdapter(getActivity(),post_list);
                    recycleView.setAdapter(homeAdapter);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = editText.getText().toString();
                lock = 0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            spinner_content = spinner.getSelectedItem().toString();
                            FormBody.Builder builder = new  FormBody.Builder().add("search", temp).add("type", spinner_content);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(host + "/operator/search/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            responseData = response.body().string();
                            gson = new Gson();
                            temp_list = gson.fromJson(responseData,new TypeToken<List<PostDetail>>(){}.getType());
                            lock = 1;

                        }catch (Exception e){
                            e.printStackTrace();
                            text.setText(responseData);
                        }
                    }
                }).start();
                while(lock==0)
                {
                    continue;
                }
                post_list.clear();
                for (int i = 0;i<temp_list.size();i++)
                {
                    post_list.add(temp_list.get(i));
                }
                homeAdapter.notifyDataSetChanged();
            }
        });

        return root;
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

    public void Search_post(View view){

    }

}
