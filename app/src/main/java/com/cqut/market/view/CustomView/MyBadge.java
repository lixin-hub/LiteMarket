package com.cqut.market.view.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MyBadge extends View {

    private final int backgroundColor = Color.RED;
    private Paint textPaint;
    private Paint backgroundPaint;
    private String number = "";
    private int textColor = Color.WHITE;
    private float x, y;
    private int radius;
    private int height, width;

    public MyBadge(Context context) {
        super(context);
        init();
    }

    public MyBadge(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBadge(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyBadge(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    public void setNumber(String number) {
        this.number = number;
        if (number == null)
            this.setVisibility(View.GONE);
        else {
            this.setVisibility(View.VISIBLE);
            init();
        }
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    private void init() {
        textPaint = new Paint();
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(30);
        int textHeight =25;//getTextHeight(number, textPaint);
        int textWidth = getTextWidth(number, textPaint);
        width = textWidth + 30;
        height = textHeight + 30;
        x = width / 2 - textWidth / 2;
        y = height / 2 + textHeight / 2;
        radius = width / 2;
        setMeasuredDimension(width, height);
    }

    private int getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    private int getTextWidth(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawCircle(width / 2, height / 2, radius, backgroundPaint);
        canvas.drawText(number + "", x-4, y, textPaint);
    }
}
