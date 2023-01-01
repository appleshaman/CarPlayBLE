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

    private BleService.BleBinder controlBle;
    private BleService bleService;
    private MyServiceConn myServiceConn;
    private Intent intent;

    private List<BleDevice> resultList;
    private int selected = -1;
    private final LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter(this);

    private ReceiverForDeviceList receiverForDeviceList;
    private LocalBroadcastManager localBroadcastManagerForDeviceList;
    private IntentFilter intentFilterForDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan_page);
        initialComponent();
        initBroadcastListenersForDeviceList();

        intent = new Intent(this, BleService.class);
        myServiceConn = new MyServiceConn();
        bindService(intent, myServiceConn, BIND_AUTO_CREATE);
        intent = new Intent(this, BleService.class);
        bindService(intent, myServiceConn, BIND_AUTO_CREATE);
        startService(intent);//bind the service

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
                }else{
                    controlBle.connectLeDeviceInPosition(selected);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(myServiceConn);
    }

    private void initialComponent(){
        buttonScan = findViewById(R.id.buttonScanNewDevice);
        buttonConnect = findViewById(R.id.buttonConnectOld);
        bleList = findViewById(R.id.deviceList);
        deviceAddress = findViewById(R.id.deviceAddress);
        deviceName = findViewById(R.id.deviceName);
    }

    private void initBroadcastListenersForDeviceList(){
        localBroadcastManagerForDeviceList = LocalBroadcastManager.getInstance(this);// register a receiver to receive the information about devices that scanned
        intentFilterForDeviceList = new IntentFilter("deviceList");
        receiverForDeviceList = new ReceiverForDeviceList();
        localBroadcastManagerForDeviceList.registerReceiver(receiverForDeviceList, intentFilterForDeviceList);
    }

    public class ReceiverForDeviceList extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            JavaBeanDevice javaBeanDevice = (JavaBeanDevice) intent.getSerializableExtra("deviceList");
            resultList = javaBeanDevice.getBleDevices();
            leDeviceListAdapter.addDeviceList(resultList);
            leDeviceListAdapter.notifyDataSetChanged();
        }
    }
}

