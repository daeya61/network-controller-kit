package com.mommoo.suityourself;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mommoo.customview.BtnCardView;
import com.mommoo.customview.ControlUiView;
import com.mommoo.customview.AirMouse;
import com.mommoo.customview.MouseClickEvent;
import com.mommoo.customview.MouseEvent;
import com.mommoo.customview.OnClickTouchListener;
import com.mommoo.customview.SoftKey;
import com.mommoo.customview.SoftKeyEvent;
import com.mommoo.customview.TouchPad;
import com.mommoo.customview.UiExampleView;
import com.mommoo.db.MommooDb;
import com.mommoo.tool.ActionBar;
import com.mommoo.tool.Statusbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import mommoo.com.library.MommooThread;
import mommoo.com.library.SearchView;
import mommoo.com.library.anim.AnimationCallBack;
import mommoo.com.library.anim.TransitionAnimation;
import mommoo.com.library.manager.DIPManager;
import mommoo.com.library.manager.ScreenManager;
import mommoo.com.library.widget.MommooDialog;

/**
 * Created by mommoo on 2016-04-19.
 */
public class UiActivity extends AppCompatActivity implements View.OnClickListener{

    private int PADDING,dialogWidth;
    private int buttonSize;
    private MommooDialog mommooDialog;
    private FrameLayout rootView;
    private GridView gridView;
    private TextView textArea;
    private ArrayList<String> messages;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ArrayList<FunctionInfo> functionInfo = new ArrayList<>();
    private boolean isSoftKey,isTouchPad,isAirMouse;
    private SoftKey softKey;
    private TouchPad touchPad;
    private int touchPadHeight;
    private AirMouse airMouse;
    private String activityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String uiName = getIntent().getStringExtra("uiName");
        activityName = getIntent().getStringExtra("activityName");
        try{
            if(activityName!=null) {
                if (activityName.equals("Wifi")) {
//                    inputStream = WifiActivity.socket.getInputStream();
//                    outputStream = WifiActivity.socket.getOutputStream();
                } else if (activityName.equals("Blue")) {
                    inputStream = BluetoothActivity.socket.getInputStream();
                    outputStream = BluetoothActivity.socket.getOutputStream();
                }
            }
        }catch(IOException io){
            io.printStackTrace();
        }
        setContentView(R.layout.ui_activity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messages = new ArrayList<>();
        rootView = (FrameLayout)findViewById(R.id.rootView);
        gridView = (GridView)findViewById(R.id.gridView);

        softKey = new SoftKey(this);
        rootView.addView(softKey);
        softKey.setY(new ScreenManager(this).getScreenHeight());
        softKey.setSoftKyEvent(new SoftKeyEvent() {
            @Override
            public void onClick(String keyValue) {
                sendMessage(keyValue.getBytes());
                sendMessage(("01" + keyValue.substring(2)).getBytes());
            }
        });

        touchPad = new TouchPad(this);
        touchPadHeight = DIPManager.dip2px(210,this);
        rootView.addView(touchPad, new FrameLayout.LayoutParams(new ScreenManager(this).getScreenWidth() - DIPManager.dip2px(20, this), touchPadHeight));
        touchPad.setPadColor(Integer.parseInt(getResources().getStringArray(R.array.colors)[(int) (Math.random() * 19)]));
        touchPad.setY(new ScreenManager(this).getScreenHeight() - softKey.getSoftKeyViewHeight() - touchPadHeight);
        touchPad.setX(-new ScreenManager(this).getScreenWidth());
        touchPad.setMouseEvent(new MouseEvent() {
            @Override
            public void onMove(String mousePoint) {
                sendMessage(mousePoint.getBytes());
            }
        });
        touchPad.setMouseClickEvent(new MouseClickEvent() {
            @Override
            public void onPress() {
                sendMessage("2/".getBytes());
            }

            @Override
            public void onRelease() {
                sendMessage("3/".getBytes());
            }
        });
        
        
        airMouse = new AirMouse(this);
        airMouse.setMouseEvent(new MouseEvent() {
            @Override
            public void onMove(String mousePoint) {
                sendMessage(mousePoint.getBytes());
            }
        });

        airMouse.setOnClickTouchListener(new OnClickTouchListener() {
            @Override
            public void onActionDown() {
                sendMessage("2/".getBytes());
            }

            @Override
            public void onActionMove() {

            }

            @Override
            public void onActionUp() {
                sendMessage("3/".getBytes());
            }
        });

        PADDING = DIPManager.dip2px(15,this);
        dialogWidth = new ScreenManager(this).getScreenWidth() - PADDING*6;
        buttonSize = ((dialogWidth - PADDING*3)/2);

        if(uiName == null) showChoiceDialog();
        else{
            getSupportActionBar().setTitle(uiName);

            int row = 0, col = 0;
            String function="";
            MommooDb mommooDb = new MommooDb(UiActivity.this,"MyUi",null, ControlUiView.DATABASE_VERSION);
            SQLiteDatabase database = mommooDb.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from MyUi where UiName='"+uiName+"'",null);
            while(cursor.moveToNext()){
                row = Integer.parseInt(cursor.getString(1));
                col = Integer.parseInt(cursor.getString(2));
                function = cursor.getString(3);
            }
            for(int i=0; i<row*col;i++) functionInfo.add(new FunctionInfo(false,false,false));
            String[] ui = function.split("/");
            GridViewAdapter adapter = setGridView(row, col);
            UiExampleView uiExampleView = setUiView(row,col);
            for(String tempUi : ui){
                if(!tempUi.equals("")){
                    String[] uiInfo = tempUi.split(",");
                    int position = Integer.parseInt(uiInfo[0]);
                    String uiBtnName = uiInfo[1];
                    String uiFunction = uiInfo[2];
                    String[] keyList = uiFunction.split("\\+");
                    ArrayList<String> tempList = new ArrayList<>();
                    int index = 0;
                    for(String func : keyList){
                        func = func.trim();
                        if(func.equals("키보드 기능")) {
                            functionInfo.get(position).keyboard = true;
                        }else if(func.equals("에어마우스")){
                            functionInfo.get(position).airMouse = true;
                        }else if(func.equals("터치패드")){
                            functionInfo.get(position).touchPad = true;
                        }else{
                            tempList.add(func.substring(0, func.length() - 1));
                        }
                    }
                    functionInfo.get(position).addKey(tempList.toArray(new String[tempList.size()]));
                    for(String key : functionInfo.get(position).getKeyCombination()){
                        System.out.println("key 나열 : "+key);
                    }
                    System.out.println("keyboard : "+ functionInfo.get(position).keyboard);
                    System.out.println("touchPad : "+ functionInfo.get(position).touchPad);
                    System.out.println("airMouse : "+ functionInfo.get(position).airMouse);
                    adapter.registerUiBtn(position, uiBtnName);
                    adapter.setFuctionInfoArray(functionInfo);
                    uiExampleView.setRectExist(position, true);
                }
            }
            adapter.notifyDataSetChanged();
            mommooDb.close();
            database.close();
            cursor.close();
        }
    }

