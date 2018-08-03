package it.emperor.intervaltraining.ui.training;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import it.emperor.intervaltraining.App;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.models.Repetition;
import it.emperor.intervaltraining.models.Training;
import it.emperor.intervaltraining.providers.SoundProvider;
import it.emperor.intervaltraining.ui.base.BaseActivity;
import it.emperor.intervaltraining.ui.views.AnimatedTextView;
import it.emperor.intervaltraining.utility.Constants;
import it.emperor.intervaltraining.utility.MyCountdownTimer;
import it.emperor.intervaltraining.utility.Prefs;
import it.emperor.intervaltraining.utility.Utils;
import rjsv.circularview.CircleView;

public class Act_RunTraining extends BaseActivity {

    @BindView(R.id.bg)
    View mBg;
    @BindView(R.id.statusBar)
    View mStatusBarView;
    @BindView(R.id.main_progress)
    CircleView mMainProgress;
    @BindView(R.id.secondary_progress)
    RoundCornerProgressBar mSecondaryProgress;
    @BindView(R.id.step_value)
    TextView mStepValue;
    @BindView(R.id.type_text)
    TextView mTypeText;
    @BindView(R.id.type_value)
    TextView mTypeValue;
    @BindView(R.id.play_pause)
    ImageButton mPlayPause;
    @BindView(R.id.start_countdown_layout)
    View mStartCountdownLayout;
    @BindView(R.id.start_countdown)
    AnimatedTextView mStartCountdownText;
    @BindView(R.id.skip_next)
    View mSkipNext;

    @BindView(R.id.ad_view_layout)
    LinearLayout mAdViewLayout;

    @OnClick(R.id.skip_previous)
    void skipPrevious() {
        if (mPosition > 0) {
            if (mDone) {
                mBuilder = null;
            }
            mSavedSeconds = -1;
            mDone = false;
            showStep(mPosition - 1);
        }
    }

    @OnClick(R.id.skip_next)
    void skipNext(View view) {
        if (mPosition < mSteps.size() - 1) {
            if (view == null && mVibrationEnabled) {
                mVibrator.vibrate(700);
            }
            if (view == null && mSoundsEnabled) {
                soundProvider.startMultiBell();
            }
            mSavedSeconds = -1;
            showStep(mPosition + 1);
        } else {
            if (mDone) {
                return;
            }

            if (view == null && mVibrationEnabled) {
                mVibrator.vibrate(new long[]{0, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 500}, -1);
            }
            if (view == null && mSoundsEnabled) {
                soundProvider.startMultiBell(3);
            }
            if (mPosition == mSteps.size() - 1) {
                mPosition += 1;
            }
            mSavedSeconds = -1;
            displayDoneNotification(mSteps.size());
        }
    }

    @OnClick(R.id.play_pause)
    void playPauseClicked() {
        if (mDone) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Action action = Utils.isLollipop() ? mBuilder.mActions.get(1) : null;
        if (mCountDownTimer != null) {
            if (action != null) {
                action.icon = R.drawable.ic_play_arrow_black_24dp;
                action.title = getString(R.string.training_run_play);
            }

            mCountDownTimer.stop();
            mCountDownTimer = null;
            mPlayPause.setImageResource(R.drawable.ic_play_arrow_white_48dp);
        } else {
            if (action != null) {
                action.icon = R.drawable.ic_pause_black_24dp;
                action.title = getString(R.string.training_run_pause);
            }

            startCountDown(mSavedSeconds, mSteps.get(mPosition).seconds);
            mPlayPause.setImageResource(R.drawable.ic_pause_white_48dp);
        }
        notificationManager.notify(1, mBuilder.build());
    }

    // SUPPORT

    @Inject
    SoundProvider soundProvider;

