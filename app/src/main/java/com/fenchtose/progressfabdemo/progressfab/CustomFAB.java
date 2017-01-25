package com.fenchtose.progressfabdemo.progressfab;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Jay Rambhia on 11/18/16.
 */

public class CustomFAB extends FloatingActionButton {

    private static final String TAG = "CustomFAB";

    public CustomFAB(Context context) {
        super(context);
    }

    public CustomFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "2 called");
    }

    public CustomFAB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "3 called");
    }
}
