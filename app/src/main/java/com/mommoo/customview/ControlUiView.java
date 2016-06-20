package com.mommoo.customview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mommoo.db.MommooDb;
import com.mommoo.suityourself.R;
import com.mommoo.suityourself.UiActivity;
import com.mommoo.tool.Statusbar;
import com.mommoo.tool.StreamSerialize;

import java.net.Socket;
import java.util.ArrayList;

import mommoo.com.library.anim.AnimationCallBack;
import mommoo.com.library.anim.TransitionAnimation;
import mommoo.com.library.manager.DIPManager;
import mommoo.com.library.manager.ScreenManager;
import mommoo.com.library.widget.MommooDialog;

/**
 * Created by mommoo on 2016-04-15.
 */
public class ControlUiView extends FrameLayout {

    private SoftKeyboard keyboard;
    private String activityName;
    private AirMouse mouse;
    private TouchMouse touchMouse;
    private MakeUi makeUi;
    private int screenWidth, screenHeight,btnSize,actionBarSize,startYLine;
    private final int PADDING = DIPManager.dip2px(10,getContext());
    public static final int DATABASE_VERSION = 2;
    private SoftKey softKey;
    private TouchPad touchPad;
    private boolean isSoftKey,isTouchPad;
    private MouseEvent mouseEvent;
    private RecyclerView clientUiList;
    private TextView listTitle;
    private StreamSerialize streamSerialize;

    public ControlUiView(Context context,String activityName) {
        super(context);
        initialize();
        this.activityName = activityName;
    }

