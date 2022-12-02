package com.example.carplay_android;


import android.app.Notification;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends NotificationListenerService {


    public NotificationService() {
        Log.d("1","Created");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("1","onCreate");
        Context context = getApplicationContext();
        CharSequence text = "onCreate";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.d("!","Posted");
        if (sbn != null && isGMapNotification(sbn)){
            handleGMapNotification(sbn);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d("!","removed");
    }



    private boolean isGMapNotification(StatusBarNotification sbn){
        if(!sbn.isOngoing() || !sbn.getPackageName().contains("com.google.android.apps.maps")){
            return false;
        }

        return (sbn.getId() == 1);
    }


    private void handleGMapNotification (StatusBarNotification sbn){
        Bundle bundle = sbn.getNotification().extras;
        Bundle broadcastBundle = new Bundle();

        String string = bundle.getString(Notification.EXTRA_TEXT);
        String[] strings = string.split("-");
        bundle.putString("destination",strings[0]);
        strings = strings[1].split(" ");
        if(strings.length == 3){
            strings[0] = strings[0].concat(" ");//concat a " "
            strings[0] = strings[0].concat(strings[1]);//if use 12 hour type, then concat the time and AM/PM
        }
        bundle.putString("ETA",strings[1]);

        string = bundle.getString(Notification.EXTRA_TITLE);
        strings = string.split("-");
        if(strings.length  == 2){
            bundle.putString("Direction",strings[1]);
        }
        else if(strings.length  == 1){
            bundle.putString("Direction",strings[0]);
        }

        string = bundle.getString(Notification. EXTRA_SUB_TEXT);
        strings = string.split("·");
        bundle.putString("Minutes",strings[0]);
        bundle.putString("Distance",strings[1]);


        Icon largeIcon =  sbn.getNotification().getLargeIcon();
        largeIcon = largeIcon;
        sbn.getNotification().getSmallIcon();

    }




}