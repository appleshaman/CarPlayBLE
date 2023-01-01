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

import java.util.ArrayList;
import java.util.List;

public class ScanBleDeviceUtils {
    private static List<BleDevice> resultList;

    public static void scanLeDevice(Context context) {

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(10000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
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
                Log.d("s", "Finished");
                resultList = scanResultList;

                JavaBeanDevice javaBeanDevice = new JavaBeanDevice();
                javaBeanDevice.setBleDevices(scanResultList);
                Intent intent = new Intent();
                intent.putExtra("deviceList", javaBeanDevice);
                intent.setAction("deviceList");// for intent filter to fit different information
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }



    public static BleDevice getDevice(int position){
        return resultList.get(position);
    }
}
