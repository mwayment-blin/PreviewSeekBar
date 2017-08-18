package com.github.rubensousa.previewseekbar.base;


import android.os.Build;
import android.util.Log;
import android.view.View;

class PreviewDelegate implements PreviewView.OnPreviewChangeListener {

    private PreviewLayout previewLayout;
    private PreviewAnimator animator;
    private boolean showing;
    private boolean startTouch;
    private boolean setup;
    private boolean autoHideAfterScrub = false;

    public PreviewDelegate(PreviewLayout previewLayout) {
        this.previewLayout = previewLayout;
    }

    public void setup() {
        previewLayout.getPreviewFrameLayout().setVisibility(View.INVISIBLE);
        previewLayout.getMorphView().setVisibility(View.INVISIBLE);
        previewLayout.getFrameView().setVisibility(View.INVISIBLE);
        previewLayout.getPreviewView().addOnPreviewChangeListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.animator = new PreviewAnimatorLollipopImpl(previewLayout);
        } else {
            this.animator = new PreviewAnimatorImpl(previewLayout);
        }
        setup = true;
    }

    public boolean isShowing() {
        return showing;
    }

    public void show() {
        Log.d("PreviewDelegate", "show: showing = " + showing + ", setup = " + setup);
        if (!showing && setup) {
            animator.show();
            showing = true;
        } else if (setup) {
            animator.move();
        }
    }

    public void hide() {
        if (showing) {
            animator.hide();
            showing = false;
        }
    }

    public void setAutoHideAfterScrub(boolean autoHideAfterScrub) {
        this.autoHideAfterScrub = autoHideAfterScrub;
    }

    public boolean isAutoHideAfterScrub() {
        return autoHideAfterScrub;
    }

    @Override
    public void onStartPreview(PreviewView previewView) {
        startTouch = true;
    }

    @Override
    public void onStopPreview(PreviewView previewView) {
        if (autoHideAfterScrub) {
            hide();
        }
        startTouch = false;
    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {
        Log.d("PreviewDelegate", "onPreview: setup = " + setup + ", progress = " + progress +
                ", fromUser: " + fromUser + ", startTouch = " + startTouch + ", showing: " + showing);
        if (setup) {
            animator.move();
            if (!showing && !startTouch) {
                animator.show();
                showing = true;
            }
        }
        startTouch = false;
    }
}
