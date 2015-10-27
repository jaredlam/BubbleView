package com.jaredlam.bubbleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Jared Luo on 15/10/25.
 */
public class BubbleView extends View {

    private static final int DEFAULT_PADDING = 10;

    private Paint mPaint;
    private Paint mTextPaint;
    private float mTextMeasureWidth;
    private String mText;
    private Rect mTextBound;

    public BubbleView(Context context) {
        super(context);
        init();
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(18);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                widthSize = (int) getTextMeasureWidth();
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightSize = (int) getTextMeasureWidth();
                break;
        }

        setMeasuredDimension(widthSize + DEFAULT_PADDING * 2, heightSize + DEFAULT_PADDING * 2);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        canvas.drawCircle(width / 2, height / 2, width / 2, mPaint);
        if (mText != null && mTextBound != null) {
            canvas.drawText(mText, width / 2, height / 2 - mTextBound.exactCenterY(), mTextPaint);
        }
    }

    public void setTextColorRes(int colorRes) {
        mTextPaint.setColor(getResources().getColor(colorRes));
    }

    public void setText(String label) {
        mText = label;
        mTextMeasureWidth = mTextPaint.measureText(label);
        mTextBound = new Rect();
        mTextPaint.getTextBounds(label, 0, label.length(), mTextBound);
    }

    public float getTextMeasureWidth() {
        return mTextMeasureWidth;
    }
}