    private Vibrator mVibrator;
    private NotificationCompat.Builder mBuilder;
    private MyCountdownTimer mStartCountDownTimer;
    private MyCountdownTimer mCountDownTimer;
    private Training mTraining;
    private List<Step> mSteps;
    private boolean mVibrationEnabled;
    private boolean mSoundsEnabled;
    private boolean mShowAds;
    private boolean mDone;
    private int mSavedSeconds;
    private int mSavedSecondsStart;
    private int mPosition;

    private static final String SAVED_POSITION = "savedPosition";
    private static final String SAVED_DONE = "savedDone";
    private static final String SAVED_SECOND = "savedSeconds";
    private static final String SAVED_SECOND_START = "savedSecondsStart";

    private static final String PARAM_ID = "paramId";
    private static final String PARAM_SHOW_ADS = "paramShowAds";

    // SYSTEM

    public static Intent newIntent(Training training, boolean showAds) {
        Intent intent = new Intent(App.context(), Act_RunTraining.class);
        intent.putExtra(PARAM_ID, Parcels.wrap(training));
        intent.putExtra(PARAM_SHOW_ADS, showAds);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Prefs.getBoolean(Constants.PREF_SCREEN_ON, true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (getSupportActionBar() != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        }

        if (Utils.isLollipop()) {
            mStatusBarView.post(new Runnable() {
                @Override
                public void run() {
                    int width = Utils.screenWidth(Act_RunTraining.this);
                    int padding = (int) getResources().getDimension(R.dimen.standard_dimension) * 2;

                    mStatusBarView.getLayoutParams().height = Utils.getStatusBarHeight(Act_RunTraining.this);
                    mMainProgress.getLayoutParams().height = width - padding;
                }
            });
        } else {
            mStatusBarView.setVisibility(View.GONE);
        }

        if (mShowAds) {
            mAdViewLayout.removeAllViews();
            mAdViewLayout.addView(Utils.createAdView(this));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_run_training;
    }

    @Override
    protected void initVariables() {
        mSteps = new ArrayList<>();
        mSavedSecondsStart = 3;
        mPosition = 0;
        mSavedSeconds = -1;
        mVibrationEnabled = Prefs.getBoolean(Constants.PREF_VIBRATION_ON, true);
        mSoundsEnabled = Prefs.getBoolean(Constants.PREF_SOUND_ON, true);
        mDone = false;
    }

    @Override
    protected void loadParameters(Bundle extras) {
        mTraining = Parcels.unwrap(extras.getParcelable(PARAM_ID));
        mShowAds = extras.getBoolean(PARAM_SHOW_ADS, false);
    }

    @Override
    protected void loadInfos(Bundle savedInstanceState) {
        mPosition = savedInstanceState.getInt(SAVED_POSITION, 0);
        mDone = savedInstanceState.getBoolean(SAVED_DONE, false);
        mSavedSeconds = savedInstanceState.getInt(SAVED_SECOND, -1);
        mSavedSecondsStart = savedInstanceState.getInt(SAVED_SECOND_START, 3);
    }

    @Override
    protected void saveInfos(Bundle outState) {
        outState.putInt(SAVED_POSITION, mPosition);
        outState.putBoolean(SAVED_DONE, mDone);
        outState.putInt(SAVED_SECOND, mSavedSeconds);
        if (mSavedSecondsStart == 0) {
            outState.putInt(SAVED_SECOND_START, mSavedSecondsStart);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_ACTION_SKIP_PREVIOUS);
        filter.addAction(Constants.BROADCAST_ACTION_PLAYPAUSE);
        filter.addAction(Constants.BROADCAST_ACTION_SKIP_NEXT);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.stop();
            mCountDownTimer = null;
        }
        if (mStartCountDownTimer != null) {
            mStartCountDownTimer.stop();
            mStartCountDownTimer = null;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onBackPressed() {
        if (!mDone) {
            new MaterialDialog.Builder(Act_RunTraining.this)
                    .title(getString(R.string.training_run_interrompi_popup_title))
                    .content(getString(R.string.training_run_interrompi_popup_content))
                    .positiveText(getString(R.string.general_interrompi))
                    .negativeText(getString(R.string.general_annulla))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Act_RunTraining.super.onBackPressed();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void initialize() {
        if (init()) {
            start();
        } else {
            displayDoneNotification(0);
        }
    }

    // FUNCTIONS

    private boolean init() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTraining.getTitle());
        }
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (!mVibrator.hasVibrator()) {
            mVibrationEnabled = false;
        }

        mBg.setBackgroundColor(Utils.getColorFromType(this, mTraining.getColor()));
        mMainProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparentWhite));

        mStartCountdownText.setTextAppearance(R.style.TextAppearance_AppCompat_Display4);
        mStartCountdownText.setTextSize(200);

        if (mTraining.getPrepareTime() != 0) {
            mSteps.add(new Step(mTraining.getPrepareTime(), Constants.TYPE_PREPARE));
        }
        for (Repetition repetition : mTraining.getRepetitions()) {
            for (int i = 0; i < repetition.getCount(); i++) {
                mSteps.add(new Step(repetition.getWorkTime(), Constants.TYPE_WORK));
                mSteps.add(new Step(repetition.getRestTime(), Constants.TYPE_REST));
            }
        }
        if (mTraining.getCooldownTime() != 0) {
            mSteps.add(new Step(mTraining.getCooldownTime(), Constants.TYPE_COOLDOWN));
        }

        return mSteps.size() != 0;
    }