    public ControlUiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ControlUiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    @TargetApi(21)
    public ControlUiView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }
    private void initialize(){
        ScreenManager screenManager = new ScreenManager(getContext());
        screenWidth = screenManager.getScreenWidth();
        screenHeight = screenManager.getScreenHeight();

        btnSize = (screenWidth - (5*PADDING))/4;
        TypedValue outValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, outValue, true)) {
            actionBarSize = TypedValue.complexToDimensionPixelSize(outValue.data, getResources().getDisplayMetrics());
        } else {
            actionBarSize = DIPManager.dip2px(56, getContext());
        }
        startYLine = Statusbar.getCustomStatusBarHeight(getContext()) + actionBarSize + PADDING;

        softKey = new SoftKey(getContext());
        softKey.setY(screenHeight);
        addView(softKey);

        touchPad = new TouchPad(getContext());
        ControlUiView.this.addView(touchPad, new FrameLayout.LayoutParams(screenWidth - (PADDING * 2)
                , screenHeight - actionBarSize - Statusbar.getCustomStatusBarHeight(getContext()) - (btnSize + PADDING * 3) - softKey.getSoftKeyViewHeight()));
        touchPad.setX(-screenWidth);
        touchPad.setY(startYLine + btnSize + PADDING);
        touchPad.setPadColor(Color.RED);

        keyboard = new SoftKeyboard(getContext());
        mouse = new AirMouse(getContext());
        touchMouse = new TouchMouse(getContext());
        makeUi = new MakeUi(getContext());

        addView(keyboard, new FrameLayout.LayoutParams(btnSize, btnSize));
        addView(mouse, new FrameLayout.LayoutParams(btnSize, btnSize));
        addView(touchMouse, new FrameLayout.LayoutParams(btnSize, btnSize));
        addView(makeUi,new FrameLayout.LayoutParams(btnSize,btnSize));

        keyboard.setY(getHeight());
        keyboard.setOnClickListener(new OnClickListener() {
            private boolean flag = false;
            private final int DURATION = 600;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    if (isSoftKey) {
                        new TransitionAnimation(softKey, 0, 0, getHeight() - softKey.getSoftKeyViewHeight(), getHeight())
                                .setInterpolator(new DecelerateInterpolator())
                                .start(DURATION, new AnimationCallBack() {
                                    @Override
                                    public void callBack() {
                                        flag = false;
                                    }
                                });
                    } else {
                        new TransitionAnimation(softKey, 0, 0, getHeight(), getHeight() - softKey.getSoftKeyViewHeight())
                                .setInterpolator(new DecelerateInterpolator())
                                .start(DURATION, new AnimationCallBack() {
                                    @Override
                                    public void callBack() {
                                        flag = false;
                                    }
                                });
                    }
                    isSoftKey = !isSoftKey;
                }
            }
        });
        mouse.setY(screenHeight);
        touchMouse.setY(screenHeight);
        touchMouse.setOnClickListener(new OnClickListener() {
            private boolean flag = false;
            private int DURATION = 600;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    System.out.println("touch패드 y값" + touchPad.getY());
                    if (isTouchPad) {

                        new TransitionAnimation(touchPad, PADDING, -screenWidth, touchPad.getY(), touchPad.getY())
                                .setInterpolator(new DecelerateInterpolator())
                                .start(DURATION, new AnimationCallBack() {
                                    @Override
                                    public void callBack() {
                                        flag = false;
                                    }
                                });
                    } else {
                        new TransitionAnimation(touchPad, -screenWidth, PADDING, touchPad.getY(), touchPad.getY())
                                .setInterpolator(new DecelerateInterpolator())
                                .start(DURATION, new AnimationCallBack() {
                                    @Override
                                    public void callBack() {
                                        flag = false;
                                    }
                                });
                    }
                    isTouchPad = !isTouchPad;
                }
            }
        });
        makeUi.setY(screenHeight);
        makeUi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout dialogParentView = new LinearLayout(getContext());
                dialogParentView.setPadding(0,0,0,DIPManager.dip2px(16,getContext()));
                dialogParentView.setOrientation(LinearLayout.VERTICAL);
                listTitle = new TextView(getContext());


                MommooDb mommooDb = new MommooDb(getContext(),"MyUi",null,DATABASE_VERSION);
                SQLiteDatabase db = mommooDb.getReadableDatabase();
                Cursor cursor = db.rawQuery("select UiName from MyUi",null);
                ArrayList<String> list = new ArrayList<>();
                while(cursor.moveToNext()){
                    list.add(cursor.getString(0));
                }
                mommooDb.close();
                db.close();
                cursor.close();

                clientUiList = new RecyclerView(getContext());
                clientUiList.setHasFixedSize(true);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(),list,activityName);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                clientUiList.setAdapter(adapter);
                clientUiList.setLayoutManager(layoutManager);

                int count = adapter.getItemCount();
                String listMessage = "내 리스트("+count+"개)";
                listTitle.setText(listMessage);
                listTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                listTitle.setPadding(0, DIPManager.dip2px(8, getContext()), 0, DIPManager.dip2px(8, getContext()));

                BtnCardView makeUiBtn = new BtnCardView(getContext());
                makeUiBtn.setTitle("UI 만들기");
                dialogParentView.addView(listTitle,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                int height = 60;
                dialogParentView.addView(clientUiList, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DIPManager.dip2px(count > 2 ? height*3 : count * height, getContext())));
                FrameLayout frameLayout = new FrameLayout(getContext());
                frameLayout.addView(makeUiBtn, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DIPManager.dip2px(50, getContext())));
                dialogParentView.addView(frameLayout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DIPManager.dip2px(52,getContext())));
                MommooDialog dialog = new MommooDialog(getContext(),R.style.Theme_AppCompat_Translucent);

                dialog.setTitle("내 리스트");
                dialog.setMessage("저장된 UI 리스트를 터치하거나\n새로운 UI를 만드세요.");
                dialog.setCancelable(true);
                dialog.displayButton(false);
                dialog.addLowerView(dialogParentView);
                dialog.show();

                makeUiBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),UiActivity.class);
                        intent.putExtra("activityName",activityName);
                        ((Activity)getContext()).startActivityForResult(intent,0);
                    }
                });
            }
        });

    }

    private void setTransitionAnim(View targetView,int position){
        int positionX = PADDING + position*(PADDING+btnSize);
        int positionY = startYLine;
        new TransitionAnimation(targetView,positionX,positionX,0,positionY)
                .setInterpolator(new OvershootInterpolator())
                .setDelay(position*300)
                .start(700, null);
    }

    public void start(){
        setTransitionAnim(keyboard,0);
        setTransitionAnim(mouse,1);
        setTransitionAnim(touchMouse, 2);
        setTransitionAnim(makeUi, 3);
    }

    public SoftKey getSoftKeyView(){
        return softKey;
    }
    public void setMouseEvent(MouseEvent mouseEvent, MouseClickEvent clickEvent){
        this.mouseEvent = mouseEvent;
        touchPad.setMouseEvent(mouseEvent);
        touchPad.setMouseClickEvent(clickEvent);
        mouse.setMouseEvent(mouseEvent);
    }

    public void setClickTouchListener(OnClickTouchListener clickTouchListener){
        mouse.setOnClickTouchListener(clickTouchListener);
    }

    public void notifyDataChanged(){
        MommooDb mommooDb = new MommooDb(getContext(),"MyUi",null,DATABASE_VERSION);
        SQLiteDatabase db = mommooDb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select UiName from MyUi",null);
        ArrayList<String> list = new ArrayList<>();
        while(cursor.moveToNext()){
            list.add(cursor.getString(0));
        }
        mommooDb.close();
        db.close();
        cursor.close();
        RecyclerViewAdapter adapter = ((RecyclerViewAdapter) clientUiList.getAdapter());
        adapter.setList(list);
        adapter.notifyDataSetChanged();
        int count = adapter.getItemCount();
        String listMessage = "내 리스트("+count+"개)";
        listTitle.setText(listMessage);
    }

    public void setStream(Socket socket){
        streamSerialize = new StreamSerialize(socket);
    }

    private StreamSerialize getStream(){
        return streamSerialize;
    }
}

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<String> list;
    private String activityName;

    public RecyclerViewAdapter(Context context,ArrayList<String> list,String activityName){
        this.context = context;
        this.list = list;
        this.activityName = activityName;
    }

    public void setList(ArrayList<String> list){
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public MaterialRippleLayout rootView;
        public ViewHolder(View view){
            super(view);
            textView = (TextView)view.findViewById(R.id.textView);
            rootView = (MaterialRippleLayout)view.findViewById(R.id.rootView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(list.get(position));
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,UiActivity.class);
                intent.putExtra("uiName",list.get(position));
                intent.putExtra("activityName",activityName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewadapter_item_view,null));
    }

    @Override
    public int getItemCount(){
        return list.size();
    }
}
