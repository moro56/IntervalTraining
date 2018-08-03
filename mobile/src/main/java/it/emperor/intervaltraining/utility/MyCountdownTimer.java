package it.emperor.intervaltraining.utility;

import android.os.Handler;

public class MyCountdownTimer {

    private Handler handler;
    private Runnable runnable;
    private OnTimerListener onTimerListener;
    private long millisInFuture;
    private long countDownInterval;
    private boolean start;

    public MyCountdownTimer(long millisInFuture, long countDownInterval, OnTimerListener onTimerListener) {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
        this.onTimerListener = onTimerListener;
        this.start = false;
        initialize();
    }

    private void initialize() {
        runnable = new Runnable() {
            @Override
            public void run() {
                long sec = millisInFuture / 1000;
                if (start) {
                    if (millisInFuture <= 0) {
                        if (onTimerListener != null) {
                            onTimerListener.onFinish();
                        }
                    } else {
                        if (onTimerListener != null) {
                            onTimerListener.onTick(sec);
                        }
                        millisInFuture -= countDownInterval;
                        handler.postDelayed(this, countDownInterval);
                    }
                } else {
                    handler.postDelayed(this, countDownInterval);
                }
            }
        };
    }

    public void start() {
        start = true;
        if(handler == null) {
            handler = new Handler();
            handler.post(runnable);
        }
    }

    public void stop() {
        start = false;
    }

    public interface OnTimerListener {
        void onTick(long second);

        void onFinish();
    }
}
