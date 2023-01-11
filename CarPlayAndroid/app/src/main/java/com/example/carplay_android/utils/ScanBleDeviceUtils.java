package com.example.carplay_android.utils;

import android.content.Context;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;

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
                resultList = scanResultList;
                BroadcastUtils.sendBleDevices(resultList, "DeviceList", context);
            }
        });
    }

    public static List<BleDevice> getResultList() {
        return resultList;
    }
}
