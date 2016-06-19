package com.mommoo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mommoo.storage.BluetoothItemInfo;
import com.mommoo.suityourself.R;

import java.util.ArrayList;

import mommoo.com.library.widget.CircleImageView;

/**
 * Created by mommoo on 2016-04-06.
 */
public class BluetoothRecyclerViewAdapter extends RecyclerView.Adapter<BluetoothRecyclerViewAdapter.ViewHolder>{

    private ArrayList<BluetoothItemInfo> bluetoothItemInfos;
    private final String REGISTER = "페어링 등록됨";
    private final String NOT_REGISTER = "페어링 해제됨";
    private OnClickListener onClickListener;

    public BluetoothRecyclerViewAdapter(ArrayList<BluetoothItemInfo> infos){
        this.bluetoothItemInfos = infos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout rootView;
        public CircleImageView imageView;
        public TextView mainTitle,subTitle;
        public MaterialRippleLayout parentView;

        public ViewHolder(View itemView) {
            super(itemView);
            parentView = (MaterialRippleLayout)itemView.findViewById(R.id.parentView);
            rootView = (LinearLayout)itemView.findViewById(R.id.rootView);
            imageView = (CircleImageView)itemView.findViewById(R.id.imageView);
            mainTitle = (TextView)itemView.findViewById(R.id.mainTitle);
            subTitle = (TextView)itemView.findViewById(R.id.subTitle);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageResource(bluetoothItemInfos.get(position).imageResId);
        holder.mainTitle.setText(bluetoothItemInfos.get(position).bluetoothDeviceName);
        holder.subTitle.setText(bluetoothItemInfos.get(position).isPairing?REGISTER:NOT_REGISTER);
        final int tempPositon = position;
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(tempPositon);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_recyclerview_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return bluetoothItemInfos.size();
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public BluetoothItemInfo getBluetoothInfo(int position){
        return bluetoothItemInfos.get(position);
    }
}
