package com.jaredlam.bubbleview;

import android.graphics.Rect;

/**
 * Created by jaredluo on 15/10/29.
 */
public class BubbleInfo {
    private int index;
    private Rect rect;
    private double radians;
    private int speed;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public double getRadians() {
        return radians;
    }

    public void setRadians(double radians) {
        this.radians = radians;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
