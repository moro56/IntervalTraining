package it.emperor.intervaltraining.ui.training;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import org.parceler.Parcels;

import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import it.emperor.intervaltraining.App;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.models.Repetition;
import it.emperor.intervaltraining.ui.base.BaseActivity;
import it.emperor.intervaltraining.utility.Utils;

public class Act_RepetitionCreate extends BaseActivity {

    @BindView(R.id.statusBar)
    View mStatusBarView;
    @BindView(R.id.reveal)
    View mRevealView;

    @OnClick(R.id.layout)
    void layoutClicked() {
        if (mRestLayout.getVisibility() == View.VISIBLE) {
            mRestLayout.setVisibility(View.GONE);
        } else if (mWorkLayout.getVisibility() == View.VISIBLE) {
            mWorkLayout.setVisibility(View.GONE);
        } else if (mCountLayout.getVisibility() == View.VISIBLE) {
            mCountLayout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.add)
    void addClicked() {
        addRepetition();
    }

    @BindView(R.id.work_picker_layout)
    View mWorkLayout;
    @BindView(R.id.work_minute_picker)
    NumberPicker mWorkMinutePicker;
    @BindView(R.id.work_second_picker)
    NumberPicker mWorkSecondPicker;
    @BindView(R.id.work_minute)
    TextView mWorkMinute;
    @BindView(R.id.work_second)
    TextView mWorkSecond;

    @OnClick(R.id.work_layout)
    void workLayoutClicked() {
        mWorkLayout.setVisibility(mWorkLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (mRestLayout.getVisibility() == View.VISIBLE) {
            mRestLayout.setVisibility(View.GONE);
        } else if (mCountLayout.getVisibility() == View.VISIBLE) {
            mCountLayout.setVisibility(View.GONE);
        }
    }

    @BindView(R.id.rest_picker_layout)
    View mRestLayout;
    @BindView(R.id.rest_minute_picker)
    NumberPicker mRestMinutePicker;
    @BindView(R.id.rest_second_picker)
    NumberPicker mRestSecondPicker;
    @BindView(R.id.rest_minute)
    TextView mRestMinute;
    @BindView(R.id.rest_second)
    TextView mRestSecond;

    @OnClick(R.id.rest_layout)
    void restLayoutClicked() {
        mRestLayout.setVisibility(mRestLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (mWorkLayout.getVisibility() == View.VISIBLE) {
            mWorkLayout.setVisibility(View.GONE);
        } else if (mCountLayout.getVisibility() == View.VISIBLE) {
            mCountLayout.setVisibility(View.GONE);
        }
    }

    @BindView(R.id.count_picker_layout)
    View mCountLayout;
    @BindView(R.id.count_picker)
    NumberPicker mCountPicker;
    @BindView(R.id.count)
    TextView mCount;

    @OnClick(R.id.count_layout)
    void countLayoutClicked() {
        mCountLayout.setVisibility(mCountLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (mWorkLayout.getVisibility() == View.VISIBLE) {
            mWorkLayout.setVisibility(View.GONE);
        } else if (mRestLayout.getVisibility() == View.VISIBLE) {
            mRestLayout.setVisibility(View.GONE);
        }
    }

    // SUPPORT

    @Inject
    Realm realm;

    private Repetition mRepetition;
    private int mColor;

    private static final String PARAM_REPETITION = "paramRepetition";
    private static final String PARAM_COLOR = "paramColor";

    // SYSTEM

    public static Intent newIntent(int color) {
        Intent intent = new Intent(App.context(), Act_RepetitionCreate.class);
        intent.putExtra(PARAM_COLOR, color);
        return intent;
    }

    public static Intent newIntentEdit(int color, Repetition repetition) {
        Intent intent = new Intent(App.context(), Act_RepetitionCreate.class);
        intent.putExtra(PARAM_REPETITION, Parcels.wrap(repetition));
        intent.putExtra(PARAM_COLOR, color);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRevealView.setBackgroundColor(mColor);
        if (mRepetition != null) {
            setTitle(getString(R.string.app_edit_repetition));
        }

        if (Utils.isLollipop()) {
            mToolbar.post(new Runnable() {
                @Override
                public void run() {
                    int statusBarHeight = Utils.getStatusBarHeight(Act_RepetitionCreate.this);
                    int toolbarHeight = mToolbar.getHeight();

                    mStatusBarView.getLayoutParams().height = statusBarHeight;
                    mRevealView.getLayoutParams().height = toolbarHeight + statusBarHeight;
                }
            });
        } else {
            mStatusBarView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_repetition_create;
    }

    @Override
    protected void initVariables() {
        mRepetition = null;
        mColor = Color.BLUE;
    }

    @Override
    protected void loadParameters(Bundle extras) {
        if (extras.containsKey(PARAM_REPETITION)) {
            mRepetition = Parcels.unwrap(extras.getParcelable(PARAM_REPETITION));
        }
        mColor = extras.getInt(PARAM_COLOR);
    }

    @Override
    protected void loadInfos(Bundle savedInstanceState) {

    }

    @Override
    protected void saveInfos(Bundle outState) {

    }

    @Override
    protected void initialize() {
        mWorkMinutePicker.setDividerColor(mColor);
        mWorkMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mWorkMinute.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });
        mWorkSecondPicker.setDividerColor(mColor);
        mWorkSecondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mWorkSecond.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });

        mRestMinutePicker.setDividerColor(mColor);
        mRestMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mRestMinute.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });
        mRestSecondPicker.setDividerColor(mColor);
        mRestSecondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mRestSecond.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });

        mCountPicker.setDividerColor(mColor);
        mCountPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mCount.setText(String.valueOf(newVal));
            }
        });

        if (mRepetition == null) {
            mWorkMinutePicker.setValue(Integer.parseInt(mWorkMinute.getText().toString()));
            mWorkSecondPicker.setValue(Integer.parseInt(mWorkSecond.getText().toString()));
            mRestMinutePicker.setValue(Integer.parseInt(mRestMinute.getText().toString()));
            mRestSecondPicker.setValue(Integer.parseInt(mRestSecond.getText().toString()));
            mCountPicker.setValue(Integer.parseInt(mCount.getText().toString()));
        } else {
            mWorkMinutePicker.setValue(mRepetition.getWorkTime() / 60);
            mWorkSecondPicker.setValue(mRepetition.getWorkTime() % 60);
            mRestMinutePicker.setValue(mRepetition.getRestTime() / 60);
            mRestSecondPicker.setValue(mRepetition.getRestTime() % 60);
            mCountPicker.setValue(mRepetition.getCount());
        }
    }

    // FUNCTIONS

    private void addRepetition() {
        Repetition repetition = new Repetition();
        repetition.setRepetitionId(UUID.randomUUID().toString());

        int workTime = Integer.parseInt(mWorkSecond.getText().toString()) + 60 * Integer.parseInt(mWorkMinute.getText().toString());
        repetition.setWorkTime(workTime);

        int restTime = Integer.parseInt(mRestSecond.getText().toString()) + 60 * Integer.parseInt(mRestMinute.getText().toString());
        repetition.setRestTime(restTime);

        int countTime = Integer.parseInt(mCount.getText().toString());
        repetition.setCount(countTime);

        Intent intent = new Intent();
        intent.putExtra("result", Parcels.wrap(repetition));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
