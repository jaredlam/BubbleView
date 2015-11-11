/*
 * Copyright (C) 2015 Jared Luo
 * jaredlam86@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaredlam.bubbleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.TextView;

public class BubbleView extends TextView {

    private Paint mPaint;

    private ViewConfiguration mViewConfiguration;
    private VelocityTracker mVelocityTracker;
    private int mLastX;
    private int mLastY;

    private boolean mIsMoving;
    private MoveListener mMoveListener;
    private BubbleInfo mBubbleInfo;

    public BubbleView(Context context) {
        super(context);
        init(context);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);

        mViewConfiguration = ViewConfiguration.get(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        canvas.drawCircle(width / 2, height / 2, width / 2, mPaint);
        super.onDraw(canvas);
    }

    public float getTextMeasureWidth() {
        Rect bounds = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().toString().length(), bounds);
        return bounds.width();
    }

    public void setCircleColor(int colorRes) {
        int color = getResources().getColor(colorRes);
        mPaint.setColor(color);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);

        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();

        int[] location = new int[2];
        getLocationOnScreen(location);
        Rect rect = new Rect(location[0], location[1], location[0] + getWidth(), location[1] + getHeight());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = rawX;
                mLastY = rawY;
                if (rect.contains(mLastX, mLastY)) {
                    mIsMoving = true;
                }

                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);

                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(10);
                if (mIsMoving) {
                    int deltaX = rawX - mLastX;
                    int deltaY = rawY - mLastY;
                    if (Math.abs(deltaX) > mViewConfiguration.getScaledTouchSlop()
                            || Math.abs(deltaY) > mViewConfiguration.getScaledTouchSlop()) {
                        if (mMoveListener != null) {
                            int centerX = getLeft() + getWidth() / 2;
                            int centerY = getTop() + getHeight() / 2;
                            float velocityX = mVelocityTracker.getXVelocity(pointerId);
                            float velocityY = mVelocityTracker.getYVelocity(pointerId);
                            double velocity = Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));
                            mMoveListener.onMove(mBubbleInfo, centerX, centerY, deltaX, deltaY, velocity);
                        }
                        mLastX = rawX;
                        mLastY = rawY;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsMoving = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsMoving = false;
                mVelocityTracker.recycle();
                break;
        }
        return true;
    }

    public void setMoveListener(MoveListener moveListener) {
        this.mMoveListener = moveListener;
    }

    public void setBubbleInfo(BubbleInfo bubbleInfo) {
        this.mBubbleInfo = bubbleInfo;
    }

    public interface MoveListener {
        void onMove(BubbleInfo bubbleInfo, int centerX, int centerY, int deltaX, int deltaY, double velocity);
    }
}
