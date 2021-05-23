package com.cqut.market.presenter;

import androidx.annotation.Nullable;

import com.cqut.market.view.BaseView;

public abstract class BasePresenter<V extends BaseView> {
    public V getView() {
        return view;
    }

    private V view;
    public void attachView(@Nullable V view){
        this.view=view;
    }
    public void disAttachView(){
        if (view!=null)
            view=null;
    }
}
