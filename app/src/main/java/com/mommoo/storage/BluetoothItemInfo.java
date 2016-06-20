package com.mommoo.storage;

import android.bluetooth.BluetoothDevice;

/**
 * Created by mommoo on 2016-04-06.
 */
public class BluetoothItemInfo {
    public BluetoothDevice device;
    public int imageResId;
    public String bluetoothDeviceName;
    public boolean isPairing;
    public BluetoothItemInfo(BluetoothDevice device,int imageResId){
        this.imageResId = imageResId;
        this.device = device;
        this.bluetoothDeviceName = device.getName();
        this.isPairing = true;
    }
}
