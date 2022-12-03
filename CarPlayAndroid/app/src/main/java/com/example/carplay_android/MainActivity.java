package com.example.carplay_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ListView songList;
    private ReceiverForDevice receiver;
    private BleService bleService;
    private BleService.BleBinder bleBinder;
    private Button buttonScan;
    private Button buttonConnect;
    private ListView listViewDevice;
    private Intent intent;

    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bleBinder = (BleService.BleBinder) iBinder;
            bleService = bleBinder.getService();
            ActivityCompat.requestPermissions(MainActivity.this ,new String[]{Manifest.permission.BLUETOOTH_SCAN},1);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = {
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "Manifest.permission.BLUETOOTH_SCAN"
        };
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                //判断是否需要 向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                     Toast.makeText(this,"GOGOGO", Toast.LENGTH_LONG).show();

                    ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                return;
            }else{

            }
        }
        requestPermissions(permissions, 200);
        receiver = new ReceiverForDevice();
        toggleNotificationListenerService();
        buttonScan = findViewById(R.id.buttonScan);
        buttonConnect = findViewById(R.id.buttonConnect);
        listViewDevice = findViewById(R.id.deviceList);

        LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();
        listViewDevice.setAdapter(leDeviceListAdapter);

        intent = new Intent(this, BleService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bleService.scanLeDevice();
            }
        });
    }

    public void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public class ReceiverForDevice extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            JavaBeanDevice javaBeanDevice = (JavaBeanDevice) intent.getSerializableExtra("device");
            deviceList.add(javaBeanDevice.device);
        }
    }


}

