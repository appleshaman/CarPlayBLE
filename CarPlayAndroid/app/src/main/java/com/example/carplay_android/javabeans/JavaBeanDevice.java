package com.example.carplay_android.javabeans;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.io.Serializable;
import java.util.List;

public class JavaBeanDevice implements Serializable {
    BleDevice bleDevice;
    List<BleDevice> bleDeviceList;

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public void setBleDeviceList(List<BleDevice> bleDeviceList) {
        this.bleDeviceList = bleDeviceList;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public List<BleDevice> getBleDeviceList() {
        return bleDeviceList;
    }

    public String getName(){
        return bleDevice.getName();
    }
}
