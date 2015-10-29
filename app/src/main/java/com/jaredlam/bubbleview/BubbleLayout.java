package com.jaredlam.bubbleview;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jared Luo on 15/10/25.
 */
public class BubbleLayout extends ViewGroup {

    public static final int DEFAULT_PADDING = 10;

    private double mRadiansPiece = 2 * Math.PI / 6;
    private int mRandomRadians = 0;
    private List<BubbleInfo> mBubbleInfos = new ArrayList<>();

    public BubbleLayout(Context context) {
        super(context);
        init();
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
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
            BubbleInfo bubbleInfo = mBubbleInfos.get(i);
            BubbleView bubbleView = (BubbleView) child;
            int radius = bubbleView.getMeasuredWidth() / 2;
            if (i == 0) {
                baseRect = getBounds(getMeasuredWidth() / 2 - radius, getMeasuredHeight() / 2 - radius, child.getMeasuredWidth(), child.getMeasuredHeight());
                child.layout(baseRect.left, baseRect.top, baseRect.right, baseRect.bottom);
                bubbleInfo.setRect(baseRect);
            } else {
                int baseCenterX = baseRect.left + baseRect.width() / 2;
                int baseCenterY = baseRect.top + baseRect.width() / 2;

                currentRadians += mRadiansPiece;
                int[] center = getRadianPoint(baseRect.width() / 2 + DEFAULT_PADDING + radius, baseCenterX, baseCenterY, currentRadians);

                Rect rect = getBounds(center[0] - radius, center[1] - radius, child.getMeasuredWidth(), child.getMeasuredHeight());
                child.layout(rect.left, rect.top, rect.right, rect.bottom);
                bubbleInfo.setRect(rect);
            }
        }
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
        BubbleInfo info = new BubbleInfo();
        info.setRadians(getRandomRadians());
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
                        info.setIndex(i);
                        mBubbleInfos.add(info);
                        return;
                    }
                }
            }
        }
        super.addView(newChild);
        info.setIndex(getChildCount() - 1);
        mBubbleInfos.add(info);
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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
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
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                BubbleInfo bubbleInfo = mBubbleInfos.get(i);
                int[] center = getRadianPoint(2, child.getLeft() + child.getWidth() / 2, child.getTop() + child.getWidth() / 2, bubbleInfo.getRadians());
                Rect rect = getBounds(center[0] - child.getWidth() / 2, center[1] - child.getWidth() / 2, child.getMeasuredWidth(), child.getMeasuredHeight());
                List<BubbleInfo> overlapList = hasOverlap(bubbleInfo);
                if (overlapList.size() == 0) {
                    bubbleInfo.setRect(rect);
                    child.layout(rect.left, rect.top, rect.right, rect.bottom);
                } else {
                    float currentRadians = moveAway(bubbleInfo, child, overlapList);
                    moveAwayOthers(bubbleInfo);

                    bubbleInfo.setRadians(currentRadians);
                }

            }
            startAnimate();
        }
    };

    private float moveAway(BubbleInfo bubbleInfo, View child, List<BubbleInfo> overlapRect) {
        Rect oldRect = bubbleInfo.getRect();

        int[] cooperate = getCooperatePoint(overlapRect);

        float overlapRadians = (float) getRadians(new float[]{oldRect.exactCenterX(), oldRect.exactCenterY()}, new float[]{cooperate[0], cooperate[1]});

        int[] centerNew = getRadianPoint(2, child.getLeft() + child.getWidth() / 2, child.getTop() + child.getWidth() / 2, getReverseRadians(overlapRadians));
        Rect rectNew = getBounds(centerNew[0] - child.getWidth() / 2, centerNew[1] - child.getWidth() / 2, child.getMeasuredWidth(), child.getMeasuredHeight());

        child.layout(rectNew.left, rectNew.top, rectNew.right, rectNew.bottom);

        return overlapRadians;
    }

    private void moveAwayOthers(BubbleInfo bubbleInfo) {
        Map<Integer, Float> radianMap = new HashMap<>();
        for (BubbleInfo info : mBubbleInfos) {
            if (info.getIndex() != bubbleInfo.getIndex()) {
                List<BubbleInfo> overlapList = hasOverlap(info);
                if (overlapList.size() > 0) {
                    float radians = moveAway(info, getChildAt(info.getIndex()), overlapList);
                    radianMap.put(info.getIndex(), radians);
                }
            }
        }
        Iterator<Map.Entry<Integer, Float>> iterator = radianMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Float> entry = iterator.next();
            for (BubbleInfo info : mBubbleInfos) {
                if (info.getIndex() == entry.getKey()) {
                    info.setRadians(entry.getValue());
                    View child = getChildAt(info.getIndex());
                    info.setRect(new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom()));
                }
            }
        }
    }

    private int[] getCooperatePoint(List<BubbleInfo> overlapRect) {
        int totalX = 0;
        int totalY = 0;
        for (BubbleInfo info : overlapRect) {
            totalX += info.getRect().exactCenterX();
            totalY += info.getRect().exactCenterY();
        }

        return new int[]{totalX / overlapRect.size(), totalY / overlapRect.size()};
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

    private double getRandomRadians() {
        return Math.random() * 2 * Math.PI;
    }

    private double getRadians(float[] fromPoint, float[] toPoint) {
        return Math.atan2(toPoint[1] - fromPoint[1], toPoint[0] - fromPoint[0]);
    }
}
