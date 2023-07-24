package com.example.carplay_android;

import static com.example.carplay_android.javabeans.JavaBeanFilters.*;

import androidx.annotation.RequiresApi;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.data.BleDevice;
import com.example.carplay_android.services.BleService;
import com.example.carplay_android.services.NotificationService;


public class MainActivity extends AppCompatActivity {

    private BleService.BleBinder controlBle;
    private ServiceConnToBLE serviceConnToBLE;

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
            public void onClick(View view) {//open the settings for turn on notification
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });

        buttonConnectToOld.setOnClickListener(new View.OnClickListener() {//connect to previous device
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

        buttonScanNewDevice.setOnClickListener(new View.OnClickListener() {//scan new device
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
//        isNotificationServiceRunning();
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

//        SharedPreferences sharedPreferences = getSharedPreferences(getFILTER_DEVICE_USED(), 0);
//        String json = sharedPreferences.getString(getFILTER_DEVICE_USED(), null);
//        if(json != null){
//            Gson gson = new Gson();
//            Type type = new TypeToken<JavaBeanDevice>(){}.getType();
//            JavaBeanDevice javaBeanDevice = gson.fromJson(json, type);
//            deviceUsed = javaBeanDevice.getBleDevice();
//            deviceName.setText(deviceUsed.getName());
//        }
    }

    private void askPermission(){
        String[] permissions = {
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
        };
        //requestIgnoreBatteryOptimizations();
        requestPermissions(permissions, 200);
    }

    private void initBroadcastReceiver(){
        IntentFilter intentFilter;
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        intentFilter = new IntentFilter(getFILTER_DEVICE_USED());
        ReceiverForDeviceUsed receiverForDeviceUsed = new ReceiverForDeviceUsed();
        localBroadcastManager.registerReceiver(receiverForDeviceUsed, intentFilter);

        intentFilter = new IntentFilter(getFILTER_BT_STATUS());
        ReceiverForBTStatus receiverForBTStatus = new ReceiverForBTStatus();
        localBroadcastManager.registerReceiver(receiverForBTStatus, intentFilter);

        intentFilter = new IntentFilter(getFILTER_BLE_STATUS());
        ReceiverForBleStatus receiverForBleStatus = new ReceiverForBleStatus();
        localBroadcastManager.registerReceiver(receiverForBleStatus, intentFilter);

        intentFilter = new IntentFilter(getFILTER_NOTIFICATION_STATUS());
        ReceiverForNotificationStatus receiverForNotificationStatus = new ReceiverForNotificationStatus();
        localBroadcastManager.registerReceiver(receiverForNotificationStatus, intentFilter);

        intentFilter = new IntentFilter(getFILTER_DEVICE_STATUS());
        ReceiverForDeviceStatus receiverForDeviceStatus = new ReceiverForDeviceStatus();
        localBroadcastManager.registerReceiver(receiverForDeviceStatus, intentFilter);
    }

    private void initService(){
        serviceConnToBLE = new ServiceConnToBLE();
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, serviceConnToBLE, BIND_AUTO_CREATE);
        startService(intent);//bind the service
        requestIgnoreBatteryOptimizations();
    }

    class ServiceConnToBLE implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder){
            controlBle = (BleService.BleBinder)iBinder;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){
            initService();
        }
    }


//    private void ensureNotificationServiceRunning() {
//        String enabledListeners = Settings.Secure.getString(
//                getContentResolver(),
//                "enabled_notification_listeners");
//
//        if (enabledListeners == null || !enabledListeners.contains(getPackageName())) {
//            Intent intent = new Intent(this, NotificationService.class);
//            startService(intent);
//            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
//        }
//    }






    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        boolean isIgnored = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnored = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        if(!isIgnored){
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



    class ReceiverForDeviceUsed extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
//            JavaBeanDevice javaBeanDevice = (JavaBeanDevice) intent.getSerializableExtra(getFILTER_DEVICE_USED());
//            deviceUsed = javaBeanDevice.getBleDevice();
//            deviceName.setText(deviceUsed.getName());
//            SharedPreferences sharedPreferences = getSharedPreferences(getFILTER_DEVICE_USED(), 0);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            Gson gson = new Gson();
//            String json = gson.toJson(javaBeanDevice);
//            editor.putString(getFILTER_DEVICE_USED(), json);
//            editor.apply();

        }
    }

    class ReceiverForBTStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra(getFILTER_BT_STATUS(),false)){
                imageViewBTStatus.setColorFilter(Color.GREEN);
            }else{
                imageViewBTStatus.setColorFilter(0x9c9c9c);
            }
        }
    }

    class ReceiverForBleStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra(getFILTER_BLE_STATUS(),false)){
                imageViewBleStatus.setColorFilter(Color.GREEN);
            }else{
                imageViewBleStatus.setColorFilter(0x9c9c9c);
            }
        }
    }

    class ReceiverForNotificationStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra(getFILTER_NOTIFICATION_STATUS(),false)){
                imageViewNotificationStatus.setColorFilter(Color.GREEN);
            }else{
                imageViewNotificationStatus.setColorFilter(0x9c9c9c);
            }

        }
    }
    class ReceiverForDeviceStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra(getFILTER_DEVICE_STATUS(),false)){
                imageViewDeviceStatus.setColorFilter(Color.GREEN);
            }else{
                imageViewDeviceStatus.setColorFilter(0x9c9c9c);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnToBLE);
    }
}

