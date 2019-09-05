package com.example.piduck.fridgelogic;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by piduck on 09/03/16.
 */
public class AnswerBar extends LinearLayout implements Observer {

    public AnswerBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        FsmGlobal.getInstance().addObserver(this);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        //TODO: Have the thing light up while the object is being dragged over
        if (event.ACTION_DROP == event.getAction() && FsmGlobal.getInstance().getState() == FsmGlobal.gameState.USER_WRITING) {
            View vDropped = ((View) event.getLocalState());
            ((ViewGroup) vDropped.getParent()).removeView(vDropped);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            vDropped.setLayoutParams(lp);
            this.addView(vDropped);
        }
        return true;
    }

    @Override
    public void update(Observable observable, Object oldState) {
        //TODO: Put the code in here in its own method, and just call it from the Runnable?
        /*if(((FsmGlobal.gameState)oldState) == FsmGlobal.gameState.USER_WRITING) {
            ((MainActivity) this.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String answer = new String();
                    AnswerBar line = (AnswerBar) findViewById(R.id.answerBar);

                    for (int i = 0; i < line.getChildCount(); i++) {
                        if (line.getChildAt(i) instanceof WordMagnet) {
                            answer += ((WordMagnet) line.getChildAt(i)).getText().toString() + " ";
                        }
                    }
                    GameData.getInstance().postAnswer(answer);

                    TextView blank = (TextView) line.getChildAt(0);
                    line.removeAllViews();
                    line.addView(blank);

                    Log.d("AnnounceActivity", "Advancing after posting");
                    FsmGlobal.getInstance().advanceState();
                }


            });
        }*/
    }

    public void postAnswer() {
        ((MainActivity) this.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String answer = new String();
                AnswerBar line = (AnswerBar) findViewById(R.id.answerBar);

                for (int i = 0; i < line.getChildCount(); i++) {
                    if (line.getChildAt(i) instanceof WordMagnet) {
                        WordMagnet m = ((WordMagnet) line.getChildAt(i));
                        answer += m.getText().toString() + " ";
                        if(i+1 >= line.getChildCount()) {
                            if (m.spaceHandling == WordMagnet.spacingType.SUPPRESS_NONE
                                    || m.spaceHandling == WordMagnet.spacingType.SUPPRESS_BEFORE)
                                answer += " ";
                        }
                        else {
                            WordMagnet n = ((WordMagnet) line.getChildAt(i+1));
                            if (m.spaceHandling == WordMagnet.spacingType.SUPPRESS_AFTER
                                    || m.spaceHandling == WordMagnet.spacingType.SUPPRESS_BOTH
                                    || n.spaceHandling == WordMagnet.spacingType.SUPPRESS_BEFORE
                                    || n.spaceHandling == WordMagnet.spacingType.SUPPRESS_BOTH)
                                answer += " ";
                            }
                    }
                }
                GameData.getInstance().postAnswer(answer);

                TextView blank = (TextView) line.getChildAt(0);
                line.removeAllViews();
                line.addView(blank);

                Log.d("AnnounceActivity", "Advancing after posting");
                FsmGlobal.getInstance().advanceState();
            }


        });
    }

}
