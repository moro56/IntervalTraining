package it.emperor.intervaltraining.ui.training;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import it.emperor.intervaltraining.App;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.events.AddRepetitionClickedEvent;
import it.emperor.intervaltraining.events.ColorChangedEvent;
import it.emperor.intervaltraining.events.EditRepetitionClickedEvent;
import it.emperor.intervaltraining.models.Repetition;
import it.emperor.intervaltraining.models.Training;
import it.emperor.intervaltraining.ui.base.BaseActivity;
import it.emperor.intervaltraining.ui.training.adapters.ColorAdapter;
import it.emperor.intervaltraining.ui.views.HideOthersListener;
import it.emperor.intervaltraining.ui.views.MultipleRepetitionLayout;
import it.emperor.intervaltraining.ui.views.SingleRepetitionLayout;
import it.emperor.intervaltraining.utility.Constants;
import it.emperor.intervaltraining.utility.Utils;

public class Act_TrainingCreate extends BaseActivity {

    @BindView(R.id.scrollview)
    ScrollView mScrollView;
    @BindView(R.id.main_layout)
    LinearLayout mMainLayout;
    @BindView(R.id.reveal)
    View mRevealView;
    @BindView(R.id.revealBis)
    View mRevealViewBis;
    @BindView(R.id.statusBar)
    View mStatusBarView;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.title)
    EditText mTitle;
    @BindView(R.id.save)
    Button mSave;

    @OnClick(R.id.save)
    void saveClicked() {
        if (mTraining != null) {
            update();
        } else {
            create();
        }
    }

    @BindView(R.id.single_layout)
    SingleRepetitionLayout mTabSingleLayout;
    @BindView(R.id.multiple_layout)
    MultipleRepetitionLayout mTabMultipleLayout;
    @BindView(R.id.selected)
    View mTabSelected;
    @BindView(R.id.type_tab_single)
    TextView mTabSingleText;
    @BindView(R.id.type_tab_multiple)
    TextView mTabMultipleText;

    @OnClick(R.id.type_tab_single)
    void tabSingleClicked() {
        translateTab(true);
    }

    @OnClick(R.id.type_tab_multiple)
    void tabMultipleClicked() {
        translateTab(false);
    }

    @BindView(R.id.prepare_picker_layout)
    View mPreparePickerLayout;
    @BindView(R.id.prepare_minute_picker)
    NumberPicker mPrepareMinutePicker;
    @BindView(R.id.prepare_second_picker)
    NumberPicker mPrepareSecondPicker;
    @BindView(R.id.prepare_minute)
    TextView mPrepareMinute;
    @BindView(R.id.prepare_second)
    TextView mPrepareSecond;

    @OnClick(R.id.prepare_layout)
    void prepareLayoutClicked() {
        mPreparePickerLayout.setVisibility(mPreparePickerLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (mCooldownPickerLayout.getVisibility() == View.VISIBLE) {
            mCooldownPickerLayout.setVisibility(View.GONE);
        }
        mTabSingleLayout.hideShowedPickerLayouts();
    }

    @BindView(R.id.cooldown_picker_layout)
    View mCooldownPickerLayout;
    @BindView(R.id.cooldown_minute_picker)
    NumberPicker mCooldownMinutePicker;
    @BindView(R.id.cooldown_second_picker)
    NumberPicker mCooldownSecondPicker;
    @BindView(R.id.cooldown_minute)
    TextView mCooldownMinute;
    @BindView(R.id.cooldown_second)
    TextView mCooldownSecond;

    @OnClick(R.id.cooldown_layout)
    void cooldownLayoutClicked() {
        mCooldownPickerLayout.setVisibility(mCooldownPickerLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (mPreparePickerLayout.getVisibility() == View.VISIBLE) {
            mPreparePickerLayout.setVisibility(View.GONE);
        }
        mTabSingleLayout.hideShowedPickerLayouts();
    }

    private Button mSupButton;

    // SUPPORT

    @Inject
    Realm realm;

    private Training mTraining;
    private List<Drawable> mDrawablesTab;
    private String mTrainingId;
    private boolean mTabGoToRight;
    private int mLastColor;
    private int mLastColorType;
    private int mLastPosition;

    private static final String PARAM_TRAINING_ID = "paranTrainingId";
    private static final String PARAM_COLOR = "paranColor";

    private static final int CODE_CREATE = 1001;
    private static final int CODE_EDIT = 1002;

    // SYSTEM

    public static Intent newIntentEdit(int color, String id) {
        Intent intent = new Intent(App.context(), Act_TrainingCreate.class);
        intent.putExtra(PARAM_TRAINING_ID, id);
        intent.putExtra(PARAM_COLOR, color);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUseBus(true);

        LayoutTransition layoutTransition = mMainLayout.getLayoutTransition();
        if (layoutTransition != null) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        }

        if (Utils.isLollipop()) {
            mToolbar.post(new Runnable() {
                @Override
                public void run() {
                    int statusBarHeight = Utils.getStatusBarHeight(Act_TrainingCreate.this);
                    int toolbarHeight = mToolbar.getHeight();

                    mRevealView.getLayoutParams().height = toolbarHeight + statusBarHeight;
                    mRevealViewBis.getLayoutParams().height = toolbarHeight + statusBarHeight;
                    mStatusBarView.getLayoutParams().height = statusBarHeight;
                }
            });
        } else {
            mStatusBarView.setVisibility(View.GONE);
            mRevealViewBis.setVisibility(View.GONE);

            int margin = (int) Utils.dpToPx(-6, this);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mSave.getLayoutParams();
            layoutParams.bottomMargin = margin;
            layoutParams.leftMargin = margin;
            layoutParams.rightMargin = margin;
            mSave.getLayoutParams().height = (int) Utils.dpToPx(56, this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_training_create;
    }

    @Override
    protected void initVariables() {
        mSupButton = new Button(this);
        mDrawablesTab = new ArrayList<>();
        mTabGoToRight = false;
        mLastColor = ContextCompat.getColor(this, R.color.colorSelectorRed);
        mLastColorType = Constants.COLOR_RED;
        mLastPosition = 0;
    }

    @Override
    protected void loadParameters(Bundle extras) {
        mTrainingId = extras.getString(PARAM_TRAINING_ID);
        if (extras.containsKey(PARAM_COLOR)) {
            mLastColorType = extras.getInt(PARAM_COLOR);
            mLastColor = Utils.getColorFromType(this, mLastColorType);
        }
    }

    @Override
    protected void loadInfos(Bundle savedInstanceState) {

    }

    @Override
    protected void saveInfos(Bundle outState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    protected void initialize() {
        initFromEdit();

        ViewCompat.setBackgroundTintList(mSave, ColorStateList.valueOf(mLastColor));
        if (!Utils.isLollipop()) {
            ViewCompat.setBackgroundTintList(mRevealView, ColorStateList.valueOf(mLastColor));
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(new ColorAdapter(mLastColorType));

        for (int i = 0; i < 8; i++) {
            switch (i) {
                case 0:
                    mDrawablesTab.add(i, ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_red));
                    break;
                case 1:
                    mDrawablesTab.add(i, ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_orange));
                    break;
                case 2:
                    mDrawablesTab.add(i, ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_purple));
                    break;
                case 3:
                    mDrawablesTab.add(i, ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_green));
                    break;
                case 4:
                    mDrawablesTab.add(i, ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_blue));
                    break;
                case 5:
                    mDrawablesTab.add(i, ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_cyan));
                    break;
                case 6:
                    mDrawablesTab.add(i, ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_brown));
                    break;
                case 7:
                    mDrawablesTab.add(i, ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_gray));
                    break;
            }
        }

        initTimeNumberPicker(mPrepareMinutePicker, mPrepareMinute, mPrepareSecondPicker, mPrepareSecond);
        initTimeNumberPicker(mCooldownMinutePicker, mCooldownMinute, mCooldownSecondPicker, mCooldownSecond);
        mTabSingleLayout.changeDividersColor(mLastColor);
        mTabMultipleLayout.setContainerScrollView(mScrollView);

        mTabSingleLayout.setHideOthersListener(new HideOthersListener() {
            @Override
            public void hideOther(SingleRepetitionLayout view) {
                if (mPreparePickerLayout.getVisibility() == View.VISIBLE) {
                    mPreparePickerLayout.setVisibility(View.GONE);
                } else if (mCooldownPickerLayout.getVisibility() == View.VISIBLE) {
                    mCooldownPickerLayout.setVisibility(View.GONE);
                }
            }
        });

        if (mTrainingId == null) {
            tabChanged(true);
        }
    }

    private void initFromEdit() {
        if (mTrainingId != null) {
            mTraining = Training.getTrainingById(realm, mTrainingId);

            // Colori
            mRevealViewBis.setBackgroundColor(mLastColor);
            mRevealView.setBackgroundColor(mLastColor);
            mTabSelected.setBackground(getDrawableFromColorType(mLastColorType));
            changeNumberPickerDividerColor(mLastColor);

            // Valori
            mTitle.setText(mTraining.getTitle());
            mPrepareMinute.setText(String.format(Locale.getDefault(), "%02d", mTraining.getPrepareTime() / 60));
            mPrepareSecond.setText(String.format(Locale.getDefault(), "%02d", mTraining.getPrepareTime() % 60));
            mCooldownMinute.setText(String.format(Locale.getDefault(), "%02d", mTraining.getCooldownTime() / 60));
            mCooldownSecond.setText(String.format(Locale.getDefault(), "%02d", mTraining.getCooldownTime() % 60));

            if (mTraining.getRepetitions().size() == 1) {
                mTabSingleLayout.setRepetition(realm.copyFromRealm(mTraining.getRepetitions().get(0)));
                mTabGoToRight = true;
                translateTab(true, false);
            } else {
                mTabMultipleLayout.initRepetitions(realm.copyFromRealm(mTraining.getRepetitions()));
                mTabGoToRight = false;
                translateTab(false, false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_CREATE:
                    Repetition repetition = Parcels.unwrap(data.getParcelableExtra("result"));
                    if (repetition != null) {
                        mTabMultipleLayout.addRepetition(repetition);
                    }
                    break;
                case CODE_EDIT:
                    Repetition repetitionEdit = Parcels.unwrap(data.getParcelableExtra("result"));
                    if (repetitionEdit != null) {
                        mTabMultipleLayout.updateRepetition(repetitionEdit);
                    }
                    break;
            }
        }
    }

    @Subscribe
    public void onEvent(ColorChangedEvent event) {
        int color = 0;

        switch (event.getPosition()) {
            case 0:
                color = ContextCompat.getColor(this, R.color.colorSelectorRed);
                mLastColorType = Constants.COLOR_RED;
                break;
            case 1:
                color = ContextCompat.getColor(this, R.color.colorSelectorOrange);
                mLastColorType = Constants.COLOR_ORANGE;
                break;
            case 2:
                color = ContextCompat.getColor(this, R.color.colorSelectorPurple);
                mLastColorType = Constants.COLOR_PURPLE;
                break;
            case 3:
                color = ContextCompat.getColor(this, R.color.colorSelectorGreen);
                mLastColorType = Constants.COLOR_GREEN;
                break;
            case 4:
                color = ContextCompat.getColor(this, R.color.colorSelectorBlue);
                mLastColorType = Constants.COLOR_BLUE;
                break;
            case 5:
                color = ContextCompat.getColor(this, R.color.colorSelectorCyan);
                mLastColorType = Constants.COLOR_CYAN;
                break;
            case 6:
                color = ContextCompat.getColor(this, R.color.colorSelectorBrown);
                mLastColorType = Constants.COLOR_BROWN;
                break;
            case 7:
                color = ContextCompat.getColor(this, R.color.colorSelectorGray);
                mLastColorType = Constants.COLOR_GRAY;
                break;
        }

        if (mLastColor != color) {
            if (Utils.isLollipop()) {
                animateAppAndStatusBar(color, event.getOffsetX());
            }
            animateSelectedTabDrawable(mTabSelected, mDrawablesTab.get(event.getPosition()), mDrawablesTab.get(mLastPosition));
            animateSaveButtonColor(mLastColor, color);
            changeNumberPickerDividerColor(color);

            mLastColor = color;
            mLastPosition = event.getPosition();
        }
    }

    @Subscribe
    public void onEvent(AddRepetitionClickedEvent event) {
        startActivityForResult(Act_RepetitionCreate.newIntent(mLastColor), CODE_CREATE);
    }

    @Subscribe
    public void onEvent(EditRepetitionClickedEvent event) {
        startActivityForResult(Act_RepetitionCreate.newIntentEdit(mLastColor, event.getRepetition()), CODE_EDIT);
    }

    // FUNCTIONS

    public Drawable getDrawableFromColorType(int color) {
        Drawable drawable = null;

        switch (color) {
            case Constants.COLOR_RED:
                drawable = ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_red);
                break;
            case Constants.COLOR_ORANGE:
                drawable = ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_orange);
                break;
            case Constants.COLOR_PURPLE:
                drawable = ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_purple);
                break;
            case Constants.COLOR_GREEN:
                drawable = ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_green);
                break;
            case Constants.COLOR_BLUE:
                drawable = ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_blue);
                break;
            case Constants.COLOR_CYAN:
                drawable = ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_cyan);
                break;
            case Constants.COLOR_BROWN:
                drawable = ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_brown);
                break;
            case Constants.COLOR_GRAY:
                drawable = ContextCompat.getDrawable(this, R.drawable.rect_selected_tab_gray);
                break;
        }

        return drawable;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateAppAndStatusBar(final int toColor, int offsetX) {
        Animator animator = ViewAnimationUtils.createCircularReveal(
                mRevealViewBis,
                offsetX,
                mRevealView.getHeight(),
                mRevealView.getHeight() / 2,
                mRevealView.getWidth());

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRevealViewBis.setBackgroundColor(toColor);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRevealView.setBackgroundColor(toColor);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animation.removeAllListeners();
                mRevealView.setBackgroundColor(toColor);
            }
        });

        animator.setStartDelay(100);
        animator.setDuration(175);
        animator.start();
        mRevealView.setVisibility(View.VISIBLE);
    }

    private void animateSelectedTabDrawable(View view, Drawable buttonDrawable, Drawable lastDrawable) {
        Drawable bg[] = new Drawable[2];
        if (view.getBackground() instanceof TransitionDrawable) {
            bg[0] = lastDrawable;
        } else {
            bg[0] = view.getBackground();
        }
        bg[1] = buttonDrawable;

        TransitionDrawable transitionDrawable = new TransitionDrawable(bg);
        view.setBackground(transitionDrawable);
        transitionDrawable.startTransition(200);
    }

    private void animateSaveButtonColor(int colorStart, int colorEnd) {
        ObjectAnimator animator = ObjectAnimator.ofObject(mSupButton, "backgroundColor", new ArgbEvaluator(), colorStart, colorEnd);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewCompat.setBackgroundTintList(mSave, ColorStateList.valueOf((Integer) valueAnimator.getAnimatedValue()));
                if (!Utils.isLollipop()) {
                    mRevealView.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
                }
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    private void translateTab(boolean firstTab) {
        translateTab(firstTab, true);
    }

    private void translateTab(boolean firstTab, boolean animate) {
        if (mTabGoToRight == !firstTab) {
            return;
        }

        mTabGoToRight = !firstTab;
        if (mTabGoToRight) {
            if (animate) {
                mTabSelected.animate().translationX(Utils.dpToPx(90, this)).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tabChanged(false);
                    }
                }).setDuration(150).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                ObjectAnimator.ofObject(mTabSingleText, "textColor", new ArgbEvaluator(), Color.WHITE, Color.BLACK)
                        .setDuration(150).start();
                ObjectAnimator.ofObject(mTabMultipleText, "textColor", new ArgbEvaluator(), Color.BLACK, Color.WHITE)
                        .setDuration(150).start();
            } else {
                mTabSelected.setTranslationX(Utils.dpToPx(90, this));
                mTabSingleText.setTextColor(Color.BLACK);
                mTabMultipleText.setTextColor(Color.WHITE);
                tabChanged(false);
            }
        } else {
            if (animate) {
                mTabSelected.animate().translationX(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tabChanged(true);
                    }
                }).setDuration(150).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                ObjectAnimator.ofObject(mTabSingleText, "textColor", new ArgbEvaluator(), Color.BLACK, Color.WHITE)
                        .setDuration(150).start();
                ObjectAnimator.ofObject(mTabMultipleText, "textColor", new ArgbEvaluator(), Color.WHITE, Color.BLACK)
                        .setDuration(150).start();
            } else {
                mTabSelected.setTranslationX(0);
                mTabSingleText.setTextColor(Color.WHITE);
                mTabMultipleText.setTextColor(Color.BLACK);
                tabChanged(true);
            }
        }
    }

    private void tabChanged(boolean firstTab) {
        if (firstTab) {
            mTabSingleLayout.setVisibility(View.VISIBLE);
            mTabMultipleLayout.setVisibility(View.GONE);
        } else {
            mTabSingleLayout.setVisibility(View.GONE);
            mTabMultipleLayout.setVisibility(View.VISIBLE);
        }
    }

    private void changeNumberPickerDividerColor(int color) {
        mPrepareMinutePicker.setDividerColor(color);
        mPrepareMinutePicker.invalidate();
        mPrepareSecondPicker.setDividerColor(color);
        mPrepareSecondPicker.invalidate();
        mCooldownMinutePicker.setDividerColor(color);
        mCooldownMinutePicker.invalidate();
        mCooldownSecondPicker.setDividerColor(color);
        mCooldownSecondPicker.invalidate();

        mTabSingleLayout.changeDividersColor(color);
    }

    private void initTimeNumberPicker(NumberPicker minute, final TextView minuteText, NumberPicker second, final TextView secondText) {
        minute.setDividerColor(mLastColor);
        minute.setValue(Integer.parseInt(minuteText.getText().toString()));
        minute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minuteText.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });
        second.setDividerColor(mLastColor);
        second.setValue(Integer.parseInt(secondText.getText().toString()));
        second.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                secondText.setText(String.format(Locale.getDefault(), "%02d", newVal));
            }
        });
    }

    private void create() {
        final Training training = new Training();
        training.setId(UUID.randomUUID().toString());
        training.setColor(mLastColorType);
        training.setTitle(mTitle.getText().length() > 0 ? mTitle.getText().toString() : mTitle.getHint().toString());
        training.setCreatedAt(new Date().getTime());

        int prepareTime = Integer.parseInt(mPrepareSecond.getText().toString()) + 60 * Integer.parseInt(mPrepareMinute.getText().toString());
        training.setPrepareTime(prepareTime);

        int cooldownTime = Integer.parseInt(mCooldownSecond.getText().toString()) + 60 * Integer.parseInt(mCooldownMinute.getText().toString());
        training.setCooldownTime(cooldownTime);

        if (mTabSingleLayout.getVisibility() == View.VISIBLE) {
            final Repetition repetition = mTabSingleLayout.getRepetition();
            repetition.setTrainingId(training.getId());
            training.setNumRepetition(repetition.getCount());
            training.getRepetitions().add(repetition);

            try {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(training);
                    }
                });

                setResult(RESULT_OK);
                finish();
            } catch (Exception ex) {
                ex.printStackTrace();
                //TODO messaggio
            }
        } else {
            List<Repetition> repetitions = mTabMultipleLayout.getRepetitions();
            int numCount = 0;
            for (Repetition repetition : repetitions) {
                repetition.setTrainingId(training.getId());
                training.getRepetitions().add(repetition);
                numCount += repetition.getCount();
            }
            if (prepareTime != 0) {
                numCount++;
            }
            if (cooldownTime != 0) {
                numCount++;
            }
            training.setNumRepetition(numCount);

            try {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(training);
                    }
                });

                setResult(RESULT_OK);
                finish();
            } catch (Exception ex) {
                ex.printStackTrace();
                //TODO messaggio
            }
        }
    }

    private void update() {
        final int prepareTime = Integer.parseInt(mPrepareSecond.getText().toString()) + 60 * Integer.parseInt(mPrepareMinute.getText().toString());
        final int cooldownTime = Integer.parseInt(mCooldownSecond.getText().toString()) + 60 * Integer.parseInt(mCooldownMinute.getText().toString());

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mTraining.setColor(mLastColorType);
                    mTraining.setTitle(mTitle.getText().length() > 0 ? mTitle.getText().toString() : mTitle.getHint().toString());
//                    mTraining.setUpdatedAt(new Date().getTime());

                    mTraining.setPrepareTime(prepareTime);
                    mTraining.setCooldownTime(cooldownTime);

                    mTraining.getRepetitions().deleteAllFromRealm();
                    mTraining.setRepetitions(new RealmList<Repetition>());

                    if (mTabSingleLayout.getVisibility() == View.VISIBLE) {
                        final Repetition repetition = mTabSingleLayout.getRepetition();
                        repetition.setTrainingId(mTraining.getId());
                        mTraining.setNumRepetition(repetition.getCount());
                        mTraining.getRepetitions().add(repetition);

                        setResult(RESULT_OK);
                        finish();
                    } else {
                        List<Repetition> repetitions = mTabMultipleLayout.getRepetitions();
                        int numCount = 0;
                        for (Repetition repetition : repetitions) {
                            repetition.setTrainingId(mTraining.getId());
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

                        setResult(RESULT_OK);
                        finish();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            //TODO messaggio
        }
    }
}
