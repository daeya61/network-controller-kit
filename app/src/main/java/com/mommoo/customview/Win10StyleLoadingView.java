package com.mommoo.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.mommoo.suityourself.R;

import java.util.ArrayList;

import mommoo.com.library.manager.DIPManager;

/**
 * Created by mommoo on 2016-04-07.
 */
public class Win10StyleLoadingView extends View {

    private boolean once,start;
    private final int CIRCLE_COUNT = 4;
    private int viewWidth,viewHeight,radius;
    private int PADDING;
    private int[] circleLocationX;
    private String[] colors;
    private Paint paint;
    private ArrayList<AnimatorSet> animatorSets = new ArrayList<>();

    public Win10StyleLoadingView(Context context) {
        super(context);
        initialize();
    }

    public Win10StyleLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public Win10StyleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public Win10StyleLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }
    private void initialize(){
        paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(Color.GRAY);
        PADDING = DIPManager.dip2px(5,getContext());
        radius = DIPManager.dip2px(3,getContext());
        circleLocationX = new int[CIRCLE_COUNT];
        colors = getContext().getResources().getStringArray(R.array.colors);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!once){
            once = true;
            viewWidth = getWidth();
            viewHeight = getHeight();
            if(start) startAnim();
        }

        for(int i = 0; i<CIRCLE_COUNT;i++) {
            //paint.setColor(Integer.parseInt(colors[i]));
            canvas.drawCircle(circleLocationX[i],viewHeight/2,radius,paint);
        }
    }



    public void setCircleColor(int color){
        this.paint.setColor(color);
    }
    public int getCircleColor(){
        return this.paint.getColor();
    }

    public void setCircleRadius(int radius){
        this.radius = radius;
    }

    public int getCircleRadius(){
        return radius;
    }

    private void setFirstLocationX(int locationX){
        this.circleLocationX[0] = locationX;
        invalidate();
    }
    private int getFirstLocationX(){
        return this.circleLocationX[0];
    }

    private void setSecondLocationX(int locationX){
        this.circleLocationX[1] = locationX;
        invalidate();
    }
    private int getSecondLocationX(){
        return this.circleLocationX[1];
    }

    private int getThirdLocationX(){
        return this.circleLocationX[2];
    }
    private void setThirdLocationX(int locationX){
        this.circleLocationX[2] = locationX;
        invalidate();
    }

    private int getFourthLocationX(){
        return this.circleLocationX[3];
    }
    private void setFourthLocationX(int locationX){
        this.circleLocationX[3] = locationX;
        invalidate();
    }

    public void start(){
        start = true;
        once = false;
        invalidate();
    }

    private void startAnim(){
        buildAnim("FirstLocationX",viewWidth/2,0,300).start();//2400
        buildAnim("SecondLocationX",viewWidth/2 -PADDING*2,100,200).start(); //2500
        buildAnim("ThirdLocationX",viewWidth/2 -PADDING*4,200,100).start(); //2600
        buildAnim("FourthLocationX",viewWidth/2-PADDING*6,300,0).start();//2700
    }
    private AnimatorSet buildAnim(String methodName,int toX, final int delayTime,final int repeatDelayTime){
        ObjectAnimator one = ObjectAnimator.ofInt(this,methodName,-radius,toX);
        one.setInterpolator(new DecelerateInterpolator());one.setDuration(1000);
        ObjectAnimator two = ObjectAnimator.ofInt(this,methodName,toX,(toX) + PADDING*2 );
        two.setInterpolator(new LinearInterpolator()); two.setDuration(700);
        ObjectAnimator three = ObjectAnimator.ofInt(this,methodName,(toX)+PADDING*2,viewWidth+radius);
        three.setInterpolator(new AccelerateInterpolator()); three.setDuration(700);
        final AnimatorSet set = new AnimatorSet();
        set.playSequentially(one, two, three);
        set.setStartDelay(delayTime);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        set.setStartDelay(delayTime);
                        set.start();
                    }
                }, repeatDelayTime);
            }
        });
        animatorSets.add(set);
        return set;
    }
    public void end(){
        for(AnimatorSet set : animatorSets){
            set.end();
        }
    }
}
