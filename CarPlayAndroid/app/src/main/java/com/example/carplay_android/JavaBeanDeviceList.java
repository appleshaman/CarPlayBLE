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

public class JavaBeanDeviceList implements Serializable {
    private List<BleDevice> scanLeDevice() {
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

                leDeviceListAdapter.addDeviceList(scanResultList);
                leDeviceListAdapter.notifyDataSetChanged();

                Bundle bundle = new Bundle();
                bundle.putInt("totalDuration",duration);
                bundle.putInt("currentDuration",currentPosition);
                Intent intent = new Intent();
                intent.putExtra("musicDuration", bundle);
                intent.setAction("progress");// for intent filter to fit different information
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                localBroadcastManager.sendBroadcast(intent);
            }
        });
}
