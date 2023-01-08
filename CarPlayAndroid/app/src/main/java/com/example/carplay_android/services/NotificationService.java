package com.example.carplay_android.services;


import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;


import com.example.carplay_android.utils.BroadcastUtils;
import com.example.carplay_android.utils.DirectionUtils;

public class NotificationService extends NotificationListenerService {
    public NotificationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        CharSequence text = "onCreate";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        BroadcastUtils.sendStatus(true, "NotificationStatus", getApplicationContext());
        DirectionUtils.loadSamplesFromAsserts(getApplicationContext());
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
        informationMessage = informationMessage + strings[0] + "$" + strings[1] + "$";// ETA in Minutes + Distance

        informationMessage = informationMessage;

        BitmapDrawable bitmapDrawable = (BitmapDrawable) sbn.getNotification().getLargeIcon().loadDrawable(getApplicationContext());

        String direction = DirectionUtils.getDirectionByComparing(bitmapDrawable.getBitmap());
        direction = direction;

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadcastUtils.sendStatus(false, "NotificationStatus", getApplicationContext());
    }
}