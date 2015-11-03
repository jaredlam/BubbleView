package com.jaredlam.bubbleview.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;

import com.jaredlam.bubbleview.BubbleLayout;
import com.jaredlam.bubbleview.BubbleView;

/**
 * Created by Jared Luo on 15/10/25.
 */
public class MainActivity extends Activity {

    private static String[] labels = {"Hello Bubble", "This is bubble view", "Android", "Github", "Jared", "Bubble with different size", "Yo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BubbleLayout layout = (BubbleLayout) findViewById(R.id.bubble_layout);
        for (String label : labels) {
            BubbleView bubbleView = new BubbleView(this);
            bubbleView.setText(label);
            bubbleView.setGravity(Gravity.CENTER);
            bubbleView.setPadding(10, 10, 10, 10);
            bubbleView.setTextColor(Color.parseColor("#000000"));
            layout.addViewSortByWidth(bubbleView);
        }

    }
}
