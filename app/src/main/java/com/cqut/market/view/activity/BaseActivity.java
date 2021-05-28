package com.cqut.market.view.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cqut.market.model.Constant;
import com.cqut.market.model.NetWorkUtil;
import com.cqut.market.presenter.BasePresenter;
import com.cqut.market.view.BaseView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity {
    private P presenter;
    private V view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null)
            presenter = createPresenter();
        if (view == null)
            view = createView();
        if (presenter != null && view != null)
            presenter.attachView(view);
        ActivityControl.addActivity(this);
        Constant.EXTERNAL_STORAGE = getExternalCacheDir().getAbsolutePath();
    }

   protected void setHideStatueBar(){
       Window window = getWindow();
       //设置修改状态栏
       window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
       window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
       //设置状态栏的颜色
       window.setStatusBarColor(Color.TRANSPARENT);
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
//       getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏

   }
    protected abstract P createPresenter();

    protected abstract V createView();

    public P getPresenter() {
        if (!NetWorkUtil.isNetworkAvailable(this)) {
            Constant.NETWORK_INFO = false;
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
        } else {
            Constant.NETWORK_INFO = true;
        }
        return presenter;
    }

    @Override
    protected void onDestroy() {
        if (presenter != null && view != null)
            presenter.disAttachView();
        ActivityControl.remove(this);
        super.onDestroy();
    }
}
