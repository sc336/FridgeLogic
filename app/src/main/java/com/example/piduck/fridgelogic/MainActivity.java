package com.example.piduck.fridgelogic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

/*TODO:
 * Suffices
 */

public class MainActivity extends NoBackActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.piduck.fridgelogic/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.piduck.fridgelogic/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public final class Director implements Observer {
        //TODO: This probably should eat up other observers to avoid race conditions, and get its own file
        public Director() {
            FsmGlobal.getInstance().addObserver(this);
        }

        @Override
        public void update(Observable observable, Object data) {
            FsmGlobal fsm = ((FsmGlobal)observable);
            FsmGlobal.gameState newState = ((FsmGlobal)observable).getState();
            FsmGlobal.gameState oldState = ((FsmGlobal.gameState)data);
            Log.d("Director.update", String.valueOf(oldState) + " -> " + String.valueOf(newState));

            switch(newState) {
                case PLAYER_LOBBY:
                    goLobby();
                    break;
                case ANNOUNCE_WRITER:
                    announce(GameData.getInstance().getCurrentPlayer());
                    break;
                case ROUND_START:
                    GameData.getInstance().roundInitialise(getApplicationContext());
                    Log.d("Director", "Advancing automatically after ROUND_START initialisation");
                    FsmGlobal.getInstance().advanceState();
                    break;
                case BEFORE_MAGNETS:
                    break;
                case USER_WRITING:
                    //Fridge spawns magnets but that is currently handled there
                    break;
                case AFTER_WRITING:
                    ((AnswerBar)findViewById(R.id.answerBar)).postAnswer();
                    break;
                case AFTER_ALL_PLAYERS_ANSWERED:
                    GameData.getInstance().resetList();
                    Log.d("Director", "Advancing automatically after AFTER_ALL_PLAYERS_ANSWERED");
                    FsmGlobal.getInstance().advanceState();
                    break;
                case ANNOUNCE_VOTER:
                    announce(GameData.getInstance().getCurrentPlayer());
                    break;
                case BEFORE_VOTING:
                    goVote();
                    break;
                case VOTING:
                    break;
                case AFTER_ALL_PLAYERS_VOTED:
                    goDisplayVotes();
                    Log.d("Director", "Advancing automatically after AFTER_ALL_PLAYERS_VOTED");
                    FsmGlobal.getInstance().advanceState();
                    break;
                case DISPLAYING_ANSWERS:
                    break;
            }

        }
    }

    public final static String ANNOUNCEMENT = "com.example.FridgeLogic.ANNOUNCEMENT";
    public void announce(String announcement) {
        Intent intent = new Intent(this, AnnounceActivity.class);
        intent.putExtra(ANNOUNCEMENT, announcement);
        startActivity(intent);
    }

    public final static String ANSWERS = "com.example.FridgeLogic.ANSWERS";
    public void goVote() {
        if(FsmGlobal.getInstance().getState() != FsmGlobal.gameState.BEFORE_VOTING)
            throw new RuntimeException("Trying to vote at invalid state");
        Intent intent = new Intent(this, VoteActivity.class);
        intent.putExtra(ANSWERS, (Serializable) GameData.getInstance().getAnswers());
        Log.d("goVote", "Advancing because it's time to vote");
        FsmGlobal.getInstance().advanceState();
        startActivity(intent);
    }

    public void goLobby() {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    public void goDisplayVotes() {
        Intent intent = new Intent(this, DisplayVoteActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findViewById(R.id.line).setOnDragListener(new DragListener());

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        GameTimer timer = new GameTimer(this);
        //GameData.getInstance().setContext(getApplicationContext());

        Observer director = new Director();

        //Game starts out in Lobby mode
        goLobby();
    }



}
