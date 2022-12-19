package com.example.carplay_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MainActivity extends AppCompatActivity {

    private Button buttonScan;
    private Button buttonConnect;
    private ListView bleList;
    private TextView deviceName;
    private TextView deviceAddress;

    private Handler handler = new Handler();
    private List<BleDevice> resultList;
    private int selected = 0;
    private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = {
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
        };

        requestPermissions(permissions, 200);

        buttonScan = findViewById(R.id.buttonScan);
        buttonConnect = findViewById(R.id.buttonConnect);
        bleList = findViewById(R.id.deviceList);
        deviceAddress = findViewById(R.id.deviceAddress);
        deviceName = findViewById(R.id.deviceName);


        bleList.setAdapter(leDeviceListAdapter);
        bleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(R.id.addressForSingle);
                if(textView.getText() != deviceAddress.getText()){
                    deviceAddress.setText(textView.getText());
                    textView = view.findViewById(R.id.nameForSingle);
                    deviceName.setText(textView.getText());
                    selected = i;
                }
                connectLeDevice(selected);
            }
        });


        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice();
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectLeDevice(selected);
            }
        });

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(5000);

        if(BleManager.getInstance().isSupportBle()){
            Log.d("s","support");
            if(!BleManager.getInstance().isBlueEnable()){
                Log.d("s","Not enable");
                BleManager.getInstance().enableBluetooth();
            }
        }


    }
    private static final long SCAN_PERIOD = 5000;
    private void scanLeDevice() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(10000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                Context context = getApplicationContext();
                CharSequence text = "Scan start";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Log.d("s","Finished");
                resultList = scanResultList;
                leDeviceListAdapter.addDeviceList(scanResultList);
                leDeviceListAdapter.notifyDataSetChanged();
            }
        });
    }
    private void connectLeDevice(int position){
        BleManager.getInstance().connect((BleDevice) leDeviceListAdapter.getItem(position), new BleGattCallback() {
            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {

            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

            }
        });
    }



}