    public void showChoiceDialog(){
        mommooDialog = new MommooDialog(this,R.style.Theme_AppCompat_Translucent);
        mommooDialog.setWidth(dialogWidth);
        mommooDialog.setTitle("레이아웃 선택");
        mommooDialog.setMessage("UI버튼의 정렬방식을 선택 해주세요.");
        mommooDialog.displayButton(false);

        LinearLayout rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);

        LinearLayout parentUiView = new LinearLayout(this);
        parentUiView.setOrientation(LinearLayout.HORIZONTAL);
        parentUiView.setPadding(0, 0, 0, PADDING);

        LinearLayout parentUiView2 = new LinearLayout(this);
        parentUiView2.setOrientation(LinearLayout.HORIZONTAL);
        parentUiView2.setPadding(0, 0, 0, PADDING);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(buttonSize, 4*buttonSize/3);
        params.setMargins(PADDING, 0, 0, 0);
        parentUiView.addView(makeButton(2, 1), params);
        parentUiView.addView(makeButton(2,2),params);
        parentUiView2.addView(makeButton(3, 2), params);
        parentUiView2.addView(makeButton(3, 3), params);

        rootView.addView(parentUiView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3*buttonSize/2));
        rootView.addView(parentUiView2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3*buttonSize/2));

        mommooDialog.addLowerView(rootView);
        mommooDialog.setLowerRootViewPadding(0, 0, 0, 0);
        mommooDialog.show();
        mommooDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mommooDialog.dismiss();
                        UiActivity.this.finish();
                    }
                }
                return false;
            }
        });
    }

    public GridViewAdapter setGridView(int row, int col){
        int padding = DIPManager.dip2px(25, UiActivity.this);
        GridViewAdapter adapter = new GridViewAdapter(UiActivity.this,row,col,padding);
        gridView.setNumColumns(col);
        gridView.setColumnWidth((new ScreenManager(UiActivity.this).getScreenWidth() - (padding * col + 1)) / col);

        gridView.setHorizontalSpacing(padding);
        gridView.setVerticalSpacing(padding);
        gridView.setPadding(padding, padding, padding, padding);
        gridView.setAdapter(adapter);
        return adapter;
    }

    public UiExampleView setUiView(int row, int col){
        UiExampleView uiView = new UiExampleView(UiActivity.this,row,col);
        uiView.setStroke(DIPManager.dip2px(3, UiActivity.this));
        uiView.setRectColor(ContextCompat.getColor(UiActivity.this, R.color.colorPrimaryDark));
        uiView.setInVisibleMode(true);
        int padding = DIPManager.dip2px(25, UiActivity.this);
        uiView.setPadding(padding);
        rootView.addView(uiView, 0);
        return uiView;
    }

    public CardView makeButton(final int row,final int col){
        for(int i=0; i<row*col;i++) functionInfo.add(new FunctionInfo(false,false,false));
        MaterialRippleLayout btn = new MaterialRippleLayout(this);
        CardView cardView = new CardView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        UiExampleView uiView = new UiExampleView(this,row,col);
        uiView.setAllRectExist(true);
        uiView.setRectColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        TextView uiText = new TextView(this);
        uiText.setGravity(Gravity.CENTER);
        String message = col + "x" +row+"배열";
        uiText.setText(message);
        uiText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        uiText.setPadding(0, 0, 0, DIPManager.dip2px(8, this));
        linearLayout.addView(uiView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, buttonSize));
        linearLayout.addView(uiText, new LinearLayout.LayoutParams(buttonSize, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn.addView(linearLayout);
        cardView.addView(btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mommooDialog.dismiss();
                final MommooDialog dialog = new MommooDialog(UiActivity.this, R.style.Theme_AppCompat_Translucent);
                dialog.setTitle("이름을 지어주세요.");
                dialog.setMessage("현재 이름의 리스트로 저장됩니다.");
                dialog.addEditText("Title 입력");
                dialog.displayButton(false);
                BtnCardView btn = new BtnCardView(UiActivity.this);
                btn.setTitle("시작하기");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String titleName = dialog.getEditTextToString(0);
                        if (titleName.equals("")) {
                            Toast.makeText(UiActivity.this, "이름을 지어주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            MommooDb mommooDb = new MommooDb(UiActivity.this, "MyUi", null, ControlUiView.DATABASE_VERSION);
                            SQLiteDatabase db = mommooDb.getWritableDatabase();
                            Cursor cursor = db.rawQuery("select function from MyUi where UiName ='" + titleName + "'", null);
                            if (cursor.getCount() > 0) {
                                Toast.makeText(UiActivity.this, "존재하는 Title입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                db.execSQL("insert into MyUi values('" + titleName + "','" + row + "','" + col + "','')");
                                Log.e("INSERT", "디비 생성");
                                UiActivity.this.setTitle(titleName);
                            }
                            dialog.dismiss();

                            setUiView(row, col);
                            setGridView(row, col);
                        }
                    }
                });

                btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DIPManager.dip2px(60, UiActivity.this)));
                dialog.addLowerView(btn);
                View paddingView = new View(UiActivity.this);
                paddingView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DIPManager.dip2px(16, UiActivity.this)));
                dialog.addLowerView(paddingView);
                dialog.show();
            }
        });
        return cardView;
    }

    public void startUiMakeDialog(final int row, final int col, final int position){
        messages.clear();
        final Context context = UiActivity.this;
        final MommooDialog dialog = new MommooDialog(context,R.style.Theme_AppCompat_Translucent);
        dialog.setTitle("UI만들기");
        dialog.setMessage("원하는 조합을 만든후 저장하세요");
        dialog.addEditText("UI이름");
        dialog.displayButton(false);
        int width = new ScreenManager(context).getScreenWidth()-DIPManager.dip2px(60,context);

        dialog.setWidth(width);
        dialog.setCancelable(true);
        View view = LayoutInflater.from(context).inflate(R.layout.ui_make_dialog_lowerview,null);

        textArea = (TextView)view.findViewById(R.id.textArea);
        int btnWidth = (width - DIPManager.dip2px(22,context)*3)/2;
        BtnCardView saveBtn = (BtnCardView)view.findViewById(R.id.btn); saveBtn.setTitle("저장하기");
        saveBtn.getLayoutParams().width = width -(DIPManager.dip2px(22,context)*2) - (DIPManager.dip2px(9,this));
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String btnName = dialog.getEditTextToString(0);
                if (btnName.equals("")) {
                    Toast.makeText(context, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (messages.size() == 0) {
                    Toast.makeText(context, "기능을 넣어주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean flag = false;

                    String itemList = position +","+btnName+","+textArea.getText();
                    MommooDb mommooDb = new MommooDb(context, "MyUi", null, ControlUiView.DATABASE_VERSION);
                    SQLiteDatabase db = mommooDb.getWritableDatabase();
                    Cursor cursor = db.rawQuery("select function from MyUi where UiName ='"+UiActivity.this.getSupportActionBar().getTitle()+"'",null);
                    String function="";
                    if(cursor.getCount()>0){
                        while(cursor.moveToNext()){
                            function = cursor.getString(0);
                        }
                        String[] items = function.split("/");
                        function="";
                        for(String item : items){
                            String[] itemInfo = item.split(",");
                            if(!item.equals("")) {
                                if (position == Integer.parseInt(itemInfo[0])) {
                                    flag = true;
                                    item = itemList;
                                }
                                function += item + "/";
                            }
                        }

                        if(!flag){
                            function += itemList;
                        }else{
                            function = function.substring(0,function.length()-1);
                        }

                        String[] funcList = textArea.getText().toString().split("\\+");
                        ArrayList<String> tempList = new ArrayList<>();
                        for(String func : funcList){
                            func = func.trim();
                            if(func.equals("키보드 기능")) {
                                functionInfo.get(position).keyboard = true;
                            }else if(func.equals("에어마우스")){
                                functionInfo.get(position).airMouse = true;
                            }else if(func.equals("터치패드")){
                                functionInfo.get(position).touchPad = true;
                            }else{
                                System.out.println(func.substring(0, func.length() - 1));
                                tempList.add(func.substring(0, func.length() - 1));
                            }
                            functionInfo.get(position).addKey(tempList.toArray(new String[tempList.size()]));
                        }

                        String updateQuery = "update MyUi set function = '"+function+"' where UiName='"+UiActivity.this.getSupportActionBar().getTitle()+"'";
                        db.execSQL(updateQuery);
                        Log.e("UPDATE", function + "로 업데이트");
                        GridViewAdapter adapter = ((GridViewAdapter)gridView.getAdapter());
                        adapter.setFuctionInfoArray(functionInfo);
                        adapter.registerUiBtn(position, btnName);
                        SearchView searchView = new SearchView();
                        UiExampleView uiView = searchView.getChildView(rootView,"com.mommoo.customview.UiExampleView");
                        //uiView.setRectColor();
                        uiView.setRectExist(position,true);
                    }
                    dialog.dismiss();
                    db.close();
                    mommooDb.close();
                    cursor.close();
                    Toast.makeText(context,"저장완료!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        BtnCardView backBtn = (BtnCardView)view.findViewById(R.id.backBtn); backBtn.setTitle("Back\nSpace"); backBtn.setOnClickListener(this);

        BtnCardView btn1 = (BtnCardView)view.findViewById(R.id.listBtn1); btn1.setTitle("키조합"); btn1.getLayoutParams().width = btnWidth-DIPManager.dip2px(1,this);
        BtnCardView btn2 = (BtnCardView)view.findViewById(R.id.listBtn2); btn2.setTitle("키보드 기능"); btn2.getLayoutParams().width = btnWidth-DIPManager.dip2px(1,this);
        BtnCardView btn3 = (BtnCardView)view.findViewById(R.id.listBtn3); btn3.setTitle("에어마우스"); btn3.getLayoutParams().width = btnWidth-DIPManager.dip2px(1,this);
        BtnCardView btn4 = (BtnCardView)view.findViewById(R.id.listBtn4); btn4.setTitle("터치패드"); btn4.getLayoutParams().width = btnWidth-DIPManager.dip2px(1,this);
        btn1.setOnClickListener(new View.OnClickListener() {
            private boolean isClick = false;
            private final int DURATION = 300;
            @Override
            public void onClick(View v) {
                if(!isClick){
                    isClick = true;
                    dialog.setCancelable(false);
                    final SoftKey softKey = new SoftKey(context);
                    final int screenHeight = new ScreenManager(context).getScreenHeight();
                    softKey.setY(screenHeight);
                    ((ViewGroup)dialog.getWindow().getDecorView()).addView(softKey);

                    new TransitionAnimation(softKey,0,0,screenHeight,screenHeight - softKey.getSoftKeyViewHeight())
                            .setInterpolator(new DecelerateInterpolator()).start(DURATION, new AnimationCallBack() {
                        @Override
                        public void callBack() {
                            softKey.setSoftKyEvent(new SoftKeyEvent() {
                                @Override
                                public void onClick(String keyValue) {
                                    String message;
                                    keyValue = keyValue.substring(2).substring(0,keyValue.length()-3) + "키";
                                    if(textArea.getText().equals("")){
                                        message = keyValue;
                                    }else{
                                        message = textArea.getText() +" + " + keyValue;
                                    }
                                    messages.add(keyValue);
                                    textArea.setText(message);
                                }
                            });
                            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {

                                    if(event.getAction() == KeyEvent.ACTION_UP){
                                        if(keyCode == KeyEvent.KEYCODE_BACK){
                                            new TransitionAnimation(softKey, 0, 0, softKey.getY(), screenHeight)
                                                    .setInterpolator(new DecelerateInterpolator()).start(DURATION, new AnimationCallBack() {
                                                @Override
                                                public void callBack() {
                                                    isClick = false;
                                                    dialog.setCancelable(true);
                                                }
                                            });
                                        }
                                    }
                                    return false;
                                }
                            });
                        }
                    });
                }
            }
        });
        btn2.setOnClickListener(this); btn3.setOnClickListener(this); btn4.setOnClickListener(this);

        dialog.addLowerView(view);
        dialog.show();
    }

    public void sendMessage(final byte[] bytes){
        try{
            if(!activityName.equals("Wifi"))outputStream.write(bytes);
            else if(activityName.equals("Wifi")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            final InetAddress server =InetAddress.getByName(WifiActivity.serverIp);
                            WifiActivity.socket.send(new DatagramPacket(bytes,bytes.length,server,WifiActivity.SERVER_PORT));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }catch(IOException io){
            io.printStackTrace();
        }
    }

    @Override
    public void onClick(View view){
        BtnCardView btnCardView = ((BtnCardView)view.getParent().getParent());
        if(btnCardView.getTitle().equals("키보드 기능")||btnCardView.getTitle().equals("에어마우스")||btnCardView.getTitle().equals("터치패드")){
            if(!messages.contains(btnCardView.getTitle())){
                messages.add(btnCardView.getTitle());
                String message="";
                for(String text : messages){
                    if(message.equals("")){
                        message = text;
                    }else{
                        message += " + "+text;
                    }
                }
                textArea.setText(message);
            }
        }else if(btnCardView.getTitle().equals("Back\nSpace")){
            if(messages.size() != 0) {
                messages.remove(messages.size()-1);
                String message="";
                for(String text : messages){
                    if(message.equals("")){
                        message = text;
                    }else{
                        message += " + "+text;
                    }
                }
                textArea.setText(message);
            }
        }
    }

    class GridViewAdapter extends BaseAdapter{
        private Context context;
        private int viewWidth,viewHeight;
        private boolean[] exist;
        private String[] btnNames,colors;
        private int itemCount;
        private int row, col;
        private ArrayList<FunctionInfo> functionInfoArray;

        public GridViewAdapter(Context context,int row,int col,int padding){
            this.context = context;
            this.row = row;
            this.col = col;
            this.itemCount = row*col;
            this.exist = new boolean[getCount()];
            this.btnNames = new String[getCount()];
            colors = context.getResources().getStringArray(R.array.colors);
            this.viewWidth = new ScreenManager(context).getScreenWidth();
            this.viewHeight = (new ScreenManager(context).getScreenHeight() - Statusbar.getStatusBarHeight(context) - ActionBar.getActionBarHeight(context))
                    - (padding*(row+1)) -DIPManager.dip2px(4,context) ;//padding값과 액션바 그림자 값까지
            System.out.println("viewHeight:"+viewHeight);
        }

        public void setFuctionInfoArray(ArrayList<FunctionInfo> fuctionInfoArray){
            this.functionInfoArray = fuctionInfoArray;
        }

        @Override
        public int getCount(){
            return itemCount;
        }

        @Override
        public Boolean getItem(int position){
            return exist[position];
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(final int position,View convertView, ViewGroup parent){
            if(exist[position]){
                BtnCardView btnCardView = new BtnCardView(context);
                int height = viewHeight / row;
                btnCardView.setCardBackgroundColor(Integer.parseInt(colors[position]));
                btnCardView.setTitleColor(Color.WHITE);
                btnCardView.setTitle(btnNames[position]);
                btnCardView.setDelay(false);
                ViewGroup.LayoutParams params = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,height-DIPManager.dip2px(4,context));
                btnCardView.setLayoutParams(params);
                btnCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isSoftKey && !isTouchPad && !isAirMouse) {
                            FunctionInfo functionInfo = functionInfoArray.get(position);
                            String[] array = functionInfo.getKeyCombination();
                            int duration = 700;
                            if (array.length > 0) {
                                String message = "";
                                for (String key : array) {
                                    message = "00" + key + "/";
                                    sendMessage(message.getBytes());
                                    MommooThread.sleep(10);
                                }
                                for (String key : array) {
                                    message = "01" + key + "/";
                                    sendMessage(message.getBytes());
                                    MommooThread.sleep(10);
                                }
                            }
                            if (functionInfo.keyboard) {

                                if (!isSoftKey) {
                                    isSoftKey = !isSoftKey;
                                    new TransitionAnimation(softKey, 0, 0, softKey.getY(), rootView.getHeight() - softKey.getSoftKeyViewHeight())
                                            .setInterpolator(new DecelerateInterpolator())
                                            .start(duration, new AnimationCallBack() {
                                                @Override
                                                public void callBack() {

                                                }
                                            });
                                }
                            }
                            if (functionInfo.touchPad) {
                                isTouchPad = !isTouchPad;
                                int y = rootView.getHeight() - softKey.getSoftKeyViewHeight() - touchPadHeight - DIPManager.dip2px(16, context);
                                new TransitionAnimation(touchPad, -new ScreenManager(context).getScreenWidth(), DIPManager.dip2px(10, context), y, y)
                                        .setInterpolator(new DecelerateInterpolator())
                                        .start(duration, new AnimationCallBack() {
                                            @Override
                                            public void callBack() {

                                            }
                                        });
                            }
                            if (functionInfo.airMouse) {
                                System.out.println("에어마우스 실행");
                                if(isAirMouse) isAirMouse = false;
                                airMouse.start();
                            }
                        }
                    }
                });
                return btnCardView;
            }else{
                TextView textView = new TextView(context);
                textView.setText("UI를 등록해주세요.");
                int padding = DIPManager.dip2px(5, context);
                textView.setPadding(padding, padding, padding, padding);
                int height = viewHeight / row;
                ViewGroup.LayoutParams params = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, height);
                textView.setLayoutParams(params);
                textView.setGravity(Gravity.CENTER);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startUiMakeDialog(row, col, position);
                    }
                });
                return textView;
            }
        }

        public void registerUiBtn(int position,String btnName){
            this.exist[position] = true;
            this.btnNames[position] = btnName;
            notifyDataSetChanged();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                System.out.println("HIT!!");
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isSoftKey){
            new TransitionAnimation(softKey,0,0,softKey.getY(),rootView.getHeight())
                    .setInterpolator(new DecelerateInterpolator()).start(700, new AnimationCallBack() {
                @Override
                public void callBack() {
                    isSoftKey = !isSoftKey;
                }
            });
        }
        if(isTouchPad){
            new TransitionAnimation(touchPad,touchPad.getX(),-new ScreenManager(this).getScreenWidth(),touchPad.getY(),touchPad.getY())
                    .setInterpolator(new DecelerateInterpolator()).start(700, new AnimationCallBack() {
                @Override
                public void callBack() {
                    isTouchPad = !isTouchPad;
                }
            });
        }
        if(!isSoftKey&&!isTouchPad)super.onBackPressed();
    }
}

class FunctionInfo{
    private String[] keyCombination;
    public boolean exist;
    public boolean keyboard,airMouse,touchPad;
    public FunctionInfo(boolean keyboard,boolean airMouse,boolean touchPad){
        this.keyboard = keyboard;
        this.airMouse = airMouse;
        this.touchPad = touchPad;
    }
    public void addKey(String... key){
        keyCombination = key;
    }

    public String[] getKeyCombination(){
        return keyCombination;
    }
}



