package com.example.carplay_android.services;

import static com.example.carplay_android.javabeans.JavaBeanFilters.*;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.clj.fastble.BleManager;
import com.example.carplay_android.utils.BroadcastUtils;
import com.example.carplay_android.utils.DirectionUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class NotificationService extends NotificationListenerService {

    private BleService.BleBinder controlBle;
    private ServiceConnToBle serviceConnToBle;
    private Boolean deviceStatus = false;
    private Timer timerSendNotification;
    private Boolean ifSendNotification = false;
    private String[] informationMessageSentLastTime = new String[6];

    public NotificationService() {

    }



    @Override
    public void onCreate() {
        super.onCreate();
        init();

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
        String[] informationMessage = new String[7];
        String string = bundle.getString(Notification.EXTRA_TEXT);
        String[] strings = string.split("-");//destination
        informationMessage[0] = strings[0].trim();
        strings = strings[1].trim().split(" ");
        if(strings.length == 3){
            strings[0] = strings[0] + " ";//concat a " "
            strings[0] = strings[0] + strings[1];//if use 12 hour type, then concat the time and AM/PM
        }
        informationMessage[1] = strings[0];// get the ETA

        string = bundle.getString(Notification.EXTRA_TITLE);
        strings = string.split("-");
        if(strings.length  == 2){
            informationMessage[2] = strings[0].trim();//Direction to somewhere
            informationMessage[3] = strings[1].trim();//Time to next direction

        }
        else if(strings.length  == 1){
            informationMessage[2] = strings[0].trim();//Direction to somewhere
            informationMessage[3] = "N/A";//Time to next direction
            bundle.putString("Direction",strings[0]);
        }

        string = bundle.getString(Notification. EXTRA_SUB_TEXT);
        strings = string.split("·");
        informationMessage[4] = strings[0].trim();//ETA in Minutes
        informationMessage[5] = strings[1].trim();//Distance
        BitmapDrawable bitmapDrawable = (BitmapDrawable) sbn.getNotification().getLargeIcon().loadDrawable(getApplicationContext());

        informationMessage[6] = DirectionUtils.getDirectionByComparing(bitmapDrawable.getBitmap());

        if(deviceStatus){
            if((!Arrays.equals(informationMessageSentLastTime, informationMessage))||
                    ((Arrays.equals(informationMessageSentLastTime, informationMessage))&&(ifSendNotification))){
                controlBle.sendDestination(informationMessage[0]);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controlBle.sendEta(informationMessage[1]);
                    }
                },100);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controlBle.sendDirection(informationMessage[2]);
                    }
                },100);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controlBle.sendDirectionDistances(informationMessage[3]);
                    }
                },100);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controlBle.sendEtaInMinutes(informationMessage[4]);
                    }
                },100);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controlBle.sendDistance(informationMessage[5]);
                    }
                },100);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controlBle.sendDirectionPrecise(informationMessage[6]);
                    }
                },100);
                Log.d("d","done");
                informationMessageSentLastTime = informationMessage;
                ifSendNotification = false;//reduce the frequency of sending messages
                //why not just check if two messages are the same,  why still need to send same message every half second:
                //because if the device lost connection before, we have to keep send message to it to keep it does not
                //receive any wrong message.
            }
        }
    }

    private void init(){
        initService();
        initBroadcastReceiver();
        setSendNotificationTimer();
        BroadcastUtils.sendStatus(true, getFILTER_NOTIFICATION_STATUS(), getApplicationContext());
        DirectionUtils.loadSamplesFromAsserts(getApplicationContext());
    }

    private void initService(){
        serviceConnToBle = new ServiceConnToBle();
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, serviceConnToBle, BIND_AUTO_CREATE);
        startService(intent);//bind the service
    }

    private void initBroadcastReceiver(){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForDeviceStatus receiverForDeviceStatus = new ReceiverForDeviceStatus();
        IntentFilter intentFilterForDeviceStatus = new IntentFilter(getFILTER_DEVICE_STATUS());
        localBroadcastManager.registerReceiver(receiverForDeviceStatus, intentFilterForDeviceStatus);
    }

    private class ServiceConnToBle implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder){
            controlBle = (BleService.BleBinder)iBinder;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){
        }
    }

    private class ReceiverForDeviceStatus extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            deviceStatus = intent.getBooleanExtra(getFILTER_DEVICE_STATUS(), false);
        }
    }

    public void setSendNotificationTimer(){
        if(timerSendNotification == null){
            timerSendNotification = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    ifSendNotification = true;
                }
            };
            timerSendNotification.schedule(timerTask, 10, 2000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadcastUtils.sendStatus(false, getFILTER_NOTIFICATION_STATUS(), getApplicationContext());
        unbindService(serviceConnToBle);
    }
}