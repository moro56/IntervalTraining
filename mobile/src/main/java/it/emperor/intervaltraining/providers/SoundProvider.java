package it.emperor.intervaltraining.providers;

import android.media.MediaPlayer;
import android.os.Handler;

import it.emperor.intervaltraining.App;
import it.emperor.intervaltraining.R;

public class SoundProvider {

    private MediaPlayer bellPlayer;
    private MediaPlayer multiBellPlayer;
    private int i;

    public SoundProvider() {
        bellPlayer = MediaPlayer.create(App.context(), R.raw.benboncan_multi_boxing_bell_66952);
        multiBellPlayer = MediaPlayer.create(App.context(), R.raw.benboncan_boxing_bell_66951);
    }

    public void startSingleBell() {
        bellPlayer.seekTo(0);
        bellPlayer.start();
    }

    public void startMultiBell() {
        multiBellPlayer.seekTo(0);
        multiBellPlayer.start();
    }

    public void startMultiBell(final int times) {
        i = 0;
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                startMultiBell();
                i++;
                if (i < times) {
                    handler.postDelayed(this, 200);
                }
            }
        });
    }
}