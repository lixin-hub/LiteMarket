package com.cqut.market.view.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cqut.market.R;
import com.cqut.market.view.CustomView.ExpressViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class ExpressFragment extends Fragment {

    private ViewPager2 viewPager2;
    private ExpressViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_express, container, false);
        viewPager2 = view.findViewById(R.id.fragment_express_viewpager);
        tabLayout=view.findViewById(R.id.fragment_express_tab);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("休闲零食");
        strings.add("早餐素食");
        strings.add("生活用品");
        strings.add("快递代取");
        strings.add("酒水饮料");
        strings.add("泡面热食");

        viewPagerAdapter = new ExpressViewPagerAdapter(strings, this.getContext());
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(strings.get(position));
            }
        }).attach();
        return view;
    }

}
