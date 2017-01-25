/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Rahat Ahmed
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.fenchtose.progressfabdemo.progressfab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.fenchtose.progressfabdemo.R;

public class ProgressRingView extends View implements FabUtil.OnFabValueCallback{
    String TAG = ProgressRingView.class.getSimpleName();

    private Paint progressPaint;
    private Paint noProgressPaint;

    private int size = 0;
    private RectF bounds;
    private float boundsPadding = 0.14f;
    private int viewRadius;
    private float ringWidthRatio = 0.14f; //of a possible 1f;
    private boolean indeterminate,autostartanim;
    private float progress, maxProgress, indeterminateSweep, indeterminateRotateOffset;
    private int ringWidth, noringWidth, midRingWidth, animDuration;
    private int progressColor = Color.BLACK;
    private int noProgressColor = Color.WHITE;


    // Animation related stuff
    private float startAngle;
    private float actualProgress;
    private ValueAnimator startAngleRotate;
    private ValueAnimator progressAnimator;
    private AnimatorSet indeterminateAnimator;

    private boolean isAnimating;
    private boolean isInView;

    private ProgressFloatingActionButton.OnFabViewListener fabViewListener;

    public ProgressRingView(Context context) {
        super(context);
        init(null/*, 0*/);
    }

    public ProgressRingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs/*, 0*/);
    }

    public ProgressRingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs/*, defStyle*/);
    }

    protected void init(AttributeSet attrs/*, int defStyle*/) {
        if (attrs != null) {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressFloatingActionButton);
            progress = a.getFloat(R.styleable.ProgressFloatingActionButton_android_progress, 0f);
            progressColor = a.getColor(R.styleable.ProgressFloatingActionButton_fbb_progressColor, progressColor);
            noProgressColor = a.getColor(R.styleable.ProgressFloatingActionButton_fbb_noprogressColor, noProgressColor);

//            Log.i(TAG, "progress color: " + progressColor);
//            Log.i(TAG, "no progress color: " + noProgressColor);

            maxProgress = a.getFloat(R.styleable.ProgressFloatingActionButton_android_max, 100f);
            indeterminate = a.getBoolean(R.styleable.ProgressFloatingActionButton_android_indeterminate, false);
            autostartanim = a.getBoolean(R.styleable.ProgressFloatingActionButton_fbb_autoStart, true);
            animDuration = a.getInteger(R.styleable.ProgressFloatingActionButton_android_indeterminateDuration, 4000);
            ringWidthRatio = a.getFloat(R.styleable.ProgressFloatingActionButton_fbb_progressWidthRatio, ringWidthRatio);

            ringWidth = a.getDimensionPixelOffset(R.styleable.ProgressFloatingActionButton_fbb_ring_width, 6);
            noringWidth = a.getDimensionPixelOffset(R.styleable.ProgressFloatingActionButton_fbb_bg_ring_width, 8);

            a.recycle();
        }

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.BUTT);
        progressPaint.setStrokeWidth(ringWidth);

        noProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        noProgressPaint.setColor(noProgressColor);
        noProgressPaint.setStyle(Paint.Style.STROKE);
        noProgressPaint.setStrokeCap(Paint.Cap.BUTT);
        noProgressPaint.setStrokeWidth(noringWidth);

        /*if(autostartanim) {
            startAnimation();
        }*/

        if (isInEditMode()) {
            isInView = true;
        }
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        size = Math.min(w,h);
        viewRadius = size / 2;
