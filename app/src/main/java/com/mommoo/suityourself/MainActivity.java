package com.mommoo.suityourself;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mommoo.customview.ControlUiView;

/**
 * Created by mommoo on 2016-04-07.
 */
public class MainActivity extends AppCompatActivity {

    private boolean flag = true;
    private ControlUiView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //test();
        start();
    }

    public void test() {
        view = new ControlUiView(this,"Main");
        setContentView(view);
        view.start();
        //startActivity(new Intent(this,UiActivity.class));
    }

    public void start() {
        setContentView(R.layout.main_activity);
        MaterialRippleLayout bluetoothBtn = (MaterialRippleLayout) findViewById(R.id.bluetoothBtn);
        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            private boolean click = false;
            @Override
            public void onClick(View v) {
                if (!click) {
                    click = true;
                    if (flag) {
                        flag = false;
                        startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
                        click = false;
                    } else {
                        Snackbar.make(MainActivity.this.getWindow().getDecorView(), "1초뒤에 실행됩니다.", Snackbar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
                                click = false;
                            }
                        }, 1500);
                    }
                }
            }
        });
        MaterialRippleLayout wifi = (MaterialRippleLayout)findViewById(R.id.wifiBtn);
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WifiActivity.class));
            }
        });
    }
}
