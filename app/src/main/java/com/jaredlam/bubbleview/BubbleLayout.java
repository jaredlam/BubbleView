package com.jaredlam.bubbleview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jared Luo on 15/10/25.
 */
public class BubbleLayout extends ViewGroup {

    public static final int DEFAULT_PADDING = 10;

    double mRadiansPiece = 2 * Math.PI / 6;
    private int mRandomRadians = 0;

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
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Rect baseRect = null;
        int currentRadians = mRandomRadians;
        List<BubbleView> sortResult = sort();
        for (int i = 0; i < sortResult.size(); i++) {
            View child = sortResult.get(i);
            BubbleView bubbleView = (BubbleView) child;
            int radius = bubbleView.getMeasuredWidth() / 2;
            if (i == 0) {
                baseRect = getBounds(getMeasuredWidth() / 2 - radius, getMeasuredHeight() / 2 - radius, child.getMeasuredWidth(), child.getMeasuredHeight());
                child.layout(baseRect.left, baseRect.top, baseRect.right, baseRect.bottom);
            } else {
                int baseCenterX = baseRect.left + baseRect.width() / 2;
                int baseCenterY = baseRect.top + baseRect.width() / 2;

                currentRadians += mRadiansPiece;
                int[] center = getRadianPoint(baseRect.width() / 2 + DEFAULT_PADDING + radius, baseCenterX, baseCenterY, currentRadians);

                Rect rect = getBounds(center[0] - radius, center[1] - radius, child.getMeasuredWidth(), child.getMeasuredHeight());
                child.layout(rect.left, rect.top, rect.right, rect.bottom);

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


    private int getRandomBetween(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    private Rect getBounds(int left, int top, int width, int height) {
        return new Rect(left, top, left + width, top + height);
    }


    private boolean doOverlap(Rect rect1, Rect rect2) {
        return !(rect1.left > rect2.right || rect2.left > rect1.right) && !(rect1.top > rect2.bottom || rect2.top > rect1.bottom);
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

}
