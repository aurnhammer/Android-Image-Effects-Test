package com.aurnhammer.imageeffects;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
        	try {
	            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/brandon_re.ttf");
	            setTypeface(tf);
        	}catch (Exception e) {
    			System.out.println("MyTextView setFont Exception: " + e);
    		}
        }
        
    }
}