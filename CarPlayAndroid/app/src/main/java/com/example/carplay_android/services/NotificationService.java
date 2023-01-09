package com.example.carplay_android.services;


import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.carplay_android.BleScanPage;
import com.example.carplay_android.utils.BroadcastUtils;
import com.example.carplay_android.utils.DirectionUtils;
import com.example.carplay_android.utils.ScanBleDeviceUtils;

public class NotificationService extends NotificationListenerService {

    private BleService.BleBinder controlBle;
    private BleService bleService;
    private MyServiceConn myServiceConn;
    private Intent intent;
    private Boolean deviceStatus = false;

    private LocalBroadcastManager localBroadcastManager;
    private ReceiverForDeviceStatus receiverForDeviceStatus;
    private IntentFilter intentFilterForDeviceStatus;

    public NotificationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        BroadcastUtils.sendStatus(true, "NotificationStatus", getApplicationContext());
        DirectionUtils.loadSamplesFromAsserts(getApplicationContext());
    }



    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (sbn != null && isGMapNotification(sbn)){
            handleGMapNotification(sbn);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d("Notification","removed");
    }



    private boolean isGMapNotification(StatusBarNotification sbn){
        if(!sbn.isOngoing() || !sbn.getPackageName().contains("com.google.android.apps.maps")){
            return false;
        }
        return (sbn.getId() == 1);
    }


    private void handleGMapNotification (StatusBarNotification sbn){
        Bundle bundle = sbn.getNotification().extras;
        String informationMessage;
        String string = bundle.getString(Notification.EXTRA_TEXT);
        String[] strings = string.split("-");//destination
        informationMessage = strings[0].trim() + "$";
        strings = strings[1].trim().split(" ");
        if(strings.length == 3){
            strings[0] = strings[0] + " ";//concat a " "
            strings[0] = strings[0] + strings[1];//if use 12 hour type, then concat the time and AM/PM
        }
        informationMessage = informationMessage + strings[0] + "$";// get the ETA

        string = bundle.getString(Notification.EXTRA_TITLE);
        strings = string.split("-");
        if(strings.length  == 2){
            informationMessage = informationMessage + strings[0].trim() + "$"  + strings[1].trim() + "$";//time to next direction + Direction to somewhere
        }
        else if(strings.length  == 1){
            informationMessage = informationMessage + strings[0] + "$";//Direction to somewhere
            bundle.putString("Direction",strings[0]);
        }

        string = bundle.getString(Notification. EXTRA_SUB_TEXT);
        strings = string.split("·");
        informationMessage = informationMessage + strings[0].trim() + "$" + strings[1].trim() + "$";// ETA in Minutes + Distance

        BitmapDrawable bitmapDrawable = (BitmapDrawable) sbn.getNotification().getLargeIcon().loadDrawable(getApplicationContext());

        informationMessage = informationMessage + DirectionUtils.getDirectionByComparing(bitmapDrawable.getBitmap());
        if(deviceStatus){
            BroadcastUtils.sendString(informationMessage, "informationMessage", getApplicationContext());
        }
    }

    private void init(){
        initService();
        initBroadcastReceiver();
    }

    private void initService(){
        myServiceConn = new MyServiceConn();
        intent = new Intent(this, BleService.class);
        bindService(intent, myServiceConn, BIND_AUTO_CREATE);
        startService(intent);//bind the service
    }

    private void initBroadcastReceiver(){
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        receiverForDeviceStatus = new ReceiverForDeviceStatus();
        intentFilterForDeviceStatus = new IntentFilter("DeviceList");
        localBroadcastManager.registerReceiver(receiverForDeviceStatus, intentFilterForDeviceStatus);
    }

    private class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder){
            controlBle = (BleService.BleBinder)iBinder;
            bleService = controlBle.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name){
        }
    }

    private class ReceiverForDeviceStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            deviceStatus = intent.getBooleanExtra("DeviceStatus", false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadcastUtils.sendStatus(false, "NotificationStatus", getApplicationContext());
    }
}