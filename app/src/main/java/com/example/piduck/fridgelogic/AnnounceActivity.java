package com.example.piduck.fridgelogic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class AnnounceActivity extends NoBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);

        Intent intent = getIntent();
        String announcement = (String)intent.getSerializableExtra(MainActivity.ANNOUNCEMENT);
        TextView t = ((TextView)findViewById(R.id.announceText));
        Log.d("AnnounceActivity", "Announcing: " + announcement);
        t.setText(announcement);
        ((TextView)findViewById(R.id.announceTopic)).setText(GameData.getInstance().getTopic());
        ((RelativeLayout)t.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AnnounceActivity", "Advancing past announcement");
                FsmGlobal.getInstance().advanceState();
                finish();
            }
        });
    }
}
