package it.emperor.intervaltraining.ui.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import it.emperor.intervaltraining.utility.Utils;

public class BetterTouchScrollView extends ScrollView {

    private float mOffsetEnd;

    public BetterTouchScrollView(Context context) {
        super(context);
    }

    public BetterTouchScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BetterTouchScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BetterTouchScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mOffsetEnd = Utils.dpToPx(32, getContext());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = super.onInterceptTouchEvent(ev);
        if (ev.getRawX() < getWidth() - mOffsetEnd) {
            if (!intercept) {
                onTouchEvent(ev);
            }
        }
        return intercept;
    }
}
