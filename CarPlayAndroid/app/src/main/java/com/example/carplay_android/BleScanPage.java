package com.example.carplay_android;

import static com.example.carplay_android.ScanBleDeviceUtils.scanLeDevice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;

import java.util.List;

public class BleScanPage extends AppCompatActivity {

    private Button buttonScan;
    private Button buttonConnect;
    private ListView bleList;
    private TextView deviceName;
    private TextView deviceAddress;

    private List<BleDevice> resultList;
    private int selected = -1;
    private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter(this);

    private ReceiverForScanning receiverForScanning;
    private LocalBroadcastManager localBroadcastManagerForScanning;
    private IntentFilter intentFilterForScanning;

    private BleService.BleBinder controlBle;
    private BleService bleService;
    private MyServiceConn myServiceConn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan_page);
        init();

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

            }
        });


        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(getApplicationContext());
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlBle.connectLeDeviceInPosition(selected);
            }
        });
    }

    private void init(){
        initComponents();
        initBroadcastReceiver();
        initService();
    }

    private void initComponents(){
        buttonScan = findViewById(R.id.buttonScanNew);
        buttonConnect = findViewById(R.id.buttonConnectOld);
        bleList = findViewById(R.id.deviceList);
        deviceAddress = findViewById(R.id.deviceAddress);
        deviceName = findViewById(R.id.deviceName);
    }

    private void initBroadcastReceiver(){
        localBroadcastManagerForScanning = LocalBroadcastManager.getInstance(getApplicationContext());
        receiverForScanning = new ReceiverForScanning();
        intentFilterForScanning = new IntentFilter("DeviceList");
        localBroadcastManagerForScanning.registerReceiver(receiverForScanning, intentFilterForScanning);
    }

    private void initService(){
        myServiceConn = new MyServiceConn();
        intent = new Intent(this, BleService.class);
        bindService(intent, myServiceConn, BIND_AUTO_CREATE);
        startService(intent);//bind the service
    }

    class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder){
            controlBle = (BleService.BleBinder)iBinder;
            bleService = controlBle.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name){
        }
    }

    private class ReceiverForScanning extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            JavaBeanDevice javaBeanDevice = (JavaBeanDevice) intent.getSerializableExtra("DeviceList");
            resultList = javaBeanDevice.getBleDeviceList();
            leDeviceListAdapter.addDeviceList(resultList);
            leDeviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(myServiceConn);
    }
}

