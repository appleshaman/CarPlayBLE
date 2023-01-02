package com.example.carplay_android;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class BleService extends Service {


    private Timer timerBTState;
    private Handler handler = new Handler();



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BleBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setBTCheckTimer();
    }

    public void setBTCheckTimer(){
        if(timerBTState == null){
            timerBTState = new Timer();
            TimerTask  timerTask = new TimerTask() {
                @Override
                public void run() {
                    BleManager.getInstance().init(getApplication());
                    boolean status = false;
                    if(BleManager.getInstance().isSupportBle()){
                        if(!BleManager.getInstance().isBlueEnable()){
                            BleManager.getInstance().enableBluetooth();
                            //check again see if BT is enabled
                            status = !BleManager.getInstance().isBlueEnable();
                        }else{
                            status = true;
                        }
                    }
                    Intent intent = new Intent();
                    intent.setAction("BT");
                    intent.putExtra("BT", status);
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManager.sendBroadcast(intent);
                }
            };
            timerBTState.schedule(timerTask, 10, 1000);
        }
    }

    public class BleBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }

        public void connectLeDevice(BleDevice bleDevice){
            BleManager.getInstance().connect((BleDevice) bleDevice, new BleGattCallback() {
                @Override
                public void onStartConnect() {

                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                    Log.d("s","Connect failed");
                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    Log.d("s","Connect success");
                    List<BluetoothGattService> serviceList = gatt.getServices();
                    for (BluetoothGattService service : serviceList) {
                        UUID uuid_service = service.getUuid();

                        List<BluetoothGattCharacteristic> characteristicList= service.getCharacteristics();
                        for(BluetoothGattCharacteristic characteristic : characteristicList) {
                            UUID uuid_chara = characteristic.getUuid();
                        }
                    }
                    String uuid_service;
                    String uuid_characteristic_read;
                    BleManager.getInstance().read(
                            bleDevice,
                            uuid_service = "4fafc201-1fb5-459e-8fcc-c5c9c331914b",
                            uuid_characteristic_read ="beb5483e-36e1-4688-b7f5-ea07361b26a8",
                            new BleReadCallback() {
                                @Override
                                public void onReadSuccess(byte[] data) {
                                    // 读特征值数据成功
                                    Log.d("1","读特征值数据成功");
                                    Log.d("1", new String(data));
                                }

                                @Override
                                public void onReadFailure(BleException exception) {
                                    // 读特征值数据失败
                                    Log.d("1","读特征值数据失败");
                                }
                            });

                    String uuid_characteristic_write = "beb5483e-36e1-4688-b7f5-ea07361b26a8";
                    byte[] data = "123312334".getBytes();
                    for (int i =0; i < 10; i++) {
                        BleManager.getInstance().write(
                                bleDevice,
                                uuid_service,
                                uuid_characteristic_write,
                                data,
                                new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                        // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                                        Log.d("1", "发送成功");
                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {
                                        // 发送数据到设备失败
                                        Log.d("1", "发送shibai");
                                    }
                                });
                    }


                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                    Log.d("s","Disconnected");
                }
            });
        }

    }



}

