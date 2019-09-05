package com.example.piduck.fridgelogic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

public class DisplayVoteActivity extends NoBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_vote);
        TableLayout table = (TableLayout)findViewById(R.id.displayVoteTable);
        GameData.getInstance().countVotes();
        List<GameData.ScoreRow> tableData = GameData.getInstance().getScoreTable();
        TableRow headerRow = new TableRow(this);
        Utils.addTextView(headerRow, "Player");
        Utils.addTextView(headerRow, "Answer");
        Utils.addTextView(headerRow, "Voted for");
        Utils.addTextView(headerRow, "Votes acquired");
        Utils.addTextView(headerRow, "Score");
        ((TableLayout) findViewById(R.id.displayVoteTable)).addView(headerRow);


        for(GameData.ScoreRow rd : tableData) {
            TableRow r = new TableRow(this);
            Utils.addTextView(r, rd.playerName);
            Utils.addTextView(r, rd.currentAnswer);
            Utils.addTextView(r, tableData.get(rd.currentVote).playerName);
            Utils.addTextView(r, "" + rd.votesWon);
            Utils.addTextView(r, "" + rd.score);
            ((TableLayout) findViewById(R.id.displayVoteTable)).addView(r);
        }
        table.setOnClickListener(new View.OnClickListener() {
            //TODO: replace this when redoing Director / FSMGlobal
            boolean lock = false;
            @Override
            public void onClick(View v) {
                if(!lock) {
                    lock=true;
                    Log.d("AnnounceActivity", "Advancing after displaying votes");
                    FsmGlobal.getInstance().advanceState();
                    finish();
                }
            }
        });
    }
}
