package it.emperor.intervaltraining.ui.views;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.Locale;
import java.util.UUID;

import butterknife.ButterKnife;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.models.Repetition;

public class SingleRepetitionLayout extends LinearLayout {

    private HideOthersListener mListener;

    private View mWorkLayout;
    private View mWorkPickerLayout;
    private NumberPicker mWorkMinutePicker;
    private NumberPicker mWorkSecondPicker;
    private TextView mWorkMinute;
    private TextView mWorkSecond;

    private View mRestLayout;
    private View mRestPickerLayout;
    private NumberPicker mRestMinutePicker;
    private NumberPicker mRestSecondPicker;
    private TextView mRestMinute;
    private TextView mRestSecond;

    private View mCountLayout;
    private View mCountPickerLayout;
    private NumberPicker mCountPicker;
    private TextView mCount;

    public SingleRepetitionLayout(Context context) {
        super(context);
    }

    public SingleRepetitionLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleRepetitionLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SingleRepetitionLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition != null) {
            layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        }

        mWorkLayout = ButterKnife.findById(this, R.id.work_layout);
        mWorkPickerLayout = ButterKnife.findById(this, R.id.work_picker_layout);
        mWorkMinutePicker = ButterKnife.findById(this, R.id.work_minute_picker);
        mWorkSecondPicker = ButterKnife.findById(this, R.id.work_second_picker);
        mWorkMinute = ButterKnife.findById(this, R.id.work_minute);
        mWorkSecond = ButterKnife.findById(this, R.id.work_second);

        mRestLayout = ButterKnife.findById(this, R.id.rest_layout);
        mRestPickerLayout = ButterKnife.findById(this, R.id.rest_picker_layout);
        mRestMinutePicker = ButterKnife.findById(this, R.id.rest_minute_picker);
        mRestSecondPicker = ButterKnife.findById(this, R.id.rest_second_picker);
        mRestMinute = ButterKnife.findById(this, R.id.rest_minute);
        mRestSecond = ButterKnife.findById(this, R.id.rest_second);

        mCountLayout = ButterKnife.findById(this, R.id.count_layout);
        mCountPickerLayout = ButterKnife.findById(this, R.id.count_picker_layout);
        mCountPicker = ButterKnife.findById(this, R.id.count_picker);
        mCount = ButterKnife.findById(this, R.id.count);

        initTimeNumberPicker(mWorkMinutePicker, mWorkMinute, mWorkSecondPicker, mWorkSecond);
        initTimeNumberPicker(mRestMinutePicker, mRestMinute, mRestSecondPicker, mRestSecond);

        mCountPicker.setValue(Integer.parseInt(mCount.getText().toString()));
        mCountPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mCount.setText(String.valueOf(newVal));
            }
        });

        mWorkLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mWorkPickerLayout.setVisibility(mWorkPickerLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (mRestPickerLayout.getVisibility() == View.VISIBLE) {
                    mRestPickerLayout.setVisibility(View.GONE);
                } else if (mCountPickerLayout.getVisibility() == View.VISIBLE) {
                    mCountPickerLayout.setVisibility(View.GONE);
                }

                if (mListener != null) {
                    mListener.hideOther(SingleRepetitionLayout.this);
                }
            }
        });
        mRestLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mRestPickerLayout.setVisibility(mRestPickerLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (mWorkPickerLayout.getVisibility() == View.VISIBLE) {
                    mWorkPickerLayout.setVisibility(View.GONE);
                } else if (mCountPickerLayout.getVisibility() == View.VISIBLE) {
                    mCountPickerLayout.setVisibility(View.GONE);
                }

                if (mListener != null) {
                    mListener.hideOther(SingleRepetitionLayout.this);
                }
            }
        });
        mCountLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCountPickerLayout.setVisibility(mCountPickerLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (mRestPickerLayout.getVisibility() == View.VISIBLE) {
                    mRestPickerLayout.setVisibility(View.GONE);
                } else if (mWorkPickerLayout.getVisibility() == View.VISIBLE) {
                    mWorkPickerLayout.setVisibility(View.GONE);
                }

                if (mListener != null) {
                    mListener.hideOther(SingleRepetitionLayout.this);
                }
            }
        });
    }

    private void initTimeNumberPicker(NumberPicker minute, final TextView minuteText, NumberPicker second, final TextView secondText) {
        minute.setValue(Integer.parseInt(minuteText.getText().toString()));
        minute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minuteText.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });
        second.setValue(Integer.parseInt(secondText.getText().toString()));
        second.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                secondText.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });
    }

    public void changeDividersColor(int color) {
        mWorkMinutePicker.setDividerColor(color);
        mWorkMinutePicker.invalidate();
        mWorkSecondPicker.setDividerColor(color);
        mWorkSecondPicker.invalidate();
        mRestMinutePicker.setDividerColor(color);
        mRestMinutePicker.invalidate();
        mRestSecondPicker.setDividerColor(color);
        mRestSecondPicker.invalidate();
        mCountPicker.setDividerColor(color);
        mCountPicker.invalidate();
    }

    public Repetition getRepetition() {
        Repetition repetition = new Repetition();
        repetition.setRepetitionId(UUID.randomUUID().toString());
        repetition.setOrder(0);

        int workTime = Integer.parseInt(mWorkSecond.getText().toString()) + 60 * Integer.parseInt(mWorkMinute.getText().toString());
        repetition.setWorkTime(workTime);

        int restTime = Integer.parseInt(mRestSecond.getText().toString()) + 60 * Integer.parseInt(mRestMinute.getText().toString());
        repetition.setRestTime(restTime);

        int countTime = Integer.parseInt(mCount.getText().toString());
        repetition.setCount(countTime);

        return repetition;
    }

    public void setRepetition(Repetition repetition) {
        mWorkMinute.setText(String.format(Locale.getDefault(), "%02d", repetition.getWorkTime() / 60));
        mWorkSecond.setText(String.format(Locale.getDefault(), "%02d", repetition.getWorkTime() % 60));
        mRestMinute.setText(String.format(Locale.getDefault(), "%02d", repetition.getRestTime() / 60));
        mRestSecond.setText(String.format(Locale.getDefault(), "%02d", repetition.getRestTime() % 60));
        mCount.setText(String.valueOf(repetition.getCount()));
    }

    public void hideShowedPickerLayouts() {
        if (mRestPickerLayout.getVisibility() == View.VISIBLE) {
            mRestPickerLayout.setVisibility(View.GONE);
        } else if (mWorkPickerLayout.getVisibility() == View.VISIBLE) {
            mWorkPickerLayout.setVisibility(View.GONE);
        } else if (mCountPickerLayout.getVisibility() == View.VISIBLE) {
            mCountPickerLayout.setVisibility(View.GONE);
        }
    }

    public void setHideOthersListener(HideOthersListener listener) {
        mListener = listener;
    }
}
