package com.example.carplay_android;

import androidx.appcompat.app.AppCompatActivity;
<<<<<<< Updated upstream
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

=======
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
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

    private Button buttonScanNew;
    private Button buttonConnectOld;
    private BleDevice lastTimeUsedDevice;
    private TextView deviceName;
    private ImageView btStatus;
    private ImageView bleStatus;
    private ImageView notificationStatus;

    private IntentFilter intentFilterForDevice;
    private LocalBroadcastManager localBroadcastManagerForDevice;
    private ReceiverForDevice receiverForDevice;
    private BleDevice deviceUsedBefore;


>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< Updated upstream

        toggleNotificationListenerService();



=======
        initComponents();
        askPermissions();
        initBroadcastListeners();

        myServiceConn = new MyServiceConn();
        intent = new Intent(this, BleService.class);
        bindService(intent, myServiceConn, BIND_AUTO_CREATE);
        startService(intent);//bind the service

        buttonScanNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BleScanPage.class);
                startActivity(intent);
            }
        });

        buttonConnectOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deviceUsedBefore == null){
                    CharSequence text = "No previous device";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                }else{
                    controlBle.connectLeDevice(deviceUsedBefore);
                }
            }
        });
>>>>>>> Stashed changes
    }

    public void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

<<<<<<< Updated upstream
        pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

=======
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

    private void initComponents(){
        buttonScanNew = findViewById(R.id.buttonScanNewDevice);
        buttonConnectOld = findViewById(R.id.buttonConnectOld);
        deviceName = findViewById(R.id.textViewDeviceName);
        btStatus = findViewById(R.id.imageViewBT);
        bleStatus  = findViewById(R.id.imageViewBle);
        notificationStatus = findViewById(R.id.imageViewNotification);

    }
    private void initBroadcastListeners(){
        localBroadcastManagerForDevice = LocalBroadcastManager.getInstance(this);// register a receiver to receive the information about device last time used
        intentFilterForDevice = new IntentFilter("device");
        receiverForDevice = new ReceiverForDevice();
        localBroadcastManagerForDevice.registerReceiver(receiverForDevice, intentFilterForDevice);
    }
    private void askPermissions(){
        String[] permissions = {
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
        };
        requestPermissions(permissions, 200);
        BleManager.getInstance().init(getApplication());
        Bundle bundle = new Bundle();
        if(BleManager.getInstance().isSupportBle()){
            if(!BleManager.getInstance().isBlueEnable()){
                BleManager.getInstance().enableBluetooth();
                if(!BleManager.getInstance().isBlueEnable()) {//check if it finally enabled
                    btStatus.setColorFilter(Color.GREEN);
                }else {
                    btStatus.setColorFilter(0x595c5e);
                }
            }
        }else{
            btStatus.setColorFilter(0x595c5e);
        }

    }

    public class ReceiverForDevice extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            JavaBeanDevice javaBeanDevice = (JavaBeanDevice) intent.getSerializableExtra("device");
            lastTimeUsedDevice = javaBeanDevice.getBleDevice();
            if (deviceName != null){
                deviceName.setText(lastTimeUsedDevice.getName());
            }
        }
    }
}
>>>>>>> Stashed changes

}