package com.mommoo.tool;

import android.content.Context;
import android.util.TypedValue;

import mommoo.com.library.manager.DIPManager;

/**
 * Created by mommoo on 2016-04-21.
 */
public class ActionBar {
    public static int getActionBarHeight(Context context){
        int actionBarSize;
        TypedValue outValue = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, outValue, true)) {
            actionBarSize = TypedValue.complexToDimensionPixelSize(outValue.data, context.getResources().getDisplayMetrics());
        } else {
            actionBarSize = DIPManager.dip2px(56, context);
        }
        return actionBarSize;
    }
}
