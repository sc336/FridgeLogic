package com.example.piduck.fridgelogic;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by piduck on 25/02/16.
 */
public class FridgeLayout extends AbsoluteLayout implements Observer {

    private WordSelector wordsGrammar = null;
    //private WordSelector wordsSubject = null;
    private WordSelector wordsCommon = null;

    public FridgeLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        FsmGlobal.getInstance().addObserver(this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FsmGlobal.getInstance().getState() == FsmGlobal.gameState.BEFORE_MAGNETS) {
                    if(GameData.getInstance().getCurrentPlayer() == "") {
                        ((MainActivity)getContext()).goLobby();
                    } else {

                        spawnMagnets();
                        //Snackbar s = Snackbar.make(findViewById(R.id.fridge), GameData.getInstance().getCurrentPlayer(), Snackbar.LENGTH_LONG);
                        //s.show();
                        Log.d("AnnounceActivity", "Advancing on magnet spawn");
                        FsmGlobal.getInstance().advanceState();
                    }
                }
            }
        });
    }

    @Override
    public void update(Observable observable, Object oldState) {
        if((FsmGlobal.gameState)oldState == FsmGlobal.gameState.USER_WRITING) {
            ((Activity)this.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((FridgeLayout)findViewById(R.id.fridge)).removeAllViews();
                }
            });
        } else if(((FsmGlobal)observable).getState() == FsmGlobal.gameState.AFTER_ALL_PLAYERS_ANSWERED){
            wordsGrammar = null;
            //wordsSubject = null;
            wordsCommon = null;
        } else if(((FsmGlobal)observable).getState() == FsmGlobal.gameState.ROUND_START) {
            //TODO: add capability for user custom word files
            //wordsSubject = new WordSelector("/sdcard/words.txt", 60);
            int subjectResource = getResources().getIdentifier("mgen","raw",getContext().getPackageName());
            //wordsSubject = new WordSelector(getResources().openRawResource(subjectResource), 60);

            //TODO: Modifier magnets like -s, -ed, -'s ; punctuation
            wordsGrammar = new WordSelector(getResources().openRawResource(R.raw.top50), 35);
            wordsCommon = new WordSelector(getResources().openRawResource(R.raw.top1000), 35);
            //getResources().getIdentifier("FILENAME_WITHOUT_EXTENSION","raw", getPackageName());

            ProgressBar progress = (ProgressBar)((Activity)getContext()).findViewById(R.id.progress);
            AnswerBar answer = (AnswerBar)((Activity)getContext()).findViewById(R.id.answerBar);
            progress.setMinimumHeight(answer.getHeight());
        }

    }

    public boolean onDragEvent(DragEvent event) {
        //TODO: Have the thing light up while the object is being dragged over?
        if (event.ACTION_DROP == event.getAction() && FsmGlobal.getInstance().getState() == FsmGlobal.gameState.USER_WRITING) {
            WordMagnet dropped = ((WordMagnet)event.getLocalState());
            ((ViewGroup)dropped.getParent()).removeView(dropped);
            AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Math.round(event.getX() - dropped.getWidth()/2), Math.round(event.getY() - dropped.getHeight() / 2));
            this.addView(dropped, lp);
            }
        // Always return true on a ACTION_DRAG_STARTED to register for future events
        return true; }

    //TODO: instead, calculate these spacings based on the number of words
    final static int EMS_REQUIRED = 600;
    final static int X_MIN = 2;
    static int X_MAX_VAR = 20;
    static int Y_MAX_VAR = 20;
    class Rectangle {
        public int left, right, top, bottom;
        public Rectangle(int left, int top, int right, int bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }
    }

    private void spawnMagnets(List<WordMagnet> wordMagnets, int left, int top, int right, int bottom) {
        //TODO: Refactor and perhaps rethink the positioning of magnets
        //Position magnets from left to right, adding a little randomness on the way
        Iterator<WordMagnet> i = wordMagnets.iterator();
        double area = 0;
        while(i.hasNext()) {
            WordMagnet v = i.next();
            area += v.getMeasuredHeight() * v.getMeasuredWidth();
        }
        X_MAX_VAR = Y_MAX_VAR = (int)(0.5*Math.sqrt(((right-left)*(bottom-top) - area)/wordMagnets.size()));
        /*Debug
        Log.d("spawnMagnets","Used area: "+(area));
        Log.d("spawnMagnets","Leftover area: "+((right-left)*(bottom-top) - area));
        Log.d("spawnMagnets","per magnet: "+(int)((right-left)*(bottom-top) - area)/wordMagnets.size());
        Log.d("spawnMagnets", "Spacing: " + X_MAX_VAR);*/
        i = wordMagnets.iterator();
        List<Rectangle> dealt = new LinkedList<Rectangle>();
        Random random = new Random();
        int lastRight = left;
        int lastTop = top;
        while(i.hasNext()) {
            WordMagnet v = i.next();
            int x, y;
            //Random distance from top left corner, or top right corner of previous
            do {
                x = lastRight + X_MIN + random.nextInt(X_MAX_VAR);
                y = lastTop + random.nextInt(Y_MAX_VAR/2);
            } while(findLowestOverlap(x, y, v.getWidth(), v.getHeight(), dealt)>0);
            //Log.d("spawnMagnets","lastRight starts at "+lastRight);
            //Start new line
            if (x + v.getMeasuredWidth() > right) {
                x = left + X_MIN + random.nextInt(X_MAX_VAR);
                y = lastTop + v.getMeasuredHeight() + Y_MAX_VAR/2 + random.nextInt(Y_MAX_VAR/2);
                lastTop = y;
                //TODO: What to do if you run out of space
                /*if(y+v.getMeasuredHeight() > bottom)
                    break;*/
                //Log.d("spawnMagnets","Starting new line at "+y);
            }
            if (y + v.getMeasuredHeight() > right) ;
            //Overlap detection definitely needs improvement
            int lowestOverlap = findLowestOverlap(x, y, v.getWidth(), v.getHeight(), dealt);
            if (lowestOverlap >= 0)
                y = lowestOverlap + random.nextInt(Y_MAX_VAR / 2);

            lastRight = x + v.getMeasuredWidth();
            //HACK No idea why this hack works but apparently Dalvik doesn't hear you the first time
            lastRight = x + v.getMeasuredWidth(); //It doesn't make sense.  But don't remove this line.

            //Log.d("spawnMagnets", "Adding magnet " + v.getText() + " at " + x + ", " + y+ "; "+dealt.size()+" dealt");
            this.removeView(v);
            AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, x, y);
            v.setLayoutParams(lp);
            this.addView(v);
            dealt.add(new Rectangle(x, y, x + v.getMeasuredWidth(), y + v.getMeasuredHeight()));
            i.remove();
        }
    }

    public void spawnMagnets() {
        FrameLayout line = ((FrameLayout)((Activity)getContext()).findViewById(R.id.answerFrame));
        int AnswerBottom = line.getTop() + line.getHeight();

        int size = (int) (Math.sqrt((double)getLowerArea()/EMS_REQUIRED));
        Log.d("spawnMagnets", "Square root " + size + " rounded from " + Math.sqrt((double) size));
        WordMagnet.size = (int)(0.9*size);

        //COMMON WORDS: Top Left
        spawnMagnets(getMagnetList(wordsCommon), 0, 0, this.getWidth() / 2, line.getTop());
        //GRAMMAR WORDS: Top right
        spawnMagnets(getMagnetList(wordsGrammar), this.getWidth()/2, 0, this.getWidth(), line.getTop());
        //SUBJECT: Bottom
        spawnMagnets(getMagnetList(GameData.getInstance().getTopicWords()), 0, AnswerBottom, this.getWidth(), this.getHeight());
    }

    public int getLowerArea() {
        FrameLayout line = ((FrameLayout)((Activity)getContext()).findViewById(R.id.answerFrame));
        int AnswerBottom = line.getTop() + line.getHeight();
        return this.getWidth() * (this.getHeight() - AnswerBottom);
    }

    private List<WordMagnet> getMagnetList(WordSelector wordSelector) {
        //Populates a list with measured, word-filled magnets
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, this.getWidth() + 1, this.getHeight() + 1);
        List<WordMagnet> wordMagnets = new LinkedList<WordMagnet>();
        Iterator<String> w = wordSelector.iterator();
        while (w.hasNext()) {
            WordMagnet n = new WordMagnet(this.getContext());
            n.setText(w.next());
            n.setLayoutParams(lp);
            this.addView(n, lp);
            n.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            wordMagnets.add(n);
        }
        return wordMagnets;
    }

    private int findLowestOverlap(int x, int y, int width, int height, List<Rectangle> list) {
        //TODO: Returning -1 as "no problem" is not exactly great
        int lowest = -1;
        for(Rectangle r: list) {
            if (!(x > r.right | y > r.bottom | x+width < r.left | y+height < r.top)) {
                lowest = Math.max(r.bottom, lowest);
                Log.d("findLowestOverlap","Overlap at "+lowest);
            }
        }
        return lowest;
    }

}
