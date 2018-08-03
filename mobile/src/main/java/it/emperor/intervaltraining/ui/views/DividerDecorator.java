package it.emperor.intervaltraining.ui.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerDecorator extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private boolean mShowOnTop;
    private int mPadding;

    public DividerDecorator(int color) {
        this(color, false);
    }

    public DividerDecorator(int color, boolean showOnTop) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);

        mPadding = 0;
        mShowOnTop = showOnTop;
    }

    public DividerDecorator(int color, int padding) {
        this(color, padding, false);
    }

    public DividerDecorator(int color, int padding, boolean showOnTop) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);

        mPadding = padding;
        mShowOnTop = showOnTop;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int dividerLeft = parent.getPaddingLeft() + mPadding;
        int dividerRight = parent.getWidth() - parent.getPaddingRight() - mPadding;

        if (mShowOnTop) {
            c.drawLine(dividerLeft, 0, dividerRight, 0, mPaint);
        }

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int dividerY = child.getBottom() + params.bottomMargin;

            c.drawLine(dividerLeft, dividerY, dividerRight, dividerY, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            return;
        }

        outRect.top = 1;
    }
}
