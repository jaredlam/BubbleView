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
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BubbleLayout extends ViewGroup implements BubbleView.MoveListener {

    public static final int DEFAULT_PADDING = 10;
    public static final int DEFAULT_MIN_SPEED = 200;
    public static final int DEFAULT_MAX_SPEED = 500;

    private int padding = DEFAULT_PADDING;
    private int minPxPerTenMilliseconds = DEFAULT_MIN_SPEED;
    private int maxPxPerTenMilliseconds = DEFAULT_MAX_SPEED;

    private double mRadiansPiece = 2 * Math.PI / 6;
    private int mRandomRadians = 0;
    private List<BubbleInfo> mBubbleInfos = new ArrayList<>();
    private Timer mTimer;

    public BubbleLayout(Context context) {
        this(context, null);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.bubbleview_BubbleLayout, defStyleAttr, 0);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int id = typedArray.getIndex(i);
            if (id == R.styleable.bubbleview_BubbleLayout_bubbleview_minSpeed) {
                minPxPerTenMilliseconds = typedArray.getDimensionPixelSize(id, DEFAULT_MIN_SPEED);
            } else if (id == R.styleable.bubbleview_BubbleLayout_bubbleview_maxSpeed) {
                maxPxPerTenMilliseconds = typedArray.getDimensionPixelSize(id, DEFAULT_MAX_SPEED);
            } else if (id == R.styleable.bubbleview_BubbleLayout_bubbleview_padding) {
                padding = typedArray.getDimensionPixelSize(id, DEFAULT_PADDING);
            }
        }
        typedArray.recycle();

        mRandomRadians = getRandomBetween(0, (int) (2 * Math.PI));
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Rect baseRect = null;
        int currentRadians = mRandomRadians;
        List<BubbleView> sortResult = sort();
        for (int i = 0; i < sortResult.size(); i++) {
            View child = sortResult.get(i);

            BubbleInfo bubbleInfo = getBubbleInfoByView(child);

            if (bubbleInfo != null) {
                BubbleView bubbleView = (BubbleView) child;
                bubbleView.setMoveListener(this);
                bubbleView.setBubbleInfo(bubbleInfo);
                int radius = bubbleView.getMeasuredWidth() / 2;
                if (i == 0) {
                    baseRect = getBounds(getMeasuredWidth() / 2 - radius, getMeasuredHeight() / 2 - radius, child.getMeasuredWidth(), child.getMeasuredHeight());
                    child.layout(baseRect.left, baseRect.top, baseRect.right, baseRect.bottom);
                    bubbleInfo.setRect(baseRect);
                } else {
                    int baseCenterX = baseRect.left + baseRect.width() / 2;
                    int baseCenterY = baseRect.top + baseRect.width() / 2;

                    currentRadians += mRadiansPiece;
                    int[] center = getRadianPoint(baseRect.width() / 2 + padding + radius, baseCenterX, baseCenterY, currentRadians);

                    Rect rect = getBounds(center[0] - radius, center[1] - radius, child.getMeasuredWidth(), child.getMeasuredHeight());
                    child.layout(rect.left, rect.top, rect.right, rect.bottom);
                    bubbleInfo.setRect(rect);
                }
            }
        }
    }

    private BubbleInfo getBubbleInfoByView(View child) {
        for (BubbleInfo info : mBubbleInfos) {
            BubbleView bubbleView = (BubbleView) getChildAt(info.getIndex());
            if (bubbleView == child) {
                return info;
            }
        }
        return null;
    }

    private int[] getRadianPoint(int amount, int x, int y, double radian) {
        int resultX = x + (int) (amount * Math.cos(radian));
        int resultY = y + (int) (amount * Math.sin(radian));

        return new int[]{resultX, resultY};
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setupBubbleInfoList();

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    public void addViewSortByWidth(BubbleView newChild) {

        LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        newChild.setLayoutParams(param);
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof BubbleView) {
                    BubbleView bubbleView = (BubbleView) child;
                    float textWidth = bubbleView.getTextMeasureWidth();
                    if (newChild.getTextMeasureWidth() > textWidth) {
                        super.addView(newChild, i);
                        return;
                    }
                }
            }
        }
        super.addView(newChild);
    }

    private void setupBubbleInfoList() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            BubbleInfo info = new BubbleInfo();
            info.setRadians(getRandomRadians());
            info.setSpeed(getRandomBetween(minPxPerTenMilliseconds, maxPxPerTenMilliseconds));
            info.setOldSpeed(info.getSpeed());
            info.setIndex(i);
            mBubbleInfos.add(info);
        }
    }


    private int getRandomBetween(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    private Rect getBounds(int left, int top, int width, int height) {
        return new Rect(left, top, left + width, top + height);
    }


    private boolean doRectOverlap(Rect rect0, Rect rect1) {
        return !(rect0.left > rect1.right || rect1.left > rect0.right) && !(rect0.top > rect1.bottom || rect1.top > rect0.bottom);
    }

    private boolean doCircleOverlap(Rect rect0, Rect rect1) {
        int x0 = rect0.centerX();
        int y0 = rect0.centerY();
        int r0 = rect0.width() / 2;
        int x1 = rect1.centerX();
        int y1 = rect1.centerY();
        int r1 = rect1.width() / 2;

        return Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2) <= Math.pow(r0 + r1, 2);
    }

    private boolean chooseFromTwo() {
        return Math.random() > 0.5;
    }

    private List<BubbleView> sort() {
        List<BubbleView> allBubbleChild = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view != null && view instanceof BubbleView) {
                allBubbleChild.add((BubbleView) view);
            }
        }

        List<BubbleView> sortResult = new ArrayList<>();

        if (allBubbleChild.size() > 2) {
            sortResult.add(allBubbleChild.get(0));
            sortResult.add(allBubbleChild.get(1));

            List<BubbleView> halfList = allBubbleChild.subList(2, allBubbleChild.size());
            List<BubbleView> quarter1List = halfList.subList(0, halfList.size() / 2);
            List<BubbleView> quarter2List = halfList.subList(halfList.size() / 2, halfList.size());
            int count = Math.max(quarter1List.size(), quarter2List.size());
            for (int i = 0; i < count; i++) {
                if (i < quarter2List.size()) {
                    sortResult.add(quarter2List.get(i));
                }
                if (i < quarter1List.size()) {
                    sortResult.add(quarter1List.get(i));
                }
            }

        } else {
            sortResult = allBubbleChild;
        }

        return sortResult;
    }

    private void startAnimate() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
                this.cancel();
            }

        }, 10);

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int count = getChildCount();
            for (int i = 0; i < count && mBubbleInfos.size() > 0; i++) {

                BubbleInfo bubbleInfo = mBubbleInfos.get(i);

                List<BubbleInfo> overlapList = hasOverlap(bubbleInfo);

                Point overlapPoint = ifOverlapBounds(bubbleInfo);
                if (overlapPoint != null) {
                    reverseIfOverlapBounds(bubbleInfo);
                } else if (overlapList.size() > 0) {
                    dealWithOverlap();
                }

                moveBubble(bubbleInfo);
            }
            startAnimate();
        }
    };

    private void moveBubble(BubbleInfo info) {
        View child = getChildAt(info.getIndex());
        int[] center = getRadianPoint(info.getSpeed(), child.getLeft() + child.getWidth() / 2, child.getTop() + child.getWidth() / 2, info.getRadians());
        Rect rect = getBounds(center[0] - child.getWidth() / 2, center[1] - child.getWidth() / 2, child.getMeasuredWidth(), child.getMeasuredHeight());
        info.setRect(rect);
        child.layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    private void slowerBubbleIfNeeded(BubbleInfo info) {
        if (info.getOldSpeed() > 0 && info.getSpeed() > info.getOldSpeed()) {
            info.setSpeed(info.getSpeed() - 1);
        }
    }

    private BubbleInfo getNewMoveInfo(BubbleInfo bubbleInfo, View child, List<BubbleInfo> overlapRect) {
        Rect oldRect = bubbleInfo.getRect();

        Point cooperate = getCooperatePoint(overlapRect);
        Point overlapBoundPoint = ifOverlapBounds(bubbleInfo);
        if (overlapBoundPoint != null) {
            cooperate = new Point((cooperate.x + overlapBoundPoint.x) / 2, (cooperate.y + overlapBoundPoint.y) / 2);
        }

        float overlapRadians = (float) getRadians(new float[]{oldRect.exactCenterX(), oldRect.exactCenterY()}, new float[]{cooperate.x, cooperate.y});
        double reverseRadians = getReverseRadians(overlapRadians);

        int[] centerNew = getRadianPoint(bubbleInfo.getSpeed(), child.getLeft() + child.getWidth() / 2, child.getTop() + child.getWidth() / 2, reverseRadians);
        Rect rectNew = getBounds(centerNew[0] - child.getWidth() / 2, centerNew[1] - child.getWidth() / 2, child.getMeasuredWidth(), child.getMeasuredHeight());

        BubbleInfo bubbleInfoNew = new BubbleInfo();
        bubbleInfoNew.setIndex(bubbleInfo.getIndex());
        bubbleInfoNew.setSpeed(bubbleInfo.getSpeed());
        bubbleInfoNew.setOldSpeed(bubbleInfo.getOldSpeed());
        bubbleInfoNew.setRadians(reverseRadians);
        bubbleInfoNew.setRect(rectNew);

        return bubbleInfoNew;

    }

    private void dealWithOverlap() {
        List<BubbleInfo> tempBubbleInfoList = new ArrayList<>();
        for (BubbleInfo info : mBubbleInfos) {
            List<BubbleInfo> overlapList = hasOverlap(info);
            if (overlapList.size() > 0) {
                BubbleInfo bubbleInfoNew = getNewMoveInfo(info, getChildAt(info.getIndex()), overlapList);
                slowerBubbleIfNeeded(bubbleInfoNew);
                tempBubbleInfoList.add(bubbleInfoNew);
            }
        }

        for (int i = 0; i < tempBubbleInfoList.size(); i++) {
            BubbleInfo tempBubbleInfo = tempBubbleInfoList.get(i);
            BubbleInfo oldBubbleInfo = mBubbleInfos.get(tempBubbleInfo.getIndex());
            oldBubbleInfo.setRadians(tempBubbleInfo.getRadians());
            oldBubbleInfo.setOldSpeed(tempBubbleInfo.getOldSpeed());
            oldBubbleInfo.setIndex(tempBubbleInfo.getIndex());
            oldBubbleInfo.setSpeed(tempBubbleInfo.getSpeed());
            oldBubbleInfo.setRect(tempBubbleInfo.getRect());
        }

    }

    private Point getCooperatePoint(List<BubbleInfo> overlapRect) {
        int totalX = 0;
        int totalY = 0;
        for (BubbleInfo info : overlapRect) {
            totalX += info.getRect().exactCenterX();
            totalY += info.getRect().exactCenterY();
        }

        return new Point(totalX / overlapRect.size(), totalY / overlapRect.size());
    }

    private double getReverseRadians(double radians) {
        double reverseRadians;
        if (radians > Math.PI) {
            reverseRadians = radians - Math.PI;
        } else {
            reverseRadians = radians + Math.PI;
        }

        return reverseRadians;
    }

    private List<BubbleInfo> hasOverlap(BubbleInfo bubbleInfo) {
        int count = mBubbleInfos.size();
        List<BubbleInfo> overlapList = new ArrayList<>();
        if (bubbleInfo.getRect() != null) {
            for (int i = 0; i < count; i++) {
                BubbleInfo otherInfo = mBubbleInfos.get(i);
                if (i != bubbleInfo.getIndex()) {
                    if (otherInfo.getRect() != null) {
                        if (doCircleOverlap(otherInfo.getRect(), bubbleInfo.getRect())) {
                            overlapList.add(otherInfo);
                        }
                    }
                }
            }
        }
        return overlapList;
    }

    private void reverseIfOverlapBounds(BubbleInfo bubbleInfo) {
        Point overlapPoint = ifOverlapBounds(bubbleInfo);
        Rect totalRect = new Rect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
        if (overlapPoint != null) {
            float overlapRadians = (float) getRadians(new float[]{bubbleInfo.getRect().exactCenterX(), bubbleInfo.getRect().exactCenterY()}, new float[]{overlapPoint.x, overlapPoint.y});
            if (!totalRect.contains(bubbleInfo.getRect().centerX(), bubbleInfo.getRect().centerY())) {
                bubbleInfo.setRadians(overlapRadians);
            } else {
                double reverseRadians = getReverseRadians(overlapRadians);
                bubbleInfo.setRadians(reverseRadians);
            }
            slowerBubbleIfNeeded(bubbleInfo);
        }
    }

    private Point ifOverlapBounds(BubbleInfo bubbleInfo) {
        Rect rect = new Rect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
        if (bubbleInfo.getRect() != null) {
            Rect bubbleRect = bubbleInfo.getRect();
            List<Point> overlapPoints = new ArrayList<>();
            if (rect.left >= bubbleRect.left) {
                Point overlapPoint = new Point(rect.left, bubbleRect.centerY());
                overlapPoints.add(overlapPoint);
            }
            if (rect.top >= bubbleRect.top) {
                Point overlapPoint = new Point(bubbleRect.centerX(), rect.top);
                overlapPoints.add(overlapPoint);
            }
            if (rect.right <= bubbleRect.right) {
                Point overlapPoint = new Point(rect.right, bubbleRect.centerY());
                overlapPoints.add(overlapPoint);
            }
            if (rect.bottom <= bubbleRect.bottom) {
                Point overlapPoint = new Point(bubbleRect.centerX(), rect.bottom);
                overlapPoints.add(overlapPoint);
            }

            if (overlapPoints.size() > 0) {
                int totalX = 0;
                int totalY = 0;
                for (Point point : overlapPoints) {
                    totalX += point.x;
                    totalY += point.y;
                }
                return new Point(totalX / overlapPoints.size(), totalY / overlapPoints.size());
            }
        }

        return null;
    }

    private double getRandomRadians() {
        return Math.random() * 2 * Math.PI;
    }

    private double getRadians(float[] fromPoint, float[] toPoint) {
        return Math.atan2(toPoint[1] - fromPoint[1], toPoint[0] - fromPoint[0]);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onMove(BubbleInfo bubbleInfo, int centerX, int centerY, int deltaX, int deltaY, double velocity) {
        velocity /= 6;
        if (velocity > bubbleInfo.getSpeed()) {
            float radians = (float) getRadians(new float[]{centerX, centerY}, new float[]{centerX + deltaX, centerY + deltaY});
            bubbleInfo.setRadians(radians);
            bubbleInfo.setSpeed((int) velocity);
        }
    }
}
