package com.mommoo.suityourself;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.mommoo.customview.ControlUiView;
import com.mommoo.customview.MouseClickEvent;
import com.mommoo.customview.MouseEvent;
import com.mommoo.customview.OnClickTouchListener;
import com.mommoo.customview.SoftKeyEvent;
import com.mommoo.tool.Statusbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import mommoo.com.library.anim.AlphaAnimation;
import mommoo.com.library.anim.AnimationCallBack;
import mommoo.com.library.anim.CircleEffectAnimation;
import mommoo.com.library.widget.CancelListener;
import mommoo.com.library.widget.MommooDialog;
import mommoo.com.library.widget.MommooProgressDialog;
import mommoo.com.library.widget.OkayListener;

/**
 * Created by mommoo on 2016-04-16.
 */
public class WifiActivity extends AppCompatActivity{
    public static final int SERVER_PORT = 50008;
    public static DatagramSocket socket;
    public static String serverIp;
    private OutputStream outputStream;
    private InputStream inputStream;
    private DatagramPacket dataPacket;
    private DatagramSocket dataSocket;
    private ControlUiView controlUiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_activity);
        Statusbar.setStatusBar(findViewById(R.id.statusbar));
        final MommooDialog dialog = new MommooDialog(this,R.style.Theme_AppCompat_Translucent);
        dialog.setMessage("서버를 키시면 자동 IP를 받을수 있습니다.\n반드시 PC와 같은 네트워크상에 있어야 합니다.");
        dialog.setTitle("와이파이 설정");
        dialog.addEditText("서버IP 입력");
        dialog.show();
        dialog.setCancelListener(new CancelListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        AsyncTask asyncTask = new AsyncTask<Void,Void,Void>(){
            byte[] message = new byte[1024];

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setOkayListener(new OkayListener() {
                    Socket connectForSocket;
                    @Override
                    public void onClick(View view) {
                        serverIp = dialog.getEditTextToString(0);
                        new AsyncTask<Void, Void, Void>() {

                            private MommooProgressDialog progressDialog;
                            private boolean isConnected;
                            private InetAddress server =null;
                            private String msg="";
                            private boolean flag = false;
                            private DatagramPacket messagePacket;

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                progressDialog = new MommooProgressDialog(WifiActivity.this);
                                progressDialog.show();
                                try{
                                    server =InetAddress.getByName(serverIp);
                                    messagePacket = new DatagramPacket(msg.getBytes(), msg.length(), server, SERVER_PORT);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            protected Void doInBackground(Void... params) {
                                try {


                                    socket = new DatagramSocket();
                                    SocketAddress sockAddr = new InetSocketAddress(serverIp, SERVER_PORT);
                                    connectForSocket = new Socket();
                                    connectForSocket.connect(sockAddr,10000);
                                    inputStream = connectForSocket.getInputStream();

                                    System.out.println("연결성공..!!");
                                    isConnected = true;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                while(true){
                                                    while(!flag){}
                                                    flag = false;
                                                    socket.send(messagePacket);
                                                }

                                            }catch(Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                } catch (IOException io) {
                                    io.printStackTrace();
                                    isConnected = false;

                                }
                                return null;
                            }

//                            public void sendMessage(String msg) {
//                                if (outputStream != null) {
//                                    try {
//                                        outputStream.write(msg.getBytes());
//                                    } catch (IOException io) {
//                                        io.printStackTrace();
//                                    }
//                                }
//                            }
                                public void sendMessage(final String msg) {
                                    //this.msg = msg;
                                    messagePacket.setData(msg.getBytes());
                                    flag = true;
                                }
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                progressDialog.dismiss();
                                if (isConnected) {
                                    //dataSocket.close();

                                    int duration = 700;
                                    final FrameLayout frameLayout = (FrameLayout) WifiActivity.this.findViewById(R.id.rootView);
                                    CircleEffectAnimation effectAnimation = (CircleEffectAnimation) WifiActivity.this.findViewById(R.id.circleAnim);
                                    effectAnimation.setCircleColor(ContextCompat.getColor(WifiActivity.this, R.color.wifiColorRipple));
                                    effectAnimation.setEndAniDuration(duration);
                                    effectAnimation.start(duration, null);
                                    new AlphaAnimation(frameLayout, AlphaAnimation.MIN_ALPHA, AlphaAnimation.MAX_ALPHA).start(duration, new AnimationCallBack() {
                                        @Override
                                        public void callBack() {
                                            controlUiView = new ControlUiView(WifiActivity.this,"Wifi");
                                            frameLayout.addView(controlUiView);
                                            //controlUiView.setStream(socket);
                                            controlUiView.start();
                                            controlUiView.getSoftKeyView().setSoftKyEvent(new SoftKeyEvent() {
                                                @Override
                                                public void onClick(String keyValue) {
                                                    sendMessage(keyValue);
                                                    sendMessage("01" + keyValue.substring(2));
                                                }
                                            });
                                            controlUiView.setMouseEvent(new MouseEvent() {
                                                @Override
                                                public void onMove(String mousePoint) {
                                                    sendMessage(mousePoint);
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
                                        }
                                    });
                                    new AsyncTask<Void,Void,Void>(){
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            try {
                                                while (inputStream.read() != -1){};
                                            }catch(IOException io){
                                                io.printStackTrace();
                                            }
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);
                                            MommooDialog mommooDialog = new MommooDialog(WifiActivity.this);
                                            mommooDialog.setTitle("연결이 끊겼습니다.");
                                            mommooDialog.setMessage("종료합니다.");
                                            mommooDialog.setOkayListener(new OkayListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    finish();
                                                }
                                            });
                                            mommooDialog.setCancelListener(new CancelListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    finish();
                                                }
                                            });
                                            mommooDialog.show();
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }else{
                                    MommooDialog mommooDialog = new MommooDialog(WifiActivity.this,R.style.Theme_AppCompat_Translucent);
                                    mommooDialog.setTitle("연결실패!");
                                    mommooDialog.setMessage("다시 연결해주세요.");
                                    mommooDialog.setOkayListener(new OkayListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dataSocket.close();
                                            finish();
                                        }
                                    });
                                    mommooDialog.setCancelListener(new CancelListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dataSocket.close();
                                            finish();
                                        }
                                    });
                                    mommooDialog.show();
                                }
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });
            }

            @Override
            protected Void doInBackground(Void... params) {
                try{
                    dataPacket = new DatagramPacket(message, message.length);
                    dataSocket = new DatagramSocket(SERVER_PORT);
                    System.out.println("여기안탔냐");
                    dataSocket.receive(dataPacket);
                    serverIp = new String(message, 0, dataPacket.getLength());

                    dataSocket.close();
                }catch(Exception e){
                    e.printStackTrace();
                    dataSocket.close();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.getEditText(0).setText(serverIp);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onBackPressed() {
        MommooDialog dialog = new MommooDialog(this,R.style.Theme_AppCompat_Translucent);
        dialog.setTitle("종료");
        dialog.setMessage("연결을 종료할까요?");
        dialog.setOkayListener(new OkayListener() {
            @Override
            public void onClick(View view) {
                try {
                    outputStream.write("2|exit".getBytes());
                    socket.close();
                    outputStream.close();
                    dataSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        dialog.show();
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
    }
}
