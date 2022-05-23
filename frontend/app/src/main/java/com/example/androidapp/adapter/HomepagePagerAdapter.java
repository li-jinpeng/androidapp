package com.example.androidapp.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.androidapp.activity.visit;
import com.example.androidapp.fragment.homepage.SelfInfoFragment;
import com.example.androidapp.fragment.main.HomeFragment;
import com.example.androidapp.util.HomeDetail;

import org.jetbrains.annotations.NotNull;


/**
 * 主页ViewPager适配器
 */
public class HomepagePagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    HomeDetail info;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public HomepagePagerAdapter(@NonNull FragmentManager fm, int NumOfTabs, HomeDetail info) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.info = info;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //return new HomeFragment();
                return new SelfInfoFragment(info);
            default:
                return new HomeFragment();
                //return new StudyInfoFragment();
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @NotNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
