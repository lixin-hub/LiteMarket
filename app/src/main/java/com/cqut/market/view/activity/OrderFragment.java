package com.cqut.market.view.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cqut.market.R;
import com.cqut.market.beans.Order;
import com.cqut.market.view.CustomView.CheckOrderListAdapter;

import java.util.ArrayList;


public class OrderFragment extends Fragment {
    ArrayList<Order> orders;

    public OrderFragment(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_order_info, container, false);
        View image = view.findViewById(R.id.fragment_mine_order_info_image);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_mine_order_recycler_view);
        CheckOrderListAdapter adapter = new CheckOrderListAdapter(getContext(), orders);
        LinearLayoutManager manager = new LinearLayoutManager(this.getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        if (orders.size()==0){
            image.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
