package com.mommoo.customview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by mommoo on 2016-04-17.
 */
public class TouchMouse extends BtnCardView {
    public TouchMouse(Context context) {
        super(context);
        initialize();
    }

    public TouchMouse(Context context, boolean ripple) {
        super(context, ripple);
        initialize();
    }

    public TouchMouse(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }
    public TouchMouse(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    private void initialize(){
        setTitle("터치패드");
    }
}
