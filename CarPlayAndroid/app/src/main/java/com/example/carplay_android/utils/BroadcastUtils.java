package com.example.carplay_android.utils;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.clj.fastble.data.BleDevice;
import com.example.carplay_android.JavaBeanDevice;

import java.util.List;

public class BroadcastUtils {
    public static void sendStatus( boolean status, String filter, Context context){
        Intent intent = new Intent();
        intent.setAction(filter);
        intent.putExtra(filter, status);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }
    public static void sendBleDevice(BleDevice bleDevice, String filter, Context context){
        JavaBeanDevice javaBeanDevice = new JavaBeanDevice();
        javaBeanDevice.setBleDevice(bleDevice);
        Intent intent = new Intent();
        intent.setAction(filter);
        intent.putExtra(filter, javaBeanDevice);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }
    public static void sendBleDevices(List<BleDevice> bleDevices, String filter, Context context){
        JavaBeanDevice javaBeanDeviceList = new JavaBeanDevice();
        javaBeanDeviceList.setBleDeviceList(bleDevices);
        Intent intent = new Intent();
        intent.setAction(filter);
        intent.putExtra(filter, javaBeanDeviceList);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }
}
