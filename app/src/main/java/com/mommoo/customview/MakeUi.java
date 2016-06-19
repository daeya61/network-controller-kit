package com.mommoo.customview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by mommoo on 2016-04-18.
 */
public class MakeUi extends BtnCardView{
    public MakeUi(Context context) {
        super(context);
        initialize();
    }

    public MakeUi(Context context, boolean ripple) {
        super(context, ripple);
        initialize();
    }

    public MakeUi(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MakeUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    private void initialize(){
        setTitle("내 리스트");
    }
}
