package com.example.carplay_android;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Scanner;
import java.util.UUID;

public class BleService extends Service {

<<<<<<< Updated upstream
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    private boolean scanning;
    private Handler handler = new Handler();
=======

>>>>>>> Stashed changes

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
<<<<<<< Updated upstream

    }

=======
    }



    public class BleBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
        public void connectLeDeviceInPosition(int position){
            connectLeDevice((BleDevice) ScanBleDeviceUtils.getDevice(position));
        }
        public void connectLeDevice(BleDevice bleDevice){
            BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                @Override
                public void onStartConnect() {

                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                    Log.d("s","Connect failed");
                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    broadcastTheDevice(bleDevice);
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

>>>>>>> Stashed changes

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

<<<<<<< Updated upstream
    private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();
    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    leDeviceListAdapter.addDevice(result.getDevice());
                    leDeviceListAdapter.notifyDataSetChanged();
                }
            };

=======

    private void broadcastTheDevice(BleDevice bleDevice){
        JavaBeanDevice javaBeanDevice = new JavaBeanDevice();
        javaBeanDevice.setBleDevice(bleDevice);
        Intent intent = new Intent();
        intent.putExtra("device", javaBeanDevice);
        intent.setAction("device");// for intent filter to fit different information
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.sendBroadcast(intent);

    }

>>>>>>> Stashed changes


}
