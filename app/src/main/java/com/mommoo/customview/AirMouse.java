package com.mommoo.customview;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by mommoo on 2016-04-14.
 */
public class AirMouse extends BtnCardView {

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener eventListener;
    private MouseEvent event;
    private TextView textView;
    private boolean isOnMouse;
    private AirMouseControlView controlView;

    public AirMouse(Context context) {
        super(context);
        initialize();
    }

    public AirMouse(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public AirMouse(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    private void initialize(){
        //testMode();
        setTitle("에어마우스");
        controlView = new AirMouseControlView(getContext());
        controlView.setOnOffClickListener(new OnOffClickListener() {
            @Override
            public void onClick() {
                stop();
            }
        });
        sensorManager = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        final int sensorType = Sensor.TYPE_GYROSCOPE;
        sensor = sensorManager.getDefaultSensor(sensorType);
        System.out.println(sensor==null);
        final int multi = 30;
        final int pivot = 1;//손떨림방지

        eventListener = new SensorEventListener() {
            int gyroX,gyroY,gyroZ;
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType()==sensorType){
                    gyroX = -Math.round(event.values[0]*multi);
                    //gyroY = Math.round(event.values[1]*multi);
                    gyroZ = -Math.round(event.values[2]*multi);
                    String message = "1"+gyroZ+","+gyroX+"/";
//                    System.out.println("message:"+message);
                    if(Math.abs(gyroX) >pivot || Math.abs(gyroZ) > pivot)
                        if(AirMouse.this.event != null) AirMouse.this.event.onMove(message);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnMouse()) stop();
                else start();
            }
        });
    }

    private void testMode(){
        LinearLayout linearLayout = new LinearLayout(getContext());
        addView(linearLayout);
        textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        linearLayout.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setTextSize(20);
    }

    public void setMouseEvent(MouseEvent event){
        this.event = event;
    }

    public void setOnClickTouchListener(OnClickTouchListener clickTouchListener){
        controlView.setOnClickListener(clickTouchListener);
    }

    public void start(){
        isOnMouse = true;
        sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        ((ViewGroup)((Activity)getContext()).getWindow().getDecorView()).addView(controlView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    public void stop(){
        isOnMouse = false;
        sensorManager.unregisterListener(eventListener,sensor);
        ((ViewGroup)((Activity)getContext()).getWindow().getDecorView()).removeView(controlView);
    }
    public boolean isOnMouse(){
        return isOnMouse;
    }
}
