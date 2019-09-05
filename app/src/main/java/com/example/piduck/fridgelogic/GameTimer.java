package com.example.piduck.fridgelogic;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;


import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by piduck on 09/03/16.
 */
public class GameTimer implements Observer {

    Activity context;

    public GameTimer(Activity mainContext) {
        context = mainContext;
        FsmGlobal.getInstance().addObserver(this);
    }

    private class answerCountDown extends CountDownTimer {

        ProgressBar progress;
        int totalDurationMillis;
        boolean hasTriggered = false;

        public answerCountDown(int durationMillis, int interval, ProgressBar progressBar) {
            super(durationMillis, interval);
            totalDurationMillis = durationMillis;
            progress = progressBar;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            progress.setProgress(Math.round(progress.getMax()*(1-(float)millisUntilFinished/totalDurationMillis)));
        }

        @Override
        public void onFinish() {
            if(!hasTriggered) {
                hasTriggered = true;
                Log.d("EndWritingTask", "Timed event triggered");
                progress.setProgress(0);
                if (FsmGlobal.getInstance().getState() == FsmGlobal.gameState.USER_WRITING)
                    Log.d("AnnounceActivity", "Advancing at end of timed phase");
                    FsmGlobal.getInstance().advanceState();
            }
        }
    }

    private answerCountDown timer;

    //TODO: replace constants with R.values
    final static int MAX_SECONDS = 30;
    final static int INCREMENT = 100;

    @Override
    public void update(Observable observable, Object oldState) {
        if(((FsmGlobal)observable).getState() == FsmGlobal.gameState.USER_WRITING) {
            Log.d("GameTimer", "Starting timer");
            /*progress=0;
            hasTriggered=false;
            timer = new Timer();
            timer.schedule(new TickTask(this), INCREMENT, INCREMENT);*/
            timer = new answerCountDown(MAX_SECONDS*1000,INCREMENT,((ProgressBar)context.findViewById(R.id.progress)));
            timer.start();
        }
    }

}
