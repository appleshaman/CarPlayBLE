package com.example.carplay_android.services;

import static com.example.carplay_android.javabeans.JavaBeanFilters.*;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.carplay_android.utils.BroadcastUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class BleService extends Service {

    private Timer timerBTState;
    private BleDevice bleDeviceConnectTo;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BleBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setBTCheckTimer();
        BroadcastUtils.sendStatus(true, getFILTER_BLE_STATUS(), getApplicationContext());


    }

    public void setBTCheckTimer() {
        if (timerBTState == null) {
            timerBTState = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    BleManager.getInstance().init(getApplication());
                    boolean status = false;
                    if (BleManager.getInstance().isSupportBle()) {
                        if (!BleManager.getInstance().isBlueEnable()) {
                            BleManager.getInstance().enableBluetooth();
                            //check again see if BT is enabled
                            status = !BleManager.getInstance().isBlueEnable();
                        } else {
                            status = true;
                        }
                    }
                    BroadcastUtils.sendStatus(status, getFILTER_BT_STATUS(), getApplicationContext());
                }
            };
            timerBTState.schedule(timerTask, 10, 1000);
        }
    }

    public class BleBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
        public void setMtu(BleDevice bleDevice){
            BleManager.getInstance().setMtu(bleDevice, 512, new BleMtuChangedCallback() {
                @Override
                public void onSetMTUFailure(BleException exception) {
                    // 设置MTU失败
                    int a = 2;
                }

                @Override
                public void onMtuChanged(int mtu) {
                    // 设置MTU成功，并获得当前设备传输支持的MTU值
                    int a = mtu;
                }
            });
        }

        public void connectLeDevice(BleDevice bleDevice) {
            BleManager.getInstance().connect((BleDevice) bleDevice, new BleGattCallback() {
                @Override
                public void onStartConnect() {

                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                    Log.d("s", "Connect failed");
                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    Log.d("s", "Connect success");

                    BroadcastUtils.sendStatus(true, getFILTER_DEVICE_STATUS(), getApplicationContext());
                    bleDeviceConnectTo = bleDevice;


                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                    Log.d("s", "Disconnected");
                    BroadcastUtils.sendStatus(false, getFILTER_DEVICE_STATUS(), getApplicationContext());

                }
            });
        }

        public void sendDestination(String information){
            String DESTINATION_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";
            sendToDevice(information, DESTINATION_UUID);
        }
        public void sendEta(String information){
            String ETA_UUID = "ca83fac2-2438-4d14-a8ae-a01831c0cf0d";
            sendToDevice(information, ETA_UUID);
        }
        public void sendDirection(String information){
            String DIRECTION_UUID = "dfc521a5-ce89-43bd-82a0-28a37f3a2b5a";
            sendToDevice(information, DIRECTION_UUID);
        }
        public void sendDirectionDistances(String information){
            String DIRECTION_UUID = "0343ff39-994e-481b-9136-036dabc02a0b";
            sendToDevice(information, DIRECTION_UUID);
        }
        public void sendEtaInMinutes(String information){
            String ETA_DISTANCE_UUID = "563c187d-ff17-4a6a-8061-ca9b7b70b2b0";
            sendToDevice(information, ETA_DISTANCE_UUID);
        }
        public void sendDistance(String information){
            String ETA_DISTANCE_UUID = "8bf31540-eb0d-476c-b233-f514678d2afb";
            sendToDevice(information, ETA_DISTANCE_UUID);
        }
        public void sendDirectionPrecise(String information){
            String DIRECTION_PRECISE_UUID = "a602346d-c2bb-4782-8ea7-196a11f85113";
            sendToDevice(information, DIRECTION_PRECISE_UUID);
        }

        private void sendToDevice(String informationMessage, String uuid) {

            String uuid_service = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
            byte[] data = informationMessage.getBytes();
            BleManager.getInstance().write(
                    bleDeviceConnectTo,
                    uuid_service,
                    uuid,
                    data,
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.d("1", "Success to send");
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            // 发送数据到设备失败
                            Log.d("1", "Failed to send");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendToDevice(informationMessage, uuid);
                                }
                            },100);
                        }
                    });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadcastUtils.sendStatus(false, getFILTER_BLE_STATUS(), getApplicationContext());
    }
}

