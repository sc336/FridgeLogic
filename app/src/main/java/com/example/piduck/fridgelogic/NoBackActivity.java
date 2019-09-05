package com.example.piduck.fridgelogic;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by piduck on 02/05/16.
 */
public abstract class NoBackActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        //Requires API level 5+
        moveTaskToBack(true);
    }
}