    private void start() {
        if (mDone) {
            displayDoneNotification(mSteps.size());
        } else if (mSavedSecondsStart == 0) {
            mStartCountdownLayout.setVisibility(View.GONE);
            showStep(mPosition);
        } else {
            mStartCountdownLayout.setVisibility(View.VISIBLE);
            mStartCountdownText.setValue(mSavedSecondsStart);
            mStartCountDownTimer = new MyCountdownTimer(3000, 1000, new MyCountdownTimer.OnTimerListener() {
                @Override
                public void onTick(long second) {
                    if (mVibrationEnabled) {
                        mVibrator.vibrate(200);
                    }
                    if (mSoundsEnabled) {
                        soundProvider.startSingleBell();
                    }

                    mStartCountdownText.setValue(second);
                    mSavedSecondsStart = (int) second;
                }

                @Override
                public void onFinish() {
                    if (mVibrationEnabled) {
                        mVibrator.vibrate(700);
                    }
                    if (mSoundsEnabled) {
                        soundProvider.startMultiBell();
                    }

                    mStartCountdownLayout.setVisibility(View.GONE);
                    mSavedSecondsStart = 0;
                    showStep(mPosition);
                }
            });
            mStartCountDownTimer.start();
        }
    }

    private void showStep(int position) {
        mPosition = position;
        Step step = mSteps.get(mPosition);
        if (mSavedSeconds != -1) {
            startCountDown(mSavedSeconds, step.seconds);
        } else {
            startCountDown(step.seconds, step.seconds);
        }

        mStepValue.setText(String.format(Locale.getDefault(), "%d/%d", mPosition + 1, mSteps.size()));
        mSecondaryProgress.setProgress((float) mPosition / (float) mSteps.size());

        mTypeText.setText(step.getTypeText());
        mMainProgress.setProgressValue(100);
    }

