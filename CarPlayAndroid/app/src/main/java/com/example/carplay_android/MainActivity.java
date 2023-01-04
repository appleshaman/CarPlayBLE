package com.example.carplay_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.data.BleDevice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class MainActivity extends AppCompatActivity {

    private BleService.BleBinder controlBle;
    private BleService bleService;
    private MyServiceConn myServiceConn;
    private Intent intent;

    private Button buttonOpenNotification;
    private Button buttonScanNewDevice;
    private Button buttonConnectToOld;
    private ImageView imageViewBTStatus;
    private ImageView imageViewBleStatus;
    private ImageView imageViewNotificationStatus;
    private ImageView imageViewDeviceStatus;
    private TextView deviceName;

    private BleDevice deviceUsed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        buttonOpenNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });

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
        buttonOpenNotification  = findViewById(R.id.buttonNotification);
        buttonConnectToOld = findViewById(R.id.buttonConnectOld);
        buttonScanNewDevice = findViewById(R.id.buttonScanNew);
        imageViewBTStatus = findViewById(R.id.imageViewBT);
        imageViewBleStatus = findViewById(R.id.imageViewBleStatus);
        imageViewNotificationStatus = findViewById(R.id.imageViewNotification);
        imageViewDeviceStatus = findViewById(R.id.imageViewDevice);
        deviceName = findViewById(R.id.textViewDeviceName);

        SharedPreferences sharedPreferences = getSharedPreferences("DeviceUsed", 0);
        String json = sharedPreferences.getString("DeviceUsed", null);
        if(json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<JavaBeanDevice>(){}.getType();
            JavaBeanDevice javaBeanDevice = gson.fromJson(json, type);
            deviceUsed = javaBeanDevice.getBleDevice();
            deviceName.setText(deviceUsed.getName());
        }


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

        intentFilter = new IntentFilter("DeviceUsed");
        LocalBroadcastManager localBroadcastManagerForDeviceUsed = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForDeviceUsed receiverForDeviceUsed = new ReceiverForDeviceUsed();
        localBroadcastManagerForDeviceUsed.registerReceiver(receiverForDeviceUsed, intentFilter);

        intentFilter = new IntentFilter("BTStatus");
        LocalBroadcastManager localBroadcastManagerForBTStatus = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForBTStatus receiverForBTStatus = new ReceiverForBTStatus();
        localBroadcastManagerForBTStatus.registerReceiver(receiverForBTStatus, intentFilter);

        intentFilter = new IntentFilter("BleStatus");
        LocalBroadcastManager localBroadcastManagerForBleStatus = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForBleStatus receiverForBleStatus = new ReceiverForBleStatus();
        localBroadcastManagerForBleStatus.registerReceiver(receiverForBleStatus, intentFilter);

        intentFilter = new IntentFilter("NotificationStatus");
        LocalBroadcastManager localBroadcastManagerForNotificationStatus = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForNotificationStatus receiverForNotificationStatus = new ReceiverForNotificationStatus();
        localBroadcastManagerForNotificationStatus.registerReceiver(receiverForNotificationStatus, intentFilter);

        intentFilter = new IntentFilter("DeviceStatus");
        LocalBroadcastManager localBroadcastManagerForDeviceStatus = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForDeviceUsed receiverForDeviceStatus = new ReceiverForDeviceUsed();
        localBroadcastManagerForDeviceStatus.registerReceiver(receiverForDeviceStatus, intentFilter);

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



    class ReceiverForDeviceUsed extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            JavaBeanDevice javaBeanDevice = (JavaBeanDevice) intent.getSerializableExtra("DeviceUsed");
            deviceUsed = javaBeanDevice.getBleDevice();
            deviceName.setText(deviceUsed.getName());
            SharedPreferences sharedPreferences = getSharedPreferences("DeviceUsed", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(javaBeanDevice);
            editor.putString("DeviceUsed", json);
            editor.apply();

        }
    }

    class ReceiverForBTStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("BTStatus",false)){
                imageViewBTStatus.setColorFilter(Color.GREEN);
            }else{
                imageViewBTStatus.setColorFilter(0x9c9c9c);
            }
        }
    }

    class ReceiverForBleStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("BleStatus",false)){
                imageViewBleStatus.setColorFilter(Color.GREEN);
            }else{
                imageViewBleStatus.setColorFilter(0x9c9c9c);
            }
        }
    }

    class ReceiverForNotificationStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("NotificationStatus",false)){
                imageViewNotificationStatus.setColorFilter(Color.GREEN);
            }else{
                imageViewNotificationStatus.setColorFilter(0x9c9c9c);
            }

        }
    }
    class ReceiverForDeviceStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("DeviceStatus",false)){
                imageViewDeviceStatus.setColorFilter(Color.GREEN);
            }else{
                imageViewDeviceStatus.setColorFilter(0x9c9c9c);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(myServiceConn);
    }
}

