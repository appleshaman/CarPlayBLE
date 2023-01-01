package com.example.carplay_android;

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
    private BleDevice bleDevice;
    private List<BleDevice> bleDevices;

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public List<BleDevice> getBleDevices() {
        return bleDevices;
    }

    public void setBleDevices(List<BleDevice> bleDevices) {
        this.bleDevices = bleDevices;
    }

    public String getName() {
        return bleDevice.getName();
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }
}
