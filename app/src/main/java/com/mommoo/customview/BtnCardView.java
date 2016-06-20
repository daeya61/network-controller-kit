package com.mommoo.customview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import mommoo.com.library.SearchView;

/**
 * Created by mommoo on 2016-04-15.
 */
public class BtnCardView extends CardView {

    private String title;
    private TextView textView;
    private MaterialRippleLayout layout;
    private boolean ripple = true;
    private int color;

    public BtnCardView(Context context) {
        super(context);
        initialize();
    }

    public BtnCardView(Context context,boolean ripple){
        super(context);
        this.ripple = ripple;
        initialize();
    }

    public BtnCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public BtnCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    private void initialize(){
        textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        if(ripple) {
            layout = new MaterialRippleLayout(getContext());
            addView(layout);
            layout.addView(textView);
        }else{
            addView(textView);
            final int originalColor = new SearchView().parseBackgroundColor(textView);
            textView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        textView.setBackgroundColor(originalColor);
                    }else if(event.getAction()==MotionEvent.ACTION_DOWN){
                        textView.setBackgroundColor(Color.LTGRAY);
                    }
                    return false;
                }
            });
        }
    }

    public void setTitle(String title){
        this.title = title;
        textView.setText(title);
    }
    public String getTitle(){
        return title;
    }

    public void setTitleSize(int sp){
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
    }

    public int getTitleSize(){
        return (int)textView.getTextSize();
    }

    public void setTitleColor(int color){
        this.color = color;
        textView.setTextColor(color);
    }

    public int getTitleColor(){
        return this.color;
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener){
        if(ripple)layout.setOnClickListener(onClickListener);
        else textView.setOnClickListener(onClickListener);
    }

    public void setDelay(boolean delay){
        layout.setRippleDelayClick(delay);
    }
}
