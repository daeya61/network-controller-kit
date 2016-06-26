package com.mommoo.suityourself;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mommoo.adapter.BluetoothRecyclerViewAdapter;
import com.mommoo.adapter.OnClickListener;
import com.mommoo.customview.ControlUiView;
import com.mommoo.customview.AirMouse;
import com.mommoo.customview.MouseClickEvent;
import com.mommoo.customview.MouseEvent;
import com.mommoo.customview.OnClickTouchListener;
import com.mommoo.customview.SoftKeyEvent;
import com.mommoo.customview.Win10StyleLoadingView;
import com.mommoo.storage.BluetoothItemInfo;
import com.mommoo.tool.Statusbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import mommoo.com.library.SearchView;
import mommoo.com.library.anim.AlphaAnimation;
import mommoo.com.library.anim.AnimationCallBack;
import mommoo.com.library.anim.CircleEffectAnimation;
import mommoo.com.library.manager.DIPManager;
import mommoo.com.library.manager.ScreenManager;
import mommoo.com.library.widget.CancelListener;
import mommoo.com.library.widget.CircleImageView;
import mommoo.com.library.widget.MommooDialog;
import mommoo.com.library.widget.OkayListener;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {
    private int screenWidth, screenHeight, padding, actionBarSize, statusbarSize;
    public boolean readyComplete, semaphore = true, connect;
    private boolean click, isParing, open;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver receiver;
    private LinearLayout layout;
    private TextView textView;
    private Win10StyleLoadingView loadingView;
    private HashMap<String, BluetoothDevice> foundedDevices = new HashMap<>();
    public static BluetoothSocket socket;
    private HandlerForUiThread handler = new HandlerForUiThread();
    private MaterialRippleLayout materialRippleLayout;
    private MommooDialog dialog;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String recentDeviceName;
    private ControlUiView controlUiView;
    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//"00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Statusbar.setStatusBar(findViewById(R.id.statusbar));
        ScreenManager screenManager = new ScreenManager(this);

        screenWidth = screenManager.getScreenWidth();
        screenHeight = screenManager.getScreenHeight();
        TypedValue outValue = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, outValue, true)) {
            actionBarSize = TypedValue.complexToDimensionPixelSize(outValue.data, getResources().getDisplayMetrics());
        } else {
            actionBarSize = DIPManager.dip2px(56, this);
        }
        statusbarSize = Statusbar.getCustomStatusBarHeight(this);
        padding = DIPManager.dip2px(30, this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null)
            if (!bluetoothAdapter.isEnabled()) requestBluetoothEnable(this);
            else {
                readyComplete = true;
                semaphore = false;
            }
        else nullMessage(this);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                while (semaphore) {
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (readyComplete) {
                    addReceiver();
                    Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
                    Iterator<BluetoothDevice> iterator = bluetoothDevices.iterator();
                    ArrayList<BluetoothItemInfo> infoArrayList = new ArrayList<>();
                    while (iterator.hasNext()) {
                        BluetoothDevice device = iterator.next();
                        //System.out.println("deviceName : " + device.getName()+ " / deviceAddress : "+device.getAddress() +" / deviceType : "+getBluetoothDeviceType(device.getBluetoothClass().getMajorDeviceClass()));
                        infoArrayList.add(new BluetoothItemInfo(device, getBluetoothDeviceTypeResId(device.getBluetoothClass().getMajorDeviceClass())));
                    }
                    startBluetoothChoiceDialog(infoArrayList);
                }
            }
        }.execute();
    }

    public void nullMessage(Activity activity) {
        MommooDialog mommooDialog = new MommooDialog(activity);
        mommooDialog.setTitle("블루투스가 없습니다.");
        mommooDialog.setMessage("해당 애플리케이션은 블루투스를 지원하지 않습니다.");
        mommooDialog.show();
    }

    public void requestBluetoothEnable(Activity activity) {
        MommooDialog mommooDialog = new MommooDialog(activity);
        mommooDialog.setTitle("블루투스 승인 요청");
        mommooDialog.setMessage("애플리케이션에서 블루투스 실행을 원합니다. 허용할까요?");
        mommooDialog.show();
        mommooDialog.setOkayListener(new OkayListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter.enable();
                while (!bluetoothAdapter.isEnabled()) {
                } //enable 시점 기다리기....!
                readyComplete = true;
                semaphore = false;
            }
        });
        mommooDialog.setCancelListener(new CancelListener() {
            @Override
            public void onClick(View view) {
                semaphore = false;
                finish();
            }
        });
    }

    public void startDiscovery() {
        if (bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
        else bluetoothAdapter.startDiscovery();
    }

    public void addReceiver() {
        if (this.receiver == null) {
            this.receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, Intent intent) {
                    String action = intent.getAction();
                    System.out.println(action);
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (!foundedDevices.containsKey(device.getAddress())) {
                            foundedDevices.put(device.getAddress(), device);
                            addFoundedDeviceView(context, device);
                        }
                        System.out.println("Device Found!!!");
                    }
                    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        System.out.println("Device Discovery Start!!");
                        startLoadingView(context);
                        textView.setText("중지");
                    }
                    if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        System.out.println("Device Discovery End!!");
                        endLoadingView(context);
                        textView.setText("찾기");
                    }
                    if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                        //if(isParing) {
                        successConnection(context);
                    }
                    if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                        System.out.println("디스컨넥트_리퀘스트");
                    }
                    if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                        System.out.println("디스컨넥티드");
                        if (connect) {
                            MommooDialog dialog = new MommooDialog(BluetoothActivity.this);
                            String message = recentDeviceName + "과 연결이 끊겼습니다.";
                            dialog.setTitle("접속종료");
                            dialog.setMessage(message);
                            dialog.setOkayListener(new OkayListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            });
                            dialog.setCancelListener(new CancelListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            });
                            dialog.show();
                            connect = false;
                        }
                    }
                    if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                        System.out.println("페어링");
                    }
                    if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                        System.out.println("Dfdf");
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(receiver, filter);
        }
    }

    private void successConnection(final Context context){
        dialog.dismiss();
        int duration = 1000;
        CircleEffectAnimation circleAnim = (CircleEffectAnimation) findViewById(R.id.circleAnim);
        circleAnim.setEndAniDuration(duration);
        circleAnim.setCircleColor(ContextCompat.getColor(context, R.color.bluetoothColorRipple));
        circleAnim.start(duration, null);
        controlUiView = new ControlUiView(context,"Blue");
        //controlUiView.setStream(socket);
        new AlphaAnimation(findViewById(R.id.rootView), AlphaAnimation.MIN_ALPHA, AlphaAnimation.MAX_ALPHA)
                .setInterpolator(new AccelerateInterpolator())
                .start(duration, new AnimationCallBack() {
                    @Override
                    public void callBack() {
                        FrameLayout rootView = (FrameLayout) findViewById(R.id.rootView);
                        rootView.addView(controlUiView);
                        controlUiView.start();
                    }
                });
        controlUiView.getSoftKeyView().setSoftKyEvent(new SoftKeyEvent() {
            @Override
            public void onClick(String keyValue) {
                sendMessage(keyValue.getBytes());
                sendMessage(("01" + keyValue.substring(2)).getBytes());
            }
        });
        controlUiView.setMouseEvent(new MouseEvent() {
            @Override
            public void onMove(String mousePoint) {
                sendMessage(mousePoint.getBytes());
            }
        }, new MouseClickEvent() {
            @Override
            public void onPress() {
                sendMessage("2/");
            }

            @Override
            public void onRelease() {
                sendMessage("3/");
            }
        });
        controlUiView.setClickTouchListener(new OnClickTouchListener() {
            @Override
            public void onActionDown() {
                sendMessage("2/");
            }

            @Override
            public void onActionMove() {

            }

            @Override
            public void onActionUp() {
                sendMessage("3/");
            }
        });
        connect = true;
    }

    private void startLoadingView(Context context) {
        if (!open) {
            loadingView = new Win10StyleLoadingView(context);
            int height = DIPManager.dip2px(30, context);
            layout.addView(loadingView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            layout.getLayoutParams().height += height;
            loadingView.setCircleColor(ContextCompat.getColor(context, R.color.bluetoothColorPrimaryDark));
            loadingView.start();
            open = true;
        }
    }

    private void endLoadingView(Context context) {
        if (loadingView.getRootView() != null && open) {
            open = false;
            loadingView.end();
            int height = DIPManager.dip2px(30, context);
            layout.removeView(loadingView);
            layout.getLayoutParams().height -= height;
        }
    }

    public void addFoundedDeviceView(Context context, final BluetoothDevice device) {
        if (foundedDevices.size() == 1) {
            TextView textView = new TextView(this);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setTextColor(ContextCompat.getColor(context, R.color.bluetoothColorPrimaryDark));
            textView.setText("찾은 디바이스(1개)");
            ScrollView scrollView = new ScrollView(context);
            layout.addView(textView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(scrollView, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DIPManager.dip2px(60, context)));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            scrollView.addView(linearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layout.getLayoutParams().height += DIPManager.dip2px(90, context);
        }
        View itemView = getLayoutInflater().inflate(R.layout.bluetooth_recyclerview_item, null);
        ((MaterialRippleLayout) itemView.findViewById(R.id.parentView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairDevice(device, UUID_SPP);
            }
        });
        itemView.findViewById(R.id.rootView).setPadding(0, 0, 0, 0);
        ((CircleImageView) itemView.findViewById(R.id.imageView)).setImageResource(getBluetoothDeviceTypeResId(device.getBluetoothClass().getMajorDeviceClass()));
        ((TextView) itemView.findViewById(R.id.mainTitle)).setText(device.getName());
        ((TextView) itemView.findViewById(R.id.subTitle)).setText(device.getAddress());
        SearchView searchView = new SearchView();
        ScrollView tempScrollView = searchView.getChildView(layout, "android.widget.ScrollView");
        LinearLayout tempLayout = searchView.getChildView(tempScrollView, "android.widget.LinearLayout");
        tempLayout.addView(itemView);
        String itemCount = "찾은 디바이스(" + Integer.toString(tempLayout.getChildCount()) + "개)";
        TextView tempTextView = searchView.getChildView(layout, "android.widget.TextView");
        tempTextView.setText(itemCount);
    }

    public void removeReceiver() {
        if (this.receiver != null) unregisterReceiver(this.receiver);
    }

    public String getBluetoothDeviceType(int major) {
        switch (major) {
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "AUDIO_VIDEO";
            case BluetoothClass.Device.Major.COMPUTER:
                return "COMPUTER";
            case BluetoothClass.Device.Major.HEALTH:
                return "HEALTH";
            case BluetoothClass.Device.Major.IMAGING:
                return "IMAGING";
            case BluetoothClass.Device.Major.MISC:
                return "MISC";
            case BluetoothClass.Device.Major.NETWORKING:
                return "NETWORKING";
            case BluetoothClass.Device.Major.PERIPHERAL:
                return "PERIPHERAL";
            case BluetoothClass.Device.Major.PHONE:
                return "PHONE";
            case BluetoothClass.Device.Major.TOY:
                return "TOY";
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "UNCATEGORIZED";
            case BluetoothClass.Device.Major.WEARABLE:
                return "AUDIO_VIDEO";
            default:
                return "UNKNOWN_DEVICE";
        }
    }

    public int getBluetoothDeviceTypeResId(int major) {
        switch (major) {
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return R.mipmap.audio_video;
            case BluetoothClass.Device.Major.COMPUTER:
                return R.mipmap.computer;
            case BluetoothClass.Device.Major.HEALTH:
                return R.mipmap.computer;
            case BluetoothClass.Device.Major.IMAGING:
                return R.mipmap.computer;
            case BluetoothClass.Device.Major.MISC:
                return R.mipmap.computer;
            case BluetoothClass.Device.Major.NETWORKING:
                return R.mipmap.computer;
            case BluetoothClass.Device.Major.PERIPHERAL:
                return R.mipmap.computer;
            case BluetoothClass.Device.Major.PHONE:
                return R.mipmap.phone;
            case BluetoothClass.Device.Major.TOY:
                return R.mipmap.computer;
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return R.mipmap.computer;
            case BluetoothClass.Device.Major.WEARABLE:
                return R.mipmap.audio_video;
            default:
                return R.mipmap.computer;
        }
    }

    public void startBluetoothChoiceDialog(ArrayList<BluetoothItemInfo> infos) {
        RecyclerView recyclerView = new RecyclerView(this);
        final BluetoothRecyclerViewAdapter adapter = new BluetoothRecyclerViewAdapter(infos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(int position) {
                if (!click) {
                    BluetoothItemInfo info = adapter.getBluetoothInfo(position);
                    isParing = true;
                    recentDeviceName = info.bluetoothDeviceName;
                    requestConnect(info.device, UUID_SPP);
                }
            }
        });

        int dialogWidth = new ScreenManager(this).getScreenWidth() - DIPManager.dip2px(16, this) * 4;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DIPManager.dip2px(180, this));
        recyclerView.setLayoutParams(params);

        int layoutHeight = DIPManager.dip2px(82, this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight));
        int paddingValue = DIPManager.dip2px(16, this);
        layout.setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
        materialRippleLayout = new MaterialRippleLayout(this);
        materialRippleLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.bluetoothColorRipple));
        materialRippleLayout.setRippleColor(ContextCompat.getColor(this, R.color.bluetoothColorPrimaryDark));
        materialRippleLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DIPManager.dip2px(50, this)));
        layout.addView(materialRippleLayout);

        textView = new TextView(this);
        textView.setText("찾기");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(this, R.color.primaryText));
        materialRippleLayout.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        dialog = new MommooDialog(this);
        dialog.setTitle("블루투스 설정");
        dialog.setWidth(dialogWidth);
        TextView textView = new TextView(this);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(ContextCompat.getColor(this, R.color.bluetoothColorPrimaryDark));
        textView.setText("페어링 목록");
        textView.setPadding(paddingValue, 0, 0, 0);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.addLowerView(textView);
        dialog.addLowerView(recyclerView);
        dialog.addLowerView(layout);
        dialog.setLowerRootViewPadding(0, 0, 0, 0);
        dialog.show();

        materialRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });
        dialog.setCancelListener(new CancelListener() {
            @Override
            public void onClick(View view) {
                layout.removeAllViews();
                ((ViewGroup) layout.getRootView()).removeAllViews();
                foundedDevices.clear();
                finish();
            }
        });
        dialog.setOkayListener(new OkayListener() {
            @Override
            public void onClick(View view) {
                layout.removeAllViews();
                ((ViewGroup) layout.getRootView()).removeAllViews();
                foundedDevices.clear();
                finish();
            }
        });
    }

    private void pairDevice(BluetoothDevice device, UUID uuid) {
        try {
            isParing = false;
            requestConnect(device, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BluetoothSocket getBluetoothSocket(BluetoothDevice device, UUID uuid) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    public void requestConnect(final BluetoothDevice device, UUID uuid) {
        click = true;
        if (!isParing) endLoadingView(BluetoothActivity.this);
        startLoadingView(BluetoothActivity.this);
        socket = getBluetoothSocket(device, uuid);
        final Thread thread = new Thread(new Runnable() {
            boolean flag = false;

            @Override
            public void run() {
                try {
                    socket.connect();
                    requestStreaming(socket);
                    flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    flag = false;
                } finally {
                    if (!flag) {
                        sendMessageToHandler(new HandlerEvent() {
                            @Override
                            public void event() {
                                endLoadingView(BluetoothActivity.this);
                                textView.setText("찾기");
                                click = false;
                                Snackbar.make(getWindow().getDecorView(), device.getName() + "이 연결을 허용하지 않습니다.", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
        if (socket != null) {
            thread.start();
        }
        if (handler == null) handler = new HandlerForUiThread();
        textView.setText("연결 중지");
        materialRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endLoadingView(BluetoothActivity.this);
                textView.setText("찾기");
                click = false;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDiscovery();
                    }
                });
            }
        });
    }

    public void sendMessageToHandler(HandlerEvent event) {
        handler.setHandlerEvent(event);
        handler.sendMessage(handler.obtainMessage());
    }

    public void requestStreaming(BluetoothSocket socket) {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(byte[] data) {
        if (outputStream != null) {
            try {
                outputStream.write(data);
                //outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String msg) {
        msg = msg + "\r\n";
        byte[] data = msg.getBytes();
        if (outputStream != null) {
            try {
                outputStream.write(data);
                //outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        MommooDialog dialog = new MommooDialog(this);
        dialog.setTitle("연결종료");
        dialog.setMessage(recentDeviceName + " 와의 연결을 종료할까요?");
        dialog.show();
        dialog.setOkayListener(new OkayListener() {
            @Override
            public void onClick(View view) {
                try {
                    socket.close();
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view instanceof AirMouse){
            System.out.println("Mouse제어모드 클릭");
            AirMouse mouse = (AirMouse)view;
            if((mouse.isOnMouse())){
                mouse.stop();
            }else{
                mouse.start();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int DATABASE_UPDATE = 0;
        if(resultCode == DATABASE_UPDATE){
            System.out.println("디비업데이트 해라!");
            controlUiView.notifyDataChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeReceiver();
    }
}
