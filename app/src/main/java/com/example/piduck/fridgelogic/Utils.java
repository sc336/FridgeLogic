package com.example.piduck.fridgelogic;

import android.text.Layout;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by piduck on 15/03/16.
 */
public final class Utils {
    public static void addTextView(ViewGroup vg, String text) {
        TextView ht = new TextView(vg.getContext());
        ht.setPadding(10,10,10,10);
        ht.setText(text);
        vg.addView(ht);
    }
}