//        Log.i(TAG, "onSizeChanged");
        setRingWidth(-1,true);
    }

    public void setRingWidthRatio(float ringWidthRatio) {
        this.ringWidthRatio = ringWidthRatio;
    }

    public void setAutostartanim(boolean autostartanim) {
        this.autostartanim = autostartanim;
    }

    /*public void setFabViewListener(CircleImageView.OnFabViewListener fabViewListener) {
        this.fabViewListener = fabViewListener;
    }*/

    public void setRingWidth(int width, int bgRingWidth) {

        ringWidth = width;
        noringWidth = bgRingWidth;

        midRingWidth = ringWidth/2;
        progressPaint.setStrokeWidth(ringWidth);
        noProgressPaint.setStrokeWidth(noringWidth);

//        Log.i(TAG, "set ring width: " + ringWidth);

        updateBounds();
    }

    public void setRingWidth(int width,boolean original){
        /*if(original){
            ringWidth = Math.round((float) viewRadius * ringWidthRatio);
        }else{
            ringWidth = width;
        }*/

        if (width <= 0) {
            width = ringWidth;
        } else {
            ringWidth = width;
        }

        midRingWidth = ringWidth/2;
        progressPaint.setStrokeWidth(ringWidth);
//        noProgressPaint.setStrokeWidth(ringWidth + 2);

//        Log.i(TAG, "set ring width: " + ringWidth);

        updateBounds();
    }

    public int getRingWidth() {
        return ringWidth;
    }

    public int getNoRingWidth() {
        return noringWidth;
    }

    private void updateBounds(){

        int minWidth = Math.max(ringWidth, noringWidth)/2;

        bounds = new RectF(minWidth, minWidth, size - minWidth, size- minWidth);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the arc
        float sweepAngle = isInEditMode() ? (progress/maxProgress*360) : (actualProgress/maxProgress*360);

        if (isInView) {
            canvas.drawArc(bounds, 0, 360, false, noProgressPaint);
            if(!indeterminate) {
                canvas.drawArc(bounds, startAngle, sweepAngle, false, progressPaint);
            }else {
                canvas.drawArc(bounds, startAngle + indeterminateRotateOffset, indeterminateSweep, false, progressPaint);
            }
        }
    }

    /**
     * Sets the progress of the progress bar.
     * @param currentProgress the current progress you want to set
     */
    public void setProgress(final float currentProgress) {
        this.progress = currentProgress;
        // Reset the determinate animation to approach the new progress
        if(!indeterminate){

            if(progressAnimator != null && progressAnimator.isRunning()) {
                progressAnimator.cancel();
            }

            progressAnimator = FabUtil.createProgressAnimator(this,actualProgress,currentProgress,this);
            progressAnimator.start();
            if (progressCallback != null) {
                progressCallback.onProgressStarted();
            }
            progressAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (progressCallback != null) {
                        progressCallback.onProgressCompleted();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (progressCallback != null) {
                        progressCallback.onProgressCancelled();
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }
        invalidate();
        isInView = true;
    }


    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }


    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }


    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        progressPaint.setColor(progressColor);
    }

    public void setNoProgressColor(int color) {
        this.noProgressColor = color;
        noProgressPaint.setColor(color);
    }


    /**
     * Starts the progress bar animation.
     * (This is an alias of resetAnimation() so it does the same thing.)
     */
    public void startAnimation() {

        ValueAnimator animator = FabUtil.createRevealAnimator(0.8f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                setScaleY(value);
                setScaleX(value);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (progressCallback != null) {
                    progressCallback.onProgressStarted();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
        isInView = true;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public boolean isInView() {
        return isInView;
    }

    public void stopAnimation(boolean hideProgress){
        if(startAngleRotate != null && startAngleRotate.isRunning()) {
            startAngleRotate.cancel();
        }
        if(progressAnimator != null && progressAnimator.isRunning()) {
            progressAnimator.cancel();
        }
        if(indeterminateAnimator != null && indeterminateAnimator.isRunning()) {
            indeterminateAnimator.cancel();
        }
        if(hideProgress){
            setRingWidth(0, false);
        }else{
            setRingWidth(0,true);
        }
        invalidate();
        isAnimating = false;
    }
    /**
     * Resets the animation.
     */
    public void resetAnimation() {
        stopAnimation(false);
        // Determinate animation
        if(!indeterminate){
            // The cool 360 swoop animation at the start of the animation
            startAngle = -90f;
            startAngleRotate = FabUtil.createStartAngleAnimator(this,-90f,270f,this);
            startAngleRotate.start();
            // The linear animation shown when progress is updated
            actualProgress = 0f;
            progressAnimator = FabUtil.createProgressAnimator(this, actualProgress, progress, this);
            progressAnimator.start();
        }else  { // Indeterminate animation
            startAngle = -90f;
            indeterminateSweep = FabUtil.INDETERMINANT_MIN_SWEEP;
            // Build the whole AnimatorSet
            indeterminateAnimator = new AnimatorSet();
            AnimatorSet prevSet = null, nextSet;
            for(int k=0;k<FabUtil.ANIMATION_STEPS;k++){
                nextSet = FabUtil.createIndeterminateAnimator(this,k,animDuration,this);
                AnimatorSet.Builder builder = indeterminateAnimator.play(nextSet);
                if(prevSet != null) {
                    builder.after(prevSet);
                }
                prevSet = nextSet;
            }

            // Listen to end of animation so we can infinitely loop
            indeterminateAnimator.addListener(new AnimatorListenerAdapter() {
                boolean wasCancelled = false;

                @Override
                public void onAnimationCancel(Animator animation) {
                    wasCancelled = true;
                    isAnimating = false;

                    if (progressCallback != null) {
                        progressCallback.onProgressCancelled();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!wasCancelled) {
                        resetAnimation();
                    }
                }
            });

            indeterminateAnimator.start();
        }

        isAnimating = true;

    }

    @Override
    public void onIndeterminateValuesChanged(float indeterminateSweep, float indeterminateRotateOffset, float startAngle,float progress) {
        if(indeterminateSweep != -1){
            this.indeterminateSweep = indeterminateSweep;
        }
        if(indeterminateRotateOffset != -1){
            this.indeterminateRotateOffset = indeterminateRotateOffset;
        }
        if(startAngle != -1){
            this.startAngle = startAngle;
        }
        if(progress != -1){
            this.actualProgress = progress;
            if(Math.round(actualProgress) == 100 && fabViewListener != null){
                fabViewListener.onProgressCompleted();
            }
        }
    }

    public void setFabViewListener(ProgressFloatingActionButton.OnFabViewListener fabViewListener) {
        this.fabViewListener = fabViewListener;
    }

    public void show() {
        isInView = true;
        invalidate();
    }

    public void hide() {
        isInView = false;
        invalidate();
    }

    private ProgressCallback progressCallback;

    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public interface ProgressCallback {
        void onProgressStarted();
        void onProgressCompleted();
        void onProgressCancelled();
    }
}
