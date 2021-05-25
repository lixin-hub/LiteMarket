package com.cqut.market.view.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cqut.market.R;
import com.cqut.market.beans.Good;
import com.cqut.market.view.CustomView.GoodListAdapter;

import java.util.ArrayList;

public class CategoryFragment extends Fragment implements View.OnClickListener {
    StaggeredGridLayoutManager gridLayoutManager;
    RecyclerView recyclerView;
    ArrayList<Good> allGoods;
    GoodListAdapter goodListAdapter;
    public CategoryFragment(ArrayList<Good> allGoods) {
        this.allGoods = allGoods;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_type_item, container, false);
        recyclerView = view.findViewById(R.id.type_recycler);
         goodListAdapter = new GoodListAdapter(getContext(), allGoods);
        gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        MainActivity.BottomOffsetDecoration bottomOffsetDecoration = new MainActivity.BottomOffsetDecoration(350);
        recyclerView.addItemDecoration(bottomOffsetDecoration);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(goodListAdapter);
        goodListAdapter.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        ((MainActivity) getActivity()).onClick(v);
    }
    public void notifyData(){
        if (goodListAdapter!=null)
        goodListAdapter.notifyItemRangeChanged(0, allGoods.size());
    }
    public void smoothScrollToPosition(int index){
        if (recyclerView!=null)
        recyclerView.smoothScrollToPosition(index);
    }

}
