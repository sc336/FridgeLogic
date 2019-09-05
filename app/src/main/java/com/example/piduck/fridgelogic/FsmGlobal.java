package com.example.piduck.fridgelogic;

import android.util.Log;

import java.util.Observable;

/**
 * Created by piduck on 07/03/16.
 * For managing the current mode of the game.
 * If control of the state gets out of hand, create a class which registers all the objects which
 *  want to do stuff in a particular state, and waits for them all to say advance before advancing.
 *  Such a class might even have a way of notifying objects it wants to continue.
 */
public class FsmGlobal extends Observable {
    private static FsmGlobal ourInstance = new FsmGlobal();

    public static FsmGlobal getInstance() {
        assert ourInstance != null;
        return ourInstance;
    }

    public enum gameState {
        PLAYER_LOBBY,
        ROUND_START,
        ANNOUNCE_WRITER,
        BEFORE_MAGNETS,
        USER_WRITING,
        AFTER_WRITING,
        ANNOUNCE_VOTER,
        BEFORE_VOTING,
        VOTING,
        AFTER_ALL_PLAYERS_ANSWERED,
        DISPLAYING_ANSWERS,
        AFTER_ALL_PLAYERS_VOTED
    }
    private gameState state;

    private FsmGlobal() {
        state = gameState.PLAYER_LOBBY;
    }

    public gameState getState() { return state;  }

    private void advanceState(gameState newState) {

        Log.d("GameState", "Advancing to " + newState.name());

        //FIXME: Make sure to move this code if there are ever unsuccessful state changes
        gameState oldState = state;
        state = newState;
        setChanged();
        notifyObservers(oldState);
    }

    //TODO:Make this more robust to avoid calling advanceState multiple times
    public void advanceState() {
        //Advances state when the calling object doesn't know what should come next
        switch(state) {
            case PLAYER_LOBBY:
                advanceState(gameState.ROUND_START);
                break;
            case ROUND_START:
                advanceState(gameState.ANNOUNCE_WRITER);
                break;
            case ANNOUNCE_WRITER:
                advanceState(gameState.BEFORE_MAGNETS);
                break;
            case BEFORE_MAGNETS:
                advanceState(gameState.USER_WRITING);
                break;
            case USER_WRITING:
                advanceState(gameState.AFTER_WRITING);
                break;
            case AFTER_WRITING:
                //This state waits until AnswerBar has posted before moving on.
                if(GameData.getInstance().eof())
                    advanceState(gameState.AFTER_ALL_PLAYERS_ANSWERED);
                else
                    advanceState(gameState.ANNOUNCE_WRITER);
                break;
            case AFTER_ALL_PLAYERS_ANSWERED:
                advanceState(gameState.ANNOUNCE_VOTER);
                break;
            case ANNOUNCE_VOTER:
                advanceState(gameState.BEFORE_VOTING);
                break;
            case BEFORE_VOTING:
                advanceState(gameState.VOTING);
                break;
            case VOTING:
                if(GameData.getInstance().eof())
                    advanceState(gameState.AFTER_ALL_PLAYERS_VOTED);
                else
                    advanceState(gameState.ANNOUNCE_VOTER);
                break;
            case AFTER_ALL_PLAYERS_VOTED:
                advanceState(gameState.DISPLAYING_ANSWERS);
                break;
            case DISPLAYING_ANSWERS:
                advanceState(gameState.ROUND_START);
                break;
        }
    }


}
