package com.mommoo.tool;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.lang.reflect.Field;

import mommoo.com.library.manager.ScreenManager;

/**
 * Created by mommoo on 2016-04-17.
 */
public class Statusbar {
    public static void setStatusBar(View statusBar){
        if(Build.VERSION.SDK_INT<19){
            statusBar.getLayoutParams().height = 0;
        }
    }
    public static int getCustomStatusBarHeight(Context context){
        if(Build.VERSION.SDK_INT<19){
            return 0;
        }else{
            return new ScreenManager(context).getStatusBarHeight();
        }
    }
    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        System.out.println("statusbarsize : " + statusBarHeight);
        return statusBarHeight;
    }
}
