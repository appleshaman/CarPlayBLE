package com.example.carplay_android;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeDeviceListAdapter extends BaseAdapter {
    private List<BleDevice> bleDeviceLeDevices;
    private LayoutInflater layoutInflater;

    private final ExecutorService e1 = Executors.newSingleThreadScheduledExecutor();

    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    public void addDeviceList(List<BleDevice> devices) {
            bleDeviceLeDevices = devices;
    }

    public BleDevice getDevice(int position) {
        return bleDeviceLeDevices.get(position);
    }

    public void clear() {
        bleDeviceLeDevices.clear();
    }

    @Override
    public int getCount() {
        if(bleDeviceLeDevices != null){
            return bleDeviceLeDevices.size();
        }
        return 0;

    }

    @Override
    public Object getItem(int i) {
        //return bleDeviceLeDevices.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = layoutInflater.inflate(R.layout.device_information, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.textViewAddress);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.textViewName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        e1.submit(()->{
            viewHolder.deviceAddress.post(() -> viewHolder.deviceAddress.setText(bleDeviceLeDevices.get(i).getMac()));
            viewHolder.deviceName.post(() -> viewHolder.deviceName.setText(bleDeviceLeDevices.get(i).getName()));
        });
        return view;
    }
}