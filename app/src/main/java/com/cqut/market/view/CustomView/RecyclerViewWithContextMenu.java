package com.cqut.market.view.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewWithContextMenu extends RecyclerView {

    private RecyclerViewContextInfo mContextInfo = new RecyclerViewContextInfo();

    public RecyclerViewWithContextMenu(Context context) {
        super(context);
    }

    public RecyclerViewWithContextMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithContextMenu(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        getPositionByChild(originalView);
        return super.showContextMenuForChild(originalView);
    }

    @Override
    public boolean showContextMenuForChild(View originalView, float x, float y) {
        getPositionByChild(originalView);
        return super.showContextMenuForChild(originalView, x, y);
    }

    /**
     * 记录当前RecyclerView中Item上下文菜单的Position
     * @param originalView originalView
     */
    private void getPositionByChild(View originalView){
        LayoutManager layoutManager =getLayoutManager();
        if(layoutManager!=null){
            int position=layoutManager.getPosition(originalView);
            mContextInfo.setPosition(position);
        }
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mContextInfo;
    }

    public class RecyclerViewContextInfo implements ContextMenu.ContextMenuInfo {
        private int mPosition = -1;
        public int getPosition(){
            return this.mPosition;
        }
        public int setPosition(int position){
            return this.mPosition=position;
        }
    }

}