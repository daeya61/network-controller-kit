package com.mommoo.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import mommoo.com.library.manager.DIPManager;

/**
 * Created by mommoo on 2016-04-20.
 */
public class UiExampleView extends View {

    private int col,row;
    private int viewWidth,viewHeight;
    private int rectWidth,rectHeight;
    private Paint rectPaint;
    private Paint rectEffectPaint;
    private boolean once,invisible;
    private boolean[] exist;
    private int PADDING = DIPManager.dip2px(5,getContext());
    private Rect rect;

    public UiExampleView(Context context) {
        super(context);
        initialize();
    }

    public UiExampleView(Context context,int row,int col){
        this(context);
        this.col = col;
        this.row = row;
        exist = new boolean[col*row];
    }

    public UiExampleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public UiExampleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public UiExampleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize(){
        rect = new Rect();
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setColor(Color.BLACK);

        rectEffectPaint = new Paint();
        rectEffectPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!once){
            once = true;
            viewWidth = getWidth();
            viewHeight = getHeight();
            rectWidth = (viewWidth - PADDING*(col+1))/col;
            rectHeight = (viewHeight - PADDING*(row+1))/row;

        }
        for(int i=0;i<col*row;i++){
            int left = PADDING + (i%col)*(PADDING+rectWidth);
            int top = PADDING + (i/col)*(PADDING+rectHeight);
            rect.set(left,top,left+rectWidth,top+rectHeight);
            if(exist[i]){
                if(!invisible)canvas.drawRect(rect,rectPaint);
            } else canvas.drawRect(rect,rectEffectPaint);
        }
    }

    public void setRectColor(int color){
        rectPaint.setColor(color);
        rectEffectPaint.setColor(color);
    }

    public void setPadding(int padding){
        this.PADDING = padding;
    }

    public void setStroke(int strokeWidth){
        rectEffectPaint.setStyle(Paint.Style.STROKE);
        int value = DIPManager.dip2px(10, getContext());
        rectEffectPaint.setPathEffect(new DashPathEffect(new float[]{value,value},0));
        rectEffectPaint.setStrokeWidth(strokeWidth);
    }

    public void setRectExist(int position,boolean exist){
        this.exist[position] = exist;
        invalidate();
    }

    public void setAllRectExist(boolean exist){
        for(int i=0,size = this.exist.length;i<size;i++) this.exist[i] = exist;
        invalidate();
    }

    public void setInVisibleMode(boolean inVisibleMode){
        this.invisible = inVisibleMode;
    }
}
