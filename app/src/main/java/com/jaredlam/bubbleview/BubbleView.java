package com.jaredlam.bubbleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Jared Luo on 15/10/25.
 */
public class BubbleView extends View {

    private static final int DEFAULT_PADDING_IN_DP = 10;
    private static final int DEFAULT_TEXT_SIZE_IN_SP = 16;

    private int mPadding = DEFAULT_PADDING_IN_DP;

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
        float textSizeInPx = getPxBySp(DEFAULT_TEXT_SIZE_IN_SP);
        mTextPaint.setTextSize(textSizeInPx);
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

        int paddingInPx = getPxByDp(mPadding);
        setMeasuredDimension(widthSize + paddingInPx * 2, heightSize + paddingInPx * 2);

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

    public void setTextSize(int valueInSp) {
        float textSizeInPx = getPxBySp(valueInSp);
        mTextPaint.setTextSize(textSizeInPx);
    }

    public void setBubblePadding(int paddingInDp){
        this.mPadding = paddingInDp;
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


    private float getPxBySp(int sp) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    private int getPxByDp(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources()
                .getDisplayMetrics());
    }
}
