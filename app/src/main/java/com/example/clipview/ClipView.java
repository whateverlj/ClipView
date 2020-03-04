package com.example.clipview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class ClipView extends View {

    private int mTextStartX;
    private int mTextStartY;

    private String mText = "今日头条";
    private Paint mPaint;
    private int mTextSize = sp2px(30);


    private Rect mTextBound = new Rect();
    private int mTextWidth;
    private int mTextHeight;

    private float mProgress;

    public ClipView(Context context) {
        super(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.ClipView);
        mText = ta.getString(R.styleable.ClipView_text);
        mTextSize = ta.getDimensionPixelSize(
                R.styleable.ClipView_text_size, mTextSize);
        mProgress = ta.getFloat(R.styleable.ClipView_progress, getResources().getColor(R.color.mTextOriginColor));
        ta.recycle();

        mPaint.setTextSize(mTextSize);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        measureText();

        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);

        mTextStartX = getMeasuredWidth() / 2 - mTextWidth / 2;
        mTextStartY = getMeasuredHeight() / 2 - mTextHeight / 2;
    }

    private int measureHeight(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = mTextBound.height();
                result += getPaddingTop() + getPaddingBottom();
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result;
    }

    private int measureWidth(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                // result = mTextBound.width();
                result = mTextWidth;
                result += getPaddingLeft() + getPaddingRight();
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result;
    }

    private void measureText() {
        mTextWidth = (int) mPaint.measureText(mText);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        mTextHeight = (int) Math.ceil(fm.descent - fm.top);

        mPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
        mTextHeight = mTextBound.height();
    }

    float moveX = 0;
    float moveDistance = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveDistance = Math.abs(event.getX() - moveX);
                mProgress = moveDistance/getWidth();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChangeLeft(canvas);
        drawOriginLeft(canvas);
    }

    private void drawChangeLeft(Canvas canvas) {
        drawText_h(canvas, getResources().getColor(R.color.mTextChangeColor), mTextStartX,
                (int) (mTextStartX + mProgress * mTextWidth));
    }

    private void drawText_h(Canvas canvas, int color, int startX, int endX) {
        mPaint.setColor(color);
        canvas.save();
        canvas.clipRect(startX, 0, endX, getMeasuredHeight());// left, top,
        // right, bottom
        canvas.drawText(mText, mTextStartX,
                getMeasuredHeight() / 2
                        - ((mPaint.descent() + mPaint.ascent()) / 2), mPaint);
        canvas.restore();
    }

    private void drawOriginLeft(Canvas canvas) {
        drawText_h(canvas, getResources().getColor(R.color.mTextOriginColor), (int) (mTextStartX + mProgress
                * mTextWidth), mTextStartX + mTextWidth);
    }


    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        mPaint.setTextSize(mTextSize);
        requestLayout();
        invalidate();
    }

    public void setText(String text) {
        this.mText = text;
        requestLayout();
        invalidate();
    }

    private int sp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                dpVal, getResources().getDisplayMetrics());
    }

}
