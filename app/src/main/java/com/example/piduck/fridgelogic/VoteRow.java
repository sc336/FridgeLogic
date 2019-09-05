package com.example.piduck.fridgelogic;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.Intent;

import java.util.List;
import java.util.Random;

/**
 * Created by piduck on 15/03/16.
 */
public class VoteRow extends TableRow {
    public VoteRow(Context context) {
        super(context);
    }

    public VoteRow(final Context context, final int rowNum) {
        /*Consider rethinking.  Contents need to be from a random row excluding current player.
         * Row needs to know whose answer it is so votes can be cast appropriately.
         * Currently VoteActivity is doing the thinking here.
         */
        super(context);
        TableRow.LayoutParams rlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(rlp);
        TextView t = new TextView(context);
        t.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        t.setText(GameData.getInstance().getAnswers().get(rowNum));
        this.addView(t);
        Button b = new Button(context);
        b.setText("Vote");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameData.getInstance().postVote(rowNum);
                FsmGlobal.getInstance().advanceState();
                Log.d("VoteRow", "Advancing on user submitting vote");
                ((VoteActivity)context).finish();
            }
        });
        this.addView(b);
    }
    public VoteRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
