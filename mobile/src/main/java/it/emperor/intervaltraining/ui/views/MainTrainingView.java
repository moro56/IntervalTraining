package it.emperor.intervaltraining.ui.views;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import io.realm.RealmList;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.models.Repetition;
import it.emperor.intervaltraining.models.Training;
import it.emperor.intervaltraining.utility.Utils;

public class MainTrainingView extends LinearLayout {

    private View mPreparePickerLayout;
    private NumberPicker mPrepareMinutePicker;
    private NumberPicker mPrepareSecondPicker;
    private TextView mPrepareMinute;
    private TextView mPrepareSecond;

    private View mCooldownPickerLayout;
    private NumberPicker mCooldownMinutePicker;
    private NumberPicker mCooldownSecondPicker;
    private TextView mCooldownMinute;
    private TextView mCooldownSecond;

    private List<SingleRepetitionLayout> mRepetitionLayoutList;
    private Training mTraining;

    private HideOthersListener mListener = new HideOthersListener() {
        @Override
        public void hideOther(SingleRepetitionLayout view) {
            if (mPreparePickerLayout.getVisibility() == View.VISIBLE) {
                mPreparePickerLayout.setVisibility(View.GONE);
            } else if (mCooldownPickerLayout.getVisibility() == View.VISIBLE) {
                mCooldownPickerLayout.setVisibility(View.GONE);
            } else {
                for (SingleRepetitionLayout repetitionLayout : mRepetitionLayoutList) {
                    if (repetitionLayout != view) {
                        repetitionLayout.hideShowedPickerLayouts();
                    }
                }
            }
        }
    };

    public MainTrainingView(Context context) {
        super(context);
    }

    public MainTrainingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MainTrainingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MainTrainingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        }

        mPreparePickerLayout = ButterKnife.findById(this, R.id.prepare_picker_layout);
        mPrepareMinutePicker = ButterKnife.findById(this, R.id.prepare_minute_picker);
        mPrepareSecondPicker = ButterKnife.findById(this, R.id.prepare_second_picker);
        mPrepareMinute = ButterKnife.findById(this, R.id.prepare_minute);
        mPrepareSecond = ButterKnife.findById(this, R.id.prepare_second);

        ButterKnife.findById(this, R.id.prepare_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreparePickerLayout.setVisibility(mPreparePickerLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (mCooldownPickerLayout.getVisibility() == View.VISIBLE) {
                    mCooldownPickerLayout.setVisibility(View.GONE);
                }
                for (SingleRepetitionLayout repetitionLayout : mRepetitionLayoutList) {
                    repetitionLayout.hideShowedPickerLayouts();
                }
            }
        });

        mCooldownPickerLayout = ButterKnife.findById(this, R.id.cooldown_picker_layout);
        mCooldownMinutePicker = ButterKnife.findById(this, R.id.cooldown_minute_picker);
        mCooldownSecondPicker = ButterKnife.findById(this, R.id.cooldown_second_picker);
        mCooldownMinute = ButterKnife.findById(this, R.id.cooldown_minute);
        mCooldownSecond = ButterKnife.findById(this, R.id.cooldown_second);

        ButterKnife.findById(this, R.id.cooldown_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCooldownPickerLayout.setVisibility(mCooldownPickerLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (mPreparePickerLayout.getVisibility() == View.VISIBLE) {
                    mPreparePickerLayout.setVisibility(View.GONE);
                }
                for (SingleRepetitionLayout repetitionLayout : mRepetitionLayoutList) {
                    repetitionLayout.hideShowedPickerLayouts();
                }
            }
        });

        mTraining = new Training();
        mTraining.setColor(Color.DKGRAY);
        mRepetitionLayoutList = new ArrayList<>();

        SingleRepetitionLayout repetitionLayout = (SingleRepetitionLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_rep_single, this, false);
        ((MarginLayoutParams) repetitionLayout.getLayoutParams()).topMargin = (int) getResources().getDimension(R.dimen.standard_sub_dimension);
        repetitionLayout.changeDividersColor(mTraining.getColor());
        repetitionLayout.setHideOthersListener(mListener);

        mRepetitionLayoutList.add(repetitionLayout);
        addView(repetitionLayout, getChildCount() - 2);

        initTimeNumberPicker(mPrepareMinutePicker, mPrepareMinute, mPrepareSecondPicker, mPrepareSecond);
        initTimeNumberPicker(mCooldownMinutePicker, mCooldownMinute, mCooldownSecondPicker, mCooldownSecond);
    }

    public void showTraining(Training training) {
        mTraining = training;
        mRepetitionLayoutList.clear();

        if (getChildCount() > 4) {
            removeViews(2, getChildCount() - 4);
        }

        mPrepareMinute.setText(String.format(Locale.getDefault(), "%02d", mTraining.getPrepareTime() / 60));
        mPrepareSecond.setText(String.format(Locale.getDefault(), "%02d", mTraining.getPrepareTime() % 60));
        mCooldownMinute.setText(String.format(Locale.getDefault(), "%02d", mTraining.getCooldownTime() / 60));
        mCooldownSecond.setText(String.format(Locale.getDefault(), "%02d", mTraining.getCooldownTime() % 60));

        for (Repetition repetition : mTraining.getRepetitions()) {
            SingleRepetitionLayout repetitionLayout = (SingleRepetitionLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_rep_single, this, false);
            ((MarginLayoutParams) repetitionLayout.getLayoutParams()).topMargin = (int) getResources().getDimension(R.dimen.standard_sub_dimension);
            repetitionLayout.changeDividersColor(Utils.getColorFromType(getContext(), mTraining.getColor()));
            repetitionLayout.setRepetition(repetition);
            repetitionLayout.setHideOthersListener(mListener);

            mRepetitionLayoutList.add(repetitionLayout);
            addView(repetitionLayout, getChildCount() - 2);
        }

        initTimeNumberPicker(mPrepareMinutePicker, mPrepareMinute, mPrepareSecondPicker, mPrepareSecond);
        initTimeNumberPicker(mCooldownMinutePicker, mCooldownMinute, mCooldownSecondPicker, mCooldownSecond);
    }

    public Training getTraining() {
        updateTraining();
        return mTraining;
    }

    private void initTimeNumberPicker(NumberPicker minute, final TextView minuteText, NumberPicker second, final TextView secondText) {
        int color = Utils.getColorFromType(getContext(), mTraining.getColor());
        minute.setDividerColor(color);
        minute.setValue(Integer.parseInt(minuteText.getText().toString()));
        minute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minuteText.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });
        second.setDividerColor(color);
        second.setValue(Integer.parseInt(secondText.getText().toString()));
        second.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                secondText.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });
    }

    private void updateTraining() {
        int prepareTime = Integer.parseInt(mPrepareSecond.getText().toString()) + 60 * Integer.parseInt(mPrepareMinute.getText().toString());
        mTraining.setPrepareTime(prepareTime);

        int cooldownTime = Integer.parseInt(mCooldownSecond.getText().toString()) + 60 * Integer.parseInt(mCooldownMinute.getText().toString());
        mTraining.setCooldownTime(cooldownTime);

        int numCount = 0;
        mTraining.setRepetitions(new RealmList<Repetition>());
        for (SingleRepetitionLayout repetitionLayout : mRepetitionLayoutList) {
            Repetition repetition = repetitionLayout.getRepetition();
            mTraining.getRepetitions().add(repetition);

            numCount += repetition.getCount();
        }
        if (prepareTime != 0) {
            numCount++;
        }
        if (cooldownTime != 0) {
            numCount++;
        }
        mTraining.setNumRepetition(numCount);
    }
}
