package com.fenchtose.progressfabdemo;

import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.fenchtose.progressfabdemo.progressfab.CustomFAB;
import com.fenchtose.progressfabdemo.progressfab.FabUtil;
import com.fenchtose.progressfabdemo.progressfab.ProgressFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ProgressFloatingActionButton fab;
    private ProgressFloatingActionButton fab2;
    private ViewGroup root;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_main);
        root = (ViewGroup) findViewById(R.id.activity_main);
        fab = (ProgressFloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_camera_alt_white_24dp);
        fab2 = (ProgressFloatingActionButton) findViewById(R.id.fab2);
        fab2.setImageResource(R.drawable.ic_camera_alt_white_24dp);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indeterminateProgress();
            }
        });
        fab.setIndeterminate(true);
        fab.setProgressDuration(3000);
        fab.setMaxProgress(100);
        fab.setProgress(40);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                determinateProgress();
            }
        });

        final FloatingActionButton fab3 = new FloatingActionButton(this);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateColor(fab3);
            }
        });

//        root.addView(fab3, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final ProgressFloatingActionButton fab4 = new ProgressFloatingActionButton(this);
        root.addView(fab4, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateColor(fab4.getFab());
            }
        });
    }

    private void animateColor(FloatingActionButton fab) {
        ObjectAnimator animator = FabUtil.createTintAnimator(fab, 0xfff95b4b, 0xffffffff);
        animator.setDuration(200);
        animator.start();
    }

    private void indeterminateProgress() {

        fab.startProgressAnimation();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.stopProgressAnimation();
            }
        }, 3000);


        /*fab.getFab().setBackgroundTintList(ColorStateList.valueOf(0xfff95b4b));

        fab.reset();

        animateColor(fab.getFab());

        fab.hideProgress();
        fab.setProgress(100);
        fab.setMaxProgress(100);
        fab.setProgressDuration(3000);
//        fab.setIndeterminate(true);
//        fab.startProgressAnimation();
        fab.changeColor(getColor(R.color.theme_app), 0xffffffff, true);

        fab.getFab().setImageResource(R.drawable.ic_camera_alt_theme_24dp);*/
    }

    private void determinateProgress() {
        fab2.reset();
        fab2.setMaxProgress(100);
        fab2.setProgressDuration(1500);

        fab2.setProgress(100);
    }
}
