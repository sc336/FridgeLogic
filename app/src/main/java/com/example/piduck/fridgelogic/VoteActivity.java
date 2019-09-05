package com.example.piduck.fridgelogic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class VoteActivity extends NoBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        //Intent intent = getIntent();
        //List<String> answers = (List<String>)intent.getSerializableExtra(MainActivity.ANSWERS);
        List<TableRow> rowList = new LinkedList<TableRow>();

        for(int i = 0; i < GameData.getInstance().playerCount(); i++) {
            if(i != GameData.getInstance().getCurrentPlayerNumber()) {
                TableRow r = new VoteRow(this, i);
                rowList.add(r);
            }
        }
        Collections.shuffle(rowList);
        for(TableRow r : rowList) ((ViewGroup) findViewById(R.id.table)).addView(r);

        ((TextView)findViewById(R.id.voteTopic)).setText(GameData.getInstance().getTopic());
    }

}
