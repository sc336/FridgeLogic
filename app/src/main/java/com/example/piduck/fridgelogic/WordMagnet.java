package com.example.piduck.fridgelogic;

import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.support.v4.content.ContextCompat.*;

/**
 * Created by piduck on 24/02/16.
 * TODO: Give it a pretty background.  Black bordered white rectangle, shadow, maybe a little alpha?
 */
public class WordMagnet extends TextView {

    //FIXME: This is probably not the way to set text size.
    public static int size;
    public spacingType spaceHandling;

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        if (event.ACTION_DOWN == event.getAction()) {
            ClipData data = ClipData.newPlainText("", this.getText());
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(this);
            this.startDrag(data, shadow, this, 0);
        }
        return true;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        //TODO: Have the thing light up while the object is being dragged over?
        if (event.ACTION_DROP == event.getAction()) {
            if (this.getParent() instanceof LinearLayout ) {
                LinearLayout line = ((LinearLayout) this.getParent());

                View vDropped = ((View) event.getLocalState());
                ViewGroup owner = ((ViewGroup) vDropped.getParent());

                owner.removeView(vDropped);

                line.addView(vDropped, line.indexOfChild(this));
                return true; }
            else { return false; } }
        // Always return true on a ACTION_DRAG_STARTED to register for future events
        return true; }

    //NB: Constructor is only here because AS is whining there's no default constructor.
    //FIXME: This is only one of four constructors.  Crash on start probably means I need to define the others.
    public WordMagnet(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public enum spacingType {
        SUPPRESS_NONE,
        SUPPRESS_BEFORE,
        SUPPRESS_AFTER,
        SUPPRESS_BOTH
    }

    public WordMagnet(Context context, spacingType spacing) {
        super(context);
        spaceHandling = spacing;
        initialise(context);
    }

    public WordMagnet(Context context) {
        super(context);
        spaceHandling = spacingType.SUPPRESS_NONE;
        initialise(context);
    }

    private void initialise(Context context) {
        Resources res = context.getResources();
        this.setTextColor(getColor(context, R.color.colorPrimary));
        this.setBackgroundColor(0xAAFFFFFF);
        this.setBackground(ResourcesCompat.getDrawable(res, R.drawable.border,null));
        this.setPadding(10,4,10,4);
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);//getResources().getDimension(R.dimen.magnet_font_size));
        //this.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
    }

}
