package com.mommoo.customview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mommoo on 2016-04-15.
 */
public class SoftKeyboard extends BtnCardView {

    public SoftKeyboard(Context context) {
        super(context);
        initialize();
    }

    public SoftKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SoftKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    private void initialize(){
        setTitle("키보드");
    }
}
