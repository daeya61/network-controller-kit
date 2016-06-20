package com.mommoo.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.mommoo.suityourself.R;

/**
 * Created by mommoo on 2016-04-22.
 */
public class AirMouseControlView extends FrameLayout {

    private OnOffClickListener offClickListener;
    private OnClickTouchListener onClickTouchListener;
    private FrameLayout click,off;

    public AirMouseControlView(Context context) {
        super(context);
        initialize();
    }

    public AirMouseControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public AirMouseControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public AirMouseControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void initialize(){
        inflate(getContext(), R.layout.air_mouse_control_view, this);
        click = (FrameLayout)findViewById(R.id.clickBtn);
        off = (FrameLayout)findViewById(R.id.offBtn);
        click.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    onClickTouchListener.onActionDown();
                }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    onClickTouchListener.onActionMove();
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    onClickTouchListener.onActionUp();
                }
                return true;
            }
        });
        off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offClickListener.onClick();
            }
        });
    }

    public void setOnClickListener(OnClickTouchListener onClickTouchListener){
        this.onClickTouchListener = onClickTouchListener;
    }

    public void setOnOffClickListener(OnOffClickListener offClickListener){
        this.offClickListener = offClickListener;
    }

}
