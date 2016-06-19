package com.mommoo.tool;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by mommoo on 2016-04-21.
 */
public class StreamSerialize implements Serializable{
    public Socket socket;
    public StreamSerialize(Socket socket){
        this.socket = socket;
    }
}
