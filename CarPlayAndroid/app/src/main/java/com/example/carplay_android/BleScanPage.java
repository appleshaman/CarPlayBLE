package com.example.carplay_android;

import static com.example.carplay_android.utils.ScanBleDeviceUtils.scanLeDevice;
import static com.example.carplay_android.javabeans.JavaBeanFilters.*;

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
import android.widget.Toast;

import com.clj.fastble.data.BleDevice;
import com.example.carplay_android.services.BleService;
import com.example.carplay_android.utils.BroadcastUtils;
import com.example.carplay_android.utils.ScanBleDeviceUtils;

public class BleScanPage extends AppCompatActivity {

    private Button buttonScan;
    private Button buttonConnect;
    private ListView bleList;
    private TextView deviceName;
    private TextView deviceAddress;


    private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter(this);

    private BleService.BleBinder controlBle;
    private ServiceConnBle serviceConnBle;

    private BleDevice deviceSelected;

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
                    deviceSelected = ScanBleDeviceUtils.getResultList().get(i);
                }else{
                    connectDevice();
                }
            }
        });

        scanLeDevice(getApplicationContext());


        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(getApplicationContext());
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectDevice();
            }
        });
    }

    private void init(){
        initComponents();
        initBroadcastReceiver();
        initService();
    }

    private void initComponents(){
        buttonScan = findViewById(R.id.buttonNotification);
        buttonConnect = findViewById(R.id.buttonConnectOld);
        bleList = findViewById(R.id.deviceList);
        deviceAddress = findViewById(R.id.deviceAddress);
        deviceName = findViewById(R.id.deviceName);
    }

    private void initBroadcastReceiver(){
        LocalBroadcastManager localBroadcastManagerForScanning = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForScanning receiverForScanning = new ReceiverForScanning();
        IntentFilter intentFilterForScanning = new IntentFilter(getFilterDeviceList());
        localBroadcastManagerForScanning.registerReceiver(receiverForScanning, intentFilterForScanning);
    }

    private void initService(){
        serviceConnBle = new ServiceConnBle();
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, serviceConnBle, BIND_AUTO_CREATE);
        startService(intent);//bind the service
    }

    private void connectDevice(){//try to connect a device
        if(deviceSelected == null){
            CharSequence text = "No device selected";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }else{

            controlBle.connectLeDevice(deviceSelected);
            controlBle.setMtu(deviceSelected);
            //BroadcastUtils.sendBleDevice(deviceSelected,getFILTER_DEVICE_USED(), getApplicationContext());//send this device to main page
        }
    }

    private class ServiceConnBle implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder){
            controlBle = (BleService.BleBinder)iBinder;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){
        }
    }

    private class ReceiverForScanning extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            leDeviceListAdapter.addDeviceList(ScanBleDeviceUtils.getResultList());
            leDeviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnBle);
    }
}