    private void startCountDown(int seconds, final int totalSeconds) {
        if (mCountDownTimer != null) {
            mCountDownTimer.stop();
            mCountDownTimer = null;
        }
        mCountDownTimer = new MyCountdownTimer(seconds * 1000, 1000, new MyCountdownTimer.OnTimerListener() {
            @Override
            public void onTick(long second) {
                if (second <= 3 && mVibrationEnabled) {
                    mVibrator.vibrate(200);
                }
                if (second <= 3 && mSoundsEnabled) {
                    soundProvider.startSingleBell();
                }

                if (second > 59) {
                    mTypeValue.setText(String.format(Locale.getDefault(), "%d:%02d", second / 60, second % 60));
                } else {
                    mTypeValue.setText(String.format(Locale.getDefault(), "%02d", second));
                }
                int percent = (int) (((float) second / (float) totalSeconds) * 100f);
                mMainProgress.setProgressValue(percent);
                mSavedSeconds = (int) second;
                displayNotification(mSteps.get(mPosition).getTypeText(), (int) second, percent, mPosition + 1, mSteps.size());
            }

            @Override
            public void onFinish() {
                mMainProgress.setProgressValue(0);
                mSavedSeconds = -1;
                skipNext(null);
            }
        });
        mCountDownTimer.start();
    }

    private void displayNotification(String typeText, int seconds, int percent, int step, int totalStep) {
        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(this)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setColor(ContextCompat.getColor(this, R.color.icon_bg))
                    .setSmallIcon(R.drawable.ic_launcher);
            if (Utils.isLollipop()) {
                mBuilder.addAction(R.drawable.ic_skip_previous_black_24dp, getString(R.string.training_run_prev), PendingIntent.getBroadcast(this, 0, new Intent(Constants.BROADCAST_ACTION_SKIP_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT));
                mBuilder.addAction(R.drawable.ic_pause_black_24dp, getString(R.string.training_run_pause), PendingIntent.getBroadcast(this, 0, new Intent(Constants.BROADCAST_ACTION_PLAYPAUSE), PendingIntent.FLAG_UPDATE_CURRENT));
                mBuilder.addAction(R.drawable.ic_skip_next_black_24dp, getString(R.string.training_run_next), PendingIntent.getBroadcast(this, 0, new Intent(Constants.BROADCAST_ACTION_SKIP_NEXT), PendingIntent.FLAG_UPDATE_CURRENT));
            }
        }
        mBuilder.setProgress(100, percent, false)
                .setContentTitle(typeText)
                .setContentInfo(String.format(Locale.getDefault(), "%d/%d", step, totalStep))
                .setContentText(seconds > 59 ?
                        String.format(Locale.getDefault(), "%d:%02d", seconds / 60, seconds % 60)
                        : String.format(Locale.getDefault(), "%02d", seconds));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    private void displayDoneNotification(int totalStep) {
        mDone = true;

        if (mCountDownTimer != null) {
            mCountDownTimer.stop();
        }
        mCountDownTimer = null;

        mTypeText.setText(getString(R.string.training_done));
        mTypeValue.setText(getString(R.string.training_done_time));
        mMainProgress.setProgressValue(0);
        mSecondaryProgress.setProgress(1f);

        mBuilder = new NotificationCompat.Builder(this)
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(ContextCompat.getColor(this, R.color.icon_bg))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.training_done))
                .setContentInfo(String.format(Locale.getDefault(), "%d/%d", totalStep, totalStep));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    // CLASSES

    private class Step {
        int seconds;
        int type;

        Step(int seconds, int type) {
            this.seconds = seconds;
            this.type = type;
        }

        String getTypeText() {
            String text = "";
            switch (this.type) {
                case Constants.TYPE_PREPARE:
                    text = getString(R.string.training_prepare);
                    break;
                case Constants.TYPE_WORK:
                    text = getString(R.string.training_work);
                    break;
                case Constants.TYPE_REST:
                    text = getString(R.string.training_rest);
                    break;
                case Constants.TYPE_COOLDOWN:
                    text = getString(R.string.training_cooldown);
                    break;
            }
            return text;
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                switch (intent.getAction()) {
                    case Constants.BROADCAST_ACTION_PLAYPAUSE:
                        playPauseClicked();
                        break;
                    case Constants.BROADCAST_ACTION_SKIP_PREVIOUS:
                        skipPrevious();
                        break;
                    case Constants.BROADCAST_ACTION_SKIP_NEXT:
                        skipNext(mSkipNext);
                        break;
                }
            }
        }
    };
}
