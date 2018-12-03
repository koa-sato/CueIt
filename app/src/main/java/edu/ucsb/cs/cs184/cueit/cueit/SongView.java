package edu.ucsb.cs.cs184.cueit.cueit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


/**
 * Created by deni on 12/2/18.
 */

public class SongView extends View { //TODO custom view for each song

    public SongView(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
    }

    public SongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
    }

    public SongView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}
