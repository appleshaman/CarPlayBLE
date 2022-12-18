package com.example.carplay_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BleService extends Service {



    private Handler handler = new Handler();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BleBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public class BleBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    //通过binder实现调用者client与Service之间的通信
    private BleBinder binder = new BleBinder();


}

