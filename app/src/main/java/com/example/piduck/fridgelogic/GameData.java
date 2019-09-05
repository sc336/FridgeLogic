package com.example.piduck.fridgelogic;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by piduck on 08/03/16.
 */
public class GameData implements Observer{
    private static GameData ourInstance = new GameData();

    public static GameData getInstance() {return ourInstance; }

    /*FIXME: This sort of global data might be very dumb.
    private Context context;
    public void setContext(Context applicationContext) {
        context = applicationContext;
    }
    */

    //private List<String> answers = new ArrayList<String>();
    public class ScoreRow {
        //TODO: maybe encapsulate this a bit
        public String playerName;
        public String currentAnswer = "";
        public int score = 0;
        public ScoreRow (String name) {
            playerName = name;
        }
        public int currentVote = -1;
        public int votesWon = 0;
        public ScoreRow(ScoreRow original) {
            playerName = original.playerName;
            currentVote = original.currentVote;
            votesWon = original.votesWon;
            currentAnswer = original.currentAnswer;
            score = original.score;
        }
        public void clearRound() {
            currentVote = -1;
            votesWon = 0;
            currentAnswer = "";
        }
    }

    //TODO: consider separating scoreTable out as its own class
    private List<ScoreRow> scoreTable = new ArrayList<ScoreRow>();
    private int currentRow;
    private WordSelector topicWords;
    private String topic = "";

    public void addRow(String playerName) {
        if(playerName.trim() != "") {
            Log.d("addRow", playerName);
            scoreTable.add(new ScoreRow(playerName));
            Log.d("addRow", "now " + scoreTable.size() + " rows");
        }
    }

    public List<ScoreRow> getScoreTable() {
        ArrayList<ScoreRow> tableCopy = new ArrayList<ScoreRow>();
        for (ScoreRow r : scoreTable) {
            tableCopy.add(new ScoreRow(r));
        }
        return tableCopy;
    }

    private GameData() {
        FsmGlobal.getInstance().addObserver(this); }

    public boolean eof() { return currentRow >= scoreTable.size(); }

    public void resetList() { currentRow = 0; }

    public void postAnswer (String a) {
        if(eof()) throw new RuntimeException("Posting vote past end of player list");
        Log.d("GameData.postAnswer", "Posted " + scoreTable.get(currentRow).currentAnswer);
        scoreTable.get(currentRow).currentAnswer = a;
        currentRow++;
    }

    public void postVote(int vote) {
        if(eof()) throw new RuntimeException("Posting vote past end of player list");
        Log.d("GameData.postVote", scoreTable.get(currentRow).playerName + " voted for " + scoreTable.get(vote).playerName);
        scoreTable.get(currentRow).currentVote = vote;
        scoreTable.get(vote).votesWon++;
        currentRow++;
    }

    public int playerCount() {
        return scoreTable.size();
    }

    final static int ROUND_WIN_BONUS = 2;

    public void countVotes() {
        int maxVotes = 0;
        for(ScoreRow r : scoreTable) {
            r.score += r.votesWon;
            maxVotes = maxVotes<r.votesWon? r.votesWon:maxVotes;
        }
        for(ScoreRow r : scoreTable) {
            if(r.votesWon == maxVotes) r.score += ROUND_WIN_BONUS;
            if(scoreTable.get(r.currentVote).votesWon == maxVotes) r.score += 1;
        }
    }

    public List<String> getAnswers() {
        List<String> answers = new ArrayList<String>();
        Log.d("getAnswers", String.valueOf(scoreTable.size()));
        for (ScoreRow a : scoreTable) {
            answers.add(a.currentAnswer);
        }
        return answers;
    }

    public List<String> getThisPlayerAnswers() {
        return getAnswers(currentRow);
    }

    public List<String> getAnswers(int playerId) {
        List<String> answers = new ArrayList<String>();
        Log.d("getAnswers", playerId + " of " + String.valueOf(scoreTable.size()));
        for (int i=0; i<scoreTable.size();i++) {
            if(i!=playerId)
                answers.add(scoreTable.get(i).currentAnswer);
        }
        return answers;
    }

    public int getCurrentPlayerNumber() {
        return currentRow;
    }

    public String getCurrentPlayer() {
        //DEBUG
        for(int i = 0; i < scoreTable.size(); i++) {
            if(scoreTable.get(i).playerName.trim() == null) {
                scoreTable.remove(i);
            } else {
                Log.d("Player list", i + ": " + scoreTable.get(i).playerName);
            }
        }
        Log.d("getCurrentPlayer", String.valueOf(currentRow) + " of " + String.valueOf(scoreTable.size()));

        if(currentRow >= scoreTable.size()) {
            Log.d("getCurrentPlayer", "Returning blank as player "+currentRow+" over bounds ("+scoreTable.size()+")");
            return "";
        } else {
            Log.d("getCurrentPlayer", scoreTable.get(currentRow).playerName);
            return scoreTable.get(currentRow).playerName;
        }
    }

    public String getTopic() {
        assert topic != "";
        return topic;
    }

    public WordSelector getTopicWords() {
        assert topicWords != null;
        return topicWords;
    }

    @Override
    public void update(Observable observable, Object oldState) {
        assert observable instanceof FsmGlobal;
        if(((FsmGlobal) observable).getState() == FsmGlobal.gameState.ROUND_START) {
            //roundInitialise();
        } else if(((FsmGlobal) observable).getState() == FsmGlobal.gameState.PLAYER_LOBBY) {
            scoreTable.clear();
            currentRow = 0;
            Log.d("GameData", "scoretable cleared to " + scoreTable.size());
        }
    }
    public void roundInitialise(Context context) {
        Resources res = context.getResources();
        for(ScoreRow r : scoreTable)
            r.clearRound();
        currentRow = 0;
        Random r = new Random();
        TypedArray index = res.obtainTypedArray(R.array.topicJoinIndex);
        int joinId = index.getResourceId(r.nextInt(index.length()), 0); //context.getResources().getIntArray(index[r.nextInt(index.length)]);
        TypedArray join = res.obtainTypedArray(joinId);

        //for(int i: join) Log.d("GameData","ROUND_START loading join item: "+i);  //Debug
        String[] topics = res.getStringArray(join.getResourceId(0,0));  //Load in list of topics
        topic = topics[r.nextInt(topics.length)];
        topicWords = new WordSelector(context.getResources().obtainTypedArray(join.getResourceId(1,0)), 60, context);

        Log.d("ROUND_START", "Topic selected: " + topic);
        Log.d("ROUND_START", "Words selected: " + topicWords.size());
    }
}
