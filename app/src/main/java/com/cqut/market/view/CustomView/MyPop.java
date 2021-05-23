package com.cqut.market.view.CustomView;

import android.view.View;
import android.widget.PopupWindow;

public class MyPop extends PopupWindow {
    boolean isDismiss;
    MyPop(){
        super();
    }
    public MyPop(View view){
        super(view);
    }

    @Override
    public void dismiss() {
        if (isDismiss)
        super.dismiss();
    }

    public void close(){
        super.dismiss();

    }

    public void setDismiss(boolean dismiss) {
        isDismiss = dismiss;
    }

}
