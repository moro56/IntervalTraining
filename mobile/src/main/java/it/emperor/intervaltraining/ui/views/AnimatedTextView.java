package it.emperor.intervaltraining.ui.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnimatedTextView extends View {

    private Paint mPaint;
    private ValueAnimator mAnimatorDimensions;
    private ValueAnimator mAnimatorCharacter;

    private List<AnimatedCharacter> mCharList;
    private List<AnimatedCharacter> mOldCharList;

    private String mText;
    private float mTextSize;
    private boolean mCenterText;
    private int mDuration;
    private int mLineHeight;
    private int mLineDescent;

    private int mWidth;
    private int mLastWidth;
    private int mWidthTemp;
    private int mHeight;
    private int mLastHeight;
    private int mHeightTemp;
    private int mStartOffsetX;
    private int mStartOffsetY;

    private static final int DF_ANIM_DURATION = 250;

    public AnimatedTextView(Context context) {
        super(context);
    }

    public AnimatedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimatedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setColor(Color.BLACK);
        setTextSize(16);
        mCenterText = true;

        mCharList = new ArrayList<>();
        mOldCharList = new ArrayList<>();
        mLastWidth = 0;
        mWidthTemp = 0;
        mLastHeight = 0;
        mHeightTemp = 0;
        mStartOffsetX = 0;
        mStartOffsetY = 0;

        mDuration = DF_ANIM_DURATION;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                result = MeasureSpec.getSize(measureSpec);
                break;
            case MeasureSpec.AT_MOST:
                if (mText != null) {
                    result = calculateWidth(mText) + getPaddingLeft() + getPaddingRight();
                    if (result < getMinimumWidth()) {
                        result = getMinimumWidth();
                    }
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        if (mAnimatorDimensions != null && mAnimatorDimensions.isRunning()) {
            return mLastWidth + mWidthTemp;
        }

        mWidth = result;
        if (mLastWidth == 0) {
            mLastWidth = mWidth;
        }
        return mLastWidth + mWidthTemp;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                result = MeasureSpec.getSize(measureSpec);
                break;
            case MeasureSpec.AT_MOST:
                result = calculateHeight("j") + mLineDescent + getPaddingTop() + getPaddingBottom();
                if (result < getMinimumHeight()) {
                    result = getMinimumHeight();
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        mHeight = result;
        if (mLastHeight == 0) {
            mLastHeight = mHeight;
        }
        return mLastHeight + mHeightTemp;
    }

    public void setValue(long value) {
        setValue(String.valueOf(value));
    }

    public void setValue(int value) {
        setValue(String.valueOf(value));
    }

    public void setValue(String value) {
        if (mText == null) {
            int startX = getPaddingLeft();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                int width = calculateWidth(String.valueOf(c));
                AnimatedCharacter animatedCharacter = new AnimatedCharacter();
                animatedCharacter.setPosition(i);
                animatedCharacter.setCharacter(c);
                animatedCharacter.setPosX(startX);
                animatedCharacter.setPosY(0);
                animatedCharacter.setAnimateAlpha(false);

                startX += width;

                mCharList.add(animatedCharacter);
            }

            mText = value;
            requestLayout();

            mLastWidth = mWidth;
            mLastHeight = mHeight;

            final int finalStartX = startX;
            post(new Runnable() {
                @Override
                public void run() {
                    mStartOffsetX = (mWidth - finalStartX - getPaddingLeft()) / 2;
                    mStartOffsetY = (mHeight - mLineHeight - mLineDescent) / 2;
                    requestLayout();
                }
            });
        } else {
            if (mAnimatorDimensions != null && mAnimatorDimensions.isRunning()) {
                mAnimatorDimensions.cancel();
            }
            if (mAnimatorCharacter != null && mAnimatorCharacter.isRunning()) {
                mAnimatorCharacter.cancel();
            }

            List<AnimatedCharacter> mTempCharList = new ArrayList<>();
            mOldCharList.clear();

            int startX = getPaddingLeft();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                int width = calculateWidth(String.valueOf(c));

                AnimatedCharacter animatedCharacter = findCharacter(c);
                if (animatedCharacter != null) {
                    animatedCharacter.setPosition(i);
                    animatedCharacter.setTranslationX(startX - animatedCharacter.getPosX());
                    animatedCharacter.setAnimateAlpha(false);
                } else {
                    animatedCharacter = new AnimatedCharacter();
                    animatedCharacter.setPosition(i);
                    animatedCharacter.setCharacter(c);
                    animatedCharacter.setPosX(startX);
                    animatedCharacter.setPosY(0);
                    animatedCharacter.setAnimateAlpha(true);
                }

                startX += width;

                mTempCharList.add(animatedCharacter);
            }
            mOldCharList = mCharList;
            mCharList = mTempCharList;
            for (AnimatedCharacter animatedCharacter : mOldCharList) {
                animatedCharacter.setAnimateAlpha(true);
            }

            mText = value;
            requestLayout();
            final int finalStartX = startX;
            post(new Runnable() {
                @Override
                public void run() {
                    mStartOffsetX = (mWidth - finalStartX - getPaddingLeft()) / 2;
                    mStartOffsetY = (mHeight - mLineHeight - mLineDescent) / 2;
                    requestLayout();
                    try {
                        animateDimensions();
                    } catch (Exception ignored) {
                    }
                }
            });

            animateCharacters();
        }
    }

    private AnimatedCharacter findCharacter(char character) {
        int index = -1;
        for (int i = 0; i < mCharList.size(); i++) {
            if (mCharList.get(i).getChar() == character) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return null;
        } else {
            return mCharList.remove(index);
        }
    }

    private void animateDimensions() {
        if (mLastWidth == mWidth && mLastHeight == mHeight) {
            return;
        }

        mAnimatorDimensions = ValueAnimator.ofFloat(0, 1);
        mAnimatorDimensions.setDuration(mDuration / 2);
        mAnimatorDimensions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mWidthTemp = (int) (valueAnimator.getAnimatedFraction() * (mWidth - mLastWidth));
                mHeightTemp = (int) (valueAnimator.getAnimatedFraction() * (mHeight - mLastHeight));
                requestLayout();
            }
        });
        mAnimatorDimensions.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mWidthTemp = 0;
                mHeightTemp = 0;
                mLastWidth = mWidth;
                mLastHeight = mHeight;
                requestLayout();
            }
        });
        mAnimatorDimensions.start();
    }

    private void animateCharacters() {
        mAnimatorCharacter = ValueAnimator.ofFloat(0, 1);
        mAnimatorCharacter.setDuration(mDuration);
        mAnimatorCharacter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                for (AnimatedCharacter animatedCharacter : mOldCharList) {
                    if (animatedCharacter.isAnimateAlpha()) {
                        animatedCharacter.setAlpha(255f - valueAnimator.getAnimatedFraction() * 255f);
                        animatedCharacter.setScale((1f - valueAnimator.getAnimatedFraction()));
                    }
                }
                for (AnimatedCharacter animatedCharacter : mCharList) {
                    if (animatedCharacter.isAnimateAlpha()) {
                        animatedCharacter.setAlpha(valueAnimator.getAnimatedFraction() * 255f);
                        animatedCharacter.setScale(valueAnimator.getAnimatedFraction());
                    }
                    if (animatedCharacter.getTranslationX() != 0) {
                        animatedCharacter.setTranslationXTemp(valueAnimator.getAnimatedFraction() * animatedCharacter.getTranslationX());
                    }
                }
                invalidate();
            }
        });
        mAnimatorCharacter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mOldCharList.clear();
                for (AnimatedCharacter animatedCharacter : mCharList) {
                    animatedCharacter.setPosX(animatedCharacter.getPosX() + animatedCharacter.getTranslationXTemp());
                    animatedCharacter.setTranslationXTemp(0);
                    animatedCharacter.setTranslationX(0);
                    animatedCharacter.setAlpha(255f);
                    animatedCharacter.setScale(1f);
                }
                invalidate();
            }
        });
        mAnimatorCharacter.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (AnimatedCharacter animatedCharacter : mOldCharList) {
            drawCharacter(canvas, animatedCharacter);
        }

        for (AnimatedCharacter animatedCharacter : mCharList) {
            drawCharacter(canvas, animatedCharacter);
        }
    }

    // Disegno il carattere
    private void drawCharacter(Canvas canvas, AnimatedCharacter animatedCharacter) {
        String text = String.format(Locale.getDefault(), "%c", animatedCharacter.getChar());

        // Scale il testo verticalmente
        mPaint.setAlpha((int) animatedCharacter.getAlpha());
        mPaint.setTextSize(mTextSize * animatedCharacter.getScale());

        //Disegno il testo
        canvas.drawText(text,
                (mCenterText ? mStartOffsetX : 0) + animatedCharacter.getPosX() + animatedCharacter.getTranslationXTemp(),
                (mCenterText ? mStartOffsetY : 0) + animatedCharacter.getPosY() + mLineHeight / 2 + mLineHeight / 2 * animatedCharacter.getScale(),
                mPaint);
    }

    // SETTINGS

    public void setTextColor(int color) {
        mPaint.setColor(color);
    }

    public void setTextSize(int size) {
        mTextSize = size * getContext().getResources().getDisplayMetrics().density;
        mPaint.setTextSize(mTextSize);
        mLineHeight = calculateHeight("j");
    }

    public void setTextAppearance(int resource) {
        TextView textView = new TextView(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(resource);
        } else {
            textView.setTextAppearance(getContext(), resource);
        }
        mPaint.setColor(textView.getCurrentTextColor());
        mPaint.setTextSize(textView.getTextSize());
        mPaint.setTypeface(textView.getTypeface());
    }

    public void setTypeface(Typeface typeface) {
        mPaint.setTypeface(typeface);
    }

    public void setCenterText(boolean value) {
        mCenterText = value;
        requestLayout();
    }

    // UTILITY

    private int calculateWidth(String text) {
        mPaint.setTextSize(mTextSize);
        return (int) mPaint.measureText(text);
    }

    private int calculateHeight(String text) {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float height = -fm.ascent;
        mLineDescent = (int) fm.descent;
        return (int) height;
    }

    private float toRange(float value, float oldMin, float oldMax, float newMin, float newMax) {
        return (((value - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
    }

    // CLASSES

    private class AnimatedCharacter {

        private int position;
        private char character;
        private float posX;
        private float posY;
        private float translationX;
        private float translationXTemp;
        private float alpha;
        private float scale;
        private boolean animateAlpha;

        AnimatedCharacter() {
            this.alpha = 255;
            this.translationXTemp = 0;
            this.scale = 1f;
        }

        public int getPosition() {
            return position;
        }

        void setPosition(int position) {
            this.position = position;
        }

        public char getCharacter() {
            return character;
        }

        void setCharacter(char character) {
            this.character = character;
        }

        float getPosX() {
            return posX;
        }

        void setPosX(float posX) {
            this.posX = posX;
        }

        void setPosY(float posY) {
            this.posY = posY;
        }

        float getPosY() {
            return posY;
        }

        char getChar() {
            return character;
        }

        float getTranslationX() {
            return translationX;
        }

        void setTranslationX(float translationX) {
            this.translationX = translationX;
        }

        float getTranslationXTemp() {
            return translationXTemp;
        }

        void setTranslationXTemp(float translationXTemp) {
            this.translationXTemp = translationXTemp;
        }

        float getAlpha() {
            if (this.alpha < 127) {
                return 0f;
            }
            return toRange(this.alpha, 127f, 255f, 0f, 255f);
        }

        void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        void setScale(float scale) {
            this.scale = scale;
        }

        boolean isAnimateAlpha() {
            return animateAlpha;
        }

        void setAnimateAlpha(boolean animateAlpha) {
            this.animateAlpha = animateAlpha;
            if (!animateAlpha) {
                this.alpha = 255;
            }
        }

        float getScale() {
            return toRange(this.scale, 0f, 1f, 0.5f, 1f);
        }
    }
}
