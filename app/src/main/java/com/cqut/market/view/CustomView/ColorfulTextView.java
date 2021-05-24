package com.cqut.market.view.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.security.PublicKey;

public class ColorfulTextView extends AppCompatTextView {
    private int speed;
    private boolean isFlash;
    private Paint mPaint;
    private int mViewWidth;
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private int mTransalte;
    private int startColor;
    private int endColor;

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }

    public void setSpeed(int speed){
        this.speed=speed;
    }
    public void setFlash(boolean isFlash){
        this.isFlash=isFlash;
    }
    public ColorfulTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();//获得当前绘制的Paint对象
                mLinearGradient = new LinearGradient(
                        0,//渐变起始点x坐标
                        0,//渐变起始点y坐标
                        mViewWidth,//渐变结束点x点坐标
                        getMeasuredHeight(),//渐变结束点y坐标
                        new int[]{
                                startColor,endColor},//颜色的int数组
                        null,//相对位置的颜色数组,可为null, 若为null,可为null,颜色沿渐变线均匀分布
                        Shader.TileMode.MIRROR);//平铺模式
                mPaint.setShader(mLinearGradient);//给这个paint设置linearFradient属性
                mGradientMatrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGradientMatrix != null) {
            mTransalte += mViewWidth / 5;
            if (mTransalte > 2 * mViewWidth) {
                mTransalte -= mViewWidth;
            }
            mGradientMatrix.setTranslate(mTransalte, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);//通过矩阵的方式不断平移产生渐变效果
            if (isFlash)
            postInvalidateDelayed(speed);

        }

    }
}
