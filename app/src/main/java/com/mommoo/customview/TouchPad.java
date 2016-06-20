package com.mommoo.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import mommoo.com.library.MommooThread;
import mommoo.com.library.manager.DIPManager;

/**
 * Created by mommoo on 2016-04-17.
 */
public class TouchPad extends View{

    private boolean once,press;
    private int viewWidth,viewHeight;
    private RectF rectF;
    private Paint paint;
    private int beforeX,beforeY;
    private int moveValueX,moveValueY;
    private MouseEvent mouseEvent;
    private long touchTime;
    private MouseClickEvent clickEvent;

    public TouchPad(Context context) {
        super(context);
        initialize();
    }

    public TouchPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TouchPad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public TouchPad(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }
    private void initialize(){
        paint = new Paint();
        paint.setAntiAlias(true);
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!once){
            canvas.drawColor(Color.TRANSPARENT);
            once = true;
            viewWidth = getWidth();
            viewHeight = getHeight();
            rectF.set(0,0,viewWidth,viewHeight);
        }
        canvas.drawRoundRect(rectF, DIPManager.dip2px(20,getContext()), DIPManager.dip2px(20,getContext()), paint);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        once = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(touchTime>0) {
                if(System.currentTimeMillis()-touchTime<300){
                    press = true;
                    System.out.println("Press!!");
                    if(clickEvent != null)clickEvent.onPress();
                }
            }
            touchTime = System.currentTimeMillis();
            beforeX = (int)event.getX();
            beforeY = (int)event.getY();
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            moveValueX = -beforeX + (int)event.getX();
            moveValueY = -beforeY + (int)event.getY();
            beforeX = (int)event.getX();
            beforeY = (int)event.getY();
            String message = "1" +moveValueX+","+moveValueY+"/";
            //System.out.println(message);
            if(mouseEvent!=null) mouseEvent.onMove(message);
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            if(press) {
                press = false;
                System.out.println("Release!!");
                if(clickEvent != null)clickEvent.onRelease();
            }else{
                if(System.currentTimeMillis() - touchTime<300){
                    System.out.println("click!");
                    if(clickEvent != null) {
                        clickEvent.onPress();
                        //MommooThread.sleep(10);
                        clickEvent.onRelease();
                    }
                }
            }
        }
        return true;
    }

    public void setMouseEvent(MouseEvent mouseEvent){
        this.mouseEvent = mouseEvent;
    }
    public void setMouseClickEvent(MouseClickEvent clickEvent){
        this.clickEvent = clickEvent;
    }
    public void setPadColor(int padColor){
        this.paint.setColor(padColor);
    }
    public int getPadColor(){
        return paint.getColor();
    }

}
