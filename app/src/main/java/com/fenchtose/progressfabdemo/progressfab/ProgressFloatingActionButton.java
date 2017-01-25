package com.fenchtose.progressfabdemo.progressfab;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.fenchtose.progressfabdemo.R;

/**
 * Created by Jay Rambhia on 3/21/16.
 */
public class ProgressFloatingActionButton extends FrameLayout {

    private static final String TAG = "PFAB";
    private FloatingActionButton fab;
    private ProgressRingView progressBar;

    private boolean changeFABColor = true;
    private boolean animateColor = true;
    private int normalColor = 0xfff95b4b;
    private int progressColor = 0xffffffff;

    private Drawable fabImageDrawable;

    private Context context;

    /*public ProgressFloatingActionButton(Context context) {
        super(context);
        init(context, null);
    }*/

    public ProgressFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        Log.d(TAG, "pfab 2 called");
    }

    public ProgressFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        Log.d(TAG, "pfab 3 called");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
        Log.d(TAG, "pfab 4 called");
    }

    public ProgressFloatingActionButton(Activity context) {
        super(context);
        init(context, null);
    }

    private void init(final Context context, final AttributeSet attrs) {
        this.context = context;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressFloatingActionButton);
            if (a != null) {
                normalColor = a.getColor(R.styleable.ProgressFloatingActionButton_fbb_normalColor, normalColor);
                progressColor = a.getColor(R.styleable.ProgressFloatingActionButton_fbb_inprogressColor, progressColor);
                changeFABColor = a.getBoolean(R.styleable.ProgressFloatingActionButton_fbb_change_color, true);
                animateColor = a.getBoolean(R.styleable.ProgressFloatingActionButton_fbb_animate_color, true);
            }
        }

//        fab = (FloatingActionButton) LayoutInflater.from(context).inflate(R.layout.fab_layout, null, false);
        fab = new FloatingActionButton(context, attrs);
        this.addView(fab, generateDefaultParams());
        fab.setBackgroundTintList(ColorStateList.valueOf(normalColor));

        // Fuck you compat padding
//        fab.setUseCompatPadding(true);

        progressBar = new ProgressRingView(context, attrs);
        progressBar.setFabViewListener(new OnFabViewListener() {
            @Override
            public void onProgressCompleted() {
                progressBar.stopAnimation(true);

            }
        });

        final float density = context.getResources().getDisplayMetrics().density;

        fab.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fab.getViewTreeObserver().removeOnPreDrawListener(this);

                addView(progressBar, 0, getProgressbarParams(DimensionUtils.dpToPx(56, density) + progressBar.getRingWidth()));
                requestLayout();
                ViewCompat.setElevation(progressBar, fab.getCompatElevation());

                return true;
            }
        });

        progressBar.setProgressCallback(new ProgressRingView.ProgressCallback() {
            @Override
            public void onProgressStarted() {
                Log.d(TAG, "progress started");
                animateFade(normalColor, progressColor);
            }

            @Override
            public void onProgressCompleted() {
                Log.d(TAG, "progress completed");
                animateFade(progressColor, normalColor);
            }

            @Override
            public void onProgressCancelled() {
                Log.d(TAG, "progress cancelled");
                animateFade(progressColor, normalColor);
            }
        });
    }

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = fab.getWidth();
        int height = fab.getHeight();

        if (width == 0 || height == 0) {
            return;
        }

//        if (progressBar != null && progressBar.isInView()) {

            int ringWidth = Math.max(progressBar.getRingWidth(), progressBar.getNoRingWidth());

            width += ringWidth + 16;
            height += ringWidth + 16;
//        } else {
//            return;
//        }

        Log.i(TAG, "width: " + width);

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }*/

    private LayoutParams generateDefaultParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

    private LayoutParams getProgressbarParams(int size) {
        LayoutParams params = new LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        return params;
    }

    public void startProgressAnimation() {
        if (progressBar != null) {
            progressBar.startAnimation();
            progressBar.setVisibility(VISIBLE);
        }
    }

    public void stopProgressAnimation() {
        if (progressBar != null) {
            progressBar.stopAnimation(true);
            progressBar.setVisibility(GONE);
        }
    }

    public boolean isAnimating() {
        return progressBar != null && progressBar.isAnimating();
    }

    public void setImageResource(@DrawableRes int resId) {
        setImageDrawable(ContextCompat.getDrawable(context, resId));
//        fab.setImageResource(resId);
    }

    public void setProgress(float progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
            requestLayout();
//            animateFade(normalColor, progressColor);
        }
    }

    public void setMaxProgress(float progress) {
        if (progressBar != null) {
            progressBar.setMaxProgress(progress);
        }
    }

    public void setProgressDuration(int duration) {
        if (progressBar != null) {
            progressBar.setAnimDuration(duration);
        }
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public void hideProgress() {
        progressBar.hide();
    }

    public void showProgress() {
        progressBar.show();
    }

    public void setIndeterminate(boolean status) {
        progressBar.setIndeterminate(status);
    }

    public void reset() {
        if (progressBar != null) {
            progressBar.resetAnimation();
            if (!changeFABColor && !progressBar.isAnimating()) {
                changeColor(progressColor, normalColor, false);
            }
        }
    }

    public interface OnFabViewListener {
        void onProgressCompleted();
    }

    public void setFabViewListener(OnFabViewListener listener) {
        progressBar.setFabViewListener(listener);
    }

    private void animateFade(int currentColor, int endColor) {
        if (!changeFABColor) {
            return;
        }

        changeColor(currentColor, endColor, animateColor);
    }

    public void changeColor(int currentColor, int endColor, boolean animate) {

        fab.clearAnimation();

        if (animate) {
            ObjectAnimator animator = FabUtil.createTintAnimator(fab, currentColor, endColor);
            animator.setDuration(300);
            animator.start();
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(endColor));
        }

        if (fabImageDrawable != null) {
            fabImageDrawable.setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);
            fab.setImageDrawable(fabImageDrawable);
        }

    }

    public void setImageDrawable(Drawable drawable) {
        if (drawable != null) {
            this.fabImageDrawable = drawable.mutate();
        } else {
            this.fabImageDrawable = null;
        }
        fab.setImageDrawable(fabImageDrawable);
    }
}
