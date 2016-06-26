package com.mommoo.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mommoo.adapter.OnClickListener;
import com.mommoo.suityourself.R;

import java.util.EventListener;

import mommoo.com.library.SearchView;
import mommoo.com.library.manager.DIPManager;
import mommoo.com.library.manager.ScreenManager;

/**
 * Created by mommoo on 2016-04-13.
 */
public class SoftKey extends LinearLayout implements View.OnClickListener{

    private int screenWidth,screenHeight;
    private int btnBasicSize;
    private int layoutHeight;
    private final int LINE_COUNT = 6,PADDING_SIDE = DIPManager.dip2px(10,getContext());
    private LinearLayout[] lines;
    private String[] line1 = new String[10];
    private String[] line2 = new String[9];
    private String[] line3 = new String[9];
    private String[] line4 = new String[9];
    private SoftKeyEvent event;

    public SoftKey(Context context) {
        super(context);
        initialize();
    }

    public SoftKey(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SoftKey(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public SoftKey(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }
    private void initialize(){
        screenWidth = new ScreenManager(getContext()).getScreenWidth();
        btnBasicSize = ((screenWidth - PADDING_SIDE)/10) - (PADDING_SIDE/2);

        line1[0] = "q"; line1[1] = "w"; line1[2] = "e"; line1[3] = "r"; line1[4] = "t";
        line1[5] = "y"; line1[6] = "u"; line1[7] = "i"; line1[8] = "o"; line1[9] = "p";

        line2[0] = "a"; line2[1] = "s"; line2[2] = "d"; line2[3] = "f"; line2[4] = "g";
        line2[5] = "h"; line2[6] = "j"; line2[7] = "k"; line2[8] = "l";

        line3[0] = "shift"; line3[1] = "z"; line3[2] = "x"; line3[3] = "c"; line3[4] = "v";
        line3[5] = "b"; line3[6] = "n"; line3[7] = "m"; line3[8] = "back";

        line4[0] = "Ctrl"; line4[1] = "Alt"; line4[2] = "space"; line4[3] = "←"; line4[4] = "↑";
        line4[5] = "↓"; line4[6] = "→"; line4[7] = "."; line4[8] = "enter";

        inflate(getContext(), R.layout.softkey,this);
        Class c = R.id.class;
        lines = new LinearLayout[LINE_COUNT];
        int index = 0;
        int linearPadding = PADDING_SIDE;
        layoutHeight = btnBasicSize + linearPadding;
        for(LinearLayout linear : lines){
            try{
                int tempIndex = index;
                lines[index] = (LinearLayout)findViewById((int)(c.getField("line"+(++index)).get(c)));
                lines[tempIndex].getLayoutParams().height = layoutHeight;
                lines[tempIndex].setPadding(0,linearPadding/2,0,linearPadding/2);
            }catch (NoSuchFieldException e){
                e.printStackTrace();
            }catch(IllegalAccessException e){
                e.printStackTrace();
            }
        }
        for(int j=0;j<3;j++) {
            for (int i = 0; i < 10; i++) {
                String content = "";
                if(j==0) content ="F"+Integer.toString(i+1);
                else if(j==1) content = Integer.toString(i);
                else content = line1[i];
                BtnCardView cardView = new BtnCardView(getContext(),false);
                cardView.setOnClickListener(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(btnBasicSize,btnBasicSize);
                params.setMargins(PADDING_SIDE / 2, 0, 0, 0);
                lines[j].addView(cardView, params);
                cardView.setTitle(content);
                cardView.setTitleSize(12);
            }
        }//lines2 까지완료.

        for(int i=0;i<9;i++){
            String content = line2[i];
            BtnCardView cardView = new BtnCardView(getContext(),false);
            cardView.setOnClickListener(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(btnBasicSize,btnBasicSize);
            params.setMargins(i==0?PADDING_SIDE + btnBasicSize/2:PADDING_SIDE/2,0,i==8?PADDING_SIDE/2+btnBasicSize/2:0,0);
            lines[3].addView(cardView, params);
            cardView.setTitle(content);
            cardView.setTitleSize(12);
        } // line3 까지완료.
        for(int j=0; j<2;j++) {
            for (int i = 0; i < 9; i++) {
                String content;
                if(j==0) content = line3[i];
                else content = line4[i];
                BtnCardView cardView = new BtnCardView(getContext(),false);
                cardView.setOnClickListener(this);
                LinearLayout.LayoutParams params;
                if(j==0) params = new LinearLayout.LayoutParams(i == 0 || i == 8 ? 3 * btnBasicSize / 2 + PADDING_SIDE / 2 : btnBasicSize, btnBasicSize);
                else params = new LinearLayout.LayoutParams(i == 2 || i==8? 3*btnBasicSize/2  + PADDING_SIDE/2 : btnBasicSize, btnBasicSize);
                params.setMargins(PADDING_SIDE / 2, 0, 0, 0);
                lines[4+j].addView(cardView, params);
                cardView.setTitle(content);
                cardView.setTitleSize(12);
            }//line5 까지 완료.
        }
    }

    public int getSoftKeyViewHeight(){
        return LINE_COUNT*layoutHeight;
    }

    public void setSoftKyEvent(SoftKeyEvent event){
        this.event = event;
    }

    @Override
    public void onClick(View view){
        String message = "00"+convertKeyValue(((TextView)view).getText().toString().toUpperCase())+"/";
        if(event != null){
            event.onClick(message);
        }
    }
    public String convertKeyValue(String keyValue){
        if(keyValue.equals("BACK")){
            return "BACK_SPACE";
        }else if(keyValue.equals("←")){
            return "LEFT";
        }else if(keyValue.equals("↑")){
            return "UP";
        }else if(keyValue.equals("→")){
            return "RIGHT";
        }else if(keyValue.equals("↓")){
            return "DOWN";
        }else if(keyValue.equals("CTRL")){
            return "CONTROL";
        }else if(keyValue.equals(".")){
            return "PERIOD";
        }else
            return keyValue;
    }
}

