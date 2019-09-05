package com.example.piduck.fridgelogic;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

//TODO: consider replacing table with nested LinearLayout
public class LobbyActivity extends NoBackActivity {

    private class PlayerRow extends TableRow {


        private void spawnPlayerRow() {
            TableLayout table = ((TableLayout)this.getParent());
            if(table.indexOfChild(this) == table.getChildCount() - 1) {
                Log.d("spawnPlayerRow", "Row " + String.valueOf(table.indexOfChild(this)) +" of " + String.valueOf(table.getChildCount()));
                table.addView(new PlayerRow(getBaseContext()));
            }
        }

        private void trimPlayerRow() {
            TableLayout table = ((TableLayout)this.getParent());
            if(table.indexOfChild(this) < table.getChildCount() - 1) {
                table.removeView(this);
            }
        }

        public String getPlayerName() {
            return String.valueOf(((EditText)this.getChildAt(0)).getText());
        }

        public PlayerRow(Context context) {
            super(context);
            Log.d("PlayerRow", "Adding new row");
            this.setPadding(10, 10, 10, 10);
            EditText t = new EditText(getBaseContext());
            //TODO: Do this sizing properly
            t.setMinimumHeight(10);
            t.setMinimumWidth(500);
            t.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorTarget));
            t.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().isEmpty()) trimPlayerRow();
                    else spawnPlayerRow();
                }
            });
            this.addView(t);
        }

        public PlayerRow(Context context, AttributeSet attrs) { super(context, attrs); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Log.d("LobbyActivity", "Entering Lobby");
        ((TableLayout)findViewById(R.id.playerTable)).addView(new PlayerRow(getBaseContext()));
        ((Button)findViewById(R.id.lobbyExitButton)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<((TableLayout)findViewById(R.id.playerTable)).getChildCount(); i++ ) {
                    PlayerRow r = (PlayerRow) ((TableLayout)findViewById(R.id.playerTable)).getChildAt(i);

                    if(!r.getPlayerName().trim().equals("")) {
                        GameData.getInstance().addRow(r.getPlayerName());
                    }
                }
                Log.d("LobbyActivity", "Advancing because user submitted all player names");
                FsmGlobal.getInstance().advanceState();
                finish();
            }
        });

        ((Button)findViewById(R.id.debugAliceBobbinator)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameData.getInstance().addRow("Alice");
                GameData.getInstance().addRow("Bobby");
                GameData.getInstance().addRow("Charlie");
                Log.d("LobbyActivity", "Advancing at end of AliceBobbinator");
                FsmGlobal.getInstance().advanceState();
                finish();
            }
        });

    }
}
