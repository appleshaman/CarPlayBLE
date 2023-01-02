package com.example.carplay_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;


public class MainActivity extends AppCompatActivity {

    private BleService.BleBinder controlBle;
    private BleService bleService;
    private MyServiceConn myServiceConn;
    private Intent intent;

    private Button buttonScanNewDevice;
    private Button buttonConnectToOld;
    private ImageView imageViewBTStatus;
    private ImageView imageViewBleStatus;
    private TextView deviceName;

    private BleDevice deviceUsed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        buttonConnectToOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deviceUsed == null){
                    CharSequence text = "No previous device";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                }else{
                    controlBle.connectLeDevice(deviceUsed);
                }
            }
        });

        buttonScanNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BleScanPage.class);
                startActivity(intent);
            }
        });









    }

    private void init(){
        askPermission();
        initComponents();
        initBroadcastReceiver();
        initService();
    }

    private void initComponents(){
        buttonConnectToOld = findViewById(R.id.buttonConnectOld);
        buttonScanNewDevice = findViewById(R.id.buttonScanNew);
        imageViewBleStatus = findViewById(R.id.imageViewBleStatus);
        imageViewBTStatus = findViewById(R.id.imageViewBT);
        deviceName = findViewById(R.id.textViewDeviceName);
    }

    private void askPermission(){
        String[] permissions = {
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
        };
        requestPermissions(permissions, 200);
    }

    private void initBroadcastReceiver(){
        IntentFilter intentFilter;

        intentFilter = new IntentFilter("BT");
        LocalBroadcastManager localBroadcastManagerForBTStatus = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForBTStatus receiverForBTStatus = new ReceiverForBTStatus();
        localBroadcastManagerForBTStatus.registerReceiver(receiverForBTStatus, intentFilter);

        intentFilter = new IntentFilter("DeviceUsed");
        LocalBroadcastManager localBroadcastManagerForDeviceUsed = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForDeviceUsed receiverForDeviceUsed= new ReceiverForDeviceUsed();
        localBroadcastManagerForDeviceUsed.registerReceiver(receiverForDeviceUsed, intentFilter);

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

    class ReceiverForBTStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("BT",false)){
                imageViewBTStatus.setColorFilter(Color.GREEN);
            }else{
                imageViewBTStatus.setColorFilter(0x9c9c9c);
            }
        }
    }

    class ReceiverForDeviceUsed extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            JavaBeanDevice javaBeanDevice = (JavaBeanDevice) intent.getSerializableExtra("DeviceUsed");
            deviceUsed = javaBeanDevice.getBleDevice();
            deviceName.setText(deviceUsed.getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(myServiceConn);
    }
}

